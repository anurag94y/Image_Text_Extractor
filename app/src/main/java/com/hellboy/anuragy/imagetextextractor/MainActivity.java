package com.hellboy.anuragy.imagetextextractor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hellboy.anuragy.imagetextextractor.Utils.IOHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    public static final int PICK_IMAGE = 1993;
    private ImageView imageView;
    private Context context;
    private IOHelper ioHelper;
    private String photoPath;
    private int deviceHeight;
    private int deviceWidth;
    private LinearLayout mainButtonContainer;
    Uri outPutfileUri;
    int permissionFlag = 0;
    public static final int MY_PERMISSIONS_REQUEST = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        ioHelper = new IOHelper();
        Button photoButton = (Button) this.findViewById(R.id.ClickButton);
        Button uploadButton = (Button) this.findViewById(R.id.UploadButton);
        this.imageView = (ImageView) this.findViewById(R.id.takeImage);
        mainButtonContainer = (LinearLayout) this.findViewById(R.id.MainButtonContainer);
        getDeviceHeightWidth();
        imageView.getLayoutParams().height = (int) (deviceHeight * 0.8);
        mainButtonContainer.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.9)));
        System.out.println("Remaining Height !! = " + (deviceHeight - ((int) (deviceHeight * 0.9))));
        System.out.println("Height !!! = " + deviceHeight);
        System.out.println("Height !!! = " + deviceHeight);
        photoButton.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.92)));
        photoButton.getLayoutParams().width = (int) (deviceWidth * 0.48);
        uploadButton.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.92)));
        uploadButton.getLayoutParams().width = (int) (deviceWidth * 0.48);

        //onDestroy();
        checkForStoragePermission();


        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (permissionFlag == 0) {
                    onDestroy();
                }
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                outPutfileUri = Uri.fromFile(file);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                //camera.takePicture(null, null, new PhotoHandler(context));
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image*//*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image*//*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                startActivityForResult(chooserIntent, PICK_IMAGE);*/
                if (permissionFlag < 2) {
                    onStop();
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

    }


    private void callOCRActivity() {
        Intent ocrIntent = new Intent(context, OCRActivity.class);
        Bundle extras = new Bundle();
        if (photoPath == null) {
            imageView.buildDrawingCache();
            try {
                photoPath = ioHelper.SaveImage(imageView.getDrawingCache());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        extras.putString("imagebitmap", photoPath);
        ocrIntent.putExtras(extras);
        startActivity(ocrIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    imageView.setImageDrawable(d);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                photoPath = null;
                try {
                    photoPath = ioHelper.SaveImage(bitmap);
                    callOCRActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_IMAGE) {
                Bitmap bitmap = null;
                try {
                    Uri selectedImage = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    imageView.setImageDrawable(d);
                    photoPath = IOHelper.getRealPathFromUri(context, selectedImage);
                    System.out.println("!!!!! photo path : " + photoPath);
                    callOCRActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getDeviceHeightWidth() {
        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        deviceWidth = mDisplay.getWidth();
        deviceHeight = mDisplay.getHeight();
    }


    public void checkForStoragePermission() {

        Vector<String> requestPermissionList = new Vector<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissionList.add(Manifest.permission.CAMERA);


        String[] requestPermissionArray = requestPermissionList.toArray(new String[requestPermissionList.size()]);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, requestPermissionArray, MY_PERMISSIONS_REQUEST);
        } else
            permissionFlag++;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionFlag++;

                } else {

                }
                return;
            }
        }
    }

}