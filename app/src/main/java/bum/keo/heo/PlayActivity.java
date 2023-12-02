package bum.keo.heo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import bum.keo.heo.R;
import bum.keo.heo.databinding.ActivityPlayBinding;


public class PlayActivity extends AppCompatActivity {
    private ActivityPlayBinding activityPlayBinding;
    private ArrayList<Integer> fishImages;
    private ArrayList<Integer> fishImagesLeft;
    private ArrayList<Integer> fishImagesRight;
    private CountDownTimer countDownTimer;
    private long timeInMillis = 580000;
    private int point = 0;
    private int countBullet = 300;
    private boolean isPaused = false;
    private long timeRemaining = 0;
    private int check;
    private int value=0;
    private static final String PREFS_NAME = "DataGT";
    private static final String DATE_KEY = "SelectedGT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPlayBinding = ActivityPlayBinding.inflate(getLayoutInflater());
        View view = activityPlayBinding.getRoot();
        setContentView(view);
        hideSystemUI();
        setUpTime();
        getData();
        fishImages = new ArrayList<>();
        fishImagesLeft = new ArrayList<>();
        fishImagesLeft.add(R.drawable.img_fish_l_1);
        fishImagesLeft.add(R.drawable.img_fish_l_2);
        fishImagesLeft.add(R.drawable.img_fish_l_3);
        fishImagesLeft.add(R.drawable.img_fish_l_4);
        fishImagesLeft.add(R.drawable.img_fish_l_5);
        fishImagesLeft.add(R.drawable.img_fish_l_6);
        fishImagesLeft.add(R.drawable.img_fish_l_7);
        fishImagesLeft.add(R.drawable.img_fish_l_8);
        fishImagesRight = new ArrayList<>();
        fishImagesRight.add(R.drawable.img_fish_r_1);
        fishImagesRight.add(R.drawable.img_fish_r_2);
        fishImagesRight.add(R.drawable.img_fish_r_3);
        fishImagesRight.add(R.drawable.img_fish_r_4);
        fishImagesRight.add(R.drawable.img_fish_r_5);
        fishImagesRight.add(R.drawable.img_fish_r_6);
        fishImagesRight.add(R.drawable.img_fish_r_7);
        fishImagesRight.add(R.drawable.img_fish_r_8);
        fishImagesRight.add(R.drawable.img_fish_r_9);
        fishImagesRight.add(R.drawable.img_fish_r_10);
        fishImages.add(R.drawable.img_fish_r_1);
        fishImages.add(R.drawable.img_fish_r_2);
        fishImages.add(R.drawable.img_fish_r_3);
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

