package com.example.zoron.carbom;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.zoron.carbom.Entry.INDEX.EPC;
import static com.example.zoron.carbom.Entry.INDEX.INDEX_COUNT;
import static com.example.zoron.carbom.Entry.INDEX.LOAN_DATE;

/**
 * Created by zoron on 17-12-18.
 */

public class Entry {
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
        RETURN_DATE,
        INDEX_COUNT
    }

    private ArrayList<String> data = new ArrayList<>();

    private static final String KEY_VALUE_DIV = ":";
    private static final String BLOCK_DIV = "&";
    public static final String ENTRY_DIV = "\n";

    public Entry(String src) {
        for (INDEX key : INDEX.values()) {
            if (key == INDEX_COUNT) break;
            set(key, parse(src, key));
        }
    }

    public Entry(Map<Entry.INDEX, String> map) {
        for (INDEX key : INDEX.values()) {
            if (key == INDEX_COUNT) break;
            if (map.containsKey(key)) {
                set(key, map.get(key));
            } else {
                set(key, "");
            }
        }
    }

    public Entry() {
        for (INDEX key : INDEX.values()) {
            if (key == INDEX_COUNT) break;
            set(key, "");
        }
    }

    // 从原始文本解析数据
    private static String parse(String src, INDEX key) {
        String value = "";
        String div = key.toString() + KEY_VALUE_DIV;
        if (src.contains(div)) {
            if (src.split(div)[1].split(BLOCK_DIV).length > 0)
                value = src.split(div)[1].split(BLOCK_DIV)[0];
        }
        return value;
    }

    // 解析 block
    public static String parseBlock(String src, INDEX key) {
        String div = key.toString() + KEY_VALUE_DIV;
        if (src.contains(div)) {
            return src.split(div)[1].split(BLOCK_DIV)[0];
        }
        return "";
    }

    // 设置数据
    public void set(INDEX key, String value) {
        if (data.size() < INDEX_COUNT.ordinal()) {
            data.add(key.ordinal(), value);
        } else {
            data.set(key.ordinal(), value);
        }
    }

    // 获取数据
    public String get(INDEX key) {
        return data.get(key.ordinal());
    }

    // 输出文本
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (INDEX key : INDEX.values()) {
            if (key == INDEX_COUNT) break;
            sb.append(key.toString()).append(KEY_VALUE_DIV).append(get(key)).append(BLOCK_DIV);
        }
        sb.append(ENTRY_DIV);
        return sb.toString();
    }

    // 是否满足K-V
    public Boolean is(INDEX key, String value) {
        return data.get(key.ordinal()).toLowerCase().contains(value.toLowerCase());
    }
}
