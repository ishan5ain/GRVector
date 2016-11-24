package in.ishansa.grvector;

import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class MobileActivity extends AppCompatActivity implements DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "MobileActivity";
    private static final int FROM_RAD_TO_DEG = -57;
    private GoogleApiClient mGoogleApiClient;
    OpenGLRenderer renderer = new OpenGLRenderer();
    SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Code for OpenGL Surface View
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GLSurfaceView surfaceView = new GLSurfaceView(this);
        surfaceView.setRenderer(renderer);
        setContentView(surfaceView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Log.d(TAG, "onPause: DataApi Listener removed...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.d(TAG, "onConnected: DataApi Listener added...");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: mGoogleApiClient connection suspended...");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: mGoogleApiClient connection failed...");

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged: initiated...");
        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "onDataChanged: Data Item Changed");
                DataItem dataItem = dataEvent.getDataItem();
                if (dataItem.getUri().getPath().compareTo("/grVector") == 0) {
                    Log.d(TAG, "onDataChanged: Path Matched");
                    DataMap mDataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    unpackSensorData(mDataMap);
                }
            }
        }

    }

    private void unpackSensorData(DataMap datamap) {
        Log.d(TAG, "unpackSensorData: starting to unpack DataMap received on Handheld");

        float[] values = datamap.getFloatArray("values");

        float[] rotationMatrix = new float[9];
        sensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        float[] orientationMatrix = new float[3];
        sensorManager.getOrientation(rotationMatrix, orientationMatrix);

        float azimuth = orientationMatrix[0] * FROM_RAD_TO_DEG;
        float pitch = orientationMatrix[1] * FROM_RAD_TO_DEG;
        float roll = orientationMatrix[2] * FROM_RAD_TO_DEG;

        renderer.setXYZRot(pitch, roll, -azimuth);
    }

}

