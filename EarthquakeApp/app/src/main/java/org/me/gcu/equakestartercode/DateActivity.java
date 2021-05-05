// Yassine Lakhmarti S1903349
package org.me.gcu.equakestartercode;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DateActivity extends AppCompatActivity {

    TextView specificDate, startDate, endDate;
    Button btnApply;
    RadioButton radioSpecific, radioRange;
    RecyclerView recyclerView;
    DateAdapter dateAdapter;
    List<Item> itemList = new ArrayList<>();
    String TAG = "theH";

    int highestIndexId, lowestIndexId, highestLatitudeId, lowestLatitudeId, highestLongitudeId, lowestLongitudeId;
    /*
             onCreate() method performs the basic application start-up logic, we first link the activity to the correct view
             in this case, it's activity_date
             we add a back icon in the toolbar for the user to go back
             We get the item from the adapter
             We initialize the widgets which are all the earthquake: radio buttons, date picker, and recyclerView
             We check if the itemList is not empty, the activity will not run unless there are items stored in the itemList
             Set onclicklisteners for the radio buttons
             Check which radio button is checked and runs the method accordingly
        */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        itemList.addAll(MainActivity.itemList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioSpecific = findViewById(R.id.radio_specific);
        radioRange = findViewById(R.id.radio_range);
        specificDate = findViewById(R.id.specific_date);
        startDate = findViewById(R.id.range_start);
        endDate = findViewById(R.id.range_end);
        btnApply = findViewById(R.id.btn_apply);
        recyclerView = findViewById(R.id.recycler_view);

        if (itemList.size() > 0) {
            arrangeList();
            dateAdapter = new DateAdapter(DateActivity.this, itemList, highestIndexId, lowestIndexId, highestLatitudeId, lowestLatitudeId, highestLongitudeId, lowestLongitudeId);
            recyclerView.setLayoutManager(new LinearLayoutManager(DateActivity.this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(dateAdapter);
        }

        radioSpecific.setOnClickListener(view -> radioRange.setChecked(false));
        radioRange.setOnClickListener(view -> radioSpecific.setChecked(false));

        specificDate.setOnClickListener(view -> datePickerDialog(0));
        startDate.setOnClickListener(view -> datePickerDialog(1));
        endDate.setOnClickListener(view -> datePickerDialog(2));

        btnApply.setOnClickListener(view -> {
            if (radioSpecific.isChecked()) {
                if (TextUtils.isEmpty(specificDate.getText().toString())) {
                    Toast.makeText(DateActivity.this, "Please select specific date", Toast.LENGTH_SHORT).show();
                } else {
                    getSingleDateData();
                }
            } else if (radioRange.isChecked()) {
                if (TextUtils.isEmpty(startDate.getText().toString())) {
                    Toast.makeText(DateActivity.this, "Please select start range date", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(endDate.getText().toString())) {
                    Toast.makeText(DateActivity.this, "Please select end range date", Toast.LENGTH_SHORT).show();
                } else {
                    getRangeDateData();
                }
            }
        });
    }
    /*
        Override the onSupportNavigateUp() method to enable the user to return to the previous page
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

/*
    Create the datePickerDialog() to select the dates, this displays a calendar as a modal (dialog) to the user
 */
    public void datePickerDialog(int index) {
        DatePickerDialog mDatePicker;
        final Calendar mCalendar = Calendar.getInstance();

        mDatePicker = new DatePickerDialog(DateActivity.this, (datePicker, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String formattedDate = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(mCalendar.getTime());
            if (index == 0) {
                specificDate.setText(formattedDate);
            } else if (index == 1) {
                startDate.setText(formattedDate);
            } else if (index == 2) {
                endDate.setText(formattedDate);
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePicker.show();
    }
    /*
    Checks if both dates in date range are not equal
     */
    public boolean isDateEquals(String eqDateStr, String newDateStr) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        try {
            Date eqDate = sdf.parse(eqDateStr);
            Date newDate = sdf.parse(newDateStr);
            result = newDate.equals(eqDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
        Create isDateEqualToRange for checking date are present in between range
     */
    public boolean isDateEqualToRange(String eqDateStr, String startDateStr, String endDateStr) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        try {
            Date eqDate = sdf.parse(eqDateStr);
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            result = (eqDate.equals(startDate) || eqDate.after(startDate)) && (eqDate.equals(endDate) || eqDate.before(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /*
        Create getSingleDateData method for getting specific date data
        Arrange list by magnitude
        Send a dialog if there were no earthquakes recorded on that date
     */
    public void getSingleDateData() {
        String eqDateStr = specificDate.getText().toString();
        itemList.clear();
        itemList.addAll(MainActivity.itemList);
        List<Item> newItemList = new ArrayList<>();
        for (Item item : itemList) {
            if (isDateEquals(item.getPubDate(), eqDateStr)) {
                newItemList.add(item);
            }
        }
        if (newItemList.size() > 0) {
            itemList.clear();
            itemList.addAll(newItemList);
            arrangeList();
            dateAdapter.reloadData(highestIndexId, lowestIndexId, highestLatitudeId, lowestLatitudeId, highestLongitudeId, lowestLongitudeId);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(DateActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("There were no recorded earthquakes on " + eqDateStr);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            itemList.clear();
        }

        dateAdapter.notifyDataSetChanged();

    }

    /*
        create getRangeDateData method for getting range date data
        Works similarly to getSingleDateData
     */
    public void getRangeDateData() {
        String startDateStr = startDate.getText().toString();
        String endDateStr = endDate.getText().toString();
        itemList.clear();
        itemList.addAll(MainActivity.itemList);
        List<Item> newItemList = new ArrayList<>();

        for (Item item : itemList) {
            if (isDateEqualToRange(item.getPubDate(), startDateStr, endDateStr)) {
                newItemList.add(item);
            }
        }

        if (newItemList.size() > 0) {
            itemList.clear();
            itemList.addAll(newItemList);
            arrangeList();

            dateAdapter.reloadData(highestIndexId, lowestIndexId, highestLatitudeId, lowestLatitudeId, highestLongitudeId, lowestLongitudeId);

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(DateActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("There were no recorded earthquakes on " + startDateStr + " , " + endDateStr);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            itemList.clear();
        }

        dateAdapter.notifyDataSetChanged();

    }

    /*
       arrangeList() method arranges the list accordingly to magnitude by descending order
     */
    public void arrangeList() {
        Collections.sort(itemList, (obj1, obj2) -> {
            return Double.compare(obj2.getMagnitude(), obj1.getMagnitude());
        });

        getHighestIndex();

        getLowestIndex();

        getLatitudeId();

        getLongitudeId();

        Log.d("theI", "arrangeList: highestIndexId: " + highestIndexId + " lowestIndexId: " + lowestIndexId);
        Log.d("theI", "arrangeList: highestLatitudeId: " + highestLatitudeId + " lowestLatitudeId: " + lowestLatitudeId +
                " highestLongitudeId: " + highestLongitudeId + " lowestLongitudeId: " + lowestLongitudeId);
        List<Item> itemList1 = new ArrayList<>();

        for (int i = 0; i < itemList.size(); i++) {
            int id = itemList.get(i).getId();
            if (id == highestIndexId || id == lowestIndexId ||
                    id == highestLatitudeId || id == lowestLatitudeId || id == highestLongitudeId || id == lowestLongitudeId) {
                itemList1.add(itemList.get(i));
                Log.d("theI", "arrangeList: i: " + i + " id: " + id);
            }
        }
        itemList.clear();
        itemList.addAll(itemList1);
    }

    /*
        getHighestIndex() method for getting the data with the highest depth Index Value Id
     */
    public void getHighestIndex() {
        double highest = itemList.get(0).getDepth();
        highestIndexId = itemList.get(0).getId();
        for (int s = 1; s < itemList.size(); s++) {
            double curValue = itemList.get(s).getDepth();
            if (curValue > highest) {
                highest = curValue;
                highestIndexId = itemList.get(s).getId();
            }
        }
    }

    /*
        getLowestIndex() method for getting the data with the lowest depth Index Value Id
     */
    public void getLowestIndex() {
        double lowest = itemList.get(0).getDepth();
        lowestIndexId = itemList.get(0).getId();
        for (int s = 1; s < itemList.size(); s++) {
            double curValue = itemList.get(s).getDepth();
            if (curValue < lowest) {
                lowest = curValue;
                lowestIndexId = itemList.get(s).getId();
            }
        }
    }

    /*
        getLatitudeId() method retrieves the highest and lowest latitude id
     */
    public void getLatitudeId() {
        double highest = itemList.get(0).getLatitude();
        double lowest = itemList.get(0).getLatitude();
        highestLatitudeId = itemList.get(0).getId();
        lowestLatitudeId = itemList.get(0).getId();

        for (int s = 1; s < itemList.size(); s++) {
            double curValue = itemList.get(s).getLatitude();
            if (curValue > highest) {
                highest = curValue;
                highestLatitudeId = itemList.get(s).getId();
            }
            if (curValue < lowest) {
                lowest = curValue;
                lowestLatitudeId = itemList.get(s).getId();
            }
        }
    }

    /*
        getLongitudeId() method retrieves the highest and lowest longitude id
    */
    public void getLongitudeId() {

        double highest = itemList.get(0).getLongitude();
        double lowest = itemList.get(0).getLongitude();
        highestLongitudeId = itemList.get(0).getId();
        lowestLongitudeId = itemList.get(0).getId();

        for (int s = 1; s < itemList.size(); s++) {
            double curValue = itemList.get(s).getLongitude();
            if (curValue > highest) {
                highest = curValue;
                highestLongitudeId = itemList.get(s).getId();
            }
            if (curValue < lowest) {
                lowest = curValue;
                lowestLongitudeId = itemList.get(s).getId();
            }
        }
    }

}