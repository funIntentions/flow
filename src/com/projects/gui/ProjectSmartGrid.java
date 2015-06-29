package com.projects.gui;

import com.projects.helper.Constants;
import com.projects.input.actions.*;
import com.projects.input.listeners.PropertiesTableListener;
import com.projects.input.listeners.TabTogglingListener;
import com.projects.input.listeners.TemplateStructureSelectedListener;
import com.projects.input.listeners.WorldStructureSelectedListener;
import com.projects.management.SystemController;
import com.projects.models.Structure;
import com.projects.systems.TemplateManager;
import com.projects.systems.simulation.World;

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
            removeSelectedStructureAction,
            createPrefabAction,
            editStructureAction,
            loadDefaultAction,
            loadFileAction,
            saveFileAction,
            runSimulationAction,
            pauseSimulationAction,
            resetSimulationAction;
    private TemplateStructureSelectedListener templateStructureSelectedListener;
    private WorldStructureSelectedListener worldStructureSelectedListener;
    private PropertiesTableListener propertiesTableListener;
    private SelectionPropertyPanel selectionInfoPanel;
    private WorldStructuresPanel worldStructuresPanel;
    private SimulationInfoPanel simulationInfoPanel;
    private SystemController controller;
    private TemplateStructuresPanel templateStructuresPanel;
    private StatusPanel statusBar;

    private JSplitPane rightSplitPane;
    private JSplitPane centerSplitPane;

    JTabbedPane ontologyPane;
    JTabbedPane worldPane;

    private JToolBar toolBar;
    private JButton removeStructureButton, createPrefabButton, editStructureButton, runSimulationButton, pauseSimulationButton, resetSimulationButton; // ToolBar Buttons
    private JMenuItem removeStructureItem, createStructureItem, editStructureItem; // MenuItems

    StructureEditor structureEditor;

    private ProjectSmartGrid(JFrame frame)
    {
        controller = new SystemController();

        quitApplicationAction = new QuitApplicationAction("Quit", null, null, null, controller);
        loadDefaultAction = new LoadDefaultAction("Open Default", null, controller);
        loadFileAction = new OpenFileAction("Open File", null, null, null, this, controller, Constants.SMART_GRID_FILE);
        saveFileAction = new SaveFileAction("Save File", null, null, null, this, controller, Constants.SMART_GRID_FILE);
        runSimulationAction = new RunSimulationAction("Run", null, controller);
        pauseSimulationAction = new PauseSimulationAction("Pause", null, controller);
        resetSimulationAction = new ResetSimulationAction("Reset", null, controller);

        templateStructureSelectedListener = new TemplateStructureSelectedListener(controller);
        worldStructureSelectedListener = new WorldStructureSelectedListener(controller);
        propertiesTableListener = new PropertiesTableListener(controller);

        selectionInfoPanel = new SelectionPropertyPanel("Selection", propertiesTableListener);
        worldStructuresPanel = new WorldStructuresPanel(worldStructureSelectedListener);
        templateStructuresPanel = new TemplateStructuresPanel(templateStructureSelectedListener);
        simulationInfoPanel = new SimulationInfoPanel(controller);
        statusBar = new StatusPanel("Application Started");

        structureEditor = new StructureEditor(frame, controller);

        removeSelectedStructureAction = new RemoveSelectedStructureAction("Remove Structure", null, null, null, worldStructuresPanel.getStructureTable(), worldStructuresPanel.getTemplateTable(), controller);
        createPrefabAction = new AddStructureAction("Add Structure", null, null, null, templateStructuresPanel.getStructureTable(), templateStructuresPanel.getTemplateTable(), controller); // TODO: refactor so I don't have to get the table
        editStructureAction = new EditStructureAction("Edit Structure", null, null, null, controller);

        controller.subscribeView(simulationInfoPanel);
        controller.subscribeView(structureEditor);
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
        removeStructureButton = new JButton(removeSelectedStructureAction);
        createPrefabButton = new JButton(createPrefabAction);
        editStructureButton = new JButton(editStructureAction);
        runSimulationButton = new JButton(runSimulationAction);
        pauseSimulationButton = new JButton(pauseSimulationAction);
        resetSimulationButton = new JButton(resetSimulationAction);
        toolBar.add(removeStructureButton);
        toolBar.add(createPrefabButton);
        toolBar.add(editStructureButton);
        toolBar.addSeparator();
        toolBar.add(runSimulationButton);
        toolBar.add(pauseSimulationButton);
        toolBar.add(resetSimulationButton);
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

    private JPanel createRightBottomPanel()
    {
        //JSplitPane rightBottomPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectionInfoPanel, simulationInfoPanel);
        /*rightBottomPanel.setBackground(Color.ORANGE);
        rightBottomPanel.setContinuousLayout(true);
        rightBottomPanel.setResizeWeight(0.5);
        rightBottomPanel.setOneTouchExpandable(true);*/

        return simulationInfoPanel;
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

        menuItem = new JMenuItem(loadDefaultAction);
        menu.add(menuItem);

        menuItem = new JMenuItem(loadFileAction);
        menu.add(menuItem);

        menuItem = new JMenuItem(saveFileAction);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(quitApplicationAction);
        menu.add(menuItem);

        menu = new JMenu("Edit");
        menuBar.add(menu);

        removeStructureItem = new JMenuItem(removeSelectedStructureAction);
        menu.add(removeStructureItem);

        createStructureItem = new JMenuItem(createPrefabAction);
        menu.add(createStructureItem);

        editStructureItem = new JMenuItem(editStructureAction);
        menu.add(editStructureItem);

        menu = new JMenu("View");
        menuBar.add(menu);

        rbMenuItem = new JRadioButtonMenuItem(templateStructuresPanel.getTitle());
        rbMenuItem.addItemListener(new TabTogglingListener(ontologyPane, templateStructuresPanel.getTitle(), templateStructuresPanel));
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
        removeStructureItem.setEnabled(false);
        createStructureItem.setEnabled(false);
        editStructureItem.setEnabled(false);
        toolBar.updateUI();
    }

    private void addToolBarSimulationControls()
    {
        toolBar.addSeparator();
        toolBar.add(runSimulationButton);
        toolBar.add(pauseSimulationButton);
        toolBar.add(resetSimulationButton);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(TemplateManager.PC_TEMPLATE_SELECTED))
        {
            removeToolBarAndMenuOptions();
            editStructureItem.setEnabled(true);
            createStructureItem.setEnabled(true);
            toolBar.add(editStructureButton);
            toolBar.add(createPrefabButton);
            addToolBarSimulationControls();
        }
        else if (event.getPropertyName().equals(TemplateManager.PC_CREATE_STRUCTURE))
        {
            Structure structure = (Structure)event.getNewValue();
            structureEditor.display(structure);
        }
        else if (event.getPropertyName().equals(World.PC_STRUCTURE_SELECTED))
        {
            removeToolBarAndMenuOptions();
            editStructureItem.setEnabled(true);
            removeStructureItem.setEnabled(true);
            toolBar.add(editStructureButton);
            toolBar.add(removeStructureButton);
            addToolBarSimulationControls();
        }
        else if (event.getPropertyName().equals(TemplateManager.PC_EDITING_STRUCTURE))
        {
            Structure structure = (Structure)event.getNewValue();
            structureEditor.display(structure);
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
