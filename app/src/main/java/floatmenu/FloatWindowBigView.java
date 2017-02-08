package floatmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.nickgao.R;


public class FloatWindowBigView extends LinearLayout {
    private static final String TAG = "[RC]FloatWindowBigView";

    public static int viewWidth;

    public static int viewHeight;
    private RoundSpinView rsv_test;

    public FloatWindowBigView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_window_layout);
        rsv_test = (RoundSpinView)findViewById(R.id.rsv_test);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        rsv_test.setOnRoundSpinViewListener(new RoundSpinView.onRoundSpinViewListener() {
            @Override
            public void onSingleTapUp(int position) {
                // TODO Auto-generated method stub
                switchWindow();
                switch (position) {
                    case 0:
//                        Intent intent = new Intent(context, RingOut.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        MktLog.e(TAG, "Start to open ringout");
//                        context.startActivity(intent);
                        break;
                    case 1:
//                        Intent intent1 = new Intent(context, RingCentral.class);
//                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent1.putExtra(RCMConstants.EXTRA_FLOATING_WINDOW_FLAG, true);
//                        intent1.putExtra(RCMConstants.ACTION_START_ACTIVITY_BY_FLOATING_WINDOW, RingCentral.TAB_MESSAGES_ID);
//                        context.startActivity(intent1);
                        break;
                    case 2:
//                        Intent intent2 = new Intent(context, RingCentral.class);
//                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent2.putExtra(RCMConstants.EXTRA_FLOATING_WINDOW_FLAG, true);
//                        intent2.putExtra(RCMConstants.ACTION_START_ACTIVITY_BY_FLOATING_WINDOW, RingCentral.TAB_FAVORITES_ID);
//                        context.startActivity(intent2);
                        break;

                    case 3:
//                        Intent intent3 = new Intent(context, RingCentral.class);
//                        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent3.putExtra(RCMConstants.EXTRA_FLOATING_WINDOW_FLAG, true);
//                        intent3.putExtra(RCMConstants.ACTION_START_ACTIVITY_BY_FLOATING_WINDOW, RingCentral.TAB_CONTACTS_ID);
//                        context.startActivity(intent3);
                        break;

                    case 4:
//                        Intent intent4 = new Intent(context, RingCentral.class);
//                        intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent4.putExtra(RCMConstants.EXTRA_FLOATING_WINDOW_FLAG, true);
//                        intent4.putExtra(RCMConstants.ACTION_START_ACTIVITY_BY_FLOATING_WINDOW, RingCentral.TAB_CALL_LOG_ID);
//                        context.startActivity(intent4);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void switchWindow() {
        MyWindowManager.removeBigWindow(getContext());
        MyWindowManager.createSmallWindow(getContext());
    }
}
