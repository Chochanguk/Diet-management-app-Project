package com.example.dietmannagentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaParser;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //식사 보여주기 버튼 클릭시 
    //1.식당을 보여줌(DisplayDietEnter), 2.상록원1~3층,기숙사 식당 선택 3.
    public void dietEnter(View view)
    {
        Intent intent1=new Intent(this, DisplayDietEnter.class);
        startActivity(intent1);
    }
    //식사 보여주기
    public void dietShowing(View view)
    {
        Intent intent2=new Intent(this, AteShowing.class);
        startActivity(intent2);
    }
    //식사 분석하기
    public void dietAnalysis(View view)
    {
        Intent intent3=new Intent(this, AteAnalysis.class);
        startActivity(intent3);
    }

}