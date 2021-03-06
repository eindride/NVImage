package com.example.crina.nvimage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class UploadedPictureEdit extends AppCompatActivity {

    ImageView imageView;
    Uri path;

    private Bitmap original;
    private Bitmap operation;

    private static final int CROP_ACTIVITY = 1;
    private static final int RGB_ACTIVITY = 2;
    private static final int BRIGHTNESS_ACTIVITY = 3;
    private static final int CONTRAST_ACTIVITY = 4;

    StringBuilder currentFilter = new StringBuilder("none");
    StringBuilder currentCrop = new StringBuilder("none");
    StringBuilder currentMirror = new StringBuilder("none");
    float currentAngle = 0;
    float currentContrastRedValue = 1;
    float currentContrastGreenValue = 1;
    float currentContrastBlueValue = 1;
    float currentRgbRedValue = 1;
    float currentRgbGreenValue = 1;
    float currentRgbBlueValue = 1;
    int currentBrightness = 0;

    int currentStep = 0;
    int maximumSteps = 0;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private class Element {
        StringBuilder filter = new StringBuilder();
        StringBuilder crop = new StringBuilder();
        StringBuilder mirror = new StringBuilder();
        float angle;
        float contrastRedValue;
        float contrastGreenValue;
        float contrastBlueValue;
        float rgbRedValue;
        float rgbGreenValue;
        float rgbBlueValue;
        int brightness;

        public Element(StringBuilder filter, StringBuilder crop, StringBuilder mirror, float angle,
                       float contrastRedValue, float contrastGreenValue, float contrastBlueValue,
                       float rgbRedValue, float rgbGreenValue, float rgbBlueValue, int brightness) {
            this.filter.append(filter);
            this.crop.append(crop);
            this.mirror.append(mirror);
            this.angle = angle;
            this.contrastRedValue = contrastRedValue;
            this.contrastGreenValue = contrastGreenValue;
            this.contrastBlueValue = contrastBlueValue;
            this.rgbRedValue = rgbRedValue;
            this.rgbGreenValue = rgbGreenValue;
            this.rgbBlueValue = rgbBlueValue;
            this.brightness = brightness;
        }
    }

    LinkedList<Element> undoList = new LinkedList<>();

    protected BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            View viewNavigatonFilters = findViewById(R.id.linearLayoutHome);
            View viewNavigationModif = findViewById(R.id.linearLayoutDash);
            View viewNavigationAdv = findViewById(R.id.linearLayoutNot);

            switch (item.getItemId()) {
                case R.id.navigation_filtre:
                    viewNavigatonFilters.setVisibility(View.VISIBLE);
                    viewNavigationModif.setVisibility(View.GONE);
                    viewNavigationAdv.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_modif:
                    viewNavigatonFilters.setVisibility(View.GONE);
                    viewNavigationModif.setVisibility(View.VISIBLE);
                    viewNavigationAdv.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_adv:
                    viewNavigatonFilters.setVisibility(View.GONE);
                    viewNavigationModif.setVisibility(View.GONE);
                    viewNavigationAdv.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_picture_edit);

        verifyStoragePermissions(UploadedPictureEdit.this);

        View viewNavigationModif = findViewById(R.id.linearLayoutDash);
        View viewNavigationAdv = findViewById(R.id.linearLayoutNot);
        viewNavigationModif.setVisibility(View.GONE);
        viewNavigationAdv.setVisibility(View.GONE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        imageView = (ImageView) findViewById(R.id.imageView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = (Uri) extras.get("imgurl");
            imageView.setImageURI(path);
            BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
            original = abmp.getBitmap();
        }

        undoList.add(new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness));

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                undoToOriginal();
                return true;
            }
        });
    }


    public void checkSteps() {
        while (currentStep < maximumSteps) {
            maximumSteps--;
            undoList.removeLast();
        }
    }

    public void defaultCropButtonAction(View view) {
        String whichCrop = view.getTag().toString();

        if (!undoList.get(currentStep).crop.toString().equals(whichCrop)) {
            imageView.setImageBitmap(applyBrightness(currentBrightness,
                    applyContrast(currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                            applyRGB(currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue,
                                    applyFilter(currentFilter.toString(),
                                            applyDefaultCrop(whichCrop,
                                                    applyMirroring(currentMirror.toString(),
                                                            rotateImage(currentAngle, original))))))));

            currentCrop.delete(0, currentCrop.length());
            currentCrop.append(whichCrop);
            Element e = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                    currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                    currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

            checkSteps();
            undoList.addLast(e);

            currentStep++;
            maximumSteps++;
        }
    }

    public Bitmap applyDefaultCrop(String whichCrop, Bitmap bmp) {
        Bitmap cropedBmp;
        switch (whichCrop) {
            case "1:1": {
                cropedBmp = defaultCrop11(bmp);
                break;
            }
            case "4:3": {
                cropedBmp = defaultCrop43(bmp);
                break;
            }
            case "3:4": {
                cropedBmp = defaultCrop34(bmp);
                break;
            }
            case "5:7": {
                cropedBmp = defaultCrop57(bmp);
                break;
            }
            case "7:5": {
                cropedBmp = defaultCrop75(bmp);
                break;
            }
            default: {
                cropedBmp = bmp;
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
            differenceInSides = Math.abs(bmp.getHeight() - height);
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

    public void filterButtonAction(View view) {
        String whichFilter = view.getTag().toString();

        if (!undoList.get(currentStep).filter.toString().equals(whichFilter)) {
            imageView.setImageBitmap(applyBrightness(currentBrightness,
                    applyContrast(currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                            applyRGB(currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue,
                                    applyFilter(whichFilter,
                                            applyDefaultCrop(currentCrop.toString(),
                                                    applyMirroring(currentMirror.toString(),
                                                            rotateImage(currentAngle, original))))))));

            currentFilter.delete(0, currentFilter.length());
            currentFilter.append(whichFilter);
            Element e = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                    currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                    currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

            checkSteps();
            undoList.add(e);

            currentStep++;
            maximumSteps++;
        }
    }

    public Bitmap applyFilter(String whichFilter, Bitmap bmp) {
        Bitmap filteredBmp;
        switch (whichFilter) {
            case "GreyScale": {
                filteredBmp = filterGreyScale(bmp);
                break;
            }
            case "Sepia": {
                filteredBmp = filterSepia(bmp);
                break;
            }
            case "Warmer": {
                filteredBmp = filterWarmer(bmp);
                break;
            }
            case "Cold": {
                filteredBmp = filterCold(bmp);
                break;
            }
            case "AccentColor": {
                filteredBmp = filterAccentColor(bmp);
                break;
            }

            case "White": {
                filteredBmp = filterWhite(bmp);
                break;
            }
            case "Ghost": {
                filteredBmp = filterGhost(bmp);
                break;
            }
            case "Ice": {
                filteredBmp = filterIce(bmp);
                break;
            }
            case "TvShow": {
                filteredBmp = filterTvShow(bmp);
                break;
            }
            default: {
                filteredBmp = bmp;
            }
        }
        return filteredBmp;
    }

    public Bitmap filterTvShow(Bitmap bmp) {
        int[] pixels = new int[999999];
        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        for (int i = 1; i < bmp.getHeight() / 10 - 1; i++) {
            for (int j = 1; j < bmp.getWidth() / 10 - 1; j++) {
                int avg = 0;
                bmp.getPixels(pixels, 0, bmp.getWidth(), j * 10, i * 10, 10, 10);
                for (int q = 0; q < 10; q++) {
                    avg += pixels[q];
                }
                avg /= 100;
                for (int q = 0; q < 10; q++) {
                    pixels[q] = avg;
                }
                operation.setPixels(pixels, 0, bmp.getWidth(), j * 10, i * 10, 10, 10);
            }
        }
        return operation;
    }

    public Bitmap filterIce(Bitmap bmp) {
        return createFilteredBitmap(bmp, 0.23f, 1, 0, 0, 0,
                0, 1, 0.44f, 0, 0,
                0, 0.32f, 1, 0, 0,
                1, 0, 0, 0.7f, 0);
    }

    public Bitmap filterGhost(Bitmap bmp) {
        return createFilteredBitmap(bmp, 0, 1, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                1, 0, 0, 0, 0);
    }

    public Bitmap filterWhite(Bitmap bmp) {
        return createFilteredBitmap(bmp, 0.943f, 0.423f, 0, 0.11f, 0,
                0, 0.908f, 0.43f, 0.12f, 0,
                0.43f, 0, 0.903f, 0.12f, 0,
                0, 0, 0, 1, 0);
    }

    public Bitmap filterAccentColor(Bitmap bmp) {
        return createFilteredBitmap(bmp, 1.3f, 0, 0, 0, 0,
                0, 1.3f, 0, 0, 0,
                0, 0, 1.3f, 0, 0,
                0, 0, 0, 1, 0);
    }

    public Bitmap filterCold(Bitmap bmp) {
        return createFilteredBitmap(bmp, 0.643f, 0.123f, 0, 0.11f, 0,
                0, 0.508f, 0.23f, 0.12f, 0,
                0.23f, 0, 0.503f, 0.12f, 0,
                0, 0, 0, 1, 0);
    }

    public Bitmap filterGreyScale(Bitmap bmp) {
        return createFilteredBitmap(bmp, 0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
                0, 0, 0, 1, 0);
    }

    public Bitmap filterWarmer(Bitmap bmp) {
        return createFilteredBitmap(bmp, 0.843f, 0, 0, 0, 0,
                0, 0.708f, 0, 0, 0,
                0, 0, 0.603f, 0, 0,
                0, 0, 0, 1, 0);
    }


    public Bitmap filterSepia(Bitmap bmp) {
        final ColorMatrix matrixA = new ColorMatrix();
        matrixA.setSaturation(.9f);

        final ColorMatrix matrixB = new ColorMatrix(new float[]{
                1.27f, 0, 1, 0, 0,
                0, 1.30f, 1, 0, 0,
                1, 0, 1.37f, 0, 0,
                0, 0, 0, 1, 0});
        matrixA.setConcat(matrixB, matrixA);

        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(matrixA);

        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bmp, 0, 0, paint);

        return bitmap;
    }


    private Bitmap createFilteredBitmap(Bitmap src, float a, float b, float c, float d, float e,
                                        float f, float g, float h, float i, float j,
                                        float k, float l, float m, float n, float o,
                                        float p, float q, float r, float s, float t) {
        ColorMatrix colorMatrix =
                new ColorMatrix(new float[]{
                        a, b, c, d, e,
                        f, g, h, i, j,
                        k, l, m, n, o,
                        p, q, r, s, t});

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(src, 0, 0, paint);

        return bitmap;
    }

    public Bitmap applyRGB(float redValue, float greenValue, float blueValue, Bitmap bmp) {

        return createFilteredBitmap(bmp, redValue, 0, 0, 0, 0,
                0, greenValue, 0, 0, 0,
                0, 0, blueValue, 0, 0,
                0, 0, 0, 1, 0);
    }

    public Bitmap applyContrast(float redValue, float greenValue, float blueValue, Bitmap bmp) {

        return createFilteredBitmap(bmp, redValue, 0, 0, 0, 0,
                0, greenValue, 0, 0, 0,
                0, 0, blueValue, 0, 0,
                0, 0, 0, 1, 0);
    }

    public Bitmap applyBrightness(int brightness, Bitmap bmp) {
        return createFilteredBitmap(bmp, 1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0);
    }

    public void rotateButtonActin(View view) {
        currentAngle = currentAngle + 90;
        if (currentAngle >= 360) currentAngle = 0;

        imageView.setImageBitmap(applyBrightness(currentBrightness,
                applyContrast(currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                        applyRGB(currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue,
                                applyFilter(currentFilter.toString(),
                                        applyDefaultCrop(currentCrop.toString(),
                                                applyMirroring(currentMirror.toString(),
                                                        rotateImage(currentAngle, original))))))));

        Element e = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

        checkSteps();
        undoList.addLast(e);

        currentStep++;
        maximumSteps++;
    }

    public static Bitmap rotateImage(float angle, Bitmap sourceImage) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }

    public void mirrorButtonAction(View view) {
        String whichMirror = view.getTag().toString();

        imageView.setImageBitmap(applyBrightness(currentBrightness,
                applyContrast(currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                        applyRGB(currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue,
                                applyFilter(currentFilter.toString(),
                                        applyDefaultCrop(currentCrop.toString(),
                                                applyMirroring(whichMirror,
                                                        rotateImage(currentAngle, original))))))));

        currentMirror.delete(0, currentMirror.length());
        currentMirror.append(whichMirror);
        Element e = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

        checkSteps();
        undoList.addLast(e);

        currentStep++;
        maximumSteps++;
    }

    public Bitmap applyMirroring(String whichMirror, Bitmap bmp) {
        Bitmap mirroredBmp;
        switch (whichMirror) {
            case "Vertical": {
                mirroredBmp = mirrorVertical(bmp);
                break;
            }
            case "Horizontal": {
                mirroredBmp = mirrorOrizontal(bmp);
                break;
            }
            default: {
                mirroredBmp = bmp;
                break;
            }
        }
        return mirroredBmp;
    }

    public Bitmap mirrorOrizontal(Bitmap bmp) {
        int[] pixels = new int[bmp.getWidth()];
        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        for (int i = 0; i < bmp.getHeight(); i++) {
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, bmp.getHeight() - i - 1, bmp.getWidth(), 1);
            operation.setPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
        }
        return operation;
    }

    public Bitmap mirrorVertical(Bitmap bmp) {
        int[] pixels = new int[bmp.getHeight()];
        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        for (int i = 0; i < bmp.getWidth(); i++) {
            bmp.getPixels(pixels, 0, 1, i, 0, 1, bmp.getHeight());
            operation.setPixels(pixels, 0, 1, bmp.getWidth() - i - 1, 0, 1, bmp.getHeight());
        }
        return operation;
    }


    public void undoChange(View view) {
        ListIterator<Element> undoListIterator = undoList.listIterator(0);

        for (int i = 1; i < currentStep; i++) {
            undoListIterator.next();
        }
        Element e = undoListIterator.next();

        imageView.setImageBitmap(applyBrightness(e.brightness,
                applyContrast(e.contrastRedValue, e.contrastGreenValue, e.contrastBlueValue,
                        applyRGB(e.rgbRedValue, e.rgbGreenValue, e.rgbBlueValue,
                                applyFilter(e.filter.toString(),
                                        applyDefaultCrop(e.crop.toString(),
                                                applyMirroring(e.mirror.toString(),
                                                        rotateImage(e.angle, original))))))));

        currentCrop.delete(0, currentCrop.length());
        currentCrop.append(e.crop.toString());

        currentFilter.delete(0, currentFilter.length());
        currentFilter.append(e.filter.toString());

        currentMirror.delete(0, currentMirror.length());
        currentMirror.append(e.mirror.toString());

        currentAngle = e.angle;

        currentContrastRedValue = e.contrastRedValue;
        currentContrastGreenValue = e.contrastGreenValue;
        currentContrastBlueValue = e.contrastBlueValue;

        currentRgbRedValue = e.rgbRedValue;
        currentRgbGreenValue = e.rgbGreenValue;
        currentRgbBlueValue = e.rgbBlueValue;

        currentBrightness = e.brightness;

        if (currentStep > 0) currentStep--;
    }

    public void redoChange(View view) {
        ListIterator<Element> undoListIterator = undoList.listIterator(0);

        if (currentStep < maximumSteps) {
            currentStep++;

            for (int i = 1; i <= currentStep; i++) {
                undoListIterator.next();
            }
            Element e = undoListIterator.next();

            imageView.setImageBitmap(applyBrightness(e.brightness,
                    applyContrast(e.contrastRedValue, e.contrastGreenValue, e.contrastBlueValue,
                            applyRGB(e.rgbRedValue, e.rgbGreenValue, e.rgbBlueValue,
                                    applyFilter(e.filter.toString(),
                                            applyDefaultCrop(e.crop.toString(),
                                                    applyMirroring(e.mirror.toString(),
                                                            rotateImage(e.angle, original))))))));

            currentCrop.delete(0, currentCrop.length());
            currentCrop.append(e.crop.toString());

            currentFilter.delete(0, currentFilter.length());
            currentFilter.append(e.filter.toString());

            currentAngle = e.angle;

            currentContrastRedValue = e.contrastRedValue;
            currentContrastGreenValue = e.contrastGreenValue;
            currentContrastBlueValue = e.contrastBlueValue;

            currentRgbRedValue = e.rgbRedValue;
            currentRgbGreenValue = e.rgbGreenValue;
            currentRgbBlueValue = e.rgbBlueValue;

            currentBrightness = e.brightness;
        }
    }

    public void undoToOriginal() {
        currentCrop.delete(0, currentCrop.length());
        currentCrop.append("none");

        currentFilter.delete(0, currentFilter.length());
        currentFilter.append("none");

        currentMirror.delete(0, currentMirror.length());
        currentMirror.append("none");

        currentAngle = 0;

        currentContrastRedValue = 1;
        currentContrastGreenValue = 1;
        currentContrastBlueValue = 1;

        currentRgbRedValue = 1;
        currentRgbGreenValue = 1;
        currentRgbBlueValue = 1;

        currentBrightness = 0;

        undoList.addLast(new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness));

        checkSteps();

        currentStep++;
        maximumSteps++;

        imageView.setImageBitmap(original);
    }

    public void saveButtonAction(View v) {
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();

        verifyStoragePermissions(UploadedPictureEdit.this);

        ImageView im = (ImageView) findViewById(R.id.imageView);
        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        Bitmap bm = abmp.getBitmap();
        saveBitmap(bm);
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private void saveBitmap(Bitmap bm) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/NVImage");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
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

    public void cropButtonAction(View v) {
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        Bitmap bm = abmp.getBitmap();

        saveTempFile(bm);

        File file = new File(Environment.getExternalStorageDirectory(), "NVImage/TempFile.jpg");
        Uri fileURI = Uri.fromFile(file);

        Intent intent = new Intent(this, cropActivity.class);
        intent.putExtra("imgurl", fileURI);
        startActivityForResult(intent, CROP_ACTIVITY);
    }

    public void rgbButtonAction(View v) {
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        Bitmap bm = abmp.getBitmap();

        saveTempFile(bm);

        File file = new File(Environment.getExternalStorageDirectory(), "NVImage/TempFile.jpg");
        Uri fileURI = Uri.fromFile(file);

        Intent intent = new Intent(this, rgbActivity.class);
        intent.putExtra("imgurl", fileURI);
        startActivityForResult(intent, RGB_ACTIVITY);
    }

    public void contrastButtonAction(View v) {
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        Bitmap bm = abmp.getBitmap();

        saveTempFile(bm);

        File file = new File(Environment.getExternalStorageDirectory(), "NVImage/TempFile.jpg");
        Uri fileURI = Uri.fromFile(file);

        Intent intent = new Intent(this, contrastActivity.class);
        intent.putExtra("imgurl", fileURI);
        startActivityForResult(intent, CONTRAST_ACTIVITY);
    }

    public void brightnessButtonAction(View v) {
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        Bitmap bm = abmp.getBitmap();

        saveTempFile(bm);

        File file = new File(Environment.getExternalStorageDirectory(), "NVImage/TempFile.jpg");
        Uri fileURI = Uri.fromFile(file);

        Intent intent = new Intent(this, brightnessActivity.class);
        intent.putExtra("imgurl", fileURI);
        startActivityForResult(intent, BRIGHTNESS_ACTIVITY);
    }

    public void putTempFileInImageView() {
        File file = new File(Environment.getExternalStorageDirectory(), "NVImage/TempFile.jpg");
        Uri fileURI = Uri.fromFile(file);

        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setImageURI(null);
        img.setImageURI(fileURI);
        file.delete();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CROP_ACTIVITY:
                    String whichCrop = data.getExtras().getString("whichCrop");
                    putTempFileInImageView();

                    currentCrop.delete(0, currentCrop.length());
                    currentCrop.append(whichCrop);

                    Element e = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                            currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                            currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

                    checkSteps();
                    undoList.addLast(e);

                    currentStep++;
                    maximumSteps++;
                    break;

                case RGB_ACTIVITY:
                    float[] whichRGB = data.getExtras().getFloatArray("whichRGB");
                    putTempFileInImageView();

                    currentRgbRedValue = whichRGB[0];
                    currentRgbGreenValue = whichRGB[1];
                    currentRgbBlueValue = whichRGB[2];

                    Element el = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                            currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                            currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

                    checkSteps();
                    undoList.addLast(el);

                    currentStep++;
                    maximumSteps++;
                    break;

                case CONTRAST_ACTIVITY:
                    float[] whichContrast = data.getExtras().getFloatArray("whichContrast");
                    putTempFileInImageView();

                    currentContrastRedValue = whichContrast[0];
                    currentContrastGreenValue = whichContrast[1];
                    currentContrastBlueValue = whichContrast[2];

                    Element e2 = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                            currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                            currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

                    checkSteps();
                    undoList.addLast(e2);

                    currentStep++;
                    maximumSteps++;
                    break;
                case BRIGHTNESS_ACTIVITY:
                    int whichBRI = data.getExtras().getInt("whichBRI");
                    putTempFileInImageView();

                    currentBrightness = whichBRI;

                    Element e3 = new Element(currentFilter, currentCrop, currentMirror, currentAngle,
                            currentContrastRedValue, currentContrastGreenValue, currentContrastBlueValue,
                            currentRgbRedValue, currentRgbGreenValue, currentRgbBlueValue, currentBrightness);

                    checkSteps();
                    undoList.addLast(e3);

                    currentStep++;
                    maximumSteps++;
                    break;
            }
        }
    }

}
