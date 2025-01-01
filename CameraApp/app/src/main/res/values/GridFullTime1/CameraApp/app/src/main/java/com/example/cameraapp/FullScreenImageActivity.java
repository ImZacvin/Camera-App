// FullScreenImageActivity.java

package com.example.cameraapp;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class FullScreenImageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private ArrayList<String> imagePaths;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_image);

        viewPager = findViewById(R.id.view_pager);

        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        position = getIntent().getIntExtra("position", 0);

        imagePagerAdapter = new ImagePagerAdapter(this, imagePaths);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(position);
    }
}
