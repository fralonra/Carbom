package com.example.zoron.carbom.view;

import android.widget.SimpleAdapter;

import com.android.hdhe.uhf.entity.EPC;
import com.example.zoron.carbom.R;
import com.example.zoron.carbom.misc.Utils;

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
        Map<String, String> map = new HashMap<>();
        map.put("ID", "");
        map.put("EPC", epc);
        if (hasEPC(epc)) map.put("IN", getResources().getString(R.string.in_stock));
        else map.put("IN", getResources().getString(R.string.out_stock));
        listEPC.add(pos, epc);
        listMap.add(pos, map);
        Utils.play(1, 0);
        setListView();
    }

    @Override
    protected void setAdapter() {
        adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_item,
                new String[]{"ID", "EPC", "IN"},
                new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_in});
    }
}
