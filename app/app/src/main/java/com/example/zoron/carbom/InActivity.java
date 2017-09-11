package com.example.zoron.carbom;

public class InActivity extends BaseActivity {

    @Override
    protected void firstFragment() {
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, new ScanInFragment());
    }

    @Override
    protected void newFragment(final int i) {
        InFragment fragment = new InFragment();
        fragment.init(listMap.get(i).get("EPC"), i);
        MainActivity.replaceFragment(getSupportFragmentManager(),
                R.id.container, fragment);
    }
}
