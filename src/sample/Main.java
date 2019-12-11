package sample;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Pane root = FXMLLoader.load(getClass().getResource("forms/Login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Выгрузчик");
            primaryStage.getIcons().add(new Image("data/6.png"));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
