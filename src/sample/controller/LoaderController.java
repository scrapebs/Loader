package sample.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.LoadingParams;
import sample.model.LoaderModel;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class LoaderController implements Initializable {
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private TextField dpStartHours;
    @FXML private TextField dpStartMinutes;
    @FXML private TextField dpEndHours;
    @FXML private TextField dpEndMinutes;
    @FXML private TextArea txtQuery;
    @FXML private Button makeLoad;
    @FXML private TextField txtPeriod;
    @FXML private RadioButton isPeriodDivision;
    @FXML private TextField txtThreadNumber;
    @FXML private TextField txtFinalFileName;
    @FXML private ComboBox<String> txtDivisionMark;
    @FXML private CheckBox ckeckBoxMakeArchive;

    private static String base;
    private static String login;
    private static String password;
    private static Stage primaryStageMain;

    public static String getBase() {
        return base;
    }
    public static String getLogin() {
        return base;
    }
    public static String getPassword() {
        return base;
    }
    public static void setBase(String newbase) { base = newbase; }
    public static void setLogin(String newlogin) {
        login = newlogin;
    }
    public static void setPassword(String newpassword) {
        password = newpassword;
    }

    public static void setPrimaryStageMain(Stage primaryStageMain) {
        LoaderController.primaryStageMain = primaryStageMain;
    }

    @Override
    public void initialize(URL fxmlFieldLocation, ResourceBundle resources){
        txtDivisionMark.getItems().setAll(";", ",", "#", "&", "/", "|");
    }

    public void calculatePeriod(ActionEvent event) {

        // Calculate time of running
        long timeStart = System.nanoTime();

        // Name of final File
        String finalFileName = txtFinalFileName.getText();

        if(finalFileName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Введите название конечного файла");
            alert.setHeaderText(null);
            alert.setContentText("Введите название конечного файла");
            alert.show();
            return;
        }

        // We divide the entire period of the request into such time segments (number of minutes)
        int stepMinutes = isPeriodDivision.isSelected() ? Integer.parseInt(txtPeriod.getText()) : 1000000000;

        // Quantity of threads
        int threadNumber = Integer.parseInt(txtThreadNumber.getText());

        // Division mark, that is used in creating csv
        String divisionMark = txtDivisionMark.getValue();

        // Get inputed dates
        LocalDate LDdateStart = dpStart.getValue();
        LocalDate LDdateEnd = dpEnd.getValue();

        // Get inputed time
        int startHours = !dpStartHours.getText().isEmpty() ? Integer.valueOf(dpStartHours.getText()) : 0;
        int startMinutes = !dpStartMinutes.getText().isEmpty() ? Integer.valueOf(dpStartMinutes.getText()) : 0;
        int endHours = !dpEndHours.getText().isEmpty() ? Integer.valueOf(dpEndHours.getText()) : 0;
        int endMinutes = !dpEndMinutes.getText().isEmpty() ? Integer.valueOf(dpEndMinutes.getText()) : 0;

        String messageDate = "";
        if(LDdateEnd == null || LDdateStart == null)
            messageDate = "Введите дату начала и окончания выгрузки";
        else if (! LDdateEnd.atTime(endHours, endMinutes).isAfter(LDdateStart.atTime(startHours, startMinutes)))
            messageDate = "Введите корректные дату начала и окончания выгрузки";

        if (!messageDate.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(messageDate);
            alert.setHeaderText(null);
            alert.setContentText(messageDate);
            alert.show();
            return;
        }

        // Get inputted query
        String queryText = txtQuery.getText();

        // Test whether query is correct
        String errorInQueryIfExist = LoaderModel.getErrorInQueryIfExist(queryText, base, login, password);
        if (! errorInQueryIfExist.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Не удалось выполнить запрос");
            alert.setHeaderText(null);
            alert.setContentText(errorInQueryIfExist);
            alert.show();
            return;
        }

        // Check whether we put results into archive
        LoaderModel.isMakeArchive = ckeckBoxMakeArchive.isSelected();

        try {
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = FXMLLoader.load(getClass().getResource("/sample/forms/Progress.fxml"));
            LoadingParams params = new LoadingParams(base, login, password, LDdateStart, LDdateEnd, startHours,
                    startMinutes, endHours, endMinutes, stepMinutes, queryText,  threadNumber, finalFileName, divisionMark, timeStart);
            ProgressController.setParams(params);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Выгрузчик");
            primaryStage.getIcons().add(new Image("data/6.png"));
            primaryStage.setScene(scene);
            primaryStage.show();


            // Overriding action of closing window of progress of loading
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    ProgressController.stopLoad();
                    event.consume();
                }
            });

            ProgressController.setPrimaryStage(primaryStage);
            ProgressController.setPrimaryStageMain(primaryStageMain);
            ProgressController.setMakeLoad(makeLoad);
            primaryStageMain.opacityProperty().setValue(0.9);
            makeLoad.setDisable(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void dpStartHoursCheck() {
        String numberMatch = "^[0-9]|^1[0-9]|^2[0-3]|^0[0-9]";
        if(!dpStartHours.getText().matches(numberMatch)) {
            dpStartHours.setText("");
        }
    }

    public void dpEndHoursCheck() {
        String numberMatch = "^[0-9]|^1[0-9]|^2[0-3]|^0[0-9]";
        if(!dpEndHours.getText().matches(numberMatch)) {
            dpEndHours.setText("");
        }
    }

    public void dpStartMinutesCheck() {
        String numberMatch = "^[0-9]|^[0-5][0-9]";
        if(!dpStartMinutes.getText().matches(numberMatch)) {
            dpStartMinutes.setText("");
        }
    }

    public void dpEndMinutesCheck() {
        String numberMatch = "^[0-9]|^[0-5][0-9]";
        if(!dpEndMinutes.getText().matches(numberMatch)) {
            dpEndMinutes.setText("");
        }
    }

    // Check value of field that contains number of minutes in each small period
    public void txtPeriodCheck() {
        String numberMatch = "^[1-9]|^[1-9][0-9]|^[1-9]{2}[0-9]";
        if(!txtPeriod.getText().matches(numberMatch)) {
            txtPeriod.setText("10");
        }
    }

    // Check whether we use devision of query on some periods or not
    public void isPeriodDivision(ActionEvent event) {
        if (isPeriodDivision.isSelected() == true){
            txtPeriod.setEditable(true);
            txtPeriod.setOpacity(1);
        }
        else{
            txtPeriod.setEditable(false);
            txtPeriod.setOpacity(0.5);
        }
    }

    // Check value of field that contains number of threads
    public void txtThreadNumberCheck() {
        String numberMatch = "^[1-9]|^[1-9][0-9]|^[1-6]{2}[0-9]";
        if(!txtThreadNumber.getText().matches(numberMatch)) {
            txtThreadNumber.setText("24");
        }
    }
}

