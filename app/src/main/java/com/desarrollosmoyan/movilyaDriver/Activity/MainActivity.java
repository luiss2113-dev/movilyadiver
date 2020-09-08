package com.desarrollosmoyan.movilyaDriver.Activity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollosmoyan.movilyaDriver.DriverApplication;
import com.desarrollosmoyan.movilyaDriver.Fragment.OnGoingTrips;
import com.desarrollosmoyan.movilyaDriver.Fragment.setting;
import com.desarrollosmoyan.movilyaDriver.Helper.LanguageData;
import com.desarrollosmoyan.movilyaDriver.Helper.LocaleUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.desarrollosmoyan.movilyaDriver.Fragment.EarningsFragment;
import com.desarrollosmoyan.movilyaDriver.Fragment.Help;
import com.desarrollosmoyan.movilyaDriver.Fragment.Map;
import com.desarrollosmoyan.movilyaDriver.Fragment.SummaryFragment;
import com.desarrollosmoyan.movilyaDriver.Listeners.ConnectionBooleanChangedListener;
import com.desarrollosmoyan.movilyaDriver.Utilities.Utilities;
import com.squareup.picasso.Picasso;
import com.desarrollosmoyan.movilyaDriver.Bean.Connect;
import com.desarrollosmoyan.movilyaDriver.Helper.SharedHelper;
import com.desarrollosmoyan.movilyaDriver.Helper.URLHelper;
import com.desarrollosmoyan.movilyaDriver.R;
import com.desarrollosmoyan.movilyaDriver.adapter.LanguageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.desarrollosmoyan.movilyaDriver.DriverApplication.trimMessage;

public class MainActivity extends AppCompatActivity {
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_YOURTRIPS = "yourtrips";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_HELP = "help";
    private static final String TAG_EARNINGS = "earnings";
    private static final String TAG_SHARE = "share";
    private static final String TAG_LOGOUT = "logout";
    public static FragmentManager fragmentManager;
    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    Fragment fragment;
    Activity activity;
    Context context;
    Toolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView btnavigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName, approvaltxt;
    private ImageView status;
    private Dialog alertDialog;
    LanguageData languageData;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private static final int REQUEST_LOCATION = 1450;
    Utilities utils = new Utilities();
    boolean push = false;
    Button btnFusedLocation;
    TextView tvLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private static final int APP_PERMISSION_REQUEST = 102;
    private NotificationManager notificationManager;

    Map lFrag;

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        context = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        findViewById();
        Bundle extras = getIntent().getExtras();
        changeStatusBarColor();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (extras != null) {
            push = extras.getBoolean("push");
        }

