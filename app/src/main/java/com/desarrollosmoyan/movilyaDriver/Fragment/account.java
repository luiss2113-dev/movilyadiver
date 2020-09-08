package com.desarrollosmoyan.movilyaDriver.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.desarrollosmoyan.movilyaDriver.API.RetrofitClient;
import com.desarrollosmoyan.movilyaDriver.Activity.WelcomeScreenActivity;
import com.desarrollosmoyan.movilyaDriver.DriverApplication;
import com.desarrollosmoyan.movilyaDriver.Helper.CustomDialog;
import com.desarrollosmoyan.movilyaDriver.Helper.SharedHelper;
import com.desarrollosmoyan.movilyaDriver.Helper.URLHelper;
import com.desarrollosmoyan.movilyaDriver.Model.BankAccountResponse;
import com.desarrollosmoyan.movilyaDriver.Models.Errorresponse;
import com.desarrollosmoyan.movilyaDriver.R;
import com.desarrollosmoyan.movilyaDriver.Utilities.Utilities;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static com.desarrollosmoyan.movilyaDriver.DriverApplication.trimMessage;

public class account extends Fragment {
    private static DecimalFormat df = new DecimalFormat("0.00");
    private Dialog myDialog;
    private ImageView edit_bank, provider_img, backArrow;
    private CardView view_bank;
    private FrameLayout bottom_sheet;
    private BottomSheetBehavior sheetBehavior;
    private TextView acc_pendingwith, acc_revenue, acc_withdrawn, acc_currentbalance, provider_name;
    private TextView b_bankname, b_ifsc, b_micr, b_accountholdername, b_accountnum, b_type;
    private EditText b_banknamei, b_ifsci, b_micri, b_accountholdernamei, b_accountnumi, b_typei;
    private OnFragmentInteractionListener mListener;
    private Button update_bank, withdraw_amount;

    public account() {

    }

