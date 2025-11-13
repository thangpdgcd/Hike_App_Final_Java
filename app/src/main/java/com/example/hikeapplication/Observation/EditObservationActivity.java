package com.example.hikeapplication.Observation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.Fragment.HomeFragment;
import com.example.hikeapplication.Hike.EditHikeActivity;
import com.example.hikeapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Objects;

public class EditObservationActivity extends AppCompatActivity {
    private EditText observation, dateObservation, comment;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit observation");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_observation);
        observation = findViewById(R.id.observation);
        dateObservation = findViewById(R.id.dateObservation);
        comment = findViewById(R.id.comment);
        Button dateTimeButton = findViewById(R.id.dateTimeButton);
        String id = getIntent().getStringExtra("observation_id");
        try {
            getAndSetData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initDatePicker();
        dateTimeButton.setOnClickListener(v -> {
            timePickerDialog.show();
            datePickerDialog.show();
        });

        Button editObservation = findViewById(R.id.editObservation);
        editObservation.setOnClickListener(v -> {

            String observation_name = observation.getText().toString().trim();
            String observation_time = dateObservation.getText().toString().trim();
            String observation_comment = comment.getText().toString().trim();

            if (observation_name.length() == 0
                    | observation_time.length() == 0
                    | observation_comment.length() == 0) {
                showAlertDialog();
            } else {
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("name", observation_name);
                values.put("time", observation_time);
                values.put("comment", observation_comment);

                String message = "Hike will be edited: \n" +
                        "Name: " + observation_name + "\n" +
                        "Location: " + observation_time + "\n" +
                        "Date of the hike: " + observation_comment + "\n\n" +
                        "Are you sure?";

                showConfirmDialog(message, values);
            }
        });


    }

    //    SHOW ALERT ERROR DIALOG
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditObservationActivity.this);
        builder.setTitle("Error");
        builder.setMessage("All required fields must be filled!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //    SHOW ALERT CONFIRM DIALOG
    private void showConfirmDialog(String message, ContentValues values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditObservationActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConnectDb db = new ConnectDb(EditObservationActivity.this);
                db.editObservation(values.getAsString("id"),
                        values.getAsString("name"),
                        values.getAsString("time"),
                        values.getAsString("comment"));
                Intent intent = new Intent(EditObservationActivity.this, ObservationsActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getAndSetData() throws ParseException {
        if (
                getIntent().hasExtra("observation_id")
                        && getIntent().hasExtra("name")
                        && getIntent().hasExtra("time")
                        && getIntent().hasExtra("comment")
        ) {
            String observation_name = getIntent().getStringExtra("name");
            String observation_time = getIntent().getStringExtra("time");
            String observation_comment = getIntent().getStringExtra("comment");

            observation.setText(observation_name);
            dateObservation.setText(observation_time);
            comment.setText(observation_comment);
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    //    DATE TIME PICKER
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            dateObservation.setText(makeDateString(day, month, year));

        };
        @SuppressLint("SetTextI18n") TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> dateObservation.setText(makeTimeString(minute, hourOfDay) + " - " + dateObservation.getText().toString());

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        timePickerDialog = new TimePickerDialog(this, style, timeSetListener, hour, minute, true);
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeTimeString(minute, hours) + " - " + makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        if (day < 10) {
            return "0" + day + "/" + getMonthFormat(month) + "/" + year;
        }
        return day + "/" + getMonthFormat(month) + "/" + year;
    }

    private String makeTimeString(int minute, int hour) {
        if (minute < 10 && hour < 10) {
            return "0" + hour + ":0" + minute;
        } else if (minute < 10) {
            return hour + ":0" + minute;
        } else if (hour < 10) {
            return "0" + hour + ":" + minute;
        }
        return hour + ":" + minute;
    }

    private String getMonthFormat(int month) {
        if (month == 1) return "01";
        if (month == 2) return "02";
        if (month == 3) return "03";
        if (month == 4) return "04";
        if (month == 5) return "05";
        if (month == 6) return "06";
        if (month == 7) return "07";
        if (month == 8) return "08";
        if (month == 9) return "09";
        if (month == 10) return "10";
        if (month == 11) return "11";
        if (month == 12) return "12";

        return "01";
    }
}