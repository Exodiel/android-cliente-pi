package com.example.proyectointegrador.adapter;


import com.example.proyectointegrador.R;
import com.example.proyectointegrador.app.AppController;
import com.example.proyectointegrador.models.Materia;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;



public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Materia> materias;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Materia> materias) {
        this.activity = activity;
        this.materias = materias;
    }

    @Override
    public int getCount() {
        return materias.size();
    }

    @Override
    public Object getItem(int position) {
        return materias.get(position);
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
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = convertView
                .findViewById(R.id.thumbnail);
        TextView nombre = convertView.findViewById(R.id.nombre);
        TextView codigo = convertView.findViewById(R.id.codigo);
        TextView total = convertView.findViewById(R.id.total);

        //Obteniendo los datos de las materias para las filas
        Materia m = materias.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getImagen(), imageLoader);

        // Nombre
        nombre.setText(m.getNombre());

        // codigo
        codigo.setText(""+m.getId_mat());

        // total
        total.setText(""+m.getTotal());

        return convertView;
    }
}
