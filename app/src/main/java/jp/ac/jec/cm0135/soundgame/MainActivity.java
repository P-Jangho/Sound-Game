package jp.ac.jec.cm0135.soundgame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private int[] imgButtons = {
            R.id.ibtn01, R.id.ibtn02, R.id.ibtn03,
            R.id.ibtn04, R.id.ibtn05, R.id.ibtn06,
            R.id.ibtn07, R.id.ibtn08
    };

    private MediaPlayer[] plays = new MediaPlayer[imgButtons.length];
    private int[] answer = new int[imgButtons.length];
    private int nowState = -1;
    private TextView txt;
    private TextView countMsg;
    private ImageButton preSelect = null;
    ImageButton[] buttons = new ImageButton[imgButtons.length];
    private int aa;
    private int bb;
    private String bbStr;
    private int count = 0;
    private VideoView[] mVideoView;

    MediaPlayer p;
    MediaPlayer p1;
    MediaPlayer p2;
    private int cc = 0;

    // sample.mp4 설정
    Uri uri;

    HashMap<ImageButton, Integer> hashMap = new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = findViewById(R.id.message);
        countMsg = findViewById(R.id.countMessage);

        uri = Uri.parse("android.resource://" + getPackageName() + "/raw/answer");

        mVideoView = new VideoView[] {
                findViewById(R.id.screenVideoView1), findViewById(R.id.screenVideoView2), findViewById(R.id.screenVideoView3),
                findViewById(R.id.screenVideoView4), findViewById(R.id.screenVideoView5), findViewById(R.id.screenVideoView6),
                findViewById(R.id.screenVideoView7), findViewById(R.id.screenVideoView8)
        };





//        buttons = new ImageButton[]{
//                findViewById(R.id.ibtn01), findViewById(R.id.ibtn02), findViewById(R.id.ibtn03),
//                findViewById(R.id.ibtn04), findViewById(R.id.ibtn05), findViewById(R.id.ibtn06),
//                findViewById(R.id.ibtn07), findViewById(R.id.ibtn08)
//        };

        findViewById(R.id.btnReset).setOnClickListener(new ButtonClickReset());

        for(int id: imgButtons) {
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
                if(view.getId() == imgButtons[i]) {
                    break;
                }
            }
            p = plays[i];

            bb += 1;
            bbStr = String.valueOf(bb);

            if(bb > 0) {
                findViewById(R.id.btnReset).setEnabled(true);
            }

            ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(view, "rotationY", isBackVisible ? 0.0f : 180.0f, isBackVisible ? -180.0f : -360.0f);
            flipAnimator.setDuration(1000); // 애니메이션 지속 시간 설정
            flipAnimator.start(); // 애니메이션 시작


            mVideoView[i].setVideoURI(uri);

//
            Log.i("aaa", "nowState " + nowState);
            if(nowState == -1) {
                cc += 1;
                Log.i("aaa", "lllll" + 1);
                p.start();
                p.setLooping(false);
                p.seekTo(0);
                txt.setText("2つ目を選択してください");
                nowState = answer[i];
//                nowState = hashMap.get(buttons[i]);

                preSelect = ((ImageButton) view);
                buttons[i].setEnabled(false);
                buttons[i].setImageAlpha(100);
//                buttons[i].setImageResource(R.drawable.natsu_folderopen);

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
                Log.i("aaa", "aaa " + aa);
//                return;
                Log.i("aaa", "lllll" + 2);
            }else {
                cc += 1;
                Log.i("aaa", "lllll" + 3);
                p.start();
                p.setLooping(false);
                p.seekTo(0);
                if(nowState == answer[i]) {
                    Log.i("aaa", "lllll" + 4);

                    p.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            p1.start(); // 다음 소리 재생
                            Log.i("aaa", "lllll" + 12);
                            cc = 0;
                        }
                    });
//                    p.start(); // 첫 번째 소리 재생

                    Log.i("aaa", "lllll" + 5);
                    mVideoView[aa].setVisibility(View.GONE);

                    txt.setText("正解");
                    buttons[i].setEnabled(false);
                    buttons[i].setImageAlpha(100);
//                    ((ImageButton) view).setImageResource(R.drawable.natsu_folderopen);
//                    preSelect.setImageResource(R.drawable.natsu_folderopen);
                    count += 1;
                    nowState = -1;
                    Log.i("aaa", "lllll" + 6);
                    if(count == 4) {
                        Log.i("aaa", "lllll" + 7);
                        if(bb == 8) {
                            bbStr = bbStr + " き、、、君は神か";
                        }else if(8 < bb && bb < 16) {
                            bbStr = bbStr + " 頑張れ";
                        }else {
                            bbStr = bbStr + " より一層の精進をすべし";
                        }
                    }
                }else {

                    Log.i("aaa", "lllll" + 8);
                    p.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(cc == 2) {
                                        p2.start();
                                        p2.setLooping(false);
                                        p2.seekTo(0);
                                        cc = 0;
                                    }else {
                                        return;
                                    }


                                    Log.i("aaa", "lllll" + 11);
                                }
                            }, 200); // 1초 딜레이
                        }
                    });
//                    p.start(); // 첫 번째 소리 재생


                    Log.i("aaa", "lllll" + 9);
                    mVideoView[aa].setVisibility(View.GONE);

                    txt.setText("不正解！一つ目を選択してください");
                    buttons[aa].setEnabled(true);
                    buttons[aa].setImageAlpha(250);
//                    buttons[aa].setImageResource(R.drawable.natsu_folder);


                }
                Log.i("aaa", "lllll" + 10);
                nowState = -1;
                aa = -1;
            }
            countMsg.setText(bbStr);
            p = null;
        }
    }

    class ButtonClickReset implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            initGame();
        }
    }
    private void initGame() {

//        p1.release();
//        p2.release();

        p1 = MediaPlayer.create(MainActivity.this, R.raw.se_ok);
        p2 = MediaPlayer.create(MainActivity.this, R.raw.se_ng);

        aa = -1; bb = 0; count = 0; nowState = -1;
        bbStr = String.valueOf(bb);
        countMsg.setText(bbStr);
        txt.setText("START");
        int[] setItems = {R.raw.se_drink02, R.raw.se_whistle01, R.raw.se_phone02, R.raw.se_door01};

        for (int i = 0; i < answer.length; i++) {
            //答えを初期化する
            answer[i] = -1;
        }

        //2個同じ音を設定
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < setItems.length; j++) {
                int rand = (int) (Math.random() * answer.length);
                if (answer[rand] != -1) {
                    j--;
                    continue;
                }
                Log.i("aaa", "rand " + rand);
                answer[rand] = setItems[j];
            }
        }

        for (int i = 0; i < imgButtons.length; i++) {
            buttons[i] = findViewById(imgButtons[i]);
//            hashMap.put(buttons[i], answer[i]);

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
//            plays[i].release();
//            plays[i] = MediaPlayer.create(this, answer[i]);
////            plays[i] = MediaPlayer.create(this, hashMap.get(buttons[i]));
//        }

        for (int i = 0; i < mVideoView.length; i++) {
            mVideoView[i].setVisibility(View.GONE);
        }

        findViewById(R.id.btnReset).setEnabled(false);

//        p.release();


//        for (int i = 0; i < plays.length; i++) {
//            if (i % 2 == 0) {
//                plays[i] = MediaPlayer.create(this, R.raw.se_whistle01);
//            }else {
//                plays[i] = MediaPlayer.create(this, R.raw.se_phone02);
//            }
//        }
    }
}
