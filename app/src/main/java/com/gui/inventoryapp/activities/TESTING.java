package com.gui.inventoryapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.gui.inventoryapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class TESTING extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        this.imageView = (ImageView) this.findViewById(R.id.imageView1);

        final Activity act = this;

        ((Button) this.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );
                        mCurrentPhotoPath = image.getAbsolutePath();
                        Log.d("!--.", " " + image.getAbsolutePath());
                        photoFile = image;

                    } catch (IOException ex) {
                        throw new IllegalArgumentException("SS");
                    }

                    try (FileOutputStream out = new FileOutputStream(photoFile)) {
                        BitmapFactory.decodeResource(
                                getApplicationContext().getResources(),
                                R.drawable.cap1).compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (IOException e) {
                        throw new IllegalArgumentException("SS");
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(act,
                                "com.gui.inventoryapp.fileprovider",
                                photoFile);

                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }


            }});

        /*
        Bitmap myBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.cap1);
       // myImageView.setImageBitmap(myBitmap);

        BarcodeScanner detector = new BarcodeScanner.Builder(getApplicationContext())
                                        .setBarcodeFormats(Barcode.CODE_39)
                                        .build();

        if(!detector.isOperational()){
            Log.d("!--.","Could not set up the detector!");
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        if(barcodes.size() > 0)
            Log.d("!--.","BARCODE VALUE = " + barcodes.valueAt(0).displayValue);
    */

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File image = File.createTempFile(
                            "",  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                    mCurrentPhotoPath = image.getAbsolutePath();
                    photoFile = image;

                } catch (IOException ex) {
                    throw new IllegalArgumentException("SS");
                }

                try (FileOutputStream out = new FileOutputStream(photoFile)) {
                    BitmapFactory.decodeResource(
                            getApplicationContext().getResources(),
                            R.drawable.cap1).compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    throw new IllegalArgumentException("SS");
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
    String mCurrentPhotoPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
           // Bitmap imageBitmap = (Bitmap) extras.get("data");
/*
            if (imageBitmap == null)
                Log.d("!--.", "IMAGE NULL");
*/
            Log.d("!--.", " " + mCurrentPhotoPath);

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

            File fdelete = new File(mCurrentPhotoPath);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.d("!--.", " Borrado correcto");
                } else {
                    Log.d("!--.", " Borrado fallido");
                }
            }

            imageView.setImageBitmap(bitmap);


            BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.CODE_39)
                    .build();

            if (!detector.isOperational()) {
                Log.d("!--.", "Could not set up the detector!");
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            if (barcodes.size() > 0)
                Log.d("!--.", "BARCODE VALUE = " + barcodes.valueAt(0).rawValue);

        }
    }


}
