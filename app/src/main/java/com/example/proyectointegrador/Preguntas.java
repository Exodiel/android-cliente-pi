package com.example.proyectointegrador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.proyectointegrador.app.AppController;
import com.example.proyectointegrador.models.Pregunta;
import com.example.proyectointegrador.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Preguntas extends Activity implements View.OnClickListener {

    private TextView id_mat, nom_mat, tiempo, restante;
    private ProgressBar proceso;
    private ListView contenedor;
    private ArrayList<Pregunta> preguntas = new ArrayList<Pregunta>();
    private CustomAdapter adapter;
    private Button boton;
    private int id_es;
    private ArrayList<String> res;

    private static final String FORMAT = "%02d:%02d:%02d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        id_mat = findViewById(R.id.id_mat);
        nom_mat = findViewById(R.id.nom_mat);
        tiempo = findViewById(R.id.tiempo);
        restante = findViewById(R.id.restante);
        boton = findViewById(R.id.proceso);
        proceso = findViewById(R.id.asincrono);
        contenedor = findViewById(R.id.progreso);

        res = new ArrayList<String>(10);

        Bundle params = getIntent().getExtras();
        int codigo = params.getInt("codigo");
        String titulo = params.getString("nombre");
        id_es = params.getInt("estudiante");
        id_mat.setText("" + codigo);
        nom_mat.setText(titulo);

        proceso.setVisibility(View.INVISIBLE);

        //1200000
        new CountDownTimer(1200000, 1000) {

            public void onTick(long millisUntilFinished) {

                tiempo.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                tiempo.setText("Se terminó el tiempo");
                restante.setText("");
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View customToast = inflater.inflate(R.layout.timeout_layout, null);
                TextView txt = customToast.findViewById(R.id.timeout);
                txt.setText("Se te agoto el tiempo");
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(customToast);
                toast.show();
                finish();
            }
        }.start();


        String url = Helper.GET_ASKS + codigo;
        adapter = new CustomAdapter(Preguntas.this, R.layout.ask_row, preguntas);
        contenedor.setAdapter(adapter);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Pregunta pregunta = new Pregunta(jsonObject.getInt("id_pre"), jsonObject.getInt("cod_mat"), jsonObject.getString("enunciado"), jsonObject.getString("opcion1"), jsonObject.getString("opcion2"), jsonObject.getString("opcion3"), jsonObject.getString("opcion4"));
                        preguntas.add(pregunta);
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
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("content-type", "application/json; charset=utf-8");

                return parametros;
            }
        };


        AppController.getInstance().addToRequestQueue(request);

        boton.setOnClickListener(this);

    }

    private class CustomAdapter extends ArrayAdapter<Pregunta> {
        public CustomAdapter(Context context, int resource, ArrayList<Pregunta> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.ask_row, parent, false);
            TextView cod_pre = view.findViewById(R.id.pregunta);
            TextView enun = view.findViewById(R.id.enunciado);
            TextView mat = view.findViewById(R.id.materia);

            Pregunta pregunta = preguntas.get(position);

            final RadioGroup opciones = view.findViewById(R.id.opciones);
            RadioButton op1 = view.findViewById(R.id.opcion1);
            RadioButton op2 = view.findViewById(R.id.opcion2);
            RadioButton op3 = view.findViewById(R.id.opcion3);
            RadioButton op4 = view.findViewById(R.id.opcion4);

            cod_pre.setText("" + pregunta.getId_pre());
            enun.setText(pregunta.getEnunciado());
            mat.setText("" + pregunta.getCod_mat());

            op1.setText(pregunta.getOpcion1());
            op2.setText(pregunta.getOpcion2());
            op3.setText(pregunta.getOpcion3());
            op4.setText(pregunta.getOpcion4());


            opciones.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int radioButtonID = group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(radioButtonID);

                    int position = group.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) opciones.getChildAt(position);
                    String respuesta = "";
                    if (position == 0) {
                        respuesta = "A";
                    } else if (position == 1) {
                        respuesta = "B";
                    } else if (position == 2) {
                        respuesta = "C";
                    }
                    res.add(respuesta);
                }
            });


            return view;
        }
    }


    private class MyTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proceso.setMax(100);
            proceso.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            proceso.setMax(0);
            proceso.setVisibility(View.INVISIBLE);
            finish();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            proceso.setProgress(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            proceso.setMax(0);
            proceso.setVisibility(View.INVISIBLE);
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
                updateScore();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.info)
                .setTitle("¿Estas seguro de Salir?")
                .setMessage("Al momento de salir no quedará guardado tú progreso")
                .setCancelable(true)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    private void updateScore() throws JSONException {


        String url = Helper.POST_ASKS;
        Pregunta pregunta1 = (Pregunta) adapter.getItem(0);
        Pregunta pregunta2 = (Pregunta) adapter.getItem(1);
        Pregunta pregunta3 = (Pregunta) adapter.getItem(2);
        Pregunta pregunta4 = (Pregunta) adapter.getItem(3);
        Pregunta pregunta5 = (Pregunta) adapter.getItem(4);
        Pregunta pregunta6 = (Pregunta) adapter.getItem(5);
        Pregunta pregunta7 = (Pregunta) adapter.getItem(6);
        Pregunta pregunta8 = (Pregunta) adapter.getItem(7);
        Pregunta pregunta9 = (Pregunta) adapter.getItem(8);
        Pregunta pregunta10 = (Pregunta) adapter.getItem(9);
        int id_m = Integer.parseInt(id_mat.getText().toString());

        JSONObject object1 = new JSONObject();
        object1.put("opcion", res.get(0));
        object1.put("pregunta", pregunta1.getId_pre());
        object1.put("estudiante", id_es);
        object1.put("materia", id_m);

        JSONObject object2 = new JSONObject();
        object2.put("opcion", res.get(1));
        object2.put("pregunta", pregunta2.getId_pre());
        object2.put("estudiante", id_es);
        object2.put("materia", id_m);

        JSONObject object3 = new JSONObject();
        object3.put("opcion", res.get(2));
        object3.put("pregunta", pregunta3.getId_pre());
        object3.put("estudiante", id_es);
        object3.put("materia", id_m);

        JSONObject object4 = new JSONObject();
        object4.put("opcion", res.get(3));
        object4.put("pregunta", pregunta4.getId_pre());
        object4.put("estudiante", id_es);
        object4.put("materia", id_m);

        JSONObject object5 = new JSONObject();
        object5.put("opcion", res.get(4));
        object5.put("pregunta", pregunta5.getId_pre());
        object5.put("estudiante", id_es);
        object5.put("materia", id_m);

        JSONObject object6 = new JSONObject();
        object6.put("opcion", res.get(5));
        object6.put("pregunta", pregunta6.getId_pre());
        object6.put("estudiante", id_es);
        object6.put("materia", id_m);

        JSONObject object7 = new JSONObject();
        object7.put("opcion", res.get(6));
        object7.put("pregunta", pregunta7.getId_pre());
        object7.put("estudiante", id_es);
        object7.put("materia", id_m);

        JSONObject object8 = new JSONObject();
        object8.put("opcion", res.get(7));
        object8.put("pregunta", pregunta8.getId_pre());
        object8.put("estudiante", id_es);
        object8.put("materia", id_m);

        JSONObject object9 = new JSONObject();
        object9.put("opcion", res.get(8));
        object9.put("pregunta", pregunta9.getId_pre());
        object9.put("estudiante", id_es);
        object9.put("materia", id_m);

        JSONObject object10 = new JSONObject();
        object10.put("opcion", res.get(9));
        object10.put("pregunta", pregunta10.getId_pre());
        object10.put("estudiante", id_es);
        object10.put("materia", id_m);

        JSONArray array = new JSONArray();
        array.put(object1);
        array.put(object2);
        array.put(object3);
        array.put(object4);
        array.put(object5);
        array.put(object6);
        array.put(object7);
        array.put(object8);
        array.put(object9);
        array.put(object10);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.POST,
                url,
                array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            String res = object.getString("result");
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
                            Toast.makeText(Preguntas.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            proceso.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            Toast.makeText(Preguntas.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.proceso:

                if (res.size() < 10) {
                    Toast.makeText(this, "Responde todas las preguntas", Toast.LENGTH_SHORT).show();
                } else {
                    /*try {
                        updateScore();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    new MyTask().execute();
                    /*for (int i = 0; i < res.size(); i++) {
                        Toast.makeText(this, ""+res.get(i), Toast.LENGTH_SHORT).show();
                    }*/
                }

        }
    }
}
