package com.example.civiladvocacyapp;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.civiladvocacyapp.Offices.Offices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OfficesLoader {

    private static final String URL = "https://www.googleapis.com/civicinfo/v2/representatives";        //url to use
    private static final String APIKey = "API KEY";             //Api key
    private static String location;
    private static MainActivity mainActivity = new MainActivity();

    public OfficesLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static void setLoc(String l) {             //Setting location for finding using url
        location = l;
    }

    public static void getdata(MainActivity mainActivity) {
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("key", APIKey);
        buildURL.appendQueryParameter("address", location);
        String urlToUse = buildURL.build().toString();          //final url by adding key and location

        Response.Listener<JSONObject> listener =
                response -> handleResults(mainActivity, response.toString());

        Response.ErrorListener error =
                error1 ->  handleResults(mainActivity, null);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET,
                        urlToUse,
                        null,
                        listener,
                        error);

        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, String s) {
        if (s == null) {
            //mainActivity.showtoast("error in finding data from URL");
            return;
        }
        final ArrayList<Offices> officelist = parseJSON(s);
        if (officelist == null)
            mainActivity.showtoast("Error in loading data");
        mainActivity.updateData(officelist);
    }

    private static ArrayList<Offices> parseJSON(String str) {
        ArrayList<Offices> officelist = new ArrayList<>();
        try {
            JSONObject officobj = new JSONObject(str);
            JSONObject normalizedinput = officobj.getJSONObject("normalizedInput");
            JSONArray officesarr = officobj.getJSONArray("offices");
            JSONArray officialsarr = officobj.getJSONArray("officials");

            StringBuilder loc = new StringBuilder();          //location data
            String l1 = "";
            String c = "";
            String s = "";
            String z = "";

            l1 = normalizedinput.has("line1") ? normalizedinput.getString("line1") : null;
            loc.append(l1.equals("") ? "" : l1 + ", ");
            c = normalizedinput.has("city") ? normalizedinput.getString("city") : null;
            loc.append(c.equals("") ? "" : c + ", ");
            s = normalizedinput.has("state") ? normalizedinput.getString("state") : null;
            loc.append(s.equals("") ? "" : s + " ");
            z = normalizedinput.has("zip") ? " " + normalizedinput.getString("zip") : null;
            loc.append(z);

            for (int i = 0; i < officesarr.length(); i++) {     //office data
                String party = null;
                String phone = null;
                String email = null;
                String website = null;
                String fb = null;
                String tw = null;
                String yt = null;
                String image = null;
                String addloc = null;

                JSONObject office = (JSONObject) officesarr.get(i);
                String offname = office.getString("name");
                JSONArray offofficialindicesarr = office.getJSONArray("officialIndices");

                for (int j = 0; j < offofficialindicesarr.length(); j++) {             //data of official for the give indices
                    int idx = Integer.parseInt(offofficialindicesarr.get(j).toString());
                    JSONObject officdetails = (JSONObject) officialsarr.get(idx);
                    final String officname = officdetails.getString("name");

                    party = officdetails.has("party") ? officdetails.getString("party") : null;      //official party name

                    if (officdetails.has("address")) {           //official Address
                        JSONArray arr = officdetails.getJSONArray("address");
                        JSONObject officaddr = (JSONObject) arr.get(0);

                        StringBuilder addr = new StringBuilder();
                        String li1 = "";
                        String li2 = "";
                        String ci = "";
                        String st = "";
                        String zi = "";

                        li1 = officaddr.has("line1") ? officaddr.getString("line1") : "";
                        addr.append(li1.equals("") ? "" : li1 + ", ");
                        li2 = officaddr.has("line2") ? officaddr.getString("line2") : "";
                        addr.append(li2.equals("") ? "" : li2 + ", ");
                        ci = officaddr.has("city") ? officaddr.getString("city") : "";
                        addr.append(ci.equals("") ? "" : ci + ", ");
                        st = officaddr.has("state") ? officaddr.getString("state") : "";
                        addr.append(st.equals("") ? "" : st + " ");
                        zi = officaddr.has("zip") ? officaddr.getString("zip") : "";
                        addr.append(zi);
                        addloc = addr.toString();
                    }

                    if (officdetails.has("phones")) {        //official phone number
                        JSONArray phoneArr = officdetails.getJSONArray("phones");
                        phone = (String) phoneArr.get(0);
                    }

                    if (officdetails.has("emails")) {           //official emails
                        JSONArray emailArr = officdetails.getJSONArray("emails");
                        email = (String) emailArr.get(0);
                    }

                    if (officdetails.has("urls")) {           //official url
                        JSONArray urlArr = officdetails.getJSONArray("urls");
                        website = (String) urlArr.get(0);
                    }

                    if (officdetails.has("channels")) {           //official social media info
                        JSONArray channelarr = officdetails.getJSONArray("channels");
                        for (int k = 0; k < channelarr.length(); k++) {
                            JSONObject ch = (JSONObject) channelarr.get(k);
                            String chtype = ch.getString("type");
                            String chid = ch.getString("id");
                            if (chtype.equals("Facebook")) fb = chid;
                            else if (chtype.equals("Twitter")) tw = chid;
                            else if (chtype.equals("YouTube")) yt = chid;
                            else mainActivity.showtoast("Error");
                        }
                    }
                    image = officdetails.has("photoUrl") ? officdetails.getString("photoUrl") : null;           //official photo url

                    officelist.add(new Offices(loc.toString(),offname, officname, party,addloc, phone, email, website, fb, tw, yt,image));
                }
            }
            return officelist;
        } catch (Exception e) {
            //mainActivity.showtoast(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
