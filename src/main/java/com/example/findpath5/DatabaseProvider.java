package com.example.findpath5;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {
    public static final String AUTHORITY="com.example.findpath5.provider";
    private static UriMatcher uriMatcher;
    private MyDatabaseHelper dbHelper;

    static{

        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"wifi",0);
        uriMatcher.addURI(AUTHORITY,"wifi/#",1);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int deleteRows=0;
        switch (uriMatcher.match(uri)){
            case 0:
                deleteRows=db.delete("WiFi",selection,selectionArgs);
                break;
            case 1:
                String studentId=uri.getPathSegments().get(1);
                deleteRows=db.delete("WiFi","id=?",new String[]{studentId});
                break;
            default:
                break;
        }
        return deleteRows;
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        //content://com.example.app.provider/table/#
        //This method is used to get the MIME type of the Uri object
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)){
            case 0:
                return "vnd.android.cursor.dir/vnd.com.example.databasetest.provider.wifi";
            case 1:
                return "vnd.android.cursor.item/vnd.com.example.databasetest.provider.wifi";
        }
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Uri uriReturn=null;
        switch (uriMatcher.match(uri)){
            case 0:
            case 1:
                long newStudentId=db.insert("WiFi",null,values);
                uriReturn=Uri.parse("content://"+AUTHORITY+"/wifi/"+newStudentId);
                break;
            default:
                break;
        }
        //throw new UnsupportedOperationException("Not yet implemented");
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbHelper=new MyDatabaseHelper(getContext(),"WiFiStore.db",null,1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=null;
        switch (uriMatcher.match(uri)){
            case  0:
                cursor=db.query("WiFi",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case  1:
                String studentId=uri.getPathSegments().get(1);
                cursor=db.query("WiFi",projection,"id=?",new String[]{studentId},null,null,sortOrder);
                break;
            default:
                break;
        }
        //throw new UnsupportedOperationException("Not yet implemented");
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int updateRows=0;
        switch(uriMatcher.match(uri))
        {
            case 0:
                updateRows=db.update("WiFi",values,selection,selectionArgs);
                break;
            case 1:
                String studentId=uri.getPathSegments().get(1);
                updateRows=db.update("WiFi",values,"id=?",new String[]{studentId});
                break;
            default:
                break;
        }
        return updateRows;
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
