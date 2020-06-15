package com.sohel.bixtoggle;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class ToggleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_toggle);
        this.toggle();
        finish();
    }

    /**
     * Get preferred action from preferences and toggle the action.
     */
    private void toggle() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE);
        // String toggleActions[] = getResources().getStringArray(R.array.toggle_actions);
        int toggleAction = prefs.getInt("toggle_action", 0); // in order of @strings/toggle_actions items.
        if (toggleAction == 0) this.toggleRingerMode();
        else if (toggleAction == 1) this.toggleMediaMute();
        else if (toggleAction == 2) this.startVoiceAssistant();
        else if (toggleAction == 3) this.toggleBrightness();
        else if (toggleAction == 4) this.toggleTorch();
        else this.toggleRingerMode();
    }

    /**
     * Start voice assistant.
     */
    private void startVoiceAssistant() {
        Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Toggle between normal and vibrate mode.
     */
    private void toggleRingerMode() {
        NotificationManager nm = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nm.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        } else {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            boolean isNormalMode = audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
            int newMode = isNormalMode ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_NORMAL;
            String msg = isNormalMode ? "Vibrate" : "Normal";
            audioManager.setRingerMode(newMode);
            Toast.makeText(ToggleActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Torch toggle.
     */
    private void toggleTorch() {
        final boolean hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            Toast.makeText(ToggleActivity.this, "No torch support", Toast.LENGTH_LONG).show();
            return;
        }

        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        if (!hasCameraPermission) {
            final int CAMERA_REQUEST = 123;
            ActivityCompat.requestPermissions(ToggleActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            return;
        }

        try {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0];

            SharedPreferences prefs = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE);
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE).edit();

            int isTorchLit = prefs.getInt("is_torch_lit", 0);

            if (isTorchLit == 0) {
                cameraManager.setTorchMode(cameraId, true);
                editor.putInt("is_torch_lit", 1).apply();
            } else {
                cameraManager.setTorchMode(cameraId, false);
                editor.putInt("is_torch_lit", 0).apply();
            }
        } catch (Exception e) {
            Toast.makeText(ToggleActivity.this, "Sorry, could not change torch state.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Mute or unmute media volume.
     */
    private void toggleMediaMute() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
            int volumeNow = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE).edit();
            editor.putInt("media_volume_previous", volumeNow);
            editor.apply();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
            Toast.makeText(ToggleActivity.this, "Muted", Toast.LENGTH_LONG).show();
        } else {
            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            SharedPreferences prefs = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE);
            int volumePrevious = prefs.getInt("media_volume_previous", max / 2);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumePrevious, AudioManager.FLAG_SHOW_UI);
            Toast.makeText(ToggleActivity.this, "Un-muted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Toggle display brightness between 10% and 100%
     */
    private void toggleBrightness() {
        if (Settings.System.canWrite(getApplicationContext())) {
            ContentResolver cResolver = this.getApplicationContext().getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            int brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
            int newBrightness = brightness < 27 ? 255 : 26; // 100% vs 10%
            String msg = brightness < 27 ? "High brightness" : "Low brightness";
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, newBrightness);
            Toast.makeText(ToggleActivity.this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
