package com.example.hikeapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.hikeapplication.Hike.Hike;
import com.example.hikeapplication.Observation.Observation;

import java.util.ArrayList;
import java.util.List;

public class ConnectDb extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "manager.db";

    private static final String TABLE_HIKES = "hikes";
    private static final String TABLE_OBSERVATIONS = "observations";

    public ConnectDb(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String hikes_table = "CREATE TABLE " + TABLE_HIKES + "(hike_id INTEGER primary key autoincrement, name TEXT, location TEXT, date TEXT, parking TEXT, length INTEGER, level TEXT,description TEXT);";
        String observations_table = "CREATE TABLE " + TABLE_OBSERVATIONS + "(observation_id INTEGER primary key autoincrement, hike_id INTEGER, name TEXT, time TEXT, comment Text, foreign key(hike_id) references hikes(hike_id));";
        sqLiteDatabase.execSQL(hikes_table);
        sqLiteDatabase.execSQL(observations_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
        onCreate(sqLiteDatabase);
    }

    public void addHike(String name, String location, String date, String parking, String length, String level, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put("name", name);
        value.put("location", location);
        value.put("date", date);
        value.put("parking", parking);
        value.put("length", length);
        value.put("level", level);
        value.put("description", description);

        long result = db.insert(TABLE_HIKES, null, value);
        if (result == -1) {
            Toast.makeText(context, "Adding new hike failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Adding new hike successful", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Recycle")
    public List<Hike> getHike() {
        List<Hike> list = new ArrayList<>();
        String queryDB = "SELECT hike_id, name, location, date, parking, length, level, description FROM " + TABLE_HIKES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryDB, null);

            while ((cursor != null) && (cursor.moveToNext())) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String location = cursor.getString(2);
                String date = cursor.getString(3);
                String parking = cursor.getString(4);
                String length = cursor.getString(5);
                String level = cursor.getString(6);
                String description = cursor.getString(7);

                list.add(new Hike(id, name, location, date, parking, length, level, description));
            }
        }
        return list;
    }

    public void editHike(String row_id, String name, String location, String date, String parking, String length, String level, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put("name", name);
        value.put("location", location);
        value.put("date", date);
        value.put("parking", parking);
        value.put("length", length);
        value.put("level", level);
        value.put("description", description);

        long result = db.update(TABLE_HIKES, value, "hike_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Edit hike to failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Edit hike successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteHike(String HikeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long resultHike = db.delete(TABLE_HIKES, "hike_id=?", new String[]{HikeId});
        long resultObservation = db.delete(TABLE_OBSERVATIONS, "hike_id=?", new String[]{HikeId});
        if (resultHike == -1 && resultObservation == -1) {
            Toast.makeText(context, "Delete hike to failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete hike successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllHike() {
        SQLiteDatabase db = this.getWritableDatabase();
        long resultHike = db.delete(TABLE_HIKES, null, null);
        long resultObservation = db.delete(TABLE_OBSERVATIONS, null, null);
        if (resultHike == -1 && resultObservation == -1) {
            Toast.makeText(context, "Delete all hike to failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete all hike successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addObservation(String name, int hike_id, String time, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put("hike_id", hike_id);
        value.put("name", name);
        value.put("time", time);
        value.put("comment", comment);

        long result = db.insert(TABLE_OBSERVATIONS, null, value);
        if (result == -1) {
            Toast.makeText(context, "Adding new observation failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Adding new observation successful!", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Observation> getObservation(int hikeId) {
        List<Observation> list = new ArrayList<>();
        String query = "SELECT observation_id, name, time, comment FROM " + TABLE_OBSERVATIONS + " where hike_id = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(hikeId)});

            while ((cursor != null) && (cursor.moveToNext())) {
                int id = cursor.getInt(0);
                String observation = cursor.getString(1);
                String time = cursor.getString(2);
                String comment = cursor.getString(3);

                list.add(new Observation(id, observation, time, comment));
            }
        }
        return list;
    }

    public void editObservation(String observation_id, String name, String time, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("name", name);
        value.put("time", time);
        value.put("comment", comment);

        long result = db.update(TABLE_OBSERVATIONS, value, "observation_id=?", new String[]{observation_id});
        if (result == -1) {
            Toast.makeText(context, "Edit hike to failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Edit hike successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteObservation(String observationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long resultObservation = db.delete(TABLE_OBSERVATIONS, "observation_id=?", new String[]{observationId});
        if (resultObservation == -1) {
            Toast.makeText(context, "Delete observation to failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete observation successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}

