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
        structureNode.appendChild(getElement(doc, "image", String.valueOf(structure.getImage())));
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
}
