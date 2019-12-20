package com.example.parkinson_dec19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class poemPreview extends AppCompatActivity {

    TextView titleV;
    TextView firstLineV;
    TextView secLineV;

    String title;
    String firstLine;
    String secLine;

    int max;
    int poem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem_preview);

        titleV = findViewById(R.id.title);
        firstLineV = findViewById(R.id.first);
        secLineV = findViewById(R.id.second);

        Bundle receive = getIntent().getExtras();
        if (receive==null){
            try {
                showPoem(titleV);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            title = receive.getString("title");
            firstLine = receive.getString("firstLine");
            secLine = receive.getString("secLine");
            System.out.println(firstLine+secLine);
            setTexts();
        }
    }

    public void showPoem(View v) throws IOException {
//        max = getResources().getAssets().list("").length;
//
//        System.out.println(max);
        // will include 2 additional assets: images and webkit but cant be found anywhere

        max = 4; //static assignment for temp solution
        poem++;
        if (poem ==max-2) poem =0;
        String file = "poem"+ poem +".txt";

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open(file),
                        StandardCharsets.UTF_16));

        //read the contents
        title = reader.readLine();
        firstLine = reader.readLine();
        secLine = reader.readLine();

        setTexts();
    }

    public void toWritePoem(View v){
        Bundle send = new Bundle();
        send.putString("title",title);
        send.putString("firstLine",firstLine);
        send.putString("secLine",secLine);
        send.putInt("poemNumber",poem);
        send.putInt("totalNumber", max);

        Intent intent = new Intent (this, writePoem.class);
        intent.putExtras(send);
        startActivity(intent);
    }

    public void toGreetingPage(View v){
//        Intent intent = new Intent (this, greetingPage.class);
//        startActivity(intent);
    }

    //support functions
    private void setTexts(){
        titleV.setText(title);
        firstLineV.setText(firstLine);
        secLineV.setText(secLine);
    }
}
