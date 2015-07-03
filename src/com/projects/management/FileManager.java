package com.projects.management;

import com.projects.helper.*;
import com.projects.models.*;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dan on 5/27/2015.
 */
class FileManager
{

    public FileManager()
    {
    }

    public HashMap<ImageType, BufferedImage> readImages()
    {
        HashMap<ImageType, BufferedImage> images = new HashMap<ImageType, BufferedImage>();

        BufferedImage img;
        String workingDir = Utils.getWorkingDir();

        try
        {
            img = ImageIO.read(new File(workingDir + Constants.HOUSE_IMAGE_PATH));
            images.put(ImageType.HOUSE_IMAGE, img);

            img = ImageIO.read(new File(workingDir + Constants.POWER_PLANT_IMAGE_PATH));
            images.put(ImageType.POWER_PLANT_IMAGE, img);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return images;
    }

    public Template readTemplate(File file)
    {
        Template template = null;

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);

            NodeList structureNodes = doc.getElementsByTagName("template");
            List<Structure> structures = readStructures(structureNodes);
            structureNodes = doc.getElementsByTagName("world");
            List<Structure> worldStructures = readStructures(structureNodes);
            NodeList deviceNodes = doc.getElementsByTagName("devices");
            List<Device> devices = readDevices(deviceNodes);

            template = new Template(devices, structures, worldStructures);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return template;
    }

    public List<Structure> readStructures(NodeList structuresList)
    {
        List<Structure> structureList = new ArrayList<Structure>();
        Node structuresNode = structuresList.item(0);

        if (structuresNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element structuresElement = (Element)structuresNode;
            NodeList structures = structuresElement.getElementsByTagName("structure");
            int length = structures.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node structureNode = structures.item(i);
                if (structureNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element structureElement = (Element)structureNode;

                    String name = getElementStringFromTag(structureElement, "name");
                    Integer id = Integer.valueOf(getElementStringFromTag(structureElement, "id"));
                    String type = getElementStringFromTag(structureElement, "type");
                    String image = getElementStringFromTag(structureElement, "image");
                    Integer numberOfUnits = Integer.valueOf(getElementStringFromTag(structureElement, "numberOfUnits"));

                    StructureType structureType;
                    if (Utils.isInEnum(type, StructureType.class))
                    {
                        structureType = StructureType.valueOf(type);
                    }
                    else
                    {
                        // TODO: should be error or something...
                        continue;
                    }

                    ImageType imageType;
                    if (Utils.isInEnum(image, ImageType.class))
                    {
                        imageType = ImageType.valueOf(image);
                    }
                    else
                    {
                        continue;
                    }

                    NodeList structureProperties = structureElement.getElementsByTagName("properties");
                    List<Property> properties = readProperties(structureProperties);
                    NodeList devices = structureElement.getElementsByTagName("appliances");
                    List<Device> appliances = readDevices(devices);
                    devices = structureElement.getElementsByTagName("energySources");
                    List<Device> energySources = readDevices(devices);
                    devices = structureElement.getElementsByTagName("energyStorageDevices");
                    List<Device> energyStorageDevices = readDevices(devices);

                    Structure structure;

                    if (structureType != StructureType.POWER_PLANT)
                        structure =  new Structure(name, id, structureType, imageType, numberOfUnits, properties, appliances, energySources, energyStorageDevices);
                    else
                        structure = new PowerPlant(name, id, numberOfUnits, properties, appliances, energySources, energyStorageDevices);

                    structureList.add(structure);
                }
            }
        }

