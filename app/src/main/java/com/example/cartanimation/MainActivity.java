package com.example.cartanimation;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout animation_frame;
    FrameLayout detail_product_bag_framelayout;
    TextView detail_product_add_tv;
    ImageView detail_product_bag_image;
    TextView detail_product_bag_num;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        StatusBarUtil.setStatusBarColor(this, R.color.fit_module_bg);
        StatusBarUtil.setLightStatusBar(this, true);
        StatusBarUtil.transparencyBar(this);

        setContentView(R.layout.activity_main);

        animation_frame = findViewById(R.id.animation_frame);
        detail_product_bag_framelayout = findViewById(R.id.detail_product_bag_framelayout);
        detail_product_add_tv = findViewById(R.id.detail_product_add_tv);
        detail_product_bag_image = findViewById(R.id.detail_product_bag_image);
        detail_product_bag_num = findViewById(R.id.detail_product_bag_num);

        detail_product_add_tv.setOnClickListener(this);
    }

    //动画开始
    public void addBagAnimation(){
        CircleImageView circleImageView = newImageview();
        shapeScaleAnimation(circleImageView);
    }

    //创建一个图片并显示
    public CircleImageView newImageview(){

        CircleImageView circleImageView = new CircleImageView(MainActivity.this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(getApplicationContext(),300),dip2px(getApplicationContext(),300));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        circleImageView.setLayoutParams(layoutParams);
        animation_frame.addView(circleImageView);
        circleImageView.setImageResource(R.drawable.pic);
        animation_frame.setVisibility(View.VISIBLE);
        return circleImageView;
    }

    //图片缩放
    public void shapeScaleAnimation(final CircleImageView imageView){
        if (MainActivity.this.isDestroyed()){
            return;
        }
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(imageView, "ScaleX", 1f, 0.4f);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(imageView, "ScaleY", 1f, 0.4f);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 1f);
        set.play(animatorScaleX).with(animatorScaleY).with(animatorAlpha);
        set.setDuration(500);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dropAnimation(imageView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    //抛物线动画
    public void dropAnimation(final CircleImageView imageView){
        if (MainActivity.this.isDestroyed()){
            return;
        }
        int[] beginLocation = new int[2];
        int[] endLocation = new int[2];
        imageView.getLocationInWindow(beginLocation);
        detail_product_bag_image.getLocationInWindow(endLocation);

        Point startP = new Point();
        Point endP = new Point();

        startP.x = dip2px(getApplicationContext(),30);
        startP.y = 0;

        endP.x = endLocation[0] - beginLocation[0];
        endP.y = endLocation[1] - beginLocation[1];

        int pointX = (int) (startP.x );
        int pointY = (int) (startP.y);
        Point controllPoint = new Point(pointX, pointY);

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(imageView, "ScaleX", 0.4f, 0f);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(imageView, "ScaleY", 0.4f, 0f);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator2(controllPoint),startP, endP);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Point point = (Point) valueAnimator.getAnimatedValue();
                imageView.setX(point.x);
                imageView.setY(point.y);
            }
        });

        set.play(valueAnimator).with(animatorAlpha).with(animatorScaleX).with(animatorScaleY);
        set.setDuration(600);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation_frame.setVisibility(View.GONE);
                animation_frame.removeView(imageView);
                count += 1;
                detail_product_bag_num.setText(count + "");
                shakeAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.animation_frame:
                break;
            case R.id.detail_product_bag_framelayout:
                break;
            case R.id.detail_product_add_tv:
                addBagAnimation();
                break;
        }
    }

    //估值器
    public class BizierEvaluator2 implements TypeEvaluator<Point> {

        private Point controllPoint;

        public BizierEvaluator2(Point controllPoint) {
            this.controllPoint = controllPoint;
        }

        @Override
        public Point evaluate(float t, Point startValue, Point endValue) {
            int x = (int) ((1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * controllPoint.x + t * t * endValue.x);
            int y = (int) ((1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * controllPoint.y + t * t * endValue.y);
            return new Point(x, y);
        }
    }

    //购物车摇晃
    public void shakeAnimation(){
        if (MainActivity.this.isDestroyed()){
            return;
        }

        Animation rotateAnim = new RotateAnimation(-10f, 10f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(1000 / 10);
        rotateAnim.setRepeatMode(Animation.REVERSE);
        rotateAnim.setRepeatCount(5);
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        AnimationSet smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(rotateAnim);

        detail_product_bag_framelayout.startAnimation(smallAnimationSet);
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
