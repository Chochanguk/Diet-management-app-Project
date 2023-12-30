package com.example.dietmannagentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

public class DietInformation extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_information);

        String[] columns = new String[]{"_id", "restaurant_info","diet_picture","diet_name",
                "diet_review","date","time","cost","checkbox1"};
        // Ateshowing 클래스에서 클릭한 리스트에서 id를 추출해서 Intent에 넣음.
        // 해당 id값을 cursor의 값에 넣음.
        long dietId = getIntent().getLongExtra("dietId", -1);
        String selection = "_id=?";
        String[] selectionArgs = {String.valueOf(dietId)};
        // Query the database to get detailed information about the selected diet
        Cursor cursor = getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                columns, //조회할 열
                selection, //where 조건문
                selectionArgs,//? 값
                null //groub by절
        );

        if (cursor != null&& cursor.moveToFirst()) {

            int id=cursor.getInt(0);
            String restaurant = cursor.getString(1);
            byte[] imageData = cursor.getBlob(2);
            String dietName = cursor.getString(3);
            String review = cursor.getString(4);
            String date = cursor.getString(5);
            String time = cursor.getString(6);
            String cost = cursor.getString(7);
            Integer check=cursor.getInt(8);
            // Display data in the UI
            displayDietDetails(imageData, dietName, date, time, cost, restaurant,review,id,check);
        }

        // Close the cursor
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
    private void displayDietDetails(byte[] imageData, String dietName, String date, String time,
                                    String cost, String restaurant, String review,long dietId,int check) {
        // Display data in the UI
        ImageView imageView = findViewById(R.id.imageView);
        TextView dietNameTextView = findViewById(R.id.dietNameTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        TextView costTextView = findViewById(R.id.costTextView);
        TextView restaurantTextView = findViewById(R.id.restaurantTextView);
        TextView caloriesTextView = findViewById(R.id.caloriesTextView);
        TextView reviewTextView = findViewById(R.id.reviewTextView);
        TextView checkTextView=findViewById(R.id.checkTextView);

        // Convert byte array to Bitmap and set it to the ImageView
        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(imageData));
        imageView.setImageBitmap(bitmap);

        // Set other data to TextViews
        dietNameTextView.setText(dietName);
        dateTextView.setText(date);
        timeTextView.setText(time);
        costTextView.setText(cost);
        restaurantTextView.setText(restaurant);
        reviewTextView.setText(review);
        if(check==1)
        {
            caloriesTextView.setText(""+beverageCalories(dietId));
            checkTextView.setText("음료");
        }else {
            caloriesTextView.setText(""+foodCalories(dietId));
            checkTextView.setText("음식");
        }
    }
    private int beverageCalories(long dietId) {
        // Use the dietId to get a corresponding calorie value from the array
        int[] calories = {100, 30, 120, 60, 40, 90, 80, 50, 0, 70};
                  //나머지: 0    1    2     3    4    5    6    7   8   9

        int index = (int) (dietId % 10);

        return calories[index];
    }

    private int foodCalories(long dietId) {
        // Use the dietId to get a corresponding calorie value from the array
        int[] calories = {100, 300, 1200, 600, 400, 900, 800, 500, 0, 700};
                   //나머지: 0    1    2     3    4    5    6    7   8   9

        int index = (int) (dietId % 10);

        return calories[index];
    }
}