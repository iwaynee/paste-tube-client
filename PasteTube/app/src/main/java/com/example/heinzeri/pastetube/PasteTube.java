package com.example.heinzeri.pastetube;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class PasteTube {
    private static String TAG = "PasteTube";
    private static String SERVER = "https://paste-tube.herokuapp.com";

    private RequestQueue requestQueue;

    public PasteTube(Context p_context) {

        // Setup Request Queue
        requestQueue = Volley.newRequestQueue(p_context);
    }

    public String CreateUser(){
        JSONObject response = MakeRequest("/CreateUser");



        // Get Userid
        try {
            String userid = response.getString("userid");
            Log.v(TAG,"Userid: " + userid );
            //Toast.makeText(context, "Userid: " + userid , Toast.LENGTH_SHORT).show();
            return userid;

        } catch (JSONException e) {
            Log.v(TAG,"There's no Userid..." );
            //Toast.makeText(context, "There's no Userid..." , Toast.LENGTH_SHORT).show();

        }

        return  null;
    }

    public Boolean Connect( String userid)
    {
        JSONObject response = MakeRequest("/Connect?mac=xyz&userid=" + userid);

        if (response != null)
        {
            try {
                String state = response.getString("state");
                Log.v(TAG, "Connected! " + userid);

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public Boolean Copy(String userid, String data){
        JSONObject response = MakeRequest("/Copy?userid=" + userid +"&data=" + data);

        if (response != null)
        {
            try {
                String state = response.getString("state");
                Log.v(TAG, "Copied! " + userid);

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*
        , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(context, "Copied!" + userid , Toast.LENGTH_SHORT).show();
            }
        });
        */
        return false;
    }

    public String Paste(String userid){

        JSONObject response = MakeRequest("/Paste?userid=" + userid);

        if (response != null){
            try {
                String data = response.getString("data");
                return data;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public JSONArray GetConnectedDevices(String userid) {

        JSONObject response = MakeRequest("/GetConnectedDevices?userid="+userid);
        if (response != null) {
            try {
                JSONArray data = response.getJSONArray("devices");
                return data;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    public JObject GetConnectedDevices()
    {
        // GetConnectedDevices
        JObject response = PasteTube.MakeRequest("/GetConnectedDevices?userid=" + userid);
        return response;
    }

    public JObject Copy( string data)
    {
        // Copy
        JObject response = PasteTube.MakeRequest("/Copy?userid=" + userid+"&data=" + data);
        return response;
    }

    public JObject Paste()
    {
        // Paste
        JObject response = PasteTube.MakeRequest("/Paste?userid=" + userid);
        return response;
    }

    */
    private JSONObject MakeRequest(String api_call){
        Log.v(TAG, api_call);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        // Request a string response from the provided URL.
        JsonRequest request = new JsonObjectRequest(
                Request.Method.GET,
                SERVER + api_call,
                null,
                future,
                future);
        /*
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String state = response.getString("state");

                            // Check if there is an error
                            if (state.equals("1")) {
                                Log.v(TAG, response.toString());
                                callback.onResponse(response);

                            } else {
                                String error = response.getString("error");
                                Toast.makeText(context, "Error: " + error , Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Response is not a valid JSON: " + e , Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                        Log.v(TAG, "Could not build up a connection");
                    }
                }
        );
        */
        requestQueue.add(request);

        try {
            JSONObject response = future.get();

            try {
                String state = response.getString("state");

                // Check if there is an error
                if (!state.equals("1")) {
                    String error = response.getString("error");
                    Log.v(TAG, "Error: " + error);
                }

                return response;

            } catch (JSONException e) {
                e.printStackTrace();
                Log.v(TAG,  "Response is not a valid JSON: " + e );
            }
        } catch (InterruptedException e ) {
            e.printStackTrace();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}