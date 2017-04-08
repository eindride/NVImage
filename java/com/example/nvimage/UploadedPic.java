package com.example.nvimage;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.example.nvimage.R.layout.activity_uploaded_pic;
import static java.io.File.separator;

public class UploadedPic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_uploaded_pic);


        ImageView iv_photo=(ImageView)findViewById(R.id.imageView);
        Bundle extras= getIntent().getExtras();
        if(extras!=null)
        {
            Uri path = (Uri) extras.get("imgurl");
            iv_photo.setImageURI(path);
        }

        Button buton1 = (Button) this.findViewById(R.id.save);
        buton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ImageView imageview=(ImageView)findViewById(R.id.imageView);
                imageview.buildDrawingCache();
                Bitmap bm = imageview.getDrawingCache();

                SaveImage(bm);




            }
        });

    }

    private static void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/NVImage");
        myDir.mkdirs();

        String fname = "Image-" +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