        return structureList;
    }

    private String getElementStringFromTag(Element parent, String tag)
    {
        NodeList nodeList = parent.getElementsByTagName(tag);
        Element element = (Element)nodeList.item(0);
        NodeList childNodes = element.getChildNodes();
        return childNodes.item(0).getNodeValue().trim();
    }

    public List<Property> readProperties(NodeList propertiesList)
    {
        List<Property> propertyList = new ArrayList<Property>();
        Node propertiesNode = propertiesList.item(0);

        if (propertiesNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element propertiesElement = (Element)propertiesNode;
            NodeList properties = propertiesElement.getElementsByTagName("property");
            int length = properties.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node propertyNode = properties.item(i);
                if (propertyNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element propertyElement = (Element)propertyNode;

                    String name = getElementStringFromTag(propertyElement, "name");

                    NodeList nodeList = propertyElement.getElementsByTagName("value");
                    Element element = (Element)nodeList.item(0);
                    String value = element.getChildNodes().item(0).getNodeValue().trim();

                    String type = nodeList.item(0).getAttributes().getNamedItem("type").getNodeValue();
                    Property property;

                    if (type.equals("BOOLEAN"))
                    {
                        property = new Property<Boolean>(name, Boolean.valueOf(value));
                    }
                    else if (type.equals("DOUBLE"))
                    {
                        property = new Property<Double>(name, Double.valueOf(value));
                    }
                    else if (type.equals("INTEGER"))
                    {
                        property = new Property<Integer>(name, Integer.valueOf(value));
                    }
                    else
                    {
                        // TODO: should be error...
                        continue;
                    }

                    propertyList.add(property);
                }
            }
        }

        return propertyList;
    }

    public List<Device> readDevices(NodeList devicesList)
    {
        List<Device> deviceList = new ArrayList<Device>();
        Node devicesNode = devicesList.item(0);

        if (devicesNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element devicesElement = (Element)devicesNode;
            NodeList devices = devicesElement.getElementsByTagName("device");
            int length = devices.getLength();

            for (int i = 0; i < length; ++i)
            {
                Node deviceNode = devices.item(i);
                if (deviceNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element deviceElement = (Element)deviceNode;

                    String name = getElementStringFromTag(deviceElement, "name");
                    String type = getElementStringFromTag(deviceElement, "type");
                    DeviceType deviceType;

                    if (Utils.isInEnum(type, DeviceType.class))
                    {
                        deviceType = DeviceType.valueOf(type);
                    }
                    else
                    {
                        // TODO: should be error or something...
                        continue;
                    }

                    NodeList propertiesList = deviceElement.getElementsByTagName("properties");
                    List<Property> properties = readProperties(propertiesList);

                    Device device = null;

                    switch (deviceType)
                    {
                        case APPLIANCE:
                        {
                            device = new Appliance(name, -1, properties);
                        } break;
                        case ENERGY_SOURCE:
                        {
                            device = new EnergySource(name, -1, properties);
                        } break;
                        case ENERGY_STORAGE:
                        {
                            device = new EnergyStorage(name, -1, properties);
                        } break;
                    }

                    deviceList.add(device);
                }
            }
        }

        return deviceList;
    }

    public void saveSimulation(File file, HashMap<Integer, Structure> structures, Collection<Integer> templateStructures, Collection<Integer> worldStructures, Template temp)
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

            Element template = doc.createElement("template");
            for (int structure : templateStructures)
            {
                template.appendChild(getStructureNode(doc, structures.get(structure)));
            }
            mainRootElement.appendChild(template);

            Element world = doc.createElement("world");
            for (int structure : worldStructures)
            {
                world.appendChild(getStructureNode(doc, structures.get(structure)));
            }
            mainRootElement.appendChild(world);


            Element devices = doc.createElement("devices"); // TODO: this is purely temporary!!
            devices.appendChild(getDeviceNode(doc, temp.getApplianceTemplate()));
            devices.appendChild(getDeviceNode(doc, temp.getEnergySourceTemplate()));
            devices.appendChild(getDeviceNode(doc, temp.getEnergyStorageTemplate()));
            mainRootElement.appendChild(devices);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult outputFile = new StreamResult(file);

            transformer.transform(source, outputFile);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private Node getStructureNode(Document doc, Structure structure)
    {
        Element structureNode = doc.createElement("structure");

        structureNode.appendChild(getElement(doc, "name", structure.getName()));
        structureNode.appendChild(getElement(doc, "id", String.valueOf(structure.getId())));
        structureNode.appendChild(getElement(doc, "type", String.valueOf(structure.getType())));
        structureNode.appendChild(getElement(doc, "numberOfUnits", String.valueOf(structure.getNumberOfUnits())));

        Element propertyMembers = doc.createElement("properties");
        List<Property> propertyList = structure.getProperties();
        for (Property property : propertyList)
        {
            propertyMembers.appendChild(getPropertyNode(doc, property));
        }
        structureNode.appendChild(propertyMembers);

        Element appliances = doc.createElement("appliances");
        List<Device> deviceList = structure.getAppliances();
        for (Device device : deviceList)
        {
            appliances.appendChild(getDeviceNode(doc, device));
        }
        structureNode.appendChild(appliances);

        Element energySources = doc.createElement("energySources");
        deviceList = structure.getEnergySources();
        for (Device device : deviceList)
        {
            energySources.appendChild(getDeviceNode(doc, device));
        }
        structureNode.appendChild(energySources);

        Element energyStorageDevices = doc.createElement("energyStorageDevices");
        deviceList = structure.getEnergyStorageDevices();
        for (Device device : deviceList)
        {
            energyStorageDevices.appendChild(getDeviceNode(doc, device));
        }
        structureNode.appendChild(energyStorageDevices);

        return structureNode;
    }

    private Node getDeviceNode(Document doc, Device device)
    {
        Element deviceNode = doc.createElement("device");

        deviceNode.appendChild(getElement(doc, "name", device.getName()));
        deviceNode.appendChild(getElement(doc, "id", String.valueOf(device.getId())));
        deviceNode.appendChild(getElement(doc, "type", String.valueOf(device.getType())));

        Element propertyMembers = doc.createElement("properties");
        List<Property> properties = device.getProperties();
        for (Property property : properties)
        {
            propertyMembers.appendChild(getPropertyNode(doc, property));
        }
        deviceNode.appendChild(propertyMembers);

        return deviceNode;
    }

    private Element getElement(Document doc, String elementName, String value)
    {
        Element node = doc.createElement(elementName);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private Node getPropertyNode(Document doc, Property property)
    {
        Element propertyNode = doc.createElement("property");

        propertyNode.appendChild(getElement(doc, "name", property.getName()));
        Element value = getElement(doc, "value", String.valueOf(property.getValue()));

        if (property.getValue() instanceof Double)
            value.setAttribute("type", "DOUBLE");
        else if (property.getValue() instanceof Boolean)
            value.setAttribute("type", "BOOLEAN");
        else if (property.getValue() instanceof  Integer)
            value.setAttribute("type", "INTEGER");

        propertyNode.appendChild(value);

        return propertyNode;
    }
    /*public void savePrefabs(File file, Collection<Prefab> prefabs)
    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        Document doc;

        try
        {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.newDocument();

            Element mainRootElement = doc.createElementNS("www.test.com/", "Prefabs"); // TODO: use proper NS
            doc.appendChild(mainRootElement);

            for (Prefab prefab : prefabs)
            {
                mainRootElement.appendChild(getPrefabNode(doc, prefab));
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult outputFile = new StreamResult(file);

            transformer.transform(source, outputFile);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

    }

    private Node getPrefabNode(Document doc, Prefab prefab)
    {
        Element prefabNode = doc.createElement("Prefab");
        Element prefabMembers = doc.createElement("PrefabMembers");

        prefabNode.appendChild(getElement(doc, "Name", prefab.getName()));
        prefabNode.appendChild(getElement(doc, "MemberSuffix", prefab.getMemberSuffix()));
        prefabNode.appendChild(getElement(doc, "ID", String.valueOf(prefab.getId())));
        prefabNode.appendChild(prefabMembers);

        List<IndividualModel> members = prefab.getMembers();
        for (IndividualModel member : members)
        {
            prefabMembers.appendChild(getIndividualNode(doc, member));
        }

        return prefabNode;
    }

    private Node getIndividualNode(Document doc, IndividualModel individual)
    {
        Element individualNode = doc.createElement("Individual");
        Element individualProperties = doc.createElement("Properties");
        //TODO: save the jena individual or get rid of it from IndividualModel?
        individualNode.appendChild(getElement(doc, "Name", individual.getName()));
        String desc = individual.getDescription();
        if (desc == null) desc = ""; // TODO: This check should happen elsewhere
        individualNode.appendChild(getElement(doc, "Description", desc));
        individualNode.appendChild(getElement(doc, "ClassName", individual.getClassName()));
        individualNode.appendChild(getElement(doc, "ID", String.valueOf(individual.getId())));
        individualNode.appendChild(individualProperties);

        List<Property> properties = individual.getProperties();
        for (Property propertyModel : properties)
        {
            individualProperties.appendChild(getPropertyNode(doc, propertyModel));
        }

        return individualNode;
    }*/
    /*
    public Collection<Prefab> readPrefabFile(File file)
    {
        Collection<Prefab> prefabCollection = null;

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);

            NodeList prefabList = doc.getElementsByTagName("Prefab");
            prefabCollection = readPrefabs(prefabList);

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return prefabCollection;
    }

    private Collection<Prefab> readPrefabs(NodeList prefabList)
    {
        List<Prefab> prefabs = new ArrayList<Prefab>();

        int length = prefabList.getLength();
        for (int node = 0; node < length; ++node)
        {
            Node prefabNode = prefabList.item(node);

            if (prefabNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element prefabElement = (Element)prefabNode;

                NodeList nameList = prefabElement.getElementsByTagName("Name");
                Element nameElement = (Element)nameList.item(0);

                NodeList nameText = nameElement.getChildNodes();
                String prefabName = nameText.item(0).getNodeValue().trim();

                NodeList suffixList = prefabElement.getElementsByTagName("MemberSuffix");
                Element suffixElement = (Element)suffixList.item(0);

                NodeList suffixText = suffixElement.getChildNodes();
                String prefabMemberSuffix = suffixText.item(0).getNodeValue().trim();

                NodeList idList = prefabElement.getElementsByTagName("ID");
                Element idElement = (Element)idList.item(0);

                NodeList idText = idElement.getChildNodes();
                Integer prefabId = Integer.parseInt(idText.item(0).getNodeValue().trim());

                NodeList memberList = prefabElement.getElementsByTagName("PrefabMembers");
                Element memberElement = (Element)memberList.item(0);

                NodeList individuals = memberElement.getElementsByTagName("Individual");
                List<IndividualModel> prefabMembers = readPrefabMembers(individuals);

                Prefab newPrefab = new Prefab(prefabId, prefabName, prefabMemberSuffix, prefabMembers);
                prefabs.add(newPrefab);
            }
        }

        return prefabs;
    }


    private List<IndividualModel> readPrefabMembers(NodeList members)
    {
        List<IndividualModel> memberIndividuals = new ArrayList<IndividualModel>();

        int length = members.getLength();
        for (int node = 0; node < length; ++node)
        {
            Node memberNode = members.item(node);

            if (memberNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element memberElement = (Element)memberNode;

                NodeList idList = memberElement.getElementsByTagName("ID");
                Element idElement = (Element)idList.item(0);

                NodeList idText = idElement.getChildNodes();
                Integer memberId = Integer.parseInt(idText.item(0).getNodeValue().trim());

                NodeList nameList = memberElement.getElementsByTagName("Name");
                Element nameElement = (Element)nameList.item(0);

                NodeList nameText = nameElement.getChildNodes();
                String memberName = nameText.item(0).getNodeValue().trim();

                NodeList classList = memberElement.getElementsByTagName("ClassName");
                Element classElement = (Element)classList.item(0);

                NodeList classText = classElement.getChildNodes();
                String memberClassName = classText.item(0).getNodeValue().trim();


                NodeList descriptionList = memberElement.getElementsByTagName("Description");
                Element descriptionElement = (Element)descriptionList.item(0);

                NodeList descriptionText = descriptionElement.getChildNodes();
                String memberDescription = "";

                if (descriptionText.getLength() > 0)
                    memberDescription = descriptionText.item(0).getNodeValue();


                NodeList propertiesList = memberElement.getElementsByTagName("Properties");
                Element propertiesElement = (Element)propertiesList.item(0);

                NodeList propertyList = propertiesElement.getElementsByTagName("Property");
                List<Property> memberProperties = readMemberProperties(propertyList);

                IndividualModel member = new IndividualModel(memberId, memberName, memberClassName, memberDescription, memberProperties);
                memberIndividuals.add(member);
            }
        }

        return memberIndividuals;
    }

    private List<Property> readMemberProperties(NodeList properties)
    {
        List<Property> memberProperties = new ArrayList<Property>();

        int length = properties.getLength();
        for (int property = 0; property < length; ++property)
        {
            Node propertyNode = properties.item(property);

            if (propertyNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element propertyElement = (Element)propertyNode;

                NodeList nameList = propertyElement.getElementsByTagName("Name");
                Element nameElement = (Element)nameList.item(0);

                NodeList nameText = nameElement.getChildNodes();
                String propertyName = nameText.item(0).getNodeValue();

                NodeList valueList = propertyElement.getElementsByTagName("Value");
                Element valueElement = (Element)valueList.item(0);

                NodeList valueText = valueElement.getChildNodes();
                String value = valueText.item(0).getNodeValue().trim();

                if (value.equals("true") || value.equals("false")) // TODO: remove repetition here and in Individual Model
                {
                    Property<Boolean> newProperty = new Property<Boolean>(propertyName, Boolean.getBoolean(value));
                    memberProperties.add(newProperty);
                }
                else
                {
                    Property<Double> newProperty = new Property<Double>(propertyName, Double.parseDouble(value));
                    memberProperties.add(newProperty);
                }
            }
        }

        return memberProperties;
    }

    */
}
