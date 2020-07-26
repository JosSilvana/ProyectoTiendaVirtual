package com.uisrael;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PerfilUsuario extends AppCompatActivity {

    ImageView ivFoto;
    Button btnTomarFoto, btnSeleccionarImagen;
    String RecibirNombre, RecibirApellido, RecibirEmail, RecibirIdUsuario, emailUser;
    EditText nombre, apellido, email;
    Uri imagenUri;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;
    String CARPETA_RAIZ = "RestPryMovil/";
    String CARPETAS_IMAGENES = "imagenes";
    String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    String path;
    Bundle datoRes;
    private Toolbar toolbar;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        nombre = findViewById(R.id.etNombre);
        apellido = findViewById(R.id.etApellido);
        email= findViewById(R.id.etEmail);
        toolbar = findViewById(R.id.tool);
        datoRes=getIntent().getExtras();
        RecibirNombre=datoRes.getString("nombre");
        RecibirApellido=datoRes.getString("apellido");
        RecibirEmail=datoRes.getString("email");
        nombre.setText(RecibirNombre);
        apellido.setText(RecibirApellido);
        email.setText(RecibirEmail);
        toolbar = findViewById(R.id.tool);
        setTitle("Perfil de Usuario");
        setSupportActionBar(toolbar);
        datoRes=getIntent().getExtras();
        RecibirIdUsuario=datoRes.getString("idUsuario");
        ivFoto = findViewById(R.id.ivFoto);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);

        if(ContextCompat.checkSelfPermission(PerfilUsuario.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PerfilUsuario.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
    public void ventanaActualizarContraseña(View v){
        Intent intentEnvio = new Intent(this, ActualizarContrasenia.class);
        intentEnvio.putExtra("email", RecibirEmail);
        intentEnvio.putExtra("idUsuario", RecibirIdUsuario);
        intentEnvio.putExtra("nombre", RecibirNombre);
        intentEnvio.putExtra("apellido", RecibirApellido);
        startActivity(intentEnvio);
    }
    public void tomarFoto(View v) {
        String nombreImagen = "";
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();

        if(isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        if(isCreada == true) {
            nombreImagen = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = this.getPackageName()+".provider";
            Uri imageUri = FileProvider.getUriForFile(this, authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }

        startActivityForResult(intent, TOMAR_FOTO);
    }
    public void seleccionarImagen(View v) {
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, SELEC_IMAGEN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == SELEC_IMAGEN) {
            imagenUri = data.getData();
            ivFoto.setImageURI(imagenUri);
        } else if(resultCode == RESULT_OK && requestCode == TOMAR_FOTO) {
            MediaScannerConnection.scanFile(PerfilUsuario.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String s, Uri uri) {

                }
            });

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            ivFoto.setImageBitmap(bitmap);
        }
    }
    public void EditarRegistro(View v){
        String nombreUsuario= nombre.getText().toString();
        String apellidoUsuario= apellido.getText().toString();
        String emailUsuario= email.getText().toString();
        String fotografia="" ;

        if(RecibirEmail.equals(emailUsuario)==false){
            if(verificarExistencia(emailUsuario)==true){
                Toast.makeText(getApplicationContext(),"¡El usuario ya se encuentra registrado!", Toast.LENGTH_LONG).show();
            }else {
                int id = Integer.parseInt(RecibirIdUsuario);
                PutUsuario servicioTask = new PutUsuario(this, "http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php?idUsuario=" + RecibirIdUsuario + "&nombre=" + nombreUsuario + "&apellido=" + apellidoUsuario + "&email=" + emailUsuario + "&fotografia=" + fotografia);
                servicioTask.execute();
                irInicio(nombreUsuario,apellidoUsuario,emailUsuario, RecibirIdUsuario);
            }
        }else{
            PutUsuario servicioTask = new PutUsuario(this, "http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php?idUsuario=" + RecibirIdUsuario + "&nombre=" + nombreUsuario + "&apellido=" + apellidoUsuario + "&email=" + emailUsuario + "&fotografia=" + fotografia);
            servicioTask.execute();
            irInicio(nombreUsuario,apellidoUsuario,emailUsuario, RecibirIdUsuario);
        }

    }
    public  void irInicio(String nombreUsuario, String apellidoUsuario , String emailUsuario, String id){
        Intent intentEnvio = new Intent(this, Inicio.class);
        intentEnvio.putExtra("nombre", nombreUsuario);
        intentEnvio.putExtra("apellido", apellidoUsuario);
        intentEnvio.putExtra("email", emailUsuario);
        intentEnvio.putExtra("idUsuario", id);
        startActivity(intentEnvio);
    }
    public boolean verificarExistencia(String email){
        boolean valor= false;
        String ws = "http://192.168.1.3/RestProyectoTiendaMovil/db/PostUsuario.php?email="+email;
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
}
