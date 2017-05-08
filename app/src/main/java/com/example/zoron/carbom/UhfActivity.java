package com.example.zoron.carbom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.ArrayList;

public class UhfActivity extends AppCompatActivity {
    private int id;
    private String rfid;
    private boolean scanned;
    private ArrayList<Fragment> fragmentList;

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("ActionBar", e.toString());
        }

        setContentView(R.layout.activity_uhf);

        id = getIntent().getIntExtra("id", 0);
        rfid = getIntent().getStringExtra("rfid");
        initViewPager();
        initNavigation();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_setting:
                Intent setting = new Intent(this, SettingActivity.class);
                startActivity(setting);
                return true;
            case R.id.menu_exit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getRfid() {
        return rfid;
    }

    private void initViewPager() {
        QueryFragment queryFragment = new QueryFragment();
        InFragment inFragment = new InFragment();
        OutFragment outFragment = new OutFragment();
        BorrowFragment borrowedFragment = new BorrowFragment();
        StockFragment stockFragment = new StockFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(MainActivity.FG_ID.QUERY.ordinal(), queryFragment);
        fragmentList.add(MainActivity.FG_ID.IN.ordinal(), inFragment);
        fragmentList.add(MainActivity.FG_ID.OUT.ordinal(), outFragment);
        fragmentList.add(MainActivity.FG_ID.BORROWED.ordinal(), borrowedFragment);
        fragmentList.add(MainActivity.FG_ID.STOCK.ordinal(), stockFragment);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(id).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(id);
    }

    private void initNavigation() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_query:
                        viewPager.setCurrentItem(MainActivity.FG_ID.QUERY.ordinal());
                        return true;
                    case R.id.navigation_in:
                        viewPager.setCurrentItem(MainActivity.FG_ID.IN.ordinal());
                        return true;
                    case R.id.navigation_out:
                        viewPager.setCurrentItem(MainActivity.FG_ID.OUT.ordinal());
                        return true;
                    case R.id.navigation_borrowed:
                        viewPager.setCurrentItem(MainActivity.FG_ID.BORROWED.ordinal());
                        return true;
                    case R.id.navigation_stock:
                        viewPager.setCurrentItem(MainActivity.FG_ID.STOCK.ordinal());
                        return true;
                }
                return false;
            }
        });
        navigation.setSelectedItemId(id);
    }

    private void initData() {

    }
}
