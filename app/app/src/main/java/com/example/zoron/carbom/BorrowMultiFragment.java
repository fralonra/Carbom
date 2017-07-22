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
                                    //parentActivity.leftClick(++index);
                                    delayFinishActivity();
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
                    //parentActivity.leftClick(++index);
                    delayFinishActivity();
                }
                break;
            case R.id.cancel:
                //parentActivity.leftClick(++index);
                delayFinishActivity();
                break;
            case R.id.expected_loan_back:
                pickDate();
                break;
            default:
                break;
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
    protected void loanBack() {
        mapToWrite = new HashMap<>();
        mapToWrite.put(CsvReader.INDEX.KEEPER, getResources().getString(R.string.stored));
        mapToWrite.put(CsvReader.INDEX.LOAN_DATE, "");
        mapToWrite.put(CsvReader.INDEX.EXPECTED_LOAN_BACK, "");
        reader.modify(list, mapToWrite);
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
