package com.projects.management;

import com.hp.hpl.jena.ontology.OntModel;
import com.projects.gui.SubscribedView;
import com.projects.models.*;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The SystemController works as a layer between the GUI and the applications data (MVC-like). The handling of user input and communication between the two happens here.
 * Created by Dan on 5/28/2015.
 */
public class SystemController implements PropertyChangeListener
{

    private ArrayList<SubscribedView> views;
    private TaskManager taskManager;
    private OntologyModel ontologyModel;
    private WorldModel worldModel;
    private FileManager fileManager;
    private SelectionType currentlySelected;
    private Task testTask;

    /**
     * the constructor
     */
    public SystemController()
    {
        views = new ArrayList<SubscribedView>();
        ontologyModel = new OntologyModel();
        worldModel = new WorldModel();
        fileManager = new FileManager();
        taskManager = new TaskManager();

        ontologyModel.addPropertyChangeListener(this);
        worldModel.addPropertyChangeListener(this);
        currentlySelected = SelectionType.NONE;

        testTask = new Task(0, "File Loading", "Waiting");
    }

    /**
     * Adds a SubscribedView which is interested in having the SystemController handle communications between it and the models/systems.
     * @param view the view interested in getting events from the SystemController
     */
    public void subscribeView(SubscribedView view)
    {
        views.add(view);
    }

    /**
     * Clears any currently loaded data and/or running systems before spawning a new worker to load in a specified ontology file.
     *
     * @param file an ontology file specified by the user which will be loaded into the application
     */
    public void loadOntology(File file)
    {
        ontologyModel.clearOntology();
        worldModel.clearWorld();
        OntologyLoadingWorker ontologyLoadingWorker = new OntologyLoadingWorker(file, fileManager, ontologyModel);
        ontologyLoadingWorker.assignTask(testTask);
        taskManager.submitWorker(ontologyLoadingWorker);
    }

    /**
     * Clears any data loaded in from the current ontology file.
     */
    public void closeOntology()
    {
        ontologyModel.clearOntology();
        worldModel.clearWorld();
    }

    /**
     * Called when the application should close.
     */
    public void quitApplication()
    {
        taskManager.kill();
        System.exit(0); // TODO: look into shutdown hooks and whether or not I'll need them.
    }

    // TODO: should this be moved somewhere else?
    private class OntologyLoadingWorker extends SwingWorker<Void, String>
    {
        FileManager fileManager;
        OntologyModel ontologyModel;
        File ontFile;
        OntModel result;
        Task task;

        public OntologyLoadingWorker(File file, FileManager fileManager, OntologyModel ontologyModel)
        {
            publish("Loading File: " + file.getName() + "...");
            this.fileManager = fileManager;
            this.ontologyModel = ontologyModel;
            ontFile = file;
            publish("File Loaded.");
        }

        public void assignTask(Task assignedTask)
        {
            task = assignedTask;
        }

        protected void process(List<String> update)
        {
            if (task != null)
                task.setDescription(update.get(update.size()-1));
        }

        protected Void doInBackground()
        {
            result = fileManager.readOntology(ontFile);

            return null;
        }

        protected void done()
        {
            ontologyModel.loadOntology(result);
        }
    }

    /**
     * Changes the currently selected Model to an Individual from the OntologyModel
     * @param id the unique id of the newly selected Individual
     */
    public void newIndividualSelected(int id)
    {
        newIndividualSelected(id, false);
    }


    public void newIndividualSelected(int id, Boolean inPrefab)
    {
        currentlySelected = SelectionType.INDIVIDUAL;
        ontologyModel.changeSelectedIndividual(id, inPrefab);
    }

    /**
     * Changes the currently selected Model to an instance of an Individual in the WorldModel
     * @param id the unique id of the newly selected instance
     */
    public void newInstanceSelected(int id)
    {
        currentlySelected = SelectionType.INSTANCE;
        worldModel.changeSelectedInstance(id);
    }

    /**
     * Changes the currently selected Model to a Class from the OntologyModel
     * @param index the unique id of the newly selected Class
     */
    public void newClassSelected(int index)
    {
        currentlySelected = SelectionType.CLASS;
        ontologyModel.changeSelectedClass(index);
    }

    /**
     * Creates a new instance from the selected Individual which is defined in the OntologyModel. This new instance Individual will be added to the WorldModel.
     */
    public void createInstanceFromIndividual()
    {
        IndividualModel individual = ontologyModel.getIndividual(ontologyModel.getSelectedIndividual());
        worldModel.addNewInstance(individual, false);
    }

    /**
     * Creates a new instance Prefab(collection of Individuals) from the selected Prefab which is defined in the OntologyModel. This new instance Prefab will be added to the WorldModel.
     * @param prefab the Prefab which has been selected to be the template for the new Prefab
     */
    public void createPrefabInstancesFromPrefab(Prefab prefab)
    {
        worldModel.addNewPrefab(prefab);
    }

    /**
     * Creates a new Prefab(collection of Individuals) from a selected group of Individuals that are defined in the OntologyModel. This new Prefab will be added to the OntologyModel.
     * @param selection the selected group of Individuals which will makeup the new Prefab
     */
    public void createPrefabFromSelection(Integer[] selection)
    {
        List<Integer> selectionList = Arrays.asList(selection);

        // TODO: implement something more expressive to allow restriction/altering
        String inputName = JOptionPane.showInputDialog("Prefab's Name: ", JOptionPane.QUESTION_MESSAGE);

        if (inputName == null || inputName.equals("")) // Dialog box cancelled or no input given
            return;

        ontologyModel.createNewPrefab(inputName, selectionList);
    }

    /**
     * Removes the currently selected instance Individual from the WorldModel.
     */
    public void deleteSelectedInstance()
    {
        worldModel.deleteInstance(worldModel.getSelectedInstance());
    }

    // TODO: implement those that need to be...

    /**
     * Changes a selected Models property
     * @param index this index specifies which property belonging to the Model needs to change.
     * @param newValue this Object is the new value for the property that's changing
     */
    public void selectionPropertyChanged(int index, Object newValue)
    {
        switch (currentlySelected)
        {
            case CLASS:
            {
                // Not implemented
            } break;
            case INDIVIDUAL:
            {
                ontologyModel.changePropertyValueOfSelected(index, newValue);
            } break;
            case INSTANCE:
            {
                worldModel.changePropertyValueOfSelected(index, newValue);
            } break;
        }
    }

    /**
     * Systems that the SystemController has registered with will send updates that back to it which the GUI may need to handle.
     * @param event the update that may need to be handled by a GUI component that has subscribed to the SystemController
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        for (SubscribedView view : views)
        {
            view.modelPropertyChange(event);
        }
    }
}
