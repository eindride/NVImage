package helloworld.example.com.nvimagefilters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {

    ImageView im;

    private Bitmap bmp;
    private Bitmap operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        im = (ImageView) findViewById(R.id.imageView);

        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        bmp = abmp.getBitmap();
    }

    public void filterNegative(View view) {
        int[] pixels=new int[1920000];

        operation = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());

        for (int i = 0; i < bmp.getHeight(); i++) {
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);

            for(int j=0;j<bmp.getWidth();j++){
                int r = 255-Color.red(pixels[j]);
                int g = 255-Color.green(pixels[j]);
                int b = 255-Color.blue(pixels[j]);
                pixels[j]=rgb(r,g,b);
            }

            operation.setPixels(pixels, 0, bmp.getWidth(), 0, i, bmp.getWidth(), 1);
        }

        /*for (int i = 0; i < bmp.getWidth(); i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                int p = bmp.getPixel(i, j);
                int r = 255-Color.red(p);
                int g = 255-Color.green(p);
                int b = 255-Color.blue(p);

                operation.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
            }
        }*/
        im.setImageBitmap(operation);
    }

    public void undoChange(View view){
        im.setImageBitmap(bmp);
    }
}

