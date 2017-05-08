package helloworld.example.com.nvimagecropview;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    String msg = "Android : ";

    ImageView im;

    private Bitmap bmp;
    private Bitmap operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	//branch test
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        im = (ImageView) findViewById(R.id.imageView);

        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        bmp = abmp.getBitmap();
    }


    public void cropBottom(View view){
        int[] pixels=new int[1920000];
        operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
        for (int i = 0; i < bmp.getHeight(); i++) {
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
            operation.setPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
        }
        im.setImageBitmap(operation);
    }

    public void defaultCrop11(View view){
        int dif;
        int[] pixels=new int[1920000];

        if(bmp.getWidth()<bmp.getHeight()){
            dif=bmp.getHeight()-bmp.getWidth();
            operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight()-dif+1, bmp.getConfig());

            Log.d("dif", String.valueOf(dif));
            for (int i = dif/2; i < bmp.getHeight()-dif/2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i-dif/2, bmp.getWidth(), 1);
            }
        }
        else{
            dif=bmp.getWidth()-bmp.getHeight();
            operation = Bitmap.createBitmap(bmp.getWidth()-dif,bmp.getHeight(), bmp.getConfig());

            Log.d("dif", String.valueOf(dif));
            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth()-dif, dif/2, i, bmp.getWidth()-dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth()-dif, 0, i, bmp.getWidth()-dif, 1);
            }
        }
        im.setImageBitmap(operation);
    }

    public void defaultCrop43(View view){
        int dif;
        int[] pixels=new int[1920000];

        if(bmp.getWidth()<bmp.getHeight()){
            int width=bmp.getWidth();
            int height=3*width/4;
            dif=bmp.getHeight()-height;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight()-dif+1, bmp.getConfig());


            for (int i = dif/2; i < bmp.getHeight()-dif/2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i-dif/2, bmp.getWidth(), 1);
            }
        }
        else{
            int height=bmp.getHeight();
            int width=4*height/3;
            dif=bmp.getWidth()-width;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth()-dif,bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth()-dif, dif/2, i, bmp.getWidth()-dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth()-dif, 0, i, bmp.getWidth()-dif, 1);
            }
        }
        im.setImageBitmap(operation);

    }

    public void defaultCrop34(View view){
        int dif;
        int[] pixels=new int[1920000];

        if(bmp.getWidth()<bmp.getHeight()){
            int width=bmp.getWidth();
            int height=4*width/3;
            dif=bmp.getHeight()-height;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight()-dif+1, bmp.getConfig());


            for (int i = dif/2; i < bmp.getHeight()-dif/2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i-dif/2, bmp.getWidth(), 1);
            }
        }
        else{
            int height=bmp.getHeight();
            int width=3*height/4;
            dif=bmp.getWidth()-width;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth()-dif,bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth()-dif, dif/2, i, bmp.getWidth()-dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth()-dif, 0, i, bmp.getWidth()-dif, 1);
            }
        }
        im.setImageBitmap(operation);

    }

    public void defaultCrop57(View view){
        int dif;
        int[] pixels=new int[1920000];

        if(bmp.getWidth()<bmp.getHeight()){
            int width=bmp.getWidth();
            int height=7*width/5;
            dif=bmp.getHeight()-height;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight()-dif+1, bmp.getConfig());


            for (int i = dif/2; i < bmp.getHeight()-dif/2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i-dif/2, bmp.getWidth(), 1);
            }
        }
        else{
            int height=bmp.getHeight();
            int width=5*height/7;
            dif=bmp.getWidth()-width;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth()-dif,bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth()-dif, dif/2, i, bmp.getWidth()-dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth()-dif, 0, i, bmp.getWidth()-dif, 1);
            }
        }
        im.setImageBitmap(operation);

    }

    public void defaultCrop75(View view){
        int dif;
        int[] pixels=new int[1920000];

        if(bmp.getWidth()<bmp.getHeight()){
            int width=bmp.getWidth();
            int height=5*width/7;
            dif=bmp.getHeight()-height;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight()-dif+1, bmp.getConfig());


            for (int i = dif/2; i < bmp.getHeight()-dif/2; i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
                operation.setPixels(pixels, 0, bmp.getWidth(), 0, i-dif/2, bmp.getWidth(), 1);
            }
        }
        else{
            int height=bmp.getHeight();
            int width=7*height/5;
            dif=bmp.getWidth()-width;
            Log.d("dif", String.valueOf(dif));
            operation = Bitmap.createBitmap(bmp.getWidth()-dif,bmp.getHeight(), bmp.getConfig());


            for (int i = 0; i < bmp.getHeight(); i++) {
                bmp.getPixels(pixels, 0, bmp.getWidth()-dif, dif/2, i, bmp.getWidth()-dif, 1);
                operation.setPixels(pixels, 0, bmp.getWidth()-dif, 0, i, bmp.getWidth()-dif, 1);
            }
        }
        im.setImageBitmap(operation);

    }

    public void undoChange(View view){
        im.setImageBitmap(bmp);
    }



}
