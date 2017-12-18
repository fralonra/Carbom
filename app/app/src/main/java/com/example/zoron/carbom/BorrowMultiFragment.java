package com.example.zoron.carbom;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoron on 17-5-11.
 */

public class BorrowMultiFragment extends BorrowFragment {
    private ArrayList<String> list = new ArrayList<>();

    public void init(ArrayList<Map<String, String>> epcs) {
        for (Map<String, String> map : epcs) {
            String epc = map.get("EPC");
            list.add(epc);
        }
    }

    @Override
    protected void setText() {
        id.setText(reader.getEntryByList(list, CsvReader.INDEX.EPC));
        type.setText(reader.getEntryByList(list, CsvReader.INDEX.TYPE));
        name.setText(reader.getEntryByList(list, CsvReader.INDEX.NAME));
        stage.setText(reader.getEntryByList(list, CsvReader.INDEX.STAGE));
        status.setText(reader.getEntryByList(list, CsvReader.INDEX.STATUS));
        time.setText(reader.getEntryByList(list, CsvReader.INDEX.TIME));
        location.setText(reader.getEntryByList(list, CsvReader.INDEX.LOCATION));
        keeper.setText(reader.getEntryByList(list, CsvReader.INDEX.KEEPER));
    }

    @Override
    protected void loan() {
        mapToWrite = new HashMap<>();
        getInput(CsvReader.INDEX.KEEPER, loaner);
        getInput(CsvReader.INDEX.LOAN_DATE, loan_date);
        getInput(CsvReader.INDEX.EXPECTED_LOAN_BACK, expectedLoanBack);
        getInput(CsvReader.INDEX.NOTE, note);
        reader.modify(list, mapToWrite);
    }

    @Override
    protected void loanBack(String status, String note) {
        mapToWrite = new HashMap<>();
        mapToWrite.put(CsvReader.INDEX.KEEPER, getResources().getString(R.string.stored));
        mapToWrite.put(CsvReader.INDEX.LOAN_DATE, "");
        mapToWrite.put(CsvReader.INDEX.EXPECTED_LOAN_BACK, "");
        mapToWrite.put(CsvReader.INDEX.STATUS, status);
        mapToWrite.put(CsvReader.INDEX.NOTE, note);
        reader.modify(list, mapToWrite);
    }

    @Override
    protected void closeFragment() {
        delayFinishActivity();
    }

    private void delayFinishActivity() {
        Toast toast = Toast.makeText(getContext(), "即将返回主界面", Toast.LENGTH_SHORT);
        toast.show();
        Handler homeHandler = new Handler();
        homeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        }, 2500);
    }
}
