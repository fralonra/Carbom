package com.example.zoron.carbom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by zoron on 17-4-13.
 */

public class BorrowLoanFragment extends BaseFragment implements View.OnClickListener {

    public BorrowLoanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_loan, container, false);
        EditText id = (EditText) view.findViewById(R.id.id);
        id.setText(epc);

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
                parentActivity.leftClick(index);
                break;
            case R.id.right:
                parentActivity.rightClick(index);
                break;
            default:
                break;
        }
    }
}
