package com.example.isaac.examplecamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static final int REQUEST_CAMERA = 200;
    private static final String FILE_PROVIDER = "com.example.isaac.examplecamera.fileprovider";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Button takePicture;
    private Button selectPicture;
    private ImageView picture;
    private TextView urlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewElements();
    }

    public void initViewElements(){
        takePicture = (Button) findViewById(R.id.b_take);
        selectPicture = (Button) findViewById(R.id.b_select);
        picture = (ImageView) findViewById(R.id.picture);
        urlView = (TextView) findViewById(R.id.urlpath);

        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePermissionsStorage();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePermissionsCamera();
            }
        });
    }

    public void validatePermissionsStorage(){

        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Debemos mostrar un mensaje?
            if (ActivityCompat.shouldShowRequestPermissionRationale
                (this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Mostramos una explicación de que no aceptó dar permiso para acceder a la librería
                Toast toast = Toast.makeText(this, "Necesitas aceptar los permisos para continuar", Toast.LENGTH_LONG);
                toast.show();
            } else {
                // Pedimos permiso
                ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
            }
        }else{
            startIntentSelectPhotos();
        }
    }

    public void startIntentSelectPhotos(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // Si la petición se cancela se regresa un arreglo vacío
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                    startIntentSelectPhotos();
                } else {
                    // Permiso negado
                    Toast toast = Toast.makeText(this, "Necesitas aceptar los permisos para continuar", Toast.LENGTH_LONG);
                    toast.show();
                }
                return;
            }

            case REQUEST_CAMERA: {
                System.out.println("["+grantResults+"] "+grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Se concedió acceso
                    startIntentSelectPhotos();
                } else {
                    Toast toast = Toast.makeText(this, "Necesitas aceptar los permisos para continuar", Toast.LENGTH_LONG);
                    toast.show();
                }
                return;
            }
        }
    }

    public void validatePermissionsCamera(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
        }else{
            startIntentTakePhoto();
        }
    }

    public void startIntentTakePhoto(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Validamos que hay una actividad de cámara
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Creamos un nuevo objeto para almacenar la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error creando archivo
                Toast toast = Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_LONG);
                toast.show();
            }
            // Si salió bien
            System.out.println(urlView.getText());
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, FILE_PROVIDER, photoFile);
                // Mandamos llamar el intent
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Creamos el archivo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreImagen = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                nombreImagen, /* prefijo */
                ".jpg", /* sufijo */
                storageDir /* directorio */
        );

        // Obtenemos la URL
        String urlName = "file://" + image.getAbsolutePath();
        urlView.setText(urlName);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri imagenSeleccionada = imageReturnedIntent.getData();
                    try{
                        InputStream imagenStream = getContentResolver().openInputStream(imagenSeleccionada);
                        Bitmap imagen = BitmapFactory.decodeStream(imagenStream);
                        picture.setImageBitmap(imagen);
                        urlView.setText(imagenSeleccionada.toString());
                    }catch (FileNotFoundException fnte){
                        Toast.makeText(this, fnte.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                    return;
                }

            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    Picasso.with(this).load(urlView.getText().toString()).into(picture);
                }
                return;
        }
    }

    // /storage/emulated/0/Android/data/com.example.isaac.examplecamera/files/Pictures/JPEG_20171005_014944_566523068.jpg


}
