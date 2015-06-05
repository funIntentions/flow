package com.projects.gui;

import com.projects.management.SystemController;
import com.projects.actions.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * Created by Dan on 5/26/2015.
 */
public class ProjectSmartGrid extends JPanel //TODO: Maybe create JPanel instance instead of extending it
{
    static final int FRAME_WIDTH = 800;
    static final int FRAME_HEIGHT = 600;
    private Action quitApplicationAction,
            loadInstanceAction,
            closeOntologyAction,
            createInstanceFromSelectionAction,
            deleteSelectedInstanceAction,
            createPrefabFromSelectionAction,
            createInstancesFromPrefabAction;
    private IndividualSelectedListener individualSelectedListener;
    private InstanceSelectedListener instanceSelectedListener;
    private PropertiesTableListener propertiesTableListener;
    private ClassSelectedListener classSelectedListener;
    private SelectionPropertyPanel selectionInfoPanel;
    private InstancePanel instancePanel;
    private SystemController controller;
    private IndividualPanel individualPanel;
    private ClassPanel classPanel;
    private PrefabPanel prefabPanel;

    private JSplitPane rightSplitPane;
    private JSplitPane centerSplitPane;

    public ProjectSmartGrid()
    {
        controller = new SystemController();
        quitApplicationAction = new QuitApplicationAction("Quit", null, null, null, controller);
        closeOntologyAction = new CloseOntologyAction("Close", null, null, null, controller);
        loadInstanceAction = new LoadInstancesAction("Open", null, null, null, this, controller);
        createInstanceFromSelectionAction = new CreateInstanceFromSelectionAction("Create From Selection", null, null, null, controller);
        deleteSelectedInstanceAction = new DeleteSelectedInstanceAction("Delete Selected Instance", null, null, null, controller);
        individualSelectedListener = new IndividualSelectedListener(controller);
        instanceSelectedListener = new InstanceSelectedListener(controller);
        propertiesTableListener = new PropertiesTableListener(controller);
        classSelectedListener = new ClassSelectedListener(controller);
        selectionInfoPanel = new SelectionPropertyPanel("Selection", propertiesTableListener);
        instancePanel = new InstancePanel(controller);
        individualPanel = new IndividualPanel(individualSelectedListener);
        classPanel = new ClassPanel(controller);
        prefabPanel = new PrefabPanel(controller);

        createPrefabFromSelectionAction = new CreatePrefabFromSelectionAction("Create Prefab From Selection", null, null, null, individualPanel.getIndividualTable(), controller); // TODO: refactor so I don't have to get the table
        createInstancesFromPrefabAction = new CreateInstancesFromPrefabAction("Create Instances From Prefab", null, null, null, prefabPanel.getPrefabTree(), controller);

        controller.subscribeView(prefabPanel);
        controller.subscribeView(classPanel);
        controller.subscribeView(individualPanel);
        controller.subscribeView(selectionInfoPanel);
        controller.subscribeView(instancePanel);
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
        add(createStatusBar(), BorderLayout.PAGE_END);
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
        mainFrame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ProjectSmartGrid project = new ProjectSmartGrid();
        mainFrame.setMinimumSize(new Dimension(720, 600));
        mainFrame.setJMenuBar(project.createMenuBar());
        mainFrame.setContentPane(project);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private JToolBar createToolBar()
    {
        JToolBar toolBar = new JToolBar("Available Options");
        JButton createInstanceButton = new JButton(createInstanceFromSelectionAction);
        JButton deleteInstanceButton = new JButton(deleteSelectedInstanceAction);
        JButton createPrefabButton = new JButton(createPrefabFromSelectionAction);
        JButton createInstancesFromPrefabButton = new JButton(createInstancesFromPrefabAction);
        toolBar.add(createInstanceButton);
        toolBar.add(deleteInstanceButton);
        toolBar.add(createPrefabButton);
        toolBar.add(createInstancesFromPrefabButton);
        return toolBar;
    }

    private JPanel createStatusBar()
    {
        JPanel statusBar = new JPanel();
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        JLabel statusLabel = new JLabel(" Status...");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusBar.add(statusLabel);
        return statusBar;
    }

    private JPanel createLeftPanel()
    {
        JPanel leftPanel = new JPanel(false);
        leftPanel.setLayout(new GridLayout(1,2));
        leftPanel.setBackground(Color.CYAN);

        JTabbedPane ontologyPane = new JTabbedPane();
        JComponent panel2 = new JPanel(false);
        JComponent panel3 = new JPanel(false);

        ontologyPane.addTab("Classes", classPanel);
        ontologyPane.addTab("Object Properties", panel2);
        ontologyPane.addTab("Data Properties", panel3);
        ontologyPane.addTab("Individuals", individualPanel);
        ontologyPane.addTab("Prefabs", prefabPanel);

        JTabbedPane instancePane = new JTabbedPane();
        instancePane.addTab("Instances", instancePanel);

        leftPanel.add(ontologyPane);
        leftPanel.add(instancePane);
        return leftPanel;
    }

    private JPanel createRightTopPanel()
    {
        JPanel rightTopPanel = new JPanel(false);
        rightTopPanel.setBackground(Color.PINK);
        return rightTopPanel;
    }

    private JPanel createRightBottomPanel()
    {
        JPanel rightBottomPanel = new JPanel(false);
        rightBottomPanel.setBackground(Color.ORANGE);
        rightBottomPanel.setLayout(new GridLayout(1,2));

        rightBottomPanel.add(selectionInfoPanel);
        rightBottomPanel.add(createSimulationInfoPanel());

        return rightBottomPanel;
    }

    public JPanel createSimulationInfoPanel()
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

    public JMenuBar createMenuBar()
    {
        JMenuBar menuBar;
        JMenu menu, subMenu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menuBar.add(menu);

        menuItem = new JMenuItem(loadInstanceAction);
        menuItem.getAccessibleContext().setAccessibleDescription("Loads Ontology");
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

        menuItem = new JMenuItem(createInstanceFromSelectionAction);
        menu.add(menuItem);

        menuItem = new JMenuItem(deleteSelectedInstanceAction);
        menu.add(menuItem);

        menuItem = new JMenuItem(createPrefabFromSelectionAction);
        menu.add(menuItem);

        menuItem = new JMenuItem(createInstancesFromPrefabAction);
        menu.add(menuItem);

        return menuBar;
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
