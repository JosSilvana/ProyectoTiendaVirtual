package com.uisrael;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class DetalleProducto extends AppCompatActivity {
    private Toolbar toolbar;
    Bundle datoRes;
    String RecibirIdProducto, RecibirIdUsuario, RecibirNombre, RecibirApellido, RecibirEmail,RecibirNombreProducto, RecibirDescripcion, RecibirCantidad, RecibirPrecio;
    TextView mostrarNombre, mostrarDescripcion, mostrarCantidad, mostrarPrecio, cantidad, total;
    ImageButton mas, menos;
    Button añadir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);
        toolbar = findViewById(R.id.tool);
        setTitle("Detalle Producto");
        setSupportActionBar(toolbar);

        mostrarNombre = findViewById(R.id.tvNombre);
        mostrarDescripcion = findViewById(R.id.tvDescripcion);
        mostrarPrecio = findViewById(R.id.tvPrecio);
        mostrarCantidad = findViewById(R.id.tvCantidad);
        cantidad = findViewById(R.id.tvCant);
        datoRes=getIntent().getExtras();
        RecibirIdUsuario=datoRes.getString("idUsuario");
        RecibirNombre=datoRes.getString("nombre");
        RecibirApellido=datoRes.getString("apellido");
        RecibirEmail=datoRes.getString("email");
        RecibirIdProducto = datoRes.getString("idProducto");
        total = findViewById(R.id.tvTotal);
        mas = findViewById(R.id.btnMas);
        menos = findViewById(R.id.btnMenos);
        añadir = findViewById(R.id.btnAñadir);

        datoRes=getIntent().getExtras();
        RecibirNombreProducto =datoRes.getString("nombreP");
        RecibirDescripcion=datoRes.getString("descripcion");
        RecibirPrecio = datoRes.getString("precio");
        RecibirCantidad = datoRes.getString("cantidad");
        mostrarNombre.setText(RecibirNombreProducto);
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
            case R.id.inicio:
                Intent irInicio = new Intent(this, Inicio.class);
                irInicio.putExtra("idUsuario", RecibirIdUsuario);
                irInicio.putExtra("nombre", RecibirNombre);
                irInicio.putExtra("apellido", RecibirApellido);
                irInicio.putExtra("email", RecibirEmail);
                startActivity(irInicio);
                break;
            case R.id.usuario:
                Intent irPerfil = new Intent(this, PerfilUsuario.class);
                irPerfil.putExtra("idUsuario", RecibirIdUsuario);
                irPerfil.putExtra("nombre", RecibirNombre);
                irPerfil.putExtra("apellido", RecibirApellido);
                irPerfil.putExtra("email", RecibirEmail);
                startActivity(irPerfil);
                break;
            case R.id.productos:
                Intent irProductos = new Intent(this, ListarProductos.class);
                irProductos.putExtra("idUsuario", RecibirIdUsuario);
                irProductos.putExtra("nombre", RecibirNombre);
                irProductos.putExtra("apellido", RecibirApellido);
                irProductos.putExtra("email", RecibirEmail);
                startActivity(irProductos);
                break;
            case R.id.carrito:
                Intent irCarrito = new Intent(this, CarritoCompras.class);
                irCarrito.putExtra("idUsuario", RecibirIdUsuario);
                irCarrito.putExtra("nombre", RecibirNombre);
                irCarrito.putExtra("apellido", RecibirApellido);
                irCarrito.putExtra("email", RecibirEmail);
                startActivity(irCarrito);
                break;
            case R.id.cerrarSesion:
                Intent irLogin = new Intent(this, IniciarSesion.class);
                startActivity(irLogin);
                break;
        }
        return true;
    }
    public void mas(View v){
        menos.setEnabled(true);
        int cantidadT = Integer.parseInt(cantidad.getText().toString());

        if(Integer.parseInt(cantidad.getText().toString()) > Integer.parseInt(mostrarCantidad.getText().toString())){
            Toast.makeText(getApplicationContext(),"No se dispone esa cantidad de producto", Toast.LENGTH_SHORT).show();
            mas.setEnabled(false);
            añadir.setEnabled(false);
        }else{
            añadir.setEnabled(true);
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
        if(incremento<=0){
            incremento=0;
            menos.setEnabled(false);
            añadir.setEnabled(false);
        }else{
            menos.setEnabled(true);
            añadir.setEnabled(true);
            cantidad.setText(String.valueOf(incremento));
        }

    }
    public void CalcularTotalProducto(int cantidad){
        double resultado = 0;
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        resultado = cantidad*Double.parseDouble(mostrarPrecio.getText().toString());
        total.setText(String.valueOf(format.format(resultado)));
    }
    public void añadirCarrito(View v){
        PostAgregarProductosCarrito servicioTask= new PostAgregarProductosCarrito(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostAgregarProductoCarrito.php",RecibirIdUsuario, RecibirIdProducto, RecibirNombreProducto, cantidad.getText().toString(), RecibirCantidad, total.getText().toString());
        servicioTask.execute();
    }
}
