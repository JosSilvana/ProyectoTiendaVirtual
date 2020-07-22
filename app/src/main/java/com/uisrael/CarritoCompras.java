package com.uisrael;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CarritoCompras extends AppCompatActivity {

    Bundle datoRes;
    private Toolbar toolbar;
    String RecibirIdUsuario, RecibirIdProducto, RecibirNombre, RecibirTotalProducto;
    TextView total;
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    double totalComprar = 0;
    ListView listaProductosCarrito;
    ArrayList<String> listaCarrito = new ArrayList<>();
    Button comprar;
    private ArrayAdapter<String> listaCarritoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_compras);

        toolbar = findViewById(R.id.tool);
        setTitle("");
        setSupportActionBar(toolbar);
        comprar=findViewById(R.id.btnComprar);
        listaProductosCarrito = findViewById(R.id.listaCarritoCompras);
        total = findViewById(R.id.tvTotal);
        datoRes=getIntent().getExtras();
        RecibirIdUsuario=datoRes.getString("idUsuario");
        RecibirIdProducto=datoRes.getString("idProducto");
        RecibirNombre=datoRes.getString("nombre");
        RecibirTotalProducto=datoRes.getString("totalProducto");
        productoAgregado();

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
    public void productoAgregado(){
        String ws="http://192.168.1.3/RestProyectoTiendaMovil/db/PostAgregarProductoCarrito.php";
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
                listaCarrito.add(objeto.optString("idProducto")+" "+objeto.optString("nombreProducto")+" "+objeto.optString("cantidad")+" "+objeto.optString("total"));
                totalComprar += Double.parseDouble(objeto.optString("total"));
            }
            total.setText(String.valueOf(totalComprar));
            listaCarritoAdapter = new ArrayAdapter<String>(CarritoCompras.this,android.R.layout.simple_list_item_1, listaCarrito);
            listaProductosCarrito.setAdapter(listaCarritoAdapter);
        }
        catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void comprar(View v) throws ExecutionException, InterruptedException {
        PostPedido servicioTask= new PostPedido(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostPedido.php",RecibirIdUsuario,date);
        servicioTask.execute();
        servicioTask.getStatus();
        for(int i=0;i<listaCarrito.size();i++){
            String[] DatosProductos = listaCarrito.get(i).split(" ");
            PostDetallePedido servicioTask1= new PostDetallePedido(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostDetallePedido.php","8",DatosProductos[0],DatosProductos[2], DatosProductos[3]);
            servicioTask1.execute();
        }

    }
}
