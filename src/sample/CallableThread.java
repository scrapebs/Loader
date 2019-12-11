package sample;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

class CallableThread implements Callable {
    private int curpoint;
    private LocalDateTime LDTDateStart;
    private int stepMinutes;
    private String queryText;
    private String base;
    private String login;
    private String password;
    private String curFilesPath;
    private String divisionMark;

    public CallableThread(int curpoint, LocalDateTime LDTDateStart, int stepMinutes, String queryText, String base,
                          String login, String password, String curFilesPath, String divisionMark){
        this.curpoint = curpoint;
        this.LDTDateStart = LDTDateStart;
        this.stepMinutes = stepMinutes;
        this.queryText = queryText;
        this.base = base;
        this.login = login;
        this.password = password;
        this.curFilesPath = curFilesPath;
        this.divisionMark = divisionMark;
    }

    @Override
    public Void call() {
        if(!LoaderModel.interrupted){
            // Create thread.sleep for period 22:30 - 2:30 MSC
            ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
            if(base.equals("10.67.30.59:1521:ufo"))
                if(localTime.getHour() == 22 && localTime.getMinute() > 30)  //22^30
                {
                    System.out.println("stopped!");
                    try {
                        Thread.sleep(1000 * 60 * 60 * 4);
                        // Если база не встала - спать еще
                        while (! DBManager.checkDBState(base, login, password))
                            Thread.sleep(1000 * 60 * 30 * 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            LocalDateTime startOfQueryPeriod = LDTDateStart.plusMinutes(curpoint);
            LocalDateTime endOfQueryPeriod = LDTDateStart.plusMinutes(curpoint + stepMinutes);

            queryText = queryText.replaceAll("@DATE1", "to_date('" + startOfQueryPeriod.format(LoaderModel.formatterFull) + "', 'dd.mm.yyyy hh24:mi:ss') " );
            queryText = queryText.replaceAll("@DATE2", "to_date('" + endOfQueryPeriod.format(LoaderModel.formatterFull) + "', 'dd.mm.yyyy hh24:mi:ss') " );
            String curFileName = startOfQueryPeriod.format(LoaderModel.formatterFull) + " - " + endOfQueryPeriod.format(LoaderModel.formatterFull);

            try {
                while(! DBManager.makeQuery(queryText, base, login, password, curFilesPath, curFileName, divisionMark))
                {
                    System.out.println("Запрос выполнен неуспешно");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            // Update value for progress bar
            LoaderModel.setCountProgress(LoaderModel.getCountProgress() + 1);
        } else {
            for(Future<Void> future : LoaderModel.futures){
                future.cancel(true);
            }
        }
        return null;
    }
}