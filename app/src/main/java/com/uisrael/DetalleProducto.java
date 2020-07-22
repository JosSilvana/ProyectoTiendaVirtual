package com.uisrael;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DetalleProducto extends AppCompatActivity {
    private Toolbar toolbar;
    Bundle datoRes;
    String RecibirIdProducto, RecibirIdUsuario, RecibirNombre, RecibirDescripcion, RecibirCantidad, RecibirPrecio;
    TextView mostrarNombre, mostrarDescripcion, mostrarCantidad, mostrarPrecio, cantidad, total;
    ImageButton mas, menos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);
        toolbar = findViewById(R.id.tool);
        setTitle("");
        setSupportActionBar(toolbar);

        mostrarNombre = findViewById(R.id.tvNombre);
        mostrarDescripcion = findViewById(R.id.tvDescripcion);
        mostrarPrecio = findViewById(R.id.tvPrecio);
        mostrarCantidad = findViewById(R.id.tvCantidad);
        cantidad = findViewById(R.id.tvCant);
        datoRes=getIntent().getExtras();
        RecibirIdUsuario=datoRes.getString("idUsuario");
        RecibirIdProducto = datoRes.getString("idProducto");
        total = findViewById(R.id.tvTotal);
        mas = findViewById(R.id.btnMas);
        menos = findViewById(R.id.btnMenos);

        datoRes=getIntent().getExtras();
        RecibirNombre=datoRes.getString("nombre");
        RecibirDescripcion=datoRes.getString("descripcion");
        RecibirPrecio = datoRes.getString("precio");
        RecibirCantidad = datoRes.getString("cantidad");
        mostrarNombre.setText(RecibirNombre);
        mostrarDescripcion.setText(RecibirDescripcion);
        mostrarPrecio.setText(RecibirPrecio);
        mostrarCantidad.setText(RecibirCantidad);
        total.setText(RecibirPrecio);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.usuario:
                Intent irPerfil = new Intent(this, PerfilUsuario.class);
                irPerfil.putExtra("idUsuario", RecibirIdUsuario);
                startActivity(irPerfil);
                break;
            case R.id.productos:
                Intent irProductos = new Intent(this, ListarProductos.class);
                irProductos.putExtra("idUsuario", RecibirIdUsuario);
                startActivity(irProductos);
                break;
            case R.id.carrito:
                Intent irCarrito = new Intent(this, CarritoCompras.class);
                irCarrito.putExtra("idUsuario", RecibirIdUsuario);
                startActivity(irCarrito);
                break;
            case R.id.cerrarSesion:
                Intent irInicio = new Intent(this, IniciarSesion.class);
                startActivity(irInicio);
                break;
        }
        return true;
    }
    public void mas(View v){
        menos.setEnabled(true);
        int cantidadT = Integer.parseInt(cantidad.getText().toString());

        if(Integer.parseInt(cantidad.getText().toString()) > Integer.parseInt(mostrarCantidad.getText().toString())){
            Toast.makeText(getApplicationContext(),"No se dispone esa cantidad de producto", Toast.LENGTH_LONG).show();
            mas.setEnabled(false);
        }else{
            mas.setEnabled(true);
            int incremento = cantidadT+1;
            cantidad.setText(String.valueOf(incremento));
            CalcularTotalProducto(incremento);
        }
    }
    public void menos(View v){
        mas.setEnabled(true);
        int cantidadT = Integer.parseInt(cantidad.getText().toString());
        int incremento = cantidadT-1;
        CalcularTotalProducto(incremento);
        if(incremento<0){
            incremento=0;
            menos.setEnabled(false);
        }else{
            menos.setEnabled(true);
            cantidad.setText(String.valueOf(incremento));
        }

    }
    public void CalcularTotalProducto(int cantidad){
        double resultado = 0;
        resultado = cantidad*Double.parseDouble(mostrarPrecio.getText().toString());
        total.setText(String.valueOf(resultado));
    }
    public void añadirCarrito(View v){
        PostAgregarProductosCarrito servicioTask= new PostAgregarProductosCarrito(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostAgregarProductoCarrito.php",RecibirIdUsuario, RecibirIdProducto, RecibirNombre, cantidad.getText().toString(), total.getText().toString());
        servicioTask.execute();
    }
}
