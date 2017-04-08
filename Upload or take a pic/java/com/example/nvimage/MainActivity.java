package com.example.nvimage;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.FileDescriptor;
import java.io.IOException;


public class MainActivity extends Activity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button buton1 = (Button) this.findViewById(R.id.camera);
        buton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        Button buton2 = (Button) this.findViewById(R.id.upload_button);
        buton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), SELECT_PICTURE);
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode ) {
            case SELECT_PICTURE:
                Uri selectedImage = data.getData();
                Intent intent2 = new Intent(this, UploadedPic.class);
                intent2.putExtra("imgurl", selectedImage);
                startActivity(intent2);
                break;
            case CAMERA_REQUEST:
                //Bitmap photo2 = (Bitmap) data.getExtras().get("data");
                Uri uri = data.getData();
                Intent intent= new Intent(this,UploadedPic.class);
                intent.putExtra("imgurl", uri );
                startActivity(intent);
                break;
        }


    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }







}





