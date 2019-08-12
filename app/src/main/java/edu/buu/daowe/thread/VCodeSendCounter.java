package edu.buu.daowe.thread;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import edu.buu.daowe.R;

public class VCodeSendCounter extends CountDownTimer {
    TextView mTextView;

    public VCodeSendCounter(TextView tv, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        mTextView = tv;
    }

    @Override
    public void onTick(long l) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(l / 1000 + "秒后可重新发送"); //设置倒计时时间
        mTextView.setBackgroundResource(R.drawable.bg_identify_code_press); //设置按钮为灰
        SpannableString spannableString = new SpannableString(mTextView.getText().toString());
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mTextView.setText(spannableString);
    }

    @Override
    public void onFinish() {

        mTextView.setText("重新获取验证码");
        mTextView.setClickable(true);//重新获得点击
        mTextView.setBackgroundResource(R.drawable.bg_identify_code_normal);

    }
}
