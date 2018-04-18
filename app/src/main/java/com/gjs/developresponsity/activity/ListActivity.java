package com.gjs.developresponsity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.adapter.SwipeListAdapter;
import com.gjs.developresponsity.model.YourDataObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView list = findViewById(R.id.list_view);

        ArrayList<String> dataObjects = new ArrayList<>();
        for (int i = 0; i < 30 ; i++){
            dataObjects.add("item : " + i);
        }
        SwipeListAdapter adapter = new SwipeListAdapter(this, dataObjects);
        list.setAdapter(adapter);

    }
}
