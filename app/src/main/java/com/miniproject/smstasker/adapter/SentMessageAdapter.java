package com.miniproject.smstasker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.miniproject.smstasker.R;
import com.miniproject.smstasker.classes.SentMessage;

import java.util.ArrayList;

public class SentMessageAdapter extends ArrayAdapter {

    private ArrayList<SentMessage> sentMessageArrayList;
    private Activity context;

    public SentMessageAdapter(@NonNull Activity context, ArrayList<SentMessage> sentMessageArrayList) {
        super(context, R.layout.layout_sent_message, sentMessageArrayList);
        this.sentMessageArrayList = sentMessageArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder")
        View view = layoutInflater.inflate(R.layout.layout_sent_message, parent, false);
        SentMessage sentMessage = sentMessageArrayList.get(position);
        TextView name = view.findViewById(R.id.textView_name_STATUS);
        TextView number = view.findViewById(R.id.textView_number_STATUS);
        TextView message = view.findViewById(R.id.textView_message_STATUS);
        TextView dateTime = view.findViewById(R.id.textView_date_time_STATUS);
        if (sentMessage != null) {
            name.setText(sentMessage.getName());
            number.setText(sentMessage.getNumber());
            message.setText(sentMessage.getMessage());
            dateTime.setText(sentMessage.getDateTime());
        }
        return view;
    }
}
