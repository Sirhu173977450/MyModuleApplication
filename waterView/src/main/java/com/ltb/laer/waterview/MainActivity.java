package com.ltb.laer.waterview;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ltb.laer.waterview.model.Water;
import com.ltb.laer.waterview.view.WaterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WaterView mWaterView;

    private List<Water> mWaters = new ArrayList<>();

    {
        for (int i = 0; i <6; i++) {
//            mWaters.add(new Water((int) (i + Math.random() * 4), "item" + i));
            mWaters.add(new Water((int) i ,"item" + i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWaterView = findViewById(R.id.wv_water);
        mWaterView.setWaters(mWaters);
    }

    public void onRest(View view) {
        mWaterView.setWaters(mWaters);
    }
}
