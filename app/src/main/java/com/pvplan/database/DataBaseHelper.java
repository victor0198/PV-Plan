package com.pvplan.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jjoe64.graphview.series.DataPoint;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;


public class DataBaseHelper extends SQLiteOpenHelper {
    Context creatorContext;
    public final String PROJECTS_TABLE = "projects";
    public final String PROJECTS_ID = "id";
    public final String PROJECTS_NAME = "name";
    public final String PROJECTS_POWER = "power";
    public final String PROJECTS_LAT = "latitude";
    public final String PROJECTS_LON = "longitude";
    public final String PROJECTS_SLOPE = "slope";
    public final String PROJECTS_AZIMUTH = "azimuth";

    public final String MONTHLY_DATA_TABLE = "monthly_data";
    public final String MONTHLY_DATA_ID = "project_id";
    public final String MONTHLY_DATA_MONTH = "month";
    public final String MONTHLY_DATA_W_DAY = "W_day";
    public final String MONTHLY_DATA_W_MONTH = "W_month";

    public final String TMY_TABLE = "tmy";
    public final String TMY_ID = "project_id";
    public final String TMY_MONTH = "month";
    public final String TMY_YEAR = "year";


    public final String CONSUMPTION_TABLE = "consumption";
    public final String CONSUMPTION_PROJECT_ID = "project_id";
    public final String CONSUMPTION_VALUE = "value";
    public final String CONSUMPTION_ENERGY_UNIT = "energy_unit";
    public final String CONSUMPTION_TIME_UNIT = "time_unit";
    public final String CONSUMPTION_MONTH = "month";
    public final String CONSUMPTION_PROFILE_ID = "profile_id";

    public final String PROFILE_TABLE = "profile";
    public final String PROFILE_PROFILE_ID = "profile_id";
    public final String PROFILE_NAME = "name";

    public final String D_PROFILE_TABLE = "daily_profile";
    public final String D_PROFILE_PROFILE_ID = "profile_id";
    public final String D_PROFILE_HOUR = "hour";
    public final String D_PROFILE_VALUE = "value";

    public final String M_PROFILE_TABLE = "monthly_profile";
    public final String M_PROFILE_PROFILE_ID = "profile_id";
    public final String M_PROFILE_MONTH = "month";
    public final String M_PROFILE_VALUE = "value";

    public final String OPTIMAL_TABLE = "optimal";
    public final String OPTIMAL_ID = "project_id";
    public final String OPTIMAL_P_POWER = "P_power";
    public final String OPTIMAL_S_POWER = "S_power";
    public final String OPTIMAL_SLOPE = "slope";
    public final String OPTIMAL_AZIMUTH = "azimuth";

    public final String M_B_TABLE = "battery_monthly";
    public final String M_B_ID = "project_id";
    public final String M_B_MONTH = "month";
    public final String M_B_PRODUCTION = "production_W_d";
    public final String M_B_NOT_CAPTURED = "not_captured_W_d";
    public final String M_B_FULL = "battery_full";
    public final String M_B_EMPTY = "battery_empty";


