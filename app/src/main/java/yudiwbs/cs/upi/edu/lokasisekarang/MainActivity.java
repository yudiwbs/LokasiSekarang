package yudiwbs.cs.upi.edu.lokasisekarang;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST = 99 ; //integer bebas, tapi maks 1 byte
    boolean ijin = false; //sudah mendapat ijin untuk mengakses lokasi
    GoogleApiClient mGoogleApiClient ;
    Location mLastLocation;
    TextView mLatText;
    TextView mLongText;

    @Override
    protected void onStart() {
        super.onStart();
        if (ijin) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (ijin) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("yw", "mulai!!");
        mLatText =  (TextView) findViewById(R.id.tvLat);
        mLongText =  (TextView) findViewById(R.id.tvLong);

        //cek permission
        //mulai dari Android 6: API 23, persmission dilakukan secara dinamik (tidak diawal lagi saat install)
        //untuk jenis2 persmisson tertentu, termasuk lokasi

        if (ContextCompat.checkSelfPermission(this,
                //hati2, jika konstanta tidak cocok, tidak ada runtimeerror
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
                    // MY_PERMISSIONS_REQUEST adalah konstanta, nanti digunakan di  onRequestPermissionsResult

        } else {
            //sudah diijinkan
            ijin = true;
            buildGoogleApiClient();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //permission diberikan, mulai ambil lokasi
                buildGoogleApiClient();
                ijin = true;
            } else {
                //permssion tidak diberikan, tampilkan pesan
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Tidak mendapat ijin, tidak dapat mengambil lokasi");
                ad.show();
            }
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("yw", "connected!!");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("yw", "suspend!!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("yw", "failed!!");
    }




}
