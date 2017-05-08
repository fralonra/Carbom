package com.example.zoron.carbom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import java.util.ArrayList;
import java.util.Map;

public class OutActivity extends BaseActivity {

    @Override
    protected void newFragment(final int i) {
        OutFragment fragment = new OutFragment();
        fragment.init(listMap.get(i).get("EPC").toString(), i);
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, fragment);
    }
}
