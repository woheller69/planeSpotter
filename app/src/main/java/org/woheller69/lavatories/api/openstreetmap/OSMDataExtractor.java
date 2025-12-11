package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import org.woheller69.lavatories.R;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.api.IDataExtractor;

import java.nio.charset.StandardCharsets;


public class OSMDataExtractor implements IDataExtractor {

    @Override
    public boolean wasCityFound(String data) {
        try {
            JSONObject json = new JSONObject(data);
            return json.has("ac");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Lavatory extractLavatory(String data, int cityId, Context context) {
        try {
            //fix issues with Ã¼ instead of ü, etc. OSM data is UTF-8 encoded
            //Overpass-API does not provide info about utf-8 charset in header
            //String(byte[] bytes, Charset charset) constructs a new String by decoding the specified array of bytes using the specified charset.
            //data = (new String(data.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            Lavatory lavatory = new Lavatory();
            lavatory.setTimestamp((long) ((System.currentTimeMillis())/ 1000));

            JSONObject json = new JSONObject(data);
            lavatory.setOperator(" ");
            lavatory.setOpeningHours(" ");
            lavatory.setAddress1(" ");
            lavatory.setAddress2(" ");

            lavatory.setUuid(json.getString("hex"));
            lavatory.setLatitude(json.getDouble("lat"));
            lavatory.setLongitude(json.getDouble("lon"));
            lavatory.setOperator(json.getString("flight"));
            lavatory.setAddress1(json.getString("desc"));
            String alt = json.has("alt_geom") ? json.getString("alt_geom") : json.getString("alt_baro");
            if (!alt.equals("ground")) alt = alt + "\u2009ft";
            lavatory.setOpeningHours( json.getString("gs")+"\u2009kt "+ alt);
            if (json.has("track")) lavatory.setDistance(json.getDouble("track"));
            else if (json.has("true_heading")) lavatory.setDistance(json.getDouble("true_heading"));
            else if (json.has("mag_heading")) lavatory.setDistance(json.getDouble("mag_heading"));
            else lavatory.setDistance(-1);
            return lavatory;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
