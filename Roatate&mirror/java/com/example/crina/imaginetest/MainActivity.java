package com.example.crina.imaginetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {
    Button b;
    ImageView im;

    private Bitmap bmp, auxBmp;
    private Bitmap operation;
    ImageView img;
    Bitmap source;
    float angle=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = (Button) findViewById(R.id.button5);
        im = (ImageView) findViewById(R.id.imageView);

        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        bmp = abmp.getBitmap();
        auxBmp = abmp.getBitmap();
    }

    public void undo(View view){
        im.setImageBitmap(bmp);
    }

    public void mirrorOrizontal(View view){

        int[] pixels=new int[1920000];
        operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
        for (int i = 0; i < bmp.getHeight(); i++) {
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, bmp.getHeight() - i - 1, bmp.getWidth(), 1);
            operation.setPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
        }
        im.setImageBitmap(operation);
    }

    public void mirrorVertical(View view){
        int[] pixels=new int[1920000];
        operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
        for (int i = 0; i < bmp.getWidth(); i++) {
            bmp.getPixels(pixels, 0, 1, i, 0, 1, bmp.getHeight());
            operation.setPixels(pixels, 0, 1, bmp.getWidth() - i - 1, 0, 1, bmp.getHeight());
        }
        im.setImageBitmap(operation);
    }

    public void rotate(View view){
        angle+=90;
        Bitmap rotatedImage=rotateImage(bmp,angle);
        im.setImageBitmap(rotatedImage);
    }

    public static Bitmap rotateImage(Bitmap sourceImage, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }


}