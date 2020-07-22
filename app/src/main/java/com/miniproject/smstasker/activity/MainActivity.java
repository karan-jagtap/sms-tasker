package com.miniproject.smstasker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miniproject.smstasker.R;
import com.miniproject.smstasker.adapter.ViewPagerAdapter;
import com.miniproject.smstasker.fragments.SentMessageFragment;
import com.miniproject.smstasker.fragments.TaskFragment;
import com.miniproject.smstasker.helper.MySQLiteDBClass;

public class MainActivity extends AppCompatActivity {

    //components
    private FloatingActionButton fabButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    //data
    private TaskFragment taskFragment;
    private SentMessageFragment sentMessageFragment;
    private MySQLiteDBClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_MainActivity);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         *
         * this function is called here because after deleting a task in "DetailedActivity.java"
         * it returns to MainActivity.java and then we need to update the view again for remaining
         * tasks from Database. So when we are back from DetailedActivity.java to MainActivity.java
         * onCreate() method is not called onStart() is called. Hence, update() function is used in here
         *
         * **/
        /**
         *
         * function to assign all xml ids to component objects
         *
         * **/
        declarations();

        /**
         *
         * function to register all component's listeners
         *
         * **/
        eventListeners();
    }

    private void declarations() {
        fabButton = findViewById(R.id.fab_button_MainActivity);
        tabLayout = findViewById(R.id.tabLayout_MainActivity);
        viewPager = findViewById(R.id.viewPager_MainActivity);
        taskFragment = new TaskFragment(MainActivity.this);
        sentMessageFragment = new SentMessageFragment(MainActivity.this);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(taskFragment, getString(R.string.task));
        adapter.addFragment(sentMessageFragment, getString(R.string.sent_messages));
        viewPager.setAdapter(adapter);
    }

    private void eventListeners() {
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *
                 * This function opens the dialog to add the task name from user to database
                 *
                 * **/
                //taskFragment.openDialog();
                openDialogN();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fabButton.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        fabButton.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //creating the info menu from menu_main.xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //setting the action to be performed on its click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info) {
            openInfoDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    //showing the user how the app works
    private void openInfoDialog() {
        AlertDialog alertDialog = new AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.info))
                .setIcon(getResources().getDrawable(R.drawable.ic_info_outline))
                .setMessage(getString(R.string.info_message))
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)


                .create();
        alertDialog.show();
    }

    public void openDialogN() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_add_timer, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final EditText editText = dialogView.findViewById(R.id.editText_add_timer);
        final Button addButton = dialogView.findViewById(R.id.buttonAdd_add_timer);
        final Button cancelButton = dialogView.findViewById(R.id.buttonCancel_add_timer);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input_text = editText.getText().toString().trim();
                if (!input_text.isEmpty()) {
                    db = new MySQLiteDBClass(MainActivity.this);
                    //stores the name of task into database
                    db.insertTableRecord(input_text);
                    db.close();
                    //again update the screen to view the recently added task
                    //update();
                    //close the dialog
                    alertDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                    intent.putExtra("timer_name", input_text);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "New Timer added...!", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.show();
                    Toast.makeText(MainActivity.this, "Please Enter Timer Name...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }
}
