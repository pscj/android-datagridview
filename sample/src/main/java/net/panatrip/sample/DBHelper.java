package net.panatrip.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;

/**
 * Created by huabo on 15/5/8.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sample.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext = null;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            DBUtils.executeSqlScript(mContext,db, "sample.sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
