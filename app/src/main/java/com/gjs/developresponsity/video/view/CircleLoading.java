package com.gjs.developresponsity.video.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.VideoUtils;

/**
 * 自定义圆形加载view
 */
public class CircleLoading extends View {

    private static final int OUTER_LAYOUT_CIRCLE_STROKE_WIDTH = 2;
    /**默认圆的半径**/
    private static final int OUT_RADIUS = 30;
    /**外部圆颜色**/
    private int outerCircleColor = 0;
    /**外部圆线宽度**/
    private int outerCircleStrokeWidth = 0;
    /**半径**/
    private int mRadius = 0;

    private Paint outerCirclePaint;
    private Paint mArcPaint;
    private Paint mTrianglePaint;

    private float mArcAngle;
    //扇形半径
    private float angleRadius;
    //默认开始状态
    private Status mStatus = Status.Start;
    /**view的宽和高比较小者**/
    private int viewMinSize = 0;
    /**三角形边长**/
    private float mTriangleLength;
    /**画三角形的path**/
    private Path trianglePath;

    public CircleLoading(Context context) {
        this(context,null);
    }

    public CircleLoading(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //默认外部圆的颜色为白色
        outerCircleColor = getResources().getColor(R.color.white);
        //默认外部圆画笔的宽度为2dp
        outerCircleStrokeWidth = VideoUtils.dp2px(context, OUTER_LAYOUT_CIRCLE_STROKE_WIDTH);

        //默认半径
        mRadius = VideoUtils.dp2px(context, OUT_RADIUS);

        //获取自定义属性
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CircleLoading);
        int indexCount = array.getIndexCount();
        for(int i = 0;i < indexCount;i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.CircleLoading_outer_layout_circle_color:
                    outerCircleColor = array.getColor(attr, outerCircleColor);
                    break;
                case R.styleable.CircleLoading_outer_layout_circle_stroke_width:
                    outerCircleStrokeWidth = (int) array.getDimension(attr, outerCircleStrokeWidth);
                    break;
                case R.styleable.CircleLoading_circle_radius:
                    mRadius = (int) array.getDimension(attr, mRadius);
                    break;
            }
        }
        //回收
        array.recycle();
        //画三角形
        trianglePath = new Path();
        //设置画笔
        setPaint();

    }

    private void setPaint() {
        outerCirclePaint = new Paint();
        outerCirclePaint.setAntiAlias(true);
        outerCirclePaint.setDither(true);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setColor(outerCircleColor);
        outerCirclePaint.setStrokeWidth(outerCircleStrokeWidth);
        outerCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        mArcPaint.setStyle(Paint.Style.FILL);
        mArcPaint.setColor(outerCircleColor);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mTrianglePaint = new Paint();
        mTrianglePaint.setAntiAlias(true);
        mTrianglePaint.setDither(true);
        mTrianglePaint.setColor(outerCircleColor);
        mTrianglePaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;
        if(widthMode != MeasureSpec.EXACTLY){
            width = getPaddingLeft() + mRadius * 2 + outerCircleStrokeWidth * 2 + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }
        if(heightMode != MeasureSpec.EXACTLY){
            height = getPaddingTop() + mRadius * 2 + outerCircleStrokeWidth * 2 + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int loadWidth = getWidth();
        int loadHeight = getHeight();
        if(loadWidth >= loadHeight){
            viewMinSize = loadHeight;
        } else {
            viewMinSize = loadWidth;
        }
        //半径为view的宽和高中的小者减去左右padding和外部圆画笔的笔宽
        mRadius = (viewMinSize - getPaddingLeft() - getPaddingRight()- outerCircleStrokeWidth * 2) / 2;
        angleRadius = (float) (mRadius * (1 - 0.06));
        mTriangleLength = mRadius;

        canvas.save();
        canvas.translate(getPaddingLeft(),getPaddingTop());
        //画圆
        canvas.drawCircle(loadWidth / 2, loadHeight / 2, mRadius, outerCirclePaint);
        if(mStatus == Status.Start){//开始
            //画三角形。根据等边三角形重心是圆形，再根据勾股定理计算
            float firstPointX = (float)(loadWidth/2 - Math.sqrt(3.0) / 6 * mRadius);
            float firstPointY = loadHeight/2 - mRadius/2;
            trianglePath.moveTo(firstPointX, firstPointY);
            trianglePath.lineTo(firstPointX, loadHeight/2 + mRadius/2);
            trianglePath.lineTo((float)(loadWidth/2 + Math.sqrt(3.0) / 3 * mRadius), loadHeight/2);
            trianglePath.close();
            canvas.drawPath(trianglePath, mTrianglePaint);
        }
        if(mStatus == Status.End){
            //结束隐藏
            setVisibility(View.GONE);
        }
        if(mStatus == Status.Starting){//正在进行状态
            //画扇形
            RectF AngleRectF = new RectF(loadWidth / 2 - angleRadius, loadHeight/ 2 - angleRadius,
                    loadWidth / 2 + angleRadius, loadHeight/ 2 + angleRadius);
            canvas.drawArc(AngleRectF, -90, 360 * mArcAngle, true, mArcPaint);
        }

        canvas.restore();
    }


    /**
     * 圆形进度条展示
     * @param currentSize
     * @param totalSize
     */
    public void loadAngle(long currentSize, long totalSize){
        mStatus = getStatus();
        mArcAngle =  (float) currentSize / totalSize;
        invalidate();
    }



    public enum Status{
        //开始
        Start,
        //结束
        End,
        //进行中
        Starting
    }
    private Status getStatus(){
        return mStatus;
    }

    /**
     * 设置进度条的状态
     * @param status
     */
    public void setStatus(Status status){
        this.mStatus = status;
    }
}
