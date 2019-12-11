package sample;


import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {
    public static Connection Connector(String base, String login, String password) throws SQLException {

        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@"+base, login, password)) {   //jdbc:oracle:thin:@10.67.30.59:1521:ufo
            if (conn != null) {
                System.out.println("Connected to the database!");
                return conn;
            } else {
                System.out.println("Failed to make connection!");
                return null;
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isQueryCorrect(String queryText, String base, String login, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + base, login, password)) {
            Statement statement = conn.createStatement();
            queryText = queryText.replaceAll("@DATE1", "sysdate" );
            queryText = queryText.replaceAll("@DATE2", "sysdate" );
            System.out.println(queryText);
            ResultSet rs = statement.executeQuery(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean checkDBState(String base, String login, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + base, login, password)) {
            Statement statement = conn.createStatement();
            String queryText = "select 1 from dual";
            ResultSet rs = statement.executeQuery(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean makeQuery(String queryText, String base, String login, String password, String curFilesPath,
                                 String curFileName, String divisionMark) throws IOException {

        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@"+base, login, password)) {
            Statement statement = conn.createStatement();
            ResultSet rs;
            String initializeSQL = "begin ibs.executor.set_system_context; end;";
            //rs = statement.executeQuery(initializeSQL);
            rs = statement.executeQuery(queryText);
            ResultSetMetaData rsmd = rs.getMetaData();

            int numberOfColumns = rsmd.getColumnCount();
            String columnNames ="";
            List<String> rowsOfQuery = new LinkedList<>();

            while (rs.next()) {
                String rowOfQuery = "";
                for(int i = 0; i < numberOfColumns; i++) {
                    rowOfQuery += "\""+rs.getString(i+1)+"\"" + divisionMark;
                }
                rowOfQuery.concat("\n");
                rowOfQuery = rowOfQuery.replaceAll("null", " ");

                rowsOfQuery.add(rowOfQuery);
            }

            //Создать временный Файл с данным периодом выгрузки
            if (rowsOfQuery.size() !=0) {
                FileManager.WriteInFile(rowsOfQuery, columnNames, curFilesPath, curFileName);
            }

            rs.close();
            statement.close();
            //Query successfully done
            return true;

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DBManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

            //Если ошибка - спим 10 минут
            //Возвращаем ответ о неуспешности для повторного исполнения
            try {
                Thread.sleep(1000 * 60 * 10 * 1);
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
            return false;
        }
    }

    public static String getColumnNames(String queryText, String base, String login, String password, String divisionMark) {

        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@"+base, login, password)) {
            Statement statement = conn.createStatement();
            queryText = queryText.replaceAll("@DATE1", "sysdate" );
            queryText = queryText.replaceAll("@DATE2", "sysdate" );

            ResultSet rs = statement.executeQuery(queryText);
            ResultSetMetaData rsmd = rs.getMetaData();

            //Названия столбцов
            int numberOfColumns = rsmd.getColumnCount();
            String columnNames ="";
            for(int i = 0; i<numberOfColumns;i++){
                columnNames += "\"" + rsmd.getColumnName(i+1)+"\"" + divisionMark;
            }
            return columnNames;

        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
}
