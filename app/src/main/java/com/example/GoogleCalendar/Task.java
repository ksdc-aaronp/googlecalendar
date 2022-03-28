package com.example.GoogleCalendar;

public class Task {

    private String taskPrincipal;
    private String taskName;
    private String taskStatus;
    private int taskColour;

    public Task(String taskPrincipal, String taskName, String taskStatus, int taskColour) {
        this.taskPrincipal = taskPrincipal;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskColour = taskColour;
    }

    public String getTaskPrincipal() {
        return taskPrincipal;
    }

    public void setTaskPrincipal(String taskPrincipal) {
        this.taskPrincipal = taskPrincipal;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskColour() {
        return taskColour;
    }

    public void setTaskColour(int taskColour) {
        this.taskColour = taskColour;
    }
}
