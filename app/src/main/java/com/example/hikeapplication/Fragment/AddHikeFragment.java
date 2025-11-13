package com.example.hikeapplication.Fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class AddHikeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText name, location, date, length, level, description;
    private RadioGroup radioGroup;
    private RadioButton btn_yes, btn_no;
    private DatePickerDialog datePickerDialog;
    private Button save_btn, dateButton;
    private Spinner levelSpinner;

    public AddHikeFragment() {
        // Required empty public constructor
    }

    public static AddHikeFragment newInstance(String param1, String param2) {
        AddHikeFragment fragment = new AddHikeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Hiện nút back ngay khi vào trang Add
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Add new hike");
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_hike, container, false);

        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        date = view.findViewById(R.id.date);
        length = view.findViewById(R.id.length);
        level = view.findViewById(R.id.level);
        description = view.findViewById(R.id.description);
        radioGroup = view.findViewById(R.id.radioGroup);
        btn_yes = view.findViewById(R.id.radioButton_yes);
        btn_no = view.findViewById(R.id.radioButton_no);

        date.setText(getTodayDate());

        initDatePicker();

        levelSpinner = view.findViewById(R.id.levelSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.level, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        levelSpinner.setAdapter(adapter);

        dateButton = view.findViewById(R.id.dateButton);
        dateButton.setOnClickListener(v -> datePickerDialog.show());

        save_btn = view.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(v -> saveHike());

        return view;
    }

    // ✅ Lưu dữ liệu
    private void saveHike() {
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

        RadioButton selected = getView().findViewById(idGroup);
        String parking = selected.getText().toString().trim();

        ContentValues value = new ContentValues();
        value.put("name", name_hike);
        value.put("location", location_hike);
        value.put("date", date_hike);
        value.put("parking", parking);
        value.put("length", length_hike);
        value.put("level", level_hike);
        value.put("description", description_hike);

        String message = "New hike will be added:\n\n"
                + "Name: " + name_hike + "\n"
                + "Location: " + location_hike + "\n"
                + "Date: " + date_hike + "\n"
                + "Parking: " + parking + "\n"
                + "Length: " + length_hike + "\n"
                + "Level: " + level_hike + "\n"
                + "Description: " + description_hike + "\n\nAre you sure?";

        showConfirmDialog(message, value);
    }

    // ⚙️ Hàm hiển thị thông báo lỗi
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Error");
        builder.setMessage("All required fields must be filled!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // ⚙️ Xác nhận thêm mới
    private void showConfirmDialog(String message, ContentValues values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirmation");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            ConnectDb db = new ConnectDb(getActivity());
            db.addHike(
                    values.getAsString("name"),
                    values.getAsString("location"),
                    values.getAsString("date"),
                    values.getAsString("parking"),
                    values.getAsString("length"),
                    values.getAsString("level"),
                    values.getAsString("description")
            );

            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem home = menu.findItem(R.id.home);
            home.setChecked(true);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();

            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // 📅 Khởi tạo DatePicker
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            month = month + 1;
            String dateP = makeDateString(day, month, year);
            date.setText(dateP);
        };

        Calendar cal = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(
                getContext(),
                AlertDialog.THEME_HOLO_DARK,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        return makeDateString(
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)
        );
    }

    // ✅ HÀM NÀY BỊ MẤT TRƯỚC ĐÓ — GIỜ ĐÃ THÊM LẠI
    private String makeDateString(int day, int month, int year) {
        return (day < 10 ? "0" + day : day) + "/" + getMonthFormat(month) + "/" + year;
    }

    private String getMonthFormat(int month) {
        if (month < 10) {
            return "0" + month;
        } else {
            return String.valueOf(month);
        }
    }
}
