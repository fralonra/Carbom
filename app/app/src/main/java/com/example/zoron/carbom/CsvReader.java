package com.example.zoron.carbom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.Environment;
import android.util.Log;

import static com.example.zoron.carbom.CsvReader.INDEX.EPC;

/**
 * Created by zoron on 17-4-14.
 */

public class CsvReader {
    public static final String SAVE_FILE = "data.cbm";

    public static final String INDEX_EPC = "EPC";
    public static final String INDEX_TYPE = "TYPE";
    public static final String INDEX_NAME = "NAME";
    public static final String INDEX_STAGE = "STAGE";
    public static final String INDEX_STATUS = "STATUS";
    public static final String INDEX_TIME = "TIME";
    public static final String INDEX_LOCATION = "LOCATION";
    public static final String INDEX_KEEPER = "KEEPER";
    public static final String INDEX_NOTE = "NOTE";
    public static final String INDEX_LOAN_DATE = "LOAN_DATE";
    public static final String INDEX_EXPECTED_LOAN_BACK = "RETURN_DATE";

    public enum INDEX {
        EPC,
        TYPE,
        NAME,
        STAGE,
        STATUS,
        TIME,
        LOCATION,
        KEEPER,
        NOTE,
        LOAN_DATE,
        EXPECTED_LOAN_BACK,
        TOTAL_INDEX
    }

    private File csv = null;

    private static final String KEY_VALUE_DIV = ":";
    private static final String ENTRY_DIV = "&";
    private static final String DATA_DIV = "\n";

