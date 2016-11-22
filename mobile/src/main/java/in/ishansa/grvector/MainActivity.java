package in.ishansa.grvector;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
private static final String TAG = "MainActivity";
    private TextView xVal;
    private TextView yVal;
    private TextView zVal;
    private TextView timestampReading;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xVal = (TextView) findViewById(R.id.xReading);
        yVal = (TextView) findViewById(R.id.yReading);
        zVal = (TextView) findViewById(R.id.zReading);
        timestampReading = (TextView) findViewById(R.id.readingTimestamp);

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
        for(DataEvent dataEvent : dataEventBuffer){
            if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                Log.d(TAG, "onDataChanged: Data Item Changed");
                DataItem dataItem = dataEvent.getDataItem();
                if(dataItem.getUri().getPath().compareTo("/grVector")==0){
                    Log.d(TAG, "onDataChanged: Path Matched");
                    DataMap mDataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    unpackSensorData(mDataMap);
                }
            }
        }

    }

    private void unpackSensorData(DataMap datamap){
        Log.d(TAG, "unpackSensorData: starting to unpack DataMap received on Handheld");

        long timestamp = datamap.getLong("timestamp");
        int accuracy = datamap.getInt("accuracy");
        float[] values = datamap.getFloatArray("values");

        timestampReading.setText(Long.toString(timestamp));
        xVal.setText(Float.toString(values[0]));
        yVal.setText(Float.toString(values[1]));
        zVal.setText(Float.toString(values[2]));
    }

}

