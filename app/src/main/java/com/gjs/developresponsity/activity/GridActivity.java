package com.gjs.developresponsity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.adapter.SwipeGridAdapter;
import com.gjs.developresponsity.model.YourDataObject;

import java.util.ArrayList;
import java.util.List;

public class GridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        GridView gridView = findViewById(R.id.grid);

        ArrayList<YourDataObject> dataObjects = new ArrayList<>();
        for (int i = 0; i < 30 ; i++){
            YourDataObject dataObject = new YourDataObject();
            dataObject.setText("item : " + i);
            dataObjects.add(dataObject);
        }
        gridView.setAdapter(new SwipeGridAdapter(GridActivity.this,dataObjects));
    }
}
