package com.example.smartbottledraft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, String[] modes) {
        // string, layout, adapter type
        super(context, R.layout.custom_row, modes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // prepares to render the image/text
        LayoutInflater inflater = LayoutInflater.from(getContext());
        // equal to one custom row (image and text)
        View customView = inflater.inflate(R.layout.custom_row, parent, false);

        String singleModeItem = getItem(position);
        TextView modeText = (TextView)customView.findViewById(R.id.modeText);
        ImageView modeImage = (ImageView)customView.findViewById(R.id.modeImage);

        modeText.setText(singleModeItem);

        if(position == 0){
            modeImage.setImageResource(R.mipmap.sick);
        }
        else if(position == 1){
            modeImage.setImageResource(R.mipmap.workout);
        }
        else{
            modeImage.setImageResource(R.mipmap.happy);
        }
        return customView;
    }
}
