package de.moritzs.glass2;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by moritz on 14.06.15.
 */
public class SalesForceAccess {
    private String deviceId;
    private String accessToken = null;
    public SalesForceAccess(Context context) {

        // Get device ID
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        deviceId = deviceUuid.toString();

        new SalesForceAuth().execute();
        //salesForceAuth();

    }
    private class SalesForcePost extends AsyncTask<Double, Void, Void> {
        @Override
        protected Void doInBackground(Double... doubles){
            URL url = null;
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("Accuracy__c", doubles[0]);
                jsonBody.put("Device_ID__c", deviceId);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                TimeZone tz = TimeZone.getTimeZone("UTC");
                sdf.setTimeZone(tz);
                jsonBody.put("TimeStamp__c", sdf.format(new Date()));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                url = new URL("https://emea.salesforce.com/services/data/v20.0/sobjects/Correct__c");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer "+ accessToken);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoOutput(true);
            DataOutputStream dataOut = null;
            try {
                dataOut = new DataOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dataOut.write(jsonBody.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dataOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get response
            InputStream is = null;
            try {
                int status = connection.getResponseCode();
                System.out.println(status);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String res = response.toString();
            System.out.println(res);

            //return "lala";

            //String id = res.substring(1, res.length() - 2);
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            //accessToken = result;
            Log.i("Successfully uploaded statistics", "SalesForceAccess");
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}




    }
    private class SalesForceAuth extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids){

            String body = "grant_type=password&client_id=3MVG9WtWSKUDG.x7QdmCkyaty0ZZPQIRY1uLwvhSaXxwjC5jTCCMCSv.BOSioIC3Wfrj6m77eYqiTY1yfLTGn&client_secret=8310173715955844635&username=hasanyah@gmail.com&password=F1tGlassnesso7lIkGC1hbgix8XEDDKESmCPh";

            URL url = null;
            try {
                url = new URL("https://login.salesforce.com/services/oauth2/token"+"?"+body);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("User-Agent", "ConnectOnTap/");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoOutput(true);
            DataOutputStream dataOut = null;
            try {
                dataOut = new DataOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dataOut.write(body.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dataOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get response
            InputStream is = null;
            try {
                int status = connection.getResponseCode();
                System.out.println(status);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String res = response.toString();
            System.out.println(res);
            String at;
            try {
                JSONObject jsonResponse = new JSONObject(res);
                at = jsonResponse.getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }

            return at;

            //String id = res.substring(1, res.length() - 2);
        }
        @Override
        protected void onPostExecute(String result) {
            accessToken = result;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    // has to be async..!!!
    public void pushData(double wrongRatio) {
        // check for presence of accessToken
        if(accessToken == null)
            return;
        new SalesForcePost().execute(wrongRatio);
    }
}
