package com.uisrael;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ActualizarContrasenia extends AppCompatActivity {

    Bundle datoRes;
    private Toolbar toolbar;
    String RecibirEmail, RecibirId, RecibirNombre, RecibirApellido;
    EditText etContraseñaUser, etNuevaContraseñaUser, etConfirmarContraseñaUser;
    private String  contrasenaUser, idUser;
    String passwordEncriptacion = "gdsawr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contrasenia);
        toolbar = findViewById(R.id.tool);
        setTitle("Actualizar Contraseña");
        setSupportActionBar(toolbar);
        datoRes=getIntent().getExtras();
        RecibirEmail= datoRes.getString("email");
        RecibirId= datoRes.getString("idUsuario");
        RecibirNombre=datoRes.getString("nombre");
        RecibirApellido=datoRes.getString("apellido");
        etContraseñaUser= findViewById(R.id.etContraseña);
        etNuevaContraseñaUser= findViewById(R.id.etNuevaContrasenia);
        etConfirmarContraseñaUser= findViewById(R.id.etNuevaContraseniaC);
    }
    public void obtenerDatos() throws Exception {
        String contraseña = etContraseñaUser.getText().toString().trim();
        String ncontraseña = etNuevaContraseñaUser.getText().toString().trim();
        String vcontraseña = etConfirmarContraseñaUser.getText().toString().trim();
        if (validarCampos(contraseña, ncontraseña,vcontraseña)== true) {
            String ws = "http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php?email=" + RecibirEmail;
            StrictMode.ThreadPolicy politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(politica);
            URL url = null;
            HttpURLConnection conn;
            //Capturar las excepciones que se presenten
            try {
                url = new URL(ws);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                String json;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                json = response.toString();

                JSONObject respJSON = new JSONObject(json);
                contrasenaUser = respJSON.getString("contrasenia");
                String Descontrasenia = desencriptar(contrasenaUser, passwordEncriptacion);

                if (verificarContraseña(ncontraseña, vcontraseña) == true) {
                    VerificarCrendenciales(contraseña, Descontrasenia, ncontraseña);
                }

            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void VerificarCrendenciales(String contraseña, String contraseñaUser,String nuevaContraseña) throws Exception {
        if(contraseña.equals(contraseñaUser)==true ){
            int id = Integer.parseInt(RecibirId);
            String contraseniaEncriptada = encriptar(nuevaContraseña,passwordEncriptacion);
            PutUsuario servicioTask= new PutUsuario(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php?idUsuario=" +id +"&contrasenia="+contraseniaEncriptada);
            servicioTask.execute();
            Intent intentEnvio = new Intent(this, IniciarSesion.class);
            startActivity(intentEnvio);

        }else{
            Toast.makeText(getApplicationContext(),"¡La contraseña indicada no coincide con los registros!", Toast.LENGTH_LONG).show();
        }
    }
    private String encriptar(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(datos.getBytes());
        String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        return datosEncriptadosString;
    }
    private String desencriptar(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDescoficados = Base64.decode(datos, Base64.DEFAULT);
        byte[] datosDesencriptadosByte = cipher.doFinal(datosDescoficados);
        String datosDesencriptadosString = new String(datosDesencriptadosByte);
        return datosDesencriptadosString;
    }
    private SecretKeySpec generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
    public boolean validarCampos(String contraseña, String ncontraseña, String vcontraseña){
        boolean valor= true;
        if(contraseña.isEmpty()){
            etContraseñaUser.setError("No ha ingresado la contraseña");
            valor=false;
        }
        if(ncontraseña.isEmpty()){
            etNuevaContraseñaUser.setError("No ha ingresado  la contraseña");
            valor=false;
        }
        if(vcontraseña.isEmpty()){
            etConfirmarContraseñaUser.setError("No ha ingresado  la contraseña");
            valor=false;
        }
        return valor;
    }
    public void Actualizar(View v) throws Exception {
        obtenerDatos();
    }
    public boolean verificarContraseña(String contraseña, String rcontraseña) {
        if(contraseña.equals(rcontraseña)==true){
            return true;
        }else{
            Toast.makeText(getApplicationContext(),"¡Las contraseñas no coinciden!", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.inicio:
                Intent irInicio = new Intent(this, Inicio.class);
                irInicio.putExtra("idUsuario", RecibirId);
                irInicio.putExtra("nombre", RecibirNombre);
                irInicio.putExtra("apellido", RecibirApellido);
                irInicio.putExtra("email", RecibirEmail);
                startActivity(irInicio);
                break;
            case R.id.usuario:
                Intent irPerfil = new Intent(this, PerfilUsuario.class);
                irPerfil.putExtra("idUsuario", RecibirId);
                irPerfil.putExtra("nombre", RecibirNombre);
                irPerfil.putExtra("apellido", RecibirApellido);
                irPerfil.putExtra("email", RecibirEmail);
                startActivity(irPerfil);
                break;
            case R.id.productos:
                Intent irProductos = new Intent(this, ListarProductos.class);
                irProductos.putExtra("idUsuario", RecibirId);
                irProductos.putExtra("nombre", RecibirNombre);
                irProductos.putExtra("apellido", RecibirApellido);
                irProductos.putExtra("email", RecibirEmail);
                startActivity(irProductos);
                break;
            case R.id.carrito:
                Intent irCarrito = new Intent(this, CarritoCompras.class);
                irCarrito.putExtra("idUsuario", RecibirId);
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
}