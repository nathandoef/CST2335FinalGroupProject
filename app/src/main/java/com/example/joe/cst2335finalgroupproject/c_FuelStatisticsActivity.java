package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Activity used to display the average gas price and total purchase amount of the previous month,
 * as well as the total purchase amount for all previous month year combination if purchases
 * were made.
 * @author 		Nathan Doef
 * @version		2
 */
public class c_FuelStatisticsActivity extends Activity {

    /** Used to display the purchase amounts for the previous month year combinations */
    private ListView lvFuelStatistics;

    /** ArrayList used to hold fuel details objects */
    private ArrayList<c_FuelStats> gasPurchasesPerMonth;

    /** Adapter used to attach lvFuelStatistics and gasPurchasesPerMonth */
    private FuelStatisticsAdapter adapter;

    /**
     * Gets all the statistics data that was sent from the c_CarTrackerActivity activity, sets up
     * all of the controls and connects the Activity's ArrayList to the ListView
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_fuel_statistics);

        Bundle extras = getIntent().getExtras();
        Bundle data = extras.getBundle("data");

        final TextView tvNoHistory = findViewById(R.id.c_tvNoHistory);
        gasPurchasesPerMonth = data.getParcelableArrayList("gasPurchasesPerMonth");

        if(gasPurchasesPerMonth == null || gasPurchasesPerMonth.isEmpty()){
            tvNoHistory.setText(getResources().getString(R.string.c_avgGasPurchHistNone));
            tvNoHistory.setVisibility(View.VISIBLE);
        }

        lvFuelStatistics = findViewById(R.id.lvFuelStatistics);
        adapter = new FuelStatisticsAdapter(this);
        lvFuelStatistics.setAdapter(adapter);

        double prevMonthGasPriceAvg = data.getDouble("prevMonthGasPriceAvg");
        final TextView tvPrevMonthAvgGasPrice = findViewById(R.id.tvPrevMonthAvgGasPrice);
        tvPrevMonthAvgGasPrice.setText(prevMonthGasPriceAvg == -1 ? "N/A"
                : String.format("$ %.2f", prevMonthGasPriceAvg));

        double prevMonthGasPriceTot = data.getDouble("prevMonthGasPriceTot");
        final TextView tvPrevMonthTotalGas = findViewById(R.id.tvPrevMonthTotalGas);
        tvPrevMonthTotalGas.setText(String.format("$ %.2f", prevMonthGasPriceTot));
    }

    /**
     * Custom ArrayAdapter used to display the objects in the activity's ArrayList
     * @author 		Nathan Doef
     * @version		2
     */
    private class FuelStatisticsAdapter extends ArrayAdapter<c_FuelStats> {

        /**
         * Constructor
         * @param context
         */
        private FuelStatisticsAdapter(Context context) {
            super(context, 0);
        }

        /**
         * @return the number of objects in the activity's ArrayList
         */
        @Override
        public int getCount() {
            return gasPurchasesPerMonth.size();
        }

        /**
         * @param position of the object to be retrieved
         * @return the object at the position in the activity's ArrayList
         */
        @Override
        public c_FuelStats getItem(int position) {
            return gasPurchasesPerMonth.get(position);
        }

        /**
         * Inflates a view for each of the c_FuelStats objects in the activity's ArrayList
         * @param position in the activity's ArrayList
         * @param convertView
         * @param parent
         * @return View to be loaded in the row of the activity's ListView
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;

            // only inflate a View if it does not already exist
            if (view == null){
                LayoutInflater inflater = c_FuelStatisticsActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.c_fuel_statistic_row, null);
            }

            // set up zebra striping for the table
            GridLayout glFuelStatRow = view.findViewById(R.id.c_glFuelStatRow);
            if ((position % 2) == 0){
                glFuelStatRow.setBackgroundColor(getResources().getColor(R.color.c_rowWhite));
            } else {
                glFuelStatRow.setBackgroundColor(getResources().getColor(R.color.c_rowBlue));
            }

            c_FuelStats stats = getItem(position);

            TextView c_StatsLabel = view.findViewById(R.id.c_StatsLabel);
            c_StatsLabel.setText(stats.getMonthYear());

            TextView c_StatsPurchase = view.findViewById(R.id.c_StatsPurchase);
            c_StatsPurchase.setText(String.format("$ %.2f", stats.getTotalPurchases()));

            return view;
        }
    }
}