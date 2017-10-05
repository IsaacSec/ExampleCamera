package com.example.isaac.examplecamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Button takePicture;
    private Button selectPicture;
    private ImageView picture;

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

        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePermissionsStorage();
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

    }
}
