package com.example.hikeapplication.Observation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.R;


import java.util.Calendar;
import java.util.Objects;

public class AddObservationsActivity extends AppCompatActivity {
    private EditText observation, dateObservation, comment;
    private Button dateTimeButton;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private int hike_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add Observations");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observations);
        dateTimeButton = findViewById(R.id.dateTimeButton);

        Intent intent = getIntent();
        hike_id = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("hike_id")));
        initDatePicker();
        dateObservation = findViewById(R.id.dateObservation);
        dateObservation.setText(getTodayDate());
        dateTimeButton.setOnClickListener(v -> {
            timePickerDialog.show();
            datePickerDialog.show();
        });

        Button createObservation = findViewById(R.id.createObservation);
        createObservation.setOnClickListener(v -> {
            observation = findViewById(R.id.observation);
            dateTimeButton = findViewById(R.id.dateTimeButton);
            comment = findViewById(R.id.comment);
            String name_observation = observation.getText().toString().trim();
            String date_observation = dateObservation.getText().toString().trim();
            String comment_observation = comment.getText().toString().trim();

            if (name_observation.length() == 0
                    | date_observation.length() == 0
                    | comment_observation.length() == 0
            ) {
                showAlertDialog();
            } else {
                ContentValues values = new ContentValues();
                values.put("hike_id", hike_id);
                values.put("observation", name_observation);
                values.put("date", date_observation);
                values.put("comment", comment_observation);

                String message = "New observation will be added: \n\n" +
                        "Observation: " + name_observation + ",\n" +
                        "Time of observation: " + date_observation + ",\n" +
                        "Comment: " + comment_observation + ".\n\n" +
                        "Are you sure?";
                showConfirmDialog(message, values);
            }
        });
    }

    //    SHOW ALERT CONFIRM DIALOG
    private void showConfirmDialog(String message, ContentValues values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddObservationsActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            ConnectDb db = new ConnectDb(AddObservationsActivity.this);
            db.addObservation(values.getAsString("observation"),
                    values.getAsInteger("hike_id"),
                    values.getAsString("date"),
                    values.getAsString("comment"));

            Intent intent = new Intent(AddObservationsActivity.this, ObservationsActivity.class);
            startActivity(intent);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //    SHOW ALERT ERROR DIALOG
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("All required fields must be filled!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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