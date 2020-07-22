package com.uisrael;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListarProductos extends AppCompatActivity {

    Bundle datoRes;
    private Toolbar toolbar;
    ListView listaProductos;
    String RecibirIdUsuario, idProducto;
    private List<Productos> listaP = new ArrayList<>();
    private ListAdapter listaProductosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_productos);

        toolbar = findViewById(R.id.tool);
        setTitle("");
        setSupportActionBar(toolbar);
        datoRes=getIntent().getExtras();
        RecibirIdUsuario=datoRes.getString("idUsuario");

        listaProductos = findViewById(R.id.ListP);
        getData();

        listaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(listaProductos.getContext(), listaP.get(position),Toast.LENGTH_SHORT).show();
                Intent irDetalle = new Intent(listaProductos.getContext(), DetalleProducto.class);
                irDetalle.putExtra("idUsuario",RecibirIdUsuario);
                irDetalle.putExtra("idProducto",listaP.get(position).getIdProducto());
                irDetalle.putExtra("nombre", listaP.get(position).getNombre());
                irDetalle.putExtra("descripcion", listaP.get(position).getDescripcion());
                irDetalle.putExtra("cantidad", listaP.get(position).getCantidad());
                irDetalle.putExtra("precio",listaP.get(position).getPrecio());
                startActivity(irDetalle);
            }
        });
    }
    public void getData(){

        String ws="http://192.168.1.3/RestProyectoTiendaMovil/db/PostProducto.php";
        StrictMode.ThreadPolicy politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(politica);
        URL url;
        HttpURLConnection conn;

        //Capturar exepciones
        try {

            url = new URL(ws);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            String json;

            while((inputLine = in.readLine())!=null){
                response.append(inputLine);
            }

            json = response.toString();
            JSONArray array = null;
            array = new JSONArray(json);
            for(int i=0; i<array.length(); i++){
                JSONObject objeto = array.getJSONObject(i); //NOMBRE, APELLIDO, etc
                listaP.add(new Productos(objeto.optString("idProducto"),objeto.optString("nombre"),objeto.optString("descripcion"),objeto.optString("precio"),objeto.optString("cantidad"),objeto.optString("imagen")));
            }
            listaProductosAdapter = new ListAdapter(ListarProductos.this,R.layout.productos,listaP);
            listaProductos.setAdapter(listaProductosAdapter);
        }
        catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
}
