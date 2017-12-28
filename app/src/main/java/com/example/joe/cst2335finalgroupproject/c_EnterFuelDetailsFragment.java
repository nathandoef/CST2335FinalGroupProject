package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * Accepts input from the user. This fragment is used for both adding new fuel details
 * and updating existing fuel details
 * @author 		Nathan Doef
 * @version		2
 */
public class c_EnterFuelDetailsFragment extends Fragment {

    /** Activity where the fragment is loaded */
    private Activity callingActivity;

    /** details passesd to the fragment */
    Bundle fuelDetails;

    /** text that will be displayed in the alert message if an error occurs */
    String alertMsgText = "";

    /**
     * Finds controls and sets the title and button text of the fragment. If the user is editting
     * an existing entry, the values are loaded into the appropriate fields.
     * @param inflater
     * @param container
     * @param saveInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        View view = inflater.inflate(R.layout.c_enter_fuel_details_fragment, container, false);

        final TextView enterFuelDetailsTitle = view.findViewById(R.id.enterFuelDetailsTitle);
        final EditText etPrice = view.findViewById(R.id.etPrice);
        final EditText etLitres = view.findViewById(R.id.etLitres);
        final EditText etKilometers = view.findViewById(R.id.etKilometers);
        final EditText etDate = view.findViewById(R.id.etDate);
        final Button btnEnterDetails = view.findViewById(R.id.btnEnterDetailsSubmit);
        final Button btnEnterDetailsCancel = view.findViewById(R.id.btnEnterDetailsCancel);

        Bundle fragmentDetails = getArguments();

        if (fragmentDetails != null){
            String title = fragmentDetails.getString("fragmentTitle");
            String btnText = fragmentDetails.getString("btnText");

            enterFuelDetailsTitle.setText(title);
            btnEnterDetails.setText(btnText);

            // cache fuel details, will be null if add request
            fuelDetails = fragmentDetails.getBundle("fuelDetails");

            // Edit request, populate the previous field values
            if (fuelDetails != null){
                alertMsgText = getResources().getString(R.string.c_AlertFillFieldsEdit);
                double price = fuelDetails.getDouble("price");
                double litres = fuelDetails.getDouble("litres");
                double kilometers = fuelDetails.getDouble("kilometers");
                long longDate = fuelDetails.getLong("date");

                etPrice.setText(String.valueOf(price));
                etLitres.setText(String.valueOf(litres));
                etKilometers.setText(String.valueOf(kilometers));
                etDate.setText(String.valueOf(c_CarTrackerActivity.DD_MM_YYYY.format(longDate)));
            }
            // Add request, just populate today's date
            else {
                alertMsgText = getResources().getString(R.string.c_AlertFillFieldsAdd);
                etDate.setText(String.valueOf(c_CarTrackerActivity.DD_MM_YYYY.format(new Date())));
            }
        }

        // Event handlers when the users submits an added or edited entry
        btnEnterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    // get values in EditText fields
                    double price = Double.parseDouble(etPrice.getText().toString());
                    double litres = Double.parseDouble(etLitres.getText().toString());
                    double kilometers = Double.parseDouble(etKilometers.getText().toString());
                    String stringDate = etDate.getText().toString();
                    Date date =  c_CarTrackerActivity.DD_MM_YYYY.parse(stringDate);

                    // add details request
                    if (fuelDetails == null){
                        fuelDetails = new Bundle();
                    }

                    // update the fuel details bundle
                    fuelDetails.putDouble("price", price);
                    fuelDetails.putDouble("litres", litres);
                    fuelDetails.putDouble("kilometers", kilometers);
                    fuelDetails.putLong("date", date.getTime());

                    switch(callingActivity.getLocalClassName()){

                        // portrait orientation - edit existing entry
                        case "c_EditFuelDetailsActivity":
                            ((c_EditFuelDetailsActivity)callingActivity).updateFuelDetail(fuelDetails);
                            break;

                        // portrait orientation - add entry
                        case "c_AddFuelDetailsActivity":
                            ((c_AddFuelDetailsActivity)callingActivity).addFuelDetail(fuelDetails);
                            break;

                        // landscape orientation
                        case "c_CarTrackerActivity":
                            // no id present, add fuel detail
                            if (fuelDetails.getLong("id", -1) == -1){
                                ((c_CarTrackerActivity)callingActivity).addFuelDetail(fuelDetails);
                            }
                            // id present, edit fuel detail
                            else {
                                ((c_CarTrackerActivity)callingActivity).updateFuelDetail(fuelDetails);
                            }
                            callingActivity.getFragmentManager().beginTransaction()
                                    .remove(c_EnterFuelDetailsFragment.this).commit();
                            break;
                    }
                } catch (Exception e) { // attempt to save empty or invalid date

                    LayoutInflater inflater = callingActivity.getLayoutInflater();
                    LinearLayout rootView
                            = (LinearLayout) inflater.inflate(R.layout.c_custom_alert_dialog, null);

                    ((TextView)rootView.findViewById(R.id.tvCarAlertMsg)).setText(alertMsgText);

                    // display error message
                    AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
                    builder.setView(rootView);
                    builder.setPositiveButton(getResources().getString(R.string.c_Ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }
                    );

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });


        // Event Handler for when the user cancels an entry
        btnEnterDetailsCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callingActivity.setResult(Activity.RESULT_CANCELED);

                switch(callingActivity.getLocalClassName()){

                    // Finish the edit and add activities if in portrait orientation
                    case "c_EditFuelDetailsActivity":
                    case "c_AddFuelDetailsActivity":
                        callingActivity.finish();
                        break;

                    // remove fragment if in landscape orientation
                    case "c_CarTrackerActivity":
                        callingActivity.getFragmentManager().beginTransaction()
                                .remove(c_EnterFuelDetailsFragment.this).commit();
                        break;

                }
            }
        });


        // update the Edit Text to reflect the date picked in the DatePicker
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c_DatePickerFragment datePicker = new c_DatePickerFragment();
                datePicker.setDisplay(etDate);
                datePicker.show(getFragmentManager(), "Date Picker");
            }
        });

        return view;
    }

    /**
     * get a handle on the calling activty
     * @param activity calling activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callingActivity = activity;
    }
}