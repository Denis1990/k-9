package com.fsck.k9.activity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.fsck.k9.BaseAccount;
import com.fsck.k9.R;
import com.fsck.k9.provider.MailListViewWidgetProvider;

/**
 * Activity to select an account for the mail list view widget.
 */
public class MailListViewWidgetConfiguration extends AccountList {
    /**
     * Name of the preference file to store the widget configuration.
     */
    private static final String PREFS_NAME = "mail_list_view_widget_configuration.xml";

    /**
     * Prefix for the preference keys.
     */
    private static final String PREF_PREFIX_KEY = "mail_list_view_widget.";


    /**
     * The ID of the widget we are configuring.
     */
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private static void saveAccountUuid(Context context, int appWidgetId, String accountUuid) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(PREF_PREFIX_KEY + appWidgetId, accountUuid);
        editor.commit();
    }

    public static String getAccountUuid(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String accountUuid = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        return accountUuid;
    }

    public static void deleteWidgetConfiguration(Context context, int appWidgetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(PREF_PREFIX_KEY + appWidgetId);
        editor.commit();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Find the widget ID from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget ID, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        setTitle(R.string.mail_list_view_select_account);
    }

    @Override
    protected boolean displaySpecialAccounts() {
        return true;
    }

    @Override
    protected void onAccountSelected(BaseAccount account) {
        // Save widget configuration
        String accountUuid = account.getUuid();
        saveAccountUuid(this, mAppWidgetId, accountUuid);
        // Update widget
        Context context = getApplicationContext();
        MailListViewWidgetProvider.updateAppWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId, accountUuid);

        // Let the caller know that the configuration was successful
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}