    public static account newInstance(String param1, String param2) {
        account fragment = new account();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        myDialog = new Dialog(Objects.requireNonNull(getActivity()));
        update_bank = view.findViewById(R.id.update_bank);
        provider_img = view.findViewById(R.id.provider_img);
        backArrow = view.findViewById(R.id.backArrow);
        provider_name = view.findViewById(R.id.provider_name);
        b_bankname = view.findViewById(R.id.b_bankname);
        withdraw_amount = view.findViewById(R.id.with_amount);
        b_ifsc = view.findViewById(R.id.b_ifsc);
        b_micr = view.findViewById(R.id.b_micr);
        b_accountholdername = view.findViewById(R.id.b_accountholdername);
        b_accountnum = view.findViewById(R.id.b_accnumber);
        b_type = view.findViewById(R.id.b_type);
        b_banknamei = view.findViewById(R.id.b_banknamei);
        b_ifsci = view.findViewById(R.id.b_ifsci);
        b_micri = view.findViewById(R.id.b_micri);
        b_accountholdernamei = view.findViewById(R.id.b_accountholdernamei);
        b_accountnumi = view.findViewById(R.id.b_accountnumi);
        b_typei = view.findViewById(R.id.b_typei);
        edit_bank = view.findViewById(R.id.edit_bank);
        view_bank = view.findViewById(R.id.view_bank_account);
        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        acc_pendingwith = view.findViewById(R.id.acc_pendingwith);
        acc_revenue = view.findViewById(R.id.acc_revenue);
        acc_withdrawn = view.findViewById(R.id.acc_withdrawn);
        acc_currentbalance = view.findViewById(R.id.acc_currentbalance);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        Glide.with(getActivity()).load(SharedHelper.getKey(getActivity(),"picture")).placeholder(getResources().getDrawable(R.drawable.ic_dummy_user)).into(provider_img);
        provider_name.setText(SharedHelper.getKey(getActivity(), "first_name")+" "+SharedHelper.getKey(getActivity(), "last_name"));
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new setting();

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        });
        withdraw_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SharedHelper.getKey(getActivity(),"currentbalance").equals(null) && !SharedHelper.getKey(getActivity(),"currentbalance").equals("0.00") ){
                    final CustomDialog customDialog = new CustomDialog(getActivity());
                    customDialog.setCancelable(false);
                    customDialog.show();
                    Call<Errorresponse> call = RetrofitClient.getInstance().getApi().addwithdraw(SharedHelper.getKey(getActivity(), "id"),SharedHelper.getKey(getActivity(), "bid"), SharedHelper.getKey(getActivity(),"currentbalance"));
                    call.enqueue(new Callback<Errorresponse>() {
                        @Override
                        public void onResponse(Call<Errorresponse> call, retrofit2.Response<Errorresponse> response) {
                            if(response.isSuccessful()){
                                if(response.body().isError()){
                                    Toast.makeText(getActivity(), "Withdraw Failed", Toast.LENGTH_SHORT).show();
                                }else{
                                    ShowPopuplogout();
                                }
                            }
                            customDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<Errorresponse> call, Throwable t) {
                            customDialog.dismiss();
                        }
                    });
                }
            }
        });
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        view_bank.setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        view_bank.setVisibility(View.VISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        update_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatebankaccount();
            }
        });
        edit_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_bank.setVisibility(View.GONE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        getProviderSummary();
        getbankaccount();
        return view;
    }

    private void updatebankaccount() {
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setCancelable(false);
        customDialog.show();
        Call<Errorresponse> call = RetrofitClient.getInstance().getApi().addbankaccount(
                b_accountholdernamei.getText().toString(),
                b_typei.getText().toString() ,
                b_banknamei.getText().toString(),
                b_accountnumi.getText().toString(),
                b_ifsci.getText().toString(),
                b_micri.getText().toString(),
                SharedHelper.getKey(getActivity(), "id"),
               "", "");
        call.enqueue(new Callback<Errorresponse>() {
            @Override
            public void onResponse(Call<Errorresponse> call, retrofit2.Response<Errorresponse> response) {
                if(response.isSuccessful()){
                    if(response.body().isError()){
                        Toast.makeText(getActivity(), "Bank Account Update Failed", Toast.LENGTH_SHORT).show();
                    }else{
                        getbankaccount();
                        Toast.makeText(getActivity(), "Bank Account Success", Toast.LENGTH_SHORT).show();
                    }
                }
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                view_bank.setVisibility(View.VISIBLE);
                customDialog.dismiss();
                Utilities.hideKeyboard(getActivity());
            }

            @Override
            public void onFailure(Call<Errorresponse> call, Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    private void getbankaccount() {
        Call<BankAccountResponse> call = RetrofitClient.getInstance().getApi().getbankaccount(SharedHelper.getKey(getActivity(), "id"));
        call.enqueue(new Callback<BankAccountResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<BankAccountResponse> call, retrofit2.Response<BankAccountResponse> response) {
                if(response.isSuccessful()){
                    BankAccountResponse resp = response.body();
                    if(resp.getBankaccount().getPending() != null){
                        acc_pendingwith.setText(SharedHelper.getKey(getActivity(), "currency")+resp.getBankaccount().getPending());
                    }
                    if(resp.getBankaccount().getPaid() != null){
                        acc_withdrawn.setText(SharedHelper.getKey(getActivity(), "currency")+resp.getBankaccount().getPaid());
                    }
                    SharedHelper.putKey(getActivity(), "currentbalance",
                            df.format ( Double.valueOf(SharedHelper.getKey(getActivity(), "totalrev") ) -  Double.valueOf(resp.getBankaccount().getTotal())));
                    SharedHelper.putKey(getActivity(), "bid", resp.getBankaccount().getBid());
                    acc_currentbalance.setText(SharedHelper.getKey(getActivity(), "currency")+
                            df.format ( Double.valueOf(SharedHelper.getKey(getActivity(), "totalrev") ) -  Double.valueOf(resp.getBankaccount().getTotal())));
                    b_bankname.setText(resp.getBankaccount().getBank_name());
                    b_ifsc.setText(resp.getBankaccount().getIFSC_code());
                    b_micr.setText(resp.getBankaccount().getMICR_code());
                    b_accountnum.setText(resp.getBankaccount().getAccount_number());
                    b_accountholdername.setText(resp.getBankaccount().getAccount_name());
                    b_type.setText(resp.getBankaccount().getType());
                    b_banknamei.setText(resp.getBankaccount().getBank_name());
                    b_ifsci.setText(resp.getBankaccount().getIFSC_code());
                    b_micri.setText(resp.getBankaccount().getMICR_code());
                    b_accountnumi.setText(resp.getBankaccount().getAccount_number());
                    b_accountholdernamei.setText(resp.getBankaccount().getAccount_name());
                    b_typei.setText(resp.getBankaccount().getType());
                }
            }

            @Override
            public void onFailure(Call<BankAccountResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @SuppressLint("SetTextI18n")
    public void ShowPopuplogout() {
        myDialog.setContentView(R.layout.dialog_withdraw_success);
        TextView email = myDialog.findViewById(R.id.popemail);
        TextView datetext = myDialog.findViewById(R.id.popdata);
        TextView name = myDialog.findViewById(R.id.popfullname);
        TextView time = myDialog.findViewById(R.id.poptime);
        TextView amount = myDialog.findViewById(R.id.popamount);

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        Date currentTime = Calendar.getInstance().getTime();

        email.setText(SharedHelper.getKey(getActivity(), "email"));
        datetext.setText(dateFormat.format(currentTime));
        time.setText(dateFormat2.format(currentTime));
        name.setText(SharedHelper.getKey(getActivity(), "first_name")+" "+SharedHelper.getKey(getActivity(), "last_name"));
        amount.setText(SharedHelper.getKey(getActivity(), "currency")+" "+SharedHelper.getKey(getActivity(), "currentbalance"));
        FloatingActionButton popdismiss = myDialog.findViewById(R.id.popdismiss);
        popdismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                getbankaccount();
            }
        });
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.closeOptionsMenu();
        myDialog.show();
    }

    public void getProviderSummary() {
        {
            final CustomDialog customDialog = new CustomDialog(getActivity());
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SUMMARY, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("summary", response.toString());
                    customDialog.dismiss();
                    SharedHelper.putKey(getActivity(),"totalrev", response.optString("withdraw"));
                    acc_revenue.setText(SharedHelper.getKey(getContext(), "currency")+response.optString("withdraw"));



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                    e.printStackTrace();
                                }
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity();
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
                            e.printStackTrace();
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getProviderSummary();
                        }
                    }
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"));
                    Log.e("", "Access_Token" + SharedHelper.getKey(getContext(), "access_token"));
                    return headers;
                }
            };

            DriverApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        }
    }

    public void displayMessage(String toastString) {
        Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }


    public void GoToBeginActivity() {
        SharedHelper.putKey(getContext(), "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(getContext(), WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }


}
