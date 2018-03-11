package com.example.zoron.carbom.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.misc.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.text.InputType.TYPE_NULL;
import static com.example.zoron.carbom.data.Entry.INDEX.*;

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
    protected EditText note;

    protected EditText loaner;
    protected EditText loan_date;
    protected EditText expectedLoanBack;
    protected EditText loan_note;

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
        note = (EditText) view.findViewById(R.id.note);

        loaner = (EditText) view.findViewById(R.id.loaner);
        loan_date = (EditText) view.findViewById(R.id.loan_date);
        expectedLoanBack = (EditText) view.findViewById(R.id.expected_loan_back);
        loan_note = (EditText) view.findViewById(R.id.loan_note);

        setText();
        expectedLoanBack.setInputType(TYPE_NULL);
        setCurrentDate(loan_date);

        Button left = (Button) view.findViewById(R.id.left);
        Button center = (Button) view.findViewById(R.id.center);
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
        center.setOnClickListener(this);
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
                    showLoanView();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("确认归还吗？").setTitle("提示")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    setLoanBackState();
                                }
                            })
                            .setNeutralButton("转借", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    showLoanView();

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
            case R.id.center:
                parentActivity.backToFirstFragment(this);
                break;
            case R.id.right:
                closeFragment();
                break;
            case R.id.ok:
                if (Utils.isTextViewEmpty(loaner)) {
                    Toast.makeText(getContext(), "请填写借用人", Toast.LENGTH_SHORT).show();
                } else {
                    loan();
                    closeFragment();
                }
                break;
            case R.id.cancel:
                closeFragment();
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
        type.setText(reader.getValueByEpc(epc, TYPE));
        name.setText(reader.getValueByEpc(epc, NAME));
        stage.setText(reader.getValueByEpc(epc, STAGE));
        status.setText(reader.getValueByEpc(epc, STATUS));
        time.setText(reader.getValueByEpc(epc, TIME));
        location.setText(reader.getValueByEpc(epc, LOCATION));
        keeper.setText(reader.getValueByEpc(epc, KEEPER));
        note.setText(reader.getValueByEpc(epc, NOTE));
        loan_note.setText(reader.getValueByEpc(epc, NOTE));
    }

    protected void loan() {
        mapToWrite = new HashMap<>();
        mapToWrite.put(EPC, epc);
        getInput(KEEPER, loaner);
        getInput(LOAN_DATE, loan_date);
        getInput(RETURN_DATE, expectedLoanBack);
        getInput(NOTE, loan_note);
        if (reader.hasEntry(mapToWrite)) {
            reader.modify(mapToWrite);
        }
    }

    protected void loanBack(String status, String note) {
        mapToWrite = new HashMap<>();
        mapToWrite.put(EPC, epc);
        mapToWrite.put(KEEPER, getResources().getString(R.string.stored));
        mapToWrite.put(LOAN_DATE, "");
        mapToWrite.put(RETURN_DATE, "");
        mapToWrite.put(STATUS, status);
        mapToWrite.put(NOTE, note);
        if (reader.hasEntry(mapToWrite)) {
            reader.modify(mapToWrite);
        }
    }

    protected void closeFragment() {
        parentActivity.leftClick(++index);
    }

    protected void showLoanView() {
        mainView.setVisibility(View.GONE);
        loanView.setVisibility(View.VISIBLE);
    }

    protected void setLoanBackState() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_loanback_state, null);
        final EditText status = (EditText) layout.findViewById(R.id.loanback_status);
        final EditText note = (EditText) layout.findViewById(R.id.loanback_note);
        String statusText = data.get(STATUS);
        if (!statusText.equals("")) status.setText(data.get(STATUS));
        else status.setText((R.string.status_fine));
        note.setText(data.get(NOTE));

        builder.setMessage("请填写设备状态").setView(layout)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loanBack(status.getText().toString(), note.getText().toString().replaceAll("\\n", "。"));
                        closeFragment();
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
