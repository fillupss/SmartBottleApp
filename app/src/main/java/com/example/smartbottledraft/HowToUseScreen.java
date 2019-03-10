package com.example.smartbottledraft;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HowToUseScreen extends AppCompatActivity {

    private TextView helpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use_screen);
        helpText = (TextView)findViewById(R.id.helpText);
        displayHelpText();
    }

    private void displayHelpText(){
        String helpMessage1 = "1. To use the app, from the main menu click on the button that says Freshen Up!\n";
        String helpMessage2 = "2. After clicking the button, choose one of the three modes: Sicko, Work, or Normal.\n";
        String helpMessage3 = "3. You can place your water bottle down into the sensor to measure the weight and the timer will start.\n";
        String helpMessage4 = "4. Make sure to Freshen Up or else the the evil alarm will go off :P\n";
        helpText.setText(helpMessage1 + helpMessage2 + helpMessage3 + helpMessage4);
    }

    public void HelpToMenu(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
