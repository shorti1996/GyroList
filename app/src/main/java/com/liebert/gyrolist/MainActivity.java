package com.liebert.gyrolist;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liebert.gyrolist.Views.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private float[] localUp;
    private boolean calibrate = false;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    SensorEventListener mAccelerometerEventListener;

//    List<Integer> placeholderList = new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    List<ApplicationInfo> appList;

    @BindView(R.id.main_list_rv)
    RecyclerView mainRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccelerometerEventListener = new MyAccelerometerListener(this);

        localUp = new float[]{0,-1,0};

        ButterKnife.bind(this);

        setupRv();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // TODO someday
    // maybe
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_calibrate:
                calibrate = true;
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAccelerometerListener implements SensorEventListener {
        Context mContext;

        public MyAccelerometerListener(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if (calibrate) {
                    localUp[1] = Utils.normalizeVector(event.values)[1];
                    calibrate = false;
                }

                float[] scr = Utils.canonicalOrientationToScreenOrientation(event.values, mContext);
                float[] wrld = Utils.canonicalToWorld(Utils.getDisplayOrientation(mContext), event.values);
                android.support.v4.util.Pair p = Utils.computeAxisAngle(localUp, scr);
                float angle = (float) Math.toDegrees((float) p.second);
                float dir = Math.signum(((float[]) p.first)[2]);
                Log.d("Aaa:", "Angle: " + angle * dir);

                if (angle <= 20) {
                    return;
                } else {
                    angle = angle - 10f;
                    angle = angle * angle / 2;

                }

                float angleDir = angle * dir;
                mainRv.smoothScrollBy((int) angleDir, (int) angleDir);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    void setupRv() {
        appList = getPackageManager().getInstalledApplications(0);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mainRv.setLayoutManager(layoutManager);
        mainRv.setAdapter(new ItemsAdapter(this, appList, mainRv));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mAccelerometerEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mAccelerometerEventListener);
    }

    class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {
        Context mContext;
        List<ApplicationInfo> mItemsList;
        RecyclerView mRecyclerView;

        public ItemsAdapter(Context mContext, List<ApplicationInfo> mItemsList, RecyclerView mRecyclerView) {
            this.mContext = mContext;
            this.mItemsList = mItemsList;
            this.mRecyclerView = mRecyclerView;
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
            ApplicationInfo applicationInfo = appList.get(position);
            Drawable icon = applicationInfo.loadIcon(getPackageManager());
//            Bitmap iconBitmap = Utils.getAppIconBitmap(icon);
//            holder.itemSiv.setImageBitmap(icon.getBitmap());
            holder.itemSiv.setImageDrawable(icon);
            holder.titleTv.setText(applicationInfo.loadLabel(getPackageManager()));
        }

        @Override
        public int getItemCount() {
            return mItemsList.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.item_icon)
            SquareImageView itemSiv;

            @BindView(R.id.item_title)
            TextView titleTv;

            public ItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, titleTv.getText(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
