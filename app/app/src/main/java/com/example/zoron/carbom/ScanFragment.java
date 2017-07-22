package com.example.zoron.carbom;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.hdhe.uhf.entity.EPC;
import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.biff.ByteArray;

/**
 * Created by zoron on 17-4-12.
 */

public class ScanFragment extends Fragment {
    protected OnScanFinishedListener parentActivity;
    protected InventoryThread thread;
    protected SimpleAdapter adapter;
    protected UhfReader reader;
    protected ArrayList<String> listEPC;
    protected ArrayList<Map<String, String>> listMap;

    protected boolean stocking = false;
    protected boolean needUpdate = false;
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

        reader = UhfReader.getInstance();
        listEPC = new ArrayList<>();
        listMap = new ArrayList<>();
        Utils.initSoundPool(getContext());

        if (thread == null) {
            thread = new InventoryThread();
        }
        thread.start();
    }

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
                parentActivity.scanFinish(listMap, multiChoice);
            }
        });
        return view;
    }

    public interface OnScanFinishedListener {
        public void scanFinish(final ArrayList<Map<String, String>> list, final boolean multiChoice);
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
                        //byte[] id = reader.readFrom6C(1, 0, 30, Tools.HexString2Bytes(keyword));
                        //Log.d("epcid", Tools.Bytes2HexString(id, id.length));
                        for (byte[] epc : epcList) {
                            String epcStr = Tools.Bytes2HexString(epc, epc.length);
                            epcStr = Utils.hexToAscii(epcStr);
                            addToList(listEPC, epcStr);
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

    protected void setMsgText() {
        msg.setText(R.string.msg_not_in);
    }

    protected void addToList(final List<String> list, final String epc) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //第一次读入数据
                if (list.isEmpty()) {
                    Utils.play(1, 0);
                    list.add(epc);
                    needUpdate = true;
                } else {
                    for (int i = 0; i < list.size(); ++i) {
                        String mEPC = list.get(i);
                        //list中有此EPC
                        if (epc.equals(mEPC)) {
                            break;
                        } else if (i == (list.size() - 1)) {
                            Utils.play(1, 0);
                            list.add(epc);
                            needUpdate = true;
                        }
                    }
                }
                if (needUpdate) {
                    addData(list);
                    needUpdate = false;
                }
            }
        });
    }

    protected void addData(List<String> list) {
        listMap.clear();
        int idcount = 1;
        for (String epc : list) {
            if (hasEPC(epc)) {
                Map<String, String> map = new HashMap<>();
                map.put("ID", Integer.toString(idcount));
                map.put("EPC", epc);
                idcount++;
                listMap.add(map);
            }
        }
        setListView();
    }

    protected boolean hasEPC(String epc) {
        CsvReader reader = new CsvReader(CsvReader.SAVE_FILE);
        return reader.hasData(epc);
    }

    protected void setListView() {
        if (!listMap.isEmpty()) {
            msg.setVisibility(View.GONE);
            listViewData.setVisibility(View.VISIBLE);
            adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_item,
                    new String[]{"ID", "EPC"},
                    new int[]{R.id.textView_id, R.id.textView_epc});
            listViewData.setAdapter(adapter);
        }
    }

    protected void setButtonClickable(Button button, boolean flag) {
        button.setClickable(flag);
        if (flag) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.GRAY);
        }
    }
}
