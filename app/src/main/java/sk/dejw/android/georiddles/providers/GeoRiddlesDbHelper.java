package sk.dejw.android.georiddles.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sk.dejw.android.georiddles.providers.RiddleContract.Entry;

public class GeoRiddlesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "geo_riddles.db";

    private static final int DATABASE_VERSION = 1;

    public GeoRiddlesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_GAMES_TABLE = "CREATE TABLE " +
                GameContract.Entry.TABLE_NAME + " (" +
                GameContract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameContract.Entry.COLUMN_UUID + " TEXT NOT NULL, " +
                GameContract.Entry.COLUMN_TITLE + " TEXT NOT NULL, " +
                GameContract.Entry.COLUMN_CODE + " TEXT NOT NULL, " +
                GameContract.Entry.COLUMN_GPS_LAT + " REAL NOT NULL, " +
                GameContract.Entry.COLUMN_GPS_LNG + " REAL NOT NULL, " +
                " UNIQUE (" + GameContract.Entry._ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_RIDDLES_TABLE = "CREATE TABLE " +
                Entry.TABLE_NAME + " (" +
                Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Entry.COLUMN_GAME_UUID + " TEXT NOT NULL, " +
                Entry.COLUMN_TITLE + " TEXT NOT NULL, " +
                Entry.COLUMN_NO + " INTEGER NOT NULL, " +
                Entry.COLUMN_GPS_LAT + " REAL NOT NULL, " +
                Entry.COLUMN_GPS_LNG + " REAL NOT NULL, " +
                Entry.COLUMN_IMAGE_PATH + " TEXT NOT NULL, " +
                Entry.COLUMN_QUESTION + " TEXT NOT NULL, " +
                Entry.COLUMN_ANSWER + " TEXT NOT NULL, " +
                Entry.COLUMN_ACTIVE + " INTEGER NOT NULL, " +
                Entry.COLUMN_LOCATION_CHECKED + " INTEGER NOT NULL, " +
                Entry.COLUMN_RIDDLE_SOLVED + " INTEGER NOT NULL, " +
                " UNIQUE (" + Entry._ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_GAMES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RIDDLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GameContract.Entry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Entry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}