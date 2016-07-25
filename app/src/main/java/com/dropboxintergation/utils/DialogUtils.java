/*
 *  Copyright © 2015,
 * Written under contract by vijay
 */

package com.dropboxintergation.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dropboxintergation.R;

/**
 * Utility class to manage dialogs
 */
public class DialogUtils {


    private static ProgressDialog mProgressDialog;
    private static AlertDialog mAlertDialog;


    /**
     * To show progress messages.
     *
     * @param context Calling context.
     * @param message Progress message.
     */
    public static void showProgressDialog(Context context, String message) {
        try {
            if (mProgressDialog == null || !mProgressDialog.isShowing()) {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage(message);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
            } else {
                mProgressDialog.setMessage(message);
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (WindowManager.BadTokenException bte) {
            bte.printStackTrace();
        }
    }


    /**
     * Dialog to handle default alert messages.
     *
     * @param context                  Calling context.
     * @param title                    Title of dialog.
     * @param message                  Message to display in dialog.
     * @param positiveBtnText          Positive button text. Can be null for hiding this button in dialog.
     * @param positiveBtnClickListener Listener for handling positive button click.
     * @param negativeBtnText          Negative button text. Can be null for hiding this button in dialog.
     * @param negativeBtnClickListener Listener for handling negative button click.
     * @param cancelable               Boolean for blocking or non-blocking dialog.
     */
    public static void showDefaultAlertDialog(Context context, String title, String message,
                                              String positiveBtnText, DialogInterface.OnClickListener positiveBtnClickListener,
                                              String negativeBtnText, DialogInterface.OnClickListener negativeBtnClickListener,
                                              boolean cancelable) {
        try {
            if (mAlertDialog == null || !mAlertDialog.isShowing()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                if (!TextUtils.isEmpty(title)) {
                    alertDialogBuilder.setTitle(title);
                }
                if (!TextUtils.isEmpty(message)) {
                    alertDialogBuilder.setMessage(message);
                }
                alertDialogBuilder
                        .setCancelable(cancelable)
                        .setPositiveButton(positiveBtnText, positiveBtnClickListener)
                        .setNegativeButton(negativeBtnText, negativeBtnClickListener);
                mAlertDialog = alertDialogBuilder.create();
                mAlertDialog.show();
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (WindowManager.BadTokenException bte) {
            bte.printStackTrace();
        }
    }

    /**
     * To dismiss progress dialog associated with mProgressDialog instance.
     */
    public static void dismissProgress() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (WindowManager.BadTokenException bte) {
            bte.printStackTrace();
        }
    }


    /**
     *
     * @param context
     * @param actions
     * @param mListener
     */
    public static void showAlertDialog(Context context,String [] actions,DialogInterface.OnClickListener mListener){

        try {
            if (mAlertDialog == null || !mAlertDialog.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(actions, mListener);
                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }


    /**
     *
     * @param context
     * @param mListener
     */
    public static void showFolderCreationDialog(final Activity context, final FolderNameSelectionCallback mListener){

        try {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = context.getLayoutInflater();
            View propmtView = inflater.inflate(R.layout.dialog_create_folder, null);
            final EditText nameFiled = (EditText) propmtView.findViewById(R.id.et_folder_name);
            builder.setView(propmtView);
                builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameFiled.getText().toString().trim();
                        if(name.isEmpty()){
                            Toast.makeText(context,"Please enter folder name",Toast.LENGTH_SHORT).show();
                        }else{
                            mListener.onDialogNameSelected(name);
                            dialog.dismiss();
                        }

                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            builder.create().show();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }



    /**
     * Method which shows popup when there is no network
     *
     * @param context               Calling context.
     * @param finishCurrentActivity boolean variables set to true if we want to finish the current activity
     */

    public static void showNoNetworkDialog(final Context context, final boolean finishCurrentActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.no_network))
                .setMessage(context.getString(R.string.please_check_internet_connection))
                .setPositiveButton(context.getString(R.string.ok).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (finishCurrentActivity && context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                });
            builder.create().show();
    }
}
