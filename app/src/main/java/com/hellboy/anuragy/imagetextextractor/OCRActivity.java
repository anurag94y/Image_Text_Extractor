package com.hellboy.anuragy.imagetextextractor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hellboy.anuragy.imagetextextractor.Utils.IOHelper;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCRActivity extends Activity {

    Bitmap image;
    private TessBaseAPI mTess;
    String datapath = "";
    ImageView imageView;
    Button ocrButton;
    IOHelper ioHelper;
    String OCRresult = "Default";
    Context context;
    private int deviceWidth;
    private int deviceHeight;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        ioHelper = new IOHelper();
        context = this.getApplicationContext();
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = findViewById(R.id.ocrProgressBar);
        progressBar.setVisibility(View.GONE);
        datapath = getFilesDir()+ "/tesseract/";
        getDeviceHeightWidth();
        imageView.getLayoutParams().height = (int) (deviceHeight * 0.87);
        LinearLayout ocrButtonContainer = findViewById(R.id.OCRButtonContainer);
        ocrButtonContainer.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.9)));
        ocrButton = (Button) findViewById(R.id.OCRbutton);
        ocrButton.getLayoutParams().height = (deviceHeight - ((int) (deviceHeight * 0.9)));


        //init image
        image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);

        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            image = ioHelper.OpenImage(extras.getString("imagebitmap"));
            System.out.println("Image!!!! " + image);
            imageView.setImageBitmap(image);

        }
        System.out.println("Parsed Data = " + OCRresult);
    }


    public void processImage(View view) {
        //imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new MyTask().execute(0);
    }

    private void checkFile(File dir) {
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getDeviceHeightWidth() {
        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        deviceWidth = mDisplay.getWidth();
        deviceHeight = mDisplay.getHeight();
    }

    class MyTask extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                String language = "eng";
                mTess = new TessBaseAPI();
                checkFile(new File(datapath + "tessdata/"));
                mTess.init(datapath, language);
                mTess.setImage(image);
                OCRresult = mTess.getUTF8Text();
                System.out.println("OCR result !!! : " + OCRresult);
                return OCRresult;
            } catch (Exception e) {
                e.printStackTrace();
                return "Image is not Good";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(context, ShowDataActivity.class);
            Bundle extras = new Bundle();
            extras.putString("response", OCRresult);
            intent.putExtras(extras);
            startActivity(intent);
        }

        @Override
        protected void onPreExecute() {

        }

    }

}