    public CsvReader(final String file) {
        csv = new File(Environment.getExternalStorageDirectory(), file);
        //csv = new File("/mnt/sdcard/Document/", file);
        Log.i("dir", csv.getAbsolutePath());
        if (!csv.exists()) {
            try {
                csv.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasData(Map<INDEX, String> data) {
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String line;
            while ((line = content.readLine()) != null) {
                if (getEntry(line, EPC).equals(data.get(EPC))) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasData(String epc) {
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String line;
            while ((line = content.readLine()) != null) {
                if (getEntry(line, EPC).equals(epc)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getData(String epc) {
        if (hasData(epc)) {
            try {
                LineNumberReader content = new LineNumberReader(new FileReader(csv));
                String line;
                while ((line = content.readLine()) != null) {
                    if (getEntry(line, EPC).equals(epc)) {
                        return line;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static boolean hasEntry(String data, INDEX key) {
        return data.contains(key.toString() + KEY_VALUE_DIV);
    }

    public static String getEntry(String data, INDEX key) {
        String div = key.toString() + KEY_VALUE_DIV;
        if (data.contains(div)) {
            return data.split(div)[1].split(ENTRY_DIV)[0];
        }
        return "";
    }

    public String getEntryByList(ArrayList<String> list, INDEX key) {
        String value = "";
        for (String epc : list) {
            String v = getEntryByEPC(epc, key);
            if (value.equals("")) {
                value = v;
            } else {
                if (!v.equals(value)) {
                    return "多个值";
                }
            }
        }
        return value;
    }

    public String getEntryByEPC(String epc, INDEX key) {
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String line;
            while ((line = content.readLine()) != null) {
                String id = getEntry(line, EPC);
                if (id.equals(epc)) {
                    return getEntry(line, key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void append(Map<INDEX, String> data) {
        String content = dataToText(data);
        write(content, true);
    }

    public void remove(String epc) {
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String all = null;
            String line;
            while ((line = content.readLine()) != null) {
                if (!getEntry(line, EPC).equals(epc)) {
                    if (all == null) {
                        all = line + DATA_DIV;
                    } else {
                        all = all.concat(line + DATA_DIV);
                    }
                }
            }
            write(all, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modify(Map<INDEX, String> data) {
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String all = null;
            String line;
            while ((line = content.readLine()) != null) {
                if (getEntry(line, EPC).equals(data.get(EPC))) {
                    //Log.i("dsa", data.get(ID) + "  " + Integer.toString(data.size()));
                    Iterator it = data.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        INDEX key = (INDEX) pair.getKey();
                        if (key != EPC) {
                            String value = pair.getValue().toString();
                            //Log.i("jg", value);
                            line = modifyEntry(line, key, value);
                        }
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }
                if (all == null) {
                    all = line + DATA_DIV;
                } else {
                    all = all.concat(line + DATA_DIV);
                }
            }
            write(all, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modify(ArrayList<String> list, Map<INDEX, String> data) {
        for (String epc : list) {
            Map<INDEX, String> d = new HashMap<>(data);
            d.put(EPC, epc);
            modify(d);
        }
    }

    public ArrayList<String> search(INDEX key, String value) {
        ArrayList<String> results = new ArrayList<>();
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String line;
            while ((line = content.readLine()) != null) {
                if (getEntry(line, key).contains(value)) {
                    //Log.i("get", getEntry(line, key));
                    //Log.i("line key", line + " " + key + " " + value);
                    results.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public ArrayList<String> search(Map<INDEX, String> data) {
        ArrayList<String> results = new ArrayList<>();
        Iterator it = data.entrySet().iterator();
        int count = 1;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            INDEX key = (INDEX) pair.getKey();
            String value = pair.getValue().toString();
            if (count == 1) {
                results = search(key, value);
            } else {
                for (int i = 0; i < results.size(); ++i) {
                    if (hasEntry(results.get(i), key) && getEntry(results.get(i), key).contains(value)) {
                        break;
                    } else {
                        results.remove(i);
                    }
                }
            }
            count++;
            it.remove(); // avoids a ConcurrentModificationException
        }
        return results;
    }

    public ArrayList<String> stock(ArrayList<String> list) {
        ArrayList<String> results = new ArrayList<>();
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String line;
            while ((line = content.readLine()) != null) {
                for (int i = 0; i < list.size(); ++i) {
                    String epc = list.get(i);
                    if (getEntry(line, EPC).equals(epc)) {
                        break;
                    } else if (i == (list.size() - 1)) {
                        results.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public ArrayList<String> filter(INDEX key, ArrayList<String> list) {
        ArrayList<String> result = new ArrayList<>();
        for (String line : list) {
            String value = getEntryByEPC(getEntry(line, EPC), key);
            if (!result.contains(line) && (!value.equals("")))
                result.add(value);
        }
        return result;
    }

    public ArrayList<String> getLinebyFilter(final ArrayList<String> data, INDEX key, final String filter) {
        ArrayList<String> result = new ArrayList<>();
        for (String line : data) {
            String epc = getEntry(line, EPC);
            if (filter.equals(getEntryByEPC(epc, key)))
                result.add(line);
        }
        return result;
    }

    private void write(final String content, boolean append) {
        FileOutputStream os;
        try {
            os = new FileOutputStream(csv, append);
            os.write(content.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String modifyEntry(String data, INDEX key, String value) {
        //if (hasEntry(data, key)) {
        String div = key.toString() + KEY_VALUE_DIV;
        //Log.i("aaa", div + "  " + Boolean.toString(data.contains(div)));
        if (data.contains(div)) {
            String old1 = div + data.split(div)[1];
            String old = old1.split(ENTRY_DIV)[0];
            if (value.equals("")) {
                data = data.replaceAll(old + ENTRY_DIV, "");
            } else {
                data = data.replaceAll(old, div + value);
            }
        } else {
            data = data.concat(key.toString() + KEY_VALUE_DIV + value + ENTRY_DIV);
        }
        return data;
    }

    private Map<INDEX, String> textToData(String text) {
        Map<INDEX, String> data = new HashMap<>();
        for (INDEX key : INDEX.values()) {
            if (hasEntry(text, key)) {
                data.put(key, getEntry(text, key));
            }
        }
        return data;
    }

    private String dataToText(Map<INDEX, String> data) {
        Iterator it = data.entrySet().iterator();
        String text = null;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String entry = pair.getKey() + KEY_VALUE_DIV + pair.getValue() + ENTRY_DIV;
            if (text == null) {
                text = entry;
            } else {
                text = text.concat(entry);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        if (text != null) {
            text = text.concat(DATA_DIV);
        }
        return text;
    }
}
