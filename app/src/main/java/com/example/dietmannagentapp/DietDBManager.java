package com.example.dietmannagentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DietDBManager extends SQLiteOpenHelper {
    static final String DIET_DB = "Diets1.db"; //데베이름
    static final String DIET_TABLE = "testdiet2"; //데베 안의 테이블 이름(테이블 변경시 고려사항 첫번째)
    Context context = null;

    private static DietDBManager dbManager = null;

    // 데이터 베이스에 테이블 생성 쿼리
    static final String CREATE_DB = "CREATE TABLE " +
            DIET_TABLE + " (_id INTEGER PRIMARY KEY, "
            + " restaurant_info TEXT," +
            "diet_picture BLOB," +  // 이미지를 바이트 데이터로 저장
            "diet_name TEXT," +
            "diet_review TEXT," +
            "date TEXT," +
            "time TEXT," +
            "cost REAL,"+
            "checkbox1 INTEGER);";  // checkbox1 컬럼 추가
    public static DietDBManager getInstance(Context context)
    {
        //새로운 테이블 생성이므로 version을 올려주었음.
        if(dbManager == null) {
            dbManager = new DietDBManager(context,DIET_DB, null, 5);
        }
        return dbManager;
    }
    public DietDBManager(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, dbName, factory, version);
        this.context = context;
        getWritableDatabase();  // 이 부분을 추가하면 생성자 호출 시 테이블이 생성될 것입니다.
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
            db.execSQL(CREATE_DB);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + DIET_TABLE);
        onCreate(db);
    }
    // 이미지를 바이너리 데이터로 데이터베이스에 저장하는 메서드
    public long insertWithImageBinary(ContentValues addValue) {
        return getWritableDatabase().insert(DIET_TABLE, null, addValue);
    }
    public long insert(ContentValues addValue) {
        return getWritableDatabase().insert(DIET_TABLE, null, addValue);
    }

    public Cursor query(String [] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        return getReadableDatabase().query(DIET_TABLE, columns, selection,
                selectionArgs, groupBy, having, orderBy);
    }
    public int delete(String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(DIET_TABLE, whereClause, whereArgs);
    }

}
