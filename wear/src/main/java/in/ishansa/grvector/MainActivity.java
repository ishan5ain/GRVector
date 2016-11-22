package in.ishansa.grvector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SensorManager sensorManager;
    private Sensor gameRotationVectorSensor;
    private TextView xValue = null;
    private TextView yValue = null;
    private TextView zValue = null;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "WearActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                xValue = (TextView) stub.findViewById(R.id.x1);
                //xValue.setText("xxx");
                yValue = (TextView) stub.findViewById(R.id.y1);
                //yValue.setText("yyy");
                zValue = (TextView) stub.findViewById(R.id.z1);
                //zValue.setText("zzz");
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gameRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startMeasurement();

    }

    private void startMeasurement() {

        if (gameRotationVectorSensor != null) {
            sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

        sendGameRotVector(sensorEvent.sensor.getType(), sensorEvent.accuracy, sensorEvent.timestamp, sensorEvent.values);
//        Log.d(TAG, "onSensorChanged: X = " + sensorEvent.values[0]);
        try {
            xValue.setText(Float.toString(sensorEvent.values[0]));
            yValue.setText(Float.toString(sensorEvent.values[1]));
            zValue.setText(Float.toString(sensorEvent.values[2]));
        } catch (NullPointerException e) {
            Log.d(TAG, "onSensorChanged: Null Poiner Exception in printing values on Wear.");
            e.printStackTrace();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.d(TAG, "onStart: mGoogleApiClient connected...");
//        mTeleportClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onResume: measurement started...");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            stopMeasurement();
            Log.d(TAG, "onPause: measurement paused...");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        Log.d(TAG, "onStop: mGoogleApiClient disconnected...");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: GoogleApiClient (Wear) connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: GoogleApiClient (Wear) suspended.");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: GoogleApiClient (Wear) failed to connect.");

    }

    private void sendGameRotVector(final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        Log.d(TAG, "sendGameRotVector: sendGameRotVector initiated...");
        PutDataMapRequest mDataMap = PutDataMapRequest.create("/grVector");

        mDataMap.getDataMap().putInt("accuracy", accuracy);
        mDataMap.getDataMap().putLong("timestamp", timestamp);
        mDataMap.getDataMap().putFloatArray("values", values);

        PutDataRequest request = mDataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess())
                            Log.d(TAG, "onResult: Failed to send grvector Data");
                        else
                            Log.d(TAG, "onResult: Successful in sending grvector Data ");
                    }
                });
    }
}
