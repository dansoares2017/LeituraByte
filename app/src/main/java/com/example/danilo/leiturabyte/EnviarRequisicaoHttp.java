package com.example.danilo.leiturabyte;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Danilo on 01/08/2017.
 */

public class EnviarRequisicaoHttp
{
    Context contexto;
    public EnviarRequisicaoHttp(Context context)
    {
        this.contexto = context;
    }
    private boolean Resposta=false;

    public void GetResponse(String Url,final VolleyCallback callback)
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(contexto);
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        callback.onSuccessResponse(response);
                        Log.d("reso_tv", String.valueOf(response.length()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        error.printStackTrace();
                        Toast.makeText(contexto, error + "error", Toast.LENGTH_LONG).show();
                    }
                })
        {
            // set headers
            @Override
            public Map< String, String > getHeaders() throws com.android.volley.AuthFailureError {
                Map < String, String > params = new HashMap< String, String >();
                params.put("Authorization: Basic", "Algo");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
