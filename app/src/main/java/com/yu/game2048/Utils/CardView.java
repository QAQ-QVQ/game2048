package com.yu.game2048.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yu.game2048.R;

/**
 * CREATED BY DY ON 2019/8/22.
 * TIME BY 11:48.
 **/
public class CardView extends FrameLayout {
    // 卡片文字
    private int num = 0;
    private TextView tv_num;
    private int radius = 20;
    public CardView(@NonNull Context context) {
        super(context);
        // 初始化TextView
        tv_num = new TextView(getContext());
        // 卡片文字大小
        tv_num.setTextSize(40);
        // 布局控制器，填充满整个父容器
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,10,10,0);
//        setElevation(4f);
        addView(tv_num, lp);
      //  setNum(0);
    }

    public CardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        tv_num.setGravity(Gravity.CENTER);
        SetColor(this.num);
        // 要呈现出来的文字（这里要注意是String类型的）
        if (num <= 0) {
            tv_num.setText("");
        } else {
            tv_num.setText(String.valueOf(num));
        }
    }

    // 两卡片相同的比较方法
    public boolean equals(CardView card) {
        return getNum() == card.getNum();
    }

    public void SetColor(int num){
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);//圆角半径
        String NumColor = "cardBackground"+num;
        gradientDrawable.setColor(getResources().getColor(ResourceUtil.getColorResIDByName(getContext(),NumColor)));
        tv_num.setBackground(gradientDrawable);
        //        switch (num) {
//            case 0:
//                gradientDrawable.setColor(0xffBDB76A);
//                break;
//            case 2:
//                gradientDrawable.setColor(0xffeee4da);
//                break;
//            case 4:
//                gradientDrawable.setColor(0xffede0c8);
//                break;
//            case 8:
//                gradientDrawable.setColor(0xfff2b179);
//                break;
//            case 16:
//                gradientDrawable.setColor(0xfff59563);
//                break;
//            case 32:
//                gradientDrawable.setColor(0xfff67c5f);
//                break;
//            case 64:
//                gradientDrawable.setColor(0xfff65e3b);
//                break;
//            case 128:
//                gradientDrawable.setColor(0xffedcf72);
//                break;
//            case 256:
//                gradientDrawable.setColor(0xffedcc61);
//                break;
//            case 512:
//                gradientDrawable.setColor(0xffedc850);
//                break;
//            case 1024:
//                gradientDrawable.setColor(0xffedc53f);
//                break;
//            case 2048:
//                gradientDrawable.setColor(0xffedc22e);
//                break;
//            default:
//                gradientDrawable.setColor(0xff3c3a32);
//                break;
//        }

    }
}
