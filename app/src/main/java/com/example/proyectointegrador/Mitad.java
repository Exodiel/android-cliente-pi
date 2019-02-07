package com.example.proyectointegrador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Mitad extends Activity {
    Button mate,lite,est,bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitad);

        mate = findViewById(R.id.mate);
        lite = findViewById(R.id.lite);
        est = findViewById(R.id.est);
        bio = findViewById(R.id.bio);

        Bundle bundle = this.getIntent().getExtras();
        final String nombre = bundle.getString("nombre");
        final int cod = bundle.getInt("cod_es");
        final int mat1 = bundle.getInt("cod_mat");
        final int mat2 = bundle.getInt("cod_lit");
        final int mat3 = bundle.getInt("cod_est");
        final int mat4 = bundle.getInt("cod_bio");

        mate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle param = new Bundle();
                param.putInt("cod_m",mat1);
                param.putInt("alumno",cod);
                param.putString("name",nombre);
                param.putString("nombre","Matematicas");
                Intent intent = new Intent(Mitad.this,Notas.class);
                intent.putExtras(param);
                startActivity(intent);
            }
        });

        lite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle param = new Bundle();
                param.putInt("cod_m",mat2);
                param.putInt("alumno",cod);
                param.putString("name",nombre);
                param.putString("nombre","Lengua y Literatura");
                Intent intent = new Intent(Mitad.this,Notas.class);
                intent.putExtras(param);
                startActivity(intent);
            }
        });

        est.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle param = new Bundle();
                param.putInt("cod_m",mat3);
                param.putInt("alumno",cod);
                param.putString("name",nombre);
                param.putString("nombre","Estudios Sociales");
                Intent intent = new Intent(Mitad.this,Notas.class);
                intent.putExtras(param);
                startActivity(intent);
            }
        });

        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle param = new Bundle();
                param.putInt("cod_m",mat4);
                param.putInt("alumno",cod);
                param.putString("name",nombre);
                param.putString("nombre","Biologia");
                Intent intent = new Intent(Mitad.this,Notas.class);
                intent.putExtras(param);
                startActivity(intent);
            }
        });
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