    public DataBaseHelper(@Nullable Context context) {
        super(context, "projects.db", null, 1); // Environment.getExternalStorageDirectory()+"/PV/database/
        creatorContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProjectsTable = "CREATE TABLE " + PROJECTS_TABLE +
                " (" + PROJECTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PROJECTS_NAME + " TEXT, " +
                PROJECTS_POWER + " INTEGER, " +
                PROJECTS_LAT + " REAL, " +
                PROJECTS_LON + " REAL, " +
                PROJECTS_SLOPE + " REAL, " +
                PROJECTS_AZIMUTH + " REAL " +
                ");";
        db.execSQL(createProjectsTable);

        String createMonthlyDataTable = "CREATE TABLE " + MONTHLY_DATA_TABLE +
                " (" + MONTHLY_DATA_ID + " INTEGER, " +
                MONTHLY_DATA_MONTH + " INTEGER, " +
                MONTHLY_DATA_W_DAY + " REAL, " +
                MONTHLY_DATA_W_MONTH + " REAL " +
                ");";
        db.execSQL(createMonthlyDataTable);

        String createTMYTable = "CREATE TABLE " + TMY_TABLE +
                " (" + TMY_ID + " INTEGER, " +
                TMY_MONTH + " INTEGER, " +
                TMY_YEAR + " INTEGER " +
                ");";
        db.execSQL(createTMYTable);

        String createConsumptionTable = "CREATE TABLE " + CONSUMPTION_TABLE +
                " (" + CONSUMPTION_PROJECT_ID + " INTEGER, " +
                CONSUMPTION_VALUE + " REAL, " +
                CONSUMPTION_ENERGY_UNIT + " TEXT, " +
                CONSUMPTION_TIME_UNIT + " TEXT, " +
                CONSUMPTION_MONTH + " INTEGER, " +
                CONSUMPTION_PROFILE_ID + " INTEGER " +
                ");";
        db.execSQL(createConsumptionTable);

        String createProfileTable = "CREATE TABLE " + PROFILE_TABLE +
                " (" + PROFILE_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PROFILE_NAME + " TEXT " +
                ");";
        db.execSQL(createProfileTable);

        String createDailyProfileTable = "CREATE TABLE " + D_PROFILE_TABLE +
                " (" + D_PROFILE_PROFILE_ID + " INTEGER, " +
                D_PROFILE_HOUR + " INTEGER, " +
                D_PROFILE_VALUE + " REAL " +
                ");";
        db.execSQL(createDailyProfileTable);

        String createMonthlyProfileTable = "CREATE TABLE " + M_PROFILE_TABLE +
                " (" + M_PROFILE_PROFILE_ID + " INTEGER, " +
                M_PROFILE_MONTH + " INTEGER, " +
                M_PROFILE_VALUE + " REAL " +
                ");";
        db.execSQL(createMonthlyProfileTable);

        String createOptimalTable = "CREATE TABLE " + OPTIMAL_TABLE +
                " (" + OPTIMAL_ID + " INTEGER, " +
                OPTIMAL_P_POWER + " REAL, " +
                OPTIMAL_S_POWER + " REAL, " +
                OPTIMAL_SLOPE + " REAL, " +
                OPTIMAL_AZIMUTH + " REAL " +
                ");";
        db.execSQL(createOptimalTable);

        String createMBTable = "CREATE TABLE " + M_B_TABLE +
                " (" + M_B_ID + " INTEGER, " +
                M_B_MONTH + " INTEGER, " +
                M_B_PRODUCTION + " REAL, " +
                M_B_NOT_CAPTURED + " REAL, " +
                M_B_FULL + " REAL, " +
                M_B_EMPTY + " REAL " +
                ");";
        db.execSQL(createMBTable);

        // add average household consumption profile
        ContentValues cvProfile = new ContentValues();
        cvProfile.put(PROFILE_NAME, "average household");
        db.insert(PROFILE_TABLE, null, cvProfile);

        // add average household daily profile
        ArrayList<Float> profileDaily = new ArrayList<Float>(
                Arrays.asList(0.0505f,0.0350f,0.0270f,0.0250f,0.0245f,0.0265f,0.0345f,0.0295f,
                        0.0230f,0.0180f,0.0180f,0.0205f,0.0265f,0.0350f,0.0415f,0.0505f,
                        0.0455f,0.0340f,0.0250f,0.0300f,0.0650f,0.1095f,0.1175f,0.0880f));
        int hour = 0;
        for (Float value : profileDaily) {
            ContentValues cvDaily = new ContentValues();
            cvDaily.put(D_PROFILE_PROFILE_ID, 1);
            cvDaily.put(D_PROFILE_HOUR, hour);
            cvDaily.put(D_PROFILE_VALUE, value);
            db.insert(D_PROFILE_TABLE, null, cvDaily);
            hour += 1;
        }

        // add average household monthly profile
        ArrayList<Float> profileMonthly = new ArrayList<Float>(
                Arrays.asList(201.46f,
                        156f,
                        181.38f,
                        136.48f,
                        148.98f,
                        140.75f,
                        184.63f,
                        155.2f,
                        140.75f,
                        174.23f,
                        170.83f,
                        197.22f));
        int month = 1;
        for (Float value :
                profileMonthly) {
            ContentValues cvMonthly = new ContentValues();
            cvMonthly.put(M_PROFILE_PROFILE_ID, 1);
            cvMonthly.put(M_PROFILE_MONTH, month);
            cvMonthly.put(M_PROFILE_VALUE, value);
            db.insert(M_PROFILE_TABLE, null, cvMonthly);
            month += 1;
        }


        // add average household consumption profile
        ContentValues cvProfile2 = new ContentValues();
        cvProfile.put(PROFILE_NAME, "constant");
        db.insert(PROFILE_TABLE, null, cvProfile);

        // add average household daily profile
        ArrayList<Float> profileDaily2 = new ArrayList<Float>(
                Arrays.asList(0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,
                        0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,
                        0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f,0.041665f));
        hour = 0;
        for (Float value : profileDaily2) {
            ContentValues cvDaily = new ContentValues();
            cvDaily.put(D_PROFILE_PROFILE_ID, 2);
            cvDaily.put(D_PROFILE_HOUR, hour);
            cvDaily.put(D_PROFILE_VALUE, value);
            db.insert(D_PROFILE_TABLE, null, cvDaily);
            hour += 1;
        }

        // add average household monthly profile
        ArrayList<Float> profileMonthly2 = new ArrayList<Float>(
                Arrays.asList(180f,
                        180f,
                        180f,
                        180f,
                        180f,
                        180f,
                        180f,
                        180f,
                        180f,
                        180f,
                        180f,
                        180f));
        month = 1;
        for (Float value :
                profileMonthly2) {
            ContentValues cvMonthly = new ContentValues();
            cvMonthly.put(M_PROFILE_PROFILE_ID, 2);
            cvMonthly.put(M_PROFILE_MONTH, month);
            cvMonthly.put(M_PROFILE_VALUE, value);
            db.insert(M_PROFILE_TABLE, null, cvMonthly);
            month += 1;
        }

        //Toast.makeText(creatorContext, "Tables created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addProject(Context ctx, String projectName, Double power) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(PROJECTS_NAME, projectName);
        cv.put(PROJECTS_POWER, power);
        cv.put(PROJECTS_LAT, "-181.0");
        cv.put(PROJECTS_LON, "-181.0");
        cv.put(PROJECTS_SLOPE, "-1.0");
        cv.put(PROJECTS_AZIMUTH, "-181.0");

        long insert = db.insert(PROJECTS_TABLE, null, cv);

        db.close();

        if (insert == -1) {
            Toast.makeText(ctx, "Project data was not added.", Toast.LENGTH_LONG).show();
        }

        return insert != -1;
    }

