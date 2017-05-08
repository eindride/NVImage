package com.example.crina.nvimage;

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
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.ListIterator;

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
                            rotateImage(currentAngle,original))));

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
                            rotateImage(currentAngle,original))));

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
            default: {
                filteredBmp = bmp;
            }
        }
        return filteredBmp;
    }

    public Bitmap filterGreyScale(Bitmap bmp) {
        return createFilteredBitmap(bmp, 0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
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
                        rotateImage(currentAngle,original))));
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
                        rotateImage(currentAngle,
                                applyMirroring(whichMirror,original)))));

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
                        rotateImage(e.angle,original))));

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
                            rotateImage(e.angle,original))));

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

}
