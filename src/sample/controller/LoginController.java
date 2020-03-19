package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.ParseJson;
import sample.model.LoginModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Label isConnected;
    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> chooseBase;


    @Override
    public void initialize(URL fxmlFieldLocation, ResourceBundle resources){

        List<String> bases = ParseJson.getNamesOfDatabases();
        chooseBase.getItems().setAll(bases);
        chooseBase.setValue(bases.get(0));
    }

    public void Login (ActionEvent event){

        //tns-name for base to connect
        String base = ParseJson.getJsonFieldByBaseNameAndFieldName("Name",chooseBase.getValue(), "Url");

        try {
            if (LoginModel.isLogin(base, txtLogin.getText(), txtPassword.getText())) {
                ((Node)event.getSource()).getScene().getWindow().hide();
                Stage primaryStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                Pane root = FXMLLoader.load(getClass().getResource("/sample/forms/Loader.fxml"));
                LoaderController.setBase(base);
                LoaderController.setLogin(txtLogin.getText());
                LoaderController.setPassword(txtPassword.getText());
                Scene scene = new Scene(root);
                primaryStage.setTitle("Выгрузчик");
                primaryStage.getIcons().add(new Image("data/6.png"));
                primaryStage.setScene(scene);
                primaryStage.show();
                LoaderController.setPrimaryStageMain(primaryStage);
            }
            else{
                isConnected.setText("Неверный логин или пароль");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
