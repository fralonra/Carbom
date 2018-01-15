package com.example.zoron.carbom;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.example.zoron.carbom.Entry.INDEX.EPC;
import static com.example.zoron.carbom.Entry.INDEX.KEEPER;
import static com.example.zoron.carbom.Entry.INDEX.LOCATION;
import static com.example.zoron.carbom.Entry.INDEX.TYPE;
import static com.example.zoron.carbom.Utils.binarySearch;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockFragment extends ScanFragment {
    private OnExport parentActivity;
    private ArrayList<Entry> unstockList;
    private ArrayList<String> stockFilterResult;

    private static final int BYTYPE = 0;
    private static final int BYLOCATION = 1;
    private static final int BYKEEPER = 2;
    private static final int DEFAULTWAY = BYTYPE;

    private int way = DEFAULTWAY;
    private Entry.INDEX filterIndex;
    private int filterValue = 0;
    private Boolean stockDone = false;

    private TextView location;
    private TextView keeper;
    private Button filter;
    private Button export;
    private AlertDialog stockWayDialog;
    private AlertDialog stockFilterDialog;
    private Spinner stockSpinner;

    public StockFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            parentActivity = (OnExport) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnExport");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        unstockList = new ArrayList<>();
        stockFilterResult = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_stock, container, false);
        listViewData = (ListView) view.findViewById(R.id.listView_data);
        msg = (TextView) view.findViewById(R.id.msg);
        location = (TextView) view.findViewById(R.id.location);
        keeper = (TextView) view.findViewById(R.id.keeper);
        setMsgText();

        final Button scan = (Button) view.findViewById(R.id.scan);
        filter = (Button) view.findViewById(R.id.filter);
        export = (Button) view.findViewById(R.id.export);
        setButtonClickable(filter, false);
        setButtonClickable(export, false);

        // Stockway Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_stock_way, null);
        Button byType = (Button) layout.findViewById(R.id.by_type);
        Button byLocation = (Button) layout.findViewById(R.id.by_location);
        Button byKeeper = (Button) layout.findViewById(R.id.by_keeper);
        Button cancel = (Button) layout.findViewById(R.id.cancel);

        builder.setCancelable(false);
        builder.setTitle("选择盘点方式").setView(layout);
        stockWayDialog = builder.create();
        byType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock(BYTYPE);
                stockWayDialog.dismiss();
            }
        });
        byLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock(BYLOCATION);
                stockWayDialog.dismiss();
            }
        });
        byKeeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock(BYKEEPER);
                stockWayDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockWayDialog.dismiss();
            }
        });

        // Filter
        AlertDialog.Builder builderF = new AlertDialog.Builder(getContext());
        View layoutF = getActivity().getLayoutInflater().inflate(R.layout.dialog_stock_filter, null);
        stockSpinner = (Spinner) layoutF.findViewById(R.id.stock_spinner);
        Button filterApply = (Button) layoutF.findViewById(R.id.filter_apply);
        Button filterCancel = (Button) layoutF.findViewById(R.id.filter_cancel);

        builder.setCancelable(false);
        builderF.setTitle("请选择过滤条件").setView(layoutF);
        stockFilterDialog = builderF.create();
        stockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterValue = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        filterApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterValue == 0) {
                    listStock(unstockList);
                } else if (!stockFilterResult.get(filterValue).equals("")) {
                    listStock(csv.getEntrybyFilter(unstockList, filterIndex, stockFilterResult.get(filterValue)));
                }
                stockFilterDialog.dismiss();
            }
        });
        filterCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockFilterDialog.dismiss();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!unstockList.isEmpty()) {
                    if (way == BYTYPE) {
                        filterIndex = TYPE;
                    } else if (way == BYLOCATION) {
                        filterIndex = LOCATION;
                    } else if (way == BYKEEPER) {
                        filterIndex = KEEPER;
                    }
                    if (stockFilterResult.isEmpty()) {
                        stockFilterResult = csv.filter(filterIndex, unstockList);
                        stockFilterResult.add(0, "无过滤");
                    }
                    stockSpinner.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item,
                            stockFilterResult));
                    stockFilterDialog.show();
                }
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stocking = !stocking;
                if (stocking) {
                    if (location.getVisibility() == View.VISIBLE) {
                        location.setVisibility(View.GONE);
                    }
                    if (keeper.getVisibility() == View.VISIBLE) {
                        keeper.setVisibility(View.GONE);
                    }
                    if (stockDone) {
                        stockDone = false;
                        clearList();
                    }
                    scan.setText(R.string.stop_scan);
                    if (export.isClickable()) {
                        setButtonClickable(filter, false);
                        setButtonClickable(export, false);
                    }
                    /*
                    Log.d("xxcc", "start");
                    for (Integer i = 0; i < BaseActivity.reader.data.size(); ++i) {
                        Log.d("xxcc", i.toString());
                        Entry e = BaseActivity.reader.data.get(i);
                        if (listEPC.isEmpty()) {
                            addData(0, e.get(Entry.INDEX.EPC));
                        } else {
                            int pos = binarySearch(listEPC, e.get(Entry.INDEX.EPC));
                            if (pos != -1) {
                                addData(pos, e.get(Entry.INDEX.EPC));
                            }
                        }
                    }
                    */
                } else {
                    if (!listMap.isEmpty()) {
                        stockWayDialog.show();
                    }
                    scan.setText(R.string.start_scan);
                }
            }
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (export.isClickable()) {
                    if (!unstockList.isEmpty()) {
                        parentActivity.export(unstockList);
                    }
                }
            }
        });
        return view;
    }

    public interface OnExport {
        public void export(ArrayList<Entry> data);
    }

    @Override
    protected void setMsgText() {
        msg.setText(R.string.please_scan);
    }

    @Override
    protected void addToList(final ArrayList<String> list, final String epc) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!listEPC.contains(epc))
                    addData(listEPC.size(), epc);
            }
        });
    }

    @Override
    protected void addData(final int pos, final String epc) {
        if (hasEPC(epc)) {
            Map<String, String> map = new HashMap<>();
            map.put("EPC", epc);
            map.put("TYPE", csv.getValueByEpc(epc, TYPE));
            map.put("STATUS", getResources().getText(R.string.stocked).toString());
            listEPC.add(pos, epc);
            listMap.add(pos, map);
            //Log.d("xxcc", String.valueOf(listMap.size()));
            Utils.play(1, 0);
            setListView();
        }
    }

    @Override
    protected void setListView() {
        if (!listMap.isEmpty()) {
            msg.setVisibility(View.GONE);
            listViewData.setVisibility(View.VISIBLE);
            location.setVisibility(View.GONE);
            keeper.setVisibility(View.GONE);
            adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_stock,
                    new String[]{"EPC", "TYPE", "STATUS"},
                    new int[]{R.id.textView_epc, R.id.textView_type, R.id.textView_status});
            listViewData.setAdapter(adapter);
            listViewData.setSelection(listViewData.getCount() - 1);
        }
    }

    private void stock(final int way) {
        this.way = way;
        if (!listMap.isEmpty()) {
            setButtonClickable(filter, true);
            setButtonClickable(export, true);
        }
        unstockList = csv.stock(listEPC);
        if (unstockList.isEmpty()) {
            msg.setText("全部已盘");
            msg.setVisibility(View.VISIBLE);
            listViewData.setVisibility(View.GONE);
        } else {
            listStock(unstockList);
        }
        stockFilterResult.clear();
        stockDone = true;
    }

    private void listStock(final ArrayList<Entry> list) {
        listMap.clear();
        for (Entry e : list) {
            Map<String, String> map = new HashMap<>();
            map.put("EPC", e.get(EPC));
            if (way == BYLOCATION) {
                map.put("LOCATION", e.get(LOCATION));
            }
            if (way == BYKEEPER) {
                map.put("KEEPER", e.get(KEEPER));
            }
            map.put("TYPE", e.get(TYPE));
            map.put("STATUS", getResources().getString(R.string.unstored));
            listMap.add(map);
        }
        setStockListView();
    }

    private void setStockListView() {
        if (!listMap.isEmpty()) {
            msg.setVisibility(View.GONE);
            listViewData.setVisibility(View.VISIBLE);
            if (way == BYTYPE) {
                Collections.sort(listMap, new MapComparator("TYPE"));
                location.setVisibility(View.GONE);
                keeper.setVisibility(View.GONE);
                adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_stock,
                        new String[]{"EPC", "TYPE", "STATUS"},
                        new int[]{R.id.textView_epc, R.id.textView_type, R.id.textView_status});
            } else if (way == BYLOCATION) {
                Collections.sort(listMap, new MapComparator("LOCATION"));
                location.setVisibility(View.VISIBLE);
                keeper.setVisibility(View.GONE);
                adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_stock_location,
                        new String[]{"EPC", "LOCATION", "TYPE", "STATUS"},
                        new int[]{R.id.textView_epc, R.id.textView_location, R.id.textView_type, R.id.textView_status});
            } else if (way == BYKEEPER) {
                Collections.sort(listMap, new MapComparator("KEEPER"));
                location.setVisibility(View.GONE);
                keeper.setVisibility(View.VISIBLE);
                adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_stock_keeper,
                        new String[]{"EPC", "KEEPER", "TYPE", "STATUS"},
                        new int[]{R.id.textView_epc, R.id.textView_keeper, R.id.textView_type, R.id.textView_status});
            }
            listViewData.setAdapter(adapter);
            listViewData.setSelection(listViewData.getCount() - 1);
        }
    }

    private class MapComparator implements Comparator<Map<String, String>> {
        private final String key;

        public MapComparator(String key) {
            this.key = key;
        }

        public int compare(Map<String, String> first, Map<String, String> second) {
            // TODO: Null checking, both for maps and values
            String firstValue = first.get(key);
            String secondValue = second.get(key);
            return firstValue.compareTo(secondValue);
        }
    }
}
