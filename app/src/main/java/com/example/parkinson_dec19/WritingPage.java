package com.example.parkinson_dec19;

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
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.myscript.iink.Configuration;
import com.myscript.iink.ContentPackage;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.Engine;
import com.myscript.iink.IEditorListener;
import com.myscript.iink.MimeType;
import com.myscript.iink.PointerEvent;
import com.myscript.iink.Renderer;
import com.myscript.iink.uireferenceimplementation.FontMetricsProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WritingPage extends AppCompatActivity {

    drawBoard drawingBoard;
    TextView titleV;
    TextView wordV;
    TextView bannerV;

    int poem;
    int max;
    int startWord = 0; //start at index 0
    int poemLength;
    int wrongWordTries = 0;

    String title;
    String firstLine;
    String secLine;
    String poemText;
    String baseFilePath;

    Boolean configured = false;
    Boolean lastWordSaved = false;
    Boolean correctWordWritten;

    Timer timer;
    PoemList update;

    Engine engine;
    Editor editor;
    ContentPackage contentPackage;
    ContentPart contentPart;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_poem);

        permission();

        update = new PoemList();

        drawingBoard = findViewById(R.id.drawinboard);
        titleV = findViewById(R.id.title);
        wordV = findViewById(R.id.word);
        bannerV = findViewById(R.id.banner);

        Bundle receive = getIntent().getExtras();
        title = receive.getString("title");
        firstLine = receive.getString("firstLine");
        secLine = receive.getString("secLine");
        poemText = firstLine + secLine;
        poemLength = poemText.length();

        poem = receive.getInt("poemNumber");
        max = receive.getInt("totalNumber");

        setDisplayedTexts();
        setBaseFilePath();

        //todo: clears word progress and poem flags