    public ArrayList<ProjectModel> getProjectsList() {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ArrayList<ProjectModel> projectsList = new ArrayList<>();
        String getProjectsQuery = "SELECT * FROM " + PROJECTS_TABLE;
        Cursor cursor = dbRead.rawQuery(getProjectsQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int projectId = cursor.getInt(0);
                String name = cursor.getString(1);
                Double power = cursor.getDouble(2);
                String latitude = cursor.getString(3);
                String longitude = cursor.getString(4);
                String slope = cursor.getString(5);
                String azimuth = cursor.getString(6);
                ProjectModel credential = new ProjectModel(
                        projectId,
                        name,
                        power,
                        latitude,
                        longitude,
                        slope,
                        azimuth
                );
                projectsList.add(credential);
            } while (cursor.moveToNext());

        }

        cursor.close();
        dbRead.close();

        return projectsList;
    }

    public ProjectModel getProjectInfoById(int id) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getProjectQuery = "SELECT * FROM " + PROJECTS_TABLE + " WHERE " + PROJECTS_ID + "=" + String.valueOf(id);
        Cursor cursor = dbRead.rawQuery(getProjectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int projectId = cursor.getInt(0);
                String name = cursor.getString(1);
                Double power = cursor.getDouble(2);
                String latitude = cursor.getString(3);
                String longitude = cursor.getString(4);
                String slope = cursor.getString(5);
                String azimuth = cursor.getString(6);
                ProjectModel project = new ProjectModel(
                        projectId,
                        name,
                        power,
                        latitude,
                        longitude,
                        slope,
                        azimuth
                );

                cursor.close();
                dbRead.close();
                return project;
            } while (cursor.moveToNext());

        }

        return new ProjectModel(-1, "No project", 0d, Long.toString((long) -181.0), Long.toString((long) -181.0), Long.toString((long) -1.0), Long.toString((long) -181.0));
    }


    public boolean setProjectLocation(int id, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        String setLatQuery = "UPDATE " + PROJECTS_TABLE +
                " SET " + PROJECTS_LAT + " = " + latitude +
                " WHERE " + PROJECTS_TABLE +  "." + PROJECTS_ID + " = " + id;
        db.execSQL(setLatQuery);
        Log.d("In DB ->>>", setLatQuery);

        String setLonQuery = "UPDATE " + PROJECTS_TABLE +
                " SET " + PROJECTS_LON + " = " + longitude +
                " WHERE " + PROJECTS_TABLE +  "." + PROJECTS_ID + " = " + id;
        db.execSQL(setLonQuery);

        db.close();

        return true;
    }

    public boolean addOptimalAngles(int projectId, double slope, double azimuth) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(OPTIMAL_ID, projectId);
        cv.put(OPTIMAL_P_POWER, Double.valueOf(0).toString());
        cv.put(OPTIMAL_S_POWER, Double.valueOf(0).toString());
        cv.put(OPTIMAL_SLOPE, Double.valueOf(slope).toString());
        cv.put(OPTIMAL_AZIMUTH, Double.valueOf(azimuth).toString());

        long insert = db.insert(OPTIMAL_TABLE, null, cv);

        db.close();

        return insert != -1;
    }

    public boolean updateOptimalPower(int projectId, double power) {
        SQLiteDatabase db = this.getWritableDatabase();
        String setLatQuery = "UPDATE " + OPTIMAL_TABLE +
                " SET " + OPTIMAL_P_POWER + " = " + power +
                " WHERE " + OPTIMAL_TABLE +  "." + OPTIMAL_ID + " = " + projectId;
        db.execSQL(setLatQuery);

        db.close();

        return true;
    }

    public boolean updateOptimalStorage(int projectId, double storage) {
        SQLiteDatabase db = this.getWritableDatabase();
        String setLatQuery = "UPDATE " + OPTIMAL_TABLE +
                " SET " + OPTIMAL_S_POWER + " = " + storage +
                " WHERE " + OPTIMAL_TABLE +  "." + OPTIMAL_ID + " = " + projectId;
        db.execSQL(setLatQuery);

        db.close();

        return true;
    }

    public OptimalModel getOptimalById(int id) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getProjectQuery = "SELECT * FROM " + OPTIMAL_TABLE + " WHERE " + OPTIMAL_ID + "=" + id;
        Cursor cursor = dbRead.rawQuery(getProjectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int projectId = cursor.getInt(0);
                String P_power = cursor.getString(1);
                String S_power = cursor.getString(2);
                String slope = cursor.getString(3);
                String azimuth = cursor.getString(4);
                OptimalModel optimal = new OptimalModel(
                    projectId,
                    P_power,
                    S_power,
                    slope,
                    azimuth
                );

                cursor.close();
                dbRead.close();
                return optimal;
            } while (cursor.moveToNext());

        }

        return new OptimalModel(-1, "0", "0", "0", "0");
    }

    public boolean setArraySlope(int id, double slope) {
        SQLiteDatabase db = this.getWritableDatabase();
        String setLatQuery = "UPDATE " + PROJECTS_TABLE +
                " SET " + PROJECTS_SLOPE + " = " + slope +
                " WHERE " + PROJECTS_TABLE +  "." + PROJECTS_ID + " = " + id;
        db.execSQL(setLatQuery);

        db.close();

        return true;
    }

    public boolean setArrayAzimuth(int id, double azimuth) {
        SQLiteDatabase db = this.getWritableDatabase();
        String setLatQuery = "UPDATE " + PROJECTS_TABLE +
                " SET " + PROJECTS_AZIMUTH + " = " + azimuth +
                " WHERE " + PROJECTS_TABLE +  "." + PROJECTS_ID + " = " + id;
        db.execSQL(setLatQuery);

        db.close();

        return true;
    }

    public boolean monthlyIsAvailable(int id) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getProjectQuery = "SELECT COUNT(*) FROM " + MONTHLY_DATA_TABLE + " WHERE " + MONTHLY_DATA_ID + "=" + String.valueOf(id);
        Cursor cursor = dbRead.rawQuery(getProjectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int recordsNo = cursor.getInt(0);
                Log.d("Monthly data - recordsNo", String.valueOf(recordsNo));
                boolean ok = false;
                if(recordsNo == 12)
                    ok = true;

                cursor.close();
                dbRead.close();

                if(ok){
                    return true;
                }
            } while (cursor.moveToNext());

        }

        return false;
    }

    public boolean tmyIsAvailable(int id) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getProjectQuery = "SELECT COUNT(*) FROM " + TMY_TABLE + " WHERE " + TMY_ID + "=" + String.valueOf(id);
        Cursor cursor = dbRead.rawQuery(getProjectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int recordsNo = cursor.getInt(0);
                Log.d("TMY - recordsNo", String.valueOf(recordsNo));
                boolean ok = false;
                if(recordsNo == 12)
                    ok = true;

                cursor.close();
                dbRead.close();

                if(ok){
                    return true;
                }
            } while (cursor.moveToNext());

        }

        return false;
    }

    public boolean addMonthlyRecord(Context ctx, int projectId, int month, double W_day, double W_month) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(MONTHLY_DATA_ID, projectId);
        cv.put(MONTHLY_DATA_MONTH, month);
        cv.put(MONTHLY_DATA_W_DAY, W_day);
        cv.put(MONTHLY_DATA_W_MONTH, W_month);

        long insert = db.insert(MONTHLY_DATA_TABLE, null, cv);

        db.close();
        if (insert == -1) {
            Toast.makeText(ctx, "Month data was not added.", Toast.LENGTH_LONG).show();
        }

        return insert != -1;
    }

    public boolean addTMYRecord(Context ctx, int projectId, int month, double year) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TMY_ID, projectId);
        cv.put(TMY_MONTH, month);
        cv.put(TMY_YEAR, year);

        long insert = db.insert(TMY_TABLE, null, cv);

        db.close();
        if (insert == -1) {
            Toast.makeText(ctx, "Month data was not added.", Toast.LENGTH_LONG).show();
        }

        return insert != -1;
    }

    public boolean deleteMonthly(int projectId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getMonthlyQuery = "DELETE FROM " + MONTHLY_DATA_TABLE +
                " WHERE " + MONTHLY_DATA_ID + " = " + projectId;
        Cursor cursor = dbRead.rawQuery(getMonthlyQuery, null);

        Integer result = cursor.getCount();
        Log.d(DataBaseHelper.class.toString(), "Credentials delete result:" + result);

        cursor.close();
        dbRead.close();

        return result != -1;
    }

    public boolean deleteTMY(int projectId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getTMYQuery = "DELETE FROM " + TMY_TABLE +
                " WHERE " + TMY_ID + " = " + projectId;
        Cursor cursor = dbRead.rawQuery(getTMYQuery, null);

        Integer result = cursor.getCount();
        Log.d(DataBaseHelper.class.toString(), "Credentials delete result:" + result);

        cursor.close();
        dbRead.close();

        return result != -1;
    }

    public ConsumptionModel getConsumption(int projectId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getProjectQuery = "SELECT * FROM " + CONSUMPTION_TABLE + " WHERE " + CONSUMPTION_PROJECT_ID + "=" + projectId;
        Cursor cursor = dbRead.rawQuery(getProjectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                Double value = cursor.getDouble(1);
                String energyUnit = cursor.getString(2);
                String timeUnit = cursor.getString(3);
                Integer month = cursor.getInt(4);
                Integer profileId = cursor.getInt(5);
                ConsumptionModel optimal = new ConsumptionModel(
                        id,
                        value,
                        energyUnit,
                        timeUnit,
                        month,
                        profileId
                );

                cursor.close();
                dbRead.close();
                return optimal;
            } while (cursor.moveToNext());

        }

        return new ConsumptionModel(1, 2000d, "KW", "year", 1, 1);
    }

    // getConsumptionData (int project_id)
    // return profile_id

    // getProfileName (int profile_id)
    // return name

    public ArrayList<Float> getDailyProfile(int profile_id) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ArrayList<Float> dailyProfile = new ArrayList<>();
        String getDailyQuery = "SELECT * FROM " + D_PROFILE_TABLE + " WHERE " + D_PROFILE_PROFILE_ID + " = " + profile_id;
        Cursor cursor = dbRead.rawQuery(getDailyQuery, null);

        if (cursor.getCount() == 24){
            if (cursor.moveToFirst()) {
                do {
                    float value = cursor.getFloat(2);
                    Log.d("Daily profile data","Got:"+value);
                    dailyProfile.add(value);
                } while (cursor.moveToNext());
            }

        }else{
            Log.d("Daily profile data", "Not 24 records");
        }

        cursor.close();
        dbRead.close();

        return dailyProfile;
    }

    public ArrayList<Float> getMonthlyProfile(int profile_id) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ArrayList<Float> dailyProfile = new ArrayList<>();
        String getMonthlyQuery = "SELECT * FROM " + M_PROFILE_TABLE + " WHERE " + M_PROFILE_PROFILE_ID + " = " + profile_id;
        Cursor cursor = dbRead.rawQuery(getMonthlyQuery, null);

        if (cursor.getCount() == 12){
            if (cursor.moveToFirst()) {
                do {
                    float value = cursor.getFloat(2);
                    Log.d("Monthly profile data","Got:"+value);
                    dailyProfile.add(value);
                } while (cursor.moveToNext());
            }

        }else{
            Log.d("Monthly profile data", "Not 12 records");
        }

        cursor.close();
        dbRead.close();

        return dailyProfile;
    }

    public boolean applyConsumption(Context ctx, int project_id, float value, String energy_unit, String time_unit, int month, int profile_id) {
        SQLiteDatabase dbRead = this.getWritableDatabase();
        String getConsumptionQuery = "DELETE FROM " + CONSUMPTION_TABLE +
                " WHERE " + CONSUMPTION_PROJECT_ID + " = " + project_id;
        Cursor cursor = dbRead.rawQuery(getConsumptionQuery, null);

        Integer result = cursor.getCount();
        Log.d(DataBaseHelper.class.toString(), "Credentials delete result:" + result);

        cursor.close();
        dbRead.close();


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CONSUMPTION_PROJECT_ID, project_id);
        cv.put(CONSUMPTION_VALUE, value);
        cv.put(CONSUMPTION_ENERGY_UNIT, energy_unit);
        cv.put(CONSUMPTION_TIME_UNIT, time_unit);
        cv.put(CONSUMPTION_MONTH, month);
        cv.put(CONSUMPTION_PROFILE_ID, profile_id);

        long insert = db.insert(CONSUMPTION_TABLE, null, cv);

        db.close();

        if (insert == -1) {
            Toast.makeText(ctx, "Project data was not added.", Toast.LENGTH_LONG).show();
        }

        return insert != -1;
    }

    public ArrayList<ProfileModel> getProfilesList() {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ArrayList<ProfileModel> profileList = new ArrayList<>();
        String getProfilesQuery = "SELECT * FROM " + PROFILE_TABLE;
        Cursor cursor = dbRead.rawQuery(getProfilesQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int projectId = cursor.getInt(0);
                String name = cursor.getString(1);
                ProfileModel credential = new ProfileModel(
                        projectId,
                        name
                );
                profileList.add(credential);
            } while (cursor.moveToNext());

        }

        cursor.close();
        dbRead.close();

        return profileList;
    }

    public ArrayList<Float> getMonthlyData(int projectId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ArrayList<Float> data = new ArrayList<>();
        String getMonthlyDataQuery = "SELECT * FROM " + MONTHLY_DATA_TABLE + " WHERE " + MONTHLY_DATA_ID + " = " + projectId;
        Cursor cursor = dbRead.rawQuery(getMonthlyDataQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Float W_month = cursor.getFloat(3);
                data.add(W_month);
            } while (cursor.moveToNext());

        }

        cursor.close();
        dbRead.close();

        return data;
    }

    public ArrayList<MonthlyBatteryModel> getBatteryInfo(int projectId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ArrayList<MonthlyBatteryModel> data = new ArrayList<>();
        String getMonthlyDataQuery = "SELECT * FROM " + M_B_TABLE + " WHERE " + M_B_ID + " = " + projectId;
        Cursor cursor = dbRead.rawQuery(getMonthlyDataQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MonthlyBatteryModel cmbm = new MonthlyBatteryModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5)
                );
                data.add(cmbm);
            } while (cursor.moveToNext());

        }

        cursor.close();
        dbRead.close();

        return data;
    }

    public boolean setPower(int projectId, double P_power) {
        SQLiteDatabase db = this.getWritableDatabase();
        String setLatQuery = "UPDATE " + PROJECTS_TABLE +
                " SET " + PROJECTS_POWER + " = " + P_power +
                " WHERE " + PROJECTS_TABLE +  "." + PROJECTS_ID + " = " + projectId;
        db.execSQL(setLatQuery);

        db.close();
        return true;
    }

    public boolean applyMonthlyBattery(int project_id, ArrayList<MonthlyBatteryModel> mb) {
        SQLiteDatabase dbRead = this.getWritableDatabase();
        String getConsumptionQuery = "DELETE FROM " + M_B_TABLE +
                " WHERE " + M_B_ID + " = " + project_id;
        Cursor cursor = dbRead.rawQuery(getConsumptionQuery, null);

        Integer result = cursor.getCount();
        Log.d(DataBaseHelper.class.toString(), "MB delete result:" + result);

        cursor.close();
        dbRead.close();


        SQLiteDatabase db = this.getWritableDatabase();
        for (MonthlyBatteryModel amb : mb){
            ContentValues cv = new ContentValues();

            cv.put(M_B_ID, amb.getProjectId());
            cv.put(M_B_MONTH, amb.getMonth());
            cv.put(M_B_PRODUCTION, amb.getProductionW_d());
            cv.put(M_B_NOT_CAPTURED, amb.getNot_capturedW_d());
            cv.put(M_B_FULL, amb.getBatteryFull());
            cv.put(M_B_EMPTY, amb.getBatteryEmpty());
            long insert = db.insert(M_B_TABLE, null, cv);
        }

        db.close();

        return true;
    }

    public Double getLowestPerfD(int projectId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String getProjectQuery = "SELECT * FROM monthly_data WHERE project_id = " + projectId + " ORDER BY W_day ASC";
        Cursor cursor = dbRead.rawQuery(getProjectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Double value = cursor.getDouble(2);
                return value;
            } while (cursor.moveToNext());

        }

        return 1d;
    }


