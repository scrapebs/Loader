package sample;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {
    public static Map<Connection, String>  Connector(String base, String login, String password) throws SQLException {
        Map<Connection, String> result = new HashMap();
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@"+base, login, password)) {
            if (conn != null) {
                System.out.println("Connected to the database!");
                result.put(conn,"");
                return result;
            } else {
                System.out.println("Failed to make connection!");
                result.put(null,"Failed to make connection!");
                return result;
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            result.put(null,e.getMessage());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put(null,e.getMessage());
            return result;
        }
    }

    public static Map<Boolean, String> isQueryCorrect(String queryText, String base, String login, String password) {
        Map<Boolean, String> result = new HashMap();
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + base, login, password)) {
            Statement statement = conn.createStatement();
            queryText = queryText.replaceAll("@DATE1", "sysdate" );
            queryText = queryText.replaceAll("@DATE2", "sysdate" );
            ResultSet rs = statement.executeQuery(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
            result.put(false,e.getMessage());
            return result;
        }
        result.put(true,"");
        return result;
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

            String initializeSQL = "begin ibs.executor.set_system_context; end;";
            CallableStatement callableStatement = conn.prepareCall(initializeSQL);
            callableStatement.execute();

            ResultSet rs;
            rs = statement.executeQuery(queryText);
            ResultSetMetaData rsmd = rs.getMetaData();

            int numberOfColumns = rsmd.getColumnCount();
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
                FileManager.writeInFile(rowsOfQuery,  curFilesPath, curFileName);
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
