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

    private StructureDetailsPaneController structureDetailsPaneController;
    private SimulationControlsController simulationControlsController;
    private DailyStatisticsController dailyStatisticsController;
    private ProductionStatisticsController productionStatisticsController;
    private StructureOverviewController structureOverviewController;
    private WorldViewController worldViewController;

    private HashMap<String, String> storageStrategyScripts;
    private HashMap<Integer, AnimatedSprite> buildingSprites;
    private HashMap<Integer, AnimatedSprite> powerPlantSprites;
    private World world;

    public Main() {
        List<Image> images = new ArrayList<>();
        try {

            BufferedImage bufferedImage = ImageIO.read(new File(Utils.getWorkingDir() + "/images/Selection.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            images.add(image);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Structure structure = new Structure("Temp", -1, -1, -1, new AnimatedSprite(-1, images, -1, -1, -1));
        selectedTemplateStructure = new SimpleObjectProperty<>(structure);
        selectedWorldStructure = new SimpleObjectProperty<>(structure);
        selectedStructure = new SimpleObjectProperty<>(structure);

        storageStrategyScripts = new HashMap<>();
        buildingSprites = new HashMap<>();
        powerPlantSprites = new HashMap<>();
        world = new World();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void readStorageStrategies() {
        storageStrategyScripts.clear();
        File[] strategyScriptFiles = new File(Constants.STRATEGIES_FILE_PATH).listFiles();

        if (strategyScriptFiles != null) {
            for (File file : strategyScriptFiles) {
                storageStrategyScripts.put(Utils.getStrategyName(file.getName()), file.getName());
            }
        }
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

    public StructureDetailsPaneController getStructureDetailsPaneController() {
        return structureDetailsPaneController;
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

    public StructureOverviewController getStructureOverviewController() {
        return structureOverviewController;
    }

    public void toggleLegendShowing() {
        worldViewController.setShowLegend(!worldViewController.isShowLegend());
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(Constants.APP_NAME);

        world.setMain(this);
        readSpriteData();
        initRootLayout();
        world.resetSimulation();
    }

    public void reset() {
        worldStructureData.clear();
        templateStructureData.clear();
        dailyStatisticsController.clearDemandChart();
        dailyStatisticsController.clearEmissionChart();
        dailyStatisticsController.clearPriceChart();
        productionStatisticsController.clearEmissionForDemandChart();
        productionStatisticsController.clearPriceForDemandChart();
        structureDetailsPaneController.clearLoadProfileDetails();
        structureDetailsPaneController.clearComparisons();
        worldViewController.clearSelection();
        world.resetSimulation();
        setSimulationFilePath(null);
    }

    /**
     * Initializes the root layout.
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

            rootLayout.setCenter(initStructureOverview());
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

    private Pane initStructureDetailsPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/StructureDetailsPane.fxml"));
            Pane structureDetails = loader.load();

            structureDetailsPaneController = loader.getController();
            structureDetailsPaneController.setMain(this);
            return structureDetails;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

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
     * Shows the person overview inside the root layout.
     */
    public Pane initStructureOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/StructureOverview.fxml"));
            Pane personOverview = loader.load();

            structureOverviewController = loader.getController();
            structureOverviewController.setMain(this);
            structureOverviewController.showSimulationControlsPane(initSimulationControlsPane());
            structureOverviewController.showStructureDetailsPane(initStructureDetailsPane());
            structureOverviewController.showDailyStatisticsPane(initDailyStatisticsPane());
            structureOverviewController.showProductionStatisticsPane(initProductionStatisticsPane());
            structureOverviewController.showWorldViewPane(initWorldViewPane());
            return personOverview;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Opens a dialog to edit details for the specified structure. If the user
     * clicks OK, the changes are saved into the provided structure object and true
     * is returned.
     *
     * @param structure the structure object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showBuildingEditDialog(Building structure) {
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
            structureDetailsPaneController.setStructureData(structure, structure.getLoadProfilesForWeek());

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showLoadProfileEditDialog(Building structure) {
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

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to edit details for the specified structure. If the user
     * clicks OK, the changes are saved into the provided structure object and true
     * is returned.
     *
     * @param powerPlant the structure object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPowerPlantEditDialog(PowerPlant powerPlant) {
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

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
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

    private String getElementStringFromTag(Element parent, String tag) {
        NodeList nodeList = parent.getElementsByTagName(tag);
        Element element = (Element) nodeList.item(0);
        NodeList childNodes = element.getChildNodes();
        return childNodes.item(0).getNodeValue();
    }

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

                    EnergySource energySource = new EnergySource(name, DeviceUtil.getNextDeviceId(), 0, 0, 0); // TODO: implement energy source

                    deviceList.add(energySource);
                }
            }
        }

        return deviceList;
    }

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

    public void saveSimulation(File file) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        Document doc;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.newDocument();

            Element mainRootElement = doc.createElementNS("", "smartGrid"); // TODO: use proper NS
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

    private Node getStructureNode(Document doc, Structure structure) {
        if (structure instanceof PowerPlant)
            return getPowerPlantStructureNode(doc, (PowerPlant) structure);
        else
            return getBuildingNode(doc, (Building) structure);
    }

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

    private Node getBuildingNode(Document doc, Building structure) {
        Element structureNode = doc.createElement("simpleStructure");

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

    private Node getEnergySourceNode(Document doc, EnergySource energySource) {
        Element deviceNode = doc.createElement("energySource");

        deviceNode.appendChild(getElement(doc, "name", energySource.getName()));

        return deviceNode;
    }

    private Node getEnergyStorageNode(Document doc, EnergyStorage energyStorage) {
        Element deviceNode = doc.createElement("energyStorage");

        deviceNode.appendChild(getElement(doc, "name", energyStorage.getName()));
        deviceNode.appendChild(getElement(doc, "transferCapacity", String.valueOf(energyStorage.getTransferCapacity())));
        deviceNode.appendChild(getElement(doc, "storageCapacity", String.valueOf(energyStorage.getStorageCapacity())));
        deviceNode.appendChild(getElement(doc, "storageStrategy", String.valueOf(energyStorage.getStorageStrategy())));

        return deviceNode;
    }

    private Element getElement(Document doc, String elementName, String value) {
        Element node = doc.createElement(elementName);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private Node getTimeSpanNode(Document doc, TimeSpan timeSpan) {
        Element timeSpanNode = doc.createElement("timeSpan");
        timeSpanNode.appendChild(getElement(doc, "from", String.valueOf(timeSpan.getFrom().toSecondOfDay())));
        timeSpanNode.appendChild(getElement(doc, "to", String.valueOf(timeSpan.getTo().toSecondOfDay())));

        return timeSpanNode;
    }

    private Node getUsageTimeSpanNode(Document doc, UsageTimeSpan usageTimeSpan) {
        Element usageTimeSpanNode = doc.createElement("usageTimeSpan");
        usageTimeSpanNode.appendChild(getElement(doc, "usage", String.valueOf(usageTimeSpan.getUsage())));
        usageTimeSpanNode.appendChild(getElement(doc, "from", String.valueOf(usageTimeSpan.getFrom().toSecondOfDay())));
        usageTimeSpanNode.appendChild(getElement(doc, "to", String.valueOf(usageTimeSpan.getTo().toSecondOfDay())));

        return usageTimeSpanNode;
    }
}
