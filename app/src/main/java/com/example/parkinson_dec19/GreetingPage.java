package com.example.parkinson_dec19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GreetingPage extends AppCompatActivity {

    ProgressBar barV;
    TextView countV;

    ImageView seedV;
    ImageView seedlingV;
    ImageView treeV;

    int stage1 = 20;
    int stage2 = 50;
    int stage3 = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting_page);

        seedV = findViewById(R.id.seed);
        seedlingV = findViewById(R.id.seedling);
        treeV = findViewById(R.id.tree);
        seedlingV.setVisibility(View.INVISIBLE);
        treeV.setVisibility(View.INVISIBLE);


        countV = findViewById(R.id.count);
        barV = findViewById(R.id.progressBar);
        updateProgress();
    }

    public void showMenu(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.writePoem:
                        toPoemPreview();
                        return true;
                    case R.id.gallery:
                        toGalleryPage();
                        return true;
                }
                return false;
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options, popup.getMenu());
        popup.show();
    }

    //support functions
    private void toPoemPreview(){
        Intent intent = new Intent (this, PoemPreviewPage.class);
        startActivity(intent);
    }

    private void toGalleryPage(){
        Intent intent = new Intent (this, GalleryPage.class);
        startActivity(intent);
    }

    private void updateProgress(){
        // set 3 stages atm. (20,50,100 words)
        SharedPreferences sharedPref = getSharedPreferences("WordCount", Context.MODE_PRIVATE);
        int count = sharedPref.getInt(getString(R.string.wordCount), 0);

        System.out.println(count);

        int stage;
        if (count<stage1) stage = stage1;
        else if (count<stage2) {
            stage = stage2;
            seedV.setVisibility(View.INVISIBLE);
            seedlingV.setVisibility(View.VISIBLE);
        }
        else {
            stage = stage3;
            seedV.setVisibility(View.INVISIBLE);
            seedlingV.setVisibility(View.INVISIBLE);
            treeV.setVisibility(View.VISIBLE);
        }
        barV.setMax(stage);

        countV.setText(count+"/"+stage);
        barV.setProgress(count);
    }
}
