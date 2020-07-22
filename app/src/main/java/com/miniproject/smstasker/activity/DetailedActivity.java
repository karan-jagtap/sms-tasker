package com.miniproject.smstasker.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.miniproject.smstasker.R;
import com.miniproject.smstasker.classes.TaskClass;
import com.miniproject.smstasker.helper.MySQLiteDBClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class DetailedActivity extends AppCompatActivity implements View.OnClickListener {

    //data
    private MySQLiteDBClass db;
    private TaskClass taskClass;
    private String timerName;
    private String fromTimeData, toTimeData, messageData;

    //components
    private Button fromButton, toButton, deleteButton, submitButton;
    private TextView fromTextView, toTextView, counterTextView;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Toolbar toolbar = findViewById(R.id.toolbar_DetailedActivity);
        setSupportActionBar(toolbar);

        /**
         *
         * Getting the data sent from "MainActivity.java" to this activity using intent
         * because we sent it using intent
         *
         * **/
        Intent intent = getIntent();
        timerName = intent.getStringExtra("timer_name");

        //set the title of the screen to the task name
        Objects.requireNonNull(getSupportActionBar()).setTitle(timerName);

        //enables the back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //assigning all xml ids to views
        declarations();
        //update the view into the screen from database just like previous activity
        updateFromDBToView();
        //listeners to handle component actions performed
        eventListeners();
    }

    @SuppressLint("SetTextI18n")
    private void updateFromDBToView() {
        //getting the records from database to "taskClass" object which we have created
        taskClass = db.getParticularRecord(timerName);
        if (!taskClass.isEmpty()) {
            messageData = taskClass.getMessageData();
            messageEditText.setText(messageData);
            int sms = messageEditText.length() / 160;
            float d = (float) messageEditText.length() / 160;
            double textLeftt = 160 * (1 - (d - sms));
            int textLeft = (int) Math.round(textLeftt);
            int smsNo = sms + 1;
            counterTextView.setText(smsNo + "/" + textLeft);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            try {
                Date fromDate = dateFormat.parse(taskClass.getFromTimeData());
                fromTimeData = dateFormat.format(fromDate);
                updateTime(fromDate, "from");

                Date toDate = dateFormat.parse(taskClass.getToTimeData());
                toTimeData = dateFormat.format(toDate);
                updateTime(toDate, "to");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //show these things if the user has opened this activity for the first time
            fromTimeData = toTimeData = "";
            messageEditText.setText("");
            fromTextView.setText("N.A.");
            toTextView.setText("N.A.");
            //disabling the delete button because we dont have anything to delete yet
            // we will be enabling it again once we have data stored into database
            counterTextView.setText("1/160");
        }
    }

    private void eventListeners() {
        fromButton.setOnClickListener(this);
        toButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        final TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @SuppressLint("SetTextI18n")
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int sms = s.length() / 160;
                float d = (float) s.length() / 160;
                double textLeftt = 160 * (1 - (d - sms));
                int textLeft = (int) Math.round(textLeftt);
                int smsNo = sms + 1;
                counterTextView.setText(smsNo + "/" + textLeft);
            }

            public void afterTextChanged(Editable s) {
            }
        };
        messageEditText.addTextChangedListener(watcher);
    }

    private void declarations() {
        fromButton = findViewById(R.id.button_set_from_timer_DetailedActivity);
        toButton = findViewById(R.id.button_set_to_timer_DetailedActivity);
        deleteButton = findViewById(R.id.button_delete_DetailedActivity);
        submitButton = findViewById(R.id.button_submit_DetailedActivity);
        fromTextView = findViewById(R.id.textView_set_from_timer_DetailedActivity);
        toTextView = findViewById(R.id.textView_set_to_timer_DetailedActivity);
        messageEditText = findViewById(R.id.editText_message_body_DetailedActivity);
        counterTextView = findViewById(R.id.textView_counter);
        db = new MySQLiteDBClass(getApplicationContext());
        taskClass = new TaskClass();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == fromButton.getId() || view.getId() == toButton.getId()) {
            //when clicked on set time, then open the time picker where you can set time
            // by using the following function
            openTimePickerDialog(view);
        } else if (view.getId() == submitButton.getId()) {
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                //this means user has not granted us permission, ask for them now
                openPermissionInfoDialog();
            } else { //this means user has granted us permissions
                submitData();
            }
        } else if (view.getId() == deleteButton.getId()) {
            //delete the record of the particular task only
            db.deleteARecord(timerName);
            db.close();
            finish(); //closes this activity and returns to "MainActivity.java" onStart() function
            // now you will know why update() function in MainActivity.java is given in onStart()
            Toast.makeText(this, "Task '" + timerName + "' successfully deleted...!", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitData() {
        //validateData() - this function just checks that user has set from time, to time and
        // message body is not empty, it returns true if all are satisfied
        if (validateData()) {
            //so now we have all the data and now we only need to save it into the database
            db.insertTotalTableRecord(timerName, fromTimeData, toTimeData, messageData);
            db.close();
            finish();
            //as said before, once the data is stored into the database now we can make
            // the delete button visible
            Toast.makeText(this, "Submitted...!", Toast.LENGTH_SHORT).show();
        }
    }

    //shows the user a dialog with a message that why we need permissions
    public void openPermissionInfoDialog() {
        AlertDialog alertDialog = new AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.important))
                .setMessage(getString(R.string.permission_message))
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(DetailedActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .create();
        alertDialog.show();
    }

    private boolean validateData() {
        messageData = messageEditText.getText().toString().trim();
        if (fromTimeData.isEmpty()) {
            Toast.makeText(this, "Please specify the start time...!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (toTimeData.isEmpty()) {
            Toast.makeText(this, "Please specify the end time...!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (messageData.isEmpty()) {
            Toast.makeText(this, "Please Enter Message Body...!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openTimePickerDialog(final View view) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        //this timepicker is provided by android and we are just using it to get time
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                //we are telling the android timepicker the format we want our time in hh:mm 20:30
                // we will get the time in 24-hour format
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date date = new Date();
                date.setHours(h);
                date.setMinutes(m);
                if (view.getId() == fromButton.getId()) {
                    fromTimeData = dateFormat.format(date);
                    //refer the function description for updateTime() for details
                    updateTime(date, "from");
                } else {
                    toTimeData = dateFormat.format(date);
                    updateTime(date, "to");
                }
            }
        }, hour, min, false);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();
    }

    //to convert the 24-hour format to 12-hour format for user to view on screen
    private void updateTime(@NonNull Date date, String key) {
        String timeSet;
        int hours, mins;
        hours = date.getHours();
        mins = date.getMinutes();
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }
        String minutes;
        if (mins < 10) {
            minutes = "0" + mins;
        } else {
            minutes = String.valueOf(mins);
        }
        String h;
        if (hours < 10) {
            h = "0" + hours;
        } else {
            h = String.valueOf(hours);
        }
        if (key.equals("from")) {
            String fromTimeData = h + ":" + minutes + " " + timeSet;
            fromTextView.setText(fromTimeData);
        } else if (key.equals("to")) {
            String toTimeData = h + ":" + minutes + " " + timeSet;
            toTextView.setText(toTimeData);
        }
    }

    //for runtime permissions from marshmallow
    private final int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG};

    //checks if permission is given or not
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //this is executed when user clicks "allow" or "deny"
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            int x = 0;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    //if permission granted then save the data
                    // otherwise do nothing
                    x++;
                }
            }
            if (x == permissions.length) {
                submitData();
            }
        }
    }
}