        map();
        Connect.addMyBooleanListener(new ConnectionBooleanChangedListener() {
            @Override
            public void OnMyBooleanChanged() {
                Toast.makeText(getApplication(), "Changed", Toast.LENGTH_SHORT).show();
            }
        });
        loadNavHeader();
        setUpNavigationView();
        setUpBottomNavigationView();

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(activity, EditProfile.class));
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, APP_PERMISSION_REQUEST);
//        }
    }


    private void findViewById() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        btnavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.usernameTxt);
        approvaltxt = (TextView) navHeader.findViewById(R.id.status_txt);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        status = (ImageView) navHeader.findViewById(R.id.status);
    }

    private void setUpBottomNavigationView() {
        btnavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.b_nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        fragment = new Map();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("push", push);
                        fragment.setArguments(bundle);
                        FragmentManager manager = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.b_nav_history:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SUMMARY;
                        fragment = new OnGoingTrips();
                        drawer.closeDrawers();
                        FragmentManager manager4 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction3 = manager4.beginTransaction();
                        transaction3.replace(R.id.content, fragment);
                        transaction3.addToBackStack(null);
                        transaction3.commit();
                        break;
                    case R.id.b_nav_wallet:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_SUMMARY;
                        fragment = new SummaryFragment();
                        drawer.closeDrawers();
                        FragmentManager manager2 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction1 = manager2.beginTransaction();
                        transaction1.replace(R.id.content, fragment);
                        transaction1.addToBackStack(null);
                        transaction1.commit();
                        break;
                    case R.id.b_nav_profile:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SUMMARY;
                        fragment = new setting();
                        drawer.closeDrawers();
                        FragmentManager manager3 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction2 = manager3.beginTransaction();
                        transaction2.replace(R.id.content, fragment);
                        transaction2.addToBackStack(null);
                        transaction2.commit();
                        break;
                }
                return true;
            }
        });
    }
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        fragment = new Map();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("push", push);
                        fragment.setArguments(bundle);
                        FragmentManager manager = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.nav_profile:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, EditProfile.class));
                        break;
                    case R.id.nav_yourtrips:
                       /* navItemIndex = 1;
                        CURRENT_TAG = TAG_YOURTRIPS;
                        fragment = new YourTrips();
                        GoToFragment();*/
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                        break;
                    case R.id.nav_wallet:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SUMMARY;
                        fragment = new SummaryFragment();
                        drawer.closeDrawers();
                        FragmentManager manager2 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction1 = manager2.beginTransaction();
                        transaction1.replace(R.id.content, fragment);
                        transaction1.addToBackStack(null);
                        transaction1.commit();
                        //GoToFragment();
                        break;
                    case R.id.nav_help:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_HELP;
                        fragment = new Help();
                        drawer.closeDrawers();
                        FragmentManager manager4 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction2 = manager4.beginTransaction();
                        transaction2.replace(R.id.content, fragment);
                        transaction2.addToBackStack(null);
                        transaction2.commit();
                        //GoToFragment();
                        break;
                    case R.id.nav_earnings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_EARNINGS;
                        fragment = new EarningsFragment();
                        drawer.closeDrawers();
                        FragmentManager manager1 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction3 = manager1.beginTransaction();
                        transaction3.replace(R.id.content, fragment);
                        transaction3.addToBackStack(null);
                        transaction3.commit();
