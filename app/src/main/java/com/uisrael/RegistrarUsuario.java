package com.uisrael;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrarUsuario extends AppCompatActivity {
    EditText etNombres, etApellidos, etContraseña, etRContraseña, etCorreo;
    String passwordEncriptacion = "gdsawr";
    String emailUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        etNombres = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellido);
        etCorreo= findViewById(R.id.etEmail);
        etContraseña = findViewById(R.id.etContrasenia);
        etRContraseña = findViewById(R.id.etRcontrasenia);

    }
    public boolean verificarContraseña(String contraseña, String rcontraseña) {
        if(contraseña.equals(rcontraseña)==true){
            return true;
        }else{
            Toast.makeText(getApplicationContext(),"¡Las contraseñas no coinciden!", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public boolean validarCampos(String nombre, String apellido,String username,String contrasenia, String  rcontrasenia){
        boolean valor= true;
        if(nombre.isEmpty()){
            etNombres.setError("No ha ingresado el nombre");
            valor=false;
        }
        if(apellido.isEmpty()){
            etApellidos.setError("No ha ingresado el apellido");
            valor=false;
        }
        if(username.isEmpty()){
            etCorreo.setError("No ha ingresado el email");
            valor=false;
        }
        if(contrasenia.isEmpty()){
            etContraseña.setError("No ha ingresado la contraseña");
            valor=false;
        }
        if(rcontrasenia.isEmpty()){
            etRContraseña.setError("No ha ingresado la confirmación de contraseña");
            valor=false;
        }
        return valor;
    }
    private String encriptar(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(datos.getBytes());
        String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        return datosEncriptadosString;
    }
    private SecretKeySpec generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
    public void LimpiarDatos() {
        etNombres.setText("");
        etApellidos.setText("");
        etCorreo.setText("");
        etContraseña.setText("");
        etRContraseña.setText("");
    }
    public void Registrar(View v) throws Exception {
        String nombre= etNombres.getText().toString().trim();
        String apellido= etApellidos.getText().toString().trim();
        String username= etCorreo.getText().toString().trim();
        String contrasenia= etContraseña.getText().toString().trim();
        String rcontrasenia= etRContraseña.getText().toString().trim();

        if (validarCampos(nombre, apellido,username,contrasenia, rcontrasenia)== true){
            if(verificarContraseña(contrasenia,rcontrasenia)==true){
                if( verificarExistencia(username)==true){
                    Toast.makeText(getApplicationContext(),"¡El usuario ya se encuentra registrado!", Toast.LENGTH_LONG).show();
                }else{
                    String ValContraseña= encriptar(contrasenia, passwordEncriptacion);
                    //postUsuario servicioTask= new postUsuario(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php",nombre,apellido,username,ValContraseña);
                    postUsuario servicioTask= new postUsuario(this,"http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php",nombre,apellido,username,ValContraseña);
                    servicioTask.execute();
                    Intent intentEnvio = new Intent(this, IniciarSesion.class);
                    LimpiarDatos();
                    startActivity(intentEnvio);
                }
            }
        }
    }
    public void VentanaLogin(View v){
        Intent intentEnvio = new Intent(this, IniciarSesion.class);
        LimpiarDatos();
        startActivity(intentEnvio);
    }
    public boolean verificarExistencia(String email){
        boolean valor= false;
        String ws = "http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php?email=" + email;
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
            emailUser = respJSON.getString("email");
            if(emailUser.equals(email)){
                valor=true;
            } else{
                valor= false;
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (JSONException e) {
        }
        return  valor;
    }

}
