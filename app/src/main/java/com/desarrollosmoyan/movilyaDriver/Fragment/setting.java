package com.desarrollosmoyan.movilyaDriver.Fragment;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.desarrollosmoyan.movilyaDriver.Activity.EditProfile;
import com.desarrollosmoyan.movilyaDriver.Activity.WelcomeScreenActivity;
import com.desarrollosmoyan.movilyaDriver.DriverApplication;
import com.desarrollosmoyan.movilyaDriver.Helper.LanguageData;
import com.desarrollosmoyan.movilyaDriver.Helper.LocaleUtils;
import com.desarrollosmoyan.movilyaDriver.Helper.SharedHelper;
import com.desarrollosmoyan.movilyaDriver.Helper.URLHelper;
import com.desarrollosmoyan.movilyaDriver.R;
import com.squareup.picasso.Picasso;
import com.desarrollosmoyan.movilyaDriver.adapter.LanguageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.desarrollosmoyan.movilyaDriver.DriverApplication.trimMessage;

public class setting extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Dialog alertDialog;
    private LanguageData languageData;
    private TextView username, viewprofile;
    private CircularImageView userimage;
    private LinearLayout history, share, gethelp, feedback, logout,accountmanager, s_tmeter,s_language;
    public setting() {


    }


    public static setting newInstance(String param1, String param2) {
        setting fragment = new setting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        userimage = view.findViewById(R.id.s_imageview);
        username = view.findViewById(R.id.s_username);
        viewprofile = view.findViewById(R.id.s_viewprofile);
        history = view.findViewById(R.id.s_history);
        share = view.findViewById(R.id.s_share);
        gethelp = view.findViewById(R.id.s_gethelp);
        feedback = view.findViewById(R.id.s_feedback);
        logout = view.findViewById(R.id.s_logout);
        accountmanager = view.findViewById(R.id.s_account);
        s_tmeter = view.findViewById(R.id.s_tmeter);
        s_language = view.findViewById(R.id.s_language);
        Picasso.get().load(SharedHelper.getKey(getActivity(), "picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(userimage);
        username.setText(SharedHelper.getKey(getActivity(), "first_name") + " " + SharedHelper.getKey(getActivity(), "last_name"));
        s_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_alert_view(getActivity());
            }
        });
        s_tmeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new taximeter();

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        });
        accountmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new account();

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PastTrips();

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToShareScreen(URLHelper.APP_URL+ getActivity().getPackageName());
            }
        });
        viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        gethelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Help();

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        });
        return view;
    }

    public void  navigateToShareScreen(String shareUrl) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void logout() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(getActivity(), "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (SharedHelper.getKey(getActivity(), "login_by").equals("facebook"))
                    LoginManager.getInstance().logOut();
                if (SharedHelper.getKey(getActivity(), "login_by").equals("google"))

                if (!SharedHelper.getKey(getActivity(), "account_kit_token").equalsIgnoreCase("")) {
                    Log.e("setting", "Account kit logout: " + SharedHelper.getKey(getActivity(), "account_kit_token"));
                    AccountKit.logOut();
                    SharedHelper.putKey(getActivity(), "account_kit_token", "");
                }
                SharedHelper.putKey(getActivity(), "current_status", "");
                SharedHelper.putKey(getActivity(), "loggedIn", getString(R.string.False));
                SharedHelper.putKey(getActivity(), "email", "");
                Intent goToLogin = new Intent(getActivity(), WelcomeScreenActivity.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goToLogin);
                getActivity().finish();
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
                Log.e("getHeaders: Token", SharedHelper.getKey(getActivity(), "access_token") + SharedHelper.getKey(getActivity(), "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(getActivity(), "access_token"));
                return headers;
            }
        };

        DriverApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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

            LanguageAdapter detailsSizeAdapter = new LanguageAdapter(getActivity(), languageDataArrayList);
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
                    Intent intent = getActivity().getIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void updateLocale(LanguageData languageData ) {

        SharedHelper.putKey(getActivity(), "language", languageData.getLanguageCode());

        setLanguage();

    }
    private void setLanguage() {
        String languageCode = SharedHelper.getKey(getActivity(), "language");
        LocaleUtils.setLocale(getActivity(), languageCode);
    }

    public void displayMessage(String toastString) {
        Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
