package com.example.parkinson_dec19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * add the buttons in the xml page for new assets added
 */

public class GalleryPage extends AppCompatActivity {

    HashMap<String, Integer> poemStatus = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_page);

        getPoemStatus();
        markFlagged();
    }

    private void getPoemStatus(){
        PoemList poemList = new PoemList();
        HashMap<String, Integer> flags = poemList.getPoemList();

        for (Map.Entry<String, Integer> entry : flags.entrySet()) {
            String key = entry.getKey();

            SharedPreferences sharedPref = getSharedPreferences(key, Context.MODE_PRIVATE);
            int value = sharedPref.getInt(key, 0);
            poemStatus.put(key,value);
        }
    }

    private void markFlagged(){
        //display all poems, make buttons green if flagged 1 (ie attempted)
        for (Map.Entry<String, Integer> entry : poemStatus.entrySet()) {

            if (entry.getValue()==1){
                int id = getResources().getIdentifier(entry.getKey(),
                        "id", getPackageName());
                Button button = findViewById(id);
                button.setBackgroundColor(getResources().getColor(R.color.colorFlag));
            }
        }
    }

    public void toGreetingPage(View v){
        Intent intent = new Intent (this, GreetingPage.class);
        startActivity(intent);
    }

    public void toHandwritingPage(View v){
        String id = getResources().getResourceName(v.getId());
        String name = id.substring(id.length()-5);

        System.out.println("name of poem: "+name);

        if (poemStatus.get(name)==0){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Not completed yet!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Bundle send = new Bundle();
            send.putString("title",name);

            Intent intent = new Intent (this, WritingHistoryPage.class);
            intent.putExtras(send);
            startActivity(intent);
        }
    }
}
