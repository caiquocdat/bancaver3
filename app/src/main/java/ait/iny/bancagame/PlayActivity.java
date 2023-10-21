package ait.iny.bancagame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ait.iny.bancagame.databinding.ActivityPlayBinding;

public class PlayActivity extends AppCompatActivity {
    private ActivityPlayBinding activityPlayBinding;
    private ArrayList<Integer> fishImages;
    private ArrayList<Integer> fishImagesLeft;
    private ArrayList<Integer> fishImagesRight;
    private CountDownTimer countDownTimer;
    private long timeInMillis = 580000;
    private int point = 0;
    private int countBullet = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPlayBinding = ActivityPlayBinding.inflate(getLayoutInflater());
        View view = activityPlayBinding.getRoot();
        setContentView(view);
        hideSystemUI();
        setUpTime();
        fishImages = new ArrayList<>();
        fishImagesLeft = new ArrayList<>();
        fishImagesLeft.add(R.drawable.img_fish_1);
        fishImagesLeft.add(R.drawable.img_fish_2);
        fishImagesLeft.add(R.drawable.img_fish_3);
        fishImagesLeft.add(R.drawable.img_fish_4);
        fishImagesRight = new ArrayList<>();
        fishImagesRight.add(R.drawable.img_fish_5);
        fishImagesRight.add(R.drawable.img_fish_6);
        fishImagesRight.add(R.drawable.img_fish_7);
        fishImagesRight.add(R.drawable.img_fish_8);
        fishImagesRight.add(R.drawable.img_fish_9);
        fishImagesRight.add(R.drawable.img_fish_10);
        fishImagesRight.add(R.drawable.img_fish_11);
        fishImagesRight.add(R.drawable.img_fish_12);
        fishImages.add(R.drawable.img_fish_1);
        fishImages.add(R.drawable.img_fish_2);
        fishImages.add(R.drawable.img_fish_3);
        activityPlayBinding.brgRel.post(new Runnable() {
            @Override
            public void run() {
                for (int fishResId : fishImagesLeft) {
                    animateFish(fishResId, true);
                }
                for (int fishResId : fishImagesRight) {
                    animateFish(fishResId, false);
                }
            }
        });
        activityPlayBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                countDownTimer.cancel();
            }
        });

        activityPlayBinding.brgRel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (countBullet > 0) {
                    countBullet = countBullet - 1;
                    activityPlayBinding.countBulletTv.setText("Đạn: " + countBullet);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) { // Khi người dùng nhấn vào màn hình
                        float touchX = event.getX(); // Lấy tọa độ X của sự kiện touch
                        float touchY = event.getY(); // Lấy tọa độ Y của sự kiện touch
                        Log.d("ClickEvent", "Clicked at X: " + touchX + ", Y: " + touchY);
                        shootBullet(touchX, touchY); // Gọi hàm để bắn viên đạn
                    }
                }else{
                   countBullet=300;
                }
                return true; // Trả về true để thông báo rằng sự kiện đã được xử lý
            }
        });
    }

    private void setUpTime() {
        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Cập nhật giao diện mỗi giây
                String timeText = String.format(Locale.getDefault(), "Time: %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                TextView timeTv = findViewById(R.id.timeTv);
                timeTv.setText(timeText);
            }

            public void onFinish() {
                // Xử lý khi thời gian kết thúc, ví dụ thông báo hết giờ
                Toast.makeText(PlayActivity.this, "Yeah bạn đã giành được " + point + " điểm!", Toast.LENGTH_SHORT).show();
                DatabaseHelper db= new DatabaseHelper(PlayActivity.this);
                db.insertData(point);
                finish();
            }
        }.start();
    }


    private void shootBullet(float touchX, float touchY) {
        ImageView bulletImageView = new ImageView(this);
        bulletImageView.setImageResource(R.drawable.img_bullet);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);  // Kích thước tùy chỉnh cho viên đạn
        bulletImageView.setLayoutParams(params);

        activityPlayBinding.brgRel.addView(bulletImageView);  // Thêm viên đạn vào RelativeLayout

        float startX = activityPlayBinding.brgRel.getWidth() / 2; // Tọa độ X ban đầu là giữa màn hình
        float startY = activityPlayBinding.brgRel.getHeight(); // Tọa độ Y ban đầu ở đáy màn hình

        // Di chuyển viên đạn theo hướng chỉ tay
        ObjectAnimator bulletXAnimator = ObjectAnimator.ofFloat(bulletImageView, "translationX", startX, touchX);
        ObjectAnimator bulletYAnimator = ObjectAnimator.ofFloat(bulletImageView, "translationY", startY, touchY);

        AnimatorSet bulletAnimatorSet = new AnimatorSet();
        bulletAnimatorSet.playTogether(bulletXAnimator, bulletYAnimator);
        bulletAnimatorSet.setDuration(500); // Độ dài thời gian di chuyển
        bulletAnimatorSet.start();

        bulletAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < activityPlayBinding.brgRel.getChildCount(); i++) {
                    View child = activityPlayBinding.brgRel.getChildAt(i);
                    Object tag = child.getTag();
                    if (tag != null && (fishImagesRight.contains((Integer) tag) || fishImagesLeft.contains((Integer) tag))) {
                        Rect fishRect = new Rect((int) child.getX(), (int) child.getY(),
                                (int) child.getX() + child.getWidth(),
                                (int) child.getY() + child.getHeight());

                        if (fishRect.contains((int) touchX + 100, (int) touchY + 100)) {
                            countBullet = countBullet + 1;
                            Random random = new Random();
                            int randomValue = 10 + random.nextInt(21); // Kết quả sẽ là một số từ 10 đến 30
                            point = point + randomValue;
                            activityPlayBinding.countBulletTv.setText("Đạn: " + countBullet);
                            activityPlayBinding.pointTv.setText("Điểm: " + point);
                            activityPlayBinding.brgRel.removeView(child);
                            break;
                        }
                    }
                }


                bulletImageView.setImageResource(R.drawable.img_bum);
                RelativeLayout.LayoutParams explosionParams = new RelativeLayout.LayoutParams(200, 200);
                bulletImageView.setLayoutParams(explosionParams);

                bulletImageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityPlayBinding.brgRel.removeView(bulletImageView);
                    }
                }, 500);
            }
        });
    }

    private boolean isBulletOutsideScreen(ImageView bulletImageView) {
        return bulletImageView.getX() <= 0 || bulletImageView.getX() >= activityPlayBinding.brgRel.getWidth() ||
                bulletImageView.getY() <= 0 || bulletImageView.getY() >= activityPlayBinding.brgRel.getHeight();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void animateFish(int fishResId, boolean startFromLeft) {
        ImageView fishImageView = new ImageView(this);
        fishImageView.setTag(fishResId);
        fishImageView.setImageResource(fishResId);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);

        int randomMarginTop = dpToPx(70 + new Random().nextInt(201));
        params.topMargin = randomMarginTop;

        final float startX = startFromLeft ? 0f : activityPlayBinding.brgRel.getWidth();
        final float endX = startFromLeft ? activityPlayBinding.brgRel.getWidth() : 0f;

        fishImageView.setLayoutParams(params);

        activityPlayBinding.brgRel.addView(fishImageView);

        // Tạo một thời gian di chuyển ngẫu nhiên từ 3000 đến 7000 ms
        int randomDuration = 6000 + new Random().nextInt(8001);

        ValueAnimator fishAnimator = ValueAnimator.ofFloat(startX, endX);
        fishAnimator.setDuration(randomDuration);
        fishAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                fishImageView.setTranslationX(value);
                float currentX = fishImageView.getX() + value;
                float currentY = fishImageView.getY();

            }
        });
        fishAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                activityPlayBinding.brgRel.removeView(fishImageView);
                // Tùy vào hướng di chuyển mà chọn danh sách cá tương ứng
                if (startFromLeft) {
                    animateFish(fishImagesLeft.get(new Random().nextInt(fishImagesLeft.size())), true);
                } else {
                    animateFish(fishImagesRight.get(new Random().nextInt(fishImagesRight.size())), false);
                }
            }
        });
        fishAnimator.start();
    }


    private boolean isBulletCollidingWithAnyFish(ImageView bulletImageView) {
        Rect bulletRect = new Rect(
                (int) bulletImageView.getX(),
                (int) bulletImageView.getY(),
                (int) (bulletImageView.getX() + bulletImageView.getWidth()),
                (int) (bulletImageView.getY() + bulletImageView.getHeight())
        );

        for (int i = 0; i < activityPlayBinding.brgRel.getChildCount(); i++) {
            View child = activityPlayBinding.brgRel.getChildAt(i);
            if (child instanceof ImageView && fishImages.contains(((ImageView) child).getDrawable())) {
                Rect fishRect = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                if (bulletRect.intersect(fishRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}