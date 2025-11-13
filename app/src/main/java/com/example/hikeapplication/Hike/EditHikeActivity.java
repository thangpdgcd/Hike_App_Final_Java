package com.example.hikeapplication.Hike;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.MainActivity;
import com.example.hikeapplication.Observation.ObservationsActivity;
import com.example.hikeapplication.R;

import java.text.ParseException;
import java.util.Calendar;

public class EditHikeActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private EditText name, location, date, length, description;
    private RadioGroup radioGroup;
    private RadioButton btn_yes, btn_no;
    private Button save_btn, dateButton, observation_btn;
    private String id_hike, name_hike, location_hike, date_hike, parking_hike, length_hike, level_hike, description_hike;
    private Spinner levelSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hike);

        // Hiển thị nút back trên ActionBar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Detail Hike");
        }

        // Ánh xạ view
        name = findViewById(R.id.name);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        length = findViewById(R.id.length);
        description = findViewById(R.id.description);
        radioGroup = findViewById(R.id.radioGroup);
        btn_yes = findViewById(R.id.radioButton_yes);
        btn_no = findViewById(R.id.radioButton_no);
        dateButton = findViewById(R.id.dateButton);
        save_btn = findViewById(R.id.save_btn);
        observation_btn = findViewById(R.id.observation_btn);
        levelSpinner = findViewById(R.id.levelSpinner);

        // Spinner độ khó
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.level,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        );
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        levelSpinner.setAdapter(adapter);

        // Lấy dữ liệu từ Intent
        try {
            getAndSetData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // DatePicker
        initDatePicker();
        dateButton.setOnClickListener(v -> datePickerDialog.show());

        // Nút lưu
        save_btn.setOnClickListener(v -> {
            String name_hike = name.getText().toString().trim();
            String location_hike = location.getText().toString().trim();
            String date_hike = date.getText().toString().trim();
            String length_hike = length.getText().toString().trim();
            String level_hike = levelSpinner.getSelectedItem().toString().trim();
            String description_hike = description.getText().toString().trim();
            int idGroup = radioGroup.getCheckedRadioButtonId();

            if (idGroup < 0 || name_hike.isEmpty() || location_hike.isEmpty() ||
                    date_hike.isEmpty() || length_hike.isEmpty() ||
                    level_hike.isEmpty() || description_hike.isEmpty()) {
                showAlertDialog();
                return;
            }

            String parking = btn_yes.isChecked() ? btn_yes.getText().toString().trim() : btn_no.getText().toString().trim();

            ContentValues value = new ContentValues();
            value.put("id", id_hike);
            value.put("name", name_hike);
            value.put("location", location_hike);
            value.put("date", date_hike);
            value.put("parking", parking);
            value.put("length", length_hike);
            value.put("level", level_hike);
            value.put("description", description_hike);

            String message = "Hike will be updated:\n" +
                    "Name: " + name_hike + "\n" +
                    "Location: " + location_hike + "\n" +
                    "Date: " + date_hike + "\n" +
                    "Parking: " + parking + "\n" +
                    "Length: " + length_hike + "\n" +
                    "Level: " + level_hike + "\n" +
                    "Description: " + description_hike;

            showConfirmDialog(message, value);
        });

        // Nút Observation
        observation_btn.setOnClickListener(v -> {
            Intent intent = new Intent(EditHikeActivity.this, ObservationsActivity.class);
            intent.putExtra("hike_id", id_hike);
            startActivity(intent);
        });
    }

    // ✅ Lấy và set dữ liệu hike
    public void getAndSetData() throws ParseException {
        if (getIntent().hasExtra("id")) {
            id_hike = getIntent().getStringExtra("id");
            name_hike = getIntent().getStringExtra("name");
            location_hike = getIntent().getStringExtra("location");
            date_hike = getIntent().getStringExtra("date");
            parking_hike = getIntent().getStringExtra("parking");
            length_hike = getIntent().getStringExtra("length");
            level_hike = getIntent().getStringExtra("level");
            description_hike = getIntent().getStringExtra("description");

            name.setText(name_hike);
            location.setText(location_hike);
            date.setText(date_hike);
            length.setText(length_hike);
            description.setText(description_hike);

            if (parking_hike != null && parking_hike.equalsIgnoreCase("Yes")) {
                btn_yes.setChecked(true);
            } else {
                btn_no.setChecked(true);
            }

            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) levelSpinner.getAdapter();
            int spinnerPosition = adapter.getPosition(level_hike);
            levelSpinner.setSelection(spinnerPosition);
        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Dialog cảnh báo
    private void showAlertDialog() {
        new AlertDialog.Builder(EditHikeActivity.this)
                .setTitle("Error")
                .setMessage("All required fields must be filled!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ✅ Dialog xác nhận lưu
    private void showConfirmDialog(String message, ContentValues values) {
        new AlertDialog.Builder(EditHikeActivity.this)
                .setTitle("Confirmation")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    ConnectDb db = new ConnectDb(EditHikeActivity.this);
                    db.editHike(
                            values.getAsString("id"),
                            values.getAsString("name"),
                            values.getAsString("location"),
                            values.getAsString("date"),
                            values.getAsString("parking"),
                            values.getAsString("length"),
                            values.getAsString("level"),
                            values.getAsString("description")
                    );

                    Toast.makeText(EditHikeActivity.this, "Hike updated successfully!", Toast.LENGTH_SHORT).show();

                    // 🔁 Quay lại MainActivity thay vì crash vì Fragment null
                    Intent intent = new Intent(EditHikeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ✅ DatePicker
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            month++;
            String dateP = makeDateString(day, month, year);
            date.setText(dateP);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return String.format("%02d/%02d/%04d", day, month, year);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
