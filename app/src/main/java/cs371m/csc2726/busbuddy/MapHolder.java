package cs371m.csc2726.busbuddy;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by thunt on 10/25/16.
 * Holds Maps...What else would it do?
 */

public class MapHolder implements OnMapReadyCallback {
    /* Some from RedFetch some from this example:
    http://theoryapp.com/parse-json-in-java/
    */
    private float defaultZoom = 15.0f;
    private static class NameToLatLngTask extends AsyncTask<String, Object, LatLng> {
        public interface OnLatLngCallback {
            public void onLatLng(LatLng a);
        }

        OnLatLngCallback cb;
        Context context;

        URL geocoderURLBuilder(String address) {
            URL result = null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("maps.googleapis.com")
                        .appendPath("maps")
                        .appendPath("api")
                        .appendPath("geocode")
                        .appendPath("json")
                        // NB: A key is not necessary, but if it exists, it needs the proper
                        // restrictions.  Oddly, in a URL like this
                        // https://console.developers.google.com/apis/credentials/key/3?project=famous-sunbeam-200419
                        // You have to add permissions in the API restrictions tab before it works.
                        .appendQueryParameter("key", context.getResources().getString(R.string.google_maps_key))
                        .appendQueryParameter("address", URLEncoder.encode(address, "UTF-8"));
                result = new URL(builder.build().toString());
            } catch (UnsupportedEncodingException e) {
                Log.e("Geocoder", "Encoding address: " + e.toString());
            } catch (MalformedURLException e) {
                Log.e("Geocoder", "Building URL: " + e.toString());
            }
            return result;
        }

        public NameToLatLngTask(Context ctx, String addr, OnLatLngCallback _cb) {
            context = ctx;
            execute(addr);
            cb = _cb;
        }

        protected LatLng latLngFromJsonString(String json) throws JSONException {
            JSONObject obj = new JSONObject(json);
            LatLng result = null;
            if (!obj.getString("status").equals("OK")) {
                Log.e("URLfetch", "returned status" + obj.getString("status"));
            } else {
                JSONObject loc = obj.getJSONArray("results").getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location");
                double lat = loc.getDouble("lat");
                double lng = loc.getDouble("lng");
                result = new LatLng(lat, lng);
                Log.d("Geocoder", "got lat: " + lat + ", lng: " + lng);
            }
            return result;
        }

        @Override
        protected LatLng doInBackground(String... params) {
            assert(params.length > 1);
            String name = params[0];
            URL url;
            LatLng pos = null;

            /* Try Geocoder first */
            {
                Geocoder geo = new Geocoder(context);

                /* XXX write me
                    Use the Geocoder object for fast(er) geocoding first
                 */
                //new NameToLatLngTask(context, new NameToLatLngTask.OnLatLngCallback(), cb);
                try {
                    List<Address> e;
                    e = geo.getFromLocationName(name, 1);
                    Address location = e.get(0);
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    pos = new LatLng(lat,lon);
                    return pos;
                }
                catch (IOException ex) {
                    return pos;
                }
            }

            /* go remote as a last resort*/
            /*url = geocoderURLBuilder(name);
            if (url == null) {
                cancel(true);
                return null;
            }

            try {
                String result = null;
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.connect();

                if( urlConn.getContentType().startsWith("application/json") )
                    result = fetchJson(urlConn);
                else
                    Log.e("URLfetch", "Result has bad type (not json)");

                if (result != null)
                        pos = latLngFromJsonString(result);
            } catch (IOException e) {
                Log.e("URLfetch", e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("JsonBuild", "JSON malformed");
            }

            if (pos == null) {
                cancel(false);
            }
            return pos;
        }

        protected String readStreamToString(InputStream in) throws IOException{
            int numRead;
            final int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream outString = new ByteArrayOutputStream();

            while ((numRead = in.read(buffer)) != -1) {
                outString.write(buffer, 0, numRead);
                if (isCancelled()) {
                    return null;
                }
            }
            return new String(outString.toByteArray(), "UTF-8");
        }

        protected String fetchJson(HttpURLConnection conn) {
            InputStream in = null;
            String result = null;
            try {
                in = new BufferedInputStream(conn.getInputStream());
                result = readStreamToString(in);
                Log.d("fetchJson", "json " + result);
            } catch( IOException e ) {
                e.printStackTrace();
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;*/
        }

        @Override
        protected void onPostExecute(LatLng result) {
            cb.onLatLng(result);
        }

        @Override
        protected void onCancelled(LatLng result) {
            Log.e("NameToLatLng", "cancelled");
            // Callback does not expect a null value
            //cb.onLatLng(null);
        }
    }


    private GoogleMap gMap;
    private Context context;

    public MapHolder(Context ctx) {
        context = ctx;
    }

    public boolean warnIfNotReady() {
        if (gMap == null) {
            Toast.makeText(context, "No map yet.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    public void showAddress(final String address) {
        if (warnIfNotReady())
            return;
        /* XXX write me */
        //call callback
        new NameToLatLngTask(context, address, new NameToLatLngTask.OnLatLngCallback() {
            @Override
            public void onLatLng(LatLng a) {
                gMap.clear();
                CameraUpdate camUp = CameraUpdateFactory.newLatLngZoom(a, 10);
                gMap.animateCamera(camUp);
                gMap.addMarker(new MarkerOptions().position(a).title(address));
            }
        });
    }
}
