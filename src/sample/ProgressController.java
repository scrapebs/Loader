package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class ProgressController implements Initializable {

    @FXML private Button stopLoad;
    @FXML private Label txtMessage;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Label curTime;

    private static LoadingParams params;

    public static Stage primaryStage;
    public static Stage primaryStageMain;
    public static Button makeLoad;

    public static Task<Void> taskLoading;

    public static void setPrimaryStage(Stage primaryStage) {
        ProgressController.primaryStage = primaryStage;
    }

    public static void setPrimaryStageMain(Stage primaryStageMain) {
        ProgressController.primaryStageMain = primaryStageMain;
    }

    public static void setMakeLoad(Button makeLoad) {
        ProgressController.makeLoad = makeLoad;
    }


    public static void setParams(LoadingParams params) {
        ProgressController.params = params;
    }

    public static LoadingParams getParams() {
        return params;
    }

    @Override
    public void initialize(URL fxmlFieldLocation, ResourceBundle resources) {

        //start
        progressBar.setProgress(0);
        progressBar.progressProperty().unbind();

        taskLoading = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Main function with business logic
                try {
                    LoaderModel loaderModel = new LoaderModel(progressBar, statusLabel, curTime);
                    loaderModel.makeLoading(params.getLDdateStart(), params.getLDdateEnd(), params.getStartHours(),
                            params.getStartMinutes(), params.getEndHours(), params.getEndMinutes(), params.getStepMinutes(),
                            params.getQueryText(), params.getBase(), params.getLogin(), params.getPassword(),
                            params.getThreadNumber(), params.getFinalFileName(), params.getDivisionMark());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread thread = new Thread(taskLoading);
        thread.start();
    }

    public void stopLoad(ActionEvent event) {
        stopLoad();
    }

    public static void stopLoad() {
        taskLoading.cancel();

        LoaderModel.executor.shutdownNow();
        LoaderModel.interrupted = true;
        System.out.println("in stopload");
        while (!LoaderModel.executor.isTerminated()) {
        }
        System.out.println("done");
        taskLoading = null;
        primaryStage.hide();
        primaryStageMain.show();
        primaryStageMain.opacityProperty().setValue(1);
        makeLoad.setDisable(false);
    }

    public static void showEndMessage(String resultText){
        primaryStage.hide();
        primaryStageMain.show();
        primaryStageMain.opacityProperty().setValue(1);
        makeLoad.setDisable(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Выгрузка завершена");
        alert.setHeaderText(null);
        alert.setContentText(resultText);
        alert.show();
    }
}
