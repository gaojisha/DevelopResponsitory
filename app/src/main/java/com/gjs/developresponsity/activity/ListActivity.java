package com.gjs.developresponsity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.adapter.SwipeListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    private SwipeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView list = findViewById(R.id.list_view);

        ArrayList<String> dataObjects = new ArrayList<>();
        for (int i = 0; i < 30 ; i++){
            dataObjects.add("item : " + i);
        }
        HashMap<String,Boolean> selectedList = new HashMap<>();
        mAdapter = new SwipeListAdapter(this, dataObjects, selectedList);
        list.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_select_all:
                if("编辑".equals(item.getTitle().toString())) {
                    item.setTitle("全选");
                    mAdapter.setCheckVisible(true);
                } else if("全选".equals(item.getTitle().toString())) {
                    mAdapter.setSelectAll(true);
//                    mAdapter.selectAll();
                    item.setTitle("取消");
                } else if("取消".equals(item.getTitle().toString())) {
                    mAdapter.setSelectAll(false);
                    mAdapter.setCheckVisible(false);
                    item.setTitle("编辑");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
