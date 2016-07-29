package com.samaritan.android.samaritan.utilities;

import android.os.AsyncTask;
import android.view.View;

import com.samaritan.android.samaritan.Samaritan;
import com.thetorine.samaritan.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Connection extends AsyncTask<String,Void,Void> {
    public String response;
    public Samaritan s;
    public Connection(Samaritan s){
        this.s = s;
    }
    @Override
    public Void doInBackground(String...messages) {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 2000);
        JSONObject json = new JSONObject();
        response = "calculating response";

        try {
            HttpPost post = new HttpPost("https://samaritan.herokuapp.com/remote_query");
            json.put("query", messages[0]);
            StringEntity se = new StringEntity(json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);

            HttpResponse httpResponse = client.execute(post);

            if (httpResponse != null) {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity())).getString("response");
            }
        } catch (Exception e) {
            response = "error";
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void booger) {
        s.parseText(response);
        s.startAnimation();

        // Quickly finish animating "listen" animation so the app can start the "display" animation
        View v = s.findViewById(R.id.black_line);
        if (v.getAnimation() != null) {
            v.getAnimation().setDuration(200);
            v.requestLayout();
        }
    }
}