package com.sohel.bixtoggle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class ToggleActivity extends Activity {

    private static boolean flashIsOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_toggle);
        this.toggle();
        finish();
    }

    private void toggle() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE);
        int primary_action = prefs.getInt("primary_action", 0);
//        String primary_options[] = getResources().getStringArray(R.array.primary_options);
        if (primary_action == 0) this.toggleRingerMode();
        else if (primary_action == 1) this.toggleMediaVolume();
        else this.toggleRingerMode();
//        this.toggleFlashlight();
    }

    private void toggleRingerMode() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        boolean isNormal = audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
        int mode = isNormal ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_NORMAL;
        String msg = isNormal ? "Switched to Vibrate Mode" : "Switched to Normal Mode";
        audioManager.setRingerMode(mode);
        Toast.makeText(ToggleActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void toggleFlashlight() {
        final boolean hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        boolean flashPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (!hasFlash || !flashPerm) return;
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, !flashIsOn);
            flashIsOn = !flashIsOn;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleMediaVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
            Toast.makeText(ToggleActivity.this, "Muted", Toast.LENGTH_LONG).show();
        } else {
            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max / 2, AudioManager.FLAG_PLAY_SOUND);
            Toast.makeText(ToggleActivity.this, "Unmuted", Toast.LENGTH_LONG).show();
        }
    }

}
