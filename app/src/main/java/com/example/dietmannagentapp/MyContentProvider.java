package com.example.dietmannagentapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.dietmannagentapp.MyContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/testdiet2"; //(테이블 변경시 고려사항 두번째)
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final String _ID = "_id";
    static final String RESTAURANT_NAME = "restaurant_info";
    static final String DIET_PICTURE= "diet_picture";
    static final String DIET_NAME= "diet_name";
    static final String DIET_REVIEW= "diet_review";
    static final String DATE = "date";
    static final String TIME = "time";
    static final String COST = "cost";
    static final String CHECK = "checkbox1"; // 추가된 체크박스 컬럼
    public DietDBManager dbManager;
    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        dbManager = DietDBManager.getInstance(getContext());
        // 데이터를 삭제하고 삭제된 행의 수를 얻음
        int deletedRows = dbManager.delete(selection, selectionArgs);
        // 변경 사항을 등록된 옵저버에게 알림
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        // MIME 유형을 정의하여 반환
        return "vnd.android.cursor.item/vnd." + PROVIDER_NAME + "testdiet2";//(테이블 변경시 고려사항 세번째)
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        dbManager = DietDBManager.getInstance(getContext());
        long rowid = dbManager.insert(values);
        if (rowid > 0) {
            // 데이터가 성공적으로 추가되면 그 데이터를 나타내는 Uri를 생성하여 반환
            Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, rowid);
            // 변경 사항을 등록된 옵저버에게 알림
            getContext().getContentResolver().notifyChange(insertedUri, null);
            return insertedUri;
        }
        // 데이터 추가 실패 시 RuntimeException을 발생시킴
        throw new RuntimeException("데이터 추가 실패");
    }
    @Override
    public boolean onCreate() {
        dbManager = DietDBManager.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return dbManager.query(projection, selection,
                selectionArgs, null, null, sortOrder);

    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}