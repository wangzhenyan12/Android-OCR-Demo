package com.jason.ocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.jason.ocr.camera.EasyCamera;
import com.jason.ocr.camera.util.DisplayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnCapture;
    private ImageView ivImage;
    private TextView resultView;
    private int screenWidth;
    private float ratio = 0.5f;

    private TessBaseAPI tessBaseApi;
    private static final String lang = "eng";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/";
    private static final String TESSDATA = "tessdata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivImage = (ImageView) findViewById(R.id.iv_image);
        btnCapture = (Button) findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SAMPLE_CROPPED_IMAGE_NAME = "cropImage_"+System.currentTimeMillis()+".png";
                Uri destination = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));
                EasyCamera.create(destination)
                        .withViewRatio(ratio)
                        .withMarginCameraEdge(50,50)
                        .start(MainActivity.this);
            }
        });

        screenWidth = (int) DisplayUtils.getScreenWidth(this);
        ivImage.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, (int) (screenWidth * ratio)));
        ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        resultView = (TextView) findViewById(R.id.result);

        prepareTesseract();
    }

    private void prepareTesseract() {
        try {
            prepareDirectory(DATA_PATH + TESSDATA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyTessDataFiles(TESSDATA);
    }

    private void prepareDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "ERROR: Creation of directory " + path + " failed," +
                        " check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i(TAG, "Created directory " + path);
        }
    }

    private void copyTessDataFiles(String path) {
        try {
            String fileList[] = getAssets().list(path);
            for (String fileName : fileList) {
                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = DATA_PATH + path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(path + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.d(TAG, "Copied " + fileName + "to tessdata");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to copy files to tessdata " + e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == EasyCamera.REQUEST_CAPTURE) {
                Uri resultUri = EasyCamera.getOutput(data);
                int width = EasyCamera.getImageWidth(data);
                int height = EasyCamera.getImageHeight(data);
                ivImage.setImageURI(resultUri);

                Log.i(TAG,"imageWidth:"+width);
                Log.i(TAG,"imageHeight:"+height);

                startOCR(resultUri);
            }
        }
    }

    private void startOCR(Uri imgUri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 1 - means max size. 4 - means maxsize/4 size.
            // Don't use value <4, because you need more memory in the heap to store your data.
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(imgUri.getPath(), options);

            String result = extractText(bitmap);
            resultView.setText(result);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private String extractText(Bitmap bitmap) {
        try {
            tessBaseApi = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (tessBaseApi == null) {
                Log.e(TAG, "TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }

        tessBaseApi.init(DATA_PATH, lang);

        tessBaseApi.setImage(bitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, "Error in recognizing text.");
        }
        tessBaseApi.end();
        return extractedText;
    }
}
