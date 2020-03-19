package sample.model;

import javafx.scene.control.Alert;
import sample.DBManager;

import java.sql.*;
import java.util.Map;

public class LoginModel {
    static Connection connection;

    public static boolean isLogin(String base, String login, String password) throws SQLException {
        if (connection != null) {
            return true;
        }
        else {
            Map<Connection, String> isConnected = DBManager.Connector(base, login, password);
            if (isConnected.get(null) != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка подключения к базе");
                alert.setHeaderText(null);
                alert.setContentText(isConnected.get(null));
                alert.show();
                return false;
            }
            else
                return true;
        }
    }
}