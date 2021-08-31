package com.example.traveling;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class CustomDialog extends AppCompatDialogFragment {

    TextView distance;
    TextView altitude;
    TextView comments;

    ImageView imageView;
    ImageButton xButton;

    AlertDialog.Builder builder;
    AlertDialog alert;
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        builder = new AlertDialog.Builder(getActivity());
        alert = builder.create();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.custom_box, null);

        imageView = view.findViewById(R.id.imageView);
        //xButton = view.findViewById(R.id.xBtn);

//        xButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view1) {
//                Log.e("X BUTTON WORKS!!!", "YEEEEE");
//            }
//        });

        String theString = AllMarkersActivity.markerClickedId;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Locations");
        query.whereEqualTo("locationName", theString);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if( e == null){
                    if( objects.size() > 0) {

                        Log.e("Found " + objects.size(), "objects");
                    }
                    else {
                        Log.e("OBJECT SIZE IS", String.valueOf(objects.size()));
                    }
                } else {
                    Log.e("" , e.getMessage());
                }
            }
        });

        builder.setView(view)
                .setPositiveButton("ОДВЕДИ МЕ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e("Possitive button ", "works!!!!!");
                    }
                })
                .setNegativeButtonIcon(Drawable.createFromPath("cancel"))
                .setNegativeButton("ОТКАЖИ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();

            }
        });

        return builder.create();
    }
}
