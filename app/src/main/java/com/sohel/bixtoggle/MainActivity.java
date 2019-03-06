package com.sohel.bixtoggle;

import android.Manifest;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE);
        int primary_option = prefs.getInt("primary_action", 0);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        spinner.setSelection(primary_option);
        // final TextView textView = (TextView) findViewById(R.id.textView);
        // textView.setText(primary_option);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(ToggleActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }*/

    public void onClickSaveButton(View v) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE).edit();
        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        int selection = spinner.getSelectedItemPosition();
        editor.putInt("primary_action", selection);
        editor.apply();
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
    }
}
