package com.example.proyectointegrador.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.proyectointegrador.R;
import com.example.proyectointegrador.models.Nota;

import java.util.List;

public class NotasAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Nota> notas;

    public NotasAdapter(Activity activity, List<Nota> notas) {
        this.activity = activity;
        this.notas = notas;
    }

    @Override
    public int getCount() {
        return notas.size();
    }

    @Override
    public Object getItem(int position) {
        return notas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.nota_row, null);


        TextView correcto = convertView.findViewById(R.id.correcto);
        TextView incorrecto = convertView.findViewById(R.id.incorrecto);

        Nota n = notas.get(position);

        correcto.setText(n.getCorrecto());
        incorrecto.setText(n.getIncorrecto());

        return convertView;
    }
}
