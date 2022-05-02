package com.example.civiladvocacyapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {
    TextView tvlocation;
    TextView tvname;
    TextView tvoff;
    ImageView img;

    String location;
    String name;
    String off;
    String party;
    String image = "";
    String partywebsite;

    Picasso picasso;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail_activity);

        tvlocation = findViewById(R.id.photo_location);
        tvoff = findViewById(R.id.photo_office);
        tvname = findViewById(R.id.photo_name);
        img = findViewById(R.id.photo_image);

        picasso = Picasso.get();
        Intent intent = getIntent();

        location = String.valueOf(intent.getSerializableExtra("LOCATION"));
        name = String.valueOf(intent.getSerializableExtra("NAME"));
        off = String.valueOf(intent.getSerializableExtra("OFFICE"));
        party = String.valueOf(intent.getSerializableExtra("PARTY"));
        image = String.valueOf(intent.getSerializableExtra("IMAGE"));

        setTitle("Know Your Government");

        tvlocation.setText(location);          //set value of location
        tvoff.setText(off);         //set value of office name
        tvname.setText(name);           //set value of name

        if (party.contains("Democratic")) {        //BG and logo according to party name
            ConstraintLayout cl = findViewById(R.id.photo_constraint);
            cl.setBackgroundColor(Color.BLUE);
            ImageView partyimg = findViewById(R.id.photo_partyimg);
            partyimg.setImageResource(R.drawable.dem_logo);
            partywebsite = "https://democrats.org";
        } else if (party.contains("Republic")) {
            ConstraintLayout cl = findViewById(R.id.photo_constraint);
            cl.setBackgroundColor(Color.RED);
            ImageView partyimg = findViewById(R.id.photo_partyimg);
            partyimg.setImageResource(R.drawable.rep_logo);
            partywebsite = "https://www.gop.com";
        } else {
            ConstraintLayout cl = findViewById(R.id.photo_constraint);
            cl.setBackgroundColor(Color.BLACK);
            ImageView partyimg = findViewById(R.id.photo_partyimg);
            partyimg.setVisibility(ImageView.INVISIBLE);
        }

        picasso.load(image)         //set image
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(img);
    }

    public void partyImgClick(View view) {           //open party website
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(partywebsite));
        startActivity(intent);
    }
}
