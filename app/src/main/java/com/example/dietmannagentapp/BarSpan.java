package com.example.dietmannagentapp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.LineBackgroundSpan;

public class BarSpan implements LineBackgroundSpan {

    private final int color;
    private final int height;
    private final int cornerRadius;

    public BarSpan(int color, int height, int cornerRadius) {
        this.color = color;
        this.height = height;
        this.cornerRadius = cornerRadius;
    }
    @Override
    public void drawBackground(
            Canvas c, Paint p, int left, int right, int top, int baseline, int bottom,
            CharSequence text, int start, int end, int lnum
    ) {
        // 기존 텍스트 색상을 저장
        int originalTextColor = p.getColor();
        RectF rect = new RectF(left-12, bottom - height, right-12, bottom);
        p.setColor(color);
        c.drawRoundRect(rect, cornerRadius, cornerRadius, p);

        // 원래의 텍스트 색상으로 복원
        p.setColor(originalTextColor);
    }
}