package com.miniproject.smstasker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.miniproject.smstasker.R;
import com.miniproject.smstasker.activity.DetailedActivity;
import com.miniproject.smstasker.activity.MainActivity;
import com.miniproject.smstasker.helper.MySQLiteDBClass;

import java.util.ArrayList;

public class TaskFragment extends Fragment {

    //components
    private ListView listView;

    //data
    private Context context;
    private ArrayList<String> timerNamesArrayList;

    //helper
    private MySQLiteDBClass db;

    public TaskFragment() {
    }

    @SuppressLint("ValidFragment")
    public TaskFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);
        listView = view.findViewById(R.id.listView_timers_fragment_main_activity);
        db = new MySQLiteDBClass(context);
        timerNamesArrayList = new ArrayList<>();
        update();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /**
                 *
                 * When any item from the listView is clicked then take the name of the item
                 * and pass it onto the next activity "DetailedActivity.java" using
                 * intent.putExtra() function
                 *
                 * **/
                String timerName = adapterView.getItemAtPosition(position).toString();
                if (!timerName.equals(getString(R.string.no_task_message))) {
                    Intent intent = new Intent(context, DetailedActivity.class);
                    intent.putExtra("timer_name", timerName);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    private void update() {
        timerNamesArrayList = db.getListOfRecords();
        /**
         *
         * If the arraylist is empty then it means that user has not yet added any sms task,
         * hence notify the user by giving the following message
         *
         * **/
        if (timerNamesArrayList.isEmpty()) {
            timerNamesArrayList.clear();
            timerNamesArrayList.add(getString(R.string.no_task_message));
        }

        /**
         *
         * This is how the data is added into the listView at runtime
         *
         * **/
        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, timerNamesArrayList);
        listView.setAdapter(adapter);

        /**
         *
         * Closing the database object when we are done using it
         *
         * **/
        db.close();
    }


}
