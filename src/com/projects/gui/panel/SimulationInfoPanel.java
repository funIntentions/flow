package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.gui.table.RankingTable;
import com.projects.management.SystemController;
import com.projects.models.Structure;
import com.projects.models.WorldTimer;
import com.projects.systems.simulation.DemandManager;
import com.projects.systems.simulation.SimulationStatus;
import com.projects.systems.simulation.World;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Created by Dan on 6/25/2015.
 */
public class SimulationInfoPanel extends JPanel implements SubscribedView
{
    private JLabel timeLabel;
    private JLabel updateLabel;
    private JComboBox<WorldTimer.UpdateRate> updateRateOptions;
    private JLabel costLabel;
    private JLabel usageLabel;
    private JLabel emissionsLabel;
    private DecimalFormat decimalFormat;
    private DecimalFormat timeFormat;
    private DatePicker datePickerForEndDate;
    private DatePicker datePickerForStartDate;
    private LocalDate currentDate;
    private SystemController controller;
    private RankingTable expensesTable;
    private RankingTable impactTable;
    private JTable structureExpenseRankingTable;
    private JTable environmentalImpactRankingTable;

    public SimulationInfoPanel(final SystemController systemController)
    {
        controller = systemController;
        setLayout(new GridBagLayout());
        decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        timeFormat = new DecimalFormat("00");
        GridBagConstraints constraints = new GridBagConstraints();

        currentDate = LocalDate.now();
        final JFXPanel fxEndDatePanel = new JFXPanel();
        final JFXPanel fxStartDatePanel = new JFXPanel();
        final JPanel simulationTimePanel = new JPanel(new BorderLayout(10, 10));
        simulationTimePanel.setBorder(BorderFactory.createTitledBorder("Simulation Time"));

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                VBox vBoxStartDate = new VBox(20);
                Scene sceneStartDate = new Scene(vBoxStartDate);
                fxStartDatePanel.setScene(sceneStartDate);

                Label labelStartDate = new Label("Start Date: ");
                GridPane gridPaneStartDate = new GridPane();
                datePickerForStartDate = new DatePicker();
                datePickerForStartDate.setValue(LocalDate.now());
                datePickerForStartDate.setOnAction(event -> {
                    controller.setStartDate(datePickerForStartDate.getValue());
                });
                gridPaneStartDate.add(labelStartDate, 0, 0);
                gridPaneStartDate.add(datePickerForStartDate, 0, 1);
                vBoxStartDate.getChildren().add(gridPaneStartDate);

                VBox vBoxEndDate = new VBox(20);
                Scene sceneEndDate = new Scene(vBoxEndDate);
                fxEndDatePanel.setScene(sceneEndDate);

                javafx.scene.control.Label labelEndDate = new Label("End Date: ");
                GridPane gridPaneEndDate = new GridPane();
                datePickerForEndDate = new DatePicker();
                datePickerForEndDate.setValue(datePickerForStartDate.getValue().plusDays(1));
                datePickerForEndDate.setOnAction(event -> {
                    controller.setEndDate(datePickerForEndDate.getValue());
                });
                gridPaneEndDate.add(labelEndDate, 0,0);
                gridPaneEndDate.add(datePickerForEndDate, 0, 1);
                vBoxEndDate.getChildren().add(gridPaneEndDate);

                final Callback<DatePicker, DateCell> startDayCellFactory =
                    new Callback<DatePicker, DateCell>()
                    {
                        @Override
                        public DateCell call(final DatePicker datePicker)
                        {
                            return new DateCell()
                            {
                                @Override
                                public void updateItem(LocalDate item, boolean empty)
                                {
                                    super.updateItem(item, empty);

                                    if (item.isBefore(datePickerForStartDate.getValue().plusDays(1)))
                                    {
                                        setDisable(true);
                                        setStyle("-fx-background-color: #ffc0cb;");
                                    }
                                }
                            };
                        }
                    };
                
                datePickerForEndDate.setDayCellFactory(startDayCellFactory);

                final Callback<DatePicker, DateCell> endDayCellFactory =
                        new Callback<DatePicker, DateCell>()
                        {
                            @Override
                            public DateCell call(final DatePicker datePicker)
                            {
                                return new DateCell()
                                {
                                    @Override
                                    public void updateItem(LocalDate item, boolean empty)
                                    {
                                        super.updateItem(item, empty);

                                        if (item.isAfter(datePickerForEndDate.getValue().minusDays(1)))
                                        {
                                            setDisable(true);
                                            setStyle("-fx-background-color: #ffc0cb;");
                                        }
                                    }
                                };
                            }
                        };

                datePickerForStartDate.setDayCellFactory(endDayCellFactory);
            }
        });

        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        constraints.insets = new Insets(5,5,5,5);

        updateRateOptions = new JComboBox<WorldTimer.UpdateRate>(WorldTimer.UpdateRate.values());
        updateRateOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                systemController.changeSimulationUpdateRate(WorldTimer.UpdateRate.valueOf(updateRateOptions.getSelectedItem().toString()));
            }
        });
        JPanel updateRatePanel = new JPanel(new GridLayout(1, 2));
        updateLabel = new JLabel("Update Rate: ");
        updateRatePanel.add(updateLabel);
        updateRatePanel.add(updateRateOptions);

        fxStartDatePanel.setPreferredSize(new Dimension(112, 50));

        fxEndDatePanel.setPreferredSize(new Dimension(112, 50));

        timeLabel = new JLabel("Time: ");
        simulationTimePanel.add(timeLabel, BorderLayout.PAGE_START);
        JPanel simulationSpanPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        simulationSpanPanel.add(fxStartDatePanel);
        simulationSpanPanel.add(fxEndDatePanel);
        simulationTimePanel.add(simulationSpanPanel, BorderLayout.CENTER);
        simulationTimePanel.add(updateRatePanel, BorderLayout.PAGE_END);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        add(simulationTimePanel, constraints);

        expensesTable = new RankingTable(new String[]{"Structure", "Expense"});
        structureExpenseRankingTable = new JTable(expensesTable);
        JScrollPane expenseRankingScrollPane = new JScrollPane(structureExpenseRankingTable);

        impactTable = new RankingTable(new String[]{"Structure", "Emissions"});
        environmentalImpactRankingTable = new JTable(impactTable);
        JScrollPane environmentalImpactRankingScrollPane = new JScrollPane(environmentalImpactRankingTable);

        constraints.gridheight = 6;
        constraints.gridx = 0;
        add(expenseRankingScrollPane, constraints);

        constraints.gridx = 1;
        add(environmentalImpactRankingScrollPane, constraints);
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(World.PC_WORLD_UPDATE))
        {
            SimulationStatus simulationStatus = (SimulationStatus)event.getNewValue();
            WorldTimer worldTimer = simulationStatus.worldTimer;

            currentDate = datePickerForStartDate.getValue().plusDays(worldTimer.getDay());

            timeLabel.setText("Time: " + timeFormat.format(worldTimer.getHourOfDay()) + ":" + timeFormat.format(worldTimer.getMinutesOfHour()) + ":" + timeFormat.format(worldTimer.getSecondsOfMinute())
                    + " Date: " + currentDate.toString());
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_STARTED))
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    datePickerForStartDate.setDisable(true);
                    datePickerForEndDate.setDisable(true);
                }
            });
            updateRateOptions.setEnabled(false);
        }
        else if (event.getPropertyName().equals(World.PC_SIMULATION_FINISHED))
        {
            DemandManager demandManager = (DemandManager)event.getNewValue();

            List<Structure> structures = demandManager.getStructures();
            HashMap<Integer, Float> expenses = demandManager.getStructureExpenses();
            HashMap<Integer, Float> environmentalImpact = demandManager.getStructureEnvironmentalImpact();

            for (Structure structure : structures)
            {
                expensesTable.addRow(new Object[]{structure.getName(), expenses.get(structure.getId())});
                impactTable.addRow(new Object[]{structure.getName(), environmentalImpact.get(structure.getId())});
            }

            TableRowSorter<TableModel> sorter = new TableRowSorter<>(expensesTable);
            structureExpenseRankingTable.setRowSorter(sorter);
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();

            int columnIndexToSort = 1;
            sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));

            sorter.setSortKeys(sortKeys);
            sorter.sort();

            sorter = new TableRowSorter<>(impactTable);
            environmentalImpactRankingTable.setRowSorter(sorter);
            sortKeys = new ArrayList<>();

            sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));

            sorter.setSortKeys(sortKeys);
            sorter.sort();
        }

        if (event.getPropertyName().equals(World.PC_WORLD_RESET))
        {
            expensesTable.clearTable();
            impactTable.clearTable();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    datePickerForStartDate.setDisable(false);
                    datePickerForEndDate.setDisable(false);
                }
            });
            updateRateOptions.setEnabled(true);
        }
    }
}
