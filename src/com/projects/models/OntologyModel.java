package com.projects.models;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntTools;
import com.hp.hpl.jena.vocabulary.OWL;
import com.projects.management.SystemController;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dan on 5/27/2015.
 */
public class OntologyModel
{
    private static Integer nextAvailableIndividualId = 0;
    private static Integer nextAvailableClassId = 0;
    private static Integer nextAvailablePrefabId = 0;
    private int selectedIndividual;
    private int selectedClass;
    private HashMap<Integer, IndividualModel> individuals;
    private HashMap<Integer, ClassModel> classes;
    private HashMap<Integer, Prefab> prefabs;
    private PropertyChangeSupport changeSupport;

    // Property Changes this class can fire
    //TODO: Handle new ontology being loaded in all systems and views (instance panel... etc)
    public static final String PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED = "PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED";
    public static final String PC_NEW_ONTOLOGY_CLASSES_LOADED = "PC_NEW_ONTOLOGY_CLASSES_LOADED";
    public static final String PC_NEW_INDIVIDUAL_SELECTED = "PC_NEW_INDIVIDUAL_SELECTED";
    public static final String PC_NEW_CLASS_SELECTED = "PC_NEW_CLASS_SELECTED";
    public static final String PC_ONTOLOGY_CLEARED = "PC_ONTOLOGY_CLEARED";
    public static final String PC_NEW_PREFAB_CREATED = "PC_NEW_PREFAB_CREATED";

    private static Integer getNextAvailableIndividualId()
    {
        return nextAvailableIndividualId += 3;
    }

    private static Integer getNextAvailableClassId()
    {
        return nextAvailableClassId += 2;
    }

    private static Integer getNextAvailablePrefabId()
    {
        return nextAvailablePrefabId++;
    }

    public OntologyModel()
    {
        prefabs = new HashMap<Integer, Prefab>();
        individuals = new HashMap<Integer, IndividualModel>();
        classes = new HashMap<Integer, ClassModel>();
        changeSupport = new PropertyChangeSupport(this);
        selectedIndividual = -1;
        selectedClass = -1;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        changeSupport.removePropertyChangeListener(l);
    }

    public void loadOntology(OntModel base)
    {
        Iterator<Individual> list = base.listIndividuals();
        while(list.hasNext())
        {
            Individual individual = list.next();
            IndividualModel individualModel = new IndividualModel(getNextAvailableIndividualId(), individual);
            individuals.put(individualModel.getId(), individualModel);
        }

        List<IndividualModel> individualsList = new ArrayList<IndividualModel>(individuals.values());
        changeSupport.firePropertyChange(PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED, null, individualsList);

        OntClass thing = base.getOntClass( OWL.Thing.getURI() );
        Iterator<OntClass> classList = thing.listSubClasses(true);

        ClassModel root = new ClassModel(getNextAvailableClassId(), thing);
        classes.put(root.getId(), root);

        while(classList.hasNext())
        {
            OntClass ontClass = classList.next();
            ClassModel classModel = new ClassModel(getNextAvailableClassId(), ontClass);
            classes.put(classModel.getId(), classModel);
            root.addChild(classModel);
            addSubClasses(classModel);
        }

        changeSupport.firePropertyChange(PC_NEW_ONTOLOGY_CLASSES_LOADED, null, root);
    }

    // TODO: remove recursion
    private void addSubClasses(ClassModel root)
    {
        Iterator<OntClass> classList = root.getOntClass().listSubClasses(true);
        while(classList.hasNext())
        {
            OntClass ontClass = classList.next();
            ClassModel classModel = new ClassModel(getNextAvailableClassId(), ontClass);
            classes.put(classModel.getId(), classModel);
            root.addChild(classModel);
            addSubClasses(classModel);
        }
    }

    public void createNewPrefab(String name, List<Integer> selectedIndividuals)
    {
        if (selectedIndividuals.isEmpty())
            return;
        
        List<IndividualModel> prefabsMembers = new ArrayList<IndividualModel>();
        for (int index : selectedIndividuals)
        {
            prefabsMembers.add(individuals.get(index));
        }

        Prefab prefab = new Prefab(getNextAvailablePrefabId(), name, prefabsMembers);
        prefabs.put(prefab.getId(), prefab);

        changeSupport.firePropertyChange(PC_NEW_PREFAB_CREATED, null, prefab);
    }

    public void changeSelectedIndividual(int id)
    {
        if (id < 0) //TODO: throw exception?
            return;

        IndividualModel lastSelected = null;

        if (selectedIndividual >= 0)
            lastSelected =  individuals.get(selectedIndividual);

        selectedIndividual = id;

        changeSupport.firePropertyChange(PC_NEW_INDIVIDUAL_SELECTED, lastSelected, individuals.get(selectedIndividual));
    }

    public void changeSelectedClass(int id)
    {
        ClassModel lastSelected = null;

        if (selectedClass >= 0)
            lastSelected = classes.get(selectedClass);

        selectedClass = id;

        changeSupport.firePropertyChange(PC_NEW_CLASS_SELECTED, lastSelected, classes.get(selectedClass));
    }

    public void clearOntology()
    {
        selectedIndividual = -1;
        individuals.clear();
        classes.clear();
        changeSupport.firePropertyChange(PC_ONTOLOGY_CLEARED, null, null);
    }

    public int getSelectedIndividual() { return selectedIndividual; }

    public IndividualModel getIndividual(int id)
    {
        if (id < 0)
            return null;

        return individuals.get(id);
    }

    public void changePropertyValueOfSelected(int index, Object newValue)
    {
        individuals.get(selectedIndividual).changeProperty(index, newValue);
    }
}
