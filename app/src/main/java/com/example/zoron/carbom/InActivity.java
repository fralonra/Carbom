package com.example.zoron.carbom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class InActivity extends BaseActivity {

    @Override
    protected void firstFragment() {
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, new InScanFragment());
    }

    @Override
    protected void newFragment(final int i) {
        InFragment fragment = new InFragment();
        fragment.init(listMap.get(i).get("EPC").toString(), i);
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, fragment);
    }
}
