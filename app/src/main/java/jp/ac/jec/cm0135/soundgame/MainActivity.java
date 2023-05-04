package jp.ac.jec.cm0135.soundgame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private int[] imgButtons = {R.id.ibtn01, R.id.ibtn02, R.id.ibtn03, R.id.ibtn04, R.id.ibtn05, R.id.ibtn06, R.id.ibtn07, R.id.ibtn08};
    private MediaPlayer[] plays = new MediaPlayer[imgButtons.length];
    private int[] answer = new int[imgButtons.length];
    private int nowState = -1;
    private TextView txt;
    private TextView countMsg;
    private ImageButton preSelect = null;
    ImageButton[] buttons = new ImageButton[imgButtons.length];
    private int aa;
    private int totalClicks;
    private String totalClicksStr;
    private int count = 0;
    private VideoView[] mVideoView;

    MediaPlayer answerSound;
    MediaPlayer correctSound;
    MediaPlayer incorrectSound;
    private int clickCount;
    Uri uri; //sample.mp4 설정
    int[] array = new int[buttons.length];

    HashMap<ImageButton, Integer> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = new VideoView[]{findViewById(R.id.screenVideoView1), findViewById(R.id.screenVideoView2), findViewById(R.id.screenVideoView3), findViewById(R.id.screenVideoView4), findViewById(R.id.screenVideoView5), findViewById(R.id.screenVideoView6), findViewById(R.id.screenVideoView7), findViewById(R.id.screenVideoView8)};

        txt = findViewById(R.id.message);
        countMsg = findViewById(R.id.countMessage);
        uri = Uri.parse("android.resource://" + getPackageName() + "/raw/answer");
        findViewById(R.id.btnReset).setOnClickListener(new ButtonClickReset());
        for (int id : imgButtons) {
            findViewById(id).setOnClickListener(new ButtonClickAction());
        }
        //ゲームの初期化
        initGame();
    }

    class ButtonClickAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            boolean isBackVisible = view.getRotationY() >= 90.0f;
            int i = 0;
            for (i = 0; i < imgButtons.length; i++) {
                if (view.getId() == imgButtons[i]) {
                    break;
                }
            }
            answerSound = plays[i];
            answerSound.start();
            answerSound.setLooping(false);
            answerSound.seekTo(0);

            totalClicks += 1;
            totalClicksStr = String.valueOf(totalClicks);
            if (totalClicks > 0) {
                findViewById(R.id.btnReset).setEnabled(true);
            }

            ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(view, "rotationY", isBackVisible ? 0.0f : 180.0f, isBackVisible ? -180.0f : -360.0f);
            flipAnimator.setDuration(1000); //애니메이션 지속 시간 설정
            flipAnimator.start(); //애니메이션 시작
            mVideoView[i].setVideoURI(uri);

            if (nowState == -1) {
                clickCount += 1;
                txt.setText("2つ目を選択してください");
//                nowState = answer[i];
                nowState = hashMap.get(buttons[i]);

                preSelect = ((ImageButton) view);
                buttons[i].setEnabled(false);
                buttons[i].setImageAlpha(100);
                mVideoView[i].setVisibility(View.VISIBLE);
                mVideoView[i].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 준비 완료되면 비디오 재생
                        mp.start();
                    }
                });

//                //mp4종료시 처리
//                mVideoView[i].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        mp.start();
//                    }
//                });

                aa = i;
            } else {
                for (int j = 0; j < buttons.length; j++) {
                    buttons[j].setEnabled(false);
                }


                if (nowState == hashMap.get(buttons[i])) {
                    answerSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            correctSound.start();
                            clickCount = 0;
                        }
                    });

                    correctSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            for (int j = 0; j < array.length; j++) {
                                if (array[j] == 1) {
                                    buttons[j].setEnabled(false);
                                } else {
                                    buttons[j].setEnabled(true);
                                }
                            }
                        }
                    });

                    mVideoView[aa].setVisibility(View.GONE);
                    array[i] = 1;
                    array[aa] = 1;
                    txt.setText("正解");
                    buttons[i].setEnabled(false);
                    buttons[i].setImageAlpha(100);
                    count += 1;
                    if (count == 4) {
                        if (totalClicks == 8) {
                            totalClicksStr = totalClicksStr + "：き、、、君は神か";
                        } else if (8 < totalClicks && totalClicks < 16) {
                            totalClicksStr = totalClicksStr + "：頑張れ";
                        } else {
                            totalClicksStr = totalClicksStr + "：より一層の精進をすべし";
                        }
                    }
                } else {
                    clickCount += 1;
                    answerSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (clickCount == 2) {
                                        incorrectSound.start();
                                        incorrectSound.setLooping(false);
                                        incorrectSound.seekTo(0);
                                        clickCount = 0;
                                    } else {
                                        return;
                                    }
                                }
                            }, 0); //딜레이
                        }
                    });

                    incorrectSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            for (int j = 0; j < array.length; j++) {
                                if (array[j] == 1) {
                                    buttons[j].setEnabled(false);
                                } else {
                                    buttons[j].setEnabled(true);
                                }
                            }
                        }
                    });

                    mVideoView[aa].setVisibility(View.GONE);

                    txt.setText("不正解！一つ目を選択してください");
//                    buttons[aa].setEnabled(true);
                    buttons[aa].setImageAlpha(250);
                }
                nowState = -1;
                aa = -1;
            }
            countMsg.setText(totalClicksStr);
            answerSound = null;
        }
    }

    class ButtonClickReset implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            initGame();
        }
    }

    private void initGame() {
        correctSound = MediaPlayer.create(MainActivity.this, R.raw.se_ok);
        incorrectSound = MediaPlayer.create(MainActivity.this, R.raw.se_ng);

        aa = -1;
        totalClicks = 0;
        count = 0;
        nowState = -1;
        clickCount = 0;
        totalClicksStr = String.valueOf(totalClicks);
        countMsg.setText(totalClicksStr);
        txt.setText("START");
        findViewById(R.id.btnReset).setEnabled(false);
        int[] setItems = {R.raw.sound1, R.raw.sound2, R.raw.sound3, R.raw.sound4};

        for (int i = 0; i < answer.length; i++) {
            //答えを初期化する
            answer[i] = -1;
            array[i] = 0;
        }

        //2個同じ音を設定
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < setItems.length; j++) {
                int rand = (int) (Math.random() * answer.length);
                if (answer[rand] != -1) {
                    j--;
                    continue;
                }
                answer[rand] = setItems[j];
            }
        }

        for (int i = 0; i < imgButtons.length; i++) {
            buttons[i] = findViewById(imgButtons[i]);
            hashMap.put(buttons[i], answer[i]);

            buttons[i].setEnabled(true);
            buttons[i].setImageAlpha(250);
            buttons[i].setImageResource(R.drawable.piano);
        }

//        for (ImageButton btn :hashMap.keySet()) {
//            btn.setEnabled(true);
//        }

        for (int i = 0; i < plays.length; i++) {
            if (plays[i] != null) {
                plays[i].reset();
                plays[i].release();
                plays[i] = null;
            }
            plays[i] = new MediaPlayer();
            try {
                AssetFileDescriptor afd = getResources().openRawResourceFd(answer[i]);
                if (afd != null) {
                    plays[i].setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    plays[i].prepare();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        for (int i = 0; i < plays.length; i++) {
//            plays[i] = MediaPlayer.create(this, answer[i]);
//            plays[i] = MediaPlayer.create(this, hashMap.get(buttons[i]));
//        }

        for (int i = 0; i < mVideoView.length; i++) {
            mVideoView[i].setVisibility(View.GONE);
        }
    }
}
