package com.example.barcodescanner;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    Button btn_scan;
    public String book_details;
    public String url = "https://script.google.com/macros/s/AKfycbw6fKtTfEqxQeE7GU8tQt247x4PRm46WhVQdPWoIPoJzFX8bcArDN6mv52XvnJz35H-Hw/exec?action=addItem&isbn=";
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v -> {

            scanCode();

        });

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan barcode and see the price");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    public void showDetails(String response,String isbn){
        String toastText;
        if (response.equals("Successfull")){
            toastText = response + "call for isbn :" + isbn;
        }else{
            toastText = "Error for isbn :" + isbn;
        }
        Toast.makeText(this,toastText, Toast.LENGTH_SHORT).show();
    }

    private void sendData(String scan) {

        String book_url = url + scan;
        System.out.println("THIS IS THE URL I HIT -> " + book_url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, book_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("RESPONSE" + response);
                        showDetails(response,scan);
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("THE ERROR IS TRIGGERED: " + error);
                    }
                }
        );

        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        queue.add(stringRequest);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        if (result.getContents() != null) {
            sendData(result.getContents());
            loading = ProgressDialog.show(this,"Loading","please wait",false,true);
        }
    });

}