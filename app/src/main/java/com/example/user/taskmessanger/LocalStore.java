package com.example.user.taskmessanger;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LocalStore {


    public static final List<Task>			TASK_LIST		= new ArrayList<Task>();
    public static int TASK_ID_INCR				= 0;

    public static boolean initialized = false;
    private static Context context;

   public static void initOnce() {

        if(initialized) {
            return;
        }
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm");
        Task task = new Task(
                "Title",
                "Description",
                "date",
                tf.format(Calendar.getInstance().getTime())

        );

        TASK_LIST.add(task);
        initialized = true;
    }

    public static String generateTaskId() {
        return "TID" + (++TASK_ID_INCR);
    }


    public static Task findTaskById(String id) {

        for(Task task : TASK_LIST) {
            if(task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }



}
