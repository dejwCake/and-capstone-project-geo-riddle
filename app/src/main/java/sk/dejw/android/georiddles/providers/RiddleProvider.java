package sk.dejw.android.georiddles.providers;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RiddleProvider extends ContentProvider {

    public static final int CODE = 100;
    public static final int CODE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private GeoRiddlesDbHelper mGeoRiddlesDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RiddleContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RiddleContract.PATH, CODE);

        matcher.addURI(authority, RiddleContract.PATH + "/#", CODE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mGeoRiddlesDbHelper = new GeoRiddlesDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @NonNull ContentValues value) {
        final SQLiteDatabase db = mGeoRiddlesDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CODE:
                long id = db.insert(RiddleContract.Entry.TABLE_NAME, null, value);
                if (id > 0) {
                    returnUri = RiddleContract.Entry.withId(id);
                } else {
                    throw new SQLException("Failed to insert row into: " + uri);
                }
                break;
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mGeoRiddlesDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE:
                cursor = db.query(
                        RiddleContract.Entry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_WITH_ID:
                String[] selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(
                        RiddleContract.Entry.TABLE_NAME,
                        projection,
                        RiddleContract.Entry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mGeoRiddlesDbHelper.getWritableDatabase();
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE:
                numRowsDeleted = db.delete(
                        RiddleContract.Entry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_WITH_ID:
                String[] selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numRowsDeleted = db.delete(RiddleContract.Entry.TABLE_NAME,
                        RiddleContract.Entry._ID + " = ?",
                        selectionArguments);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mGeoRiddlesDbHelper.getWritableDatabase();
        int numUpdated = 0;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null values");
        }

        switch (sUriMatcher.match(uri)) {
            case CODE:
                numUpdated = db.update(RiddleContract.Entry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_WITH_ID:
                String[] selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numUpdated = db.update(RiddleContract.Entry.TABLE_NAME,
                        values,
                        RiddleContract.Entry._ID + " = ?",
                        selectionArguments);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType.");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mGeoRiddlesDbHelper.close();
        super.shutdown();
    }
}