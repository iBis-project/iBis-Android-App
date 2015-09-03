package de.mytfg.jufo.ibis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends ActionBarActivity {

    //create instance of IbisApplication class
    IbisApplication mGlobalVariable;
    //Timer for updating the map
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            updateCBCollectData();
            timerHandler.postDelayed(this, 500);
        }
    };
    //Variables declaration
    private float FloatTextSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //initialize global variable class
        mGlobalVariable = (IbisApplication) getApplicationContext();

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //get variables and set to global class
        mGlobalVariable.setCollect_data(settings.getBoolean("CollectData", false));
        mGlobalVariable.setShowLocationOverlay(settings.getBoolean("showLocationOverlay", true));
        mGlobalVariable.setShowCompassOverlay(settings.getBoolean("showCompassOverlay", true));
        mGlobalVariable.setShowScaleBarOverlay(settings.getBoolean("showScaleBarOverlay", true));
        FloatTextSize = settings.getFloat("FloatTextSize", 8);
        //set check boxes
        final CheckBox CBcollectData = (CheckBox) findViewById(R.id.CBCollectData);
        CBcollectData.setChecked(mGlobalVariable.isCollect_data());
        final CheckBox cb_show_compassOverlay = (CheckBox) findViewById(R.id.cb_show_compassOverlay);
        cb_show_compassOverlay.setChecked(mGlobalVariable.isShow_compassOverlay());
        final CheckBox cb_show_locationOverlay = (CheckBox) findViewById(R.id.cb_show_locationOverlay);
        cb_show_locationOverlay.setChecked(mGlobalVariable.isShow_locationOverlay());
        final CheckBox cb_show_scaleBarOverlay = (CheckBox) findViewById(R.id.cb_show_scaleBarOverlay);
        cb_show_scaleBarOverlay.setChecked(mGlobalVariable.isShow_scaleBarOverlay());
        //set default text
        EditText enter_text_size = (EditText) findViewById(R.id.enter_text_size);
        enter_text_size.setText(Float.toString(FloatTextSize));
        startUIUpdates();
    }

    private void startUIUpdates() {
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //saving settings
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //creating a editor and add variables
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("CollectData", mGlobalVariable.isCollect_data());
        editor.putBoolean("showCompassOverlay", mGlobalVariable.isShow_compassOverlay());
        editor.putBoolean("showLocationOverlay", mGlobalVariable.isShow_locationOverlay());
        editor.putBoolean("showScaleBarOverlay", mGlobalVariable.isShow_scaleBarOverlay());
        editor.putFloat("FloatTextSize", FloatTextSize);
        // Commit the edits!
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_MainActivity:
                Intent intent_main = new Intent(this, MainActivity.class);
                startActivity(intent_main);
                return true;
            case R.id.action_show_data_activity:
                Intent intent_showData = new Intent(this, ShowDataActivity.class);
                startActivity(intent_showData);
                return true;
            case R.id.action_RoutingActivity:
                Intent intent_routing = new Intent(this, RoutingActivity.class);
                startActivity(intent_routing);
                return true;
            case R.id.action_info:
                Intent intent_info = new Intent(this, InfoActivity.class);
                startActivity(intent_info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateCBCollectData() {
        final CheckBox CBcollectData = (CheckBox) findViewById(R.id.CBCollectData);
        CBcollectData.setChecked(mGlobalVariable.isCollect_data());
    }

    public void onCheckboxClicked(View view) {
        //check if the CheckBox is checked
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.CBCollectData:
                mGlobalVariable.setCollect_data(checked);
                break;
            case R.id.cb_show_locationOverlay:
                mGlobalVariable.setShowLocationOverlay(checked);
                break;
            case R.id.cb_show_compassOverlay:
                mGlobalVariable.setShowCompassOverlay(checked);
                break;
            case R.id.cb_show_scaleBarOverlay:
                mGlobalVariable.setShowScaleBarOverlay(checked);
                break;
        }
    }

    //called when save Button is clicked
    public void saveSettings(View view) {
        EditText enter_text_size = (EditText) findViewById(R.id.enter_text_size);
        String strEnterTxtSz = enter_text_size.getText().toString();
        //try to convert String to Float
        try {
            FloatTextSize = Float.parseFloat(strEnterTxtSz);
        } catch (java.lang.NumberFormatException e) {
            openAlert(strEnterTxtSz);
        }
        mGlobalVariable.setSettingVars(FloatTextSize);
        mGlobalVariable.setChanged_settings(true);
    }

    private void openAlert(String StrEditText) {
        //set up a new alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("Bitte geben sie eine Zahl ein!");
        alertDialogBuilder.setMessage("\"" + StrEditText + "\"" + " ist keine Zahl! ");

        //create the OK Button and onClickListener
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            //close dialog when clicked
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        //create and show alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onClickFindRoute(View view) {
        Intent intent = new Intent(this, RoutingActivity.class);
        startActivity(intent);
    }
}

