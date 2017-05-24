package com.example.crina.nvimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class cropActivity extends AppCompatActivity {

    Bitmap original;
    Bitmap operation;
    StringBuilder crop = new StringBuilder("none");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);


        ImageView imageView = (ImageView) findViewById(R.id.id);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Uri path = (Uri) extras.get("imgurl");
            imageView.setImageURI(path);
            File file = new File(Environment.getExternalStorageDirectory(), "NVImage/TempFile.jpg");
            file.delete();
        }
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        original = abmp.getBitmap();

    }

    public void defaultCropButtonAction(View view) {
        String whichCrop = view.getTag().toString();

        ImageView img = (ImageView) findViewById(R.id.id);
        img.setImageBitmap(applyDefaultCrop(whichCrop, original));


    }

    public Bitmap applyDefaultCrop(String whichCrop, Bitmap bmp) {
        Bitmap cropedBmp;
        switch (whichCrop) {
            case "1:1": {
                cropedBmp = defaultCrop11(bmp);
                crop.delete(0, crop.length());
                crop.append(whichCrop);
                break;
            }
            case "4:3": {
                cropedBmp = defaultCrop43(bmp);
                crop.delete(0, crop.length());
                crop.append(whichCrop);
                break;
            }
            case "3:4": {
                cropedBmp = defaultCrop34(bmp);
                crop.delete(0, crop.length());
                crop.append(whichCrop);
                break;
            }
            case "5:7": {
                cropedBmp = defaultCrop57(bmp);
                crop.delete(0, crop.length());
                crop.append(whichCrop);
                break;
            }
            case "7:5": {
                cropedBmp = defaultCrop75(bmp);
                crop.delete(0, crop.length());
                crop.append(whichCrop);
                break;
            }

            default: {
                cropedBmp = bmp;
                crop.delete(0, crop.length());
                crop.append(whichCrop);
            }
        }
        return cropedBmp;
    }

    public Bitmap defaultCrop11(Bitmap bmp) {
        int differenceInSides;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            differenceInSides = bmp.getHeight() - bmp.getWidth();
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - differenceInSides + 1, bmp.getConfig());

            Log.d("dif", String.valueOf(differenceInSides));
            for (int i = differenceInSides / 2; i < bmp.getHeight() - differenceInSides / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - differenceInSides / 2, bmp.getWidth(), 1);
            }
        } else {
            differenceInSides = bmp.getWidth() - bmp.getHeight();
            operation = Bitmap.createBitmap(bmp.getWidth() - differenceInSides, bmp.getHeight(), bmp.getConfig());

            Log.d("dif", String.valueOf(differenceInSides));
            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - differenceInSides, differenceInSides / 2, i, bmp.getWidth() - differenceInSides, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - differenceInSides, 0, i, bmp.getWidth() - differenceInSides, 1);
            }
        }

        return operation;
        //im.setImageBitmap(operation);
    }

    public Bitmap defaultCrop43(Bitmap bmp) {
        int differenceInSides;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            int width = bmp.getWidth();
            int height = 3 * width / 4;
            differenceInSides = Math.abs(bmp.getHeight() - height);
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - differenceInSides + 1, bmp.getConfig());


            for (int i = differenceInSides / 2; i < bmp.getHeight() - differenceInSides / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - differenceInSides / 2, bmp.getWidth(), 1);
            }
        } else {
            int height = bmp.getHeight();
            int width = 4 * height / 3;
            differenceInSides = Math.abs(bmp.getWidth() - width);
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth() - differenceInSides, bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - differenceInSides, differenceInSides / 2, i, bmp.getWidth() - differenceInSides, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - differenceInSides, 0, i, bmp.getWidth() - differenceInSides, 1);
            }
        }

        return operation;
        //im.setImageBitmap(operation);

    }

    public Bitmap defaultCrop34(Bitmap bmp) {
        int differenceInSides;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            int width = bmp.getWidth();
            int height = 4 * width / 3;
            differenceInSides = Math.abs(bmp.getHeight() - height);
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - differenceInSides + 1, bmp.getConfig());


            for (int i = differenceInSides / 2; i < bmp.getHeight() - differenceInSides / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - differenceInSides / 2, bmp.getWidth(), 1);
            }
        } else {
            int height = bmp.getHeight();
            int width = 3 * height / 4;
            differenceInSides = Math.abs(bmp.getWidth() - width);
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth() - differenceInSides, bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - differenceInSides, differenceInSides / 2, i, bmp.getWidth() - differenceInSides, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - differenceInSides, 0, i, bmp.getWidth() - differenceInSides, 1);
            }
        }

        return operation;
        //im.setImageBitmap(operation);

    }

    public Bitmap defaultCrop57(Bitmap bmp) {
        int differenceInSides;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            int width = bmp.getWidth();
            int height = 7 * width / 5;
            differenceInSides = Math.abs(bmp.getHeight() - height);
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - differenceInSides + 1, bmp.getConfig());


            for (int i = differenceInSides / 2; i < bmp.getHeight() - differenceInSides / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - differenceInSides / 2, bmp.getWidth(), 1);
            }
        } else {
            int height = bmp.getHeight();
            int width = 5 * height / 7;
            differenceInSides = Math.abs(bmp.getWidth() - width);
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth() - differenceInSides, bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - differenceInSides, differenceInSides / 2, i, bmp.getWidth() - differenceInSides, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - differenceInSides, 0, i, bmp.getWidth() - differenceInSides, 1);
            }
        }
        return operation;

    }

    public Bitmap defaultCrop75(Bitmap bmp) {
        int differenceInSides;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            int width = bmp.getWidth();
            int height = 5 * width / 7;
            differenceInSides = bmp.getHeight() - height;
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - differenceInSides + 1, bmp.getConfig());


            for (int i = differenceInSides / 2; i < bmp.getHeight() - differenceInSides / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - differenceInSides / 2, bmp.getWidth(), 1);
            }
        } else {
            int height = bmp.getHeight();
            int width = 7 * height / 5;
            differenceInSides = Math.abs(bmp.getWidth() - width);
            Log.d("dif", String.valueOf(differenceInSides));
            operation = Bitmap.createBitmap(bmp.getWidth() - differenceInSides, bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - differenceInSides, differenceInSides / 2, i, bmp.getWidth() - differenceInSides, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - differenceInSides, 0, i, bmp.getWidth() - differenceInSides, 1);
            }
        }
        return operation;

    }

    public void saveTempFile(Bitmap bm) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/NVImage");
        myDir.mkdirs();

        String fname = "TempFile.jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void okButtonAction(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.id);
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        Bitmap bmp = abmp.getBitmap();

        saveTempFile(bmp);

        Intent intent = new Intent(this, UploadedPictureEdit.class);
        intent.putExtra("whichCrop", crop.toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void xButtonAction(View view) {
        finish();
    }
}

