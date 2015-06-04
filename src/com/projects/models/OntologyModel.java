package com.projects.models;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

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
    private int selectedIndividual;
    private int selectedClass;
    private ArrayList<IndividualModel> individuals;
    //private HashMap<String, IndividualModel> individuals;
    private ArrayList<ClassModel> classes;
    private List<Prefab> prefabs;
    private PropertyChangeSupport changeSupport;

    // Property Changes this class can fire
    //TODO: Handle new ontology being loaded in all systems and views (instance panel... etc)
    public static final String PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED = "PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED";
    public static final String PC_NEW_ONTOLOGY_CLASSES_LOADED = "PC_NEW_ONTOLOGY_CLASSES_LOADED";
    public static final String PC_NEW_INDIVIDUAL_SELECTED = "PC_NEW_INDIVIDUAL_SELECTED";
    public static final String PC_NEW_CLASS_SELECTED = "PC_NEW_CLASS_SELECTED";
    public static final String PC_ONTOLOGY_CLEARED = "PC_ONTOLOGY_CLEARED";
    public static final String PC_NEW_PREFAB_CREATED = "PC_NEW_PREFAB_CREATED";

    public OntologyModel()
    {
        prefabs = new ArrayList<Prefab>();
        individuals = new ArrayList<IndividualModel>();
        classes = new ArrayList<ClassModel>();
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
        ArrayList<IndividualModel> oldInstances = new ArrayList<IndividualModel>(individuals);
        ArrayList<ClassModel> oldClasses = new ArrayList<ClassModel>(classes);

        Iterator<Individual> list = base.listIndividuals();
        while(list.hasNext())
        {
            Individual individual = list.next();
            individuals.add(new IndividualModel(individual));
        }

        changeSupport.firePropertyChange(PC_NEW_ONTOLOGY_INDIVIDUALS_LOADED, oldInstances, individuals);

        Iterator<OntClass> classList = base.listClasses();
        while(classList.hasNext())
        {
            OntClass ontClass = classList.next();
            classes.add(new ClassModel(ontClass));
        }

        changeSupport.firePropertyChange(PC_NEW_ONTOLOGY_CLASSES_LOADED, oldClasses, classes);
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

        Prefab prefab = new Prefab(name, prefabsMembers);
        prefabs.add(prefab);

        changeSupport.firePropertyChange(PC_NEW_PREFAB_CREATED, null, prefab);
    }

    public void changeSelectedIndividual(int index)
    {
        if (index < 0) //TODO: throw exception?
            return;

        IndividualModel lastSelected = null;

        if (selectedIndividual >= 0)
            lastSelected =  individuals.get(selectedIndividual);

        selectedIndividual = index;

        changeSupport.firePropertyChange(PC_NEW_INDIVIDUAL_SELECTED, lastSelected, individuals.get(selectedIndividual));
    }

    public void changeSelectedClass(int index)
    {
        ClassModel lastSelected = null;

        if (selectedClass >= 0)
            lastSelected = classes.get(selectedClass);

        selectedClass = index;

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

    public IndividualModel getIndividual(int index)
    {
        if (index < 0)
            return null;

        return individuals.get(index);
    }

    public void changePropertyValueOfSelected(int index, Object newValue)
    {
        individuals.get(selectedIndividual).changeProperty(index, newValue);
    }
}
