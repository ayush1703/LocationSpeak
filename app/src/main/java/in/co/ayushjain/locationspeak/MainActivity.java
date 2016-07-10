package in.co.ayushjain.locationspeak;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends ActionBarActivity {

    Button btnShowLocation;
    JSONArray contacts = null;
    GPSTracker gps;
    String address;
    ArrayList<HashMap<String, String>> weatherList;
    ProgressDialog dialog;
String temp,pressure,humidity,temp_min,temp_max,sunrise,sunset,country;
    Bitmap icon = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
         btnShowLocation.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(getApplicationContext(), "Buton Clicked", Toast.LENGTH_LONG).show();

                 gps = new GPSTracker(MainActivity.this);
                 if (gps.canGetLocation()) {
                     Toast.makeText(getApplicationContext(), "if executed", Toast.LENGTH_LONG).show();

                     double latitude = gps.getLatitude();
                     double longitude = gps.getLongitude();
                     address = gps.getCompleteAddressString(latitude, longitude);
                     Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                     Toast.makeText(getApplicationContext(), "Exact address is" + address, Toast.LENGTH_LONG).show();
                     Toast.makeText(getApplicationContext(), "Last line of if", Toast.LENGTH_LONG).show();

                 } else {
                     gps.showSettingsAlert();
                     Toast.makeText(getApplicationContext(), "Else part", Toast.LENGTH_LONG).show();

                 }
             }
         });
        new retrieve_weatherTask().execute();}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected class retrieve_weatherTask extends AsyncTask<Void, String, String> {



        protected void onPreExecute(){
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
// TODO Auto-generated method stub
            String qResult = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/weather?lat=28&lon=77&appid=44db6a862fba0b067b1930da0d769e98");

            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream inputStream = entity.getContent();
                    Reader in = new InputStreamReader(inputStream);
                    BufferedReader bufferedreader = new BufferedReader(in);
                    StringBuilder stringBuilder = new StringBuilder();
                    String stringReadLine = null;
                    while ((stringReadLine = bufferedreader.readLine()) != null) {
                        stringBuilder.append(stringReadLine + "\n");
                    }
                    qResult = stringBuilder.toString();
                }
                Log.d("Response: ", "> " + qResult);

                if (qResult != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(qResult);

                        // Getting JSON Array node
                        contacts = jsonObj.getJSONArray("weather");

                        // looping through All Contacts
                        JSONObject c = jsonObj.getJSONObject("main");

                        temp = c.getString("temp");
                        pressure = c.getString("pressure");
                        humidity = c.getString("humidity");
                        temp_min = c.getString("temp_min");
                        temp_max = c.getString("temp_max");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("sys");
                        sunrise = phone.getString("sunrise");
                        sunset = phone.getString("sunset");
                        country = phone.getString("country");

                        // tmp hashmap for single contact
                        HashMap<String, String> weather = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        weather.put("temp", temp);
                        weather.put("pressure", pressure);
                        weather.put("humidity", humidity);
                        weather.put("temp_min", temp_min);
                        weather.put("temp_max", temp_max);
                        weather.put("sunrise", sunrise);
                        weather.put("sunset", sunset);
                        weather.put("country", country);
                        weatherList.add(weather);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
              }
 catch (ClientProtocolException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG)
                        .show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG)
                        .show();
            }



            return null;

        }

        protected void onPostExecute(String result) {
            System.out.println("POST EXECUTE");
            if(dialog.isShowing())
                dialog.dismiss();
           //show weather
            Toast.makeText(MainActivity.this, temp, Toast.LENGTH_LONG)
                    .show();
        }
    }

}



