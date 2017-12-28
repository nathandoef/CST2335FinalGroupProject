package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity used when the device is in portrait mode. Sets up the c_EnterFuelDetailsFragment
 * to display using the entire screen so that users can create a new fuel details entry.
 * @author 		Nathan Doef
 * @version		4
 */
public class c_AddFuelDetailsActivity extends Activity {

    /**
     * Sets up the button text and title for the c_EnterFuelDetailsFragment that will be loaded
     * into this activity's frame layout.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_add_fuel_details);

        Bundle fragmentDetails = new Bundle();
        fragmentDetails.putString("btnText", getResources().getString(R.string.c_BtnAdd));
        fragmentDetails.putString("fragmentTitle", getResources().getString(R.string.c_AddDetailsTitle));

        c_EnterFuelDetailsFragment loadedFragment = new c_EnterFuelDetailsFragment();
        loadedFragment.setArguments(fragmentDetails);

        getFragmentManager().beginTransaction()
                .add(R.id.flAddDetails, loadedFragment).commit();
    }

    /**
     * Called by the loaded c_EnterFuelDetails fragment in order to finish the
     * activity and return to the c_CarTrackerActivity where all the database functionality and
     * CRUD operations are located.

     * @param fuelDetails the new fuel details entry that will be created
     */
    public void addFuelDetail(Bundle fuelDetails){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("fuelDetails", fuelDetails);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
