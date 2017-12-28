package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Main activity for the application
 * @author 		Nathan Doef
 * @version		2
 */
public class m_MainActivity extends AppCompatActivity {

    /**
     * Loads XML and sets up the activity's Toolbar
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_activity_main);

        final Toolbar m_toolbar = findViewById(R.id.m_toolbar);
        setSupportActionBar(m_toolbar);
    }

    /**
     * Inflates the menu from the XML resource file
     * @param menu to load into the Toolbar
     * @return if the Toolbar was inflated successfully
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.m_menu, menu);
        return true;
    }

    /**
     * Handles the functionality when a MenuItem is selected from the Activity's Toolbar
     * @param menuItem
     * @return if the MenuItem event was handled successfully
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch(menuItem.getItemId()){

            // send to Activity Tracker activity
            case R.id.menu_exercise:
                startActivity(new Intent(m_MainActivity.this, a_ActivityTrackerActivity.class));
                break;

            // send to Nutrition Tracker activty
            case R.id.menu_food:
                startActivity(new Intent(m_MainActivity.this, n_NutritionTrackerActivity.class));
                break;

            // send to Thermostat Activity
            case R.id.menu_thermostat:
                startActivity(new Intent(m_MainActivity.this, t_ThermostatProgramActivity.class));
                break;

            // send to Car Tracker activity
            case R.id.menu_automobile:
                startActivity(new Intent(m_MainActivity.this, c_CarTrackerActivity.class));
                break;
        }
        return true;
    }
}