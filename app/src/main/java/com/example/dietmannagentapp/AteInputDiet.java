package com.example.dietmannagentapp;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dietmannagentapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AteInputDiet extends AppCompatActivity {
    private static final int REQUEST_CODE_TIME = 1000;
    private static final int REQUEST_CODE_CALENDAR = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isCheckBoxChecked=false; // 체크박스 값 저장 변수
    private Uri selectedImageUri; // 저장할 이미지의 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ate_input_diet);
        TextView sanglokTextView = findViewById(R.id.restaurant);
        Intent intent = getIntent();
        if (intent != null) {
            String restaurantText = intent.getStringExtra("restaurantText");
            if (restaurantText != null) {
                sanglokTextView.setText(restaurantText);
            }
        }

        ImageView image = findViewById(R.id.dietpicture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        EditText calText = findViewById(R.id.TextViewDate);
        calText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //시간을 선택하는 버튼에 대한 클릭 이벤트 처리
        EditText timeText = findViewById(R.id.TextViewTime);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 여기서 Time 액티비티를 시작하도록 수정
                Intent intent4 = new Intent(AteInputDiet.this, Time.class);
                startActivityForResult(intent4, REQUEST_CODE_TIME);
            }
        });

        ImageButton listButton = findViewById(R.id.goToList);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(AteInputDiet.this, AteShowing.class);
                startActivity(intent5);
            }
        });
        // 체크 박스 값 확인
        CheckBox checkbox = findViewById(R.id.checkbox1);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckBoxChecked = ((CheckBox) v).isChecked();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                EditText editTextDate = findViewById(R.id.TextViewDate);
                editTextDate.setText(selectedDate);
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TIME && resultCode == RESULT_OK && data != null) {
            String selectedTime = data.getStringExtra("selectedTime");
            String selectedImageUri = data.getStringExtra("selectedImageUri");

            EditText editTextTime = findViewById(R.id.TextViewTime);
            editTextTime.setText(selectedTime);
        }

        if (requestCode == REQUEST_CODE_CALENDAR && resultCode == RESULT_OK && data != null) {
            String selectedDate = data.getStringExtra("selectedDate");
            String selectedImageUri = data.getStringExtra("selectedImageUri");

            EditText editTextDate = findViewById(R.id.TextViewDate);
            editTextDate.setText(selectedDate);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            ImageView imageView = findViewById(R.id.dietpicture);
            imageView.setImageURI(selectedImageUri);
        }
    }

    public void addDiet(View view) {
        try {
            if (selectedImageUri == null) {
                Toast.makeText(this, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            TextView restaurantTextView = findViewById(R.id.restaurant);
            EditText dietNameEditText = findViewById(R.id.diet_name);
            EditText dietReviewEditText = findViewById(R.id.diet_review);
            EditText editTextDate = findViewById(R.id.TextViewDate);
            EditText editTextTime = findViewById(R.id.TextViewTime);
            EditText costEditText = findViewById(R.id.cost);

            //필드의 형식이 올바른지 아닌지 확인하는 함수들(1~4)
            //1
            if (restaurantTextView.getText().toString().isEmpty() ||
                    dietNameEditText.getText().toString().isEmpty() ||
                    dietReviewEditText.getText().toString().isEmpty() ||
                    editTextTime.getText().toString().isEmpty() ||
                    editTextDate.getText().toString().isEmpty() ||
                    costEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }
            //2
            if (!isValidCost(costEditText.getText().toString())) {
                Toast.makeText(this, "비용은 숫자로만 입력 해주세요!\n (예: 5000)", Toast.LENGTH_SHORT).show();
                return;
            }
            //3
            if (!isValidDate(editTextDate.getText().toString())) {
                Toast.makeText(this, "올바른 날짜 형식을 입력해주세요!\n (예: yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
                return;
            }
            //4
            if (!isValidTime(editTextTime.getText().toString())) {
                Toast.makeText(this, "올바른 시간 형식을 입력해주세요!\n (예: HH시 mm분)", Toast.LENGTH_SHORT).show();
                return;
            }
            //이미지를 바이트 값으로 대체해서 배열에다 넣음.
            byte[] imageData = convertImageToByteArray(selectedImageUri);

            ContentValues addValues = new ContentValues();
            addValues.put(MyContentProvider.RESTAURANT_NAME, restaurantTextView.getText().toString());
            addValues.put(MyContentProvider.DIET_NAME, dietNameEditText.getText().toString());
            addValues.put(MyContentProvider.DIET_REVIEW, dietReviewEditText.getText().toString());
            addValues.put(MyContentProvider.DATE, editTextDate.getText().toString());
            addValues.put(MyContentProvider.TIME, editTextTime.getText().toString());
            addValues.put(MyContentProvider.COST, costEditText.getText().toString());
            addValues.put(MyContentProvider.DIET_PICTURE, imageData);
            addValues.put(MyContentProvider.CHECK,isCheckBoxChecked ? 1 : 0);


            Uri insertedUri = getContentResolver().insert(MyContentProvider.CONTENT_URI, addValues);

            if (insertedUri != null) {
                Toast.makeText(getBaseContext(), "Record Added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Failed to add record", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error adding record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] convertImageToByteArray(Uri imageUri) throws IOException {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "이미지를 바이트 배열로 변환하는 중에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            throw e;
        }
    }
    //날짜 형식이 올바른지 확인용 함수
    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            return parsedDate != null;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    //시간 형식이 올바른지 확인용 함수
    private boolean isValidTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH시 mm분", Locale.KOREA);
            sdf.setLenient(false);
            Date parsedTime = sdf.parse(time);
            return parsedTime != null;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    //비용이 숫자로만 이루어져있는지 확인
    private boolean isValidCost(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}