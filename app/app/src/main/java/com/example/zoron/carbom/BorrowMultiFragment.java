package com.example.zoron.carbom;

import android.support.v4.app.FragmentManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.zoron.carbom.Entry.INDEX.EPC;
import static com.example.zoron.carbom.Entry.INDEX.RETURN_DATE;
import static com.example.zoron.carbom.Entry.INDEX.KEEPER;
import static com.example.zoron.carbom.Entry.INDEX.LOAN_DATE;
import static com.example.zoron.carbom.Entry.INDEX.LOCATION;
import static com.example.zoron.carbom.Entry.INDEX.NAME;
import static com.example.zoron.carbom.Entry.INDEX.NOTE;
import static com.example.zoron.carbom.Entry.INDEX.STAGE;
import static com.example.zoron.carbom.Entry.INDEX.STATUS;
import static com.example.zoron.carbom.Entry.INDEX.TIME;
import static com.example.zoron.carbom.Entry.INDEX.TYPE;

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
        id.setText(reader.getValueByList(list, EPC));
        type.setText(reader.getValueByList(list, TYPE));
        name.setText(reader.getValueByList(list, NAME));
        stage.setText(reader.getValueByList(list, STAGE));
        status.setText(reader.getValueByList(list, STATUS));
        time.setText(reader.getValueByList(list, TIME));
        location.setText(reader.getValueByList(list, LOCATION));
        keeper.setText(reader.getValueByList(list, KEEPER));
    }

    @Override
    protected void loan() {
        mapToWrite = new HashMap<>();
        getInput(KEEPER, loaner);
        getInput(LOAN_DATE, loan_date);
        getInput(RETURN_DATE, expectedLoanBack);
        getInput(NOTE, loan_note);
        reader.modify(list, mapToWrite);
    }

    @Override
    protected void loanBack(String status, String note) {
        mapToWrite = new HashMap<>();
        mapToWrite.put(KEEPER, getResources().getString(R.string.stored));
        mapToWrite.put(LOAN_DATE, "");
        mapToWrite.put(RETURN_DATE, "");
        mapToWrite.put(STATUS, status);
        mapToWrite.put(NOTE, note);
        reader.modify(list, mapToWrite);
    }

    @Override
    protected void closeFragment() {
        parentActivity.backToFirstFragment(this);
        // delayFinishActivity();
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
        }, 500);
    }
}
