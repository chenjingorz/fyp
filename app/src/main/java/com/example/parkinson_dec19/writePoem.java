package com.example.parkinson_dec19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suyati.telvin.drawingboard.DrawingBoard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class writePoem extends AppCompatActivity {

    DrawingBoard drawingBoard;
    TextView titleV;
    TextView wordV;
    TextView bannerV;

    int poem;
    int max;
    int startWord = 0; //start at index 0
    int poemLength;

    String title;
    String firstLine;
    String secLine;
    String poemText;

    Timer timer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_poem);

        permission();

        drawingBoard = findViewById(R.id.drawinboard);
        titleV = findViewById(R.id.title);
        wordV = findViewById(R.id.word);
        bannerV = findViewById(R.id.banner);

        drawingBoard.setPenColor(R.color.colorBlack);
        drawingBoard.setPenWidth(20f);

        Bundle receive = getIntent().getExtras();
        title = receive.getString("title");
        firstLine = receive.getString("firstLine");
        secLine = receive.getString("secLine");
        poemText = firstLine + secLine;
        poemLength = poemText.length();
        setTexts();

        poem = receive.getInt("poemNumber");
        max = receive.getInt("totalNumber");

        //saveCanvasTimer();

    }

    public void changePoem(View v) throws IOException {
        //timer.cancel();
        clearCanvas(v);

        startWord = 0;

        poem++;
        if (poem ==max-2) poem =0;
        String file = "poem"+ poem +".txt";

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open(file),
                        StandardCharsets.UTF_16));

        //read the title
        title = reader.readLine();
        System.out.println(title);

        //keep poems to 2 lines
        firstLine = reader.readLine();
        secLine = reader.readLine();
        poemText = firstLine+secLine;
        System.out.println(poemText);

        setTexts();
        //saveCanvasTimer();
    }

    public void nextWord(View view){
        //edge case: when it is at the last word and click next word
        if (startWord==poemLength){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "写完了哦，换一首吧！",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            wordV.setText(String.valueOf(poemText.charAt(startWord)));
            bannerV.setText(spannable(poemText), TextView.BufferType.SPANNABLE);

            //save the image
            saveCanvas(true);

            updateWordCount();
            clearCanvas(view);
        }
    }

    public void clearCanvas(View v){
        drawingBoard.clearBoard();
    }

    public void toPoemDisplay(View v){
        timer.cancel();

        Bundle send = new Bundle();
        send.putString("title",title);
        send.putString("firstLine",firstLine);
        send.putString("secLine",secLine);

        Intent intent = new Intent (this, poemPreview.class);
        intent.putExtras(send);
        startActivity(intent);
    }

    private void saveCanvasTimer(){
        // to save drawings at 1sec interval
        // not implemented: check if bitmap is empty, if yes, don't save to reduce memory
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //stop the task when at the last word
                if (startWord==poemLength) cancel();
                saveCanvas(false);
            }
        };

        timer = new Timer();

        long delay = 1000L;
        long period = 1000;
        timer.scheduleAtFixedRate(task, delay, period);
    }

    public boolean onTouchEvent(MotionEvent event){
        float corX = event.getX();
        float corY = event.getY();
        System.out.println(corX +", "+ corY);
        return true;
    }

    //support functions
    private void setTexts(){
        titleV.setText(title);
        wordV.setText(String.valueOf(poemText.charAt(startWord)));
        bannerV.setText(spannable(poemText),TextView.BufferType.SPANNABLE);
    }

    private SpannableString spannable(String text){
        //sets the wordV to be written in bold, colored and bigger size
        SpannableString str = new SpannableString(text);
        str.setSpan(new StyleSpan(Typeface.BOLD), startWord, startWord +1,0);
        str.setSpan(new RelativeSizeSpan(1.2f), startWord, startWord +1, 0);
        str.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),
                startWord, startWord +1,0);

        startWord++;
        return str;
    }

    private void updateWordCount(){
        SharedPreferences sharedPref = getSharedPreferences("WordCount", Context.MODE_PRIVATE);
        int count = sharedPref.getInt(getString(R.string.wordCount), 0);

        System.out.println(count);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.wordCount), count+1);
        //editor.remove(getString(R.string.wordCount)
        editor.apply();
    }

    private void saveCanvas(boolean next){
        // to indicate if the method is initiated by the "next" button
        String affix = "";
        if (next) affix = "next";

        String baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fyp/poem"+poem;
        String fileName = "/poem"+poem+"_"+Calendar.getInstance().getTimeInMillis()+affix+".png";

        File f = new File(baseFilePath);
        if (!f.exists()){
            f.mkdir();
        }
        File file = new File(f+fileName);

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            Bitmap bitmap = drawingBoard.getBitMapSignature(); //check if bitmap is blank before saving?
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            System.out.println(baseFilePath+fileName);
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void permission(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        400);
            }
        }
    }
}
