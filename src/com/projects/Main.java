package com.projects;

import com.projects.helper.*;
import com.projects.model.*;
import com.projects.simulation.World;
import com.projects.view.*;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Main is responsible for loading and saving files and initializing the application and simulation.
 */
public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<Structure> templateStructureData = FXCollections.observableArrayList();
    private ObservableList<Structure> worldStructureData = FXCollections.observableArrayList();
    private ObjectProperty<Structure> selectedTemplateStructure = null;
    private ObjectProperty<Structure> selectedWorldStructure = null;
    private ObjectProperty<Structure> selectedStructure = null;
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>(LocalDate.now());
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>(startDate.getValue().plusDays(1));
    private ObjectProperty<LocalTime> currentTime = new SimpleObjectProperty<>(LocalTime.now());
    private ObjectProperty<LocalDate> currentDate = new SimpleObjectProperty<>(LocalDate.now());
    private ObjectProperty<WorldTimer.UpdateRate> updateRate = new SimpleObjectProperty<>(WorldTimer.UpdateRate.DAYS);
    private ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.FINISHED);
    private IntegerProperty dailyStatsDay = new SimpleIntegerProperty(0);

    private BuildingDetailsPaneController buildingDetailsPaneController;
    private SimulationControlsController simulationControlsController;
    private DailyStatisticsController dailyStatisticsController;
    private ProductionStatisticsController productionStatisticsController;
    private SimulationOverviewController simulationOverviewController;
    private WorldViewController worldViewController;

    private HashMap<String, String> storageStrategyScripts;
    private HashMap<Integer, AnimatedSprite> buildingSprites;
    private HashMap<Integer, AnimatedSprite> powerPlantSprites;
    private World world;

    /**
     * Main constructor.
     */
    public Main() {
        // needed to create the temp object
        List<Image> images = new ArrayList<>();
        try {

            BufferedImage bufferedImage = ImageIO.read(new File(Utils.getWorkingDir() + "/images/Selection.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            images.add(image);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // a temp structure is needed
        Structure structure = new Structure("Temp", -1, -1, -1, new AnimatedSprite(-1, images, -1, -1, -1));
        selectedTemplateStructure = new SimpleObjectProperty<>(structure);
        selectedWorldStructure = new SimpleObjectProperty<>(structure);
        selectedStructure = new SimpleObjectProperty<>(structure);

        storageStrategyScripts = new HashMap<>();
        buildingSprites = new HashMap<>();
        powerPlantSprites = new HashMap<>();
        world = new World();
    }

    /**
     * Entry point for the application.
     * @param args arguments for running the app
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called when the application is launched from the main method.
     * @param primaryStage main container, the window for the application
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(Constants.APP_NAME);

        world.setMain(this);
        readSpriteData();
        initRootLayout();
        world.resetSimulation();
    }

    /**
     * Resets the application, preparing it for a new simulation.
     */
    public void reset() {
        worldStructureData.clear();
        templateStructureData.clear();
        dailyStatisticsController.clearDemandChart();
        dailyStatisticsController.clearEmissionChart();
        dailyStatisticsController.clearPriceChart();
        productionStatisticsController.clearEmissionForDemandChart();
        productionStatisticsController.clearPriceForDemandChart();
        buildingDetailsPaneController.clearLoadProfileDetails();
        buildingDetailsPaneController.clearComparisons();
        worldViewController.clearSelection();
        world.resetSimulation();
        setSimulationFilePath(null);
    }

    /**
     * Initializes the root layout, placing the simulation overview inside it.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMain(this);

            rootLayout.setCenter(initSimulationOverview());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load last opened simulation
        File file = getSimulationFilePath();
        if (file != null) {
            readSimulation(file);
        }
    }

    /**
     * Initializes the building details pane.
     * @return the pane after it's been initialized
     */
    private Pane initBuildingDetailsPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/BuildingDetailsPane.fxml"));
            Pane buildingDetails = loader.load();

            buildingDetailsPaneController = loader.getController();
            buildingDetailsPaneController.setMain(this);
            return buildingDetails;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Initializes the simulation controls pane.
     * @return the pane after it's been initialized
     */
    private Pane initSimulationControlsPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/SimulationControls.fxml"));
            Pane simulationControls = loader.load();

            simulationControlsController = loader.getController();
            simulationControlsController.setMain(this);
            return simulationControls;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Initializes the daily statistics pane.
     * @return the pane after it's been initialized
     */
    private Pane initDailyStatisticsPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/DailyStatistics.fxml"));
            Pane dailyStatistics = loader.load();

            dailyStatisticsController = loader.getController();
            dailyStatisticsController.setMain(this);
            return dailyStatistics;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Initializes the production statistics pane.
     * @return the pane after it's been initialized
     */
    private Pane initProductionStatisticsPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ProductionStatistics.fxml"));
            Pane productionStatistics = loader.load();

            productionStatisticsController = loader.getController();
            productionStatisticsController.setMain(this);
            return productionStatistics;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Initializes the world view pane.
     * @return the pane after it's been initialized
     */
    private Pane initWorldViewPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/WorldView.fxml"));
            Pane worldViewPane = loader.load();

            worldViewController = loader.getController();
            worldViewController.setMain(this);
            return worldViewPane;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Initializes the simulation overview pane, passing all the pains that will be held inside of it.
     * @return the pane after it's been initialized
     */
    public Pane initSimulationOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/SimulationOverview.fxml"));
            Pane personOverview = loader.load();

            simulationOverviewController = loader.getController();
            simulationOverviewController.setMain(this);
            simulationOverviewController.showSimulationControlsPane(initSimulationControlsPane());
            simulationOverviewController.showStructureDetailsPane(initBuildingDetailsPane());
            simulationOverviewController.showDailyStatisticsPane(initDailyStatisticsPane());
            simulationOverviewController.showProductionStatisticsPane(initProductionStatisticsPane());
            simulationOverviewController.showWorldViewPane(initWorldViewPane());
            return personOverview;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Opens a dialog to edit properties for the specified building.
     * @param structure the building to be edited
     */
    public void showBuildingEditDialog(Building structure) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/StructureEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Building");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            StructureEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStructure(structure);

            readStorageStrategies();
            controller.setStorageStrategies(storageStrategyScripts);
            controller.setStructureSprites(buildingSprites);
            controller.setMain(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            buildingDetailsPaneController.setStructureData(structure, structure.getLoadProfilesForWeek());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to edit the load profile for the specified structure.
     * @param structure the building whose load profile will be edited
     */
    public void showLoadProfileEditDialog(Building structure) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/LoadProfileEditDialog.fxml"));
            BorderPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Load Profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            LoadProfileEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setBuilding(structure);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to edit details for the specified power plant.
     *
     * @param powerPlant the power plant object to be edited
     */
    public void showPowerPlantEditDialog(PowerPlant powerPlant) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PowerPlantEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Power Plant");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PowerPlantEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPowerPlant(powerPlant);
            controller.setSprites(powerPlantSprites);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the person file preference (the file that was last opened).
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return the file that was last opened or null
     */
    public File getSimulationFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setSimulationFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle(Constants.APP_NAME + " - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle(Constants.APP_NAME);
        }
    }

    /**
     * Toggles the legend to be displayed in the world view.
     */
    public void toggleLegendShowing() {
        worldViewController.setShowLegend(!worldViewController.isShowLegend());
    }

    /**
     * Retrieves any files found in the strategies directory and stores them as a potential storage strategy.
     */
    private void readStorageStrategies() {
        storageStrategyScripts.clear();
        File[] strategyScriptFiles = new File(Constants.STRATEGIES_FILE_PATH).listFiles();

        if (strategyScriptFiles != null) {
            for (File file : strategyScriptFiles) {
                storageStrategyScripts.put(Utils.getStrategyName(file.getName()), file.getName());
            }
        }
    }

    /**
     * Reads the sprite data xml file and creates sprites for those defined in the file.
     */
    public void readSpriteData() {
        File file = new File(Utils.getWorkingDir() + Constants.SPRITES_FILE_PATH);

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);

            NodeList structureNodes = doc.getElementsByTagName("buildingSprites");
            List<AnimatedSprite> building = readSprites(structureNodes);
            structureNodes = doc.getElementsByTagName("powerPlantSprites");
            List<AnimatedSprite> powerPlant = readSprites(structureNodes);

            for (AnimatedSprite animatedSprite : building) {
                buildingSprites.put(animatedSprite.getId(), animatedSprite);
            }

            for (AnimatedSprite animatedSprite : powerPlant) {
                powerPlantSprites.put(animatedSprite.getId(), animatedSprite);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Reads a list of sprite nodes found in an xml file.
     * @param spritesList a list of sprite nodes
     * @return a list of sprites that have been created to match specifications
     */
    public List<AnimatedSprite> readSprites(NodeList spritesList) {
        List<AnimatedSprite> spriteList = new ArrayList<>();
        Node spritesNode = spritesList.item(0);

        if (spritesNode.getNodeType() == Node.ELEMENT_NODE) {
            Element structuresElement = (Element) spritesNode;
            NodeList structures = structuresElement.getElementsByTagName("sprite");
            int length = structures.getLength();

            for (int i = 0; i < length; ++i) {
                Node spriteNode = structures.item(i);
                if (spriteNode.getNodeType() == Node.ELEMENT_NODE) {
                    spriteList.add(readSprite(spriteNode));
                }
            }
        }

        return spriteList;
    }

    /**
     * Reads the specifications of a xml sprite element and creates a sprite from it.
     * @param spriteNode the xml node of the element
     * @return the sprite created from the specifications
     */
    public AnimatedSprite readSprite(Node spriteNode) {
        Element structureElement = (Element) spriteNode;

        String name = getElementStringFromTag(structureElement, "name");
        Integer id = Integer.valueOf(getElementStringFromTag(structureElement, "id"));
        Integer numberOfImages = Integer.valueOf(getElementStringFromTag(structureElement, "numberOfImages"));
        Double duration = Double.valueOf(getElementStringFromTag(structureElement, "duration"));

        String workingDir = Utils.getWorkingDir();
        List<Image> images = new ArrayList<>();
        BufferedImage bufferedImage;
        Image image;

        try {
            for (int i = 0; i < numberOfImages; ++i) {
                bufferedImage = ImageIO.read(new File(workingDir + "/images/" + name + "_" + i + ".png"));
                image = SwingFXUtils.toFXImage(bufferedImage, null);
                images.add(image);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return new AnimatedSprite(id, images, 0, 0, duration);
    }

    /**
     * Reads in a saved simulation xml file.
     * @param file the simulation file
     */
    public void readSimulation(File file) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);

            NodeList structureNodes = doc.getElementsByTagName("templateStructures");
            List<Structure> templateStructures = readStructures(structureNodes);
            structureNodes = doc.getElementsByTagName("worldStructures");
            List<Structure> worldStructures = readStructures(structureNodes);

            templateStructureData.addAll(templateStructures);
            worldStructureData.addAll(worldStructures);

            setSimulationFilePath(file);
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Reads both the buildings and power plants from a list of xml structure nodes.
     * @param structuresList the list of xml structure nodes
     * @return a list of actual structures created from the xml specifications
     */
    public List<Structure> readStructures(NodeList structuresList) {
        List<Structure> structureList = new ArrayList<>();
        Node structuresNode = structuresList.item(0);

        if (structuresNode.getNodeType() == Node.ELEMENT_NODE) {
            Element structuresElement = (Element) structuresNode;
            NodeList structures = structuresElement.getElementsByTagName("simpleStructure");
            int length = structures.getLength();

            for (int i = 0; i < length; ++i) {
                Node structureNode = structures.item(i);
                if (structureNode.getNodeType() == Node.ELEMENT_NODE) {
                    structureList.add(readBuilding(structureNode));
                }
            }

            structures = structuresElement.getElementsByTagName("powerPlant");

            length = structures.getLength();

            for (int i = 0; i < length; ++i) {
                Node structureNode = structures.item(i);
                if (structureNode.getNodeType() == Node.ELEMENT_NODE) {
                    structureList.add(readPowerPlant(structureNode));
                }
            }

        }

        return structureList;
    }

    /**
     * Reads the specifications of a xml building element and creates a building from it.
     * @param structureNode the building node
     * @return the building
     */
    public Structure readBuilding(Node structureNode) {
        Element structureElement = (Element) structureNode;

        String name = getElementStringFromTag(structureElement, "name");
        Double x = Double.valueOf(getElementStringFromTag(structureElement, "x"));
        Double y = Double.valueOf(getElementStringFromTag(structureElement, "y"));
        Integer sprite = Integer.valueOf(getElementStringFromTag(structureElement, "sprite"));
        Boolean usingCustomLoadProfiles = Boolean.valueOf(getElementStringFromTag(structureElement, "usingCustomLoadProfiles"));

        NodeList applianceList = structureElement.getElementsByTagName("appliances");
        List<Appliance> appliances = readAppliances(applianceList);
        NodeList energySourceList = structureElement.getElementsByTagName("energySources");
        List<EnergySource> energySources = readEnergySources(energySourceList);
        NodeList energyStorageList = structureElement.getElementsByTagName("energyStorageDevices");
        List<EnergyStorage> energyStorageDevices = readEnergyStorageDevices(energyStorageList);
        NodeList usageTimeSpanList = structureElement.getElementsByTagName("usageTimeSpans");
        List<UsageTimeSpan> usageTimeSpan = readUsageTimeSpans(usageTimeSpanList);

        return new Building(name,
                StructureUtil.getNextStructureId(),
                x,
                y,
                buildingSprites.get(sprite),
                appliances,
                energySources,
                energyStorageDevices,
                usageTimeSpan,
                usingCustomLoadProfiles);
    }

    /**
     * Reads the specifications of a xml power plant element and creates a power plant from it.
     * @param structureNode the power plant node
     * @return the power plant
     */
    public Structure readPowerPlant(Node structureNode) {
        Element structureElement = (Element) structureNode;

        String name = getElementStringFromTag(structureElement, "name");
        Double x = Double.valueOf(getElementStringFromTag(structureElement, "x"));
        Double y = Double.valueOf(getElementStringFromTag(structureElement, "y"));
        Integer sprite = Integer.valueOf(getElementStringFromTag(structureElement, "sprite"));
        Double emissionRate = Double.parseDouble(getElementStringFromTag(structureElement, "emissionRate"));
        Double cost = Double.parseDouble(getElementStringFromTag(structureElement, "cost"));
        Double capacity = Double.parseDouble(getElementStringFromTag(structureElement, "capacity"));

        return new PowerPlant(name, StructureUtil.getNextStructureId(), x, y, powerPlantSprites.get(sprite), emissionRate, cost, capacity);
    }

    /**
     * Retrieves the string value between an xml element tag.
     * @param parent the parent element
     * @param tag the element's name/tag
     * @return the value of the tag
     */
    private String getElementStringFromTag(Element parent, String tag) {
        NodeList nodeList = parent.getElementsByTagName(tag);
        Element element = (Element) nodeList.item(0);
        NodeList childNodes = element.getChildNodes();
        return childNodes.item(0).getNodeValue();
    }

    /**
     * Reads the specifications from a list of xml time span elements and creates a list of time spans from them.
     * @param timeSpansList the time span xml node list
     * @return the list of actual time spans
     */
    public List<TimeSpan> readTimeSpans(NodeList timeSpansList) {
        List<TimeSpan> timeSpanList = new ArrayList<>();
        Node propertiesNode = timeSpansList.item(0);

        if (propertiesNode.getNodeType() == Node.ELEMENT_NODE) {
            Element propertiesElement = (Element) propertiesNode;
            NodeList properties = propertiesElement.getElementsByTagName("timeSpan");
            int length = properties.getLength();

            for (int i = 0; i < length; ++i) {
                Node propertyNode = properties.item(i);
                if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element propertyElement = (Element) propertyNode;

                    Integer from = Integer.parseInt(getElementStringFromTag(propertyElement, "from"));
                    Integer to = Integer.parseInt(getElementStringFromTag(propertyElement, "to"));

                    TimeSpan timeSpan = new TimeSpan(LocalTime.ofSecondOfDay(from), LocalTime.ofSecondOfDay(to));

                    timeSpanList.add(timeSpan);
                }
            }
        }

        return timeSpanList;
    }

    /**
     * Reads the specifications from a list of xml usage time span elements and creates a list of usage time spans from them.
     * @param timeSpansList the usage time span xml node list
     * @return the list of actual usage time spans
     */
    public List<UsageTimeSpan> readUsageTimeSpans(NodeList timeSpansList) {
        List<UsageTimeSpan> timeSpanList = new ArrayList<>();
        Node propertiesNode = timeSpansList.item(0);

        if (propertiesNode.getNodeType() == Node.ELEMENT_NODE) {
            Element propertiesElement = (Element) propertiesNode;
            NodeList properties = propertiesElement.getElementsByTagName("usageTimeSpan");
            int length = properties.getLength();

            for (int i = 0; i < length; ++i) {
                Node propertyNode = properties.item(i);
                if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element propertyElement = (Element) propertyNode;

                    Double usage = Double.parseDouble(getElementStringFromTag(propertyElement, "usage"));
                    Integer from = Integer.parseInt(getElementStringFromTag(propertyElement, "from"));
                    Integer to = Integer.parseInt(getElementStringFromTag(propertyElement, "to"));

                    UsageTimeSpan timeSpan = new UsageTimeSpan(usage, LocalTime.ofSecondOfDay(from), LocalTime.ofSecondOfDay(to));

                    timeSpanList.add(timeSpan);
                }
            }
        }

        return timeSpanList;
    }

    /**
     * Creates a list of appliances from the specifications in a list of appliance xml elements.
     * @param devicesList the list of appliance xml elements
     * @return the list of appliances
     */
    public List<Appliance> readAppliances(NodeList devicesList) {
        List<Appliance> deviceList = new ArrayList<>();
        Node devicesNode = devicesList.item(0);

        if (devicesNode.getNodeType() == Node.ELEMENT_NODE) {
            Element devicesElement = (Element) devicesNode;
            NodeList devices = devicesElement.getElementsByTagName("appliance");
            int length = devices.getLength();

            for (int i = 0; i < length; ++i) {
                Node deviceNode = devices.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element deviceElement = (Element) deviceNode;

                    String name = getElementStringFromTag(deviceElement, "name");
                    Double standbyConsumption = Double.parseDouble(getElementStringFromTag(deviceElement, "standbyConsumption"));
                    Double usageConsumption = Double.parseDouble(getElementStringFromTag(deviceElement, "usageConsumption"));

                    NodeList timeSpanList = deviceElement.getElementsByTagName("timeSpans");
                    List<TimeSpan> timeSpans = readTimeSpans(timeSpanList);

                    Appliance appliance = new Appliance(name, DeviceUtil.getNextDeviceId(), standbyConsumption, usageConsumption, timeSpans);

                    deviceList.add(appliance);
                }
            }
        }

        return deviceList;
    }

    /**
     * Creates a list of energy sources from the specifications in a list of energy source xml elements.
     * @param devicesList the list of energy source xml elements
     * @return the list of energy sources
     */
    public List<EnergySource> readEnergySources(NodeList devicesList) {
        List<EnergySource> deviceList = new ArrayList<>();
        Node devicesNode = devicesList.item(0);

        if (devicesNode.getNodeType() == Node.ELEMENT_NODE) {
            Element devicesElement = (Element) devicesNode;
            NodeList devices = devicesElement.getElementsByTagName("energySource");
            int length = devices.getLength();

            for (int i = 0; i < length; ++i) {
                Node deviceNode = devices.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element deviceElement = (Element) deviceNode;

                    String name = getElementStringFromTag(deviceElement, "name");

                    EnergySource energySource = new EnergySource(name, DeviceUtil.getNextDeviceId(), 0, 0, 0);

                    deviceList.add(energySource);
                }
            }
        }

        return deviceList;
    }

    /**
     * Creates a list of storage devices from the specifications in a list of storage device xml elements.
     * @param devicesList the list of storage device xml elements
     * @return the list of energy storage devices
     */
    public List<EnergyStorage> readEnergyStorageDevices(NodeList devicesList) {
        List<EnergyStorage> deviceList = new ArrayList<>();
        Node devicesNode = devicesList.item(0);

        if (devicesNode.getNodeType() == Node.ELEMENT_NODE) {
            Element devicesElement = (Element) devicesNode;
            NodeList devices = devicesElement.getElementsByTagName("energyStorage");
            int length = devices.getLength();

            for (int i = 0; i < length; ++i) {
                Node deviceNode = devices.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element deviceElement = (Element) deviceNode;

                    String name = getElementStringFromTag(deviceElement, "name");
                    Double transferCapacity = Double.parseDouble(getElementStringFromTag(deviceElement, "transferCapacity"));
                    Double storageCapacity = Double.parseDouble(getElementStringFromTag(deviceElement, "storageCapacity"));
                    String storageStrategy = getElementStringFromTag(deviceElement, "storageStrategy");

                    EnergyStorage energyStorage = new EnergyStorage(name, DeviceUtil.getNextDeviceId(), transferCapacity, storageCapacity, 0, storageStrategy);

                    deviceList.add(energyStorage);
                }
            }
        }

        return deviceList;
    }

    /**
     * Saves the simulation's world and template structures and all their properties to a specified file.
     * @param file the specified file to save the simulation to
     */
    public void saveSimulation(File file) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        Document doc;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.newDocument();

            Element mainRootElement = doc.createElementNS("", "smartGrid");
            doc.appendChild(mainRootElement);

            Element template = doc.createElement("templateStructures");
            for (Structure structure : templateStructureData) {
                template.appendChild(getStructureNode(doc, structure));
            }
            mainRootElement.appendChild(template);

            Element world = doc.createElement("worldStructures");
            for (Structure structure : worldStructureData) {
                world.appendChild(getStructureNode(doc, structure));
            }
            mainRootElement.appendChild(world);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult outputFile = new StreamResult(file);

            transformer.transform(source, outputFile);

            setSimulationFilePath(file);
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Creates either a power plant of a building node depending on the type of structure passed in.
     * @param doc the xml document being created
     * @param structure the structure to save
     * @return the xml structure node created
     */
    private Node getStructureNode(Document doc, Structure structure) {
        if (structure instanceof PowerPlant)
            return getPowerPlantStructureNode(doc, (PowerPlant) structure);
        else
            return getBuildingNode(doc, (Building) structure);
    }

    /**
     * Creates an xml node of a power plant.
     * @param doc the xml document being created
     * @param powerPlant the power plant to save
     * @return the xml node created
     */
    private Node getPowerPlantStructureNode(Document doc, PowerPlant powerPlant) {
        Element structureNode = doc.createElement("powerPlant");

        structureNode.appendChild(getElement(doc, "name", powerPlant.getName()));
        structureNode.appendChild(getElement(doc, "x", String.valueOf(powerPlant.getAnimatedSprite().getXPosition())));
        structureNode.appendChild(getElement(doc, "y", String.valueOf(powerPlant.getAnimatedSprite().getYPosition())));
        structureNode.appendChild(getElement(doc, "sprite", String.valueOf(powerPlant.getAnimatedSprite().getId())));
        structureNode.appendChild(getElement(doc, "emissionRate", String.valueOf(powerPlant.getEmissionRate())));
        structureNode.appendChild(getElement(doc, "cost", String.valueOf(powerPlant.getCost())));
        structureNode.appendChild(getElement(doc, "capacity", String.valueOf(powerPlant.getCapacity())));

        return structureNode;
    }

    /**
     * Creates an xml node of a building.
     * @param doc the document being created
     * @param structure the building being saved
     * @return the xml node created
     */
    private Node getBuildingNode(Document doc, Building structure) {
        Element structureNode = doc.createElement("building");

        structureNode.appendChild(getElement(doc, "name", structure.getName()));
        structureNode.appendChild(getElement(doc, "x", String.valueOf(structure.getAnimatedSprite().getXPosition())));
        structureNode.appendChild(getElement(doc, "y", String.valueOf(structure.getAnimatedSprite().getYPosition())));
        structureNode.appendChild(getElement(doc, "sprite", String.valueOf(structure.getAnimatedSprite().getId())));
        structureNode.appendChild(getElement(doc, "usingCustomLoadProfiles", String.valueOf(structure.isUsingCustomLoadProfile())));

        Element appliances = doc.createElement("appliances");
        List<Appliance> applianceList = structure.getAppliances();
        for (Appliance appliance : applianceList) {
            appliances.appendChild(getApplianceNode(doc, appliance));
        }
        structureNode.appendChild(appliances);

        Element energySources = doc.createElement("energySources");
        List<EnergySource> energySourceList = structure.getEnergySources();
        for (EnergySource energySource : energySourceList) {
            energySources.appendChild(getEnergySourceNode(doc, energySource));
        }
        structureNode.appendChild(energySources);

        Element energyStorageDevices = doc.createElement("energyStorageDevices");
        List<EnergyStorage> energyStorageList = structure.getEnergyStorageDevices();
        for (EnergyStorage energyStorage : energyStorageList) {
            energyStorageDevices.appendChild(getEnergyStorageNode(doc, energyStorage));
        }
        structureNode.appendChild(energyStorageDevices);

        Element usageTimeSpanMembers = doc.createElement("usageTimeSpans");
        List<UsageTimeSpan> usageTimeSpans = structure.getManualLoadProfileData();
        for (UsageTimeSpan usageTimeSpan : usageTimeSpans) {
            usageTimeSpanMembers.appendChild(getUsageTimeSpanNode(doc, usageTimeSpan));
        }
        structureNode.appendChild(usageTimeSpanMembers);

        return structureNode;
    }

    /**
     * Creates an xml node of an appliance.
     * @param doc the document being created
     * @param appliance the appliance being saved
     * @return the xml node created
     */
    private Node getApplianceNode(Document doc, Appliance appliance) {
        Element deviceNode = doc.createElement("appliance");

        deviceNode.appendChild(getElement(doc, "name", appliance.getName()));
        deviceNode.appendChild(getElement(doc, "standbyConsumption", String.valueOf(appliance.getStandbyConsumption())));
        deviceNode.appendChild(getElement(doc, "usageConsumption", String.valueOf(appliance.getUsageConsumption())));

        Element timeSpanMembers = doc.createElement("timeSpans");
        List<TimeSpan> timeSpans = appliance.getActiveTimeSpans();
        for (TimeSpan timeSpan : timeSpans) {
            timeSpanMembers.appendChild(getTimeSpanNode(doc, timeSpan));
        }
        deviceNode.appendChild(timeSpanMembers);

        return deviceNode;
    }

    /**
     * Creates an xml node of an energy source.
     * @param doc the document being created
     * @param energySource the energy source being saved
     * @return the xml node created
     */
    private Node getEnergySourceNode(Document doc, EnergySource energySource) {
        Element deviceNode = doc.createElement("energySource");

        deviceNode.appendChild(getElement(doc, "name", energySource.getName()));

        return deviceNode;
    }

    /**
     * Creates an xml node of an energy storage device.
     * @param doc the document being created
     * @param energyStorage the energy storage device being saved
     * @return the xml node created
     */
    private Node getEnergyStorageNode(Document doc, EnergyStorage energyStorage) {
        Element deviceNode = doc.createElement("energyStorage");

        deviceNode.appendChild(getElement(doc, "name", energyStorage.getName()));
        deviceNode.appendChild(getElement(doc, "transferCapacity", String.valueOf(energyStorage.getTransferCapacity())));
        deviceNode.appendChild(getElement(doc, "storageCapacity", String.valueOf(energyStorage.getStorageCapacity())));
        deviceNode.appendChild(getElement(doc, "storageStrategy", String.valueOf(energyStorage.getStorageStrategy())));

        return deviceNode;
    }

    /**
     * Creates a generic xml element.
     * @param doc the xml document being created
     * @param elementName the xml elements name
     * @param value the xml elements value
     * @return the xml element
     */
    private Element getElement(Document doc, String elementName, String value) {
        Element node = doc.createElement(elementName);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    /**
     * Creates an xml node of a time span.
     * @param doc the xml document being created
     * @param timeSpan the time span being saved
     * @return the xml node created
     */
    private Node getTimeSpanNode(Document doc, TimeSpan timeSpan) {
        Element timeSpanNode = doc.createElement("timeSpan");
        timeSpanNode.appendChild(getElement(doc, "from", String.valueOf(timeSpan.getFrom().toSecondOfDay())));
        timeSpanNode.appendChild(getElement(doc, "to", String.valueOf(timeSpan.getTo().toSecondOfDay())));

        return timeSpanNode;
    }

    /**
     * Creates an xml node of a usage time span.
     * @param doc the xml document being created
     * @param usageTimeSpan the usage time span being saved
     * @return the xml node created
     */
    private Node getUsageTimeSpanNode(Document doc, UsageTimeSpan usageTimeSpan) {
        Element usageTimeSpanNode = doc.createElement("usageTimeSpan");
        usageTimeSpanNode.appendChild(getElement(doc, "usage", String.valueOf(usageTimeSpan.getUsage())));
        usageTimeSpanNode.appendChild(getElement(doc, "from", String.valueOf(usageTimeSpan.getFrom().toSecondOfDay())));
        usageTimeSpanNode.appendChild(getElement(doc, "to", String.valueOf(usageTimeSpan.getTo().toSecondOfDay())));

        return usageTimeSpanNode;
    }

    public ObservableList<Structure> getWorldStructureData() {
        return worldStructureData;
    }

    public ObservableList<Structure> getTemplateStructureData() {
        return templateStructureData;
    }

    public ObjectProperty<Structure> selectedTemplateStructureProperty() {
        return selectedTemplateStructure;
    }

    public ObjectProperty<Structure> selectedWorldStructureProperty() {
        return selectedWorldStructure;
    }

    public ObjectProperty<Structure> selectedStructureProperty() {
        return selectedStructure;
    }

    public HashMap<Integer, AnimatedSprite> getPowerPlantSprites() {
        return powerPlantSprites;
    }

    public HashMap<Integer, AnimatedSprite> getBuildingSprites() {
        return buildingSprites;
    }

    public ObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public LocalTime getCurrentTime() {
        return currentTime.get();
    }

    public void setCurrentTime(LocalTime currentTime) {
        this.currentTime.set(currentTime);
    }

    public ObjectProperty<LocalTime> currentTimeProperty() {
        return currentTime;
    }

    public LocalDate getCurrentDate() {
        return currentDate.get();
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate.set(currentDate);
    }

    public ObjectProperty<LocalDate> currentDateProperty() {
        return currentDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public int getDailyStatsDay() {
        return dailyStatsDay.get();
    }

    public void setDailyStatsDay(int dailyStatsDay) {
        this.dailyStatsDay.set(dailyStatsDay);
    }

    public IntegerProperty dailyStatsDayProperty() {
        return dailyStatsDay;
    }

    public WorldTimer.UpdateRate getUpdateRate() {
        return updateRate.get();
    }

    public void setUpdateRate(WorldTimer.UpdateRate updateRate) {
        this.updateRate.set(updateRate);
    }

    public ObjectProperty<WorldTimer.UpdateRate> updateRateProperty() {
        return updateRate;
    }

    public World getWorld() {
        return world;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BuildingDetailsPaneController getBuildingDetailsPaneController() {
        return buildingDetailsPaneController;
    }

    public SimulationControlsController getSimulationControlsController() {
        return simulationControlsController;
    }

    public DailyStatisticsController getDailyStatisticsController() {
        return dailyStatisticsController;
    }

    public ProductionStatisticsController getProductionStatisticsController() {
        return productionStatisticsController;
    }

    public WorldViewController getWorldViewController() {
        return worldViewController;
    }

    public SimulationOverviewController getSimulationOverviewController() {
        return simulationOverviewController;
    }
}
