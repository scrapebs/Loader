package sample;

import java.sql.*;

public class LoginModel {
    static Connection connection;

    public static boolean isLogin(String base, String login, String password) throws SQLException {
        if (connection != null) {
            return true;
        }
        else {
            connection = DBManager.Connector(base, login, password);
            if (connection != null)
                return true;
            else
                return false;
        }
    }
}
