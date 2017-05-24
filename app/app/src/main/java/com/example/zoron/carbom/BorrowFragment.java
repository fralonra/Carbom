package com.example.zoron.carbom;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.text.InputType.TYPE_NULL;

/**
 * Created by zoron on 17-3-22.
 */

public class BorrowFragment extends BaseFragment {
    protected String stored = null;

    protected View mainView;
    protected View loanView;
    protected EditText id;
    protected EditText type;
    protected EditText name;
    protected EditText stage;
    protected EditText status;
    protected EditText time;
    protected EditText location;
    protected EditText keeper;

    protected EditText loaner;
    protected EditText loan_date;
    protected EditText expectedLoanBack;
    protected EditText note;

    protected SimpleDateFormat dateFormatter;

    public BorrowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow, container, false);
        mainView = view.findViewById(R.id.main_view);
        loanView = view.findViewById(R.id.loan_view);
        id = (EditText) view.findViewById(R.id.id);
        type = (EditText) view.findViewById(R.id.type);
        name = (EditText) view.findViewById(R.id.name);
        stage = (EditText) view.findViewById(R.id.stage);
        status = (EditText) view.findViewById(R.id.status);
        time = (EditText) view.findViewById(R.id.time);
        location = (EditText) view.findViewById(R.id.location);
        keeper = (EditText) view.findViewById(R.id.keeper);

        loaner = (EditText) view.findViewById(R.id.loaner);
        loan_date = (EditText) view.findViewById(R.id.loan_date);
        expectedLoanBack = (EditText) view.findViewById(R.id.expected_loan_back);
        note = (EditText) view. findViewById(R.id.note);

        setText();
        expectedLoanBack.setInputType(TYPE_NULL);
        setCurrentDate(loan_date);

        Button left = (Button) view.findViewById(R.id.left);
        Button right = (Button) view.findViewById(R.id.right);
        Button ok = (Button) view.findViewById(R.id.ok);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        stored = keeper.getText().toString();
        if (stored.equals(getResources().getString(R.string.stored))) {
            left.setText(R.string.loan);
        } else {
            left.setText(R.string.loan_back);
        }

        left.setOnClickListener(this);
        right.setOnClickListener(this);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        expectedLoanBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                if (stored.equals(getResources().getString(R.string.stored))) {
                    mainView.setVisibility(View.GONE);
                    loanView.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("确认归还吗？").setTitle("提示")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    loanBack();
                                    parentActivity.leftClick(++index);
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
                break;
            case R.id.right:
                parentActivity.leftClick(++index);
                break;
            case R.id.ok:
                if (Utils.isTextViewEmpty(loaner)) {
                    Toast.makeText(getContext(), "请填写借用人", Toast.LENGTH_SHORT).show();
                } else {
                    loan();
                    parentActivity.leftClick(++index);
                }
                break;
            case R.id.cancel:
                parentActivity.leftClick(++index);
                break;
            case R.id.expected_loan_back:
                pickDate();
                break;
            default:
                break;
        }
    }

    protected void setText() {
        id.setText(epc);
        type.setText(reader.getEntry(data, CsvReader.INDEX.TYPE));
        name.setText(reader.getEntry(data, CsvReader.INDEX.NAME));
        stage.setText(reader.getEntry(data, CsvReader.INDEX.STAGE));
        status.setText(reader.getEntry(data, CsvReader.INDEX.STATUS));
        time.setText(reader.getEntry(data, CsvReader.INDEX.TIME));
        location.setText(reader.getEntry(data, CsvReader.INDEX.LOCATION));
        keeper.setText(reader.getEntry(data, CsvReader.INDEX.KEEPER));
    }

    protected void loan() {
        mapToWrite = new HashMap<>();
        mapToWrite.put(CsvReader.INDEX.ID, epc);
        getInput(CsvReader.INDEX.KEEPER, loaner);
        getInput(CsvReader.INDEX.LOAN_DATE, loan_date);
        getInput(CsvReader.INDEX.EXPECTED_LOAN_BACK, expectedLoanBack);
        getInput(CsvReader.INDEX.NOTE, note);
        if (reader.hasData(mapToWrite)) {
            reader.modify(mapToWrite);
        }
    }

    protected void loanBack() {
        mapToWrite = new HashMap<>();
        mapToWrite.put(CsvReader.INDEX.ID, epc);
        mapToWrite.put(CsvReader.INDEX.KEEPER, getResources().getString(R.string.stored));
        mapToWrite.put(CsvReader.INDEX.LOAN_DATE, "");
        mapToWrite.put(CsvReader.INDEX.EXPECTED_LOAN_BACK, "");
        if (reader.hasData(mapToWrite)) {
            reader.modify(mapToWrite);
        }
    }

    protected void pickDate() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view,
                                          int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        expectedLoanBack.setText(dateFormatter.format(newDate.getTime()));
                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
