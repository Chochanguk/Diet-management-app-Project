package com.example.dietmannagentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//식사 분석하기 액티비티
public class AteAnalysis extends AppCompatActivity {

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ate_analysis);

        barChart = (BarChart) findViewById(R.id.cost_chart);
        Cursor resultSet = getAnalyzeCursor();

        updateTotalCalories(resultSet);
        updateBarChart(resultSet,barChart);
    }

    private Cursor getAnalyzeCursor() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());

        ArrayList<Long> dateBasedIds = new ArrayList<>();
        for (int i = 0; i <= 30; i++) {
            String dateString = dateFormat.format(cal.getTime());
            List<Long> dietIds = getDietIdsFromDate(dateString);
            dateBasedIds.addAll(dietIds);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        StringBuilder selectionBuilder = new StringBuilder("_id IN (");
        for (int i = 0; i < dateBasedIds.size(); i++) {
            selectionBuilder.append(dateBasedIds.get(i));
            if (i < dateBasedIds.size() - 1) {
                selectionBuilder.append(",");
            }
        }
        selectionBuilder.append(")");

        String[] projection = {"_id", "date", "time", "cost", "checkbox1"};

        return getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                projection,
                selectionBuilder.toString(),
                null,
                null
        );
    }

    // 날짜에 해당하는 ID를 얻는 메서드
    private List<Long> getDietIdsFromDate(String date) {
        List<Long> dietIds = new ArrayList<>();
        String[] projection = {"_id"};
        String selection = "date = ?";
        String[] selectionArgs = {date};

        Cursor cursor = getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    long dietId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                    dietIds.add(dietId);
                }
            } finally {
                cursor.close();
            }
        }

        return dietIds;
    }
    //총 칼로리 계산
    private TextView updateTotalCalories(Cursor cursor) {
        int totalCalories = 0;
        int calories=0;
        TextView calorieText = findViewById(R.id.textView16);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = (int) cursor.getFloat(cursor.getColumnIndexOrThrow("_id"));
                    //음료면 음료 칼로리 계산
                    if(cursor.getInt(4)==1)
                    {
                        calories=beverageCalories(id);
                    }
                    //음식이면 음식 칼로리 계산
                    else {
                        calories=foodCalories(id);
                    }
                    totalCalories += calories;
                } while (cursor.moveToNext());
            }
        }

        calorieText.setText(String.valueOf(totalCalories));

        return calorieText;
    }

    private void updateBarChart(Cursor cursor, BarChart barChart) {
        int[] mealTypeCost = {0, 0, 0, 0};
        ArrayList<BarEntry> chartEntry = new ArrayList<>();
        Log.i("조회 수", "조회 수 : " + cursor.getCount());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                    int cost = (int) cursor.getFloat(cursor.getColumnIndexOrThrow("cost"));
                    int isBeverage = cursor.getInt(cursor.getColumnIndexOrThrow("checkbox1"));

                    setMealTypeCost(mealTypeCost, time, isBeverage, cost);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // Y 축 레이블 값 설정
        for (int i = 0; i < mealTypeCost.length; i++) {
            chartEntry.add(new BarEntry(i, mealTypeCost[i]));
        }
        BarDataSet dataSet = new BarDataSet(chartEntry, "조식, 중식, 석식, 음료 순");
        dataSet.setColor(Color.rgb(66, 134, 244)); // Bar의 색상 설정
        Legend legend = barChart.getLegend();
        legend.setTextSize(13f); // 레전드(legend) 텍스트 크기 설정
        legend.setTypeface(Typeface.DEFAULT_BOLD);


        BarData barData = new BarData(dataSet);
        dataSet.setValueTextColor(Color.BLACK); // Bar 값의 텍스트 색상 설정
        dataSet.setValueTextSize(14f); // Bar 값의 텍스트 크기 설정
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD); // Bar 값의 텍스트 스타일 설정

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setEnabled(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);
        leftAxis.setAxisMinimum(0f); // Y 축 최소값을 0으로 설정

        barChart.getAxisRight().setEnabled(false); // 오른쪽 Y 축 비활성화

        // Description label 제거
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        // 차트에 그림자 추가
        barChart.setDrawBarShadow(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setHighlightFullBarEnabled(false);

        // 차트 애니메이션 효과 추가
        barChart.animateY(1500, Easing.EaseInOutQuad);

        barChart.setData(barData);
        barChart.invalidate();
    }


    // return 0 : 조식 1 : 중식 2: 석식 3: 음료
    private void setMealTypeCost(int[] mealTypeCost,String time,int isBeverage,int cost) {
        if (isBeverage == 1) {
            mealTypeCost[3] += cost;
            return;
        }

        int hours = Integer.parseInt(time.split("시")[0]);

        if (hours >= 6 && hours <=10) {
            mealTypeCost[0] += cost;
        } else if (hours >= 11 && hours <=15) {
            mealTypeCost[1] += cost;
        } else if (hours >= 16 && hours <= 20) {
            mealTypeCost[2] += cost;
        }
    }
    //칼로리 계산
    private int beverageCalories(long dietId) {
        // Use the dietId to get a corresponding calorie value from the array
        int[] calories = {100, 30, 120, 60, 40, 90, 80, 50, 0, 70};
        //나머지: 0    1   2    3   4   5   6   7  8   9
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