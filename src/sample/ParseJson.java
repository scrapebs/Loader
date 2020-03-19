package sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParseJson {

    public static JSONObject getJSONObjectFromFile(String fileName) {
        String line, content="";

        try {
            InputStream inputStream = FileManager.class.getClassLoader().getResourceAsStream(fileName);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8")
            );

            while ((line = reader.readLine()) != null)
                content+=line;

            JSONObject obj = new JSONObject(content);

            return obj;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getNamesOfDatabases() {
        List<String> dbNames = new ArrayList<>();

        JSONObject jsonObject = ParseJson.getJSONObjectFromFile("data/database_address.json");

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

    public static String getJsonFieldByBaseNameAndFieldName(String fieldNameSearchBy, String fieldValueSearchBy,  String fieldNameSearching) {
        String fieldValue="";

        JSONObject jsonObject = ParseJson.getJSONObjectFromFile("data/database_address.json");

        JSONArray dbArray = null;
        try {
            dbArray = jsonObject.getJSONArray("Databases");

            for(int i = 0; i < dbArray.length(); i++) {
                JSONObject dbInfo = dbArray.getJSONObject(i);
                if(dbInfo.get(fieldNameSearchBy).toString().equals(fieldValueSearchBy)) {
                    fieldValue = dbInfo.get(fieldNameSearching).toString();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  fieldValue;
    }
}