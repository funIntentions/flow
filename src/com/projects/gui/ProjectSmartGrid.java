package com.projects.gui;

import com.projects.gui.panel.*;
import com.projects.helper.Constants;
import com.projects.input.actions.*;
import com.projects.input.listeners.TabTogglingListener;
import com.projects.input.listeners.TemplateStructureSelectedListener;
import com.projects.input.listeners.WorldStructureSelectedListener;
import com.projects.management.SystemController;
import com.projects.models.Structure;
import com.projects.systems.StructureManager;
import com.projects.systems.simulation.World;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Locale;

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
    private WorldStructuresPanel worldStructuresPanel;
    private SimulationInfoPanel simulationInfoPanel;
    private SupplyAndDemandPanel supplyAndDemandPanel;
    private TemplateStructuresPanel templateStructuresPanel;
    private SelectionInfoPanel selectionInfoPanel;
    private StatusPanel statusBar;
    private GraphicsPanel graphics;

    JTabbedPane ontologyPane;
    JTabbedPane worldPane;

    private JToolBar toolBar;
    private JButton removeStructureButton, addStructureButton, editStructureButton, runSimulationButton, pauseSimulationButton, resetSimulationButton; // ToolBar Buttons
    private JMenuItem removeStructureItem, createStructureItem, editStructureItem; // MenuItems

    StructureEditor structureEditor;

    private ProjectSmartGrid(JFrame frame)
    {
        SystemController controller = new SystemController();

        quitApplicationAction = new QuitApplicationAction(controller);
        loadDefaultAction = new LoadDefaultAction(controller);
        loadFileAction = new OpenFileAction(this, controller);
        saveFileAction = new SaveFileAction(this, controller);
        runSimulationAction = new RunSimulationAction(controller);
        pauseSimulationAction = new PauseSimulationAction(controller);
        resetSimulationAction = new ResetSimulationAction(controller);

        TemplateStructureSelectedListener templateStructureSelectedListener = new TemplateStructureSelectedListener(controller);
        WorldStructureSelectedListener worldStructureSelectedListener = new WorldStructureSelectedListener(controller);

        selectionInfoPanel = new SelectionInfoPanel();
        supplyAndDemandPanel = new SupplyAndDemandPanel();
        worldStructuresPanel = new WorldStructuresPanel(worldStructureSelectedListener);
        templateStructuresPanel = new TemplateStructuresPanel(templateStructureSelectedListener);
        simulationInfoPanel = new SimulationInfoPanel(controller);
        statusBar = new StatusPanel();

        graphics = new GraphicsPanel();
        structureEditor = new StructureEditor(frame, controller);

        removeSelectedStructureAction = new RemoveSelectedStructureAction(worldStructuresPanel.getStructureTable(), worldStructuresPanel.getTemplateTable(), controller);
        createPrefabAction = new AddStructureAction(templateStructuresPanel.getStructureTable(), templateStructuresPanel.getTemplateTable(), controller); // TODO: refactor so I don't have to get the table
        editStructureAction = new EditStructureAction(controller);

        controller.subscribeView(supplyAndDemandPanel);
        controller.subscribeView(simulationInfoPanel);
        controller.subscribeView(structureEditor);
        controller.subscribeView(templateStructuresPanel);
        controller.subscribeView(selectionInfoPanel);
        controller.subscribeView(worldStructuresPanel);
        controller.subscribeView(statusBar);
        controller.subscribeView(graphics);
        controller.subscribeView(this);
        controller.setupComplete();
        setupPane();
    }

    private void setupPane()
    {
        setLayout(new BorderLayout());
        setBackground(Color.RED);

        JTabbedPane bottomRightPanel = new JTabbedPane();
        bottomRightPanel.add(simulationInfoPanel, "Overview");
        bottomRightPanel.add(selectionInfoPanel, "Selection");
        bottomRightPanel.add(supplyAndDemandPanel, "Production");

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphics, bottomRightPanel);
        rightSplitPane.setResizeWeight(0.6);
        rightSplitPane.setOneTouchExpandable(true);
        rightSplitPane.setContinuousLayout(true);

        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), rightSplitPane);
        centerSplitPane.setResizeWeight(0.06);
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
        Locale.setDefault(Locale.CANADA);
        mainFrame.setPreferredSize(new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ProjectSmartGrid project = new ProjectSmartGrid(mainFrame);
        mainFrame.setJMenuBar(project.createMenuBar());
        mainFrame.setContentPane(project);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private JToolBar createToolBar()
    {
        toolBar = new JToolBar("Available Options");
        removeStructureButton = new JButton(removeSelectedStructureAction);
        addStructureButton = new JButton(createPrefabAction);
        editStructureButton = new JButton(editStructureAction);
        runSimulationButton = new JButton(runSimulationAction);
        pauseSimulationButton = new JButton(pauseSimulationAction);
        resetSimulationButton = new JButton(resetSimulationAction);
        toolBar.add(editStructureButton);
        toolBar.add(addStructureButton);
        toolBar.add(removeStructureButton);
        toolBar.addSeparator();
        toolBar.add(runSimulationButton);
        toolBar.add(pauseSimulationButton);
        toolBar.add(resetSimulationButton);

        editStructureButton.setVisible(false);
        addStructureButton.setVisible(false);
        removeStructureButton.setVisible(false);

        return toolBar;
    }

    private JSplitPane createLeftPanel()
    {

        ontologyPane = new JTabbedPane();
        worldPane = new JTabbedPane();

        JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ontologyPane, worldPane);
        leftPanel.setResizeWeight(0.5);
        leftPanel.setOneTouchExpandable(true);
        leftPanel.setContinuousLayout(true);
        return leftPanel;
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
        removeStructureButton.setVisible(false);
        addStructureButton.setVisible(false);
        editStructureButton.setVisible(false);
        removeStructureItem.setEnabled(false);
        createStructureItem.setEnabled(false);
        editStructureItem.setEnabled(false);
        toolBar.updateUI();
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(StructureManager.PC_TEMPLATE_SELECTED))
        {
            removeToolBarAndMenuOptions();
            editStructureItem.setEnabled(true);
            createStructureItem.setEnabled(true);
            editStructureButton.setVisible(true);
            addStructureButton.setVisible(true);
        }
        else if (event.getPropertyName().equals(StructureManager.PC_CREATE_STRUCTURE))
        {
            Structure structure = (Structure)event.getNewValue();
            structureEditor.display(structure);
        }
        else if (event.getPropertyName().equals(World.PC_STRUCTURE_SELECTED))
        {
            removeToolBarAndMenuOptions();
            editStructureItem.setEnabled(true);
            removeStructureItem.setEnabled(true);
            editStructureButton.setVisible(true);
            removeStructureButton.setVisible(true);
        }
        else if (event.getPropertyName().equals(StructureManager.PC_EDITING_STRUCTURE))
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
