package com.example.parkinson_dec19;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WritingHistoryPage extends AppCompatActivity {

    TextView titleV;
    GridView gridV;

    PoemList poemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_history_page);

        titleV = findViewById(R.id.title);
        poemName = new PoemList();

        Bundle receive = getIntent().getExtras();
        String poemNumber = receive.getString("title");
        String displayTitle = poemName.getPoemName(poemNumber);
        titleV.setText(displayTitle);

        gridV = findViewById(R.id.grid);
        gridV.setAdapter(new GridAdaptor(this, poemNumber));
    }

    public void toGalleryPage(View v){
        Intent intent = new Intent (this, GalleryPage.class);
        startActivity(intent);
    }
}
