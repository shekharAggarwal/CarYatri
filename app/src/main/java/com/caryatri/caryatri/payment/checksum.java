package com.caryatri.caryatri.payment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.R;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    String custid = "", orderId = "", mid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
    }

    @Override
    public void networkNotAvailable() {
        setContentView(R.layout.layout_no_internet);
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true " + s + "  s1 " + s1);
        Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        onBackPressed();
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
