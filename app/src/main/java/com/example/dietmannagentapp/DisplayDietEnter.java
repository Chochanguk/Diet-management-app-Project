package com.example.dietmannagentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class DisplayDietEnter extends AppCompatActivity {
    ImageView i1,i2,i3,i4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_diet_enter);
        //화면에 보일 내용설정.
        //1.학식장소 선택,2.음식사진입력,3.음식과반찬이름입력,4.시간입력,5.비용입력
        i1=findViewById(R.id.imageSanglok1);
        i2=findViewById(R.id.imageSanglok2);
        i3=findViewById(R.id.imageSanglok3);
        i4=findViewById(R.id.imageDomitory);
        // sanglok1 이미지뷰에 클릭 이벤트 리스너 추가
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisplayDietEnter.this, AteInputDiet.class);
                // 추가 정보를 Intent에 넣음
                intent1.putExtra("restaurantText", "상록원 1층");
                startActivity(intent1);
            }
        });
        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(DisplayDietEnter.this, AteInputDiet.class);
                // 추가 정보를 Intent에 넣음
                intent2.putExtra("restaurantText", "상록원 2층");
                startActivity(intent2);
            }
        });
        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(DisplayDietEnter.this, AteInputDiet.class);
                // 추가 정보를 Intent에 넣음
                intent3.putExtra("restaurantText", "상록원 3층");
                startActivity(intent3);
            }
        });
        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(DisplayDietEnter.this, AteInputDiet.class);
                // 추가 정보를 Intent에 넣음
                intent4.putExtra("restaurantText", "기숙사 식당");
                startActivity(intent4);
            }
        });
    }
}