package sample;

import java.time.LocalDate;

public class LoadingParams {
    private String base;
    private String login;
    private String password;
    private LocalDate LDdateStart;
    private LocalDate LDdateEnd;
    private int startHours;
    private int startMinutes;
    private int endHours;
    private int endMinutes;
    private int stepMinutes;
    private String queryText;
    private int threadNumber;
    private String finalFileName;
    private String divisionMark;
    private long timeStart;

    public LoadingParams(String base, String login, String password, LocalDate LDdateStart, LocalDate LDdateEnd, int startHours, int startMinutes, int endHours, int endMinutes, int stepMinutes, String queryText, int threadNumber, String finalFileName, String divisionMark, long timeStart) {
        this.base = base;
        this.login = login;
        this.password = password;
        this.LDdateStart = LDdateStart;
        this.LDdateEnd = LDdateEnd;
        this.startHours = startHours;
        this.startMinutes = startMinutes;
        this.endHours = endHours;
        this.endMinutes = endMinutes;
        this.stepMinutes = stepMinutes;
        this.queryText = queryText;
        this.threadNumber = threadNumber;
        this.finalFileName = finalFileName;
        this.divisionMark = divisionMark;
        this.timeStart = timeStart;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getLDdateStart() {
        return LDdateStart;
    }

    public void setLDdateStart(LocalDate LDdateStart) {
        this.LDdateStart = LDdateStart;
    }

    public LocalDate getLDdateEnd() {
        return LDdateEnd;
    }

    public void setLDdateEnd(LocalDate LDdateEnd) {
        this.LDdateEnd = LDdateEnd;
    }

    public int getStartHours() {
        return startHours;
    }

    public void setStartHours(int startHours) {
        this.startHours = startHours;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    public void setStartMinutes(int startMinutes) {
        this.startMinutes = startMinutes;
    }

    public int getEndHours() {
        return endHours;
    }

    public void setEndHours(int endHours) {
        this.endHours = endHours;
    }

    public int getEndMinutes() {
        return endMinutes;
    }

    public void setEndMinutes(int endMinutes) {
        this.endMinutes = endMinutes;
    }

    public int getStepMinutes() {
        return stepMinutes;
    }

    public void setStepMinutes(int stepMinutes) {
        this.stepMinutes = stepMinutes;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public String getFinalFileName() {
        return finalFileName;
    }

    public void setFinalFileName(String finalFileName) {
        this.finalFileName = finalFileName;
    }

    public String getDivisionMark() {
        return divisionMark;
    }

    public void setDivisionMark(String divisionMark) {
        this.divisionMark = divisionMark;
    }

    public long getTimeStart() {
        return timeStart;
    }
}