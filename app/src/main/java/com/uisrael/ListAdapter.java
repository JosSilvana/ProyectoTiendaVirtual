package com.uisrael;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListAdapter extends ArrayAdapter<Productos> {

    private List<Productos> listaProductos;
    private Context mcontext;
    private int resourceLayout;
    public ListAdapter(@NonNull Context context, int resource, @NonNull List<Productos> listaProductos) {
        super(context, resource, listaProductos);
        this.listaProductos = listaProductos;
        this.mcontext = context;
        this.resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null)
            view = LayoutInflater.from(mcontext).inflate(resourceLayout, null);

        Productos productos = listaProductos.get(position);
        ImageView imagen = view.findViewById(R.id.imgProductos);

        TextView nombre = view.findViewById(R.id.ltvNombre);
        nombre.setText(productos.getNombre());

        TextView descripcion = view.findViewById(R.id.ltvDescripcion);
        descripcion.setText(productos.getDescripcion());

        TextView cantidad = view.findViewById(R.id.ltvCantidad);
        cantidad.setText(productos.getCantidad());

        TextView precio = view.findViewById(R.id.ltvPrecio);
        precio.setText(productos.getPrecio());

        return view;
    }
}
