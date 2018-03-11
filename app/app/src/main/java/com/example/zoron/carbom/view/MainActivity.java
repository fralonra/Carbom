package com.example.zoron.carbom.view;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.data.*;
import com.example.zoron.carbom.service.BackupService;

public class MainActivity extends AppCompatActivity implements UncaughtExceptionHandler, View.OnClickListener {
    public enum FG_ID {
        QUERY,
        IN,
        OUT,
        BORROWED,
        STOCK
    }

    protected static CsvReader reader = new CsvReader(CsvReader.SAVE_FILE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(this);

        initTask();

        setContentView(R.layout.activity_main);

        Button query = (Button) findViewById(R.id.query);
        Button in = (Button) findViewById(R.id.in);
        Button out = (Button) findViewById(R.id.out);
        Button borrowed = (Button) findViewById(R.id.borrowed);
        Button stocktaking = (Button) findViewById(R.id.stock);

        query.setOnClickListener(this);
        in.setOnClickListener(this);
        out.setOnClickListener(this);
        borrowed.setOnClickListener(this);
        stocktaking.setOnClickListener(this);
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        Log.d("AAA", "uncaughtException   " + arg1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_exit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.query:
                intent = new Intent(this, QueryActivity.class);
                startActivity(intent);
                break;
            case R.id.in:
                intent = new Intent(this, InActivity.class);
                intent.putExtra("id", FG_ID.IN.ordinal());
                startActivity(intent);
                break;
            case R.id.out:
                intent = new Intent(this, OutActivity.class);
                startActivity(intent);
                break;
            case R.id.borrowed:
                intent = new Intent(this, BorrowActivity.class);
                intent.putExtra("id", FG_ID.BORROWED.ordinal());
                startActivity(intent);
                break;
            case R.id.stock:
                intent = new Intent(this, StockActivity.class);
                intent.putExtra("id", FG_ID.STOCK.ordinal());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initTask() {
        Intent taskIntent = new Intent(this, BackupService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, taskIntent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        if (am != null) {
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }
}
