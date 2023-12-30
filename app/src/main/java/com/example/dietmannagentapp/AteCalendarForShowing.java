package com.example.dietmannagentapp;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class AteCalendarForShowing extends AppCompatActivity {

    private MaterialCalendarView materialCalendarView;
    private ListView listViewForCalendar;
    private TextView selectedDayTextView;
    private TextView totalCaloriesTextView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ate_calendar_for_showing);

        materialCalendarView = findViewById(R.id.materialCalendarView);
        materialCalendarView.setSelectedDate(CalendarDay.today());

        listViewForCalendar = findViewById(R.id.listViewForCalendar);
        selectedDayTextView = findViewById(R.id.selectedDay);
        totalCaloriesTextView = findViewById(R.id.totalCalories);

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            updateListForSelectedDate(date.getDate());
            selectedDayTextView.setText(dateFormat.format(date.getDate()));
        });

        List<String> eventDates = getEventDatesFromDatabase();

        List<CalendarDay> calendarDays = new ArrayList<>();
        for (String dateString : eventDates) {
            Date date = parseDate(dateString);
            CalendarDay calendarDay = CalendarDay.from(date);
            calendarDays.add(calendarDay);
        }
        //달력 밑에 바 형태로 표시 할거 색상(material CalendarView)
        float[] hsv = new float[3];
        Color.RGBToHSV(74,146,255,hsv);
        materialCalendarView.addDecorator(new EventDecorator(Color.HSVToColor(hsv), calendarDays));

        Cursor cursor = getDataFromDatabase();

        String[] fromColumns1 = {
                MyContentProvider.DIET_NAME,
                MyContentProvider.COST,
                MyContentProvider.TIME,
                MyContentProvider._ID
        };
        int[] toViews1 = {
                R.id.dietNameTextView,
                R.id.costTextView,
                R.id.timeTextView,
                R.id.calorieTextView
        };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.list_item_2, cursor, fromColumns1, toViews1, 0
        );

        updateListForSelectedDate(CalendarDay.today().getDate());
    }

    private List<String> getEventDatesFromDatabase() {
        List<String> eventDates = new ArrayList<>();
        Cursor cursor = getDataFromDatabase();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String dateString = cursor.getString(4); // 4는 DATE의 인덱스, 적절한 값으로 변경
                    if (isValidDate(dateString)) {
                        eventDates.add(dateString);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return eventDates;
    }

    private Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private boolean isValidDate(String date) {
        try {
            Date parsedDate = dateFormat.parse(date);
            return parsedDate != null;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Cursor getDataFromDatabase() {
        String[] projection1 = {
                MyContentProvider.DIET_NAME,
                MyContentProvider.TIME,
                MyContentProvider.COST,
                MyContentProvider._ID,
                MyContentProvider.DATE //getEventFromDatabase() 함수를 위해 사용함.
        };
        return getContentResolver().query(MyContentProvider.CONTENT_URI, projection1, null, null, null);
    }

    private void updateListForSelectedDate(Date selectedDate) {
        String formattedDate = dateFormat.format(selectedDate);
        Cursor cursor = getDataForSelectedDate(formattedDate);

        String[] fromColumns2 = {
                MyContentProvider.DIET_NAME,
                MyContentProvider.COST,
                MyContentProvider.TIME,
                MyContentProvider._ID,
                MyContentProvider.CHECK
        };
        int[] toViews2 = {
                R.id.dietNameTextView,
                R.id.costTextView,
                R.id.timeTextView,
                R.id.calorieTextView
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.list_item_2, cursor, fromColumns2, toViews2, 0
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView calorieTextView = view.findViewById(R.id.calorieTextView);
                int calories=0;
                long dietId = getItemId(position);
                if(cursor.getInt(4)==1)
                {
                    calories = beverageCalories(dietId);
                }
                else {
                    calories = foodCalories(dietId);
                }
                calorieTextView.setText(String.valueOf(calories));

                return view;
            }
        };

        listViewForCalendar.setAdapter(adapter);

        int totalCalories = calculateTotalCalories(selectedDate);
        totalCaloriesTextView.setText(String.valueOf(totalCalories));
    }

    private Cursor getDataForSelectedDate(String selectedDate) {
        String[] projection2 = {
                MyContentProvider.DIET_NAME,
                MyContentProvider.COST,
                MyContentProvider.TIME,
                MyContentProvider._ID,
                MyContentProvider.CHECK
        };
        String selection2 = MyContentProvider.DATE + "=?";
        String[] selectionArgs2 = {selectedDate};
        return getContentResolver().query(MyContentProvider.CONTENT_URI, projection2, selection2, selectionArgs2, null);
    }

    private int calculateTotalCalories(Date selectedDate) {
        String formattedDate = dateFormat.format(selectedDate);
        Cursor cursor = getDataForSelectedDate(formattedDate);

        int totalCalories = 0;

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    long dietId = cursor.getLong(cursor.getColumnIndexOrThrow(MyContentProvider._ID));
                    //check가 1이면, 즉 음료이면
                    if(cursor.getInt(4)==1)
                    {
                        totalCalories += beverageCalories(dietId);
                    }
                    //음식이면 음식 칼로리 계산
                    else {
                        totalCalories += foodCalories(dietId);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return totalCalories;
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