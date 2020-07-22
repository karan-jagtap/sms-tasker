package com.miniproject.smstasker.helper;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.miniproject.smstasker.classes.SentMessage;
import com.miniproject.smstasker.classes.TaskClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class PhoneStateReceiver extends BroadcastReceiver {

    private MySQLiteDBClass db;
    private SimpleDateFormat timeFormat;
    private Calendar currentCalendar, fromCalendar, toCalendar;
    private ArrayList<TaskClass> taskerArrayList;
    private Context context;
    private SentMessage sentMessage;
    private static boolean ringing = false, callReceived = false;

    public PhoneStateReceiver() {
    }

    //this will get called when user gets phone call
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.context = context;
            Log.i("SMSTasker", "onReceive() out");
            if (Objects.requireNonNull(intent.getAction()).equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                Log.i("SMSTasker", "onReceive() in");
                db = new MySQLiteDBClass(context);
                sentMessage = new SentMessage();
                taskerArrayList = new ArrayList<>();
                currentCalendar = Calendar.getInstance();
                fromCalendar = Calendar.getInstance();
                toCalendar = Calendar.getInstance();
                String dateTime = currentCalendar.get(Calendar.DAY_OF_MONTH)
                        + "-" + currentCalendar.get(Calendar.MONTH)
                        + "-" + currentCalendar.get(Calendar.YEAR) + " ";

                sentMessage.setDateTime(dateTime + updateTime(currentCalendar));
                timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                taskerArrayList = db.getRecordForReceiver();
                try {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        ringing = true;
                    }
                    if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                        callReceived = true;
                    }
                    if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        if (ringing && !callReceived) {
                            //send message from here
                            if (!taskerArrayList.isEmpty()) {
                                try {
                                    checkTimingsAndSendSMS(incomingNumber);
                                } catch (Exception e) {
                                    Log.i("SMSTasker", "" + e.getMessage());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.i("SMSTasker", "" + e.getMessage());
        }
    }

    private void checkTimingsAndSendSMS(String incomingNumber) throws ParseException {
        currentCalendar.setTime(timeFormat.parse(
                currentCalendar.get(Calendar.HOUR_OF_DAY) + ":" + currentCalendar.get(Calendar.MINUTE)));
        for (TaskClass taskClass : taskerArrayList) {
            fromCalendar.setTime(timeFormat.parse(taskClass.getFromTimeData()));
            toCalendar.setTime(timeFormat.parse(taskClass.getToTimeData()));

            if (fromCalendar.after(toCalendar) && currentCalendar.before(toCalendar)) {
                currentCalendar.add(Calendar.DATE, 1);
                toCalendar.add(Calendar.DATE, 1);
            } else if (fromCalendar.after(toCalendar) && (currentCalendar.after(fromCalendar) && currentCalendar.after(toCalendar))) {
                toCalendar.add(Calendar.DATE, 1);
            }

            if (currentCalendar.after(fromCalendar) && currentCalendar.before(toCalendar)) {
                sentMessage.setName(getContactDisplayNameByNumber(incomingNumber));
                sentMessage.setNumber(incomingNumber);
                sentMessage.setMessage(taskClass.getMessageData());
                db.insertSentMesssageRecord(sentMessage);
                db.close();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendMultipartTextMessage(
                            incomingNumber,
                            null,
                            smsManager.divideMessage(taskClass.getMessageData()),
                            null,
                            null);
                    Toast.makeText(context, "SMS Sent!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.i("SMSTasker", "" + e.getMessage());
                }
            }
        }

    }

    private String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            } else {
                name = "UNKNOWN NUMBER";
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    //to convert the 24-hour format to 12-hour format for user to view on screen
    private String updateTime(Calendar currentCalendar) {
        String timeSet;
        int hours, mins;
        hours = currentCalendar.get(Calendar.HOUR_OF_DAY);
        mins = currentCalendar.get(Calendar.MINUTE);
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

        return (h + ":" + minutes + " " + timeSet);
    }
}