//    public boolean deleteCredential(CredentialsModel credentialModel) {
//        SQLiteDatabase dbRead = this.getReadableDatabase();
//        String getCredentialsQuery = "DELETE FROM " + CREDENTIALS_TABLE +
//                " WHERE " + COLUMN_CREDENTIAL_ID + " = " + credentialModel.getCredentialId();
//        Cursor cursor = dbRead.rawQuery(getCredentialsQuery, null);
//
//        Integer result = cursor.getCount();
//        Log.d(DataBaseHelper.class.toString(), "Credentials delete result:" + result);
//
//        cursor.close();
//        dbRead.close();
//
//        return result != -1;
//    }
//
//    public Integer getFriendshipIdByFriendId(Integer friendId) {
//        SQLiteDatabase dbRead = this.getReadableDatabase();
//        String getCredentialsQuery = "SELECT * FROM " + FRIENDSHIP_KEYS_TABLE +
//                " WHERE " + COLUMN_FRIEND_ID + " = " + friendId;
//        Cursor cursor = dbRead.rawQuery(getCredentialsQuery, null);
//
//        Integer friendshipId = null;
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            friendshipId = cursor.getInt(0);
//        }
//
//        cursor.close();
//        dbRead.close();
//
//        Log.d(DataBaseHelper.class.toString(), "Extracted private key:".concat(friendshipId.toString()));
//
//        return friendshipId;
//    }
}