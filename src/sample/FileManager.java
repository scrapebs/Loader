package sample;

import org.json.*;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileManager {

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

    public static String createFolder(String folderName){
        File dir = null;
        String loadedFilePath = "\\";
        try {
            loadedFilePath = "C:\\Users\\" + System.getProperty("user.name") + "\\Documents" + "\\" + "Выгрузки";
            File corePath = new File(loadedFilePath);
            if(corePath.exists())
                dir = new File(loadedFilePath + "\\" + folderName);
            else {
                dir = new File(loadedFilePath);
                dir.mkdir();
                dir = new File(loadedFilePath + "\\" + folderName);
            }
        } catch (Exception e) {
            System.exit(1);
        }

        dir.mkdir();
        return loadedFilePath;
    }

    public static void WriteInFile(List<String> rowsOfQuery, String columnNames, String curFilesPath, String curFileName) {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(curFilesPath + "\\" + curFileName), "Cp1251"))) {
            for(String rowOfQuery : rowsOfQuery) {
                rowOfQuery = rowOfQuery.replaceAll("\n", " ");
                out.write(rowOfQuery + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void JoinFiles(String curFilesPath, String loadedFilePath, String finalFileName, String columnNames, boolean isMakeArchive) {
        File folder = new File(curFilesPath);

        try {
            String[] fileNames = folder.list();
            if (fileNames.length > 0) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(loadedFilePath+"\\"+finalFileName+".csv")), "Cp1251"));
                writer.write(columnNames+"\n");

                int linesCounter = 0;
                int fileNumber = 1;

                for (String fileName : fileNames) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(curFilesPath + "\\" + fileName), "Cp1251"));
                    String line = bufferedReader.readLine();
                    while(line != null){
                        if(linesCounter > 999_999){
                            linesCounter = 0;
                            writer.flush();
                            writer.close();
                            fileNumber++;
                            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(loadedFilePath+"\\"+finalFileName +"_"+ fileNumber+".csv")), "Cp1251"));
                            writer.write(columnNames+"\n");
                        }
                        writer.write(line+"\n");
                        line = bufferedReader.readLine();
                        linesCounter++;
                    }
                    bufferedReader.close();
                }
                writer.close();

                // Архивируем если нужно
                if (isMakeArchive) {
                    ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(loadedFilePath + "\\" + finalFileName + ".zip"));
                    FileInputStream fis = new FileInputStream(loadedFilePath + "\\" + finalFileName + ".csv");
                    ZipEntry entry = new ZipEntry(finalFileName + ".csv");
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                    fis.close();
                    deleteDirecory(loadedFilePath + "\\" + finalFileName + ".csv");

                    for(int i = 2; i <= fileNumber; i++){
                        fis = new FileInputStream(loadedFilePath + "\\" + finalFileName + "_"+ i + ".csv");
                        entry = new ZipEntry(finalFileName + "_" + i + ".csv");
                        zout.putNextEntry(entry);
                        buffer = new byte[fis.available()];
                        fis.read(buffer);
                        zout.write(buffer);
                        zout.closeEntry();
                        fis.close();
                        deleteDirecory(loadedFilePath + "\\" + finalFileName + "_"+ i + ".csv");
                    }
                    zout.close();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteDirecory(String folderPath) {
        File dir = new File(folderPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for(int i = 0; i<children.length; i++){
                String f = dir.getPath() + "\\" + children[i];
                deleteDirecory(f);
            }
            dir.delete();
        } else dir.delete();
    }
}
