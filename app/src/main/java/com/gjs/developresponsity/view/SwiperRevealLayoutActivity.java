package com.gjs.developresponsity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.activity.GridActivity;
import com.gjs.developresponsity.activity.ListActivity;
import com.gjs.developresponsity.activity.RecyclerActivity;

public class SwiperRevealLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiper_reveal_layout);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_recycler_view:
                startActivity(new Intent(this, RecyclerActivity.class));
                return true;

            case R.id.action_list_view:
                startActivity(new Intent(this, ListActivity.class));
                return true;

            case R.id.action_grid_view:
                startActivity(new Intent(this, GridActivity.class));
                return true;
        }

        return false;
    }

    public void layoutfour(View view){
        toast("layout4");
    }

    public void layoutthree(View view){
        toast("layout3");
    }

    public void layouttwo(View view){
        toast("layout2");
    }

    public void layoutone(View view){
        toast("layout1");
    }

    public void deletefour(View view){
        toast("star");
    }

    public void deletethree(View view){
        toast("search");
    }

    public void deletetwo(View view){
        toast("delete2");
    }

    public void deleteone(View view){
        toast("delete1");
    }

    public void more(View view){
        toast("more");
    }

    private void toast(String info) {
        Toast.makeText(SwiperRevealLayoutActivity.this, info, Toast.LENGTH_SHORT).show();
    }

}
