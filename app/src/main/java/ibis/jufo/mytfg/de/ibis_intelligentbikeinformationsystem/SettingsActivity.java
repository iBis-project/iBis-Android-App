package ibis.jufo.mytfg.de.ibis_intelligentbikeinformationsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;


public class SettingsActivity extends ActionBarActivity {

    //Variables declaration
    public boolean CollectData = false;
    public float FloatDistStartDest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void onCheckboxClicked(View view) {
        // Check if the CheckBox is checked
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.CBCollectData:
                CollectData = checked;
                break;
        }
    }

    //called when save Button is clicked
    public void saveSettings(View view) {
        boolean exception = false;
        //read text from EditText and convert to String
        EditText editDistance = (EditText) findViewById(R.id.enter_distance);
        String StrEditText = editDistance.getText().toString();
        //try to convert String to Float
        try {
            FloatDistStartDest = Float.parseFloat(StrEditText);
        } catch (java.lang.NumberFormatException e) {
            exception = true;
            openAlert(StrEditText);
        }

        if (!exception) {
            Intent intent = new Intent(this, ShowDataActivity.class);
            startActivity(intent);
        }
    }

    private void openAlert(String StrEditText) {
        //set up a new alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("Bitte geben sie eine Zahl ein!");
        alertDialogBuilder.setMessage("\""+StrEditText+"\""+" ist keine Zahl! ");

        //create the OK Button and onClickListener
        alertDialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            //close dialog when clicked
            public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            }
        });
        //create and show alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_show_data:
                Intent intent2 = new Intent(this, ShowDataActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
