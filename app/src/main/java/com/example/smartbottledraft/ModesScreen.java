package com.example.smartbottledraft;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ModesScreen extends Activity {

    private String[] modes = {"Sicko", "Workout", "Normal"};;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes_screen);

        // 2nd parameter is how you want the list to be formatted, 3rd parameter is the objects to be used
        //ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, modes);

        // the custom adapter
        ListAdapter adapter = new CustomAdapter(this, modes);

        // convert the strings to a list from an adapter
        ListView modesListView = findViewById(R.id.modesListView);
        modesListView.setAdapter(adapter);

        // an item click listener for each item in the list defined by position or id
        modesListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String mode = String.valueOf(parent.getItemAtPosition(position));
                        if(mode.equals(modes[0])){
                            // go to the sicko mode screen
                            Intent i = new Intent(ModesScreen.this, SickoScreen.class);
                            startActivity(i);
                        }
                        else if(mode.equals(modes[1])){
                            // go to the sicko mode screen
                            Intent i = new Intent(ModesScreen.this, WorkoutScreen.class);
                            startActivity(i);
                        }
                        else{
                            // go to the sicko mode screen
                            Intent i = new Intent(ModesScreen.this, NormalScreen.class);
                            startActivity(i);
                        }
                    }
                }
        );
    }

    public void ModestoMenu(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
