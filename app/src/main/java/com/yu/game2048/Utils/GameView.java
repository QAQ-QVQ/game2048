package com.yu.game2048.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.Toast;

import com.yu.game2048.MainActivity;
import com.yu.game2048.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * CREATED BY DY ON 2019/8/22.
 * TIME BY 11:06.
 **/
public class GameView extends GridLayout {
    private int cardWidth;
    // 记录卡片的二维数组
    public static CardView[][] cards = new CardView[4][4];
    private boolean CanAdd;
    private List<Point> point = new ArrayList<>();
    private int radius = 20;
  //  private SharedPreferences sharedPreferences;
  //  private SharedPreferences.Editor editor;
    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addCard(GetCardWidth(), GetCardWidth());
        initView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    // 初始化
    private void initView() {
        //换行
        setColumnCount(4);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);//圆角半径
        gradientDrawable.setColor(getResources().getColor(R.color.gameBackground));
        setBackground(gradientDrawable);
        //设置sp
        MainActivity.getMainActivity().sharedPreferences = MainActivity.getMainActivity().getSharedPreferences("map", Context.MODE_PRIVATE);
        MainActivity.getMainActivity().editor = MainActivity.getMainActivity().sharedPreferences.edit();
        dialog();
//        addCard(cardWidth, cardWidth);

