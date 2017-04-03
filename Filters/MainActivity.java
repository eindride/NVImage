package helloworld.example.com.nvimagefilters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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

   /* public void filterNegativePixels(View view) {
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
       // im.setImageBitmap(operation);
 //   }*/

    public void filterNegative(View view) {
        im.setImageBitmap(createFilteredBitmap(bmp,-1,0,0,0,255,
                                                    0,-1,0,0,255,
                                                    0,0,-1,0,255,
                                                    0,0,0,1,0));
    }

    public void filterBlackandWhite(View view) {
        im.setImageBitmap(createFilteredBitmap(bmp,0.5f,0.5f,0.5f,0,0,
                0.5f,0.5f,0.5f,0,0,
                0.5f,0.5f,0.5f,0,0,
                0,0,0,1,0));
    }

    public void filterSepia(View view) {
        final ColorMatrix matrixA = new ColorMatrix();
        // making image B&W
        matrixA.setSaturation(0);

        final ColorMatrix matrixB = new ColorMatrix(new float[] {
                1.15f, 0, 0, 0, 0,
                0, .95f, 0, 0, 0,
                0, 0, .82f, 0, 0,
                0, 0, 0, 1, 0 });
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

        im.setImageBitmap(bitmap);
    }

    public void filterGreyScale(View view) {
        im.setImageBitmap(createFilteredBitmap(bmp,0.3f,0.59f,0.11f,0,0,
                0.3f,0.59f,0.11f,0,0,
                0.3f,0.59f,0.11f,0,0,
                0,0,0,1,0));
    }

    private Bitmap createFilteredBitmap(Bitmap src,float a,float b,float c,float d,float e,
                                        float f,float g,float h,float i,float j,
                                        float k,float l,float m,float n,float o,
                                        float p,float q,float r,float s,float t) {
        ColorMatrix colorMatrix =
                new ColorMatrix(new float[] {
                         a, b, c, d, e,
                        f, g, h, i, j,
                        k, l, m, n, o,
                        p, q, r, s, t });

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(src, 0, 0, paint);

        return bitmap;
    }

    public void undoChange(View view){
        im.setImageBitmap(bmp);
    }
}

