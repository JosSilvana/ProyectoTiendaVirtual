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
import android.widget.AdapterView;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CarritoCompras extends AppCompatActivity {

    Bundle datoRes;
    private Toolbar toolbar;
    String RecibirIdUsuario, RecibirIdProducto, RecibirNombreProducto, RecibirTotalProducto, ultimoIdPedido, RecibirNombre, RecibirApellido, RecibirEmail;
    int ultimoPedido;
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
        setTitle("Carrito de Compras");
        setSupportActionBar(toolbar);
        comprar=findViewById(R.id.btnComprar);
        listaProductosCarrito = findViewById(R.id.listaCarritoCompras);
        total = findViewById(R.id.tvTotal);
        datoRes=getIntent().getExtras();
        RecibirIdUsuario=datoRes.getString("idUsuario");
        RecibirNombre=datoRes.getString("nombre");
        RecibirApellido=datoRes.getString("apellido");
        RecibirEmail=datoRes.getString("email");
        RecibirIdProducto=datoRes.getString("idProducto");
        RecibirNombreProducto =datoRes.getString("nombreP");
        RecibirTotalProducto=datoRes.getString("totalProducto");
        productoAgregado();

        listaProductosCarrito.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String datos[] = listaCarrito.get(position).split(" ");
                String idAgregado = datos[0];
                DeleteItemCarritoCompras servicioTask= new DeleteItemCarritoCompras(CarritoCompras.this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostAgregarProductoCarrito.php?idAgregarProducto="+idAgregado);
                servicioTask.execute();
                Toast.makeText(getApplicationContext(), "Producto eliminado del carrito",Toast.LENGTH_SHORT).show();
                listaCarritoAdapter.clear();
                productoAgregado();
            }
        });

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
    public void productoAgregado(){
        String ws="http://192.168.1.3/RestProyectoTiendaMovil/db/PostAgregarProductoCarrito.php?idUsuario="+RecibirIdUsuario;
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
                listaCarrito.add(objeto.optString("idAgregarProducto")+" "+objeto.optString("idProducto")+" "+objeto.optString("nombreProducto")+" "+objeto.optString("cantidad")+" "+objeto.optString("cantidadTotal")+" "+objeto.optString("total"));
                totalComprar += Double.parseDouble(objeto.optString("total"));
            }
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2); //Define 2 decimales.
            listaCarritoAdapter = new ArrayAdapter<String>(CarritoCompras.this,android.R.layout.simple_list_item_1, listaCarrito);
            listaProductosCarrito.setAdapter(listaCarritoAdapter);
            total.setText(String.valueOf(format.format(totalComprar)));
        }
        catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void comprar(View v) {
        PostPedido servicioTask= new PostPedido(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostPedido.php",RecibirIdUsuario,date);
        servicioTask.execute();
        servicioTask.getStatus();
        obtenerUltimoIdPedido();
        for(int i=0;i<listaCarrito.size();i++){
            String[] DatosProductos = listaCarrito.get(i).split(" ");
            PostDetallePedido servicioTask1= new PostDetallePedido(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostDetallePedido.php",String.valueOf(ultimoPedido),DatosProductos[1],DatosProductos[3], DatosProductos[5]);
            servicioTask1.execute();
            int StockCantidad = Integer.parseInt(DatosProductos[4]) - Integer.parseInt(DatosProductos[3]);
            UpdateStockProductos servicioTask2= new UpdateStockProductos(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostProducto.php?idProducto="+DatosProductos[1]+"&cantidad="+StockCantidad);
            servicioTask2.execute();
        }
        DeleteItemCarritoCompras servicioTask4= new DeleteItemCarritoCompras(CarritoCompras.this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostDetallePedido.php?idUsuario="+RecibirIdUsuario);
        servicioTask4.execute();
        listaCarritoAdapter.clear();
    }
    public void obtenerUltimoIdPedido(){
        String ws = "http://192.168.1.3/RestProyectoTiendaMovil/db/PostPedido.php";
        StrictMode.ThreadPolicy politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(politica);
        URL url;
        HttpURLConnection conn;
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
                ultimoIdPedido = objeto.getString("idPedido");
            }
            ultimoIdPedido.lastIndexOf(array.length()-1);
            ultimoPedido = ultimoIdPedido.lastIndexOf(array.length()-1);
            ultimoPedido = Integer.parseInt(ultimoIdPedido)+1;
        }
        catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}