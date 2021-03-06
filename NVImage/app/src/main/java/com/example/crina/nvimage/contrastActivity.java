package com.example.crina.nvimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;

public class contrastActivity extends AppCompatActivity {

    Bitmap original;
    ImageView imageView;

    float redValue = 1;
    float greenValue = 1;
    float blueValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast);

        imageView = (ImageView) findViewById(R.id.imageView3);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Uri path = (Uri) extras.get("imgurl");
            imageView.setImageURI(path);
            File file = new File(Environment.getExternalStorageDirectory(), "NVImage/TempFile.jpg");
            file.delete();
        }
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        original = abmp.getBitmap();

        final SeekBar seekBarContrast = (SeekBar) findViewById(R.id.seekBar);
        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBarContrast) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarContrast) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBarContrast, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

                Log.d("SOMETHING", String.valueOf((progress - 100) * (0.01f)));
                redValue = 1 + (progress - 100) * (0.01f);
                greenValue = 1 + (progress - 100) * (0.01f);
                blueValue = 1 + (progress - 100) * (0.01f);
                applyColorChange();
            }
        });
    }

    public void applyColorChange() {
        imageView.setImageBitmap(createFilteredBitmap(original, redValue, 0, 0, 0, 0,
                0, greenValue, 0, 0, 0,
                0, 0, blueValue, 0, 0,
                0, 0, 0, 1, 0));
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
        imageView = (ImageView) findViewById(R.id.imageView3);
        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        Bitmap bmp = abmp.getBitmap();

        saveTempFile(bmp);

        float[] colorArray = {redValue, greenValue, blueValue};
        Intent intent = new Intent(this, UploadedPictureEdit.class);
        intent.putExtra("whichContrast", colorArray);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void xButtonAction(View view) {
        finish();
    }

}
