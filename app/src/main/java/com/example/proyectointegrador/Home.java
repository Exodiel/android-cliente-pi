package com.example.proyectointegrador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.proyectointegrador.adapter.CustomListAdapter;
import com.example.proyectointegrador.app.AppController;
import com.example.proyectointegrador.models.Materia;
import com.example.proyectointegrador.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends Activity {

    private TextView id_es, nombre, cedula;
    private ListView container;
    private Button calcular,mirar;
    private ProgressBar asincrono;
    private ArrayList<Materia> matLis = new ArrayList<>();
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        id_es = findViewById(R.id.id_es);
        nombre = findViewById(R.id.nombre);
        cedula = findViewById(R.id.cedula);
        container = findViewById(R.id.list);
        calcular = findViewById(R.id.calcular);
        mirar = findViewById(R.id.mirar);
        asincrono = findViewById(R.id.asincrono);

        Bundle params = this.getIntent().getExtras();
        final int id = params.getInt("id_es");
        final String nom = params.getString("nombre");
        String ced = params.getString("cedula");

        id_es.setText("" + id);
        nombre.setText(nom);
        cedula.setText(ced);


        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getSubjects();
                        }catch (Exception e) {
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

        timer.schedule(task,0,10000);


        container.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    Materia materia = (Materia) parent.getItemAtPosition(position);
                    if(materia.getTotal() > 0){
                        Toast.makeText(Home.this, "Ya resolviste esta prueba: "+materia.getNombre(), Toast.LENGTH_SHORT).show();
                    }else {
                        Bundle params = new Bundle();
                        params.putInt("estudiante",Integer.parseInt(id_es.getText().toString()));
                        params.putString("nombre",materia.getNombre());
                        params.putInt("codigo",(position+1));
                        Intent intent = new Intent(Home.this,Preguntas.class);
                        intent.putExtras(params);
                        startActivity(intent);
                    }



                } catch (Exception e){
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        asincrono.setVisibility(View.INVISIBLE);
        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (matLis.isEmpty()) {
                        Toast.makeText(Home.this, "Espera un momento por favor.", Toast.LENGTH_SHORT).show();
                    } else {

                        Materia nota1 = (Materia) adapter.getItem(0);
                        Materia nota2 = (Materia) adapter.getItem(1);
                        Materia nota3 = (Materia) adapter.getItem(2);
                        Materia nota4 = (Materia) adapter.getItem(3);
                        if (nota1.getTotal() <= 0 || nota2.getTotal() <= 0 || nota3.getTotal() <= 0 || nota4.getTotal() <= 0) {
                            Toast.makeText(Home.this, "Primero debes responder las preguntas de cada materia", Toast.LENGTH_LONG).show();
                        }else {
                            new Mytask().execute();
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matLis.isEmpty()) {
                    Toast.makeText(Home.this, "Espera un momento por favor.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Materia nota1 = (Materia) adapter.getItem(0);
                        Materia nota2 = (Materia) adapter.getItem(1);
                        Materia nota3 = (Materia) adapter.getItem(2);
                        Materia nota4 = (Materia) adapter.getItem(3);
                        if (nota1.getTotal() <= 0 || nota2.getTotal() <= 0 || nota3.getTotal() <= 0 || nota4.getTotal() <= 0) {
                            Toast.makeText(Home.this, "Primero debes responder las preguntas de cada materia", Toast.LENGTH_LONG).show();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putInt("cod_es",Integer.parseInt(id_es.getText().toString()));
                            bundle.putString("nombre",nombre.getText().toString());
                            bundle.putInt("cod_mat",nota1.getId_mat());
                            bundle.putInt("cod_lit",nota2.getId_mat());
                            bundle.putInt("cod_est",nota3.getId_mat());
                            bundle.putInt("cod_bio",nota4.getId_mat());
                            Intent intent = new Intent(Home.this,Mitad.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    private void postScore() throws JSONException {
        String url = Helper.POST_SUBJECTS_SCORE;
        JSONObject object = new JSONObject();
        Materia nota1 = (Materia) adapter.getItem(0);
        Materia nota2 = (Materia) adapter.getItem(1);
        Materia nota3 = (Materia) adapter.getItem(2);
        Materia nota4 = (Materia) adapter.getItem(3);
        object.put("id_es",Integer.parseInt(id_es.getText().toString()));
        object.put("nota1",(int) nota1.getTotal());
        object.put("nota2",(int) nota2.getTotal());
        object.put("nota3",(int) nota3.getTotal());
        object.put("nota4",(int) nota4.getTotal());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String res = response.getString("message");
                            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            View custom = inflater.inflate(R.layout.terminado, null);
                            TextView txt = custom.findViewById(R.id.done);
                            txt.setText(res);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(custom);
                            toast.show();
                        } catch (JSONException e) {
                            Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
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
                        } catch (Exception e) {
                            Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
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

    private class Mytask extends AsyncTask<Void,Integer,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asincrono.setMax(100);
            asincrono.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            asincrono.setMax(0);
            asincrono.setVisibility(View.INVISIBLE);
            calcular.setClickable(false);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            asincrono.setMax(0);
            asincrono.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < 100; i += 10) {
                try {
                    Thread.sleep(5);
                    publishProgress(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                postScore();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void getSubjects () {
        matLis.removeAll(matLis);
        String url = Helper.GET_SUBJECTS + id_es.getText();
        adapter = new CustomListAdapter(Home.this, matLis);
        container.setAdapter(adapter);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Materia materia = new Materia(jsonObject.getInt("id_mat"),
                                jsonObject.getString("nom_mat"),
                                jsonObject.getString("imagen"),
                                jsonObject.getDouble("total"));
                        matLis.add(materia);
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
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("content-type", "application/json; charset=utf-8");

                return parametros;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private void getUserScore(){}

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