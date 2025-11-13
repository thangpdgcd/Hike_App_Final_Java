package com.example.hikeapplication.Observation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.Hike.Hike;
import com.example.hikeapplication.Hike.HikeAdapter;
import com.example.hikeapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObservationsActivity extends AppCompatActivity {
    private Button add_btn;
    private RecyclerView recyclerView;
    private int hike_id;
    ConnectDb db;
    List<Observation> observationList = new ArrayList<>();
    ObservationAdapter observationAdapter;

    ArrayList<String> id, observation, time, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        assert ab != null;
        ab.setTitle("Observations");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations);



        Intent intent = getIntent();
        int hike_id = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("hike_id")));

        recyclerView = findViewById(R.id.recyclerView);
        add_btn = findViewById(R.id.add_button);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ObservationsActivity.this, AddObservationsActivity.class);
                intent.putExtra("hike_id", String.valueOf(hike_id));
                startActivity(intent);
            }
        });
        db = new ConnectDb(ObservationsActivity.this);
        displayObservation();
        observationAdapter = new ObservationAdapter(ObservationsActivity.this, this, observationList);
        recyclerView.setAdapter(observationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ObservationsActivity.this));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void displayObservation() {
        Intent intent = getIntent();
        hike_id = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("hike_id")));
        observationList.clear();
        observationList = db.getObservation(hike_id);
        if (observationList.size() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}