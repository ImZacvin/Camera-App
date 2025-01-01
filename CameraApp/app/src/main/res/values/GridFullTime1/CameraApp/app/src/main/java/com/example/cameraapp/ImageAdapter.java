package com.example.cameraapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> imagePaths;

    public ImageAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.grid_image);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(position));
        // Add timestamp watermark
        Bitmap markedBitmap = addTimestampToBitmap(bitmap, getCurrentTimeStamp());
        imageView.setImageBitmap(markedBitmap);

        return convertView;
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private Bitmap addTimestampToBitmap(Bitmap src, String timestamp) {
        Bitmap resultBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(context.getResources().getColor(R.color.timestamp_color));
        paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.timestamp_size));
        paint.setAntiAlias(true);

        // Position the timestamp at the bottom left corner
        canvas.drawText(timestamp, 10, src.getHeight() - 10, paint);

        return resultBitmap;
    }
}
