package in.ishansa.grvector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gameRotationVectorSensor;
    private TextView xValue = null;
    private TextView yValue = null;
    private TextView zValue = null;
    private String TAG = "WEAR LOG :";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                xValue = (TextView) stub.findViewById(R.id.x1);
                xValue.setText("xxx");
                yValue = (TextView) stub.findViewById(R.id.y1);
                yValue.setText("yyy");
                zValue = (TextView) stub.findViewById(R.id.z1);
                zValue.setText("zzz");
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gameRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startMeasurement();
    }

    public void startMeasurement() {

        if (gameRotationVectorSensor != null) {
            sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "startMeasurement: measurement started");
        } else {
            Log.w(TAG, "Game Rotation Vector Sensor not found");
        }

    }

    private void stopMeasurement() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "stopMeasurement: measurement stopped.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        sendGameRotVector(sensorEvent.sensor.getType(), sensorEvent.accuracy, sensorEvent.timestamp, sensorEvent.values);
        printData(sensorEvent.values);
        Log.d(TAG, "onSensorChanged: X = " + sensorEvent.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void printData(float[] values) {
        Log.d(TAG, "printData: " + values[0]);
        try {
            xValue.setText(Float.toString(values[0]));
            yValue.setText(Float.toString(values[1]));
            zValue.setText(Float.toString(values[2]));
        } catch (NullPointerException e){
            Log.d(TAG, "printData: NULL POINTER EXCEPTION");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onResume: measurement started");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            stopMeasurement();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
