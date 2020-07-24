package com.uisrael;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListAdapter extends ArrayAdapter<Productos> {

    private List<Productos> listaProductos;
    private Context mcontext;
    private int resourceLayout;
    private Bitmap loadedImage;
    private ImageView imageView;
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
        imageView = view.findViewById(R.id.imgProductos);
        downloadFile("http://192.168.1.3/RestProyectoTiendaMovil/"+productos.getImagen());

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
    void downloadFile(String imageHttpAddress) {
        URL imageUrl = null;
        try {
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
            imageView.setImageBitmap(loadedImage);
        } catch (IOException e) {
            Toast.makeText(mcontext.getApplicationContext(), "Error cargando la imagen: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
