package com.cafeteria.cafeteria_store.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cafeteria.cafeteria_store.R;
import com.cafeteria.cafeteria_store.utils.ApplicationConstant;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQRcodeActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler   {


    private ZXingScannerView scannerView;
    private int orderNumber;
    private String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        scannerView = new ZXingScannerView(this);
        scannerView.stopCamera();
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        orderNumber = getIntent().getIntExtra("order_number",0);
    }


    @Override
    public void handleResult(Result result) {
        Log.e("handler", result.getText()); // Prints scan results
        Log.e("handler", result.getBarcodeFormat().toString()); // Prints the scan format
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String match = ApplicationConstant.ORDER_DONT_MATCH;
        if( result.getText().equalsIgnoreCase(""+orderNumber)) {
            match = ApplicationConstant.ORDER_MATCH;
        }
        resultText = match;
        builder.setTitle("Order Number : " + orderNumber);
        builder.setMessage(match);
        AlertDialog alert1 = builder.create();
        alert1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Intent data = new Intent();
                data.setData(Uri.parse(resultText));
                setResult(RESULT_OK, data);
                finish();
//                Intent intent = new Intent(getActivity(),MainActivity.class);
//                intent.putExtra("tab",2);
//                startActivity(intent);
            }
        });
        alert1.show();
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        if( resultText == null ) {
            setResult(RESULT_CANCELED, null);
        } else {
            data.setData(Uri.parse(resultText));
            setResult(RESULT_OK, data);
        }
        scannerView.stopCamera();
        finish();
        super.onBackPressed();
    }
}
