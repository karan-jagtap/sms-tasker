package com.miniproject.smstasker.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miniproject.smstasker.classes.SentMessage;
import com.miniproject.smstasker.classes.TaskClass;

import java.util.ArrayList;

public class MySQLiteDBClass extends SQLiteOpenHelper {

    //defining constant keys so that while programming spelling mistakes will be avoided
    private static final String DATABASE_NAME = "SMSTasker";
    private static final String KEY_TIMER_NAME = "timer_name";
    private static final String KEY_FROM_TIME = "from_time";
    private static final String KEY_TO_TIME = "to_time";
    private static final String KEY_MESSAGE_BODY = "message_body";
    private static final String TABLE_NAME = "timer_list";
    private static final String TABLE_SENT_MESSAGE = "sent_message";
    private static final String KEY_NAME = "contact_name";
    private static final String KEY_NUMBER = "contact_number";
    private static final String KEY_DATE_TIME = "date_time";
    private static final String KEY_ID = "id";

    //constructor to create instance of this class
    public MySQLiteDBClass(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //gets called when the application is installed
    //it is not called when the application is opened for the next time
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create table query
        String createTableTask = "CREATE TABLE '" + TABLE_NAME + "' ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TIMER_NAME + " TEXT, "
                + KEY_FROM_TIME + " TIME, "
                + KEY_TO_TIME + " TIME, "
                + KEY_MESSAGE_BODY + " TEXT );";
        //executing the query
        sqLiteDatabase.execSQL(createTableTask);

        //create table sent message query
        String createTableSentMessage = "CREATE TABLE '" + TABLE_SENT_MESSAGE + "' ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_NUMBER + " TEXT, "
                + KEY_MESSAGE_BODY + " TEXT, "
                + KEY_DATE_TIME + " DATETIME "
                + ")";
        //executing the query
        sqLiteDatabase.execSQL(createTableSentMessage);
    }

    //gets called when newer version of application is installed
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //before updating the table, first delete the old one and then create a new one
        String sql = "DROP TABLE IF EXISTS '" + TABLE_NAME + "';";
        sqLiteDatabase.execSQL(sql);//this line deletes the table if it is present
        String sql2 = "DROP TABLE IF EXISTS '" + TABLE_SENT_MESSAGE + "';";
        sqLiteDatabase.execSQL(sql2);//this line deletes the table if it is present
        onCreate(sqLiteDatabase);//this line creates the table anyways
    }

    //only inserts the task name just to show it in the main listView in MainActivity.java
    public void insertTableRecord(String timerName) {
        SQLiteDatabase db = getWritableDatabase();// getWritableDatabase() to perform write operation
        ContentValues contentValues = new ContentValues();
        //data is inserted in key-value pair
        //i.e. "timer_name","College"
        contentValues.put(KEY_TIMER_NAME, timerName);
        // now that we have the value, insert it
        db.insert(TABLE_NAME, null, contentValues);
        db.close();//closing the database after our work is done
    }

    //inserts the remaining record associated with the timer name
    public void insertTotalTableRecord(String timerName, String fromTimeData, String toTimeData, String messageData) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FROM_TIME, fromTimeData);
        contentValues.put(KEY_TO_TIME, toTimeData);
        contentValues.put(KEY_MESSAGE_BODY, messageData);
        //as we have already inserted the name of the task into database, we just have to fill in
        //the remaining columns by adding from time, to time and message data
        db.update(TABLE_NAME, contentValues, KEY_TIMER_NAME + " = ?", new String[]{timerName});
        db.close();
    }

    //inserts the sent message record
    public void insertSentMesssageRecord(SentMessage sentMessage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, sentMessage.getName());
        contentValues.put(KEY_NUMBER, sentMessage.getNumber());
        contentValues.put(KEY_MESSAGE_BODY, sentMessage.getMessage());
        contentValues.put(KEY_DATE_TIME, sentMessage.getDateTime());
        //now that the sms is sent and we have to show it in the "Sent Message" tab,
        //we have to store the data into the database using below line
        db.insert(TABLE_SENT_MESSAGE, null, contentValues);
        db.close();
    }

    //only retrieves the timer name to show it in the listView of MainActivity.java
    public ArrayList<String> getListOfRecords() {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " + KEY_TIMER_NAME + " FROM '" + TABLE_NAME + "' ;";
        //cursor is used to go through all rows in table one by one
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return arrayList;
    }

    //function for displaying the already saved details in db when users clicks on listView in
    // MainActivity.java and goes to DetailedActivity.java
    public TaskClass getParticularRecord(String timerName) {
        TaskClass taskClass;
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM '" + TABLE_NAME + "' WHERE " + KEY_TIMER_NAME + " = '" + timerName + "' ;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToNext();
        taskClass = new TaskClass(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
        db.close();
        return taskClass;
    }

    //when you will get a call at any time, the background activity PhoneStateReceiver will call this
    // function to get all the records present in the table
    public ArrayList<TaskClass> getRecordForReceiver() {
        ArrayList<TaskClass> arrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM '" + TABLE_NAME + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new TaskClass(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return arrayList;
    }

    //gets the list of sent messages from table through this function
    public ArrayList<SentMessage> getRecordForSentMessages() {
        ArrayList<SentMessage> arrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM '" + TABLE_SENT_MESSAGE + "' ORDER BY " + KEY_ID + " DESC;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new SentMessage(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return arrayList;
    }

    //this will be called when "Delete" button will be pressed
    public void deleteARecord(String timerName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, KEY_TIMER_NAME + " = ?", new String[]{timerName});
        db.close();
    }

    //this will be called when user deletes the sent message by long press
    public void deleteSentMessageRecord(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SENT_MESSAGE, KEY_ID + " = ?", new String[]{id});
        db.close();
    }
}
