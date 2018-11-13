package com.example.mani.pizza;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.barcode.BarcodeReader;

public class ScanPage extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    private final String TAG = "ScanPage";
    BarcodeReader mBarcodeReader;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mProgressDialog = new ProgressDialog(ScanPage.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressDialog.setMessage("Please Wait");

        mBarcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
    }

    @Override
    public void onScanned(final Barcode barcode) {

        mBarcodeReader.playBeep();

        ScanPage.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ScanPage.this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                mProgressDialog.show();
                sendToDatabase(barcode.displayValue);

            }
        });
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + errorMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCameraPermissionDenied() {

    }

    private void sendToDatabase(final String displayValue) {

        Log.e(TAG,"sendToDatabase is called");
        final String URL = "http://192.168.1.100/project/get_barcode.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("tag",response.toString());
                mProgressDialog.dismiss();
                Toast.makeText(ScanPage.this,"Successfully send to database",Toast.LENGTH_SHORT).show();
                finish();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse "+error.toString());
                mProgressDialog.dismiss();
                Toast.makeText(ScanPage.this,"Can't send to database",Toast.LENGTH_SHORT).show();
                finish();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("value",displayValue);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        4000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(ScanPage.this).addToRequestQueue(stringRequest);
    }
}
