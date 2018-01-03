package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The Car Activity's main page
 * @author 		Nathan Doef
 * @version		4
 */
public class c_CarTrackerActivity extends AppCompatActivity {

    /** Format displayed in the DatePicker and ListView of fuel details */
    public static final DateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

    /** Request codes for operations */
    public static final int ADD_DETAILS_REQUEST = 1;
    public static final int EDIT_DETAILS_REQUEST = 2;

    /** Constants used to determine the value to return for statistics calculations */
    private static final String AVERAGE = "average";
    private static final String TOTAL = "total";

    /** The c_EnterFuelDetailsFragment loaded in this activity */
    private c_EnterFuelDetailsFragment loadedFragment = null;

    /** Determines if the activity is in landscape (true) or portrait (false) */
    private boolean frameLayoutExists;

    /** This activity's Toolbar */
    private Toolbar c_Toolbar;

    /** Parent Layout used to display SnackBar and Alert notifications */
    private View parentLayout;

    /** Custom buttons used to add a purchase and view statistics */
    private LinearLayout btnAddPurchase;
    private LinearLayout btnViewFuelStats;

    /** Controls used to display progress bar and percent loaded during the AsyncTask */
    private GridLayout glLoading;
    private ProgressBar pbLoadFuelDetails;
    private TextView tvLoadingPercentage;

    /** Displays all fuel details entries */
    private ListView lvPurchaseHistory;

    /** Stores all the fuel detail entries */
    private ArrayList<c_FuelDetails> cFuelDetailsList;

    /** Connects lvPurchaseHistory to cFuelDetailsList */
    private FuelDetailsAdapter adapter;

