package sample.model;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import sample.CallableThread;
import sample.DBManager;
import sample.FileManager;
import sample.controller.ProgressController;

import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoaderModel {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH_mm");
    public static DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("dd.MM.yyyy HH_mm_ss");

    public static boolean isMakeArchive;

    public static volatile ExecutorService executor;
    public static volatile boolean interrupted = false;

    private static volatile int countProgress = 0;

    public static List<Future<Void>> futures;

    public static int getCountProgress() {
        return countProgress;
    }

    public static void setCountProgress(int countProgress) {
        LoaderModel.countProgress = countProgress;
    }

    private ProgressBar progressBar;
    private Label statusLabel;
    private Label curTime;

    public LoaderModel(ProgressBar progressBar, Label statusLabel, Label curTime){
        this.progressBar = progressBar;
        this.statusLabel = statusLabel;
        this.curTime = curTime;
    }

    public static String getErrorInQueryIfExist(String queryText, String base, String login, String password) {
        if(queryText.isEmpty()){
            return "Введите текст запроса";
        }
        Pattern patternRestrictions = Pattern.compile("\\b((UPDATE|INSERT|DELETE|ALTER|GRANT|CREATE|DROP|TRUNCATE)(\\s|\\n|\\*|\\())");
        Matcher matcher = patternRestrictions.matcher(queryText.toUpperCase());
        if(matcher.find()) {
            return "Запрещенная операция";
        }

        if (! DBManager.isQueryCorrect( queryText, base, login, password)) {
            return "Не удалось выполнить запрос";
        }

        return "";
    }

    public void makeLoading(LocalDate LDdateStart, LocalDate LDdateEnd, int startHours, int startMinutes,
                            int endHours, int endMinutes, int stepMinutes, String queryText, String base,
                            String login, String password, int threadNumber, String finalFileName, String divisionMark) throws InterruptedException {

        countProgress = 0;
        Instant start = Instant.now();

        // Join inputed date and time
        LocalDateTime LDTDateStart = LDdateStart.atTime(startHours, startMinutes);
        LocalDateTime LDTDateEnd = LDdateEnd.atTime(endHours, endMinutes);

        if (LDTDateEnd.isAfter(LDTDateStart)){
            long periodOfDateInMilliseconds = Date.valueOf(LDdateEnd).getTime() - Date.valueOf(LDdateStart).getTime();
            int periodOfTime = endHours*60 + endMinutes - startHours*60 - startMinutes;
            int periodInMinutes = (int) (periodOfDateInMilliseconds / (60 * 1000) + periodOfTime);

            // Создаем временную папку
            String folderName = "temp_" + finalFileName;
            String loadedFilePath = FileManager.createFolder(folderName);
            String tempFilePath = loadedFilePath + "\\" + folderName;

            executor = Executors.newFixedThreadPool(threadNumber);

            futures = new LinkedList<>();

            for (int curpoint = 0; curpoint < periodInMinutes; curpoint += stepMinutes) {
                Future<Void> future = executor.submit(new CallableThread(curpoint, LDTDateStart, stepMinutes, queryText, base, login, password, tempFilePath, divisionMark));
                futures.add(future);
            }
            executor.shutdown();

            double progressMaxValue = periodInMinutes/stepMinutes;

            while (!executor.isTerminated()) {
                try {
                    Thread.sleep(1000);

                    double curProgress = countProgress/progressMaxValue*0.9;
                    //System.out.println(curProgress);
                    progressBar.setProgress(curProgress);
                    Duration duration = Duration.between(start, Instant.now().plusSeconds(2));
                    long durationSec = duration.getSeconds();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            statusLabel.setText(String.format("%.0f", curProgress*100)+" %");

                            curTime.setText(String.format("%d:%02d:%02d", durationSec / 3600,
                                    (durationSec % 3600) / 60, durationSec % 60));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(finalFileName.equals(""))
                finalFileName = folderName;

            // Объединяем временные файлы в один финальный;
            if(! interrupted) {
                System.out.println(interrupted);
                String columnNames = DBManager.getColumnNames(queryText, base, login, password, divisionMark);
                FileManager.JoinFiles(tempFilePath, loadedFilePath, finalFileName, columnNames, isMakeArchive);

                // Удаляем папку с временными файлами
                FileManager.deleteDirecory(tempFilePath);

                // Время выполнения
                long timeEnd, timeProcess;
                timeEnd = System.nanoTime();
                timeProcess = (timeEnd - ProgressController.getParams().getTimeStart()) / 1000_000_000;

                String resultText;
                if (timeProcess > 4000) {
                    timeProcess = timeProcess / 3600;
                    resultText = "Выгрузка завершена за: " + timeProcess + " час.";
                } else if (timeProcess > 60) {
                    timeProcess = timeProcess / 60;
                    resultText = "Выгрузка завершена за: " + timeProcess + " мин.";
                } else
                    resultText = "Выгрузка завершена за: " + timeProcess + " сек.";
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("later");
                        ProgressController.showEndMessage(resultText);
                    }
                });
            } else {
                // Удаляем папку с временными файлами
                FileManager.deleteDirecory(tempFilePath);
                interrupted = false;
            }
        }
    }
}
