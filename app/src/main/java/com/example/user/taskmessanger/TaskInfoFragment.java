package com.example.user.taskmessanger;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TaskInfoFragment  extends Fragment {

    public static final String ARG_TASK_ID = "TASK_ID_INCR";
    private Task task;
    private TextView taskDescInfo,taskDateInfo,taskTitleInfo,taskTimeInfo;
    private Button taskButtonEdit,taskButtonDelete;
    private EditText taskTitle,taskDesc,taskDate,taskTime;


    /*
     * Overridden method of class Fragment.
     * It returns the view to be used for the fragment.
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_task_info, container, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        taskTitleInfo = (TextView) getView().findViewById(R.id.task_title);
        taskDateInfo = (TextView) getView().findViewById(R.id.task_date);
        taskDescInfo = (TextView) getView().findViewById(R.id.task_detail);
        taskTimeInfo = (TextView) getView().findViewById(R.id.task_time);

        task =  LocalStore.findTaskById(getArguments().getString(ARG_TASK_ID));
        taskTitleInfo.setText(task.getTitle());
        taskDateInfo.setText(  task.getTaskDate());
        taskTimeInfo.setText(task.getTaskTime());
        taskDescInfo.setText(task.getDesc());


        taskButtonEdit = (Button) getView().findViewById(R.id.task_edit_button);
        taskButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayIntentEdit();
            }
        });

        taskButtonDelete = (Button) getView().findViewById(R.id.task_delete_button);
        taskButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    /**
     *
     *
     * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
     * inflater.inflate(R.menu.actions_basic, menu);
     * super.onCreateOptionsMenu(menu, inflater);
     * }
     */

    public  void displayIntentEdit(){

        taskTitle = (EditText) getActivity().findViewById(R.id.edit_task_name);
        taskDesc = (EditText) getActivity().findViewById(R.id.desc_edit_text);
        taskDate = (EditText) getActivity().findViewById(R.id.edit_task_calendar);
        taskTime = (EditText) getActivity().findViewById(R.id.reminder_spinner);

        Log.d("Bug", task.getId());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(TaskActivity.RESULT_EXTRA_TASK_ID, task.getId());
        startActivityForResult(intent, TaskActivity.REQUEST_NEW_TASK);


    }
}
