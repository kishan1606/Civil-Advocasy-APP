package com.example.civiladvocacyapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.civiladvocacyapp.Offices.Offices;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    Offices c;
    TextView location;
    TextView off;
    TextView name;
    TextView party;
    TextView address;
    TextView phone;
    TextView email;
    TextView website;
    ImageView img;

    String fb;
    String tw;
    String yt;
    String l;
    String n;
    String o;
    String p;
    String partywebsite = "";
    String image = "";

    private Offices offices;
    private Picasso picasso;
    MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.official_activity);

        location = findViewById(R.id.off_location);
        off = findViewById(R.id.off_office);
        name = findViewById(R.id.off_name);
        party = findViewById(R.id.off_party);
        address = findViewById(R.id.off_address);
        phone = findViewById(R.id.off_phone);
        email = findViewById(R.id.off_email);
        website = findViewById(R.id.off_website);
        img = findViewById(R.id.off_image);

        picasso = Picasso.get();
        setTitle("Know Your Government");

        Intent intent = getIntent();
        c = (Offices) intent.getSerializableExtra("LIST");
        l = String.valueOf(intent.getSerializableExtra("LOCATION"));

        location.setText(l);
        if (c == null)
            return;

        o = c.getOff();         //office name
        off.setText(o);

        n = c.getName();        //official name
        name.setText(n);

        p = c.getParty();               //party name
        party.setText("(" + p + ")");
        if (p.contains("Democratic")) {        //BG according to party name
            ScrollView sc = findViewById(R.id.off_sv);
            sc.setBackgroundColor(Color.BLUE);
            ImageView partyimg = findViewById(R.id.off_partyimg);
            partyimg.setImageResource(R.drawable.dem_logo);
            partywebsite = "https://democrats.org";
        } else if (p.contains("Republic")) {
            ScrollView sc = findViewById(R.id.off_sv);
            sc.setBackgroundColor(Color.RED);
            ImageView partyimg = findViewById(R.id.off_partyimg);
            partyimg.setImageResource(R.drawable.rep_logo);
            partywebsite = "https://www.gop.com";
        } else {
            ScrollView sc = findViewById(R.id.off_sv);
            sc.setBackgroundColor(Color.BLACK);
            ImageView partyimg = findViewById(R.id.off_partyimg);
            partyimg.setVisibility(ImageView.INVISIBLE);
        }

        String add = c.getAddress();            //address
        if (add == null || add.equals("")) {
            TextView tv = findViewById(R.id.address);
            tv.setVisibility(TextView.GONE);
            address.setVisibility(TextView.GONE);

        } else {
            address.setPaintFlags(address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            address.setText(add);
            address.setOnClickListener(view -> {
                Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(add));
                Intent intent1 = new Intent(Intent.ACTION_VIEW, mapUri);
                startActivity(intent1);
            });
        }

        String ph = c.getPhone();
        if (ph != null) {          //phone
            phone.setText(ph);
            Linkify.addLinks(phone, Linkify.ALL);
        } else {
            TextView tv = findViewById(R.id.phone);
            tv.setVisibility(TextView.GONE);
            phone.setVisibility(TextView.GONE);
        }

        String em = c.getEmail();
        if (em != null) {            //email
            email.setText(em);
            Linkify.addLinks(email, Linkify.ALL);
        } else {
            TextView tv = findViewById(R.id.email);
            tv.setVisibility(TextView.GONE);
            email.setVisibility(TextView.GONE);
        }

        String ws = c.getWebsite();
        if (ws != null) {            //website
            website.setText(ws);
            Linkify.addLinks(website, Linkify.ALL);
        } else {
            TextView tv = findViewById(R.id.website);
            tv.setVisibility(TextView.GONE);
            website.setVisibility(TextView.GONE);
        }

        if (c.getFb() != null) {            //fb icon
            fb = c.getFb();
        } else {
            ImageView fbimg = findViewById(R.id.off_facebook);
            fbimg.setVisibility(ImageView.GONE);
        }

        if (c.getTw() != null) {                //twitter icon
            tw = c.getTw();
        } else {
            ImageView twimg = findViewById(R.id.off_twitter);
            twimg.setVisibility(ImageView.GONE);
        }

        if (c.getYt() != null) {                //yt icon
            yt = c.getYt();
        } else {
            ImageView ytimg = findViewById(R.id.off_youtube);
            ytimg.setVisibility(ImageView.GONE);
        }

        if (c.getImage() == null) {                //officials image
            img.setImageResource(R.drawable.missing);
        } else {
            image = c.getImage();
            picasso.load(image)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(img);
        }
    }

    public void Click(View view) {          //onclick on image
        if (c.getImage() == null) {
            return;
        } else {
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            intent.putExtra("LOCATION", String.valueOf(l));
            intent.putExtra("OFFICE", String.valueOf(o));
            intent.putExtra("NAME", String.valueOf(n));
            intent.putExtra("PARTY", String.valueOf(p));
            intent.putExtra("IMAGE", image);
            startActivity(intent);
        }
    }

    public void partyImgClick(View view) {           //opening respective party website
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(partywebsite));
        startActivity(intent);
    }

    public void facebookClicked(View v) {           //opening Facebook of officials
        String FACEBOOK_URL = "https://www.facebook.com/" + fb;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {//newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + fb;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {            //opening twitter of officials
        Intent intent = null;
        String name = tw;
        try { // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) { // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void youTubeClicked(View v) {            //opening youtube of officials
        String name = yt;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}

