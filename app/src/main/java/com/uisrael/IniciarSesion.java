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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

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

public class IniciarSesion extends AppCompatActivity {

    EditText et_Contraseña, et_Usuario;
    private static final String AES ="AES";
    private String usuario, contraseña, user, password, nombre , apellido, id;
    String passwordEncriptacion = "gdsawr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_Usuario= findViewById(R.id.etEmail);
        et_Contraseña= findViewById(R.id.etContraseña);
    }
    public void ventanaRegistrar(View v){
        Intent intentEnvio = new Intent(getApplicationContext(), RegistrarUsuario.class);
        startActivity(intentEnvio);
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
    public void IniciarSesion(View v) throws Exception {
        user = et_Usuario.getText().toString().trim();
        password = et_Contraseña.getText().toString().trim();
        if (validarCampos(user, password)== true){
            obtenerDatos();
        }
    }
    public boolean validarCampos(String emailUsuario, String contraseñaUsuario){
        boolean valor= true;
        if(emailUsuario.isEmpty()){
            et_Usuario.setError("No ha ingresado el email");
            valor=false;
        }
        if(contraseñaUsuario.isEmpty()){
            et_Contraseña.setError("No ha ingresado  la contraseña");
            valor=false;
        }
        return valor;
    }
    public void VerificarCrendenciales(String user, String password, String usuario , String contraseña, String nombre, String apellido, String id) throws Exception {
        String Descontrasenia = desencriptar(contraseña,passwordEncriptacion);
        if(user.equals(usuario)==true && password.equals(Descontrasenia)==true){
            Intent intentEnvio = new Intent(this, Inicio.class);
            intentEnvio.putExtra("nombre", nombre);
            intentEnvio.putExtra("apellido", apellido);
            intentEnvio.putExtra("email", usuario);
            intentEnvio.putExtra("idUsuario", id);
            Toast.makeText(getApplicationContext(),"¡Ingreso Exitoso!", Toast.LENGTH_LONG).show();
            startActivity(intentEnvio);
        }else{
            Toast.makeText(getApplicationContext(),"¡Sus credenciales son Incorrectas!", Toast.LENGTH_LONG).show();
        }
    }
    public void obtenerDatos() throws Exception {
        user = et_Usuario.getText().toString().trim();
        password = et_Contraseña.getText().toString().trim();
        String ws = "http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php?email="+user;
        //String ws = "http://192.168.100.42/RestPryMovil/PostUsuario.php?email="+user;
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
            json=response.toString();

            JSONObject respJSON = new JSONObject(json);
            usuario = respJSON.getString("email");
            contraseña = respJSON.getString("contrasenia");
            nombre = respJSON.getString("nombre");
            apellido = respJSON.getString("apellido");
            id = respJSON.getString("idUsuario");
            VerificarCrendenciales(user, password, usuario, contraseña, nombre, apellido, id);

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
