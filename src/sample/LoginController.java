package sample;

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
        chooseBase.getItems().setAll("Отчетка", "Архивка");
    }

    public void Login (ActionEvent event) {
        try {
            String base="";
            if(chooseBase.getValue().equals("Отчетка"))
                base = "10.67.30.59:1521:ufo";
            else if (chooseBase.getValue().equals("Архивка"))
                base = "onega-arch.cgs.sbrf.ru:1521:epsarch";

            if (LoginModel.isLogin(base, txtLogin.getText(), txtPassword.getText())) {
                ((Node)event.getSource()).getScene().getWindow().hide();
                Stage primaryStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                Pane root = FXMLLoader.load(getClass().getResource("forms/Loader.fxml"));
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
        } catch(Exception e) {}
    }

}
