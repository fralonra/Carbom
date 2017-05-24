package com.example.zoron.carbom;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by zoron on 17-3-22.
 */

public class OutFragment extends BaseFragment {
    private EditText id;
    private EditText type;
    private EditText name;
    private EditText stage;
    private EditText status;
    private EditText time;
    private EditText location;
    private EditText keeper;

    public OutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_out, container, false);
        id = (EditText) view.findViewById(R.id.id);
        type = (EditText) view.findViewById(R.id.type);
        name = (EditText) view.findViewById(R.id.name);
        stage = (EditText) view.findViewById(R.id.stage);
        status = (EditText) view.findViewById(R.id.status);
        time = (EditText) view.findViewById(R.id.time);
        location = (EditText) view.findViewById(R.id.location);
        keeper = (EditText) view.findViewById(R.id.keeper);

        id.setText(epc);
        type.setText(reader.getEntry(data, CsvReader.INDEX.TYPE));
        name.setText(reader.getEntry(data, CsvReader.INDEX.NAME));
        stage.setText(reader.getEntry(data, CsvReader.INDEX.STAGE));
        status.setText(reader.getEntry(data, CsvReader.INDEX.STATUS));
        time.setText(reader.getEntry(data, CsvReader.INDEX.TIME));
        location.setText(reader.getEntry(data, CsvReader.INDEX.LOCATION));
        keeper.setText(reader.getEntry(data, CsvReader.INDEX.KEEPER));

        Button left = (Button) view.findViewById(R.id.left);
        Button right = (Button) view.findViewById(R.id.right);

        left.setOnClickListener(this);
        right.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                update();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                EditText editText = new EditText(getContext());
                builder.setMessage("请填写出库理由").setTitle("出库").setView(editText)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                reader.remove(epc);
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
            case R.id.right:
                parentActivity.leftClick(++index);
                break;
            default:
                break;
        }
    }
}

