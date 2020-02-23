package com.example.parkinson_dec19;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class GalleryPage extends AppCompatActivity {

    HashMap<String, Integer> flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_page);

        PoemList poemList = new PoemList();
        flags = poemList.getPoemFlag();
        markFlagged();
    }

    private void markFlagged(){
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

    public void toGreetingPage(View v){
        Intent intent = new Intent (this, GreetingPage.class);
        startActivity(intent);
    }

    public void toHandwritingPage(View v){
        String id = getResources().getResourceName(v.getId());
        String name = id.substring(id.length()-5);
        System.out.println("name of poem: "+name);

        if (flags.get(name)==0){
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
