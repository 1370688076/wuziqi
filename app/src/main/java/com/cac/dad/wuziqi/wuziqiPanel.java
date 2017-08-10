package com.cac.dad.wuziqi;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dad on 2017/8/10.
 */

public class wuziqiPanel extends View {
    private int mPanelWidth;
    private float mlineHeight;
    private int MAX_LINE = 10;
    private Paint mPaint = new Paint();
    private Bitmap mWhite;
    private Bitmap mBlack;
    private float raTioPieceOflineheiht = 3 * 1f / 4;
    private boolean mIsGameOver;
    private boolean mIsWhitewinner;
    private int MAX_COUNT_IN_LINE = 5;





    private boolean mIsWhite = true;//白棋先手  或者当前应该为白起
    private ArrayList<Point> mWhiteArray = new ArrayList<Point>();
    private ArrayList<Point> mBlackArray = new ArrayList<Point>();


    public wuziqiPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhite = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlack = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mlineHeight = mPanelWidth * 1.0f / MAX_LINE;
        int pieceWidth = (int) (mlineHeight * raTioPieceOflineheiht);

        mWhite = Bitmap.createScaledBitmap(mWhite, pieceWidth, pieceWidth, false);
        mBlack = Bitmap.createScaledBitmap(mBlack, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBord(canvas);
        drawPieces(canvas);
        checkGameOver();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIsGameOver) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point point = getValidPoint(x, y);
            if (mWhiteArray.contains(point) || mBlackArray.contains(point)) {         //不能重复落子
                return false;
            }
            if (mIsWhite) {
                mWhiteArray.add(point);
            } else {
                mBlackArray.add(point);
            }
            invalidate();
            mIsWhite = !mIsWhite;
            return true;
        }


        return true;
    }

    private Point getValidPoint(int x, int y) {

        return new Point((int) (x / mlineHeight), (int) (y / mlineHeight));

    }


    private void drawBord(Canvas canvas) { //画棋盘

        int w = mPanelWidth;
        float lineHeight = mlineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }


    private void drawPieces(Canvas canvas) {         //画棋子

        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhite, (whitePoint.x + (1 - raTioPieceOflineheiht) / 2) * mlineHeight,
                    (whitePoint.y + (1 - raTioPieceOflineheiht) / 2) * mlineHeight, null);

        }

        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlack, (blackPoint.x + (1 - raTioPieceOflineheiht) / 2) * mlineHeight,
                    (blackPoint.y + (1 - raTioPieceOflineheiht) / 2) * mlineHeight, null);
        }


    }


    private void checkGameOver() {      //判断游戏是否结束
        boolean Whitewin = checkFiveLine(mWhiteArray);
        boolean Blackwin = checkFiveLine(mBlackArray);
        if (Whitewin || Blackwin) {
            mIsGameOver = true;
            mIsWhitewinner = Whitewin;
            String text = mIsWhitewinner ? "白棋胜利" : "黑棋胜利";
            Restart();
            //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

        }
    }
    public void Restart()
    {
        AlertDialog.Builder bulider=new AlertDialog.Builder(getContext());
        bulider.setTitle("获胜啦");
        bulider.setMessage("是否重新开一局");
        bulider.setIcon(R.mipmap.ic_launcher);
        bulider.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mBlackArray.clear();
                mWhiteArray.clear();
                mIsGameOver=false;
                mIsWhitewinner=false;
                invalidate();

            }
        });
        bulider.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog=bulider.create();
        dialog.show();

    }

    private boolean checkFiveLine(List<Point> points) {
        for (Point p : points) {

            int x = p.x;
            int y = p.y;
            boolean winHorizatal = checkHorizatal(x, y, points);
            if (winHorizatal) return true;

            boolean winVertical = checkVertical(x, y, points);
            if (winVertical) return true;

            boolean winLeftDiagonal = checkLeftDiagonal(x, y, points);
            if (winLeftDiagonal) return true;

            boolean winRightDiagonal = checkRightDiagonal(x, y, points);
            if (winRightDiagonal) return true;

        }


        return false;


    }

    private boolean checkHorizatal(int x, int y, List<Point> points) {  //判断横向
        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        //右
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }


    private boolean checkVertical(int x, int y, List<Point> points) {  //判断纵向
        int count = 1;
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }


    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {  //判断左斜
        int count = 1;
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {  //判断右斜
        int count = 1;
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE)
            return true;

        return false;
    }



    //存储view
    private static final String INSTANCE="instance";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());

        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle)
        {
            Bundle bundle=(Bundle) state;
            mIsGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);

    }
}
