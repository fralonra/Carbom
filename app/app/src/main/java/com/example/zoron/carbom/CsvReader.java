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

import static com.example.zoron.carbom.Entry.INDEX.EPC;
import static com.example.zoron.carbom.Entry.INDEX.LOAN_DATE;
import static com.example.zoron.carbom.Entry.INDEX.NOTE;
import static com.example.zoron.carbom.Utils.binarySearch;

/**
 * Created by zoron on 17-4-14.
 */

public class CsvReader {
    public static final String SAVE_FILE = "data.cbm";

    private WriteThread writeThread = null;
    private File csv = null;
    private ArrayList<Entry> data = null;

    public CsvReader(final String file) {
        csv = new File(Environment.getExternalStorageDirectory(), file);
        writeThread = new WriteThread();
        parseData();
        if (!csv.exists()) {
            try {
                csv.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 从原始文件解析数据
    private Boolean parseData() {
        if (csv == null) return false;
        if (data == null) data = new ArrayList<>();
        try {
            LineNumberReader content = new LineNumberReader(new FileReader(csv));
            String line;
            while ((line = content.readLine()) != null) {
                data.add(new Entry(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 检测是否包含 entry
    public boolean hasEntry(final Map<Entry.INDEX, String> map) {
        String epc = map.get(EPC);
        return hasEntry(epc);
    }

    // 检测是否包含 entry
    public boolean hasEntry(final String epc) {
        for (Entry e : data) {
            if (e.get(EPC).equals(epc)) return true;
        }
        return false;
    }

    // 获取 entry
    public Entry getEntry(final String epc) {
        for (Entry e : data) {
            if (e.get(EPC).equals(epc)) return e;
        }
        return new Entry();
    }

    // 获取单条 block
    public String getValueByEpc(final String epc, final Entry.INDEX key) {
        Entry e = getEntry(epc);
        return e.get(key);
    }

    // 获取多条 block
    public String getValueByList(final ArrayList<String> list, final Entry.INDEX key) {
        String value = "";
        for (String epc : list) {
            if (key == EPC) {
                if (list.indexOf(epc) != list.size() - 1) value += epc + ", ";
                else value += epc;
            } else {
                String v = getValueByEpc(epc, key);
                if (value.equals("")) {
                    value = v;
                } else {
                    if (!v.equals(value)) {
                        return "多个值";
                    }
                }
            }
        }
        return value;
    }

    // 添加单条
    public void append(final Map<Entry.INDEX, String> map) {
        Entry e = new Entry(map);
        data.add(e);
        write(e.toString());
    }

    // 删除单条
    public void remove(final String epc) {
        Entry e = getEntry(epc);
        if (data.contains(e)) data.remove(e);
        write();
    }

    // 修改单条
    public void modify(final Map<Entry.INDEX, String> map) {
        modify(map, true);
    }

    public void modify(final Map<Entry.INDEX, String> map, boolean write) {
        Entry e = getEntry(map.get(EPC));
        for (Object o : map.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Entry.INDEX key = (Entry.INDEX) pair.getKey();
            String value = pair.getValue().toString();
            e.set(key, value);
        }
        if (write) write();
    }

    // 修改多条
    public void modify(final ArrayList<String> list, final Map<Entry.INDEX, String> map) {
        for (String epc : list) {
            map.put(EPC, epc);
            modify(map, false);
        }
        write();
    }

    // 获取全部
    public ArrayList<Entry> all() {
        return data;
    }

    // 获取全部并排序
    public ArrayList<Entry> allBySort() {
        ArrayList<Entry> results = new ArrayList<>();
        ArrayList<String> epcs = new ArrayList<>();

        for (Entry e : data) {
            String epc = e.get(EPC);
            if (results.isEmpty()) {
                results.add(e);
                epcs.add(epc);
            } else {
                int pos = binarySearch(epcs, epc);
                if (pos != -1) {
                    results.add(pos, e);
                    epcs.add(pos, epc);
                }
            }
        }
        return results;
    }

    // 单条搜索
    public Entry search(final String epc) {
        for (Entry e : data) {
            if (e.get(EPC).equals(epc)) {
                return e;
            }
        }
        return null;
    }

    // 单项搜索
    public ArrayList<Entry> search(final Entry.INDEX key, final String value) {
        ArrayList<Entry> results = new ArrayList<>();
        for (Entry e : data) {
            if (e.is(key, value)) {
                results.add(e);
            }
        }
        return results;
    }

    // 多项搜索
    public ArrayList<Entry> search(final Map<Entry.INDEX, String> src) {
        ArrayList<Entry> results = new ArrayList<>();
        Iterator it = src.entrySet().iterator();
        int count = 1;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Entry.INDEX key = (Entry.INDEX) pair.getKey();
            String value = pair.getValue().toString();
            if (count == 1) {
                results = search(key, value);
            } else {
                for (Entry e : results) {
                    if (e.is(key, value)) {
                        break;
                    } else {
                        results.remove(results.indexOf(e));
                    }
                }
            }
            count++;
            it.remove(); // avoids a ConcurrentModificationException
        }
        return results;
    }

    // 盘点
    public ArrayList<Entry> stock(final ArrayList<String> list) {
        ArrayList<Entry> results = new ArrayList<>();
        for (Entry e : data) {
            for (String epc : list) {
                if (e.get(EPC).equals(epc)) {
                    list.remove(epc);
                    break;
                } else if (list.indexOf(epc) == list.size() - 1) {
                    results.add(e);
                }
            }
        }
        return results;
    }

    // 获取过滤条件
    public ArrayList<String> filter(final Entry.INDEX key, final ArrayList<Entry> list) {
        ArrayList<String> result = new ArrayList<>();
        for (Entry e : list) {
            String value = e.get(key);
            if (!result.contains(value) && (!value.equals(""))) {
                result.add(value);
            }
        }
        return result;
    }

    // 过滤条目
    public ArrayList<Entry> getEntrybyFilter(final ArrayList<Entry> list, final Entry.INDEX key, final String filter) {
        ArrayList<Entry> result = new ArrayList<>();
        for (Entry e : list) {
            if (filter.equals(e.get(key))) {
                result.add(e);
            }
        }
        return result;
    }

    // 写入文件
    private void write() {
        writeThread.write(dataToText(), false);
    }

    private void write(final String src) {
        writeThread.write(src, true);
    }

    private String dataToText() {
        StringBuilder sb = new StringBuilder();
        for (Entry e : data) {
            sb.append(e.toString());
        }
        return sb.toString();
    }

    private class WriteThread extends Thread {

        private void write(String content, boolean append) {
            FileOutputStream os;
            try {
                os = new FileOutputStream(csv, append);
                os.write(content.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
