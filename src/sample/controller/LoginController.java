package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

import org.json.*;

import sample.FileManager;
import sample.model.LoginModel;

import java.io.*;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;

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

        List<String> bases = getNamesOfDatabases();
        chooseBase.getItems().setAll(bases);
        chooseBase.setValue(bases.get(0));
    }

    public void Login (ActionEvent event){

        //tns-name for base to connect
        String base = getDBUrlByName(chooseBase.getValue());

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

    public String getDBUrlByName(String baseName) {
        String base="";

        JSONObject jsonObject = FileManager.getJSONObjectFromFile("data/database_address.json");

        JSONArray dbArray = null;
        try {
            dbArray = jsonObject.getJSONArray("Databases");

            for(int i = 0; i < dbArray.length(); i++) {
                JSONObject dbInfo = dbArray.getJSONObject(i);
                if(dbInfo.get("Name").toString().equals(baseName)) {
                    base = dbInfo.get("Url").toString();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  base;
    }

    public List<String> getNamesOfDatabases() {
        List<String> dbNames = new ArrayList<>();

        JSONObject jsonObject = FileManager.getJSONObjectFromFile("data/database_address.json");

        JSONArray dbArray = null;
        try {
            dbArray = jsonObject.getJSONArray("Databases");
            for(int i = 0; i < dbArray.length(); i++) {
                JSONObject dbInfo = dbArray.getJSONObject(i);
                dbNames.add(dbInfo.get("Name").toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbNames;
    }
}
