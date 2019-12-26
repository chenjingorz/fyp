package com.example.parkinson_dec19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class galleryPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_page);
    }

    public void toGreetingPage(View v){
        Intent intent = new Intent (this, greetingPage.class);
        startActivity(intent);
    }
}
