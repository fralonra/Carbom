package com.example.zoron.carbom.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.zoron.carbom.data.CsvReader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zoron on 18-3-11.
 */

public class BackupService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 6) {
            CsvReader reader = new CsvReader(CsvReader.SAVE_FILE);

            final String fileName = "data_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date()) + ".cbm";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            reader.writeToFile(file);
        }
    }
}
