package de.mytfg.jufo.ibis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RoutingDatabase {
    // Log TAG
    protected static final String TAG = "RoutingDatabase-class";
    public final String DBNAME = "RoutingDatabase";
    public final int DBVERSION = 8;
    public final String COLUMN_ID = "Id";
    public final String COLUMN_LAT = "latitude";
    public final String COLUMN_LON = "longitude";
    public final String COLUMN_DIST = "distance"; ///distance to last point in meters
    public final String COLUMN_TIMEFACTOR = "time_factor";
    public final String TABLENAME = "RoutingData";
    public final String CREATERDB = "CREATE TABLE " + TABLENAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_LAT + " REAL NOT NULL, " +
            COLUMN_LON + " REAL NOT NULL, " +
            COLUMN_DIST + " REAL," +
            COLUMN_TIMEFACTOR + " REAL " +
            ");";
    public SQLiteDatabase db;
    private Context context;
    //database variables
    private DbHelper dbHelper;

    //constructor
    public RoutingDatabase(Context context) {
        Log.i(TAG, "GPSDatabase Constructor");
        this.context = context;
        dbHelper = new DbHelper(context);
    }

    public void deleteData() {
        db.delete(TABLENAME, null, null);
    }

    public double getTotalDist() {
        double tdist;
        Cursor mCount = db.rawQuery("SELECT SUM(" + COLUMN_DIST + ") FROM " + TABLENAME, null);
        mCount.moveToFirst();
        tdist = mCount.getDouble(0);
        mCount.close();
        return tdist;
    }

    public double getTotalDistTimeFactored() {
        double tdist;
        Cursor mCount = db.rawQuery("SELECT SUM(" + COLUMN_DIST + " * " + COLUMN_TIMEFACTOR + ") FROM " + TABLENAME, null);
        mCount.moveToFirst();
        tdist = mCount.getDouble(0);
        mCount.close();
        return tdist;
    }

    public void readPointsArray(JSONArray jArray) {
        Location oldLocation = new Location("");
        double dist;
        for (int i = 0; i < jArray.length(); i++) {
            try {
                Location location = new Location("");
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                double lat = oneObject.getDouble("lat");
                double lon = oneObject.getDouble("lon");
                double tf;
                if (oneObject.has("time_factor")) {
                    tf = oneObject.getDouble("time_factor");
                } else {
                    tf = 1d;
                }
                location.setLatitude(lat);
                location.setLongitude(lon);
                if (i != 0) {
                    dist = location.distanceTo(oldLocation);
                } else {
                    dist = 0;
                }
                //insert into db
                insertData(lat, lon, dist, tf);
                oldLocation = location;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public long insertData(double lat, double lon, double dist, double tf) {
        ContentValues value = new ContentValues();
        value.put(COLUMN_LAT, lat);
        value.put(COLUMN_LON, lon);
        value.put(COLUMN_DIST, dist);
        value.put(COLUMN_TIMEFACTOR, tf);
        return db.insert(TABLENAME, null, value);
    }

    public JSONArray getAllPoints() {
        Cursor cursor = db.query(TABLENAME, new String[]{COLUMN_LAT, COLUMN_LON}, null, null, null, null, null);
        JSONArray data = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            JSONObject point = new JSONObject();
            try {
                point.put("lat", cursor.getDouble(0));
                point.put("lon", cursor.getDouble(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data.put(point);
            cursor.moveToNext();
        }
        return data;
    }

    public void open() throws SQLException {
        Log.i(TAG, "open()");
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.i(TAG, "close()");
        dbHelper.close();
    }

    public boolean deleteDatabase() {
        Log.i(TAG, "database deleted");
        //delete database
        return context.deleteDatabase(DBNAME);
    }

    //creating a DbHelper
    public class DbHelper extends SQLiteOpenHelper {
        //DbHelper constructor
        public DbHelper(Context context) {
            super(context, DBNAME, null, DBVERSION);
            Log.i(TAG, DBVERSION + "");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "onCreate()");
            db.execSQL(CREATERDB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLENAME + ";");
            onCreate(db);
        }
    }
}