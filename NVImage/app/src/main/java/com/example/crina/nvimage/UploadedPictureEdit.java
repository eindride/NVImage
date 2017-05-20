package com.example.crina.nvimage;

import android.Manifest;
import android.app.Activity;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class UploadedPictureEdit extends AppCompatActivity {

    private TextView mTextMessage;
    ImageView im;

    GestureDetector gd;
    boolean tapped;

    private Bitmap original;
    private Bitmap operation;

    //float angle = 0;

    StringBuilder currentFilter = new StringBuilder("none");
    StringBuilder currentCrop = new StringBuilder("none");
    StringBuilder currentMirror = new StringBuilder("none");
    float currentAngle=0;
    int currentStep=0;
    int maximumSteps=0;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private class Element {
        StringBuilder filter=new StringBuilder();
        StringBuilder crop=new StringBuilder();
        StringBuilder mirror=new StringBuilder();
        float angle;


        public Element(StringBuilder filter,StringBuilder crop,StringBuilder mirror,float angle) {
            this.filter.append(filter);
            this.crop.append(crop);
            this.mirror.append(mirror);
            this.angle=angle;
        }
    }

    LinkedList<Element> undoList = new LinkedList<>();

    protected BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            View a = findViewById(R.id.linearLayoutHome);
            View b = findViewById(R.id.linearLayoutDash);
            View c = findViewById(R.id.linearLayoutNot);

            switch (item.getItemId()) {
                case R.id.navigation_filtre:
                    a.setVisibility(View.VISIBLE);
                    b.setVisibility(View.GONE);
                    c.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_modif:
                    a.setVisibility(View.GONE);
                    b.setVisibility(View.VISIBLE);
                    c.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_adv:
                    a.setVisibility(View.GONE);
                    b.setVisibility(View.GONE);
                    c.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_picture_edit);

        View a = findViewById(R.id.linearLayoutHome);
        View b = findViewById(R.id.linearLayoutDash);
        View c = findViewById(R.id.linearLayoutNot);
        b.setVisibility(View.GONE);
        c.setVisibility(View.GONE);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        im = (ImageView) findViewById(R.id.imageView);

        Bundle extras= getIntent().getExtras();
        if(extras!=null)
        {
            Uri path = (Uri) extras.get("imgurl");
            im.setImageURI(path);
        }

        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        original = abmp.getBitmap();

        undoList.add(new Element(currentFilter,currentCrop,currentMirror,currentAngle));

        im.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                undoToOriginal();
                return true;
            }
        });
    }


    public void checkSteps() {
        while(currentStep<maximumSteps){
            maximumSteps--;
            undoList.removeLast();
        }
    }

    public void defaultCropButtonAction(View view) {
        String whichCrop = view.getTag().toString();

        if(!undoList.get(currentStep).crop.toString().equals(whichCrop)) {
            // Bitmap cropedBmp = applyDefaultCrop(whichCrop, original)
            // im.setImageBitmap(applyFilter(currentFilter.toString(), cropedBmp));
            im.setImageBitmap(applyFilter(currentFilter.toString(),
                    applyDefaultCrop(whichCrop,
                            applyMirroring(currentMirror.toString(),
                                rotateImage(currentAngle, original)))));

            currentCrop.delete(0, currentCrop.length());
            currentCrop.append(whichCrop);
            Element e = new Element(currentFilter, currentCrop,currentMirror,currentAngle);
            undoList.addLast(e);

            checkSteps();

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
            default: {
                cropedBmp = bmp;
            }
        }
        return cropedBmp;
    }

    public Bitmap defaultCrop11(Bitmap bmp) {
        int dif;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            dif = bmp.getHeight() - bmp.getWidth();
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - dif + 1, bmp.getConfig());

            Log.d("dif", String.valueOf(dif));
            for (int i = dif / 2; i < bmp.getHeight() - dif / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - dif / 2, bmp.getWidth(), 1);
            }
        } else {
            dif = bmp.getWidth() - bmp.getHeight();
            operation = Bitmap.createBitmap(bmp.getWidth() - dif, bmp.getHeight(), bmp.getConfig());

            Log.d("dif", String.valueOf(dif));
            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - dif, dif / 2, i, bmp.getWidth() - dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - dif, 0, i, bmp.getWidth() - dif, 1);
            }
        }

        return operation;
        //im.setImageBitmap(operation);
    }

    public Bitmap defaultCrop43(Bitmap bmp) {
        int dif;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            int width = bmp.getWidth();
            int height = 3 * width / 4;
            dif = bmp.getHeight() - height;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - dif + 1, bmp.getConfig());


            for (int i = dif / 2; i < bmp.getHeight() - dif / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - dif / 2, bmp.getWidth(), 1);
            }
        } else {
            int height = bmp.getHeight();
            int width = 4 * height / 3;
            dif = bmp.getWidth() - width;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth() - dif, bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - dif, dif / 2, i, bmp.getWidth() - dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - dif, 0, i, bmp.getWidth() - dif, 1);
            }
        }

        return operation;
        //im.setImageBitmap(operation);

    }

    public Bitmap defaultCrop34(Bitmap bmp) {
        int dif;
        int[] pixels = new int[1920000];

        if (bmp.getWidth() < bmp.getHeight()) {
            int width = bmp.getWidth();
            int height = 4 * width / 3;
            dif = bmp.getHeight() - height;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight() - dif + 1, bmp.getConfig());


            for (int i = dif / 2; i < bmp.getHeight() - dif / 2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i - dif / 2, bmp.getWidth(), 1);
            }
        } else {
            int height = bmp.getHeight();
            int width = 3 * height / 4;
            dif = bmp.getWidth() - width;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth() - dif, bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth() - dif, dif / 2, i, bmp.getWidth() - dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth() - dif, 0, i, bmp.getWidth() - dif, 1);
            }
        }

        return operation;
        //im.setImageBitmap(operation);

    }

    public void filterButtonAction(View view) {
        String whichFilter = view.getTag().toString();

        if(!undoList.get(currentStep).filter.toString().equals(whichFilter)) {
            //Bitmap filteredBmp = applyFilter(whichFilter, original);
            //im.setImageBitmap(applyDefaultCrop(currentCrop.toString(), filteredBmp));

            im.setImageBitmap(applyFilter(whichFilter,
                    applyDefaultCrop(currentCrop.toString(),
                            applyMirroring(currentMirror.toString(),
                                    rotateImage(currentAngle, original)))));

            currentFilter.delete(0, currentFilter.length());
            currentFilter.append(whichFilter);
            Element e = new Element(currentFilter, currentCrop,currentMirror,currentAngle);
            undoList.add(e);

            checkSteps();
            currentStep++;
            maximumSteps++;
        }
    }

    public Bitmap applyFilter(String whichFilter,Bitmap bmp) {
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
            case "Warmer":{
                filteredBmp = filterWarmer(bmp);
                break;
            }
            case "Cold":{
                filteredBmp = filterCold(bmp);
                break;
            }
            case "AccentColor":{
                filteredBmp = filterAccentColor(bmp);
                break;
            }

            case "White":{
                filteredBmp = filterWhite(bmp);
                break;
            }
            case "TvShow":{
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
        int[] pixels=new int[999999];
        operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
        for (int i = 1; i < bmp.getHeight()/10-1; i++) {
            for(int j=1;j<bmp.getWidth()/10-1;j++) {
                int avg=0;
                bmp.getPixels(pixels, 0, bmp.getWidth(), j*10, i*10, 10, 10);
                for(int q=0;q<10;q++) {
                    avg += pixels[q];
                }
                avg/=100;
                for(int q=0;q<10;q++) {
                    pixels[q]=avg;
                }
                operation.setPixels(pixels, 0, bmp.getWidth(), j*10, i*10, 10, 10);
            }
        }
        return operation;
    }


    public Bitmap filterWhite(Bitmap bmp) {
        return createFilteredBitmap(bmp, 1.3f, 0, 0.32f, 0, 0,
                0.11f, 1.3f, 0, 0, 0,
                0, 0.23f, 1.3f, 0, 0,
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



    public void reverseFilterGreyScale(View view) {
        im.setImageBitmap(createFilteredBitmap(original, 1.32f, 0, 0, 0, 0,
                0, 1.31f, 0, 0, 0,
                0, 0, 1.52f, 0, 0,
                0, 0, 0, 1, 0));
    }

    public Bitmap filterSepia(Bitmap bmp) {
        final ColorMatrix matrixA = new ColorMatrix();
        // making image B&W
        matrixA.setSaturation(.9f);

        final ColorMatrix matrixB = new ColorMatrix(new float[]{
                1.27f, 0, 1, 0, 0,
                0, 1.30f, 1, 0, 0,
                1, 0, 1.37f, 0, 0,
                0, 0, 0, 1, 0});
        // applying scales for RGB color values
        //matrixB.setScale(1f, .95f, .82f, 1.0f);
        matrixA.setConcat(matrixB, matrixA);

        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(matrixA);

        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bmp, 0, 0, paint);

        return bitmap;
        //im.setImageBitmap(bitmap);
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

    /*public void rotate(View view) {
        angle += 90;

        Bitmap rotatedImage = rotateImage(original, angle);
        im.setImageBitmap(rotatedImage);

    }*/

    public void rotateButtonActin(View view) {
        currentAngle = currentAngle + 90;
        if(currentAngle >= 360) currentAngle=0;

        im.setImageBitmap(applyFilter(currentFilter.toString(),
                applyDefaultCrop(currentCrop.toString(),
                        applyMirroring(currentMirror.toString(),
                                rotateImage(currentAngle, original)))));
        Element e = new Element(currentFilter,currentCrop,currentMirror, currentAngle);
        undoList.addLast(e);

        checkSteps();

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

        im.setImageBitmap(applyFilter(currentFilter.toString(),
                applyDefaultCrop(currentCrop.toString(),
                        applyMirroring(whichMirror,
                                rotateImage(currentAngle, original)))));

        currentMirror.delete(0, currentMirror.length());
        currentMirror.append(whichMirror);
        Element e = new Element(currentFilter, currentCrop,currentMirror,currentAngle);
        undoList.addLast(e);

        checkSteps();

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

    public Bitmap mirrorOrizontal(Bitmap bmp){

        int[] pixels=new int[bmp.getWidth()];
        operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
        for (int i = 0; i < bmp.getHeight(); i++) {
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, bmp.getHeight() - i - 1, bmp.getWidth(), 1);
            operation.setPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
        }
        return operation;
        //im.setImageBitmap(operation);
    }

    public Bitmap mirrorVertical(Bitmap bmp){
        int[] pixels=new int[bmp.getHeight()];
        operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
        for (int i = 0; i < bmp.getWidth(); i++) {
            bmp.getPixels(pixels, 0, 1, i, 0, 1, bmp.getHeight());
            operation.setPixels(pixels, 0, 1, bmp.getWidth() - i - 1, 0, 1, bmp.getHeight());
        }
        return operation;
        //im.setImageBitmap(operation);
    }



    public void undoChange(View view) {
        ListIterator<Element> undoListIterator = undoList.listIterator(0);
        /*while(undoListIterator.hasNext()) {
            Element e=undoListIterator.next();
            Log.d("SOMETHING", e.crop.toString() + " " + e.filter.toString());
        }*/

        for(int i=1;i<currentStep;i++) {
            undoListIterator.next();
        }
        Element e=undoListIterator.next();
        //Log.d("SOMETHING", e.crop.toString() + " " + e.filter.toString());

        // Bitmap cropedBmp = applyDefaultCrop(e.crop.toString(),original);
        //im.setImageBitmap(applyFilter(e.filter.toString(),cropedBmp));

        im.setImageBitmap(applyFilter(e.filter.toString().toString(),
                applyDefaultCrop(e.crop.toString().toString(),
                        applyMirroring(e.mirror.toString(),
                                rotateImage(e.angle, original)))));

        currentCrop.delete(0,currentCrop.length());
        currentCrop.append(e.crop.toString());

        currentFilter.delete(0,currentFilter.length());
        currentFilter.append(e.filter.toString());

        currentMirror.delete(0,currentMirror.length());
        currentMirror.append(e.mirror.toString());

        currentAngle = e.angle;

        if(currentStep>0) currentStep--;


        Log.d("Curent modifications: ", e.filter.toString() + " " + e.crop.toString() + " " + e.mirror.toString() + " " + e.angle);
    }

    public void redoChange(View view) {
        ListIterator<Element> undoListIterator = undoList.listIterator(0);

        if(currentStep<maximumSteps) {
            currentStep++;

            for(int i=1;i<=currentStep;i++) {
                undoListIterator.next();
            }
            Element e=undoListIterator.next();

            //Bitmap cropedBmp = applyDefaultCrop(e.crop.toString(),original);
            //im.setImageBitmap(applyFilter(e.filter.toString(),cropedBmp));

            im.setImageBitmap(applyFilter(e.filter.toString().toString(),
                    applyDefaultCrop(e.crop.toString().toString(),
                            applyMirroring(e.mirror.toString(),
                                    rotateImage(e.angle, original)))));

            currentCrop.delete(0,currentCrop.length());
            currentCrop.append(e.crop.toString());

            currentFilter.delete(0,currentFilter.length());
            currentFilter.append(e.filter.toString());

            currentAngle = e.angle;


        }
    }

    public void undoToOriginal() {
        currentCrop.delete(0,currentCrop.length());
        currentCrop.append("none");

        currentFilter.delete(0,currentFilter.length());
        currentFilter.append("none");

        currentMirror.delete(0,currentMirror.length());
        currentMirror.append("none");

        currentAngle = 0;

        undoList.addLast(new Element(currentFilter,currentCrop,currentMirror,currentAngle));

        checkSteps();

        currentStep++;
        maximumSteps++;

        im.setImageBitmap(original);
    }
    public void  saveButtonAction(View v){
        verifyStoragePermissions(UploadedPictureEdit.this);
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
            );}

        ImageView im = (ImageView) findViewById(R.id.imageView);
        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        Bitmap bm = abmp.getBitmap();
        saveBitmap(bm);
        Log.d("asda","asf");
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
        //Log.i(TAG, "" + file);
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

}
