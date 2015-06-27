package com.example.user.taskmessanger;


public class Task extends Object {

    public String id;
    public String title;
    public String desc;
    public String taskDate;
    public String taskTime;



    public Task() {
        this.id = LocalStore.generateTaskId();
        this.title = new String();
        this.desc = new String();
        this.taskTime = new String();
        this.taskDate = new String();


    }

    public Task(String title, String desc, String taskDate, String taskTime) {
        this.id = LocalStore.generateTaskId();
        this.title = title;
        this.desc = desc;
        this.taskTime = taskTime;
        this.taskDate = taskDate;

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public void setId(String id) {
        this.id = id;
    }


}