        activityPlayBinding.resumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countDownTimer != null && !isPaused) {
                    countDownTimer.cancel();
                    isPaused = true;
                    timeRemaining = timeInMillis;
                }
                showAlertResumDialog();
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

    private void getData() {
        Intent intent =getIntent();
        value=intent.getIntExtra("level_id",0);
    }

    private void setUpTime() {
        countDownTimer = new CountDownTimer(isPaused ? timeRemaining : timeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Cập nhật giao diện mỗi giây
                timeInMillis = millisUntilFinished;
                String timeText = String.format(Locale.getDefault(), "%02d:%02ds",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                TextView timeTv = findViewById(R.id.timeTv);
                timeTv.setText(timeText);
            }

            public void onFinish() {
                // Xử lý khi thời gian kết thúc, ví dụ thông báo hết giờ
//                Toast.makeText(PlayActivity.this, "Yeah bạn đã giành được " + point + " điểm!", Toast.LENGTH_SHORT).show();
                DatabaseHelper db= new DatabaseHelper(PlayActivity.this);
                db.insertData(point);
                showAlertThatBaiDialog();
            }
        }.start();
        if(!isPaused) {
            countDownTimer.start();
        }
    }
    private void showAlertResumDialog() {
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // không hiển thị tiêu đề của hộp thoại
        customDialog.setContentView(R.layout.item_resum_dialog);  // áp dụng layout của bạn
        customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.setCancelable(false);

        // Tùy chỉnh thuộc tính cho hộp thoại nếu muốn (như kích thước, vị trí, ...)
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);  // Đặt hộp thoại ở giữa màn hình
        }


        ImageView choiLaiImg = customDialog.findViewById(R.id.choiLaiImg);
        ImageView choiTiepImg = customDialog.findViewById(R.id.choiTiepImg);
        ImageView thoatImg = customDialog.findViewById(R.id.thoatImg);
        thoatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
               countDownTimer.cancel();
            }
        });  // đóng hộp thoại khi bấm vào hình exit
        choiLaiImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point=0;
                activityPlayBinding.progressBar.setProgress(point);
               recreate();
            }
        });
        choiTiepImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSystemUI();
                if(isPaused) {
                    setUpTime();
                    isPaused = false;
                }
                customDialog.dismiss();
            }
        });


        customDialog.show();  // Hiển thị hộp thoại
    }
    private void showAlertSettingDialog() {
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // không hiển thị tiêu đề của hộp thoại
        customDialog.setContentView(R.layout.item_setting_dialog);  // áp dụng layout của bạn
        customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.setCancelable(true);

        // Tùy chỉnh thuộc tính cho hộp thoại nếu muốn (như kích thước, vị trí, ...)
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);  // Đặt hộp thoại ở giữa màn hình
        }


        ImageView lienHeImg = customDialog.findViewById(R.id.lienHeImg);
        ImageView gioiThieuImg = customDialog.findViewById(R.id.gioiThieuImg);
        ImageView chinhSachImg = customDialog.findViewById(R.id.chinhSachImg);
        // Sử dụng OnCancelListener để xử lý sự kiện khi dialog bị hủy
        customDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                hideSystemUI();
                if(isPaused) {
                    setUpTime();
                    isPaused = false;
                }
                customDialog.dismiss();
            }
        });
        gioiThieuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSystemUI();
                check=1;
                showAlertSettingDetailDialog(check);
                customDialog.dismiss();
            }
        });  // đóng hộp thoại khi bấm vào hình exit
        lienHeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               hideSystemUI();
               check=2;
                showAlertSettingDetailDialog(check);
                customDialog.dismiss();
            }
        });
        chinhSachImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSystemUI();
                check=3;
                showAlertSettingDetailDialog(check);
                customDialog.dismiss();
            }
        });


        customDialog.show();  // Hiển thị hộp thoại
    }

    private void showAlertSettingDetailDialog(int check) {
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // không hiển thị tiêu đề của hộp thoại
        customDialog.setContentView(R.layout.item_setting_detail_dialog);  // áp dụng layout của bạn
        customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.setCancelable(true);

        // Tùy chỉnh thuộc tính cho hộp thoại nếu muốn (như kích thước, vị trí, ...)
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);  // Đặt hộp thoại ở giữa màn hình
        }

        LinearLayout topLinear= customDialog.findViewById(R.id.topLinear);
        LinearLayout gioiThieuLinear= customDialog.findViewById(R.id.gioiThieuLinear);
        ImageView backImg = customDialog.findViewById(R.id.backImg);
        TextView lableTv = customDialog.findViewById(R.id.lableTv);
        TextView lableDetailTv = customDialog.findViewById(R.id.lableDetailTv);
        ImageView zaloImg = customDialog.findViewById(R.id.zaloImg);
        ImageView messagerImg = customDialog.findViewById(R.id.messsagerImg);
        ImageView telegramImg = customDialog.findViewById(R.id.telegramImg);
        // Sử dụng OnCancelListener để xử lý sự kiện khi dialog bị hủy
        if (check==3){
            lableTv.setText("Chính sách bảo mật");
        }else if (check==2){
            topLinear.setVisibility(View.INVISIBLE);
            lableDetailTv.setVisibility(View.INVISIBLE);
            gioiThieuLinear.setVisibility(View.VISIBLE);
        }
        customDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                hideSystemUI();
                if(isPaused) {
                    setUpTime();
                    isPaused = false;
                }
                customDialog.dismiss();
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSystemUI();
                customDialog.dismiss();
            }
        });  // đóng hộp thoại khi bấm vào hình exit
        zaloImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thay thế "https://zalo.me" bằng link trang chủ hoặc trang cụ thể của Zalo
                Uri zaloUri = Uri.parse("https://zalo.me");
                Intent intent = new Intent(Intent.ACTION_VIEW, zaloUri);
                startActivity(intent);
            }
        });

        messagerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thay thế "https://messenger.com" bằng link trang chủ hoặc trang cụ thể của Messenger
                Uri messengerUri = Uri.parse("https://messenger.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, messengerUri);
                startActivity(intent);
            }
        });

        telegramImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thay thế "https://telegram.org" bằng link trang chủ hoặc trang cụ thể của Telegram
                Uri telegramUri = Uri.parse("https://telegram.org");
                Intent intent = new Intent(Intent.ACTION_VIEW, telegramUri);
                startActivity(intent);
            }
        });



        customDialog.show();  // Hiển thị hộp thoại
    }

    private void showAlertThatBaiDialog() {
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // không hiển thị tiêu đề của hộp thoại
        customDialog.setContentView(R.layout.item_thatbai_dialog);  // áp dụng layout của bạn
        customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.setCancelable(false);

        // Tùy chỉnh thuộc tính cho hộp thoại nếu muốn (như kích thước, vị trí, ...)
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);  // Đặt hộp thoại ở giữa màn hình
        }


        ImageView choiTiepImg = customDialog.findViewById(R.id.choiTiepImg);
        ImageView thoat1Img = customDialog.findViewById(R.id.thoat_1Img);
        ImageView thoat2Img = customDialog.findViewById(R.id.thoat_2Img);
        thoat1Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                countDownTimer.cancel();
            }
        });
        thoat2Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                countDownTimer.cancel();
            }
        });  // đó// đóng hộp thoại khi bấm vào hình exit
        choiTiepImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point=0;
                activityPlayBinding.progressBar.setProgress(point);
                recreate();
            }
        });


        customDialog.show();  // Hiển thị hộp thoại
    }
    private void showAlertWinDialog() {
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // không hiển thị tiêu đề của hộp thoại
        customDialog.setContentView(R.layout.item_thanhcong_dialog);  // áp dụng layout của bạn
        customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.setCancelable(false);

        // Tùy chỉnh thuộc tính cho hộp thoại nếu muốn (như kích thước, vị trí, ...)
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);  // Đặt hộp thoại ở giữa màn hình
        }


        ImageView choiTiepImg = customDialog.findViewById(R.id.choiTiepImg);
        ImageView thoat1Img = customDialog.findViewById(R.id.thoat_1Img);
        ImageView thoat2Img = customDialog.findViewById(R.id.thoat_2Img);
        thoat1Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                countDownTimer.cancel();
            }
        });
        thoat2Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                countDownTimer.cancel();
            }
        });  // đó// đóng hộp thoại khi bấm vào hình exit
        choiTiepImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point=0;
                activityPlayBinding.progressBar.setProgress(point);
                recreate();
            }
        });


        customDialog.show();  // Hiển thị hộp thoại
    }


    private void shootBullet(float touchX, float touchY) {
        ImageView bulletImageView = new ImageView(this);
        bulletImageView.setImageResource(R.drawable.img_bullet);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);  // Kích thước tùy chỉnh cho viên đạn
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
                            activityPlayBinding.progressBar.setProgress(point);
                            if (point>1000){
                                if(countDownTimer != null && !isPaused) {
                                    countDownTimer.cancel();
                                    isPaused = true;
                                    timeRemaining = timeInMillis;
                                }
                                showAlertWinDialog();
                                saveDateToPreferences(value+1);
                            }
                            activityPlayBinding.brgRel.removeView(child);
                            break;
                        }
                    }
                }


                bulletImageView.setImageResource(R.drawable.img_bum);
                RelativeLayout.LayoutParams explosionParams = new RelativeLayout.LayoutParams(400, 400);
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
    private void saveDateToPreferences(int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(DATE_KEY, value);
        editor.apply();
    }

    private int getValueFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(DATE_KEY, 1); // "Chưa lưu" là giá trị mặc định nếu chưa có giá trị nào được lưu.
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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);

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