    /** Database objects used in the activity */
    private m_GlobalDatabaseHelper carDbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    /**
     * Sets up the database elements, finds the XML controls, sets up the event listeners,
     * connects cFuelDetailsList to lvPurchaseHistory and executes an AsyncTask to load
     * all fuel details entries from the database.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_car);

        carDbHelper = new m_GlobalDatabaseHelper(c_CarTrackerActivity.this);
        db = carDbHelper.getWritableDatabase();

        findControls();
        setUpListeners();

        cFuelDetailsList = new ArrayList<>();
        adapter = new FuelDetailsAdapter(this);
        lvPurchaseHistory.setAdapter(adapter);

        new DataBaseQuery().execute();
    }

    /**
     * Creates the activity's Toolbar
     * @param menu resource to be inflated
     * @return if the menu was successfully inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.c_menu, menu);
        return true;
    }

    /**
     * Closes the database resources when the application is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        carDbHelper.close();
    }

    /**
     * Removes fragment that may have been loaded before the device's orientation changed
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (loadedFragment != null){
            getFragmentManager().beginTransaction().remove(loadedFragment).commit();
        }
        super.onConfigurationChanged(newConfig);
        startActivity(new Intent(this, c_CarTrackerActivity.class));
    }

    /**
     * Determines which operation to be performed based on the requestCode sent and resultCode
     * received.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode == Activity.RESULT_OK){

            if (requestCode == EDIT_DETAILS_REQUEST){
                Bundle extras = data.getExtras();
                Bundle fuelDetails = extras.getBundle("fuelDetails");
                updateFuelDetail(fuelDetails);
            }

            if (requestCode == ADD_DETAILS_REQUEST){
                Bundle extras = data.getExtras();
                Bundle fuelDetails = extras.getBundle("fuelDetails");
                addFuelDetail(fuelDetails);
            }
        }
    }

    /**
     * Updates an existing fuel details entry in the database and in the activity's ArrayList
     * @param fuelDetails information entered by the user
     */
    protected void updateFuelDetail(Bundle fuelDetails){

        if (fuelDetails != null){
            double price = fuelDetails.getDouble("price");
            double litres = fuelDetails.getDouble("litres");
            double kilometers = fuelDetails.getDouble("kilometers");
            long longDate = fuelDetails.getLong("date");
            long id = fuelDetails.getLong("id");
            int position = fuelDetails.getInt("position");

            ContentValues contentValues = new ContentValues();
            contentValues.put(m_GlobalDatabaseHelper.KEY_PRICE, price);
            contentValues.put(m_GlobalDatabaseHelper.KEY_LITRES, litres);
            contentValues.put(m_GlobalDatabaseHelper.KEY_KILOMETERS, kilometers);
            contentValues.put(m_GlobalDatabaseHelper.KEY_DATE, longDate);

            db.update(m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE,
                    contentValues,
                    m_GlobalDatabaseHelper.KEY_ID + "=" + id,
                    null);

            c_FuelDetails fd = new c_FuelDetails(price, litres, kilometers, new Date(longDate));
            cFuelDetailsList.set(position, fd);
            adapter.notifyDataSetChanged();
            Toast.makeText(c_CarTrackerActivity.this,
                    getResources().getString(R.string.c_changesSaved),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Adds a new fuel details entry to the database and to the activity's ArrayList
     * @param fuelDetails information entered by the user
     */
    protected void addFuelDetail(Bundle fuelDetails) {
        if (fuelDetails != null) {
            double price = fuelDetails.getDouble("price");
            double litres = fuelDetails.getDouble("litres");
            double kilometers = fuelDetails.getDouble("kilometers");
            long longDate = fuelDetails.getLong("date");

            ContentValues contentValues = new ContentValues();
            contentValues.put(m_GlobalDatabaseHelper.KEY_PRICE, price);
            contentValues.put(m_GlobalDatabaseHelper.KEY_LITRES, litres);
            contentValues.put(m_GlobalDatabaseHelper.KEY_KILOMETERS, kilometers);
            contentValues.put(m_GlobalDatabaseHelper.KEY_DATE, longDate);

            db.insert(m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE,
                    "",
                    contentValues);

            c_FuelDetails fd = new c_FuelDetails(price, litres, kilometers, new Date(longDate));
            cFuelDetailsList.add(fd);
            adapter.notifyDataSetChanged();
            Toast.makeText(c_CarTrackerActivity.this,
                    getResources().getString(R.string.c_detailsAdded),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Removes a fuel details entry from the database and from the activity's ArrayList
     * @param id of entry in the database
     * @param position of c_FuelDetails in the ArrayList
     */
    private void deleteFuelDetail(long id, int position){

        // remove fragment (edit / add details) if it exists
        if (loadedFragment != null){
            getFragmentManager().beginTransaction().remove(loadedFragment).commit();
        }

        cFuelDetailsList.remove(position);
        db.delete(m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE,
                m_GlobalDatabaseHelper.KEY_ID + "=" + id,
                null);
        adapter.notifyDataSetChanged();

        Snackbar.make(parentLayout,
                getResources().getString(R.string.c_DeleteSuccessful),
                Snackbar.LENGTH_LONG).show();
    }


    /**
     * AsyncTask used to open the database and load entries into the activity's ArrayList and ListView
     * @author 		Nathan Doef
     * @version		2
     * References:
     * Andy B(2015). Android - asynctask that fills an adapter for a listview [Webpage]. Retrieved from:
     *      https://stackoverflow.com/questions/28171256/android-asynctask-that-fills-an-adapter-for-a-listview
     */
    private class DataBaseQuery extends AsyncTask<String, Integer, ArrayList<c_FuelDetails>>{

        /**
         * Tasks to be performed on a different thread then the main UI thread
         * @param args from the execute() method
         * @return
         */
        @Override
        protected ArrayList<c_FuelDetails> doInBackground(String[] args){

            cursor = db.rawQuery(m_GlobalDatabaseHelper.C_SELECT_ALL_SQL, null);

            // build an array list in the background and pass it back to the GUI thread
            //  after the resource intense processing is complete
            ArrayList<c_FuelDetails> detailsList = new ArrayList<>();

            // used double to perform division and display overall progress
            double totalRecords = cursor.getCount();
            double counter = 0;

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                try {
                    double price = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_PRICE));
                    double litres = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_LITRES));
                    double kilometers = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_KILOMETERS));
                    long longDateRepresentation = cursor.getLong(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_DATE));
                    Date date = new Date(longDateRepresentation);

                    c_FuelDetails details = new c_FuelDetails(price, litres, kilometers, date);
                    detailsList.add(details);

                    Integer progress = (int )Math.round((++counter / totalRecords) * 100);
                    publishProgress(progress);

                    // Added so progress bar will appear longer
                    Thread.sleep(500);

                    cursor.moveToNext();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return detailsList;
        }

        /**
         * Updates the main GUI thread with the percentage of the entries loaded from the database
         * @param value
         */
        @Override
        protected void onProgressUpdate(Integer[] value){
            int progress = value[0];

            glLoading.setVisibility(View.VISIBLE);
            tvLoadingPercentage.setText(String.valueOf(progress).concat("%"));
            pbLoadFuelDetails.setProgress(progress);
        }

        /**
         * Adds all database entries to the activity's ListView and removes the loading animation
         * @param details ArrayList built up in the background thread
         */
        @Override
        protected void onPostExecute(ArrayList<c_FuelDetails> details){
            cFuelDetailsList.clear();
            cFuelDetailsList.addAll(details);
            adapter.notifyDataSetChanged();
            glLoading.setVisibility(View.GONE);
            lvPurchaseHistory.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Custom Adapter used to handle the functionality of displaying an ArrayList<c_FuelDetails>
     * in the activity's ListView
     * @author 		Nathan Doef
     * @version		2
     */
    private class FuelDetailsAdapter extends ArrayAdapter<c_FuelDetails> {

        /** Constructor */
        private FuelDetailsAdapter(Context context) {
            super(context, 0);
        }

        /**
         * @return the number of elements in the activity'y ArrayList
         */
        @Override
        public int getCount() {
            return cFuelDetailsList.size();
        }

        /**
         * @param position in the activity's ArrayList
         * @return object in the ArrayList
         */
        @Override
        public c_FuelDetails getItem(int position) {
            return cFuelDetailsList.get(position);
        }

        /**
         * @param position  in the activity's ArrayList
         * @return the database id for the entry
         */
        @Override
        public long getItemId(int position) {
            if (cursor == null)
                throw new NullPointerException("ERROR: cursor is null");

            cursor = db.rawQuery(m_GlobalDatabaseHelper.C_SELECT_ALL_SQL, null);
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_ID));
        }

        //

        /**
         * Sets up the View that will be inflated for each row of the ListView
         * References:
         * adam83(2014). ListView with Add and Delete Buttons in each Row in android [WebPage] Retrieved from:
         *      https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android
         * @param position in the arrayList
         * @param convertView
         * @param parent of the ListView
         * @return View to be loaded into the row of the ListView
         */
        @Override
        @NonNull
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;

            // only inflate if not already created
            if (view == null){
                LayoutInflater inflater = c_CarTrackerActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.c_fuel_details_summary, parent, false);
            }

