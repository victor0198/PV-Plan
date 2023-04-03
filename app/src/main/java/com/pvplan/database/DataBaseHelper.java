package com.pvplan.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.PreparedStatement;
import java.util.ArrayList;


public class DataBaseHelper extends SQLiteOpenHelper {
    Context creatorContext;
    public final String PROJECTS_TABLE = "projects";
    public final String PROJECTS_ID = "id";
    public final String PROJECTS_NAME = "name";
    public final String PROJECTS_POWER = "power";
    public final String PROJECTS_LAT = "latitude";
    public final String PROJECTS_LON = "longitude";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "projects.db", null, 1); // Environment.getExternalStorageDirectory()+"/PV/database/
        creatorContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProjectsTable = "CREATE TABLE " + PROJECTS_TABLE +
                " (" + PROJECTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PROJECTS_NAME + " TEXT, " +
                PROJECTS_POWER + " INTEGER," +
                PROJECTS_LAT + " REAL, " +
                PROJECTS_LON + " REAL " +
                ");";
        db.execSQL(createProjectsTable);

        Toast.makeText(creatorContext, "Tables created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addProject(Context ctx, String projectName, Integer power) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(PROJECTS_NAME, projectName);
        cv.put(PROJECTS_POWER, power);
        cv.put(PROJECTS_LAT, "-1");
        cv.put(PROJECTS_LON, "-1");

        long insert = db.insert(PROJECTS_TABLE, null, cv);

        db.close();

        if (insert == -1) {
            Toast.makeText(ctx, "Project data was not added.", Toast.LENGTH_LONG).show();
        }

        return insert != -1;
    }
