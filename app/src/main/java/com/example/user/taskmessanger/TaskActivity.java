package com.example.user.taskmessanger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskActivity extends FragmentActivity implements TextWatcher,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final int REQUEST_NEW_TASK = 100;
    public static final int REQUEST_EDIT_TASK = 101;
    public static final int REQUEST_ADD_TASK = 102;
    public static final String REQUEST_EXTRA_TASK_ID = "task.REQUEST_EXTRA_TASK_ID";
    public static final String RESULT_EXTRA_TASK_ID = "RESULT_EXTRA_TASK_ID";
    public static String TASK_TITLE = "task.TASK_TITLE.";
    public static final String TASK_DATE = "TASK_DATE";
    public static final String TASK_TIME = "TASK_TIME";
    public static final String TASK_DESC = "TASK_DESC";

    private Task task;
    private EditText taskTitle, taskDesc, taskDate, taskTime;
    private Calendar cal;
    private int startMinute, startHour, startDay, startMonth, startYear;

    private Switch alarm_on;
    private TextView textViewTime;


    static final int TIME_DIALOG_ID = 999;
    static final int DATE_DIALOG_ID = 899;
    SimpleDateFormat df;

    //used for register alarm manager
    PendingIntent pendingIntent;
    //used to store running alarmmanager instance
    AlarmManager alarmManager;
    //Callback function for Alarmmanager event
    BroadcastReceiver mReceiver;
    Notification mNotification;
    private TaskSharedPreferences taskSharedPreference;
    Activity context = this;

    /*
     * Overridden method of class FragmentActivity.
     * Creates the NewTaskActivity activity.
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        initActionbar();
        initAttributes();
        initListeners();


        df = new SimpleDateFormat("dd.MM.yyyy");
        taskSharedPreference = new TaskSharedPreferences();
        //  taskSharedPreference.saveTask(this,LocalStore.TASK_LIST);

        cal = Calendar.getInstance();
        startDay = cal.get(Calendar.DAY_OF_MONTH);
        startMonth = cal.get(Calendar.MONTH);
        startYear = cal.get(Calendar.YEAR);
        startHour = cal.get(Calendar.HOUR_OF_DAY);
        startMinute = cal.get(Calendar.MINUTE);


        Spinner hivesSpinner = (Spinner) findViewById(R.id.hives_spinner);


        taskTime = (EditText) findViewById(R.id.reminder_spinner);

        alarm_on = (Switch) findViewById(R.id.switch1);

        if (alarm_on != null) {
            alarm_on.setOnCheckedChangeListener(this);
        }

        //Register AlarmManager Broadcast receive.
        RegisterAlarmBroadcast();
        addButtonListener();
    }

    /*
     * Overridden method of class FragmentActivity.
     * Creates the options menu for the NewTaskActivity activity.
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_task_actions, menu);
        return true;
    }

    /*
     * Overridden method of class FragmentActivity.
     * Called when an item in the options menu is selected.
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (checkRequired()) {
            switch (item.getItemId()) {
                case R.id.action_save:
                    finish();
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("REQUEST_CODE", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    /*
     * Overridden method of interface TextWatcher.
     * Called after each text change of the linked EditTexts.
     * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
     */
    @Override
    public void afterTextChanged(Editable s) {

        if (getCurrentFocus().equals(taskTitle)) {
            task.setTitle(taskTitle.getText().toString().trim());
            Log.d("Bug", taskTitle.getText().toString().trim());

        } else if (getCurrentFocus().equals(taskDate)) {
            task.setTaskDate(s.toString());
            Log.d("Bug", taskDate.getText().toString());


        } else if (getCurrentFocus().equals(taskTime)) {
            task.setTaskTime(s.toString());
            Log.d("Bug", taskTime.getText().toString());

        } else if (getCurrentFocus().equals(taskDesc)) {
            task.setDesc(s.toString().trim());
            Log.d("Bug", taskDesc.getText().toString().trim());
        }

    }

    /*
     * Overridden method of interface TextWatcher.
     * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // Nothing to be done
    }

    /*
     * Overridden method of interface TextWatcher.
     * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Nothing to be done
      /*  taskTitle.removeTextChangedListener(this);
            if(s.length()>0) {

            task.setTitle(taskTitle.getText().toString());
            //   Log.i("Bug", taskTitle.getText().toString().trim());
              taskTitle.addTextChangedListener(this);
        }*/
    }


    /*
     * Saves the task to the store and finishes the activity.
     * Note that is should always be called after a call to
     * checkRequired() and only if the latter returns true.
     */
    @Override
    public void finish() {

        switch (getIntent().getIntExtra("REQUEST_CODE", 0)) {

            case REQUEST_NEW_TASK:

                LocalStore.TASK_LIST.add(task);
                taskSharedPreference.saveTask(context, LocalStore.TASK_LIST);

                Toast.makeText(context, taskTitle.getText().toString(), Toast.LENGTH_LONG).show();
                Log.d("Bug", taskTitle.getText().toString());

            case REQUEST_ADD_TASK:
                // LocalStore.TASK_LIST.add(task);
                //taskSharedPreference.addTask(context, task);
                break;

            case REQUEST_EDIT_TASK:
                break;

        }
        Intent intent = new Intent(this, MainActivity.class);
        // intent.putExtra(RESULT_EXTRA_TASK_ID, task.getId());
        //setResult(RESULT_OK, intent);
        startActivity(intent);
        super.finish();
    }

    /*
     * Initializes the activity's action bar.
     */
    private void initActionbar() {
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayShowCustomEnabled(true);
    }

    /*
     * Sets the values of all attributes.
     */
    private void initAttributes() {
        taskTitle = (EditText) findViewById(R.id.edit_task_name);
        taskDesc = (EditText) findViewById(R.id.desc_edit_text);
        taskDate = (EditText) findViewById(R.id.edit_task_calendar);
        taskTime = (EditText) findViewById(R.id.reminder_spinner);

        Log.d("Bug", "i have been caled" + (getIntent().getIntExtra("REQUEST_CODE", 0)));
        switch (getIntent().getIntExtra("REQUEST_CODE", 0)) {

            case REQUEST_NEW_TASK:
                Log.d("Bug", "i'm creating a new one");
                task = new Task();
                break;


            case REQUEST_EDIT_TASK:
                Log.d("Bug", "i'm editing");
                task = LocalStore.findTaskById(
                        getIntent().getStringExtra(REQUEST_EXTRA_TASK_ID));
                Toast.makeText(getBaseContext(), "REQUEST_EDIT_TASK", Toast.LENGTH_LONG).show();
                if (task.getId() != null) {

                    taskTitle.setText(String.valueOf(task.getTitle()));
                    taskTitle.setText(task.getTitle());
                    taskDate.setText(task.getTaskDate());
                    taskDesc.setText(task.getDesc());
                    taskTime.setText(task.getTaskTime());
                    Log.i("Bug", "Edit Task" + task.getTaskDate().toString());
                    Toast.makeText(getBaseContext(), "Edit Task", Toast.LENGTH_LONG).show();
                }
                break;
        }

        // args.putString(REQUEST_EXTRA_TASK_ID, task.getId());
    }

    /*
     * Sets up the listeners.
     */
    private void initListeners() {
        taskTitle.addTextChangedListener(this);
        taskDesc.addTextChangedListener(this);
        taskDate.addTextChangedListener(this);
        taskTime.addTextChangedListener(this);
    }


    /*
     * Checks if the required information about the
     * hive is given by the user. It also displays
     * a toast prompt message if something is missing.
     * @returns		true if all requirements are met,
     * 				otherwise return false
     */
    private boolean checkRequired() {

        if (task.getTitle().equals(new String())) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_prompt_task_title),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (alarm_on.isChecked()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 1000, pendingIntent);
            Toast.makeText(getBaseContext(), "Alarm", Toast.LENGTH_LONG).show();

        }

        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        showDialog(0);
    }

    /*
     * Overridden onCheckedChanged method to pass the state of the
     * alarm on/off button and disable/enable the date/time picker
     * based on the state
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        if (isChecked) {
            //enable date picker when Switch is ON
            taskTime.setEnabled(true);
            taskTime.setHint(sdf.format(new Date()));
        } else {
            //disable date picker when Switch is ON
            taskTime.setEnabled(false);
            taskTime.setHint("");
        }
    }

    public void showNotification() {

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(TaskActivity.this, TaskListFragment.class);
        PendingIntent pIntent = PendingIntent.getActivity(TaskActivity.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        if (Build.VERSION.SDK_INT < 16) {
            mNotification = new Notification.Builder(this)
                    .setContentTitle("Test!")
                    .setContentText("Test!")
                    .setSmallIcon(R.drawable.ic_action_edit)
                    .setContentIntent(pIntent)
                    .setSound(soundUri)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .getNotification();
        } else {
            mNotification = new Notification.Builder(this)
                    .setContentTitle("Test!")
                    .setContentText("Test!")
                    .setSmallIcon(R.drawable.ic_action_edit)
                    .setContentIntent(pIntent)
                    .setSound(soundUri)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, mNotification);
    }

    private void RegisterAlarmBroadcast() {
            /*
             * This is the call back function(BroadcastReceiver) which will be called when the
	         * alarm time will reached.
	         */
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showNotification();
            }
        };

        // register the alarm broadcast here
        registerReceiver(mReceiver, new IntentFilter(""));
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(""), 0);
        alarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void addButtonListener() {

        taskTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDialog(TIME_DIALOG_ID);
                } else {
                    //Hide your calender here
                }
            }
        });
        taskDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDialog(DATE_DIALOG_ID);
                } else {

                }
            }
        });


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, timePickerListener, startHour, startMinute, false);
                timePickerDialog.setTitle(getResources().getString(R.string.toast_time_picker));
                timePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,
                        getString(R.string.action_done), timePickerDialog);
                timePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE,
                        getString(R.string.action_cancel), timePickerDialog);
                return timePickerDialog;

            case DATE_DIALOG_ID:

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePickerListener, startYear, startMonth, startDay);
                datePickerDialog.setTitle(startDay + " " + startMonth + " " + startYear);
                datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,
                        getString(R.string.action_done), datePickerDialog);
                datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE,
                        getString(R.string.action_cancel), datePickerDialog);
                return datePickerDialog;
        }
        return null;

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            startHour = selectedHour;
            startMinute = selectedMinute;
            textViewTime = (TextView) findViewById(R.id.reminder_spinner);
            // set current time into text view
            taskTime.setText(new StringBuilder().append(padding_str(startHour)).append(":").append(padding_str(startMinute)));
            Log.d("system", "" + System.currentTimeMillis() + 1000);
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, startHour);
            cal.set(Calendar.MINUTE, startMinute);
            cal.set(Calendar.SECOND, 0);
            Log.d("user", "" + cal.getTimeInMillis() + 1000);

        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            //   edit_task_calendar.setText(new StringBuilder().append(startDay).append("/").append(startMonth + 1).append("/").append(startYear));
            taskDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / " + selectedYear);


        }
    };


    private String padding_str(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


}
