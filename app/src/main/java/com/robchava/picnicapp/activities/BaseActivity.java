package com.robchava.picnicapp.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.robchava.picnicapp.R;

/**
 * A base activity with some methods that are shared.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * A reference to the progress dialog.
     */
    protected ProgressDialog mDialog;

    /**
     * A reference to any running Asynctask.
     */
    protected static BaseDownloadAsyncTask runningAsyncTask;

    /**
     * Shows the loading dialog.
     */
    protected void showLoadingDialog() {
        if (mDialog == null) {
            final String message = getString(R.string.loading);
            mDialog = new ProgressDialog(this);
            mDialog.setCancelable(false);
            mDialog.setMessage(message);
            mDialog.show();
        }
    }

    /**
     * Dismisses the loading dialog.
     */
    protected void hideLoadingDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    /**
     * Display a dialog with an "OK" button.
     * @param messageResource The message resource ID to display.
     */
    protected void showOkDialog(final int messageResource) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final String message = getString(messageResource);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hideLoadingDialog();

        if (runningAsyncTask != null) {
            runningAsyncTask.dettachActivity();
        }
    }

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

        if (id == R.id.action_refresh) {
            Toast.makeText(this, R.string.refresh, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This is the base class from which the other Asynctasks extend.
     */
    protected static class BaseDownloadAsyncTask extends AsyncTask<Void, Void, Object> {

        /**
         * Reference to the activity.
         */
        BaseActivity mActivity;

        /**
         * When created and in case of orientation change or something similar, this method
         * allows updating the reference to the activity.
         */
        public void attachActivity(final BaseActivity baseActivity) {
            mActivity = baseActivity;
        }

        /**
         * Called when the activity is destroyed.
         */
        public void dettachActivity() {
            mActivity = null;
        }

        @Override
        protected Object doInBackground(Void... params) {
            return null;
        }
    }

}

