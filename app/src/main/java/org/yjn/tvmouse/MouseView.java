package org.yjn.tvmouse;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import static android.content.Context.WINDOW_SERVICE;


/**
 * 模拟鼠标视图，测试用
 */
public class MouseView extends FrameLayout {
    private Context context;

    private ImageView mMouseView;

    private Bitmap mMouseBitmap;

    //鼠标移动距离  px
    private int mMoveDis = 30;

    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;
    int screenWidth;//得到屏幕的宽度
    int screenHeight;//得到屏幕的高度

    Instrumentation inst = new Instrumentation();

    private static Handler handler;
    public final static int MOUSE_SIZE = 50;

    public MouseView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MouseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MouseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MouseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }



    /**
     * 初始化鼠标
     */
    private void init() {
        createMessageHandleThread();

        Drawable drawable =	getResources().getDrawable(
                R.mipmap.mouse);
        mMouseBitmap = drawableToBitamp(drawable);
        mMouseView = new ImageView(getContext());
        mMouseView.setImageBitmap(mMouseBitmap);

        windowManager = (WindowManager)this.context.getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();

        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        layoutParams.height = mMouseBitmap.getHeight();
        layoutParams.width = mMouseBitmap.getWidth();

        Log.i("moveMouse","layoutParams.height:"+layoutParams.height+"layoutParams.width:"+layoutParams.width);

        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;

        layoutParams.x = screenWidth/2;
        layoutParams.y = screenHeight/2;

        Log.i("moveMouse","screenWidth:"+screenWidth+", screenHeight:"+screenHeight);

        windowManager.addView(mMouseView, layoutParams);

    }

    /**
     * 生成一个鼠标图片
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitamp(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, MOUSE_SIZE,MOUSE_SIZE,true);
    }


    /**
     * mHandler
     */
    private Handler mHandler = new Handler();

    /**
     * 隐藏鼠标线程
     */
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            mMouseView.setVisibility(GONE);
        }
    };

    /**
     * 设置鼠标显示，不移动鼠标15秒后隐藏
     */
    private void setMouseShow(){
        mMouseView.setVisibility(VISIBLE);
        mHandler.removeCallbacks(hideRunnable);
        mHandler.postDelayed(hideRunnable,1500);
    }

    /**
     * 按键监听
     *
     * 模拟鼠标点击要 发送 ACTION_DOWN ACTION_UP 两个事件才会生效
     * @param webView
     * @param event
     */
    public boolean moveMouse(CustomWebView webView, final KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            setMouseShow();
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(layoutParams.x - mMoveDis >= 0) {
                        layoutParams.x -= mMoveDis;
                    }else{
                        layoutParams.x = 0;
                    }

                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(layoutParams.x + mMoveDis + layoutParams.width <= screenWidth){
                        layoutParams.x += mMoveDis;
                    }else{
                        layoutParams.x = screenWidth - MOUSE_SIZE;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(layoutParams.y - mMoveDis >= 0) {
                        layoutParams.y -= mMoveDis;
                    }else {
                        layoutParams.y = 0;
//                        if(webView.getScrollY() - mMoveDis >= 0) {
//                            webView.scrollBy(0, -mMoveDis);
//                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(layoutParams.y + mMoveDis + layoutParams.height <= screenHeight){
                        layoutParams.y += mMoveDis;
                    }else{
                        layoutParams.y = screenHeight - MOUSE_SIZE;
//                        if((webView.getContentHeight()*webView.getScale() - webView.getHeight()) - webView.getScrollY() >= 0) {
//                            webView.scrollBy(0, mMoveDis);
//                        }
                    }
                    break;
            }
            Log.i("moveMouse","layoutParams.x:"+layoutParams.x+", layoutParams.y:"+layoutParams.y);
            windowManager.updateViewLayout(mMouseView, layoutParams);
        }

        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){

            handler.post( new Runnable() {

                public void run() {

                    Log.i("tag","点击layoutParams.x:"+layoutParams.x+", layoutParams.y:"+layoutParams.y);
                    try {
                        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), event.getAction(), layoutParams.x, layoutParams.y, 0));    //x,y 即是事件的坐标
                    }catch (Exception e){
                        Log.e("tag",e.toString());
                    }



//                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, layoutParams.x, layoutParams.y, 0));
//                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis()+30,MotionEvent.ACTION_MOVE, layoutParams.x, layoutParams.y-90, 0));
//                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis()+30,MotionEvent.ACTION_UP, layoutParams.x, layoutParams.y-90, 0));
                }

            } );
        }

        if (event.getKeyCode()>= 19 && event.getKeyCode() <= 23){
            return true;
        }
        return false;
    }


    private void createMessageHandleThread(){
        HandlerThread handlerThread = new HandlerThread("thread");
        handlerThread.start();
        handler= new Handler(handlerThread.getLooper());

    }

}
