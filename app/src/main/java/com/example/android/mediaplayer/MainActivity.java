package com.example.android.mediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    AudioManager am;
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            switch (i) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaPlayer.start();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mediaPlayer.release();
                    am.abandonAudioFocus(afChangeListener);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mediaPlayer.pause();
                    break;
            }
        }
    };
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer1) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
                am.abandonAudioFocus(afChangeListener);
            }
        }
    };
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button play = (Button) findViewById(R.id.play);
        Button pause = (Button) findViewById(R.id.pause);
        Button forward = (Button) findViewById(R.id.forward);
        am = (AudioManager) getBaseContext().getSystemService(MainActivity.this.AUDIO_SERVICE);
        result = am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (mediaPlayer != null) {
            getPermission();
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.rasaali);
                }
                getPermission();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPos = mediaPlayer.getCurrentPosition();
                int maxPos = mediaPlayer.getDuration();
                currentPos += 10000;
                if (currentPos < maxPos) {
                    mediaPlayer.seekTo(currentPos);
                } else {
                    mediaPlayer.seekTo(maxPos);
                }
            }
        });
    }

    private void getPermission() {
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            getPermission();
        }
    }
}