//
//    public boolean addCredential(Context ctx, CredentialsModel credentialModel) {
//        // check database for already existing credentials
//        SQLiteDatabase dbRead = this.getReadableDatabase();
//        String getCredentialsQuery = "SELECT * FROM " + CREDENTIALS_TABLE +
//                " WHERE " + COLUMN_CREDENTIAL_SERVICE + " LIKE \"" + credentialModel.getService() + "\"" +
//                " AND " + COLUMN_CREDENTIAL_LOGIN + " LIKE \"" + credentialModel.getLogin() + "\"" +
//                " AND " + COLUMN_CREDENTIAL_PASSWORD + " LIKE \"" + credentialModel.getPassword() + "\"";
//        Cursor cursor = dbRead.rawQuery(getCredentialsQuery, null);
//
//        if (cursor.getCount() > 0) {
//
//            Toast.makeText(ctx, "These credentials are already registered.", Toast.LENGTH_LONG).show();
//            Log.d(DataBaseHelper.class.toString(), "Credentials already registered.");
//            Log.d(DataBaseHelper.class.toString(), "Cursor:" + cursor.getCount());
//            return false;
//        }
//
//        cursor.close();
//        dbRead.close();
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//
//        cv.put(COLUMN_CREDENTIAL_SERVICE, credentialModel.getService());
//        cv.put(COLUMN_CREDENTIAL_LOGIN, credentialModel.getLogin());
//        cv.put(COLUMN_CREDENTIAL_PASSWORD, credentialModel.getPassword());
//        cv.put(COLUMN_CREDENTIAL_UPLOADED, credentialModel.getUploaded());
//
//        long insert = db.insert(CREDENTIALS_TABLE, null, cv);
//
//        db.close();
//
//        if (insert == -1) {
//            Toast.makeText(ctx, "Credentials were not added.", Toast.LENGTH_LONG).show();
//        }
//
//        Log.d("DB PATH", db.getPath());
//        return insert != -1;
//    }
//
//    public boolean patchCredential(Context ctx, int credentialId, int uploaded) {
//        // check database for already existing credentials
//        SQLiteDatabase db = this.getWritableDatabase();
//        String getCredentialsQuery = "UPDATE " + CREDENTIALS_TABLE +
//                " SET " + COLUMN_CREDENTIAL_UPLOADED + " = " + uploaded +
//                " WHERE " + CREDENTIALS_TABLE +  "." + COLUMN_CREDENTIAL_ID + " = " + credentialId;
//        Cursor cursor = db.rawQuery(getCredentialsQuery, null);
//
//        if (cursor.getCount() > 0) {
//
//            Toast.makeText(ctx, "These credentials are already registered.", Toast.LENGTH_LONG).show();
//            Log.d(DataBaseHelper.class.toString(), "Credentials already registered.");
//            Log.d(DataBaseHelper.class.toString(), "Cursor:" + cursor.getCount());
//            return false;
//        }
//
//        cursor.close();
//        db.close();
//        return true;
//    }
//
//    public Integer getCredentialsId(String service, String login, String password) {
//        SQLiteDatabase dbRead = this.getReadableDatabase();
//        String getCredentialsQuery = "SELECT * FROM " + CREDENTIALS_TABLE +
//                " WHERE " + COLUMN_CREDENTIAL_SERVICE + " LIKE \"" + service + "\"" +
//                " AND " + COLUMN_CREDENTIAL_LOGIN + " LIKE \"" + login + "\"" +
//                " AND " + COLUMN_CREDENTIAL_PASSWORD + " LIKE \"" + password + "\"";
//        Cursor cursor = dbRead.rawQuery(getCredentialsQuery, null);
//
//        Integer credentialsId = 0;
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            credentialsId = cursor.getInt(0);
//        }
//
//        cursor.close();
//        dbRead.close();
//
//        Log.d(DataBaseHelper.class.toString(), "Credentials ID:".concat(credentialsId.toString()));
//
//        return credentialsId;
//    }
//
    public ArrayList<ProjectModel> getProjectsList() {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ArrayList<ProjectModel> projectsList = new ArrayList<>();
        String getCredentialsQuery = "SELECT * FROM " + PROJECTS_TABLE;
        Cursor cursor = dbRead.rawQuery(getCredentialsQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int projectId = cursor.getInt(0);
                String name = cursor.getString(1);
                int power = cursor.getInt(2);
                String latitude = cursor.getString(3);
                String longitude = cursor.getString(4);
                ProjectModel credential = new ProjectModel(
                        projectId,
                        name,
                        power,
                        latitude,
                        longitude
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
                int power = cursor.getInt(2);
                String latitude = cursor.getString(3);
                String longitude = cursor.getString(4);
                ProjectModel project = new ProjectModel(
                        projectId,
                        name,
                        power,
                        latitude,
                        longitude
                );

                cursor.close();
                dbRead.close();
                return project;
            } while (cursor.moveToNext());

        }

        return new ProjectModel(-1, "No project", 0, Long.toString((long) -1.0), Long.toString((long) -1.0));
    }


    public boolean setProjectLocation(int id, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        String setLatQuery = "UPDATE " + PROJECTS_TABLE +
                " SET " + PROJECTS_LAT + " = " + latitude +
                " WHERE " + PROJECTS_TABLE +  "." + PROJECTS_ID + " = " + id;
        db.execSQL(setLatQuery);
        Log.d("In DB ->>>", setLatQuery);
//        Cursor cursor = db.rawQuery(setLatQuery, null);
//        cursor.close();

//        String query = "UPDATE userSettings SET coins=? WHERE user=?";
//        PreparedStatement statement = db.prepareStatement(query);
//        statement.setInt(1, 10);
//        statement.setString(2, lastUser);
//        statement.executeUpdate();

        String setLonQuery = "UPDATE " + PROJECTS_TABLE +
                " SET " + PROJECTS_LON + " = " + longitude +
                " WHERE " + PROJECTS_TABLE +  "." + PROJECTS_ID + " = " + id;
        db.execSQL(setLonQuery);
//        Log.d("In DB ->>>", setLonQuery);
//        cursor = db.rawQuery(setLonQuery, null);
//
//        cursor.close();
        db.close();

        return true;
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