package com.example.zoron.carbom;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import static com.example.zoron.carbom.Entry.INDEX.EPC;
import static com.example.zoron.carbom.Entry.INDEX.RETURN_DATE;
import static com.example.zoron.carbom.Entry.INDEX.KEEPER;
import static com.example.zoron.carbom.Entry.INDEX.LOAN_DATE;
import static com.example.zoron.carbom.Entry.INDEX.LOCATION;
import static com.example.zoron.carbom.Entry.INDEX.TYPE;

/**
 * Created by zoron on 17-4-17.
 */

public class QueryResultFragment extends Fragment {
    private ArrayList<Map<String, String>> resultList;
    private int mWay;
    private String name = null;

    private ListView listViewResult;
    private View normal;
    private View bykeeper;
    private TextView keeper;

    public QueryResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_result, container, false);
        listViewResult = (ListView) view.findViewById(R.id.listView_result);
        normal = view.findViewById(R.id.normal);
        bykeeper = view.findViewById(R.id.bykeeper);
        keeper = (TextView) view.findViewById(R.id.keeper);
        Button back = (Button) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.replaceFragment(getActivity().getSupportFragmentManager(),
                        R.id.container, new QueryFragment());
            }
        });
        setListView();
        return view;
    }

    public void setResult(ArrayList<Entry> results, final int way) {
        resultList = new ArrayList<>();
        mWay = way;
        if (mWay == QueryFragment.NORMAL) {
            for (Entry e : results) {
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put(TYPE.toString(), e.get(TYPE));
                resultMap.put(EPC.toString(), e.get(EPC));
                resultMap.put(LOCATION.toString(), e.get(LOCATION));
                resultMap.put(KEEPER.toString(), e.get(KEEPER));
                resultList.add(resultMap);
            }
        } else if (mWay == QueryFragment.BYKEEPER) {
            for (Entry e : results) {
                if (name == null) {
                    name = e.get(KEEPER);
                }
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put(TYPE.toString(), e.get(TYPE));
                resultMap.put(EPC.toString(), e.get(EPC));
                resultMap.put(LOAN_DATE.toString(), e.get(LOAN_DATE));
                resultMap.put(RETURN_DATE.toString(), e.get(RETURN_DATE));
                resultList.add(resultMap);
            }
        }
    }

    private void setListView() {
        if (mWay == QueryFragment.NORMAL) {
            if (!resultList.isEmpty()) {
                normal.setVisibility(View.VISIBLE);
                bykeeper.setVisibility(View.GONE);
                keeper.setText("");
                SimpleAdapter adapter = new SimpleAdapter(getContext(),
                        resultList, R.layout.listview_query_result,
                        new String[]{TYPE.toString(), EPC.toString(), LOCATION.toString(), KEEPER.toString()},
                        new int[]{R.id.textView_type, R.id.textView_epc, R.id.textView_location, R.id.textView_keeper});
                listViewResult.setAdapter(adapter);
            }
        } else if (mWay == QueryFragment.BYKEEPER) {
            if (!resultList.isEmpty()) {
                normal.setVisibility(View.GONE);
                bykeeper.setVisibility(View.VISIBLE);
                keeper.setText(getString(R.string.keeper_name, name));
                SimpleAdapter adapter = new SimpleAdapter(getContext(),
                        resultList, R.layout.listview_query_keeper,
                        new String[]{TYPE.toString(), EPC.toString(), LOAN_DATE.toString(), RETURN_DATE.toString()},
                        new int[]{R.id.textView_type, R.id.textView_epc, R.id.textView_loan_date, R.id.textView_loan_back_date});
                listViewResult.setAdapter(adapter);
            }
        }
        listViewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), BorrowActivity.class);
                intent.putExtra("epc", resultList.get(position).get(EPC.toString()));
                startActivity(intent);
            }
        });
    }
}