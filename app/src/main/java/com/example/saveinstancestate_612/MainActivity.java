package com.example.saveinstancestate_612;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";

    private static String SAVE_LARGE_TEXT = "save_large_text";
    private static final String BUNDLE_SAVE_KEY_ARRAY_DEL = "save_array_del";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView list;
    private List<Map<String, String>> result = new ArrayList();
    private ArrayList<Integer> arrayListIndex = new ArrayList<>();
    private BaseAdapter adapter;
    private SharedPreferences mySaveSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveLargeText();
        initViews();
    }

    private void initViews() {
        list = findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        adapter = createAdapter(prepareContent());
        list.setAdapter(adapter);
        result = prepareContent();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                result.clear();
                result = prepareContent();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                arrayListIndex.clear();

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        result.remove(position);
                        adapter.notifyDataSetChanged();
                        arrayListIndex.add(position);
                    }
                });
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                result.remove(position);
                adapter.notifyDataSetChanged();
                arrayListIndex.add(position);
            }
        });
    }

    private void saveLargeText() {
        mySaveSharedPref = getSharedPreferences("SaveLargeText", MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySaveSharedPref.edit();
        String arrayContent = getString(R.string.large_text);
        myEditor.putString(SAVE_LARGE_TEXT, arrayContent);
        myEditor.apply();

        String saveLarge = mySaveSharedPref.getString(SAVE_LARGE_TEXT, "");
        Log.i(TAG, "Save largeText" + saveLarge);
    }

    private String[] getLargeText() {
        String getLarge = mySaveSharedPref.getString(SAVE_LARGE_TEXT, "");
        return getLarge.split("\n\n");
    }

    private BaseAdapter createAdapter(List<Map<String, String>> list) {
        String[] from = new String[]{KEY1, KEY2};
        int[] to = new int[]{R.id.text_1, R.id.text_2};
        adapter = new SimpleAdapter(this, list, R.layout.item, from, to);
        return adapter;
    }

    private List<Map<String, String>> prepareContent() {
        String[] array = getLargeText();
        for (String s : array) {
            Map<String, String> map = new HashMap<>();
            map.put(KEY1, s);
            map.put(KEY2, String.valueOf(s.length()));
            result.add(map);
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(BUNDLE_SAVE_KEY_ARRAY_DEL, arrayListIndex);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        arrayListIndex = savedInstanceState.getIntegerArrayList(BUNDLE_SAVE_KEY_ARRAY_DEL);
        for (int listIndex : arrayListIndex) {
            result.remove(listIndex);
            adapter.notifyDataSetChanged();
        }
    }
}
