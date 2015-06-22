package com.projects.management;

import com.hp.hpl.jena.ontology.OntModel;
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
import java.util.Arrays;
import java.util.Collection;
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
    private OntologyModel ontologyModel;
    private WorldModel worldModel;
    private FileManager fileManager;
    private TemplateManager templateManager;
    private SelectionType currentlySelected;
    private Task testTask;
    private StructureCreationControl structureCreationControl;
    private JFrame frame;

    /**
     * the constructor
     */
    public SystemController(JFrame f)
    {
        systems = new ArrayList<System>();
        views = new ArrayList<SubscribedView>();
        ontologyModel = new OntologyModel();
        worldModel = new WorldModel();
        fileManager = new FileManager();
        taskManager = new TaskManager();

        Path currentRelativePath = Paths.get("");
        String workingDir = currentRelativePath.toAbsolutePath().toString();
        File templateFile = new File(workingDir + Constants.TEMPLATE_FILE_PATH);
        Template template = fileManager.readTemplate(templateFile);

        templateManager = new TemplateManager(template);

        templateManager.addPropertyChangeListener(this);
        ontologyModel.addPropertyChangeListener(this);
        worldModel.addPropertyChangeListener(this);
        systems.add(templateManager); //TODO: add the rest after refactoring them

        currentlySelected = SelectionType.NO_SELECTION;
        frame = f;
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
        ontologyModel.clearOntology();
        worldModel.clearWorld();
        OntologyLoadingWorker ontologyLoadingWorker = new OntologyLoadingWorker(file, fileManager, ontologyModel);
        ontologyLoadingWorker.assignTask(testTask);
        taskManager.submitWorker(ontologyLoadingWorker);
    }

    public void loadPrefabs(File file)
    {
        Collection<Prefab> prefabs = fileManager.readPrefabFile(file);
        ontologyModel.loadPrefabs(prefabs);
    }

    public void savePrefabs(File file)
    {
        fileManager.savePrefabs(file, ontologyModel.getPrefabCollection());
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
        java.lang.System.exit(0); // TODO: look into shutdown hooks and whether or not I'll need them.
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
    }

    public void createStructure(Structure structure)
    {
        templateManager.createNewStructure(structure);
    }

    public void addStructureToWorld()
    {
        Structure structure = templateManager.getStructureBeingCreated();
        worldModel.addNewStructure(structure);
    }

    public void addDeviceToStructure(DeviceType deviceType)
    {
        templateManager.addDevice(deviceType);
    }

    public void selectStructureTemplate(Structure structure)
    {
        templateManager.structureTemplateSelected(structure);
    }

    public void selectDevice(int id)
    {
        templateManager.deviceSelected(id);
    }

    public void editDeviceProperty(int index, Object value)
    {
        templateManager.editDeviceProperty(index, value);
    }

    /**
     * Changes the currently selected Model to an Individual from the OntologyModel
     * @param id the unique id of the newly selected Individual
     * @param inPrefab yes if this individual is part of a prefab
     */
    public void ontologyIndividualSelected(int id, Boolean inPrefab)
    {
        if (inPrefab)
            currentlySelected = SelectionType.ONTOLOGY_PREFAB_MEMBER;
        else
            currentlySelected = SelectionType.ONTOLOGY_INDIVIDUAL;

        ontologyModel.changeSelectedIndividual(id, inPrefab);
    }

    public void ontologyPrefabSelected(int id)
    {
        currentlySelected = SelectionType.ONTOLOGY_PREFAB;
        ontologyModel.changeSelectedPrefab(id);
    }

    /**
     * Changes the currently selected Model to an instance of an Individual in the WorldModel
     * @param id the unique id of the newly selected instance
     */
    public void worldIndividualSelected(int id)
    {
        currentlySelected = SelectionType.WORLD_INDIVIDUAL;
        worldModel.changeSelectedIndividual(id);
    }

    public void worldPrefabSelected(int id)
    {
        currentlySelected = SelectionType.WORLD_PREFAB;
        worldModel.changeSelectedPrefab(id);
    }

    /**
     * Changes the currently selected Model to a Class from the OntologyModel
     * @param index the unique id of the newly selected Class
     */
    public void classSelected(int index)
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
        Integer count = worldModel.getIndividualCount(individual.getName());
        //worldModel.addNewInstance(individual, count, false);
    }

    /**
     * Creates a new instance Prefab(collection of Individuals) from the selected Prefab which is defined in the OntologyModel. This new instance Prefab will be added to the WorldModel.
     * @param prefab the Prefab which has been selected to be the template for the new Prefab
     */
    public void createPrefabInstancesFromPrefab(Prefab prefab)
    {
        //Integer count = worldModel.getPrefabCount(prefab.getName());
        //worldModel.addNewPrefab(prefab, count);
    }

    /**
     * Creates a new Prefab(collection of Individuals) from a selected group of Individuals that are defined in the OntologyModel. This new Prefab will be added to the OntologyModel.
     * @param selection the selected group of Individuals which will makeup the new Prefab
     */
    public void createPrefabFromSelection(Integer[] selection)
    {
        /*List<Integer> selectionList = Arrays.asList(selection);

        String inputName, inputSuffix;
        structureCreationControl = new StructureCreationControl(frame, ontologyModel.getPrefabCollection());

        int result = structureCreationControl.getResult();

        if (result == StructureCreationControl.OK)
        {
            inputName = structureCreationControl.getName();
            inputSuffix = structureCreationControl.getSuffix();
        }
        else
        {
            return;
        }

        ontologyModel.createNewPrefab(inputName, inputSuffix, selectionList);*/
    }

    public void removeModel(Object model)
    {
        // Note that Ontology Individuals should never be removed.
        switch (currentlySelected)
        {
            case WORLD_INDIVIDUAL:
            case WORLD_PREFAB:
            case WORLD_PREFAB_MEMBER:
            {
                if (model instanceof Prefab)
                {
                    worldModel.removePrefab((Prefab)model);
                }
                else if (model instanceof IndividualModel)
                {
                    worldModel.removeIndividual((IndividualModel)model);
                }
            } break;
            case ONTOLOGY_PREFAB:
            case ONTOLOGY_PREFAB_MEMBER:
            {
                if (model instanceof Prefab)
                {
                    ontologyModel.removePrefab((Prefab)model);
                }
                else if (model instanceof IndividualModel)
                {
                    ontologyModel.removeIndividual((IndividualModel)model);
                }
            } break;
        }
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
            case ONTOLOGY_PREFAB_MEMBER:
            case ONTOLOGY_INDIVIDUAL:
            {
                ontologyModel.changePropertyValueOfSelected(index, newValue);
            } break;
            case WORLD_PREFAB_MEMBER:
            case WORLD_INDIVIDUAL:
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

    public SelectionType getCurrentlySelected()
    {
        return currentlySelected;
    }
}
