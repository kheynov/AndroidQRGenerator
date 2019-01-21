package com.samsung.myitschool.codegenerator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button generate;

    private Button getType;
    private EditText editText;
    private String dataToEncode;
    private ProgressBar pb;
    private String type = "none";
    private String infoTAG = "APP_STATE";
    private static long back_pressed;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG) {
            Log.v(infoTAG, "Main activity started");
        }
        generate = findViewById(R.id.generate);
        getType = findViewById(R.id.choose_type);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editText = findViewById(R.id.edit_text);
        pb = findViewById(R.id.progressBar);
        pb.setVisibility(ProgressBar.INVISIBLE);
        type = "qr";
        getType.setText(R.string.qr_on_button);
        getType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });//Обработчик кнопки запуска приложения
        generate.setOnClickListener(new View.OnClickListener() { //Обработчик кнопки запуска приложения
            @Override
            public void onClick(View view) {
                dataToEncode = editText.getText().toString();
                if (dataToEncode.equals("") || dataToEncode == null) {
                    Toast.makeText(getApplicationContext(), R.string.empty_editText, Toast.LENGTH_SHORT).show();
                }else if(dataToEncode.length()>=300){
                    Toast.makeText(getApplicationContext(), R.string.too_length, Toast.LENGTH_SHORT).show();
                }
                else {
                    GenerateQR GenerateTask = new GenerateQR();
                    switch (type) {
                        case "none":
                            Toast.makeText(MainActivity.this, R.string.type_non_selected, Toast.LENGTH_SHORT).show();
                            break;
                        case "qr":
                            GenerateTask.execute(dataToEncode, "qr");
                            break;
                        case "bar":
                            GenerateTask.execute(dataToEncode, "bar");
                            break;
                    }
                }
            }
        });

    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.CENTER);
        popupMenu.inflate(R.menu.typeofcode_popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_bar:
                        type = "bar";
                        if (BuildConfig.DEBUG) {
                            Log.v(infoTAG, "BAR code type chosen");
                        }
                        getType.setText(R.string.bar_on_button);
                        return true;
                    case R.id.menu_qr:
                        type = "qr";
                        if (BuildConfig.DEBUG) {
                            Log.v(infoTAG, "QR code type chosen");
                        }
                        getType.setText(R.string.qr_on_button);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    public void Flush(View view) {
        editText.setText("");
    }

    @SuppressLint("StaticFieldLeak")
    class GenerateQR extends AsyncTask<String, Integer, Bitmap> {
        BitMatrix bitMatrix;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (BuildConfig.DEBUG) {
                Log.v(infoTAG, "Generating Started");
            }
            generate.setClickable(false);
            generate.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String[] objects) {
            BarcodeFormat format = null;
            if (Objects.equals(objects[1], "qr")) {
                format = BarcodeFormat.QR_CODE;
            } else if (Objects.equals(objects[1], "bar")) {
                format = BarcodeFormat.CODE_128;
            }
            try {

                if (format != null) {
                    if(prefs.getBoolean("compress", true)){
                        bitMatrix = new MultiFormatWriter().encode(objects[0], format, 100, 100);
                    }else{
                        if(prefs.getString("code_resolution", "800*800").equals("800*800")){
                            bitMatrix = new MultiFormatWriter().encode(objects[0], format, 800, 800);
                        }else if(prefs.getString("code_resolution", "800*800").equals("100*100")){
                            bitMatrix = new MultiFormatWriter().encode(objects[0], format, 100, 100);
                        }else if(prefs.getString("code_resolution", "800*800").equals("200*200")){
                            bitMatrix = new MultiFormatWriter().encode(objects[0], format, 200, 200);
                        }else if(prefs.getString("code_resolution", "800*800").equals("400*400")){
                            bitMatrix = new MultiFormatWriter().encode(objects[0], format, 400, 400);
                        }else{
                            bitMatrix = new MultiFormatWriter().encode(objects[0], format, 800, 800);
                        }
                    }
                }

            } catch (IllegalArgumentException e) {
                return null;
            } catch (WriterException e) {
                e.printStackTrace();
            }
            int BitmapWidth = bitMatrix.getWidth();
            int BitmapHeight = bitMatrix.getHeight();
            int[] pixels = new int[BitmapWidth * BitmapHeight];
            for (int i = 0; i < BitmapHeight; i++) {
                int offset = i * BitmapWidth;
                for (int j = 0; j < BitmapWidth; j++) {
                    pixels[offset + j] = bitMatrix.get(j, i) ? getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(BitmapWidth, BitmapHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, BitmapWidth, 0, 0, BitmapWidth, BitmapHeight);
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (BuildConfig.DEBUG) {
                Log.v(infoTAG, "Generation finished");
            }
            pb.setVisibility(ProgressBar.INVISIBLE);
            generate.setClickable(true);
            generate.setBackgroundColor(getResources().getColor(R.color.main_button));
            Intent intent = new Intent(MainActivity.this, Image_activity.class);
            String filename = createImageFromBitmap(bitmap);
            intent.putExtra("bitmap", filename);
            intent.putExtra("Text", dataToEncode);
            if (BuildConfig.DEBUG) {
                Log.v(infoTAG, "Image Activity intended");
            }
            startActivity(intent);
        }

        String createImageFromBitmap(Bitmap bitmap) {
            String fileName = "myBitmap";
            Bitmap bitmapScaled;
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (Objects.equals(type, "qr")) {
                    if(prefs.getString("code_resolution", "800*800").equals("100*100")){
                        if(prefs.getBoolean("compress", true)){
                            bitmapScaled = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                            bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }
                        else{
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }
                    }
                    else if(prefs.getString("code_resolution", "800*800").equals("200*200")){
                        if(prefs.getBoolean("compress", true)){
                            bitmapScaled = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                            bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }else{
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }
                    }
                    else if(prefs.getString("code_resolution", "800*800").equals("400*400")){
                        if(prefs.getBoolean("compress", true)){
                            bitmapScaled = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                            bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }else{
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }
                    }
                    else if(prefs.getString("code_resolution", "800*800").equals("800*800")){
                        if(prefs.getBoolean("compress", true)){
                            bitmapScaled = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
                            bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }else{
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        }
                    }

                }else {
                    bitmapScaled = Bitmap.createScaledBitmap(bitmap, 800, 600, true);
                    bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                }

                FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                fo.write(bytes.toByteArray());

                fo.close();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.e("IMAGE_FROM_BITMAP", e.getMessage());
                }
                fileName = null;
            }
            return fileName;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (BuildConfig.DEBUG) {
            Log.v(infoTAG, "OptionsMenuItemSelected");
        }
        switch (item.getItemId()) {
            case R.id.action_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), PreferenceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), R.string.quit, Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}