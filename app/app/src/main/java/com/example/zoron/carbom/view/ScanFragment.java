package com.example.zoron.carbom.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.data.*;
import com.example.zoron.carbom.misc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.zoron.carbom.misc.Utils.binarySearch;

/**
 * Created by zoron on 17-4-12.
 */

public class ScanFragment extends Fragment {
    protected OnScanFinishedListener parentActivity;
    protected InventoryThread thread;
    protected SimpleAdapter adapter;
    protected UhfReader reader;
    protected CsvReader csv;
    protected ArrayList<String> listEPC;
    protected ArrayList<Map<String, String>> listMap;

    protected boolean stocking = false;
    protected boolean multiChoice = false;

    protected TextView msg;
    protected ListView listViewData;

    public ScanFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            parentActivity = (OnScanFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnScanFinishedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        csv = BaseActivity.reader;

        reader = UhfReader.getInstance();
        listEPC = new ArrayList<>();
        listMap = new ArrayList<>();
        Utils.initSoundPool(getContext());

        if (thread == null) {
            thread = new InventoryThread();
            thread.start();
        }
    }

    @Override
    public void onDestroy() {
        if (reader != null) {
            reader.close();
        }
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = setView(inflater, container, R.layout.fragment_scan);
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
                okClick();
            }
        });
        return view;
    }

    public interface OnScanFinishedListener {
        public void scanFinish(final ArrayList<Map<String, String>> list, final boolean multiChoice);
        public void scanFinish(final ArrayList<Map<String, String>> list, final int position);
    }

    public class InventoryThread extends Thread {
        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                if (stocking) {
                    epcList = reader.inventoryRealTime(); //实时盘存
                    if (epcList != null && !epcList.isEmpty()) {
                        for (byte[] epc : epcList) {
                            String epcStr = Tools.Bytes2HexString(epc, epc.length);
                            epcStr = Utils.hexToAscii(epcStr);
                            if (epcStr.matches(".*-\\d{5}$")) {
                                addToList(listEPC, epcStr);
                            }
                        }
                    }
                    epcList = null;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected View setView(final LayoutInflater inflater, final ViewGroup container, final int layout) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    protected void okClick() {
        parentActivity.scanFinish(listMap, multiChoice);
    }

    protected void setMsgText() {
        msg.setText(R.string.msg_not_in);
    }

    protected void addToList(final ArrayList<String> list, final String epc) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //第一次读入数据
                if (list.isEmpty()) {
                    addData(0, epc);
                } else {
                    int pos = binarySearch(list, epc);
                    if (pos != -1) {
                        addData(pos, epc);
                    }
                }
            }
        });
    }

    protected void addData(final int pos, final String epc) {
        if (hasEPC(epc)) {
            Map<String, String> map = new HashMap<>();
            map.put("ID", "");
            map.put("EPC", epc);
            listEPC.add(pos, epc);
            listMap.add(pos, map);
            Utils.play(1, 0);
            setListView();
        }
    }

    protected boolean hasEPC(String epc) {
        return csv.hasEntry(epc);
    }

    protected void setListView() {
        if (!listMap.isEmpty()) {
            msg.setVisibility(View.GONE);
            listViewData.setVisibility(View.VISIBLE);
            for (Map<String, String> m : listMap) {
                m.put("ID", Integer.toString(listMap.indexOf(m) + 1));
            }
            setAdapter();
            listViewData.setAdapter(adapter);
            listViewData.setSelection(listViewData.getCount() - 1);
        }
        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
    }

    protected void setAdapter() {
        adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_item,
                new String[]{"ID", "EPC"},
                new int[]{R.id.textView_id, R.id.textView_epc});
    }

    protected void clearList() {
        listEPC.clear();
        listMap.clear();
    }

    protected void setButtonClickable(Button button, boolean flag) {
        button.setClickable(flag);
        if (flag) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.GRAY);
        }
    }

    protected void onListItemClick(final int position) {
        parentActivity.scanFinish(listMap, position);
    }
}
