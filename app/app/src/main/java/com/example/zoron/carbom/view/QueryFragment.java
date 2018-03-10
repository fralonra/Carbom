package com.example.zoron.carbom.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.data.*;
import com.example.zoron.carbom.misc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoron on 17-3-22.
 */

public class QueryFragment extends Fragment {
    public static final int NORMAL = 0;
    public static final int BYKEEPER = 1;

    private CsvReader reader = MainActivity.reader;

    private SearchView idQuery;
    private SearchView typeQuery;
    private SearchView statusQuery;
    private SearchView locationQuery;
    private SearchView keeperQuery;

    public QueryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        idQuery = (SearchView) view.findViewById(R.id.id_query);
        typeQuery = (SearchView) view.findViewById(R.id.type_query);
        statusQuery = (SearchView) view.findViewById(R.id.status_query);
        locationQuery = (SearchView) view.findViewById(R.id.location_query);
        keeperQuery = (SearchView) view.findViewById(R.id.keeper_query);
        Button query = (Button) view.findViewById(R.id.query);
        Button all = (Button) view.findViewById(R.id.all);

        idQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(Entry.INDEX.EPC, idQuery.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        typeQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(Entry.INDEX.TYPE, typeQuery.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        statusQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(Entry.INDEX.STATUS, statusQuery.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        locationQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("在库")) {
                    search(Entry.INDEX.KEEPER, locationQuery.getQuery().toString());
                    return false;
                }
                search(Entry.INDEX.LOCATION, locationQuery.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        keeperQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(Entry.INDEX.KEEPER, keeperQuery.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiSearch();
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showall();
            }
        });

        return view;
    }

    private void search(Entry.INDEX key, String value) {
        if (!value.isEmpty()) {
            ArrayList<Entry> results = reader.search(key, value);
            if (results.isEmpty()) {
                Toast toast = Toast.makeText(getContext(), "无搜索结果", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                QueryResultFragment queryResultFragment = new QueryResultFragment();
                if (key == Entry.INDEX.KEEPER) {
                    queryResultFragment.setResult(results, BYKEEPER);
                } else {
                    queryResultFragment.setResult(results, NORMAL);
                }
                Utils.replaceFragment(getActivity().getSupportFragmentManager(),
                        R.id.container, queryResultFragment);
            }
        }
    }

    private void multiSearch() {
        Map<Entry.INDEX, String> queries = new HashMap<>();
        if (searchViewhasQuery(idQuery)) {
            queries.put(Entry.INDEX.EPC, idQuery.getQuery().toString());
        }
        if (searchViewhasQuery(typeQuery)) {
            queries.put(Entry.INDEX.TYPE, typeQuery.getQuery().toString());
        }
        if (searchViewhasQuery(statusQuery)) {
            queries.put(Entry.INDEX.STATUS, statusQuery.getQuery().toString());
        }
        if (searchViewhasQuery(locationQuery)) {
            queries.put(Entry.INDEX.LOCATION, locationQuery.getQuery().toString());
        }
        if (searchViewhasQuery(keeperQuery)) {
            queries.put(Entry.INDEX.KEEPER, keeperQuery.getQuery().toString());
        }
        ArrayList<Entry> results = reader.search(queries);
        showResult(results);
    }

    private boolean searchViewhasQuery(SearchView sv) {
        return sv.getQuery().toString().trim().length() > 0;
    }

    private void showall() {
        ArrayList<Entry> results = reader.allBySort();
        if (!results.isEmpty())
            showResult(results);
        else
            Toast.makeText(getContext(), "数据库为空，或找不到文件", Toast.LENGTH_LONG).show();
    }

    private void showResult(ArrayList<Entry> results) {
        if (results == null) {
            Toast.makeText(getContext(), "无结果", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "读取成功", Toast.LENGTH_LONG).show();
            QueryResultFragment queryResultFragment = new QueryResultFragment();
            queryResultFragment.setResult(results, NORMAL);
            Utils.replaceFragment(getActivity().getSupportFragmentManager(),
                    R.id.container, queryResultFragment);
        }
    }
}