package io.github.bartoshr.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.github.bartoshr.dayactivityview.DayActivityView;

public class MainActivity extends AppCompatActivity {

    DayActivityView dayActivityView;

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dayActivityView = (DayActivityView) findViewById(R.id.dayActivityView);
        dayActivityView.enable(2);
        dayActivityView.enable(5);
        dayActivityView.enable(0);
        dayActivityView.setCurrent(4);

        dayActivityView.setOnTileClick(new DayActivityView.OnTileClick() {
            @Override
            public void onTileClick(int id) {
                dayActivityView.setCurrent(id);
            }
        });

        Log.d(TAG, "onCreate");
    }
}
