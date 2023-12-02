package bum.keo.heo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myDatabase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "MyTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MY_NUMBER = "myNumber";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MY_NUMBER + " INTEGER)";
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(int myNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MY_NUMBER, myNumber);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        // Nếu dữ liệu được chèn thành công, result sẽ trả về row ID của dòng mới được thêm vào, ngược lại sẽ trả về -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public ArrayList<Integer> getTop10() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COLUMN_MY_NUMBER + " FROM " + TABLE_NAME
                + " ORDER BY " + COLUMN_MY_NUMBER + " DESC LIMIT 10";

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Integer> top10Numbers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                top10Numbers.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return top10Numbers;
    }
}
