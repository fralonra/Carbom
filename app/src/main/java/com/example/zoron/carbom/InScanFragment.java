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

public class InScanFragment extends ScanFragment {

    @Override
    protected void setMsgText() {
        msg.setText(R.string.msg_no_update);
    }

    @Override
    protected void addData( List<String> list) {
        listMap.clear();
        int idcount = 1;
        for (String epc : list) {
            if (!hasEPC(epc)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("ID", Integer.toString(idcount));
                map.put("EPC", epc);
                idcount++;
                listMap.add(map);
            }
        }
        setListView();
    }
}
