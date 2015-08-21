package com.projects.view;

import com.projects.helper.Constants;
import com.projects.model.Building;
import com.projects.model.UsageTimeSpan;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;

import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 8/21/2015.
 */
public class LoadProfileEditDialogController
{
    @FXML
    private LineChart<String, Float> loadProfileChart;

    @FXML
    private TabPane daysOfTheWeekTabPane;

    @FXML
    private TableView<UsageTimeSpan> usageTable;

    @FXML
    private TableColumn<UsageTimeSpan, Boolean> mondayColumn;

    @FXML
    private TableColumn<UsageTimeSpan, Boolean> tuesdayColumn;

    @FXML
    private TableColumn<UsageTimeSpan, Boolean> wednesdayColumn;

    @FXML
    private TableColumn<UsageTimeSpan, Boolean> thursdayColumn;

    @FXML
    private TableColumn<UsageTimeSpan, Boolean> fridayColumn;

    @FXML
    private TableColumn<UsageTimeSpan, Boolean> saturdayColumn;

    @FXML
    private TableColumn<UsageTimeSpan, Boolean> sundayColumn;

    @FXML
    private TableColumn<UsageTimeSpan, LocalTime> usageFromColumn;

    @FXML
    private TableColumn<UsageTimeSpan, LocalTime> usageToColumn;

    @FXML
    private TableColumn<UsageTimeSpan, Double> usageColumn;

    private XYChart.Series<String, Float> series = new XYChart.Series<>();
    private Building building = null;
    private Stage dialogStage = null;

    @FXML
    private void initialize()
    {
        usageTable.setEditable(true);
        loadProfileChart.getData().add(series);

        usageColumn.setCellValueFactory(cellData -> cellData.getValue().usageProperty().asObject());
        usageFromColumn.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        usageToColumn.setCellValueFactory(cellData -> cellData.getValue().toProperty());
        mondayColumn.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesdayColumn.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesdayColumn.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursdayColumn.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        fridayColumn.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
        saturdayColumn.setCellValueFactory(cellData -> cellData.getValue().saturdayProperty());
        sundayColumn.setCellValueFactory(cellData -> cellData.getValue().sundayProperty());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.HOURS_AND_MINUTES_FORMAT);
        LocalTimeStringConverter localTimeStringConverter = new LocalTimeStringConverter(dateTimeFormatter, dateTimeFormatter);

        usageFromColumn.setCellFactory(TextFieldTableCell.<UsageTimeSpan, LocalTime>forTableColumn(localTimeStringConverter));
        usageFromColumn.setOnEditCommit((TableColumn.CellEditEvent<UsageTimeSpan, LocalTime> t) ->
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).setFrom(t.getNewValue()));

        usageToColumn.setCellFactory(TextFieldTableCell.<UsageTimeSpan,LocalTime>forTableColumn(localTimeStringConverter));
        usageToColumn.setOnEditCommit((TableColumn.CellEditEvent<UsageTimeSpan, LocalTime> t) ->
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).setTo(t.getNewValue()));

        mondayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(mondayColumn));
        tuesdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(tuesdayColumn));
        wednesdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(wednesdayColumn));
        thursdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(thursdayColumn));
        fridayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(fridayColumn));
        saturdayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(saturdayColumn));
        sundayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(sundayColumn));

        daysOfTheWeekTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> switchChartData());
    }

    public void setBuilding(Building building)
    {
        this.building = building;
        usageTable.setItems(building.getManualLoadProfileData());
        switchChartData();
    }

    public void setDialogStage(Stage dialogStage)
    {
        this.dialogStage = dialogStage;
    }

    private void switchChartData()
    {
        int day = daysOfTheWeekTabPane.getSelectionModel().getSelectedIndex();

        List<Float> loadProfile = building.getLoadProfilesForWeek() != null && building.getLoadProfilesForWeek().size() > 0 ? building.getLoadProfilesForWeek().get(day): new ArrayList<>();

        loadProfileChart.setAnimated(false);
        series.getData().clear();
        loadProfileChart.setAnimated(true);

        for (int i = 0; i < loadProfile.size(); i+=30)
        {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), loadProfile.get(i)));
        }
    }

    @FXML
    private void handleCreateTimeSpan()
    {
        UsageTimeSpan timeSpan = new UsageTimeSpan(0, LocalTime.ofSecondOfDay(0), LocalTime.ofSecondOfDay(0));
        usageTable.getItems().add(timeSpan);
        building.calculateLoadProfile();
    }

    @FXML
    private void handleRemoveTimeSpan()
    {
        int index = usageTable.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            usageTable.getItems().remove(index);
            building.calculateLoadProfile();
        }
    }
}
