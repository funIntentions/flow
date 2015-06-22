package com.projects.gui;

import com.projects.helper.Constants;
import com.projects.helper.SelectionType;
import com.projects.management.SystemController;
import com.projects.actions.*;
import com.projects.models.OntologyModel;
import com.projects.models.Structure;
import com.projects.models.TemplateManager;
import com.projects.models.WorldModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * The entry point for the application that structures the GUI and initializes underlying systems.
 * Created by Dan on 5/26/2015.
 */
public class ProjectSmartGrid extends JPanel implements SubscribedView //TODO: Maybe create JPanel instance instead of extending it
{
    private Action quitApplicationAction,
            loadOntologyAction,
            closeOntologyAction,
            addIndividualAction,
            removeStructureAction,
            removeSelectedIndividualAction,
            createPrefabAction,
            editStructureAction,
            loadPrefabsAction,
            savePrefabsAction;
    private TemplateStructureSelectedListener templateStructureSelectedListener;
    private WorldStructureSelectedListener worldStructureSelectedListener;
    private PropertiesTableListener propertiesTableListener;
    private ClassSelectedListener classSelectedListener;
    private PrefabSelectedListener prefabSelectedListener;
    private SelectionPropertyPanel selectionInfoPanel;
    private WorldStructuresPanel worldStructuresPanel;
    private SystemController controller;
    private TemplateStructuresPanel templateStructuresPanel;
    private ClassPanel classPanel;
    private PrefabPanel prefabPanel;
    private ObjectPropertyPanel objectPropertyPanel;
    private DataPropertyPanel dataPropertyPanel;
    private StatusPanel statusBar;

    private JSplitPane rightSplitPane;
    private JSplitPane centerSplitPane;

    JTabbedPane ontologyPane;
    JTabbedPane worldPane;

    private JToolBar toolBar;
    private JButton addIndividualButton, removeStructureButton, removeIndividualButton, createPrefabButton, editStructureButton; // ToolBar Buttons
    private JMenuItem addIndividualItem, removeStructureItem, removeIndividualItem, createStructureItem, editStructureItem; // MenuItems

    StructureCreationControl structureCreationControl;

    private ProjectSmartGrid(JFrame frame)
    {
        controller = new SystemController(frame);
        quitApplicationAction = new QuitApplicationAction("Quit", null, null, null, controller);
        closeOntologyAction = new CloseOntologyAction("Close", null, null, null, controller);
        loadOntologyAction = new OpenFileAction("Open Ontology", null, null, null, this, controller, Constants.OWL);
        loadPrefabsAction = new OpenFileAction("Open Prefabs", null, null, null, this, controller, Constants.PREFABS);
        savePrefabsAction = new SaveFileAction("Save Prefabs", null, null, null, this, controller, Constants.PREFABS);
        addIndividualAction = new AddIndividualAction("Add Individual", null, null, null, controller);
        templateStructureSelectedListener = new TemplateStructureSelectedListener(controller);
        worldStructureSelectedListener = new WorldStructureSelectedListener(controller);
        propertiesTableListener = new PropertiesTableListener(controller);
        classSelectedListener = new ClassSelectedListener(controller);
        prefabSelectedListener = new PrefabSelectedListener(controller);
        selectionInfoPanel = new SelectionPropertyPanel("Selection", propertiesTableListener);
        worldStructuresPanel = new WorldStructuresPanel(worldStructureSelectedListener);
        templateStructuresPanel = new TemplateStructuresPanel(templateStructureSelectedListener);
        classPanel = new ClassPanel(classSelectedListener);
        prefabPanel = new PrefabPanel(prefabSelectedListener);
        objectPropertyPanel = new ObjectPropertyPanel(new NodeSelectedListener());
        dataPropertyPanel = new DataPropertyPanel(new NodeSelectedListener());
        statusBar = new StatusPanel("Application Started");

        structureCreationControl = new StructureCreationControl(frame, controller);

        //removeStructureAction = new RemoveSelectedAction("Remove Instance", null, null, null,worldStructuresPanel.getWorldInstanceTree(), controller);
        removeSelectedIndividualAction = new RemoveSelectedAction("Remove Individual", null, null, null, prefabPanel.getPrefabTree(), controller);
        createPrefabAction = new CreateStructureAction("Create Prefab", null, null, null, templateStructuresPanel.getStructureTable(), templateStructuresPanel.getTemplateTable(), controller); // TODO: refactor so I don't have to get the table
        editStructureAction = new EditStructureAction("Edit Structure", null, null, null, worldStructuresPanel.getStructureTable(), worldStructuresPanel.getTemplateTable(), controller);

        controller.subscribeView(structureCreationControl);
        controller.subscribeView(dataPropertyPanel);
        controller.subscribeView(objectPropertyPanel);
        controller.subscribeView(prefabPanel);
        controller.subscribeView(classPanel);
        controller.subscribeView(templateStructuresPanel);
        controller.subscribeView(selectionInfoPanel);
        controller.subscribeView(worldStructuresPanel);
        controller.subscribeView(statusBar);
        controller.subscribeView(this);
        controller.setupComplete();
        setupPane();
    }

