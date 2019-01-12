package com.gui.inventoryapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.gui.inventoryapp.utils.GeneralConstants.CAMERA_REQUEST;
import static com.gui.inventoryapp.utils.GeneralConstants.MY_CAMERA_PERMISSION_CODE;

public class BarcodeScanner implements View.OnClickListener {

    public BarcodeScanner(Fragment fragment){
        this.fragment = fragment;
    }

    private String path;
    private Fragment fragment;
    private static final String TAG = "BarcodeScanner";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Fragment getFragment() {
        return fragment;
    }



    public static File generateFileCamera(Context context){
        File image;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            //Log.d(TAG, " " + image.getAbsolutePath());
        } catch (IOException ex) {
           return null;
        }
        return image;
    }


    public static String getBarCode(String mCurrentPhotoPath, Context context){

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

        //Delete the file
        File fdelete = new File(mCurrentPhotoPath);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d(TAG, " Borrado correcto");
            } else {
                Log.d(TAG, " Borrado fallido");
            }
        }

        BarcodeDetector detector = new BarcodeDetector.Builder(context.getApplicationContext())
                .setBarcodeFormats(Barcode.CODE_39)
                .build();

        if (!detector.isOperational()) {
            Log.d(TAG, "Could not set up the detector!");
            return null;
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        if (barcodes.size() > 0) {
            Log.d(TAG, "BARCODE VALUE = " + barcodes.valueAt(0).rawValue);
            return barcodes.valueAt(0).rawValue;
        }
        else{
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        if (checkSelfPermission(getFragment().getContext(),Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            getFragment().requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);
        }

        else{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File img = BarcodeScanner.generateFileCamera(getFragment().getContext());

            if(img == null) {
                Toast.makeText(getFragment().getContext(), "Error leyendo del scaner", Toast.LENGTH_LONG).show();
                return;
            }

            Uri photoURI = FileProvider.getUriForFile(getFragment().getContext(),
                    GeneralConstants.FILE_PROVIDER,
                    img);

            setPath(img.getAbsolutePath());

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            getFragment().startActivityForResult(cameraIntent, CAMERA_REQUEST);

        }
    }
}
