package com.gjs.developresponsity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.adapter.SwipeRecyclerAdapter;
import com.gjs.developresponsity.model.YourDataObject;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerActivity.this));
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        ArrayList<YourDataObject> dataObjects = new ArrayList<>();
        for (int i = 0; i < 30 ; i++){
            YourDataObject dataObject = new YourDataObject();
            dataObject.setText("item : " + i);
            dataObjects.add(dataObject);
        }
        SwipeRecyclerAdapter recyclerAdapter = new SwipeRecyclerAdapter(RecyclerActivity.this, dataObjects);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