//                        GoToFragment();
                        break;
                    case R.id.nav_share:
                        drawer.closeDrawers();
                        navigateToShareScreen(URLHelper.APP_URL+getPackageName());
                        return true;
                    case R.id.nav_language:
                        drawer.closeDrawer(Gravity.START);
                        language_alert_view(MainActivity.this);
                        return true;
                    case R.id.nav_documents:
                        drawer.closeDrawers();
                      //  startActivity(new Intent(context, DriverDocumentActivity.class).putExtra("result",true));
                        startActivity(new Intent(context, UploadDocument.class));
                        return true;
                    case R.id.nav_logout:
                        drawer.closeDrawers();
                        showLogoutDialog();
                        return true;
                    default:
                        navItemIndex = 0;
                }


                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    loadNavHeader();
                }
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    /*------------------Language Dialog here-------------------------*/

    public void language_alert_view(final Context mContext) {
        final ArrayList<LanguageData> languageDataArrayList = new ArrayList<>();
        if (alertDialog != null)
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        final View view = View.inflate(mContext, R.layout.language_lay, null);

        alertDialog = new Dialog(mContext, R.style.dialogwinddow);
        alertDialog.setContentView(view);
        alertDialog.setCancelable(true);
        alertDialog.show();

        RecyclerView langList = alertDialog.findViewById(R.id.langList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        langList.setHasFixedSize(true);
        langList.setLayoutManager(linearLayoutManager);
        try {
            for (int i = 0; i < getResources().getStringArray(R.array.languagelist).length; i++) {

                String lang_name = (getResources().getStringArray(R.array.languagelist))[i];
                String lang_code = (getResources().getStringArray(R.array.languagecode))[i];
                String lang_country_code =(getResources().getStringArray(R.array.languagecountrycode))[i];
                languageData = new LanguageData(lang_name, lang_code, lang_country_code);
                languageDataArrayList.add(languageData);
            }

            LanguageAdapter detailsSizeAdapter = new LanguageAdapter(MainActivity.this, languageDataArrayList);
            langList.setAdapter(detailsSizeAdapter);


            detailsSizeAdapter.setOnItemClickListener(new LanguageAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {

                    alertDialog.dismiss();

                    System.out.println(" Lang_Ccode -- >  " + languageDataArrayList.get(position).getLanguageCode());


                    updateLocale(languageDataArrayList.get(position));
                    /*
                    restart the activity
                     */
                    Intent intent = getIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }


    private void loadNavHeader() {
        // name, website
        txtName.setText(SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "approval_status").equals("new") || SharedHelper.getKey(context, "approval_status").equals("onboarding")) {
            approvaltxt.setTextColor(Color.YELLOW);
            approvaltxt.setText(getText(R.string.waiting_for_approval));
            status.setImageResource(R.drawable.newuser);
        } else if (SharedHelper.getKey(context, "approval_status").equals("banned")) {
            approvaltxt.setTextColor(Color.RED);
            approvaltxt.setText(getText(R.string.banned));
            status.setImageResource(R.drawable.banned);
        } else {
            approvaltxt.setTextColor(Color.GREEN);
            approvaltxt.setText(getText(R.string.approved));
            status.setImageResource(R.drawable.approved);
        }


        utils.print("Profile_PIC", "" + SharedHelper.getKey(context, "picture"));

        // Loading profile image
//        Glide.with(this).load(SharedHelper.getKey(context,"picture"))
//                .placeholder(R.drawable.ic_dummy_user)
//                .error(R.drawable.ic_dummy_user)
//                .crossFade()
//                .thumbnail(0.5f)
//                .bitmapTransform(new CircleTransform(this))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgProfile);
        //Assign current profile values to the edittext
        //Glide.with(activity).load(SharedHelper.getKey(context,"picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProfile);
        Picasso.get().load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProfile);

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
//                if (CURRENT_TAG.equalsIgnoreCase(TAG_SUMMARY) || CURRENT_TAG.equalsIgnoreCase(TAG_EARNINGS)
//                        || CURRENT_TAG.equalsIgnoreCase(TAG_YOURTRIPS)){
//
//                }else{
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    fragment = new Map();
                    GoToFragment();
                    return;
//                }
            } else {
                System.exit(0);
            }
        }
        super.onBackPressed();
    }


    private void map() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment = new Map();
                FragmentManager manager = getSupportFragmentManager();
                @SuppressLint("CommitTransaction")
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();
                fragmentManager = getSupportFragmentManager();
            }
        });
    }

    public void GoToFragment() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawers();
                FragmentManager manager = getSupportFragmentManager();
                @SuppressLint("CommitTransaction")
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });
    }

    public void  navigateToShareScreen(String shareUrl) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        utils.print("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
//	        }

    }


    public void logout() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(context, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                drawer.closeDrawers();
                if (SharedHelper.getKey(context, "login_by").equals("facebook"))
                    LoginManager.getInstance().logOut();
                if (SharedHelper.getKey(context, "login_by").equals("google"))
                    signOut();
                if (!SharedHelper.getKey(MainActivity.this, "account_kit_token").equalsIgnoreCase("")) {
                    Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(MainActivity.this, "account_kit_token"));
                    AccountKit.logOut();
                    SharedHelper.putKey(MainActivity.this, "account_kit_token", "");
                }
                SharedHelper.putKey(context, "current_status", "");
                SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
                SharedHelper.putKey(context, "email", "");
                Intent goToLogin = new Intent(activity, WelcomeScreenActivity.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goToLogin);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                                /*refreshAccessToken();*/
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        logout();
                    }
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                Log.e("getHeaders: Token", SharedHelper.getKey(context, "access_token") + SharedHelper.getKey(context, "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        DriverApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("MainAct", "Google User Logged out");
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(context,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    public  void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.logout));
            builder.setMessage(getString(R.string.exit_confirm));

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

     /*
    save into shared preference
     */

    @Override
    protected void onStop() {
        super.onStop();
       // startService(new Intent(MainActivity.this, FloatWidgetService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //stopService(new Intent(MainActivity.this, FloatWidgetService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  startService(new Intent(MainActivity.this, FloatWidgetService.class));
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void updateLocale(LanguageData languageData ) {

        SharedHelper.putKey(context, "language", languageData.getLanguageCode());

        /*
        set language
         */
        setLanguage();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(context, "language");
        LocaleUtils.setLocale(this, languageCode);
    }

}
