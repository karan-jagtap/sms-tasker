package com.miniproject.smstasker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.miniproject.smstasker.R;
import com.miniproject.smstasker.adapter.SentMessageAdapter;
import com.miniproject.smstasker.classes.SentMessage;
import com.miniproject.smstasker.helper.MySQLiteDBClass;

import java.util.ArrayList;

public class SentMessageFragment extends Fragment {

    private ListView listView;
    private Context context;
    private MySQLiteDBClass db;
    private ArrayList<SentMessage> sentMessageArrayList;

    public SentMessageFragment() {
    }

    @SuppressLint("ValidFragment")
    public SentMessageFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);
        listView = view.findViewById(R.id.listView_timers_fragment_main_activity);
        db = new MySQLiteDBClass(context);
        update();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!adapterView.getItemAtPosition(i).toString().equals("No Sent Messages data found.")) {
                    Toast.makeText(context, "Long Press to delete entry...!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //For deleting the timer
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!adapterView.getItemAtPosition(i).toString().equals("No Sent Messages data found.")) {
                    deleteDialog(sentMessageArrayList.get(i).getId());
                }
                return true;
            }
        });
        return view;
    }

    //delete dialog to delete a single message record from db
    private void deleteDialog(final String id) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle(R.string.delete)
                .setMessage("Do you want to Delete it?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db = new MySQLiteDBClass(context);
                        db.deleteSentMessageRecord(id);
                        sentMessageArrayList.clear();
                        update();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.show();
    }

    public void update() {
        sentMessageArrayList = db.getRecordForSentMessages();
        if (sentMessageArrayList.isEmpty()) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("No Sent Messages data found.");
            ArrayAdapter adapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    arrayList);
            listView.setAdapter(adapter);
        } else {
            SentMessageAdapter adapter = new SentMessageAdapter(getActivity(), sentMessageArrayList);
            listView.setAdapter(adapter);
        }
    }
}
