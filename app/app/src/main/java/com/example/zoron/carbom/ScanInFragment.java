package com.example.zoron.carbom;

import com.android.hdhe.uhf.entity.EPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zoron on 17-4-15.
 */

public class ScanInFragment extends ScanFragment {

    @Override
    protected void setMsgText() {
        msg.setText(R.string.msg_no_update);
    }

    @Override
    protected void addData(final int pos, final String epc) {
        if (!hasEPC(epc)) {
            Map<String, String> map = new HashMap<>();
            map.put("ID", "");
            map.put("EPC", epc);
            listEPC.add(pos, epc);
            listMap.add(pos, map);
            Utils.play(1, 0);
            setListView();
        }
    }
}
