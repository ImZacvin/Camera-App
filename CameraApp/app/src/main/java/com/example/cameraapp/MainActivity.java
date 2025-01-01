package com.example.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cameraapp.utils.GPSTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private DBHandler dbHandler;
    private Location currentLocation;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Button captureButton = findViewById(R.id.button_capture);
        Button viewPhotosButton = findViewById(R.id.button_view_photos);

        captureButton.setOnClickListener(v -> takePhoto());
        viewPhotosButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GridActivity.class);
            startActivity(intent);
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_PERMISSIONS);
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(MainActivity.this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                saveImage(imageBitmap);
            }
        }
    }

    private void saveImage(Bitmap finalBitmap) {
        String root = getExternalFilesDir(null).toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fname = getUniqueFileName(myDir, "Image");
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);

            // Create a new bitmap with the timestamp watermark
            Bitmap markedBitmap = addTimestampToBitmap(finalBitmap, getCurrentTimeStamp());

            // Compress and save the marked bitmap
            markedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            //save to SQLite
            DBHandler dbHandler = new DBHandler(MainActivity.this);
            String photoDate = getCurrentTimeStamp();
            GPSTracker gpsTracker = new GPSTracker(this);
            String fullAddress = gpsTracker.getFullAddress(this);
            String photoLocation = fullAddress != null ? fullAddress : "Lat: " + gpsTracker.getLatitude() + ", Lon: " + gpsTracker.getLongitude();

            dbHandler.addNewCourse(fname, photoDate, photoLocation);



            if (currentLocation != null) {
                // Save image with location and timestamp information
                saveImageWithLocationAndTimestamp(finalBitmap, currentLocation, file, timeStamp);
            } else {

                // Save image with only timestamp information
                Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();

                ArrayList<CourseModal> data = dbHandler.readCourses();
                for(int i = 1; i < data.size(); i++) {
                    Log.d("Photo Description", "Photo Name : " + data.get(i).photoName + " Photo Date : " + data.get(i).photoDate + "Photo Location : " + data.get(i).photoLocation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUniqueFileName(File directory, String baseName) {
        String fileName = baseName + "_1.jpg";
        int counter = 1;
        File file = new File(directory, fileName);
        while (file.exists()) {
            counter++;
            fileName = baseName + "_" + counter + ".jpg";
            file = new File(directory, fileName);
        }
        return fileName;
    }

    private Bitmap addTimestampToBitmap(Bitmap src, String timeStamp) {
        Bitmap resultBitmap = src.copy(src.getConfig(), true);// Adjust position as needed
        return resultBitmap;
    }

    private void saveImageWithLocationAndTimestamp(Bitmap finalBitmap, Location location, File file, String timeStamp) {
        // Add code here to save the image with location and timestamp information
        // For example, you can add location metadata and timestamp to the image file name
        Toast.makeText(this, "Image Saved with Location: " + location.getLatitude() + ", " + location.getLongitude() + " and Timestamp: " + timeStamp, Toast.LENGTH_SHORT).show();
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
