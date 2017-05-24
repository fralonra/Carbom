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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scan, container, false);
        listViewData = (ListView) view.findViewById(R.id.listView_data);
        msg = (TextView) view.findViewById(R.id.msg);
        setMsgText();

        final Button scan = (Button) view.findViewById(R.id.scan);
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

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiChoice && multiListMap.size() > 1)
                    listMap = multiListMap;
                parentActivity.scanFinish(listMap, multiChoice);
            }
        });
        return view;
    }

    @Override
    protected void setListView() {
        if (!listMap.isEmpty()) {
            msg.setVisibility(View.GONE);
            listViewData.setVisibility(View.VISIBLE);
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
                            if(((CheckBox)view).isChecked()){
                                if (++checkSum > 1) {
                                    if (!multiChoice) {
                                        multiChoice = true;
                                        multiListMap = new ArrayList<>();
                                    }
                                    if (multiListMap != null) {
                                        multiListMap.add(map);
                                    }
                                }
                            }else{
                                if (--checkSum < 2)
                                    multiChoice = false;
                                if (multiListMap != null && multiListMap.contains(map)) {
                                    multiListMap.remove(map);
                                }
                            }
                        }
                    });
                    return view;
                }
                };
            listViewData.setAdapter(adapter);
        }
    }
}
