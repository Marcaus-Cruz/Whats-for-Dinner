package edu.wwu.csci412.whatsfordinner;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class APICall {
    private final String url;

    public APICall(String url) {
        this.url = url;
    }

    public void getString(APICallback callback) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("APICall", response);
                    callback.call(response);
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("APICall", "VolleyError: "+error.getMessage());
            }
        });

        // Schedule the call
        MainActivity.volleyQueue.add(stringRequest);
    }
}
