package jinye.demo.arcprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 作者:今夜犬吠
 * 时间:2019/9/17 22:33
 * 自定义弧形进度条
 */
public class MCustomIrregularProgressBar extends View {
  /*屏幕像素密度*/private int mDensity = (int) this.getResources().getDisplayMetrics().density;
  /*上下文*/private Context mContext;
  /*Path测量器*/private PathMeasure mPathMeasure;
  /*底部Path*/private Path mBackPath;
  /*进度Path*/private Path mProgressPath;
  /*画笔*/private Paint mPaint;
  /*控件宽高-用于计算刻度位置*/private PointF mViewWidthHeight;
  /*进度线粗细*/private float mProgressLineWidth = 4 * mDensity + 0.5f;
  /*时间戳*/private String mTimestamp="0/0";
  /*底部矩形高度*/private float mBackHeight;
  /*底部矩形长度*/private float mBackWidth;
  /*底部矩形周长*/private float mBackRectPerimeter;
  /*底部矩形*/private RectF mBackRectF;

  /*总进度时长*/private long mToatlProgessDuration = 60 * 1000;
  /*进度*/private long mCurrentPro=0;
  /*暂时写死 应该配置自定义属性*/
  /*底部Path颜色*/
  /*进度Path颜色*/
  /*是否开启动画过度*/
  /*内部按钮与边界的间隔*/
  public MCustomIrregularProgressBar(Context context) {
    super(context);
    toolInit();
  }

  public MCustomIrregularProgressBar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.mContext = context;
    toolInit();
  }

  public MCustomIrregularProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context;
    toolInit();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mBackHeight = getHeight() - 10;
    mBackWidth = getWidth() - 10;
    /*获取控件的宽高*/
    mViewWidthHeight = new PointF(getWidth(), getHeight());
    mBackRectF = new RectF((getWidth() - mBackWidth) / 2, (getHeight() - mBackHeight) / 2
        , (getWidth() - mBackWidth) / 2 + mBackWidth
        , (getHeight() - mBackHeight) / 2 + mBackHeight);

    mBackPath.addRoundRect(mBackRectF, mBackWidth / 6, mBackHeight / 2, Path.Direction.CW);
    /*初始化Path测量*/
    mPathMeasure = new PathMeasure(mBackPath, false);
    /*获取底部圆角矩形长度*/
    mBackRectPerimeter = mPathMeasure.getLength();
  }

  /**
   * 初始化
   */
  private void toolInit() {
    mTimestamp="0/"+mToatlProgessDuration/1000+"s";
    /*初始化画笔*/
    mPaint = new Paint();
    /*设置画笔颜色*/
    mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
    /*设置画笔样式*/
    mPaint.setStyle(Paint.Style.FILL);
    /*设置画笔粗细*/
    mPaint.setStrokeWidth(mProgressLineWidth);
    /*文字居中*/
    mPaint.setTextAlign(Paint.Align.CENTER);
    /*使用抗锯齿*/
    mPaint.setAntiAlias(true);
    /*使用防抖动*/
    mPaint.setDither(true);
    /*设置笔触样式-圆*/
    mPaint.setStrokeCap(Paint.Cap.ROUND);
    /*设置结合处为圆弧*/
    mPaint.setStrokeJoin(Paint.Join.ROUND);
    /*初始化底部Path 圆角矩形*/
    mBackPath = new Path();

    mProgressPath = new Path();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    toolDrawBackPath(canvas);
    toolDrawProgressPath(canvas);
    toolDrawProgressTime(canvas);
  }

  /**
   * 绘制底部Path
   */

  private void toolDrawBackPath(Canvas canvas) {
    if (canvas != null && mBackPath != null) {
      mPaint.setColor(ContextCompat.getColor(mContext, R.color.backPath));
      mPaint.setStyle(Paint.Style.FILL);
      canvas.drawPath(mBackPath, mPaint);
    }
  }

  /**
   * 绘制进度Path
   */
  private void toolDrawProgressPath(Canvas canvas) {
    if (canvas != null && mProgressPath != null) {
      mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
      mPaint.setStyle(Paint.Style.STROKE);
      mPaint.setStrokeWidth(mProgressLineWidth);
      canvas.drawPath(mProgressPath, mPaint);
    }
  }

  /**
   * 绘制时间戳
   */
  private void toolDrawProgressTime(Canvas canvas) {
    if(canvas!=null){
      mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
      mPaint.setStyle(Paint.Style.FILL);
      mPaint.setStrokeWidth(1);
      mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20
          ,mContext.getResources().getDisplayMetrics()));
      float mTimeWidth = mPaint.measureText(mTimestamp);
      canvas.drawText(mTimestamp, (mViewWidthHeight.x - mTimeWidth) / 2 + mTimeWidth / 2
          , (mViewWidthHeight.y - mTimeWidth / mTimestamp.length())/2+mTimeWidth / mTimestamp.length(), mPaint);

    }
  }

  /**
   * 设置总时长
   */
  public void toolSendTotalDuration(long mTotalDuration) {
    this.mToatlProgessDuration = mTotalDuration;
  }

  /**
   * 刷新进度
   */
  public void toolRefreshPro(long mPro) {
    if (mPathMeasure != null) {
      mCurrentPro=mPro;
      mTimestamp=mPro/1000+"/"+mToatlProgessDuration/1000+"s";
      mPathMeasure.getSegment(0, mBackRectPerimeter * (float) (mPro / (float) mToatlProgessDuration), mProgressPath, true);
      /*闭合Path*/
      if(mPro>=mToatlProgessDuration){
        mProgressPath.close();
      }
      invalidate();
    }
  }

  /**
   * 开启动画做平滑过度
   */
  private void toolSmooth() {

  }

  /**
   * 重置
   */
  public void toolReset() {
    if (mPathMeasure != null) {
      mProgressPath.reset();
      mPathMeasure.getSegment(0, 0, mProgressPath, true);
      invalidate();
    }
  }

  /**
   * 回收
   */
  public void toolRecy() {
    if (mProgressPath != null) {
      mProgressPath.reset();
    }
    if (mBackPath != null) {
      mBackPath.reset();
    }
    if (mPathMeasure != null) {
      mPathMeasure = null;
    }
  }
}
