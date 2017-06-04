package com.liebert.gyrolist;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liebert.gyrolist.Views.SquareImageView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    List<Integer> placeholderList = new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5));

    @BindView(R.id.main_list_rv)
    RecyclerView mainRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ButterKnife.bind(this);

        setupList();
    }

    void setupList() {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mainRv.setLayoutManager(layoutManager);
        mainRv.setAdapter(new ItemsAdapter(this, placeholderList));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        Context mContext;
        List<Integer> mItemsList;

        public ItemsAdapter(Context context, List<Integer> items) {
            mContext = context;
            mItemsList = items;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_vh, parent, false);

            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
//            holder.itemSiv;
        }

        @Override
        public int getItemCount() {
            return mItemsList.size();
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_icon)
        SquareImageView itemSiv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
