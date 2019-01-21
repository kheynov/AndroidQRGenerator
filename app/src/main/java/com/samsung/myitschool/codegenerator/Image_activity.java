package com.samsung.myitschool.codegenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Image_activity extends AppCompatActivity {
    private String infoTAG = "APP_STATE";
    private String folderToSave;
    private String message;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_activity);

        if (BuildConfig.DEBUG) {
            Log.v(infoTAG, "Image Activity started");
        }
        TextView textView = findViewById(R.id.text);

        folderToSave = getApplicationContext().getCacheDir().toString();
        ImageView imageView = findViewById(R.id.imageView2);
        Intent intent = getIntent();
        message = intent.getStringExtra("Text");


        try {
            bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("myBitmap"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bitmap);
        if (BuildConfig.DEBUG) {
            Log.v(infoTAG, "Code show complete");
        }
        textView.setText(message);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.qr_action);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.save:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (BuildConfig.DEBUG) {
                                    Log.v(infoTAG, "Picture Saved");
                                }
                                OutputStream fout;
                                try {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        //динамическое получение прав на WRITE_EXTERNAL_STORAGE
                                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                            Log.d("Permission", "Permission is granted");
                                            File file = new File(folderToSave, "QR_" + message);
                                            fout = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fout);
                                            fout.flush();
                                            fout.close();

                                            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                                            if (BuildConfig.DEBUG) {
                                                Log.i("INFO_TAG", "File saved in: " + file.getAbsolutePath());
                                            }
                                        } else {
                                            Log.d("Permission", "Permission is revoked");
                                            //запрашиваем разрешение
                                            ActivityCompat.requestPermissions(Image_activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        }
                                    } else {
                                        File file = new File(folderToSave, "QR_" + message);
                                        fout = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, fout);
                                        fout.flush();
                                        fout.close();

                                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                                        if (BuildConfig.DEBUG) {
                                            Log.i("INFO_TAG", "File saved in: " + file.getAbsolutePath());
                                        }
                                    }

                                } catch (IOException e) {
                                    Log.e("ERROR", e.getMessage());
                                }
                            }
                        }).start();
                        Toast.makeText(getApplicationContext(), R.string.picture_saved_toast, Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                if (BuildConfig.DEBUG) {
                    Log.i("INFO", "onDismiss");
                }
            }
        });
        popupMenu.show();
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

    public void Back_to_menu(View view) {
        if (BuildConfig.DEBUG) {
            Log.v(infoTAG, "Image Activity stopped");
        }
        finish();
    }
}
