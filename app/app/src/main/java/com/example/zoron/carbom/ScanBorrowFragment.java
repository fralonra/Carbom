package com.example.zoron.carbom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoron on 17-5-11.
 */

public class ScanBorrowFragment extends ScanFragment {
    private ArrayList<Map<String, String>> multiListMap;

    @Override
    protected View setView(final LayoutInflater inflater, final ViewGroup container, final int layout) {
        return inflater.inflate(R.layout.fragment_scan_borrow, container, false);
    }

    @Override
    protected void okClick() {
        if (multiChoice)
            listMap = multiListMap;
        parentActivity.scanFinish(listMap, multiChoice);
        for (Map<String, String> m : listMap) {
            Log.d("xxtt", m.toString());
        }
    }

    protected void setAdapter() {
        adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_item_borrow,
                new String[]{"ID", "EPC"},
                new int[]{R.id.textView_id, R.id.textView_epc}) {
            int checkSum = 0;

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckBox checkBox = (CheckBox)view.findViewById(R.id.check);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final HashMap<String, String> map = (HashMap<String, String>) getItem(position);
                        if (multiListMap == null) {
                            multiListMap = new ArrayList<>();
                        }
                        if(((CheckBox)view).isChecked()){
                            if (++checkSum > 0) {
                                if (!multiChoice) multiChoice = true;
                            }
                            multiListMap.add(map);
                        } else {
                            if (--checkSum < 1) multiChoice = false;
                            multiListMap.remove(map);
                        }
                    }
                });
                return view;
            }
        };
    }
}
