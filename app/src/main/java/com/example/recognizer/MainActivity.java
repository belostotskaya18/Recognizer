package com.example.recognizer;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.UUID;

import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Language;
import ru.yandex.speechkit.OnlineModel;
import ru.yandex.speechkit.OnlineRecognizer;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.Track;


import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity implements RecognizerListener {

    private ProgressBar progressBar;
    private TextView currentStatus;
    private TextView recognitionResult;
    private Recognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SpeechKit.getInstance().init(this, "069b6659-984b-4c5f-880e-aaedcfd84102");
            SpeechKit.getInstance().setUuid(UUID.randomUUID().toString());
        } catch (SpeechKit.LibraryInitializationException ignored) {
            finish();
        }
        recognizer = new OnlineRecognizer.Builder(Language.RUSSIAN, OnlineModel.QUERIES,
                MainActivity.this).build();

        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecognizer();
            }
        });

        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.cancel();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        currentStatus = findViewById(R.id.status);
        recognitionResult = findViewById(R.id.result);
    }

    private void startRecognizer() {

    }

    private void updateResult(String text) {
        recognitionResult.setText(text);
    }

    private void updateStatus(final String text) {
        currentStatus.setText(text);
    }

    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != 31) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
            startRecognizer();
        } else {
            updateStatus("Record audio permission was not granted");
        }

    }

    @Override
    public void onRecordingBegin(@NonNull Recognizer recognizer) {
        updateStatus("Recording begin");
        updateProgress(0);
        updateResult("");
    }

    @Override
    public void onSpeechDetected(@NonNull Recognizer recognizer) {
        updateStatus("Speech detected");
    }

    @Override
    public void onSpeechEnds(@NonNull Recognizer recognizer) {
        updateStatus("Speech ends");
    }

    @Override
    public void onRecordingDone(@NonNull Recognizer recognizer) {
        updateStatus("Recording done");
    }

    @Override
    public void onPowerUpdated(@NonNull Recognizer recognizer, float v) {
        updateProgress((int) (v * progressBar.getMax()));
    }

    @Override
    public void onPartialResults(@NonNull Recognizer recognizer, @NonNull Recognition recognition, boolean b) {
        updateStatus("Partial results " + recognition.getBestResultText() + " endOfUtterrance = " + b);
        if (b) {
            updateResult(recognition.getBestResultText());
        }
    }

    @Override
    public void onRecognitionDone(@NonNull Recognizer recognizer) {
        updateStatus("Recognition done");
        updateProgress(0);
    }

    @Override
    public void onRecognizerError(@NonNull Recognizer recognizer, @NonNull Error error) {
        updateStatus("Error occurred " + error);
        updateProgress(0);
        updateResult("");
    }

    @Override
    public void onMusicResults(@NonNull Recognizer recognizer, @NonNull Track track) {
    }
}
