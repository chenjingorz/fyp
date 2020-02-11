package com.example.parkinson_dec19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class galleryPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_page);

        markFlagged();
    }

    public void toGreetingPage(View v){
        Intent intent = new Intent (this, greetingPage.class);
        startActivity(intent);
    }

    private void markFlagged(){
        PoemList poemList = new PoemList();
        HashMap<String, Integer> flags = poemList.getPoemFlag();

        //display all poems, make buttons green if flagged 1 (ie attempted)
        for (Map.Entry<String, Integer> entry : flags.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if (value==1){
                int id = getResources().getIdentifier(key,
                        "id", getPackageName());
                Button button = findViewById(id);
                button.setBackgroundColor(getResources().getColor(R.color.colorFlag));

            }
        }

    }
}
