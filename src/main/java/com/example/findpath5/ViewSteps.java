package com.example.findpath5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ViewSteps  extends View {

    //create paint
    private Paint mFill;
    private Paint mStroke;
    //degrees used to rotate
    private int mdegree;

    private int i = 1;
    //initial x and y position
    private int x = 290;
    private int y = 138;
    //initial step length
    private int stepln = 3;

    //ARROWS
    private Path mArrowPath; // arrow path
    private int sR = 6;     // radius of the small circle
    private int arrowR = 12; // radius of the arrow

    private Bitmap mBitmap;
    // create a list to store the x and y positions to update the
    //paths with data type PointF
    private List<PointF> mStepslist = new ArrayList<>();



    //constructors for this class
    public ViewSteps(Context context) {
        this(context,null);
        initial();
    }
    //constructors for this class
    public ViewSteps(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
        initial();
    }
    //constructors for this class
    public ViewSteps(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial();
        //find the picture in mipmap
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.indoor);
    }


    //initial the paint
    private void initial() {

        //FILL TYPEN PAINT
        mFill = new Paint();
        mFill.setAntiAlias(false);
        mFill.setColor(Color.BLUE);
        mFill.setStyle(Paint.Style.FILL);

        //STROKE TYPE PAINT
        mStroke = new Paint(mFill);
        mStroke.setStyle(Paint.Style.STROKE);
        mStroke.setStrokeWidth(3);

        // initial the arrow's position
        mArrowPath = new Path();
        //left,top,right,bottom
        //for x, positive represents right movement. for y, positive represents down movement
        //for rotation, positive value represents clockwise rotation
        mArrowPath.arcTo(new RectF(-arrowR, -arrowR, arrowR, arrowR), 0, 180);
        mArrowPath.lineTo(0, 2*(-arrowR) );
        mArrowPath.close();//form the complete line to form the arrow

    }

    //Override the function to draw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(),
                mBitmap.getHeight()), new Rect(0, 0, getWidth(), getHeight()), null); // 将整合到mBitmap的图片绘到canLock

        for (PointF p : mStepslist) {
            canvas.drawCircle(p.x, p.y, sR, mFill);
        } //draw the circles to show the las path


        canvas.save();                  // save canvas
        canvas.translate(x, y); // move canvas
        canvas.rotate(mdegree);         // rotate canvas
        canvas.drawPath(mArrowPath, mStroke); //draw the path: arrow
        canvas.drawCircle(0,0,4,mStroke);
        canvas.restore();               // restore the canvas

    }

    //pass the rotate degree
    public void Turn(int degrees) {
        mdegree = degrees;
        postInvalidate();
    }
    //set the steplength
    public void SetStepLength(int steplength) {
        stepln = steplength;
    }
    //update the position
    public void UpdatePosition(int steps) {
        //update the x and y position based on steps and stepslength
        if(steps/stepln == i) {

            x += (int) (stepln * 10 * Math.sin(Math.toRadians(mdegree)));
            y += -(int) (stepln * 10 * Math.cos(Math.toRadians(mdegree)));
            i += 1;
            //add the x and y position into lish
            mStepslist.add(new PointF(x, y));

        }
        postInvalidate();
    }

}
