package com.example.cameraapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.cameraapp.utils.GPSTracker;
import java.util.ArrayList;

public class FullScreenImageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<String> imagePaths;
    private int position;
    private TextView descriptionView;
    private String activeImagePath; // Member variable to store active file path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_image);

        viewPager = findViewById(R.id.view_pager);
        descriptionView = findViewById(R.id.full_screen_description);

        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        position = getIntent().getIntExtra("position", 0);

        // Initialize activeImagePath with the first image path
        if (!imagePaths.isEmpty()) {
            activeImagePath = imagePaths.get(position);
        }

        Log.d("Active Path : ", "Path : " + activeImagePath);

        DBHandler dbHandler = new DBHandler(this);
        ArrayList<CourseModal> data = dbHandler.readCourses();
        String descriptionLoc = null;
        String descriptionTime = null;

        String result = activeImagePath.substring(activeImagePath.lastIndexOf('/') + 1);

        for(int i = 0; i < data.size(); i++) {
            if(result.equals(data.get(i).photoName)) {
                descriptionLoc = data.get(i).photoLocation;
                descriptionTime = data.get(i).photoDate;
                break;
            }
        }

        String newText = "Location : " + descriptionLoc + "\n" + "Date and Time : " + descriptionTime;

        descriptionView.setText(newText);

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imagePaths);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(position);
    }
}

