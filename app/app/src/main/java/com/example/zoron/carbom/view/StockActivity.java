package com.example.zoron.carbom.view;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.example.zoron.carbom.R;
import com.example.zoron.carbom.data.*;
import com.example.zoron.carbom.misc.*;
import com.example.zoron.carbom.view.ScanFragment;
import com.example.zoron.carbom.view.StockFragment;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.example.zoron.carbom.data.Entry.INDEX.INDEX_COUNT;

public class StockActivity extends AppCompatActivity implements ScanFragment.OnScanFinishedListener,
        StockFragment.OnExport {
    private static final int SELECT_PATH = 0;
    private static final String FILE_NAME_PREFIX = "盘点";
    private ArrayList<Entry> exportData;
    private String exportPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("ActionBar", e.toString());
        }

        setContentView(R.layout.activity_stock);
        Utils.replaceFragment(getSupportFragmentManager(),
                R.id.container, new StockFragment());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PATH) {
            exportPath = data.getDataString();
        }
        exportToXls();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void scanFinish(ArrayList<Map<String, String>> list, final boolean multichoice) {
    }

    @Override
    public void scanFinish(ArrayList<Map<String, String>> list, final int position) {
    }

    public void export(ArrayList<Entry> data) {
        exportData = data;
        FolderChooserDialog directoryChooserDialog =
                new FolderChooserDialog(this,
                        new FolderChooserDialog.ChosenDirectoryListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                exportPath = chosenDir;
                                exportToXls();
                            }
                        });
        directoryChooserDialog.setNewFolderEnabled(true);
        directoryChooserDialog.chooseDirectory();
    }

    private void exportToXls() {
        final String fileName = FILE_NAME_PREFIX + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date()) + ".xls";
        File file = new File(exportPath, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(Locale.getDefault());
        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("carbom", 0);
            try {
                for (Entry.INDEX key : Entry.INDEX.values()) {
                    sheet.addCell(new Label(key.ordinal(), 0, csvIndexToString(key)));
                }
                int row = 1;
                for (Entry e : exportData) {
                    for (Entry.INDEX key : Entry.INDEX.values()) {
                        if (key == INDEX_COUNT) break;//Ut
                        sheet.addCell(new Label(key.ordinal(), row, e.get(key)));
                    }
                    ++row;
                }
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
                Toast.makeText(this, "导出完毕", Toast.LENGTH_SHORT).show();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String csvIndexToString(Entry.INDEX key) {
        switch (key) {
            case EPC:
                return getResources().getString(R.string.id);
            case TYPE:
                return getResources().getString(R.string.type);
            case NAME:
                return getResources().getString(R.string.name);
            case STAGE:
                return getResources().getString(R.string.stage);
            case STATUS:
                return getResources().getString(R.string.status);
            case TIME:
                return getResources().getString(R.string.time);
            case LOAN_DATE:
                return getResources().getString(R.string.loan_date);
            case KEEPER:
                return getResources().getString(R.string.keeper);
            case NOTE:
                return getResources().getString(R.string.note);
            case RETURN_DATE:
                return getResources().getString(R.string.expected_loan_back);
            default:
                return "";
        }
    }
}
