package com.projects.management;

import com.hp.hpl.jena.ontology.OntModel;
import com.projects.gui.SubscribedView;
import com.projects.models.IndividualModel;
import com.projects.models.OntologyModel;
import com.projects.models.WorldModel;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 5/28/2015.
 */
public class SystemController implements PropertyChangeListener
{
    enum selection_type
    {
        CLASS,
        INDIVIDUAL,
        INSTANCE,
        NONE
    }

    private ArrayList<SubscribedView> views;
    private TaskManager taskManager;
    private OntologyModel ontologyModel;
    private WorldModel worldModel;
    private FileManager fileManager;
    private selection_type currentlySelected;
    private Task testTask;

    public SystemController()
    {
        views = new ArrayList<SubscribedView>();
        ontologyModel = new OntologyModel();
        worldModel = new WorldModel();
        fileManager = new FileManager();
        taskManager = new TaskManager();

        ontologyModel.addPropertyChangeListener(this);
        worldModel.addPropertyChangeListener(this);
        currentlySelected = selection_type.NONE;

        testTask = new Task(0, "File Loading", "Waiting");
    }

    public void subscribeView(SubscribedView view)
    {
        views.add(view);
    }

    public void loadOntology(File file)
    {
        ontologyModel.clearOntology();
        worldModel.clearWorld();
        OntologyLoadingWorker ontologyLoadingWorker = new OntologyLoadingWorker(file, fileManager, ontologyModel);
        ontologyLoadingWorker.assignTask(testTask);
        taskManager.submitWorker(ontologyLoadingWorker);
    }

    public void closeOntology()
    {
        ontologyModel.clearOntology();
        worldModel.clearWorld();
    }

    public void quitApplication()
    {
        taskManager.kill();
        System.exit(0); // TODO: look into shutdown hooks and whether or not I'll need them.
    }

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

    public void newIndividualSelected(int index)
    {
        currentlySelected = selection_type.INDIVIDUAL;
        ontologyModel.changeSelectedIndividual(index);
    }

    public void newInstanceSelected(int index)
    {
        currentlySelected = selection_type.INSTANCE;
        worldModel.changeSelectedInstance(index);
    }

    public void newClassSelected(int index)
    {
        currentlySelected = selection_type.CLASS;
        ontologyModel.changeSelectedClass(index);
    }

    public void createInstanceFromIndividual()
    {
        IndividualModel individual = ontologyModel.getIndividual(ontologyModel.getSelectedIndividual());
        worldModel.addNewInstance(individual);
    }

    public void createPrefabFromSelection(int[] selection)
    {
        List<Integer> selectionList = new ArrayList<Integer>(selection.length);
        for (int i : selection)
        {
            selectionList.add(i);
        }

        // TODO: implement something more expressive to allow restriction/altering
        String inputName = JOptionPane.showInputDialog("Prefab's Name: ", JOptionPane.QUESTION_MESSAGE);

        ontologyModel.createNewPrefab(inputName, selectionList);
    }

    public void deleteSelectedInstance()
    {
        worldModel.deleteInstance(worldModel.getSelectedInstance());
    }

    // TODO: implement those that need to be...
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

    public void propertyChange(PropertyChangeEvent event)
    {
        for (SubscribedView view : views)
        {
            view.modelPropertyChange(event);
        }
    }
}
