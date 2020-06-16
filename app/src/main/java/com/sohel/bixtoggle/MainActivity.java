package com.sohel.bixtoggle;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE);
        int toggleAction = prefs.getInt("toggle_action", 0);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(toggleAction);

        // set help link
        TextView helpLink = (TextView) findViewById(R.id.textViewHelp);
        helpLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onClickSaveButton(View v) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_prefs), MODE_PRIVATE).edit();
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        int selection = spinner.getSelectedItemPosition();
        editor.putInt("toggle_action", selection).apply();
        editor.putInt("first_run_done", 1).apply();
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
    }
}
