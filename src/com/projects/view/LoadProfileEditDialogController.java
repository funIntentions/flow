package com.projects.view;

import com.projects.helper.Constants;
import com.projects.model.Building;
import com.projects.model.UsageTimeSpan;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the load profile edit dialog view.
 */
public class LoadProfileEditDialogController {
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
    private int minuteInterval = 30;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
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

        DoubleStringConverter doubleStringConverter = new DoubleStringConverter();

        usageColumn.setCellFactory(TextFieldTableCell.<UsageTimeSpan, Double>forTableColumn(doubleStringConverter));
        usageColumn.setOnEditCommit((TableColumn.CellEditEvent<UsageTimeSpan, Double> t) ->
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).setUsage(t.getNewValue()));

        usageFromColumn.setCellFactory(TextFieldTableCell.<UsageTimeSpan, LocalTime>forTableColumn(localTimeStringConverter));
        usageFromColumn.setOnEditCommit((TableColumn.CellEditEvent<UsageTimeSpan, LocalTime> t) ->
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).setFrom(t.getNewValue()));

        usageToColumn.setCellFactory(TextFieldTableCell.<UsageTimeSpan, LocalTime>forTableColumn(localTimeStringConverter));
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
                (observable, oldValue, newValue) -> updateChartData());
    }

    /**
     * Sets the building whose load profile is being edited.
     *
     * @param building building to be edited
     */
    public void setBuilding(Building building) {
        this.building = building;
        usageTable.setItems(building.getManualLoadProfileData());
        initChartData();

        building.getManualLoadProfileData().addListener((ListChangeListener<UsageTimeSpan>) c -> {
            updateChartData();
        });
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage dialog stage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

        dialogStage.setOnCloseRequest(we -> handleClose());
    }

    /**
     * Initializes the load profile chart with the buildings usage time spans.
     */
    private void initChartData() {
        building.calculateLoadProfile();
        int day = daysOfTheWeekTabPane.getSelectionModel().getSelectedIndex();

        List<Float> loadProfile = building.getLoadProfilesForWeek() != null && building.getLoadProfilesForWeek().size() > 0 ? building.getLoadProfilesForWeek().get(day) : new ArrayList<>();

        loadProfileChart.setAnimated(false);
        series.getData().clear();
        loadProfileChart.setAnimated(true);

        for (int i = 0; i < Constants.MINUTES_IN_DAY; i += minuteInterval) {
            float hour = (i / minuteInterval) / 2f;
            series.getData().add(new XYChart.Data<>(String.valueOf(hour), loadProfile.get(i)));
        }
    }

    /**
     * Alters data in the chart that has been changed.
     */
    private void updateChartData() {
        building.calculateLoadProfile();
        int day = daysOfTheWeekTabPane.getSelectionModel().getSelectedIndex();

        List<Float> loadProfile = building.getLoadProfilesForWeek() != null && building.getLoadProfilesForWeek().size() > 0 ? building.getLoadProfilesForWeek().get(day) : new ArrayList<>();

        for (int i = 0; i < series.getData().size(); ++i) {
            XYChart.Data<String, Float> data = series.getData().get(i);

            float hour = i / 2f;
            if (data.getYValue().floatValue() != loadProfile.get(i * minuteInterval).floatValue()) {
                if (i == series.getData().size() - 1) {
                    loadProfileChart.setAnimated(false);
                    series.getData().set(i, new XYChart.Data<>(String.valueOf(hour), loadProfile.get(i * minuteInterval)));
                    loadProfileChart.setAnimated(true);
                } else
                    series.getData().set(i, new XYChart.Data<>(String.valueOf(hour), loadProfile.get(i * minuteInterval)));
            }
        }
    }

    /**
     * Creates a new usage time span for the building.
     */
    @FXML
    private void handleCreateTimeSpan() {
        UsageTimeSpan timeSpan = new UsageTimeSpan(0, LocalTime.ofSecondOfDay(0), LocalTime.ofSecondOfDay(0));

        timeSpan.usageProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.fromProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.toProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.mondayProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.tuesdayProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.wednesdayProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.thursdayProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.fridayProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.saturdayProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });
        timeSpan.sundayProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
        });

        usageTable.getItems().add(timeSpan);
        building.calculateLoadProfile();
    }

    /**
     * Removes a usage time span from the building.
     */
    @FXML
    private void handleRemoveTimeSpan() {
        int index = usageTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            usageTable.getItems().remove(index);
            building.calculateLoadProfile();
        }
    }

    /**
     * Closes the dialog box.
     */
    @FXML
    private void handleClose() {
        dialogStage.close();
    }
}
