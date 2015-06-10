package com.projects.gui;

import com.projects.helper.Constants;
import com.projects.helper.SelectionType;
import com.projects.management.SystemController;
import com.projects.actions.*;
import com.projects.models.OntologyModel;
import com.projects.models.WorldModel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by Dan on 5/26/2015.
 */
public class ProjectSmartGrid extends JPanel implements SubscribedView //TODO: Maybe create JPanel instance instead of extending it
{
    private Action quitApplicationAction,
            loadInstanceAction,
            closeOntologyAction,
            addIndividualAction,
            removeSelectedInstanceAction,
            removeSelectedIndividualAction,
            createPrefabAction,
            addPrefabAction;
    private IndividualSelectedListener individualSelectedListener;
    private InstanceSelectedListener instanceSelectedListener;
    private PropertiesTableListener propertiesTableListener;
    private ClassSelectedListener classSelectedListener;
    private PrefabSelectedListener prefabSelectedListener;
    private SelectionPropertyPanel selectionInfoPanel;
    private InstancePanel instancePanel;
    private SystemController controller;
    private IndividualPanel individualPanel;
    private ClassPanel classPanel;
    private PrefabPanel prefabPanel;

    private JSplitPane rightSplitPane;
    private JSplitPane centerSplitPane;

    private JToolBar toolBar;
    private JButton addIndividualButton, removeInstanceButton, removeIndividualButton, createPrefabButton, addPrefabButton; // ToolBar Buttons
    private JMenuItem addIndividualItem, removeInstanceItem, removeIndividualItem, createPrefabItem, addPrefabItem; // MenuItems

    public ProjectSmartGrid()
    {
        controller = new SystemController();
        quitApplicationAction = new QuitApplicationAction("Quit", null, null, null, controller);
        closeOntologyAction = new CloseOntologyAction("Close", null, null, null, controller);
        loadInstanceAction = new OpenFileAction("Open", null, null, null, this, controller);
        addIndividualAction = new AddIndividualAction("Add Individual", null, null, null, controller);
        individualSelectedListener = new IndividualSelectedListener(controller);
        instanceSelectedListener = new InstanceSelectedListener(controller);
        propertiesTableListener = new PropertiesTableListener(controller);
        classSelectedListener = new ClassSelectedListener(controller);
        prefabSelectedListener = new PrefabSelectedListener(controller);
        selectionInfoPanel = new SelectionPropertyPanel("Selection", propertiesTableListener);
        instancePanel = new InstancePanel(instanceSelectedListener);
        individualPanel = new IndividualPanel(individualSelectedListener);
        classPanel = new ClassPanel(classSelectedListener);
        prefabPanel = new PrefabPanel(prefabSelectedListener);

        removeSelectedInstanceAction = new RemoveSelectedAction("Remove Instance", null, null, null,instancePanel.getWorldInstanceTree(), controller);
        removeSelectedIndividualAction = new RemoveSelectedAction("Remove Individual", null, null, null, prefabPanel.getPrefabTree(), controller);
        createPrefabAction = new CreatePrefabAction("Create Prefab", null, null, null, individualPanel.getIndividualTable(), controller); // TODO: refactor so I don't have to get the table
        addPrefabAction = new AddPrefabAction("Add Prefab", null, null, null, prefabPanel.getPrefabTree(), controller);

        controller.subscribeView(prefabPanel);
        controller.subscribeView(classPanel);
        controller.subscribeView(individualPanel);
        controller.subscribeView(selectionInfoPanel);
        controller.subscribeView(instancePanel);
        controller.subscribeView(this);
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
        mainFrame.setPreferredSize(new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ProjectSmartGrid project = new ProjectSmartGrid();
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
        removeInstanceButton = new JButton(removeSelectedInstanceAction);
        removeIndividualButton = new JButton(removeSelectedIndividualAction);
        createPrefabButton = new JButton(createPrefabAction);
        addPrefabButton = new JButton(addPrefabAction);
        toolBar.add(addIndividualButton);
        toolBar.add(removeInstanceButton);
        toolBar.add(removeIndividualButton);
        toolBar.add(createPrefabButton);
        toolBar.add(addPrefabButton);
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
        addIndividualItem = new JMenuItem(addIndividualAction);
        menu.add(addIndividualItem);

        removeInstanceItem = new JMenuItem(removeSelectedInstanceAction);
        menu.add(removeInstanceItem);

        removeIndividualItem = new JMenuItem(removeSelectedIndividualAction);
        menu.add(removeIndividualItem);

        createPrefabItem = new JMenuItem(createPrefabAction);
        menu.add(createPrefabItem);

        addPrefabItem = new JMenuItem(addPrefabAction);
        menu.add(addPrefabItem);

        return menuBar;
    }

    private void removeToolBarAndMenuOptions()
    {
        toolBar.removeAll();
        addIndividualItem.setEnabled(false);
        removeInstanceItem.setEnabled(false);
        removeIndividualItem.setEnabled(false);
        createPrefabItem.setEnabled(false);
        addPrefabItem.setEnabled(false);
        toolBar.updateUI();
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(OntologyModel.PC_NEW_INDIVIDUAL_SELECTED))
        {
            removeToolBarAndMenuOptions();
            toolBar.add(addIndividualButton);
            addIndividualItem.setEnabled(true);

            if (controller.getCurrentlySelected() == SelectionType.INDIVIDUAL)
            {
                createPrefabItem.setEnabled(true);
                removeIndividualItem.setEnabled(false);
                toolBar.add(createPrefabButton);
            }
            else if (controller.getCurrentlySelected() == SelectionType.PREFAB)
            {
                toolBar.add(addPrefabButton);
                toolBar.add(removeIndividualButton);
                addPrefabItem.setEnabled(true);
                removeIndividualItem.setEnabled(true);
            }

        }
        else if (event.getPropertyName().equals(OntologyModel.PC_NEW_CLASS_SELECTED))
        {
            removeToolBarAndMenuOptions();
        }
        else if (event.getPropertyName().equals(WorldModel.PC_NEW_INSTANCE_SELECTED))
        {
            removeToolBarAndMenuOptions();
            removeInstanceItem.setEnabled(true);
            toolBar.add(removeInstanceButton);
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
