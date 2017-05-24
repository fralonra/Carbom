package com.example.zoron.carbom;

import android.app.Activity;
import android.content.DialogInterface;
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
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockFragment extends ScanFragment {
    private OnExport parentActivity;
    private ArrayList<String> unstockList;

    private static final int BYTYPE = 0;
    private static final int BYLOCATION = 1;
    private static final int DEFAULTWAY = BYTYPE;

    private int way = DEFAULTWAY;
    private CsvReader.INDEX filterIndex;
    private int filterValue = 0;

    private TextView location;
    private Button filter;
    private Button export;

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

        View view = inflater.inflate(R.layout.fragment_stock, container, false);
        listViewData = (ListView) view.findViewById(R.id.listView_data);
        msg = (TextView) view.findViewById(R.id.msg);
        location = (TextView) view.findViewById(R.id.location);
        setMsgText();

        final Button scan = (Button) view.findViewById(R.id.scan);
        filter = (Button) view.findViewById(R.id.filter);
        export = (Button) view.findViewById(R.id.export);
        setButtonClickable(filter, false);
        setButtonClickable(export, false);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!unstockList.isEmpty()) {
                    final CsvReader reader = new CsvReader(CsvReader.SAVE_FILE);
                    if (way == BYTYPE) {
                        filterIndex = CsvReader.INDEX.TYPE;
                    } else if (way == BYLOCATION) {
                        filterIndex = CsvReader.INDEX.LOCATION;
                    }
                    final ArrayList<String> result = reader.filter(filterIndex, unstockList);
                    result.add(0, "无过滤");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    Spinner spinner = new Spinner(getContext());
                    spinner.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item,
                            result));
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            filterValue = position;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    builder.setTitle("请选择过滤条件").setView(spinner)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (filterValue == 0) {
                                        listStock(unstockList);
                                    } else if (!result.get(filterValue).equals("")) {
                                        listStock(reader.getLinebyFilter(unstockList, filterIndex, result.get(filterValue)));
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stocking = !stocking;
                if (stocking) {
                    scan.setText(R.string.stop_scan);
                    if (export.isClickable()) {
                        setButtonClickable(filter, false);
                        setButtonClickable(export, false);
                    }
                } else {
                    if (!listMap.isEmpty()) {
                        stockWayDialog();
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
        public void export(ArrayList<String> data);
    }

    @Override
    protected void setMsgText() {
        msg.setText(R.string.please_scan);
    }

    @Override
    protected void addData(List<String> list) {
        listMap.clear();
        for (String epc : list) {
            Map<String, String> map = new HashMap<>();
            map.put("EPC", epc);
            map.put("TYPE", new CsvReader(CsvReader.SAVE_FILE).getEntryByEPC(epc, CsvReader.INDEX.TYPE));
            map.put("STATUS", getResources().getText(R.string.stocked).toString());
            listMap.add(map);
        }
        setListView();
    }

    @Override
    protected void setListView() {
        if (!listMap.isEmpty()) {
            msg.setVisibility(View.GONE);
            listViewData.setVisibility(View.VISIBLE);
            if (way == BYTYPE) {
                Collections.sort(listMap, new MapComparator("TYPE"));
                location.setVisibility(View.GONE);
                adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_stock,
                        new String[]{"EPC", "TYPE", "STATUS"},
                        new int[]{R.id.textView_epc, R.id.textView_type, R.id.textView_status});
            } else if (way == BYLOCATION) {
                Collections.sort(listMap, new MapComparator("LOCATION"));
                location.setVisibility(View.VISIBLE);
                adapter = new SimpleAdapter(getContext(), listMap, R.layout.listview_stock_location,
                        new String[]{"EPC", "LOCATION", "TYPE", "STATUS"},
                        new int[]{R.id.textView_epc, R.id.textView_location, R.id.textView_type, R.id.textView_status});
            }
            listViewData.setAdapter(adapter);
        }
    }

    private void stockWayDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_stock_way, null);
        Button byType = (Button) layout.findViewById(R.id.by_type);
        Button byLocation = (Button) layout.findViewById(R.id.by_location);

        builder.setCancelable(false);
        builder.setTitle("选择盘点方式").setView(layout);
        final AlertDialog dialog = builder.create();
        byType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock(BYTYPE);
                dialog.dismiss();
            }
        });
        byLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock(BYLOCATION);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void stock(final int way) {
        CsvReader reader = new CsvReader(CsvReader.SAVE_FILE);
        this.way = way;
        if (!listMap.isEmpty()) {
            setButtonClickable(filter, true);
            setButtonClickable(export, true);
        }
        unstockList = reader.stock(listEPC);
        if (unstockList.isEmpty()) {
            msg.setText("全部已盘");
            msg.setVisibility(View.VISIBLE);
            listViewData.setVisibility(View.GONE);
        } else {
            listStock(unstockList);
        }
    }

    private void listStock(final ArrayList<String> list) {
        listMap.clear();
        if (way == BYTYPE) {
            for (String data : list) {
                Map<String, String> map = new HashMap<>();
                map.put("EPC", CsvReader.getEntry(data, CsvReader.INDEX.ID));
                map.put("TYPE", CsvReader.getEntry(data, CsvReader.INDEX.TYPE));
                map.put("STATUS", getResources().getString(R.string.unstored));
                listMap.add(map);
            }
        } else if (way == BYLOCATION) {
            for (String data : list) {
                Map<String, String> map = new HashMap<>();
                map.put("EPC", CsvReader.getEntry(data, CsvReader.INDEX.ID));
                map.put("LOCATION", CsvReader.getEntry(data, CsvReader.INDEX.LOCATION));
                map.put("TYPE", CsvReader.getEntry(data, CsvReader.INDEX.TYPE));
                map.put("STATUS", getResources().getString(R.string.unstored));
                listMap.add(map);
            }
        }
        setListView();
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
