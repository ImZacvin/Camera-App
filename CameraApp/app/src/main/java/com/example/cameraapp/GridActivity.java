package com.example.cameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class GridActivity extends AppCompatActivity {

    private static final String TAG = "GridActivity";

    private GridView gridView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        gridView = findViewById(R.id.grid_view);
        imageAdapter = new ImageAdapter(this, getAllImages());
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Grid item clicked. Position: " + position);
                ArrayList<String> imagePaths = getAllImages();
                if (imagePaths != null && position < imagePaths.size()) {
                    Log.d(TAG, "Starting FullScreenImageActivity");
                    Intent intent = new Intent(GridActivity.this, FullScreenImageActivity.class);
                    intent.putExtra("imagePaths", imagePaths);
                    intent.putExtra("position", position);
                    startActivity(intent);
                } else {
                    Toast.makeText(GridActivity.this, "Image not found!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Image not found at position: " + position);
                }
            }
        });
    }

    private ArrayList<String> getAllImages() {
        ArrayList<String> imagePaths = new ArrayList<>();
        String path = getExternalFilesDir(null).toString() + "/saved_images/";
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                imagePaths.add(file.getAbsolutePath());
            }
        }
        return imagePaths;
    }
}
