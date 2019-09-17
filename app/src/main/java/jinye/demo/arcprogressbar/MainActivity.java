package jinye.demo.arcprogressbar;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.concurrent.TimeUnit;

/**
 * 要严谨
 * 作者 今夜犬吠
 * 时间 2019/9/18 0:27
 */
public class MainActivity extends AppCompatActivity {

  /*开启进度*/private Button mStartProButton;
  /*进度条*/private MCustomIrregularProgressBar mCustomArcProgressBar;

  /*总时长*/private long mTotalPro=60*1000;
  /*进度*/private long mCurrentPro=0;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    toolInitView();
    toolSetLinstener();
  }

  /**
   * 初始化控件
   */
  private void toolInitView(){
    mCustomArcProgressBar=findViewById(R.id.MCustomArcProgressBar_MainActivity_ArcProgress);
    mStartProButton=findViewById(R.id.Button_MainActivity_Start);

    /*设置总时长*/
    mCustomArcProgressBar.toolSendTotalDuration(mTotalPro);
  }

  /*计时器控制*/private Disposable mTimerDisposable;

  /**
   * 设置监听
   */
  @SuppressLint("CheckResult")
  private void toolSetLinstener(){

    /*点击开启进度*/
    RxView.clicks(mStartProButton)
        .throttleFirst(1, TimeUnit.SECONDS)
        .subscribe(new Consumer<Unit>() {
          @Override
          public void accept(Unit unit) throws Exception {
            if(mCurrentPro==0){
              mCustomArcProgressBar.toolReset();
              toolSendPro();
            }
          }
        });
  }

  /**
   * 使用计时器模拟播放进度
   */
  @SuppressLint("CheckResult")
  private void toolSendPro(){
    mTimerDisposable=Observable.interval(0,20,TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Long>() {
          @Override
          public void accept(Long aLong) throws Exception {
            mCurrentPro=aLong*300;
            if(mCustomArcProgressBar!=null){
              mCustomArcProgressBar.toolRefreshPro(aLong*300);
            }
            if(aLong*300>=60*1000){
              mCurrentPro=0;
              if(mTimerDisposable!=null
                  &&!mTimerDisposable.isDisposed()){
                mTimerDisposable.dispose();
              }
            }
          }
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(mTimerDisposable!=null
        &&!mTimerDisposable.isDisposed()){
      mTimerDisposable.dispose();
    }
    mCustomArcProgressBar.toolRecy();
  }
}
