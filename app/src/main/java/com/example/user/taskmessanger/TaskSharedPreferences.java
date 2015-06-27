package com.example.user.taskmessanger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TaskSharedPreferences  {

    public static final String TASK_NAME= "TASK_LOCAL_STORAGE";
    public static final String TASKS = "TASK_ID";
    public static final String PREFS_KEY = "title";
    static List<Task> tasks;


        public TaskSharedPreferences() {

            super();
        }

        public void save(Context context, String text) {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE); //1
            editor = settings.edit(); //2

            editor.putString(PREFS_KEY, text); //3

            editor.commit(); //4
        }

        public String getValue(Context context) {
            SharedPreferences settings;
            String text;

            //settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE);
            text = settings.getString(PREFS_KEY, null);
            return text;
        }

        public void clearSharedPreference(Context context) {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            //settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE);
            editor = settings.edit();

            editor.clear();
            editor.commit();
        }

        public void removeValue(Context context) {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE);
            editor = settings.edit();

            editor.remove(PREFS_KEY);
            editor.commit();
        }



        public static void saveTask(Context context, List<Task> taskList) {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE);
            editor = settings.edit();

            Gson gson = new Gson();
            String jsonTasks = gson.toJson(taskList);

            editor.putString(TASKS, jsonTasks);

            editor.commit();
        }

        public void addTask(Context context, Task task) {
            List<Task> tasks = getTask(context);
            if (tasks == null)
                tasks = new ArrayList<Task>();
            tasks.add(task);
            saveTask(context, tasks);
        }

        public void removeTask(Context context, Task task) {
            ArrayList<Task> tasks = getTask(context);
            if (tasks != null) {
                tasks.remove(task);
                saveTask(context, tasks);
            }
        }


        public static ArrayList<Task> getTask(Context context) {
            SharedPreferences settings;


            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE);

            if (settings.contains(TASKS)) {
                String jsonTasks = settings.getString(TASKS, null);
                Gson gson = new Gson();
                Task[] taskItems = gson.fromJson(jsonTasks,
                        Task[].class);

                tasks = Arrays.asList(taskItems);
                tasks = new ArrayList<Task>(tasks);
            } else {
                //return null;
                saveTask(context, tasks);

            }
            return (ArrayList<Task>) tasks;
        }

        public void putJson(Context context, JSONObject jsonObject) {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.putString("JSONString", jsonObject.toString());

            editor.commit();
        }

        public ArrayList<Task> getJson(Context context, String category) {
            SharedPreferences settings;
            String json;
            JSONArray jPdtArray;
            Task task = null;
            ArrayList<Task> tasks = null;

            settings = context.getSharedPreferences(TASK_NAME, Context.MODE_PRIVATE);
            json = settings.getString("JSONString", null);

            JSONObject jsonObj = null;
            try {
                if (json != null) {
                    jsonObj = new JSONObject(json);
                    jPdtArray = jsonObj.optJSONArray(category);
                    if (jPdtArray != null) {
                        tasks = new ArrayList<Task>();
                        for (int i = 0; i < jPdtArray.length(); i++) {
                            task = new Task();
                            JSONObject pdtObj = jPdtArray.getJSONObject(i);
                            task.setTitle(pdtObj.getString("name"));
                            task.setId(String.valueOf(pdtObj.getInt("id")));
                            tasks.add(task);
                        }
                    }
                }
            } catch (JSONException e) {
            }
            return tasks;
        }
}
