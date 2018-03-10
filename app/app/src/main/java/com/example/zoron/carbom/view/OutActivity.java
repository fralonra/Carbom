package com.example.zoron.carbom.view;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.misc.*;

public class OutActivity extends BaseActivity {

    @Override
    protected void newFragment(final int i) {
        Utils.hideFragment(fm, firstFragment);
        Utils.removeFragment(fm, dataFragment);

        dataFragment = new OutFragment();
        dataFragment.init(listMap.get(i).get("EPC"), i);

        Utils.showFragment(fm, R.id.container, dataFragment);
    }
}
