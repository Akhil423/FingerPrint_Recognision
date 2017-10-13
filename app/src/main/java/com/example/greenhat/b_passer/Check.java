package com.example.greenhat.b_passer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Check extends AppCompatActivity {

    String text;
    TextView edi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

         edi=(TextView) findViewById(R.id.res1);
         text= getIntent().getStringExtra("result");
        edi.setText(text);



        Button b=(Button)findViewById(R.id.gbck);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent revoke=new Intent(Check.this,Home.class);
                startActivity(revoke);
            }
        });


    }
}