        // 识别手势
        setOnTouchListener(new View.OnTouchListener() {
            // 起始点和偏移点
            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 交互逻辑 :我们其实只要知道两点，用户手指按下的坐标点和手指离开的坐标点，然后进行比对，就能识别出用户的意图了
                 */
                switch (event.getAction()) {
                    // 手指按下
                    case MotionEvent.ACTION_DOWN:
                        // 记录按下的x,y坐标
                        startX = event.getX();
                        startY = event.getY();

                        break;
                    // 手指离开
                    case MotionEvent.ACTION_UP:
                        // 手指离开之后计算偏移量(离开的位置-按下的位置在进行判断是往哪个方向移动)
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        CanAdd = false;
                        // 开始识别方向
                        // offsetX 的绝对值大于offsetY的绝对值 说明在水平方向
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            // (直接<0 会有些许误差，我们可以 <-5)
                            if (offsetX < -5) {
                                // 左
                                ToLeft();
                            } else if (offsetX > 5) {
                                // 右
                                ToRight();
                            }
                            // 开始计算垂直方向上下的滑动
                        } else {
                            if (offsetY < -5) {
                                // 上
                                ToUp();
                            } else if (offsetY > 5) {
                                // 下
                                ToDown();

                            }
                        }
                        break;
                }
                return true;
            }
        });

     //   MainActivity.getMainActivity().getSharedPreferences().getBoolean("first",true);
    }

    private int GetCardWidth() {
        //屏幕信息的对象
        DisplayMetrics displayMetrics;
        displayMetrics = getResources().getDisplayMetrics();
        //获取屏幕信息
        int cardWidth = displayMetrics.widthPixels;
        //一行有四个卡片，每个卡片占屏幕的四分之一
        return (cardWidth - 45) / 4;
    }

    private boolean dialog() {
            if (!MainActivity.getMainActivity().getSharedPreferences().getString("map", "").equals("")) {
                new AlertDialog.Builder(getContext())
                        .setTitle("发现存档")
                        .setMessage("是否继续？")
                        .setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ContinueGame();
                                    }
                                })
                        .setNegativeButton("否",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.getMainActivity().getSharedPreferences().edit().putString("map", "");
                                        MainActivity.getMainActivity().getSharedPreferences().edit().commit();
                                        startGame();
                                    }
                                }).show();
            }else {
                startGame();
            }
            return false;
    }
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        cardWidth = (Math.min(w, h)) / 4;
//    }

    // 对View进行测量
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        setMeasuredDimension(width, height);
//    }

    // 添加卡片,参数为卡片的宽高，因为他是正方形，所以宽高都是cardWidth
    private void addCard(int cardWidth, int cardHeight) {
        // 创建方块
        CardView card;
        // 循环添加
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                card = new CardView(getContext());
                //记忆
                cards[j][i] = card;
                // num为随机数
                card.setNum(0);
                addView(card, cardWidth, cardHeight);
            }
        }

    }


    // 随机数
    private void addRandom() {
        // 我们新建一个lsit存放空的方块，操作之前清空
        point.clear();
        // 对所有的位置进行遍历
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // 空方块才可以添加数字，有值我们就不添加
                if (cards[j][i].getNum() <= 0) {
                    point.add(new Point(j, i));
                }
            }
        }
        // for循环走完之后我们要取方块
        Point p = point.remove((int) (Math.random() * point.size()));
        // 我们用Math.random()返回一个0-1的数，当大于0.1的时候是2否则就是4，也就是4出现的概率为十分之一
        cards[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
    }

    public void ContinueGame() {
        try {
            MainActivity.getMainActivity().ShowScore(MainActivity.getMainActivity().getSharedPreferences().getInt("score", 0));
            JSONArray jsonArray = new JSONArray(MainActivity.getMainActivity().getSharedPreferences().getString("map", ""));
            int num = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    cards[j][i].setNum((int) jsonArray.get(num));
                    num++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启游戏
    public void startGame() {
        //计分清零
        MainActivity.getMainActivity().ClearScore();
        // 既然是开始游戏，我们就要对所有的值进行清理
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                cards[j][i].setNum(0);
            }
        }
        // 重新添加随机数
        //  addRandom();
        // 我们要添加两个
        //  addRandom();
        endGame();
        endGame();
    }

    private void ToDown() {
        /**
         * 这里的逻辑有三种情况 1.左边为空，直接左滑到最后一格 2.左边碰到的第一个数是相等的，就相加 3.左边碰到的第一个数是不相等的，靠在旁边
         */
        for (int j = 0; j < 4; j++) {
            for (int i = 3; i >= 0; i--) {
                // 往左滑是一行一行去遍历的
                for (int i2 = i - 1; i2 >= 0; i2--) {
                    // 如果说遍历到值
                    if (cards[j][i2].getNum() > 0) {
                        // 如果当前位置上为0,就放在这个位置上去
                        if (cards[j][i].getNum() <= 0) {
//                            cards[j][i].startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.view_anim));
                            cards[j][i].setNum(cards[j][i2].getNum());
                            // 把原来位置上的数字清除
                            cards[j][i2].setNum(0);
                            // 让图形继续遍历
                            i++;
                            CanAdd = true;
                            // 有值，并且还相同
                        } else if (cards[j][i].equals(cards[j][i2])) {
                            // 合并,这里做了一个很巧妙的写法，我们相加，其实2048方块上的数字都是双倍的，所以我们只要原数据*2就可以了
                            cards[j][i].setNum(cards[j][i].getNum() * 2);
                            //开始计分
                            MainActivity.getMainActivity().AddScore(cards[j][i].getNum());
                            // 把原来位置上的数字清除
                            cards[j][i2].setNum(0);
                            CanAdd = true;
                        }
                        break;
                    }
                }
            }
        }
        if (CanAdd) {
            endGame();

        }
    }

    private void ToUp() {
        /**
         * 这里的逻辑有三种情况 1.左边为空，直接左滑到最后一格 2.左边碰到的第一个数是相等的，就相加 3.左边碰到的第一个数是不相等的，靠在旁边
         */
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                // 往左滑是一行一行去遍历的
                for (int i2 = i + 1; i2 < 4; i2++) {
                    // 如果说遍历到值
                    if (cards[j][i2].getNum() > 0) {
                        // 如果当前位置上为0,就放在这个位置上去
                        if (cards[j][i].getNum() <= 0) {
                            cards[j][i].setNum(cards[j][i2].getNum());
                            // 把原来位置上的数字清除
                            cards[j][i2].setNum(0);
                            // 让图形继续遍历
                            i--;
                            CanAdd = true;
                            // 有值，并且还相同
                        } else if (cards[j][i].equals(cards[j][i2])) {
                            // 合并,这里做了一个很巧妙的写法，我们相加，其实2048方块上的数字都是双倍的，所以我们只要原数据*2就可以了
                            cards[j][i].setNum(cards[j][i].getNum() * 2);
                            //开始计分
                            MainActivity.getMainActivity().AddScore(cards[j][i].getNum());
                            // 把原来位置上的数字清除
                            cards[j][i2].setNum(0);
                            CanAdd = true;
                        }
                        break;
                    }
                }
            }
        }
        if (CanAdd) endGame();
    }

    private void ToRight() {
        /**
         * 这里的逻辑有三种情况 1.左边为空，直接左滑到最后一格 2.左边碰到的第一个数是相等的，就相加 3.左边碰到的第一个数是不相等的，靠在旁边
         */
        for (int i = 0; i < 4; i++) {
            for (int j = 3; j >= 0; j--) {
                // 往左滑是一行一行去遍历的
                for (int j2 = j - 1; j2 >= 0; j2--) {
                    // 如果说遍历到值
                    if (cards[j2][i].getNum() > 0) {
                        // 如果当前位置上为0,就放在这个位置上去
                        if (cards[j][i].getNum() <= 0) {
                            cards[j][i].setNum(cards[j2][i].getNum());
                            // 把原来位置上的数字清除
                            cards[j2][i].setNum(0);
                            // 让图形继续遍历
                            j++;
                            CanAdd = true;
                            // 有值，并且还相同
                        } else if (cards[j][i].equals(cards[j2][i])) {
                            // 合并,这里做了一个很巧妙的写法，我们相加，其实2048方块上的数字都是双倍的，所以我们只要原数据*2就可以了
                            cards[j][i].setNum(cards[j][i].getNum() * 2);
                            //开始计分
                            MainActivity.getMainActivity().AddScore(cards[j][i].getNum());
                            // 把原来位置上的数字清除
                            cards[j2][i].setNum(0);
                            CanAdd = true;
                        }
                        break;
                    }
                }
            }
        }
        if (CanAdd) endGame();
    }

    private void ToLeft() {
        /**
         * 这里的逻辑有三种情况 1.左边为空，直接左滑到最后一格 2.左边碰到的第一个数是相等的，就相加 3.左边碰到的第一个数是不相等的，靠在旁边
         */
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // 往左滑是一行一行去遍历的
                for (int j2 = j + 1; j2 < 4; j2++) {
                    // 如果说遍历到值
                    if (cards[j2][i].getNum() > 0) {
                        // 如果当前位置上为0,就放在这个位置上去
                        if (cards[j][i].getNum() <= 0) {
                            cards[j][i].setNum(cards[j2][i].getNum());
                            // 把原来位置上的数字清除
                            cards[j2][i].setNum(0);
                            // 让图形继续遍历
                            j--;
                            CanAdd = true;
                            // 有值，并且还相同
                        } else if (cards[j][i].equals(cards[j2][i])) {
                            // 合并,这里做了一个很巧妙的写法，我们相加，其实2048方块上的数字都是双倍的，所以我们只要原数据*2就可以了
                            cards[j][i].setNum(cards[j][i].getNum() * 2);
                            //开始计分
                            MainActivity.getMainActivity().AddScore(cards[j][i].getNum());
                            // 把原来位置上的数字清除
                            cards[j2][i].setNum(0);
                            CanAdd = true;
                        }
                        break;
                    }
                }
            }
        }
        if (CanAdd) endGame();
    }

    // 游戏结束
    private void endGame() {
        // 我们新建一个lsit存放空的方块，操作之前清空
        point.clear();
        // 在每次添加新的方块的时候判断一下
        int n = 0, k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                //判断格子满没有
                if (cards[j][i].getNum() == 0) {
                    //没满
                    //  n++;
                    point.add(new Point(j, i));
                }
                //满了
                // 判断上下左右是否有相同
                if ((j > 0 && cards[j][i].equals(cards[j - 1][i]))
                        // 右
                        || (j < 3) && cards[j][i].equals(cards[j + 1][i])
                        // 上
                        || (i > 0 && cards[j][i].equals(cards[j][i - 1]))
                        // 下
                        || (i < 3 && cards[j][i].equals(cards[j][i + 1]))) {
                    k++;
                }
            }
        }

        if (point.size() != 0) {
//           addRandom();
            // for循环走完之后我们要取方块
            Point p = point.remove((int) (Math.random() * point.size()));
            // 我们用Math.random()返回一个0-1的数，当大于0.1的时候是2否则就是4，也就是4出现的概率为十分之一
            cards[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
        }

        if ((point.size() == 0) && (k == 0)) {
            //没有相同
            new AlertDialog.Builder(getContext())
                    .setTitle("Sorry，游戏结束！")
                    .setMessage("是否重新开始？")
                    .setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 重新开始
                                    startGame();
                                }
                            })
                    .setNegativeButton("否",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
        }
    }
}
