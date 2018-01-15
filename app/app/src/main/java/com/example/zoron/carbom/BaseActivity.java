package com.example.zoron.carbom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoron on 17-4-13.
 */

public abstract class BaseActivity extends AppCompatActivity implements ScanFragment.OnScanFinishedListener,
        BaseFragment.OnClickListener {
    protected static CsvReader reader = MainActivity.reader;
    protected ArrayList<Map<String, String>> listMap;
    protected int index = 0;

    protected FragmentManager fm = null;
    protected FragmentTransaction ft = null;
    protected ScanFragment firstFragment = null;
    protected BaseFragment dataFragment = null;

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

        Intent intent = getIntent();
        String epc = intent.getStringExtra("epc");

        if (fm == null) fm = getSupportFragmentManager();
        if (listMap == null) listMap = new ArrayList<>();
        if (epc != null) {
            Map<String, String> map = new HashMap<>();
            map.put("ID", "1");
            map.put("EPC", epc);
            listMap.add(map);
            scanFinish(listMap, false);
        } else {
            firstFragment();
        }
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

    public void scanFinish(final ArrayList<Map<String, String>> list, final int index) {
        listMap = list;
        initFragment(index);
    }

    public void leftClick(final int index) {
        initFragment(index);
    }

    public void rightClick(final int index) {}

    public void backToFirstFragment(Fragment old) {
        if (firstFragment == null) return;
        Utils.toggleFragment(fm, old, firstFragment);
    }

    public Fragment getFirstFragment() {
        return firstFragment;
    }

    protected void firstFragment() {
        if (firstFragment == null) firstFragment = new ScanFragment();
        Utils.showFragment(fm, R.id.container, firstFragment);
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
            }, 500);
        }
    }

    protected void newFragment(final int i) {
    }
}
