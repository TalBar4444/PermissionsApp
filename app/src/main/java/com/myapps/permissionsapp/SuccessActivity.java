package com.myapps.permissionsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.button.MaterialButton;

public class SuccessActivity extends AppCompatActivity {

    private AppCompatTextView success_LBL_title;
    private MaterialButton success_BTN_again;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_success);
        //TODO: ask for location permission
        findViews();

        success_BTN_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainActivity();
            }
        });
    }


    private void findViews() {
        success_LBL_title = findViewById(R.id.success_LBL_title);
        success_BTN_again = findViewById(R.id.success_BTN_again);
    }


    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
