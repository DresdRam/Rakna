package com.example.rakna;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import java.util.Locale;

public class LoadingDialog {

    private Activity activity;
    private Dialog dialog;

    public LoadingDialog(Activity activity){
        this.activity = activity;
        dialog = new Dialog(activity);
        LocaleHelper.setAppLanguage(dialog.getContext());
    }

    public void show(String message){
        setDialogLanguage();
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView textView = dialog.findViewById(R.id.textView_loading_dialog);
        textView.setText(message);

        dialog.create();
        dialog.show();
    }

    private void setDialogLanguage() {
        if (Locale.getDefault().getLanguage().isEmpty()) {
            LocaleHelper.setLocale(activity,LocaleHelper.ENGLISH);
        } else {
            LocaleHelper.setLocale(activity,Locale.getDefault().getLanguage());
        }
    }

    public void dismiss(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

}
