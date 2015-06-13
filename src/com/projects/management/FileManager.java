package com.projects.management;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.projects.models.IndividualModel;
import com.projects.models.Prefab;
import com.projects.models.PropertyModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * Created by Dan on 5/27/2015.
 */
class FileManager
{

    public FileManager()
    {
    }

    public OntModel readOntology(File file)
    {
        OntModel model = null;

        try
        {
            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            InputStream in = new FileInputStream(file);
            model.read(in, null);
            in.close();
        }
        catch (Exception exception)
        {
            //TODO: Handle this exception
        }

        return model;
    }

    public void readPrefabFile(File file)
    {

    }

    private static Node getCompany(Document doc, String id, String name, String age, String role) {
        Element company = doc.createElement("Company");
        company.setAttribute("id", id);
        company.appendChild(getCompanyElements(doc, company, "Name", name));
        company.appendChild(getCompanyElements(doc, company, "Type", age));
        company.appendChild(getCompanyElements(doc, company, "Employees", role));
        return company;
    }

    // utility method to create text node
    private static Node getCompanyElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    public void savePrefabs(File file, Collection<Prefab> prefabs)
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

        List<PropertyModel> properties = individual.getProperties();
        for (PropertyModel propertyModel : properties)
        {
            individualProperties.appendChild(getPropertyNode(doc, propertyModel));
        }

        return individualNode;
    }

    private Node getPropertyNode(Document doc, PropertyModel property)
    {
        Element propertyNode = doc.createElement("Properties");

        propertyNode.appendChild(getElement(doc, "Name", property.getName()));
        propertyNode.appendChild(getElement(doc, "Value", String.valueOf(property.getValue()))); // TODO: will I have to store the type of value as an attribute?

        return propertyNode;
    }

    private Node getElement(Document doc, String elementName, String value)
    {
        Element node = doc.createElement(elementName);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}
