package com.example.zoron.carbom;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class BorrowActivity extends BaseActivity {

    @Override
    public void scanFinish(final ArrayList<Map<String, String>> list, final boolean multichoice) {
        listMap = list;
        //Log.d("choice", Boolean.toString(multichoice));
        if (!multichoice) {
            initFragment(index);
        } else {
            initMultiFragment(list);
        }
    }

    @Override
    protected void firstFragment() {
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, new ScanBorrowFragment());
    }

    @Override
    protected void initFragment(final int index) {
        if (index < listMap.size()) {
            newFragment(index);
        } else {
            Toast toast = Toast.makeText(this, "无更多项，即将返回主界面", Toast.LENGTH_SHORT);
            toast.show();
            Handler homeHandler = new Handler();
            homeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
        }
    }

    @Override
    protected void newFragment(final int i) {
        BorrowFragment fragment = new BorrowFragment();
        fragment.init(listMap.get(i).get("EPC"), i);
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, fragment);
    }

    private void initMultiFragment(final ArrayList<Map<String, String>> list) {
        BorrowMultiFragment fragment = new BorrowMultiFragment();
        fragment.init(list);
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, fragment);
    }
}
