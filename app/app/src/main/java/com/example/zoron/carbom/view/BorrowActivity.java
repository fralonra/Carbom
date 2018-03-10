package com.example.zoron.carbom.view;

import android.os.Handler;
import android.widget.Toast;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.misc.*;

import java.util.ArrayList;
import java.util.Map;

public class BorrowActivity extends BaseActivity {

    @Override
    public void scanFinish(final ArrayList<Map<String, String>> list, final boolean multichoice) {
        listMap = list;
        if (!multichoice) {
            initFragment(index);
        } else {
            initMultiFragment(list);
        }
    }

    @Override
    protected void firstFragment() {
        if (firstFragment == null) firstFragment = new ScanBorrowFragment();
        Utils.replaceFragment(getSupportFragmentManager(),
                R.id.container, firstFragment, "FIRST");
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
        Utils.hideFragment(fm, firstFragment);
        Utils.removeFragment(fm, dataFragment);

        dataFragment = new BorrowFragment();
        dataFragment.init(listMap.get(i).get("EPC"), i);

        Utils.showFragment(fm, R.id.container, dataFragment);
    }

    private void initMultiFragment(final ArrayList<Map<String, String>> list) {
        Utils.hideFragment(fm, firstFragment);

        BorrowMultiFragment fragment = new BorrowMultiFragment();
        fragment.init(list);

        Utils.showFragment(fm, R.id.container, fragment);
    }
}