    private void setupPane()
    {
        setLayout(new BorderLayout());

        setBackground(Color.RED);

        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createRightTopPanel(), createRightBottomPanel());
        rightSplitPane.setResizeWeight(0.5);
        rightSplitPane.setOneTouchExpandable(true);
        rightSplitPane.setContinuousLayout(true);

        centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), rightSplitPane);
        centerSplitPane.setResizeWeight(0.5);
        centerSplitPane.setOneTouchExpandable(true);
        centerSplitPane.setContinuousLayout(true);

        add(centerSplitPane, BorderLayout.CENTER);
        add(createToolBar(), BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);
    }

    private static void createAndShowGUI()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            System.out.println("Error setting native look and feel: " + e);
        }

        JFrame mainFrame = new JFrame("Project SmartGrid");
        mainFrame.setPreferredSize(new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ProjectSmartGrid project = new ProjectSmartGrid(mainFrame);
        mainFrame.setJMenuBar(project.createMenuBar());
        mainFrame.setContentPane(project);
        mainFrame.pack();
        mainFrame.setVisible(true);
        project.removeToolBarAndMenuOptions();
    }

    private JToolBar createToolBar()
    {
        toolBar = new JToolBar("Available Options");
        addIndividualButton = new JButton(addIndividualAction);
        removeStructureButton = new JButton(removeStructureAction);
        removeIndividualButton = new JButton(removeSelectedIndividualAction);
        createPrefabButton = new JButton(createPrefabAction);
        editStructureButton = new JButton(editStructureAction);
        toolBar.add(addIndividualButton);
        toolBar.add(removeStructureButton);
        toolBar.add(removeIndividualButton);
        toolBar.add(createPrefabButton);
        toolBar.add(editStructureButton);
        return toolBar;
    }

    private JSplitPane createLeftPanel()
    {

        ontologyPane = new JTabbedPane();
        worldPane = new JTabbedPane();

        JSplitPane leftPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ontologyPane, worldPane);
        leftPanel.setResizeWeight(0.5);
        leftPanel.setOneTouchExpandable(true);
        leftPanel.setContinuousLayout(true);
        return leftPanel;
    }

    private JPanel createRightTopPanel()
    {
        return new GraphicsPanel();
    }

    private JSplitPane createRightBottomPanel()
    {
        JSplitPane rightBottomPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectionInfoPanel, createSimulationInfoPanel());
        rightBottomPanel.setBackground(Color.ORANGE);
        rightBottomPanel.setContinuousLayout(true);
        rightBottomPanel.setResizeWeight(0.5);
        rightBottomPanel.setOneTouchExpandable(true);

        return rightBottomPanel;
    }

    JPanel createSimulationInfoPanel()
    {
        JPanel simInfoPanel = new JPanel();
        simInfoPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints;

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 4, 4, 4);
        simInfoPanel.add(new JLabel("Time: "), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 4, 4, 4);
        simInfoPanel.add(new JLabel("Cloud Cover: "), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 5;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 4, 4, 4);
        simInfoPanel.add(new JLabel("Wind Level: "), constraints);

        simInfoPanel.setBorder(BorderFactory.createTitledBorder("Simulation"));
        return simInfoPanel;
    }

    JMenuBar createMenuBar()
    {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menuBar.add(menu);

        menuItem = new JMenuItem(loadOntologyAction);
        menuItem.getAccessibleContext().setAccessibleDescription("Loads Ontology");
        menu.add(menuItem);

        menuItem = new JMenuItem(loadPrefabsAction);
        menuItem.getAccessibleContext().setAccessibleDescription("Loads Prefabs");
        menu.add(menuItem);

        menuItem = new JMenuItem(savePrefabsAction);
        menuItem.getAccessibleContext().setAccessibleDescription("Saves Prefabs");
        menu.add(menuItem);

        menuItem = new JMenuItem(closeOntologyAction);
        menuItem.getAccessibleContext().setAccessibleDescription("Closes the current Ontology");
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(quitApplicationAction);
        menuItem.getAccessibleContext().setAccessibleDescription("Quits the application");
        menu.add(menuItem);

        menu = new JMenu("Edit");
        menuBar.add(menu);
        addIndividualItem = new JMenuItem(addIndividualAction);
        menu.add(addIndividualItem);

        removeStructureItem = new JMenuItem(removeStructureAction);
        menu.add(removeStructureItem);

        removeIndividualItem = new JMenuItem(removeSelectedIndividualAction);
        menu.add(removeIndividualItem);

        createStructureItem = new JMenuItem(createPrefabAction);
        menu.add(createStructureItem);

        editStructureItem = new JMenuItem(editStructureAction);
        menu.add(editStructureItem);

        menu = new JMenu("View");
        menuBar.add(menu);

        rbMenuItem = new JRadioButtonMenuItem(classPanel.getTitle());
        rbMenuItem.addItemListener(new TabTogglingListener(ontologyPane, classPanel.getTitle(), classPanel));
        rbMenuItem.setSelected(false);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(templateStructuresPanel.getTitle());
        rbMenuItem.addItemListener(new TabTogglingListener(ontologyPane, templateStructuresPanel.getTitle(), templateStructuresPanel));
        rbMenuItem.setSelected(true);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(objectPropertyPanel.getTitle());
        rbMenuItem.addItemListener(new TabTogglingListener(ontologyPane, objectPropertyPanel.getTitle(), objectPropertyPanel));
        rbMenuItem.setSelected(false);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(dataPropertyPanel.getTitle());
        rbMenuItem.addItemListener(new TabTogglingListener(ontologyPane, dataPropertyPanel.getTitle(), dataPropertyPanel));
        rbMenuItem.setSelected(false);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem(prefabPanel.getTitle());
        rbMenuItem.addItemListener(new TabTogglingListener(ontologyPane, prefabPanel.getTitle(), prefabPanel));
        rbMenuItem.setSelected(true);
        menu.add(rbMenuItem);

        menu.addSeparator();

        rbMenuItem = new JRadioButtonMenuItem(worldStructuresPanel.getTitle());
        rbMenuItem.addItemListener(new TabTogglingListener(worldPane, worldStructuresPanel.getTitle(), worldStructuresPanel));
        rbMenuItem.setSelected(true);
        menu.add(rbMenuItem);

        return menuBar;
    }

    private void removeToolBarAndMenuOptions()
    {
        toolBar.removeAll();
        addIndividualItem.setEnabled(false);
        removeStructureItem.setEnabled(false);
        removeIndividualItem.setEnabled(false);
        createStructureItem.setEnabled(false);
        editStructureItem.setEnabled(false);
        toolBar.updateUI();
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(TemplateManager.PC_TEMPLATE_SELECTED))
        {
            removeToolBarAndMenuOptions();
            createStructureItem.setEnabled(true);
            toolBar.add(createPrefabButton);
        }
        else if (event.getPropertyName().equals(TemplateManager.PC_CREATE_STRUCTURE))
        {
            Structure structure = (Structure)event.getNewValue();
            structureCreationControl.display(structure);
        }
        else if (event.getPropertyName().equals(WorldModel.PC_STRUCTURE_SELECTED))
        {
            removeToolBarAndMenuOptions();
            editStructureItem.setEnabled(true);
            removeStructureItem.setEnabled(true);
            toolBar.add(editStructureButton);
            toolBar.add(removeStructureButton);
        }
        else if (event.getPropertyName().equals(WorldModel.PC_EDIT_STRUCTURE))
        {
            Structure structure = (Structure)event.getNewValue();
            structureCreationControl.display(structure);
        }

        if (event.getPropertyName().equals(OntologyModel.PC_ONTOLOGY_CLEARED) || event.getPropertyName().equals(WorldModel.PC_WORLD_CLEARED))
        {
            removeToolBarAndMenuOptions();
        }
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_ONTOLOGY_PREFAB_SELECTED))
        {
            removeToolBarAndMenuOptions();
            editStructureItem.setEnabled(true);
            toolBar.add(editStructureButton);
            toolBar.add(removeIndividualButton);
            removeIndividualItem.setEnabled(true);
        }
        else if (event.getPropertyName().equals(OntologyModel.PC_NEW_INDIVIDUAL_SELECTED))
        {
            removeToolBarAndMenuOptions();
            addIndividualItem.setEnabled(true);

            if (controller.getCurrentlySelected() == SelectionType.ONTOLOGY_INDIVIDUAL)
            {
                createStructureItem.setEnabled(true);
                removeIndividualItem.setEnabled(false);
                toolBar.add(createPrefabButton);
                toolBar.add(addIndividualButton);
            }
            else if (controller.getCurrentlySelected() == SelectionType.ONTOLOGY_PREFAB_MEMBER)
            {
                toolBar.add(removeIndividualButton);
                removeIndividualItem.setEnabled(true);
            }

        }
        else if (event.getPropertyName().equals(OntologyModel.PC_NEW_CLASS_SELECTED))
        {
            removeToolBarAndMenuOptions();
        }
        else if (event.getPropertyName().equals(WorldModel.PC_NEW_INSTANCE_SELECTED)
                || event.getPropertyName().equals(WorldModel.PC_NEW_WORLD_PREFAB_SELECTED))
        {
            removeToolBarAndMenuOptions();
            removeStructureItem.setEnabled(true);
            toolBar.add(removeStructureButton);
        }
    }

    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
