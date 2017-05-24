package com.example.zoron.carbom;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by zoron on 17-4-13.
 */

public abstract class BaseActivity extends AppCompatActivity implements ScanFragment.OnScanFinishedListener,
        BaseFragment.OnClickListener {
    protected ArrayList<Map<String, String>> listMap;
    protected int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("ActionBar", e.toString());
        }

        setContentView(R.layout.activity_base);
        firstFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void scanFinish(final ArrayList<Map<String, String>> list, final boolean multichoice) {
        listMap = list;
        initFragment(index);
    }

    public void leftClick(final int index) {
        initFragment(index);
    }

    public void rightClick(final int index) {

    }

    protected void firstFragment() {
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, new ScanFragment());
    }

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
            }, 2500);
        }
    }

    protected void newFragment(final int i) {
    }
}
