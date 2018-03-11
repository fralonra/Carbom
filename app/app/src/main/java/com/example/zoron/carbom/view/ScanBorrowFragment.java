package com.example.zoron.carbom.view;

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

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.data.*;
import com.example.zoron.carbom.misc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoron on 17-5-11.
 */

public class ScanBorrowFragment extends ScanFragment {
    private ArrayList<Map<String, String>> multiListMap = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxList = new ArrayList<>();

    @Override
    protected View setView(final LayoutInflater inflater, final ViewGroup container, final int layout) {
        return inflater.inflate(R.layout.fragment_scan_borrow, container, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = setView(inflater, container, R.layout.fragment_scan);
        listViewData = (ListView) view.findViewById(R.id.listView_data);
        msg = (TextView) view.findViewById(R.id.msg);
        setMsgText();

        final Button scan = (Button) view.findViewById(R.id.scan);
        final Button select_all = (Button) view.findViewById(R.id.select_all);
        final Button ok = (Button) view.findViewById(R.id.ok);
        setButtonClickable(ok, false);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stocking = !stocking;
                if (stocking) {
                    scan.setText(R.string.stop_scan);
                    if (ok.isClickable()) {
                        setButtonClickable(ok, false);
                    }
                } else {
                    scan.setText(R.string.start_scan);
                    if (!listMap.isEmpty()) {
                        setButtonClickable(ok, true);
                    }
                }
            }
        });
        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiChoice = !multiChoice;
                if (multiChoice) {
                    multiListMap = listMap;
                    for (CheckBox cb : checkBoxList) {
                        cb.setChecked(true);
                    }
                } else {
                    multiListMap.clear();
                    for (CheckBox cb : checkBoxList) {
                        cb.setChecked(false);
                    }
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okClick();
            }
        });
        return view;
    }

    @Override
    protected void okClick() {
        if (multiChoice)
            listMap = multiListMap;
        parentActivity.scanFinish(listMap, multiChoice);
    }

    @Override
    protected void addData(final int pos, final String epc) {
        if (hasEPC(epc)) {
            Map<String, String> map = new HashMap<>();
            map.put("ID", "");
            map.put("EPC", epc);
            map.put("KEEPER", csv.getValueByEpc(epc, Entry.INDEX.KEEPER));
            listEPC.add(pos, epc);
            listMap.add(pos, map);
            Utils.play(1, 0);
            setListView();
        }
    }

    @Override
    protected void setAdapter() {
        adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_item_borrow,
                new String[]{"ID", "EPC", "KEEPER"},
                new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_keeper}) {
            int checkSum = 0;

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckBox checkBox = (CheckBox)view.findViewById(R.id.check);
                final HashMap<String, String> map = (HashMap<String, String>) getItem(position);

                checkBoxList.add(checkBox);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (((CheckBox)view).isChecked()) {
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

    @Override
    protected void onListItemClick(final int position) {}
}
