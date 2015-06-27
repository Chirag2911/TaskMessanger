package com.example.user.taskmessanger;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;


public class TaskListFragment extends ListFragment
        implements AdapterView.OnItemLongClickListener, ActionMode.Callback {


    Activity activity;
    public boolean actionMode = false;
    List<Task> tasks;
    private TextView emptyText;
    private ListView list;

    /*
       * Overridden method of class ListFragment.
       * It just calls the parent method to create and
       * return the ListView for our fragment.
       * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
       */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


      //  View empty = getActivity().findViewById(android.R.id.empty);
      //  listView.setEmptyView(empty);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // ListView listView= getListView();

       return super.onCreateView(inflater, container, savedInstanceState);
     /*   View view = inflater.inflate(R.layout.list_fragment, container, false);
        emptyText = (TextView)view.findViewById(android.R.id.empty);
        list = (ListView)view.findViewById(android.R.id.list);
        list.setEmptyView(emptyText);
        list.setAdapter(null);
        tasks = TaskSharedPreferences.getTask(getActivity());
        // LocalStore.TASK_LIST.add();

        //   setListAdapter(new TaskAdapter(getActivity(),LocalStore.TASK_LIST));
        setListAdapter(new TaskAdapter(getActivity(), tasks));
        return view;*/
    }

    @Override
    public void setEmptyText(CharSequence text) {
        emptyText.setText(text);
    }

    /*
     * Overridden method of class ListFragment.
     * It sets the ListView adapter.
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tasks = TaskSharedPreferences.getTask(getActivity());
        TaskAdapter tasksAdapter = new TaskAdapter(getActivity(),tasks);

    //    getListView().setEmptyView(view.findViewById(android.R.id.empty));
        //    setListAdapter(null);

   //     else {
   setListAdapter(new TaskAdapter(getActivity(),LocalStore.TASK_LIST));
      //     setListAdapter(new TaskAdapter(getActivity(),tasks));
     //   }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("REQUEST_CODE", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    /*
     * Overridden method of class ListFragment.
     * Called when an activity previously started has finished
     * with a result. In this case it just notifies the adapter
     * of potential change to the data, when the NewTaskActivity
     * returns a result.
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((TaskAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /*
     * Overridden method of class ListFragment.
     * Called when a list item is clicked (tapped).
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView parent, View clickedView,
                                int position, long id) {

        if ((parent.getAdapter().getCount() -1) == position) {
            Intent intent = new Intent(getActivity(), TaskActivity.class);
            startActivityForResult(intent, TaskActivity.REQUEST_NEW_TASK);

        } else {

            if (actionMode) {
                return;
            }

            TaskInfoFragment taskInfo = new TaskInfoFragment();

            Task task = (Task) parent.getAdapter().getItem(position);
            Bundle args = new Bundle();

            args.putString(TaskInfoFragment.ARG_TASK_ID, task.getId());
            taskInfo.setArguments(args);

            getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.container, taskInfo)
                    .addToBackStack(null)
                    .commit();
        }

    }

    /*
     * Overridden method of interface OnItemLongClickListener.
     * Called when a list item is long clicked (tapped). It sets
     * the activity into action mode, showing the contextual action bar.
     * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View clickedView,
                                   int position, long id) {
        if ((parent.getAdapter().getCount() - 1) != position) {
            getActivity().startActionMode(this);
            return true;
        }
        return false;
    }

    /*
     * Overridden method of interface ActionMode.Callback.
     * Called when the action mode is started by the user's long
     * click (tap) on a list item. It inflates the menu with the
     * actions to use specifically when in action mode.
     * @see android.view.ActionMode.Callback#onCreateActionMode(android.view.ActionMode, android.view.Menu)
     */
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.fragment_task_list_action_mode, menu);
        return actionMode = true;
    }

    /*
     * Overridden method of interface ActionMode.Callback.
     * Called when an action is clicked (tapped) when in action mode.
     * Note that actions differ between normal and action mode.
     * @see android.view.ActionMode.Callback#onActionItemClicked(android.view.ActionMode, android.view.MenuItem)
     */
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                performActionDelete();
                return true;
        }

        return false;
    }

    /*
     * Overridden method of interface ActionMode.Callback.
     * Called when the user leaves the action mode. It resets the
     * list to its normal state.
     * @see android.view.ActionMode.Callback#onDestroyActionMode(android.view.ActionMode)
     */
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        for (int i = 0; i < (getListView().getCount() - 1); ++i) {
            getListView().setItemChecked(i, false);
        }
        getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
        actionMode = false;
    }

    /*
     * Overridden method of interface ActionMode.Callback.
     * Called when the action mode is created. It sets the list
     * to multiple selection mode.
     * @see android.view.ActionMode.Callback#onPrepareActionMode(android.view.ActionMode, android.view.Menu)
     */
    @Override
    public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        return true;
    }

    /*
     * This method is internal. It is called by onActionItemClicked
     * when the delete action has been clicked (tapped). It deletes
     * all the selected items on the list and notifies the adapter
     * that the data has changed. If nothing is selected, it displays
     * an appropriate toast message.
     */
    public void performActionDelete() {
        System.out.println();
        Log.d("Bug", "Items in list: " + getListView().getCheckedItemCount());
        if (getListView().getCheckedItemCount() > 0) {
            SparseBooleanArray checkedItems =
                    getListView().getCheckedItemPositions();
            for (int i = 0; i < getListView().getCount() - 1; ++i) {
                if (checkedItems.get(i)) {
                    getListView().setItemChecked(i, false);
                    ((TaskAdapter) getListAdapter())
                            .remove((Task) getListAdapter().getItem(i));
                }
            }
            ((TaskAdapter) getListAdapter()).notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(),
                   "toast_nothing_to_delete",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
