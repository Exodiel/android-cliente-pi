package com.example.proyectointegrador;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.proyectointegrador.app.AppController;
import com.example.proyectointegrador.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login;
    private ProgressBar progreso;
    private EditText nombre,cedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        nombre = findViewById(R.id.nombre);
        cedula = findViewById(R.id.cedula);
        progreso = findViewById(R.id.progreso);
        login.setOnClickListener(this);

        progreso.setMax(100);
        progreso.setVisibility(View.INVISIBLE);

        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String t = s.toString();
                if (!t.equals(t.toUpperCase())) {
                    t = t.toUpperCase();
                    nombre.setText(t);
                }
                nombre.setSelection(nombre.getText().length());
            }
        });
    }

    private void login() throws JSONException {
        String nom = nombre.getText().toString();
        String ced = cedula.getText().toString();
        String url = Helper.LOGIN;

        final JSONObject jsonObject = new JSONObject("{\"nombre\":\""+nom+"\","
                +"\"cedula\":\""+ced+"\"}");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject result = response.getJSONObject("result");
                            Bundle params = new Bundle();
                            params.putInt("id_es",result.getInt("id_es"));
                            params.putString("nombre",result.getString("nombre"));
                            params.putString("cedula",result.getString("cedula"));
                            Intent intent = new Intent(getApplicationContext(),Home.class);
                            intent.putExtras(params);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View customToast = inflater.inflate(R.layout.toast_layout, null);
                        TextView txt = (TextView) customToast.findViewById(R.id.txttoas);
                        txt.setText(new String(response.data));
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(customToast);
                        toast.show();
                        progreso.setVisibility(View.INVISIBLE);
                    }
                }
        ) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("content-type", "application/json; charset=utf-8");

                    return parametros;
                }
        };

        AppController.getInstance().addToRequestQueue(request);

    }

    private class MyTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progreso.setMax(0);
            progreso.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < 100; i+=10) {
                try {
                    Thread.sleep(5);
                    publishProgress(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                login();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progreso.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progreso.setMax(0);
            progreso.setVisibility(View.INVISIBLE);
            nombre.setText("");
            nombre.requestFocus();
            cedula.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        if (nombre.getText().toString().equals("") || cedula.getText().toString().equals("")){
            Toast.makeText(this, "Llene todos los campos ", Toast.LENGTH_SHORT).show();
        }else {
            switch (v.getId()){
                case R.id.login:
                    new MyTask().execute();
            }
        }

    }
}
