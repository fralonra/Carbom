package com.example.zoron.carbom;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.zoron.carbom.Entry.INDEX.EPC;
import static com.example.zoron.carbom.Entry.INDEX.KEEPER;
import static com.example.zoron.carbom.Entry.INDEX.LOCATION;
import static com.example.zoron.carbom.Entry.INDEX.NAME;
import static com.example.zoron.carbom.Entry.INDEX.NOTE;
import static com.example.zoron.carbom.Entry.INDEX.STAGE;
import static com.example.zoron.carbom.Entry.INDEX.STATUS;
import static com.example.zoron.carbom.Entry.INDEX.TIME;
import static com.example.zoron.carbom.Entry.INDEX.TYPE;

/**
 * Created by zoron on 17-3-21.
 */

public class InFragment extends BaseFragment {
    private EditText id;
    private EditText type;
    private EditText name;
    private EditText stage;
    private EditText status;
    private EditText time;
    private EditText location;
    private EditText keeper;
    private EditText note;

    public InFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in, container, false);
        id = (EditText) view.findViewById(R.id.id);
        type = (EditText) view.findViewById(R.id.type);
        name = (EditText) view.findViewById(R.id.name);
        stage = (EditText) view.findViewById(R.id.stage);
        status = (EditText) view.findViewById(R.id.status);
        time = (EditText) view.findViewById(R.id.time);
        location = (EditText) view.findViewById(R.id.location);
        keeper = (EditText) view.findViewById(R.id.keeper);
        note = (EditText) view.findViewById(R.id.note);

        id.setText(epc);
        if (reader.hasEntry(epc)) {
            type.setText(data.get(TYPE));
            name.setText(data.get(NAME));
            stage.setText(data.get(STAGE));
            status.setText(data.get(STATUS));
            time.setText(data.get(TIME));
            location.setText(data.get(LOCATION));
            keeper.setText(data.get(KEEPER));
            note.setText(data.get(NOTE));
        } else {
            keeper.setText(R.string.stored);
        }

        setCurrentDate(time);

        Button left = (Button) view.findViewById(R.id.left);
        Button center = (Button) view.findViewById(R.id.center);
        Button right = (Button) view.findViewById(R.id.right);

        left.setOnClickListener(this);
        center.setOnClickListener(this);
        right.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                update();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("确认入库？").setTitle("请确认")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                updateData();
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
                break;
            case R.id.center:
                parentActivity.backToFirstFragment(this);
                break;
            case R.id.right:
                parentActivity.leftClick(++index);
                break;
            default:
                break;
        }
    }

    private void updateData() {
        mapToWrite = new HashMap<>();
        getInput(EPC, id);
        getInput(TYPE, type);
        getInput(NAME, name);
        getInput(STAGE, stage);
        getInput(STATUS, status);
        getInput(TIME, time);
        getInput(LOCATION, location);
        getInput(KEEPER, keeper);
        getInput(NOTE, note);
        if (!reader.hasEntry(mapToWrite)) {
            reader.append(mapToWrite);
        } else {
            reader.modify(mapToWrite);
        }
    }
}
