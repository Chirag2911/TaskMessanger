package com.example.user.taskmessanger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;


public class TaskAdapter extends ArrayAdapter<Task> {

    private static final int RESOURCE_ID = R.layout.task_list_view_item;
    private View view;
    private Context context;
    /*
     * Constructor of custom adapter TaskAdapter.
     * @param	context		the context of the application
     * @param	data		the data (usually an ArrayList) for the adapter
     * 						to handle
     * @return	a new instance of TaskAdapter
     */
    public TaskAdapter(Context context, List<Task> data) {
        super(context, RESOURCE_ID, data);
        this.context = context;

    }

    /*
     * Overridden method of class ArrayAdapter<T>.
     * We construct the view for each item in the list.
     * The layout for each item is declared in the file
     * id passed to the RESOURCE_ID member constant. The
     * last item in the list serves as an action to add a
     * new task, thus its view is different from the
     * rest.
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @SuppressLint("SimpleDateFormat")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        context = parent.getContext();
        view = convertView;

        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        //SimpleDateFormat df = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        if(position == (getCount() - 1)) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_last_item, null);
            TextView addItemTextView = (TextView) view.findViewById(R.id.add_item_text_view);
            addItemTextView.setText(context.getString(R.string.action_add_new_task));
        }
        else {
            view = LayoutInflater.from(context).inflate(RESOURCE_ID, null);
            TextView title = (TextView) view.findViewById(R.id.task_list_1);
            title.setText(getItem(position).getTitle());

            TextView taskDate = (TextView) view.findViewById(R.id.task_list_2);
            taskDate.setText(getItem(position).getTaskDate());
        }

        return view;
    }

    /*
     * Overridden method of class ArrayAdapter<T>.
     * It returns the count of the items in the List<Task>,
     * plus one to include the special item placed at the end
     * of the ListView.
     * @see android.widget.ArrayAdapter#getCount()
     */
    @Override
    public int getCount() {
        return (super.getCount() + 1);

    }

}
