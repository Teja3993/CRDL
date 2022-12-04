package com.tejakarri.crdl;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    ImageView imgCamera;
    Button btnCamera, uploadbtn;
    TextView Tv_uri;

    ActivityResultLauncher<String> mGetContent, mGetContent2;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgCamera = findViewById(R.id.ImgCamera);
        btnCamera = findViewById(R.id.BtnCamera);
        Tv_uri = findViewById(R.id.tv_uri);
        uploadbtn = findViewById(R.id.send_to_server);

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Uploading to server", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtaining image from Gallery
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imgCamera.setImageURI(result);
            }
        });

        // Now for using Camera










        Tv_uri.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent take_Picture_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(take_Picture_Intent);
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bundle extras = result.getData().getExtras();
                Uri image_uri;
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                WeakReference<Bitmap> result1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap,
                        imageBitmap.getHeight(), imageBitmap.getWidth(), false).copy(
                        Bitmap.Config.RGB_565, true));

                Bitmap bm = result1.get();
                image_uri = saveImage(  bm,MainActivity.this);

                Intent intent =new Intent(MainActivity.this,CropperActivity.class);
                intent.putExtra("DATA",image_uri.toString());
                startActivityForResult(intent,101);



//                imgCamera.setImageURI(image_uri);
//               Tv_uri.setText(""+image_uri);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1 && requestCode == 101){
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;
            if(result!=null){
                resultUri = Uri.parse(result);
            }

            imgCamera.setImageURI(resultUri);
            Tv_uri.setText(""+resultUri);
        }
    }




















    private Uri saveImage(Bitmap image, Context context) {
         File imagesFolder = new File(context.getFilesDir(), "images");
        Uri uri = null;
        try {
             imagesFolder.mkdirs();
            File file = new File(imagesFolder,"capturedImage.jpg");
            FileOutputStream stream = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG,100,stream);

           stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.tejakarri.crdl"+".provider", file);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return uri;
    }
}