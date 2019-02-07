package com.example.proyectointegrador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.proyectointegrador.adapter.NotasAdapter;
import com.example.proyectointegrador.app.AppController;
import com.example.proyectointegrador.models.Nota;
import com.example.proyectointegrador.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notas extends Activity {
    private TextView usuario,nota,materia;
    private ListView notas;
    private ArrayList<Nota> notasList = new ArrayList<Nota>();
    private NotasAdapter adapter;
    int mat,cod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        usuario = findViewById(R.id.usuario);
        nota = findViewById(R.id.nota);
        materia = findViewById(R.id.materia);
        notas = findViewById(R.id.notas);



        Bundle param = this.getIntent().getExtras();
        String nombre = param.getString("name");
        cod = param.getInt("alumno");
        mat = param.getInt("cod_m");
        String nom_mat = param.getString("nombre");
        usuario.setText(nombre);
        materia.setText(nom_mat);


        new MyTask().execute();


        String url = Helper.GET_USER_SCORE+cod;
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("query");
                    JSONObject object = (JSONObject) array.get(0);
                    String comb = object.getInt("nota") < 7 ? "Reprobado" : "Aprobado";
                    nota.setText(""+object.getInt("nota")+"/"+comb);
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
                        TextView txt = customToast.findViewById(R.id.txttoas);
                        txt.setText(new String(response.data));
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(customToast);
                        toast.show();
                    }
                }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("content-type", "application/json; charset=utf-8");

                return parametros;
            }
        };

        AppController.getInstance().addToRequestQueue(peticion);
    }

    private void getAnswers() {
        try {
            String url = Helper.GET_SCORE;
            JSONObject object = new JSONObject();
            object.put("cod_es",cod);
            object.put("cod_mat",mat);
            adapter = new NotasAdapter(Notas.this,notasList);
            notas.setAdapter(adapter);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("results");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String opcion = object.getString("incorrecto").equals("") ? "Vacío" : object.getString("incorrecto");
                            Nota nota = new Nota(object.getString("correcto"), opcion);
                            notasList.add(nota);
                        }
                        adapter.notifyDataSetChanged();
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
                            TextView txt = customToast.findViewById(R.id.txttoas);
                            txt.setText(new String(response.data));
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(customToast);
                            toast.show();
                        }
                    }
            ){
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("content-type", "application/json; charset=utf-8");

                    return parametros;
                }
            };
            AppController.getInstance().addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getAnswers();
            return null;
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.info)
                .setTitle("¿Estas seguro de Salir?")
                .setMessage("Puedes seguir intentando con otras materias")
                .setCancelable(true)
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }
}
