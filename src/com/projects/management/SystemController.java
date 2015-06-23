package com.projects.management;

import com.projects.gui.StructureCreationControl;
import com.projects.gui.SubscribedView;
import com.projects.helper.Constants;
import com.projects.helper.DeviceType;
import com.projects.helper.SelectionType;
import com.projects.models.*;
import com.projects.models.System;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The SystemController works as a layer between the GUI and the applications data (MVC-like). The handling of user input and communication between the two happens here.
 * Created by Dan on 5/28/2015.
 */
public class SystemController implements PropertyChangeListener
{

    private ArrayList<SubscribedView> views;
    private List<System> systems;
    private TaskManager taskManager;
    private WorldModel worldModel;
    private FileManager fileManager;
    private TemplateManager templateManager;
    private SelectionType activeSelection;
    private Task testTask;

    /**
     * the constructor
     */
    public SystemController()
    {
        systems = new ArrayList<System>();
        views = new ArrayList<SubscribedView>();
        worldModel = new WorldModel();
        fileManager = new FileManager();
        taskManager = new TaskManager();

        Path currentRelativePath = Paths.get("");
        String workingDir = currentRelativePath.toAbsolutePath().toString();
        File templateFile = new File(workingDir + Constants.TEMPLATE_FILE_PATH);
        Template template = fileManager.readTemplate(templateFile);

        templateManager = new TemplateManager(template);

        templateManager.addPropertyChangeListener(this);
        worldModel.addPropertyChangeListener(this);
        systems.add(templateManager); //TODO: add the rest after refactoring them

        activeSelection= SelectionType.NO_SELECTION;
        testTask = new Task(0, "File Loading", "Waiting");
        testTask.addPropertyChangeListener(this);
    }

    public void setupComplete()
    {
        for (System system : systems)
        {
            system.postSetupSync();
        }
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
        /*ontologyModel.clearOntology();
        worldModel.clearWorld();
        OntologyLoadingWorker ontologyLoadingWorker = new OntologyLoadingWorker(file, fileManager, ontologyModel);
        ontologyLoadingWorker.assignTask(testTask);
        taskManager.submitWorker(ontologyLoadingWorker);*/
    }

    public void loadPrefabs(File file)
    {
        //Collection<Prefab> prefabs = fileManager.readPrefabFile(file);
        //ontologyModel.loadPrefabs(prefabs);
    }

    public void savePrefabs(File file)
    {
        //fileManager.savePrefabs(file, ontologyModel.getPrefabCollection());
    }

    /**
     * Clears any data loaded in from the current ontology file.
     */
    public void closeOntology()
    {
        // TODO: clear template manager
        worldModel.clearWorld();
    }

    /**
     * Called when the application should close.
     */
    public void quitApplication()
    {
        taskManager.kill();
        java.lang.System.exit(0); // TODO: look into shutdown hooks and whether or not I'll need them.
    }

    // TODO: should this be moved somewhere else?
    /*private class OntologyLoadingWorker extends SwingWorker<Void, String>
    {
        FileManager fileManager;
        OntologyModel ontologyModel;
        File ontFile;
        OntModel result;
        Task task;

        public OntologyLoadingWorker(File file, FileManager fileManager, OntologyModel ontologyModel)
        {
            this.fileManager = fileManager;
            this.ontologyModel = ontologyModel;
            ontFile = file;
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
            publish("Loading File: " + ontFile.getName());
            result = fileManager.readOntology(ontFile);
            publish("File Loaded");

            return null;
        }

        protected void done()
        {
            ontologyModel.loadOntology(result);
        }
    }*/

    public void editStructuresName(String name)
    {
        templateManager.editName(name);
    }

    public void editStructuresNumberOfUnits(Integer num)
    {
        templateManager.editNumberOfUnits(num);
    }

    public void editingComplete()
    {
        Structure structure = templateManager.getCopyOfStructureBeingEdited();

        if (activeSelection == SelectionType.WORLD)
        {
            worldModel.setStructure(structure);
        }
        else if (activeSelection == SelectionType.TEMPLATE)
        {
            templateManager.setStructure(structure);
        }
    }

    public void addStructureToWorld(Integer id)
    {
        Structure structure = templateManager.createStructureFromTemplate(id);
        worldModel.addNewStructure(structure);
    }

    public void addDeviceToStructure(DeviceType deviceType)
    {
        templateManager.addDevice(deviceType);
    }

    public void selectTemplateStructure(Structure structure)
    {
        templateManager.selectTemplateStructure(structure);
        activeSelection = SelectionType.TEMPLATE;
    }

    public void removeWorldStructure(Integer id)
    {
        worldModel.removeStructure(id);
    }

    public void selectWorldStructure(Structure structure)
    {
        worldModel.selectStructure(structure);
        activeSelection = SelectionType.WORLD;
    }

    public void editActiveSelection()
    {
        Structure structure = null;

        if (activeSelection == SelectionType.TEMPLATE)
        {
            structure = templateManager.getLastSelected();
        }
        else if (activeSelection == SelectionType.WORLD)
        {
            structure = worldModel.getLastSelected();
        }

        if (structure == null)
        {
            return; // TODO: display message?
        }

        templateManager.editStructure(structure);
    }

    public void selectDevice(int id)
    {
        templateManager.deviceSelected(id);
    }

    public void editDeviceProperty(int index, Object value)
    {
        templateManager.editDeviceProperty(index, value);
    }

    public void editObjectProperty(int index, Object value)
    {
        templateManager.editObjectProperty(index, value);
    }

    public void removeModel(Object model)
    {

    }

    // TODO: implement those that need to be...
    /**
     * Changes a selected Models property
     * @param index this index specifies which property belonging to the Model needs to change.
     * @param newValue this Object is the new value for the property that's changing
     */
    public void selectionPropertyChanged(int index, Object newValue)
    {
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

    public SelectionType getCurrentlySelected()
    {
        return activeSelection;
    }
}
