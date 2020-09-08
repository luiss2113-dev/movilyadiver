package com.desarrollosmoyan.movilyaDriver.Helper;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

public class CustomDialog extends ProgressDialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setIndeterminate(true);
        setMessage("Por favor espere...");
    }
}
