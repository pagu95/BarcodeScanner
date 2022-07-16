package com.example.barcodescanner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button btn_scan;
    public String book_details;
    public String url = "https://script.google.com/macros/s/AKfycbyTUIgpvf2ytNTWC-5_VlzIG5xFODwLTE5_29AntcNieZmvTW-RhN3lHNv_CiEgBfr0TQ/exec?action=getItems&isbn=";

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

    public void showDetails(String book_details){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Result");
        builder.setMessage(book_details);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private String fetchDetails(String bookString) {
        String title = null;
        String price = null;
        String supplier = null;
        String details = "Book Not Found";
        try {

            if (!bookString.contains("[]")) {

                JSONObject bookJson = new JSONObject(bookString);
                JSONArray barcode = bookJson.getJSONArray("barcodes");
                title = barcode.getJSONObject(0).getString("title");
                price = Long.toString(barcode.getJSONObject(0).getLong("price"));
                supplier = barcode.getJSONObject(0).getString("supplier");

                details = title + " ΤΙΜΗ: " + price + " ΠΡΟΜΗΘΕΥΤΗΣ: " + supplier;

            } else {
                System.out.println("THIS IS THE ELSE RESULT ->" + bookString);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }

    private void getdata(String scan) {

        String book_url = url + scan;
        System.out.println("THIS IS THE URL I HIT -> " + book_url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, book_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("RESPONSE" + response);
                        book_details = fetchDetails(response);
                        showDetails(book_details);
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
            getdata(result.getContents());
        }
    });

}