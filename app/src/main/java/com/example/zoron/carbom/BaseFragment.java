package com.example.zoron.carbom;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zoron on 17-4-13.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    protected OnClickListener parentActivity;
    protected CsvReader reader;
    protected Map<CsvReader.INDEX, String> mapToWrite;
    protected String data = "";
    protected String epc = "";
    protected int index = 0;

    public BaseFragment() {
    }

    public BaseFragment(String epc, int index) {
        setEPC(epc);
        setIndex(index);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            parentActivity = (OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reader = new CsvReader(CsvReader.SAVE_FILE);
        data = reader.getData(epc);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                update();
                parentActivity.leftClick(++index);
                break;
            case R.id.right:
                parentActivity.leftClick(++index);
                break;
            default:
                break;
        }
    }

    public interface OnClickListener {
        public void leftClick(final int index);
        public void rightClick(final int index);
    }

    public void init(final String epc, final int index) {
        setEPC(epc);
        setIndex(index);
    }

    protected void setEPC(final String mEpc) {
        epc = mEpc;
    }

    protected void setIndex(final int i) {
        index = i;
    }

    protected void update() {
    }

    protected void getInput(final CsvReader.INDEX index, EditText editText) {
        if (!Utils.isTextViewEmpty(editText)) {
            if (mapToWrite != null) {
                mapToWrite.put(index, editText.getText().toString().trim());
            } else {
                Log.i("mapToWrite", "mapToWrite is null");
            }
        }
    }

    protected static void setCurrentDate(EditText editText) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        editText.setText(dateFormat.format(new Date())); // it will show 16/07/2013
    }
}