//        clearMemory("poem"+poem);
    }

    public void changePoem(View v) throws IOException {
        //timer.cancel();
        clearCanvas(v);
        lastWordSaved = false;
        startWord = 0;

        poem++;
        if (poem == max) poem =0;
        setBaseFilePath();

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

        setDisplayedTexts();
        //saveCanvasTimer();
    }

    public void nextWord(View view){
        if (startWord==poemLength){
            //save last word once
            if (!lastWordSaved){
                if (saveCanvas(true)) {
                    lastWordSaved = true;}
            }
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Try a new poem!",
                    Toast.LENGTH_SHORT);
            toast.show();
            updatePoemFlag("poem"+poem);
        }
        else {
            //save the image
            if (saveCanvas(true)){
                wordV.setText(String.valueOf(poemText.charAt(startWord)));
                bannerV.setText(spannable(poemText), TextView.BufferType.SPANNABLE);
                updateWordCount();
            }
        }
        clearCanvas(view);
    }

    public void clearCanvas(View v){
        drawingBoard.clear();

        //erase all strokes recorded for text recognition
        drawingBoard.eraseEvents();
        drawingBoard.clearWritingMatrix();
        drawingBoard.clearTime();
    }

    public void toPoemDisplay(View v){
        //timer.cancel();
        if (contentPackage!=null){
            editor.waitForIdle();
            contentPackage.close();
            contentPackage = null;
            contentPart.close();
            contentPart = null;
        }

        Bundle send = new Bundle();
        send.putString("title",title);
        send.putString("firstLine",firstLine);
        send.putString("secLine",secLine);
        send.putInt("poem",poem);

        Intent intent = new Intent (this, PoemPreviewPage.class);
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

    //support functions
    private void setDisplayedTexts(){
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

        editor.apply();
    }

    private Boolean saveCanvas(boolean next){
        try
        {
            Bitmap bitmap = drawingBoard.getBitmap(false);

            //if the written word is correctWordWritten, save the image
            if (recognise().equals(wordV.getText())){
                // to indicate if the method is initiated by the "next" button
                String affix = "";
                if (next) affix = "next";

                File f = new File(baseFilePath+"/images");
                if (!f.exists()){
                    f.mkdir();
                    System.out.println(f);
                }
                String fileName = "/poem"+poem+"_"+Calendar.getInstance().getTimeInMillis()+affix+".png";
                File file = new File(f+fileName);
                FileOutputStream fos = new FileOutputStream(file);

                correctWordWritten = true;
                writeToFile();
                wrongWordTries = 0;
                System.out.println("correctWordWritten!");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
            else if (wrongWordTries==2){ //if third try is still wrong word
                correctWordWritten = false;
                writeToFile();
                wrongWordTries = 0;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Word skipped!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                wrongWordTries++;
                correctWordWritten = false;
                writeToFile();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Write the correct word!",
                        Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    private void writeToFile(){ //todo: use pipe as separator!
        //save the matrix and timestamp info with the word, attempt#, flag
        ArrayList<String> matrix = drawingBoard.getWritingMatrix();
        ArrayList<Long> time = drawingBoard.getTime();


        File txtPath = new File(baseFilePath+"/txt");
        if (!txtPath.exists()){
            txtPath.mkdir();
            System.out.println("created text path: "+txtPath);
        }

        File txt = new File(txtPath, "info.txt");
        try {
            FileOutputStream fOut = new FileOutputStream(txt,true);
            OutputStreamWriter writer = new OutputStreamWriter(fOut);
            writer.append(wordV.getText()+",");
            writer.append(wrongWordTries+",");
            writer.append(correctWordWritten+",");
            writer.append(matrix+",");
            writer.append(time+"\n");
            writer.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBaseFilePath(){
        baseFilePath = Environment.getExternalStorageDirectory().getPath()+"/fyp/poem"+poem;
        File basePath = new File(baseFilePath);
        if (!basePath.exists()){
            basePath.mkdir();
            System.out.println("created base path: "+basePath);
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

    //fixme: engine cannot be opened again when this activity is closed
    private void engineConf(){
        if (!configured){
            engine = InkEngine.getEngine();
            //configure
            Configuration conf = engine.getConfiguration();
            conf.setBoolean("text.guides.enable", false);
            String confDir = "zip://" + getPackageCodePath() + "!/assets/conf";
            conf.setStringArray("configuration-manager.search-path", new String[]{confDir});
            String tempDir = getFilesDir().getPath() + File.separator + "tmp";
            conf.setString("content-package.temp-folder", tempDir);
            conf.setString("lang", "zh_CN");

            // Create a renderer with a null render target
            float dpiX = drawingBoard.getHeight();
            float dpiY = drawingBoard.getWidth();

            System.out.println(dpiX+" "+dpiY);
            Renderer renderer = engine.createRenderer(dpiX, dpiY, null);

            // Create the editor
            editor = engine.createEditor(renderer);
            editor.addListener(new IEditorListener() {
                @Override
                public void partChanging(Editor editor, ContentPart contentPart, ContentPart contentPart1) {

                }

                @Override
                public void partChanged(Editor editor) {

                }

                @Override
                public void contentChanged(Editor editor, String[] strings) {

                }

                @Override
                public void onError(Editor editor, String s, String s1) {
                    System.out.println(s1);
                }
            });

            // The editor requires a font metrics provider and a view size *before* calling setPart()
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            Map<String, Typeface> typefaceMap = new HashMap<>();
            editor.setFontMetricsProvider(new FontMetricsProvider(displayMetrics, typefaceMap));
            editor.setViewSize((int)dpiX, (int)dpiY);

            // Create a temporary package and contentPart for the editor to work with
            contentPackage = null;
            try {
                contentPackage = engine.createPackage("text.iink");
            } catch (IOException e) {
                e.printStackTrace();
            }

            contentPart = contentPackage.createPart("Text");
            editor.setPart(contentPart);

            configured = true;
        }
    }

    private String recognise(){
        engineConf();

        ArrayList<PointerEvent> events = drawingBoard.getPointerEvents();
        editor.pointerEvents(events.toArray(new PointerEvent[0]), false);
        editor.waitForIdle();

        //export
        String result = null;
        try {
            result = editor.export_(editor.getRootBlock(), MimeType.TEXT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        editor.clear();

        return result;
    }

    private void updatePoemFlag(String poem){
        SharedPreferences sharedPref = getSharedPreferences(poem, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(poem, 1);
        editor.apply();
    }

    private void clearMemory(String poem){
        SharedPreferences flag = getSharedPreferences(poem, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = flag.edit();
        editor.remove(poem).commit();

        SharedPreferences wordCount = getSharedPreferences("WordCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = wordCount.edit();
        editor1.remove(getString(R.string.wordCount)).commit();
    }
}
