package com.yinhao.arcviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArcView mArcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArcView = (ArcView) findViewById(R.id.arcview);

        List<Times> times = new ArrayList<>();
        for (int i = 6; i > 0; i--) {
            Times t = new Times();
            t.hour = i;
            t.text = "Number" + i;
            times.add(t);
        }

        ArcView.ArcViewAdapter<Times> adapter = mArcView.new ArcViewAdapter<Times>() {

            @Override
            public double getValue(Times times) {
                return times.hour;
            }

            @Override
            public String getText(Times times) {
                return times.text;
            }
        };
        adapter.setData(times);
        mArcView.setMaxNum(5);
        mArcView.setRadius(100);
    }

    class Times {
        int hour;
        String text;
    }
}
