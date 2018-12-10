package cs371m.csc2726.busbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SecondHandler.IUpdate{

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Firestore firestore;
    static boolean active = false;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    protected FirebaseFirestore db;
    protected Auth auth;
    private SupportMapFragment mapFragment;
    private MapHolder mapHolder;
    private RecyclerView recyclerView;
    public Boolean driver;
    public double lat;
    public double lon;


    private FusedLocationProviderClient client;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        active = true;
        driver = false;

        /*ListView kidList = (ListView) findViewById(R.id.theListView);
        String[] kidNames = getResources().getStringArray(R.array.kids);
        ArrayList arrayList = new ArrayList<>(Arrays.asList(kidNames));
        ArrayAdapter adapter=new ArrayAdapter<String>(this, R.layout.row, R.id.text, arrayList);
        kidList.setAdapter(adapter);*/

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = Firestore.getInstance();
        firestore.init(auth);
        Storage.getInstance().init(getApplicationContext());

        new SecondHandler(this);

        if (currentUser == null)
            loginFunc();
        else {
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            String uid = currentUser.getUid();

            Log.d("TAG", "onLocationChanged: true " + uid);


            if (uid.equals("dDUspoyUzweejSd4UUDhj3xBr8q2")) {
                driver = true;
                Log.d("TAG", "driver changed: true ");
            } else {
                driver = false;
            }
        }

        initRecyclerView();

        client = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("TAG", "onLocationChanged: "+ location.toString());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
        else {
            if (driver) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        60, 0, locationListener);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!driver)
                    return;
                ColorAdapter ca = (ColorAdapter) recyclerView.getAdapter();
                Log.d("TAG", "onClick: "+layoutManager);
                ca.removeAll(layoutManager);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapFragment = new SupportMapFragment();
        mapHolder = new MapHolder(this);
        mapFragment.getMapAsync(mapHolder);

        /* Notice the handy method chaining idiom for fragment transactions */
        getSupportFragmentManager().beginTransaction()
                .add(R.id.drawer_layout, mapFragment)
                .hide(mapFragment)
                .commit();
    }

    public void loginFunc() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        int result = 1;
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                result);


        updateUser();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signOutButton) {
            mAuth.signOut();
            loginFunc();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.bus_location) {
            if (driver) {
                if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Location here:", "failed");
                    return false;
                }

                client.getLastLocation().addOnSuccessListener(MainActivity.this,
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                    LatLng pos = new LatLng(lat, lon);
                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                    try {
                                        addresses = geocoder.getFromLocation(lat, lon, 1);
                                        String address = addresses.get(0).getAddressLine(0);
                                        toMapFragment(address);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
            }
            else {
                if (lat == 0.0 || lon == 0.0) {
                    Toast.makeText(this,
                            "Bus drivers location has not been updated", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(lat, lon, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    toMapFragment(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.students) {
            goToMain();
        } else if (id == R.id.add_student) {
            if (!driver) {
                Toast.makeText(this, "You don't have the credentials", Toast.LENGTH_SHORT).show();
                return false;
            }
            addStudentHandler();

        } else if (id == R.id.reset) {
            if (!driver) {
                Toast.makeText(this, "You don't have the credentials", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent i = getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else if (id == R.id.exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToMain() {
        if (active)
            return;
        Intent students = new Intent(this, MainActivity.class);
        startActivity(students);
    }

    public void updateUser() {

        /*Toast.makeText(this, "In updateUser", Toast.LENGTH_SHORT).show();
        String user = mAuth.getCurrentUser().toString();
        TextView userName = findViewById(R.id.userName);
        userName.setText("" + user);
        TextView userEmail = findViewById(R.id.userEmail);
        userEmail.setText("" + userEmail);*/
    }

    public void addStudentHandler() {
        Intent addKid = new Intent(this, AddKid.class);
        final int result = 1;
        startActivityForResult(addKid, result);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // XXX write this entire function
        if (resultCode == RESULT_CANCELED)
            return;
        Bundle extras = data.getExtras();
        String name = ((String)extras.get("kidName"));
        Bitmap bits = ((Bitmap)extras.get("bitmap"));
        if (name != null && bits != null) {
            Firestore fs = Firestore.getInstance();
            String photoId = UUID.randomUUID().toString();
            PhotoObject p = new PhotoObject(currentUser.getUid(), photoId, name);
            fs.saveKid(p);
            byte[] imageBytes = convertBitmapToBytes(bits, 100);
            Storage store = Storage.getInstance();
            store.uploadJpg(p, imageBytes);
            ColorAdapter ca = (ColorAdapter) recyclerView.getAdapter();
            ca.add(name, layoutManager, ca, bits);
        }
    }


    private void initRecyclerView () {
        // Get the widgets reference from XML layout
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // Initialize a new instance of RecyclerView Adapter instance
        adapter = new ColorAdapter(this, this);
        // Set the adapter for RecyclerView
        recyclerView.setAdapter(adapter);
        Log.d("TAG", "initonClick: "+layoutManager);
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private byte[] convertBitmapToBytes(Bitmap bitmap, int compression) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
        byte[] bitmapdata = stream.toByteArray();
        Log.d("TAG", "size of jpg: " + bitmapdata.length);
        return bitmapdata;
    }

    public void toMapFragment(String address) {
        mapHolder.showAddress(address);
        getSupportFragmentManager().beginTransaction().addToBackStack("map")
                .replace(R.id.drawer_layout, mapFragment).show(mapFragment).commit();
    }

    public void requestLocation() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        60, 0, locationListener);
            }
        }

    }

    public void checkDriver() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            return;
        String uid = currentUser.getUid();
        if (uid.equals("dDUspoyUzweejSd4UUDhj3xBr8q2")) {
            driver = true;
            Log.d("TAG", "driver changed: true ");
        }
        else {
            driver = false;
        }
    }
}