            // create a striped table effect
            TableRow fuelDetailRow = view.findViewById(R.id.fuelDetailRow);
            if ((position % 2) == 0){
                fuelDetailRow.setBackgroundColor(getResources().getColor(R.color.c_rowWhite));
            } else {
                fuelDetailRow.setBackgroundColor(getResources().getColor(R.color.c_rowBlue));
            }

            // Event handler for when the delete icon is tapped
            RelativeLayout btnDeleteFuelDetails = view.findViewById(R.id.btnDeleteFuelDetails);
            btnDeleteFuelDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // display confirmation dialog
                    LayoutInflater inflater = getLayoutInflater();
                    LinearLayout rootView
                            = (LinearLayout) inflater.inflate(R.layout.c_custom_alert_dialog, null);

                    ((TextView)rootView.findViewById(R.id.tvCarAlertMsg))
                            .setText(getResources().getString(R.string.c_AlertDeleteDetailsMsg));

                    AlertDialog.Builder builder = new AlertDialog.Builder(c_CarTrackerActivity.this);
                    builder.setView(rootView);

                    // get the database id of the entry being displayed in the View and delete it
                    builder.setPositiveButton(getResources().getString(R.string.c_Yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                long id = adapter.getItemId(position);
                                deleteFuelDetail(id, position);
                            }
                        }
                    );

                    // close dialog if user does not want to delete entry
                    builder.setNegativeButton(getResources().getString(R.string.c_No),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {}
                            }
                    );

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            // get c_FuelDetails object from the activity's ArrayList
            c_FuelDetails details = getItem(position);

            // display all fields of c_FuelDetails in the ListView row
            if (details != null){
                TextView tvPrice = view.findViewById(R.id.tvPrice);
                tvPrice.setText(String.valueOf(details.getPrice()));

                TextView tvLitres = view.findViewById(R.id.tvLitres);
                tvLitres.setText(String.valueOf(details.getLitres()));

                TextView tvKilometers = view.findViewById(R.id.tvKilometers);
                tvKilometers.setText(String.valueOf(details.getKilometers()));

                TextView tvDate = view.findViewById(R.id.tvDate);
                tvDate.setText(DD_MM_YYYY.format(details.getDate()));
            }
            return view;
        }
    }

    /**
     * Find all of the XML controls for the Views that were declared as instance variables
     */
    private void findControls(){
        frameLayoutExists = (findViewById(R.id.flEnterFuelDetailsHolder) != null);
        c_Toolbar = findViewById(R.id.c_Toolbar);
        setSupportActionBar(c_Toolbar);
        parentLayout = findViewById(R.id.fuelDetailsParent);
        lvPurchaseHistory = findViewById(R.id.lvPurchaseHistory);
        btnAddPurchase = findViewById(R.id.btnAddPurchase);
        btnViewFuelStats = findViewById(R.id.btnViewFuelStats);
        pbLoadFuelDetails = findViewById(R.id.pbLoadFuelDetails);
        glLoading = findViewById(R.id.glLoading);
        tvLoadingPercentage = findViewById(R.id.tvLoadingPercentage);
    }

    /**
     * Sets up all the listeners for instance variable Views
     */
    private void setUpListeners(){

        // Event Handler when the user taps the Add Purchase button
        btnAddPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // landscape orientation - loaded fragment
                if (frameLayoutExists){
                    Bundle fragmentDetails = new Bundle();
                    fragmentDetails.putString("btnText", getResources().getString(R.string.c_BtnAddPurchase));
                    fragmentDetails.putString("fragmentTitle", getResources().getString(R.string.c_AddDetailsTitle));

                    c_EnterFuelDetailsFragment addFragment = new c_EnterFuelDetailsFragment();
                    addFragment.setArguments(fragmentDetails);

                    // cache the fragment so it can be removed
                    loadedFragment = addFragment;

                    getFragmentManager().beginTransaction()
                            .replace(R.id.flEnterFuelDetailsHolder, addFragment).commit();
                }

                // portrait orientation - send to new activity
                else {
                    Intent intent = new Intent(c_CarTrackerActivity.this,
                            c_AddFuelDetailsActivity.class);
                    startActivityForResult(intent, ADD_DETAILS_REQUEST);
                }
            }
        });


        // Event Handler for when the user clicks the View Fuel Statistics button
        // Gets fuel statistics from this activity's private methods and sends them to the
        // new activity.
        btnViewFuelStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c_CarTrackerActivity.this,
                        c_FuelStatisticsActivity.class);

                Bundle data = new Bundle();
                data.putParcelableArrayList("gasPurchasesPerMonth", getPrevGasPurchasesByMonth());
                data.putDouble("prevMonthGasPriceAvg", getPrevMonthGasStat(AVERAGE));
                data.putDouble("prevMonthGasPriceTot", getPrevMonthGasStat(TOTAL));

                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

        // Event handler for when a row in the ListView is tapped
        lvPurchaseHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                c_FuelDetails details = cFuelDetailsList.get(position);
                Bundle fuelDetails = new Bundle();
                fuelDetails.putDouble("price", details.getPrice());
                fuelDetails.putDouble("litres", details.getLitres());
                fuelDetails.putDouble("kilometers", details.getKilometers());
                fuelDetails.putLong("date", details.getDate().getTime());
                fuelDetails.putLong("id", id);
                fuelDetails.putInt("position", position);

                // landscape mode - load fragment
                if (frameLayoutExists){
                    Bundle fragmentDetails = new Bundle();
                    fragmentDetails.putString("btnText", getResources().getString(R.string.c_BtnSaveDetails));
                    fragmentDetails.putString("fragmentTitle", getResources().getString(R.string.c_EditDetailsTitle));
                    fragmentDetails.putBundle("fuelDetails", fuelDetails);

                    c_EnterFuelDetailsFragment editFragment = new c_EnterFuelDetailsFragment();
                    editFragment.setArguments(fragmentDetails);

                    // cache the fragment so it can be removed on orientation changes
                    loadedFragment = editFragment;

                    getFragmentManager().beginTransaction()
                            .replace(R.id.flEnterFuelDetailsHolder, editFragment).commit();
                }

                // portrait mode - send to new activity
                else {
                    Intent intent = new Intent(c_CarTrackerActivity.this, c_EditFuelDetailsActivity.class);
                    intent.putExtra("fuelDetails", fuelDetails);
                    startActivityForResult(intent, EDIT_DETAILS_REQUEST);
                }
            }
        });
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
                startActivity(new Intent(c_CarTrackerActivity.this, a_ActivityTrackerActivity.class));
                break;

            // send to Nutrition Tracker activity
            case R.id.menu_food:
                startActivity(new Intent(c_CarTrackerActivity.this, n_NutritionTrackerActivity.class));
                break;

            // send to Thermostat Tracker activity
            case R.id.menu_thermostat:
                startActivity(new Intent(c_CarTrackerActivity.this, t_ThermostatProgramActivity.class));
                break;

            // send to Home page
            case R.id.menu_home:
                startActivity(new Intent(c_CarTrackerActivity.this, m_MainActivity.class));
                break;

            // Display the Car Tracker Activity's help menu
            case R.id.menu_help:
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout rootView
                        = (LinearLayout) inflater.inflate(R.layout.c_custom_alert_dialog, null);

                TextView tvAlertMsg = rootView.findViewById(R.id.tvCarAlertMsg);
                tvAlertMsg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                tvAlertMsg.setText(getResources().getText(R.string.c_helpMenu));

                AlertDialog.Builder builder = new AlertDialog.Builder(c_CarTrackerActivity.this);
                builder.setView(rootView);
                builder.setPositiveButton(getResources().getString(R.string.c_done),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }
                );

                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
        return true;
    }

    /**
     * gets the total gas purchases by month and year if there were any purchases
     * @return an ArrayList of c_FuelStats {" month year": total gas purchases}
     */
    private ArrayList<c_FuelStats> getPrevGasPurchasesByMonth(){
        ArrayList<c_FuelStats> purchases = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        cursor = db.rawQuery(m_GlobalDatabaseHelper.C_SELECT_ALL_SQL, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            long lDate = cursor.getLong(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_DATE));
            calendar.setTime(new Date(lDate));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            String monthYear = getResources().getStringArray(R.array.c_months)[month] + " " + String.valueOf(year);

            // multiply the price of gas by the total number of litres to get the total purchase price
            // of the entry in the database
            double purchasePrice = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_PRICE))
                    * cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_LITRES));

            c_FuelStats stats;

            // no entries or current monthYear is different from the last
            if (purchases.isEmpty() || !purchases.get(purchases.size()-1).getMonthYear().equals(monthYear)){
                stats = new c_FuelStats(monthYear, purchasePrice);
                purchases.add(stats);
            }
            // there is already an entry for the month year combination, increment the amount
            else {
                stats = purchases.get(purchases.size()-1);
                stats.setTotalPurchases(stats.getTotalPurchases() + purchasePrice);
            }

            cursor.moveToNext();
        }
        return purchases;
    }

    /**
     * gets the average gas price of previous month or total gas purchases
     * @param stat AVERAGE (gas price of previous month) TOTAL (purchases for previous month)
     * @return
     */
    private double getPrevMonthGasStat(String stat){
        String table = m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE;
        String where = m_GlobalDatabaseHelper.KEY_DATE + " >= ? AND " + m_GlobalDatabaseHelper.KEY_DATE + " <= ?";
        String[] whereArgs = {
                String.valueOf(getFirstTimestampOfPrevMonth()),
                String.valueOf(getLastTimestampOfPrevMonth())
        };

        cursor = db.query(table, null, where, whereArgs, null, null, null);
        cursor.moveToFirst();

        double gasPriceSum = 0;
        double totalPrice = 0;

        while(!cursor.isAfterLast()){
            double gasPrice = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_PRICE));
            double litres = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_LITRES));

            gasPriceSum += gasPrice;
            totalPrice += (gasPrice * litres);
            cursor.moveToNext();
        }

        // calculate the average gas price
        if (stat.equals(AVERAGE) && cursor.getCount() != 0){
            return (gasPriceSum / cursor.getCount());
        }
        // return the total gas price
        else if (stat.equals(TOTAL)){
            return totalPrice;
        }
        return -1; // no results
    }

    /**
     * gets the first time stamp of the previous month
     * @return timestamp
     */
    private long getFirstTimestampOfPrevMonth(){
        Calendar calendar = getPrevMonthAndYear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date firstDateOfPrevMonth = calendar.getTime();
        return firstDateOfPrevMonth.getTime();
    }

    /**
     * gets the last time stamp of the previous month
     * @return timestamp
     */
    private long getLastTimestampOfPrevMonth(){
        Calendar calendar = getPrevMonthAndYear();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date lastDayOfPrevMonth = calendar.getTime();
        return lastDayOfPrevMonth.getTime();
    }

    /**
     * gets a calendar object for the previous month and year
     * @return calendar
     */
    private Calendar getPrevMonthAndYear(){
        Calendar calendar = Calendar.getInstance();
        int currMonth = calendar.get(Calendar.MONTH);
        int prevMonth = currMonth == 0 ? 11 : currMonth-1;
        int currYear = calendar.get(Calendar.YEAR);
        int prevYear = currMonth == 0 ? currYear-1 : currYear;
        calendar.set(Calendar.MONTH, prevMonth);
        calendar.set(Calendar.YEAR, prevYear);
        return calendar;
    }
}