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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
    private StructureComparisonsController structureComparisonsController;
    private WorldViewController worldViewController;


    private HashMap<String, String> storageStrategyScripts;
    private World world;

    public Main()
    {
        Structure structure = new Structure("Temp", -1, -1, -1, ImageType.HOUSE_IMAGE);
        selectedTemplateStructure = new SimpleObjectProperty<>(structure);
        selectedWorldStructure = new SimpleObjectProperty<>(structure);
        selectedStructure = new SimpleObjectProperty<>(structure);

        storageStrategyScripts = new HashMap<>();
        world = new World();
    }

    private void readStorageStrategies()
    {
        File[] strategyScriptFiles = new File(Constants.STRATEGIES_FILE_PATH).listFiles();

        if (strategyScriptFiles != null)
        {
            for (File file : strategyScriptFiles)
            {
                storageStrategyScripts.put(Utils.getStrategyName(file.getName()), file.getName());
            }
        }
    }

    public ObservableList<Structure> getWorldStructureData()
    {
        return worldStructureData;
    }
    public ObservableList<Structure> getTemplateStructureData()
    {
        return templateStructureData;
    }
    public ObjectProperty<Structure> selectedTemplateStructureProperty()
    {
        return selectedTemplateStructure;
    }
    public ObjectProperty<Structure> selectedWorldStructureProperty()
    {
        return selectedWorldStructure;
    }
    public ObjectProperty<Structure> selectedStructureProperty()
    {
        return selectedStructure;
    }

    public ObjectProperty<SimulationState> simulationStateProperty()
    {
        return simulationState;
    }

    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }

    public LocalTime getCurrentTime()
    {
        return currentTime.get();
    }

    public ObjectProperty<LocalTime> currentTimeProperty()
    {
        return currentTime;
    }

    public void setCurrentTime(LocalTime currentTime)
    {
        this.currentTime.set(currentTime);
    }

    public LocalDate getCurrentDate()
    {
        return currentDate.get();
    }

    public ObjectProperty<LocalDate> currentDateProperty()
    {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate)
    {
        this.currentDate.set(currentDate);
    }

    public LocalDate getEndDate()
    {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty()
    {
        return endDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate.set(endDate);
    }

    public LocalDate getStartDate()
    {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> startDateProperty()
    {
        return startDate;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate.set(startDate);
    }

    public int getDailyStatsDay()
    {
        return dailyStatsDay.get();
    }

    public IntegerProperty dailyStatsDayProperty()
    {
        return dailyStatsDay;
    }

    public void setDailyStatsDay(int dailyStatsDay)
    {
        this.dailyStatsDay.set(dailyStatsDay);
    }

    public WorldTimer.UpdateRate getUpdateRate()
    {
        return updateRate.get();
    }
    public ObjectProperty<WorldTimer.UpdateRate> updateRateProperty()
    {
        return updateRate;
    }
    public void setUpdateRate(WorldTimer.UpdateRate updateRate)
    {
        this.updateRate.set(updateRate);
    }

    public World getWorld()
    {
        return world;
    }
    public StructureDetailsPaneController getStructureDetailsPaneController() {
        return structureDetailsPaneController;
    }
    public SimulationControlsController getSimulationControlsController() {
        return simulationControlsController;
    }
    public DailyStatisticsController getDailyStatisticsController()
    {
        return dailyStatisticsController;
    }
    public ProductionStatisticsController getProductionStatisticsController()
    {
        return productionStatisticsController;
    }
    public StructureComparisonsController getStructureComparisonsController()
    {
        return structureComparisonsController;
    }
    public WorldViewController getWorldViewController()
    {
        return worldViewController;
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Working Title");

        world.setMain(this);
        initRootLayout();
        world.resetSimulation();
    }

    public void reset()
    {
        worldStructureData.clear();
        templateStructureData.clear();
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
            rootLayout = (BorderPane) loader.load();

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
        if (file != null)
        {
            readSimulation(file);
        }
    }

    private BorderPane initStructureDetailsPane()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/StructureDetailsPane.fxml"));
            BorderPane structureDetails = (BorderPane) loader.load();

            structureDetailsPaneController = loader.getController();
            return structureDetails;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BorderPane initSimulationControlsPane()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/SimulationControls.fxml"));
            BorderPane simulationControls = (BorderPane) loader.load();

            simulationControlsController = loader.getController();
            simulationControlsController.setMain(this);
            return simulationControls;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BorderPane initDailyStatisticsPane()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/DailyStatistics.fxml"));
            BorderPane dailyStatistics = (BorderPane) loader.load();

            dailyStatisticsController = loader.getController();
            dailyStatisticsController.setMain(this);
            return dailyStatistics;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BorderPane initProductionStatisticsPane()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ProductionStatistics.fxml"));
            BorderPane productionStatistics = (BorderPane) loader.load();

            productionStatisticsController = loader.getController();
            productionStatisticsController.setMain(this);
            return productionStatistics;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private BorderPane initStructureComparisonsPane()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/StructureComparisons.fxml"));
            BorderPane comparisonsPane = (BorderPane) loader.load();

            structureComparisonsController = loader.getController();
            structureComparisonsController.setMain(this);
            return comparisonsPane;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private BorderPane initWorldViewPane()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/WorldView.fxml"));
            BorderPane worldViewPane = (BorderPane) loader.load();

            worldViewController = loader.getController();
            worldViewController.setMain(this);
            return worldViewPane;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public AnchorPane initStructureOverview()
    {
        try
        {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/StructureOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            StructureOverviewController controller = loader.getController();
            controller.setMain(this);
            controller.showSimulationControlsPane(initSimulationControlsPane());
            controller.showStructureDetailsPane(initStructureDetailsPane());
            controller.showDailyStatisticsPane(initDailyStatisticsPane());
            controller.showProductionStatisticsPane(initProductionStatisticsPane());
            controller.showStructureComparisonsPane(initStructureComparisonsPane());
            controller.showWorldViewPane(initWorldViewPane());
            return personOverview;
        }
        catch (IOException e)
        {
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
    public boolean showStructureEditDialog(SingleUnitStructure structure)
    {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/StructureEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Structure");
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

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
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
    public boolean showPowerPlantEditDialog(PowerPlant powerPlant)
    {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PowerPlantEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

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
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getSimulationFilePath()
    {
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
    public void setSimulationFilePath(File file)
    {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("Working Title - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("Working Title");
        }
    }

    public void readSimulation(File file)
    {
        try
        {
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
        }
        catch (Exception exception)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public List<Structure> readStructures(NodeList structuresList)
    {
        List<Structure> structureList = new ArrayList<Structure>();
        Node structuresNode = structuresList.item(0);

        if (structuresNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element structuresElement = (Element)structuresNode;
            NodeList structures = structuresElement.getElementsByTagName("simpleStructure");
            int length = structures.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node structureNode = structures.item(i);
                if (structureNode.getNodeType() == Node.ELEMENT_NODE)
                {
                   structureList.add(readSimpleStructure(structureNode));
                }
            }

            structures = structuresElement.getElementsByTagName("powerPlant");

            length = structures.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node structureNode = structures.item(i);
                if (structureNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    structureList.add(readPowerPlant(structureNode));
                }
            }

        }

        return structureList;
    }

    public Structure readSimpleStructure(Node structureNode)
    {
        Element structureElement = (Element)structureNode;

        String name = getElementStringFromTag(structureElement, "name");
        Integer id = Integer.valueOf(getElementStringFromTag(structureElement, "id")); // TODO: remove id's from save file?
        String image = getElementStringFromTag(structureElement, "image");

        ImageType imageType = ImageType.HOUSE_IMAGE;
        if (Utils.isInEnum(image, ImageType.class))
        {
            imageType = ImageType.valueOf(image);
        }

        NodeList applianceList = structureElement.getElementsByTagName("appliances");
        List<Appliance> appliances = readAppliances(applianceList);
        NodeList energySourceList = structureElement.getElementsByTagName("energySources");
        List<EnergySource> energySources = readEnergySources(energySourceList);
        NodeList energyStorageList = structureElement.getElementsByTagName("energyStorageDevices");
        List<EnergyStorage> energyStorageDevices = readEnergyStorageDevices(energyStorageList);

        return new SingleUnitStructure(name,
                StructureUtil.getNextStructureId(),
                imageType,
                0,
                0,
                appliances,
                energySources,
                energyStorageDevices);
    }

    public Structure readPowerPlant(Node structureNode)
    {
        Element structureElement = (Element)structureNode;

        String name = getElementStringFromTag(structureElement, "name");
        Integer id = Integer.valueOf(getElementStringFromTag(structureElement, "id"));
        String image = getElementStringFromTag(structureElement, "image");
        Double emissionRate = Double.parseDouble(getElementStringFromTag(structureElement, "emissionRate"));
        Double cost = Double.parseDouble(getElementStringFromTag(structureElement, "cost"));
        Double capacity = Double.parseDouble(getElementStringFromTag(structureElement, "capacity"));

        ImageType imageType = ImageType.POWER_PLANT_IMAGE;
        if (Utils.isInEnum(image, ImageType.class))
        {
            imageType = ImageType.valueOf(image);
        }

        return new PowerPlant(name, StructureUtil.getNextStructureId(), imageType, 0, 0, emissionRate, cost, capacity);
    }

    private String getElementStringFromTag(Element parent, String tag)
    {
        NodeList nodeList = parent.getElementsByTagName(tag);
        Element element = (Element)nodeList.item(0);
        NodeList childNodes = element.getChildNodes();
        return childNodes.item(0).getNodeValue();
    }

    public List<TimeSpan> readTimeSpans(NodeList timeSpansList)
    {
        List<TimeSpan> timeSpanList = new ArrayList<TimeSpan>();
        Node propertiesNode = timeSpansList.item(0);

        if (propertiesNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element propertiesElement = (Element)propertiesNode;
            NodeList properties = propertiesElement.getElementsByTagName("timeSpan");
            int length = properties.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node propertyNode = properties.item(i);
                if (propertyNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element propertyElement = (Element)propertyNode;

                    Integer from = Integer.parseInt(getElementStringFromTag(propertyElement, "from"));
                    Integer to = Integer.parseInt(getElementStringFromTag(propertyElement, "to"));

                    TimeSpan timeSpan = new TimeSpan(LocalTime.ofSecondOfDay(from), LocalTime.ofSecondOfDay(to));

                    timeSpanList.add(timeSpan);
                }
            }
        }

        return timeSpanList;
    }

    public List<Appliance> readAppliances(NodeList devicesList)
    {
        List<Appliance> deviceList = new ArrayList<>();
        Node devicesNode = devicesList.item(0);

        if (devicesNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element devicesElement = (Element)devicesNode;
            NodeList devices = devicesElement.getElementsByTagName("appliance");
            int length = devices.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node deviceNode = devices.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element deviceElement = (Element)deviceNode;

                    String name = getElementStringFromTag(deviceElement, "name");
                    Integer id = Integer.parseInt(getElementStringFromTag(deviceElement, "id"));
                    Double standbyConsumption = Double.parseDouble(getElementStringFromTag(deviceElement, "standbyConsumption"));
                    Double usageConsumption = Double.parseDouble(getElementStringFromTag(deviceElement, "usageConsumption"));

                    NodeList timeSpanList = deviceElement.getElementsByTagName("timeSpans");
                    List<TimeSpan> timeSpans = readTimeSpans(timeSpanList);

                    Appliance appliance = new Appliance(name, id, standbyConsumption, usageConsumption, timeSpans);

                    deviceList.add(appliance);
                }
            }
        }

        return deviceList;
    }

    public List<EnergySource> readEnergySources(NodeList devicesList)
    {
        List<EnergySource> deviceList = new ArrayList<>();
        Node devicesNode = devicesList.item(0);

        if (devicesNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element devicesElement = (Element)devicesNode;
            NodeList devices = devicesElement.getElementsByTagName("energySource");
            int length = devices.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node deviceNode = devices.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element deviceElement = (Element)deviceNode;

                    String name = getElementStringFromTag(deviceElement, "name");
                    Integer id = Integer.parseInt(getElementStringFromTag(deviceElement, "id"));

                    EnergySource energySource = new EnergySource(name, id, 0, 0, 0); // TODO: implement energy source

                    deviceList.add(energySource);
                }
            }
        }

        return deviceList;
    }

    public List<EnergyStorage> readEnergyStorageDevices(NodeList devicesList)
    {
        List<EnergyStorage> deviceList = new ArrayList<>();
        Node devicesNode = devicesList.item(0);

        if (devicesNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element devicesElement = (Element)devicesNode;
            NodeList devices = devicesElement.getElementsByTagName("energyStorage");
            int length = devices.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node deviceNode = devices.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element deviceElement = (Element)deviceNode;

                    String name = getElementStringFromTag(deviceElement, "name");
                    Integer id = Integer.parseInt(getElementStringFromTag(deviceElement, "id"));
                    Double chargeDischargeRate = Double.parseDouble(getElementStringFromTag(deviceElement, "chargingRate"));
                    Double storageCapacity = Double.parseDouble(getElementStringFromTag(deviceElement, "storageCapacity"));
                    String storageStrategy = getElementStringFromTag(deviceElement, "storageStrategy");

                    EnergyStorage energyStorage = new EnergyStorage(name, id, chargeDischargeRate, storageCapacity, 0, storageStrategy);

                    deviceList.add(energyStorage);
                }
            }
        }

        return deviceList;
    }

    public void saveSimulation(File file)
    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        Document doc;

        try
        {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.newDocument();

            Element mainRootElement = doc.createElementNS("", "smartGrid"); // TODO: use proper NS
            doc.appendChild(mainRootElement);

            Element template = doc.createElement("templateStructures");
            for (Structure structure : templateStructureData)
            {
                template.appendChild(getStructureNode(doc, structure));
            }
            mainRootElement.appendChild(template);

            Element world = doc.createElement("worldStructures");
            for (Structure structure : worldStructureData)
            {
                world.appendChild(getStructureNode(doc, structure));
            }
            mainRootElement.appendChild(world);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult outputFile = new StreamResult(file);

            transformer.transform(source, outputFile);

            setSimulationFilePath(file);
        }
        catch (Exception exception)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    private Node getStructureNode(Document doc, Structure structure)
    {
        if (structure instanceof PowerPlant)
            return getPowerPlantStructureNode(doc, (PowerPlant)structure);
        else
            return getSimpleStructureNode(doc, (SingleUnitStructure)structure);
    }

    private Node getPowerPlantStructureNode(Document doc, PowerPlant powerPlant)
    {
        Element structureNode = doc.createElement("powerPlant");

        structureNode.appendChild(getElement(doc, "name", powerPlant.getName()));
        structureNode.appendChild(getElement(doc, "id", String.valueOf(powerPlant.getId())));
        structureNode.appendChild(getElement(doc, "image", String.valueOf(powerPlant.getImage())));
        structureNode.appendChild(getElement(doc, "emissionRate", String.valueOf(powerPlant.getEmissionRate())));
        structureNode.appendChild(getElement(doc, "cost", String.valueOf(powerPlant.getCost())));
        structureNode.appendChild(getElement(doc, "capacity", String.valueOf(powerPlant.getCapacity())));

        return structureNode;
    }

    private Node getSimpleStructureNode(Document doc, SingleUnitStructure structure)
    {
        Element structureNode = doc.createElement("simpleStructure");

        structureNode.appendChild(getElement(doc, "name", structure.getName()));
        structureNode.appendChild(getElement(doc, "id", String.valueOf(structure.getId())));
        structureNode.appendChild(getElement(doc, "image", String.valueOf(structure.getImage())));

        Element appliances = doc.createElement("appliances");
        List<Appliance> applianceList = structure.getAppliances();
        for (Appliance appliance : applianceList)
        {
            appliances.appendChild(getApplianceNode(doc, appliance));
        }
        structureNode.appendChild(appliances);

        Element energySources = doc.createElement("energySources");
        List<EnergySource> energySourceList = structure.getEnergySources();
        for (EnergySource energySource : energySourceList)
        {
            energySources.appendChild(getEnergySourceNode(doc, energySource));
        }
        structureNode.appendChild(energySources);

        Element energyStorageDevices = doc.createElement("energyStorageDevices");
        List<EnergyStorage> energyStorageList = structure.getEnergyStorageDevices();
        for (EnergyStorage energyStorage : energyStorageList)
        {
            energyStorageDevices.appendChild(getEnergyStorageNode(doc, energyStorage));
        }
        structureNode.appendChild(energyStorageDevices);

        return structureNode;
    }

    private Node getApplianceNode(Document doc, Appliance appliance)
    {
        Element deviceNode = doc.createElement("appliance");

        deviceNode.appendChild(getElement(doc, "name", appliance.getName()));
        deviceNode.appendChild(getElement(doc, "id", String.valueOf(appliance.getId())));
        deviceNode.appendChild(getElement(doc, "standbyConsumption", String.valueOf(appliance.getStandbyConsumption())));
        deviceNode.appendChild(getElement(doc, "usageConsumption", String.valueOf(appliance.getUsageConsumption())));

        Element timeSpanMembers = doc.createElement("timeSpans");
        List<TimeSpan> timeSpans = appliance.getActiveTimeSpans();
        for (TimeSpan timeSpan : timeSpans)
        {
            timeSpanMembers.appendChild(getTimeSpanNode(doc, timeSpan));
        }
        deviceNode.appendChild(timeSpanMembers);

        return deviceNode;
    }

    private Node getEnergySourceNode(Document doc, EnergySource energySource)
    {
        Element deviceNode = doc.createElement("energySource");

        deviceNode.appendChild(getElement(doc, "name", energySource.getName()));
        deviceNode.appendChild(getElement(doc, "id", String.valueOf(energySource.getId())));

        return deviceNode;
    }

    private Node getEnergyStorageNode(Document doc, EnergyStorage energyStorage)
    {
        Element deviceNode = doc.createElement("energyStorage");

        deviceNode.appendChild(getElement(doc, "name", energyStorage.getName()));
        deviceNode.appendChild(getElement(doc, "id", String.valueOf(energyStorage.getId())));
        deviceNode.appendChild(getElement(doc, "chargingRate", String.valueOf(energyStorage.getChargingRate())));
        deviceNode.appendChild(getElement(doc, "storageCapacity", String.valueOf(energyStorage.getStorageCapacity())));
        deviceNode.appendChild(getElement(doc, "storageStrategy", String.valueOf(energyStorage.getStorageStrategy())));

        return deviceNode;
    }
    private Element getElement(Document doc, String elementName, String value)
    {
        Element node = doc.createElement(elementName);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private Node getTimeSpanNode(Document doc, TimeSpan timeSpan)
    {
        Element timeSpanNode = doc.createElement("timeSpan");
        timeSpanNode.appendChild(getElement(doc, "from", String.valueOf(timeSpan.getFrom().toSecondOfDay())));
        timeSpanNode.appendChild(getElement(doc, "to", String.valueOf(timeSpan.getTo().toSecondOfDay())));

        return timeSpanNode;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
