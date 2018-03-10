package com.example.zoron.carbom.view;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.misc.*;

public class InActivity extends BaseActivity {

    @Override
    protected void firstFragment() {
        if (firstFragment == null) firstFragment = new ScanInFragment();
        Utils.replaceFragment(fm, R.id.container, firstFragment, "FIRST");
    }

    @Override
    protected void newFragment(final int i) {
        Utils.hideFragment(fm, firstFragment);
        Utils.removeFragment(fm, dataFragment);

        dataFragment = new InFragment();
        dataFragment.init(listMap.get(i).get("EPC"), i);

        Utils.showFragment(fm, R.id.container, dataFragment);
    }
}
