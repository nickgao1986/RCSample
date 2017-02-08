package floatmenu;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.logging.MktLog;


public class RoundSpinView extends View {
    private static final String TAG = "[RC]RoundSpinView";
    private Paint mPaint = new Paint();
    private PaintFlagsDrawFilter pfd;

    private int startMenu;

    private BigStone[] mStones;
    private static final int STONE_COUNT = 5;

    private int mPointX = 0, mPointY = 0;
    private int mRadius = 0;
    private int mDegreeDelta;

    private int menuRadius;

    private int mCur = -1;

    private boolean[] quadrantTouched;

    // Touch detection
    private GestureDetector mGestureDetector;

    private onRoundSpinViewListener mListener;

    private final static int TO_ROTATE_BUTTON = 0;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_ROTATE_BUTTON:
                    float velocity = Float.parseFloat(msg.obj.toString());
                    rotateButtons(velocity/75);
                    velocity /= 1.0666F;
                    new Thread(new FlingRunnable(velocity)).start();
                    break;

                default:
                    break;
            }
        };
    };

    public interface onRoundSpinViewListener{
        public void onSingleTapUp(int position);
    }

    public RoundSpinView(Context context, AttributeSet attrs) {
        super(context,attrs);
        if(attrs!=null){
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.RoundSpinView);
            startMenu = a.getResourceId(R.styleable.RoundSpinView_menuStart, 0);
        }
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        mPaint.setPathEffect(effects);
        quadrantTouched = new boolean[] { false, false, false, false, false };
        mGestureDetector = new GestureDetector(getContext(),
                new MyGestureListener());

        setupStones();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPointX = this.getMeasuredWidth()/2;
        mPointY = this.getMeasuredHeight()/2;
        mRadius = mPointX-mPointX/5;
        menuRadius = (int)(mPointX/5.5);
        computeCoordinates();
    }

    private void setupStones() {
        mStones = new BigStone[STONE_COUNT];
        BigStone stone;
        int angle = 270;
        mDegreeDelta = 360 / STONE_COUNT;

        for (int index = 0; index < STONE_COUNT; index++) {
            stone = new BigStone();
            if (angle >= 360) {
                angle -= 360;
            }else if(angle < 0){
                angle += 360;
            }
            stone.angle = angle;
/*            stone.bitmap = BitmapFactory.decodeResource(getResources(),
                    startMenu + index);*/
            stone.bitmap = getBimapFromView(startMenu + index);
            angle += mDegreeDelta;

            mStones[index] = stone;
        }
    }

    private Bitmap getBimapFromView(int resId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.float_view_item, null);
        view.findViewById(R.id.float_item_icon).setBackgroundResource(resId);
        TextView textView = (TextView) view.findViewById(R.id.float_item_text);
       // int count = RingCentral.getUnreadMessageAccount(getContext());
        int count = 6;
        MktLog.e(TAG, "count:" + count);
        if (resId == R.drawable.menu2 && count > 0) {
            textView.setText(String.valueOf(count));
        } else {
            textView.setVisibility(View.GONE);
        }
        view.setDrawingCacheEnabled(true);
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    public void updateMessageCount() {
        mStones[1].bitmap = getBimapFromView(startMenu + 1);
        invalidate();
    }

    private void resetStonesAngle(float x, float y) {
        int angle = computeCurrentAngle(x, y);
        Log.d("RoundSpinView", "angle:" + angle);
        for (int index = 0; index < STONE_COUNT; index++) {
            mStones[index].angle = angle;
            angle += mDegreeDelta;
        }
    }

    private void computeCoordinates() {
        BigStone stone;
        for (int index = 0; index < STONE_COUNT; index++) {
            stone = mStones[index];
            stone.x = mPointX
                    + (float) (mRadius * Math.cos(Math.toRadians(stone.angle)));
            stone.y = mPointY
                    + (float) (mRadius * Math.sin(Math.toRadians(stone.angle)));
        }
    }

    private int computeCurrentAngle(float x, float y) {
        float distance = (float) Math
                .sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
                        * (y - mPointY)));
        int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
        if (y < mPointY) {
            degree = -degree;
        }

        Log.d(TAG, "x:" + x + ",y:" + y + ",degree:" + degree);
        return degree;
    }

    private double startAngle;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x, y;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = (int) event.getX();
            y = (int) event.getY();
            mCur = getInCircle(x, y);
            if (mCur == -1) {
                startAngle = computeCurrentAngle(x, y);
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            x = (int) event.getX();
            y = (int) event.getY();
            if (mCur != -1) {
                mStones[mCur].x = x;
                mStones[mCur].y = y;
                invalidate();
            } else {
                double currentAngle = computeCurrentAngle(x, y);
                rotateButtons(startAngle - currentAngle);
                startAngle = currentAngle;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            x = (int) event.getX();
            y = (int) event.getY();
            if (mCur != -1) {
                computeCoordinates();
                int cur = getInCircle(x, y);
                if (cur != mCur && cur != -1) {
                    int angle = mStones[mCur].angle;
                    mStones[mCur].angle = mStones[cur].angle;
                    mStones[cur].angle = angle;
                }
                computeCoordinates();
                invalidate();
                mCur = -1;
            }
        }

        // set the touched quadrant to true
        quadrantTouched[getQuadrant(event.getX() - mPointX,
                mPointY - event.getY())] = true;
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // get the quadrant of the start and the end of the fling
            int q1 = getQuadrant(e1.getX() - mPointX, mPointY - e1.getY());
            int q2 = getQuadrant(e2.getX() - mPointX, mPointY - e2.getY());

            // the inversed rotations
            if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math
                    .abs(velocityY))
                    || (q1 == 3 && q2 == 3)
                    || (q1 == 1 && q2 == 3)
                    || (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math
                    .abs(velocityY))
                    || ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
                    || ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
                    || (q1 == 2 && q2 == 4 && quadrantTouched[3])
                    || (q1 == 4 && q2 == 2 && quadrantTouched[3])) {

                // CircleLayout.this.post(new FlingRunnable(-1
                // * (velocityX + velocityY)));
                new Thread(new FlingRunnable(velocityX+velocityY)).start();
            } else {
                // the normal rotation
                // CircleLayout.this
                // .post(new FlingRunnable(velocityX + velocityY));
                new Thread(new FlingRunnable(-(velocityX+velocityY))).start();
            }

            return true;

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            int cur = getInCircle((int)e.getX(),(int)e.getY());
//            if(cur!=-1){
                if(mListener!=null){
                    mListener.onSingleTapUp(cur);
                }
