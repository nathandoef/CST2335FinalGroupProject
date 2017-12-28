package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity used when the device is in portrait mode. Sets up the c_EnterFuelDetailsFragment
 * to display using the entire screen so that users can edit an existing fuel details entry.
 *
 * @author 		Nathan Doef
 * @version		4
 */
public class c_EditFuelDetailsActivity extends Activity {

    /**
     * Sets up the button text and title for the c_EnterFuelDetailsFragment that will be loaded
     * into this activity's frame layout.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_edit_fuel_details);

        Bundle fragmentDetails = getIntent().getExtras();
        if (fragmentDetails != null){

            // add title of the fragment and button text
            fragmentDetails.putString("btnText", getResources().getString(R.string.c_BtnSaveDetails));
            fragmentDetails.putString("fragmentTitle", getResources().getString(R.string.c_EditDetailsTitle));

            c_EnterFuelDetailsFragment loadedFragment = new c_EnterFuelDetailsFragment();
            loadedFragment.setArguments(fragmentDetails);

            getFragmentManager().beginTransaction().
                    replace(R.id.flEditDetails, loadedFragment).commit();
        }
    }

    /**
     * Called by the loaded c_EnterFuelDetails fragment in order to finish the
     * activity and return to the c_CarTrackerActivity where all the database functionality and
     * CRUD operations are located.

     * @param fuelDetails the existing fuel details entry that will be updated
     */
    public void updateFuelDetail(Bundle fuelDetails){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("fuelDetails", fuelDetails);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