//				Toast.makeText(getContext(), "position:"+cur, 0).show();
                return false;
//            }
//            else{

//            }
//            return false;
        }

    }

    private class FlingRunnable implements Runnable {

        private float velocity;

        public FlingRunnable(float velocity){
            this.velocity = velocity;
        }

        @Override
        public void run() {
            if(Math.abs(velocity)>=200){
                Message message = Message.obtain();
                message.what = TO_ROTATE_BUTTON;
                message.obj = velocity;
                handler.sendMessage(message);
            }
        }

    }

    /**
     * @return The selected quadrant.
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
        }
        return y >= 0 ? 2 : 3;
    }

    private void rotateButtons(double degree) {
        for (int i = 0; i < STONE_COUNT; i++) {
            mStones[i].angle -= degree;
            if (mStones[i].angle < 0) {
                mStones[i].angle += 360;
            }else if(mStones[i].angle >=360){
                mStones[i].angle -= 360;
            }
        }

        computeCoordinates();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(mPointX, mPointY, mRadius, mPaint);
        for (int index = 0; index < STONE_COUNT; index++) {
            if (!mStones[index].isVisible)
                continue;
            drawInCenter(canvas, mStones[index].bitmap, mStones[index].x,
                    mStones[index].y);
        }
    }

    private void drawInCenter(Canvas canvas, Bitmap bitmap, float left,
                              float top) {
        Rect dst = new Rect();
        dst.left = (int) (left - menuRadius);
        dst.right = (int) (left + menuRadius);
        dst.top = (int) (top - menuRadius);
        dst.bottom = (int) (top + menuRadius);
        canvas.setDrawFilter(pfd);
        canvas.drawBitmap(bitmap, null, dst, mPaint);
    }

    private int getInCircle(int x, int y) {
        for (int i = 0; i < STONE_COUNT; i++) {
            BigStone stone = mStones[i];
            int mx = (int) stone.x;
            int my = (int) stone.y;
            if (((x - mx) * (x - mx) + (y - my) * (y - my)) < menuRadius
                    * menuRadius) {
                return i;
            }
        }
        return -1;
    }

    public void setOnRoundSpinViewListener(onRoundSpinViewListener listener){
        this.mListener = listener;
    }

    class BigStone {

        Bitmap bitmap;

        int angle;

        float x;

        float y;

        boolean isVisible = true;
    }
}