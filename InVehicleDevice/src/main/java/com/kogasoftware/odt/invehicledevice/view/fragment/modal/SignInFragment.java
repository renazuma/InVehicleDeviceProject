package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.SignInErrorBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * サインイン情報の入力画面
 */
public class SignInFragment
        extends PreferenceFragment
        implements LoaderCallbacks<Cursor>, OnSharedPreferenceChangeListener {

    private static final int LOADER_ID = 1;
    private static final String FIRST_LOAD_KEY = "first_load";

    // TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
    private static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + SignInFragment.class;

    private final List<Dialog> dialogs = Lists.newLinkedList();
    private LoaderManager loaderManager;
    private SharedPreferences preferences;
    private ExecutorService executor;
    private Boolean firstLoad;
    private Preference policyLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_in_fragment, container, false);
    }

    private final BroadcastReceiver errorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAlertDialog(SignInErrorBroadcastIntent.of(intent).getMessage());
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.sign_in_preference);
        if (savedInstanceState != null) {
            firstLoad = savedInstanceState.getBoolean(FIRST_LOAD_KEY, true);
        } else {
            firstLoad = true;
        }
        loaderManager = getLoaderManager();
        executor = Executors.newFixedThreadPool(1);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);
        getActivity().registerReceiver(errorReceiver, new IntentFilter(SignInErrorBroadcastIntent.ACTION));
        loaderManager.initLoader(LOADER_ID, null, this);
        Button saveConfigButton = (Button) getView().findViewById(R.id.save_config_button);
        saveConfigButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveConfigButtonClick();
            }
        });

        updateSummary();

        policyLink = (Preference) findPreference("privacy_policy_link");
        policyLink.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String policySiteUri = getString(R.string.privacy_policy_url);
                new AlertDialog.Builder(getActivity())
                        .setTitle("プライバシーポリシー")
                        .setMessage("外部ブラウザでページを表示します。よろしいですか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse(policySiteUri);
                                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(i);
                            }

                        })
                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FIRST_LOAD_KEY, firstLoad);
    }

    private Boolean checkAscii(String value, Integer id, List<String> errors) {
        if (Strings.isNullOrEmpty(value)) {
            errors.add(String.format(Locale.US,
                    getString(R.string.error_null_or_empty), getString(id)));
            return false;
        } else if (!CharMatcher.ASCII.matchesAllOf(value)) {
            errors.add(String.format(Locale.US,
                    getString(R.string.error_non_ascii), getString(id)));
            return false;
        } else {
            return true;
        }
    }

    private List<String> check() {
        List<String> errors = Lists.newLinkedList();
        String url = preferences.getString(InVehicleDevice.Columns.URL, "");
        if (checkAscii(url, R.string.server_url, errors)) {
            Uri uri = Uri.parse(url);
            if (Strings.isNullOrEmpty(uri.getScheme())
                    || Strings.isNullOrEmpty(uri.getHost())) {
                errors.add(String.format(Locale.US,
                        getString(R.string.error_invalid_uri),
                        getString(R.string.server_url)));
            }
        }
        checkAscii(preferences.getString(InVehicleDevice.Columns.LOGIN, ""),
                R.string.login, errors);
        checkAscii(
                preferences.getString(InVehicleDevice.Columns.PASSWORD, ""),
                R.string.password, errors);
        return errors;
    }

    private void onSaveConfigButtonClick() {
        List<String> errors = check();
        if (!errors.isEmpty()) {
            String li = errors.size() == 1 ? "" : "- ";
            showAlertDialog(li + Joiner.on("\n" + li).join(errors));
            return;
        }
        showProgressDialog();

        final ContentResolver contentResolver = getActivity().getContentResolver();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                for (String key : new String[]{InVehicleDevice.Columns._ID,
                        InVehicleDevice.Columns.URL,
                        InVehicleDevice.Columns.LOGIN,
                        InVehicleDevice.Columns.PASSWORD}) {
                    values.put(key, preferences.getString(key, null));
                }
                contentResolver.insert(InVehicleDevice.CONTENT.URI, values);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loaderManager.destroyLoader(LOADER_ID);
        executor.shutdownNow();
        getActivity().unregisterReceiver(errorReceiver);
        dismissAllDialogs();
    }

    public static SignInFragment newInstance() {
        SignInFragment fragment = new SignInFragment();
        return fragment;
    }


    // TODO: Loaderは同じqueryを見るものを、activity起動時にも定義している。（そちらは初期表示用）
    // TODO: 纏めてしまって良いのでは？
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), InVehicleDevice.CONTENT.URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // 0取得結果が無い場合
        if (!cursor.moveToFirst()) {
            firstLoad = false;
            return;
        }

        // トークンが取得済みかつfirstLoadではない場合に、画面を隠す
        if (cursor.getString(cursor.getColumnIndexOrThrow("authentication_token")) != null && !firstLoad) {
            Fragments.hide(this);
        }

        // データが存在する場合の初期表示
        firstLoad = false;
        Editor editor = preferences.edit();
        for (String key : new String[]{InVehicleDevice.Columns._ID,
                InVehicleDevice.Columns.URL,
                InVehicleDevice.Columns.LOGIN,
                InVehicleDevice.Columns.PASSWORD}) {
            String value = cursor.getString(cursor.getColumnIndexOrThrow(key));
            editor.putString(key, value);
        }
        editor.apply();
    }

    private void showAlertDialog(String message) {
        if (!isAdded()) {
            return;
        }
        dismissAllDialogs();
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.an_error_occurred))
                .setPositiveButton(android.R.string.ok, null)
                .setMessage(message).show();
        dialogs.add(alertDialog);
    }

    private void showProgressDialog() {
        if (!isAdded()) {
            return;
        }
        dismissAllDialogs();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(Html
                .fromHtml(getString(R.string.checking_connection_config)));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        dialogs.add(progressDialog);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void updateSummary() {
        EditTextPreference url = (EditTextPreference) findPreference("url");
        url.setSummary(url.getText());
        EditTextPreference login = (EditTextPreference) findPreference("login");
        login.setSummary(login.getText());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updateSummary();
    }

    private void dismissAllDialogs() {
        for (Dialog dialog : dialogs) {
            dialog.dismiss();
        }
        dialogs.clear();
    }

    // TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
    public static void showModal(InVehicleDeviceActivity inVehicleDeviceActivity) {
        if (inVehicleDeviceActivity.destroyed) {
            return;
        }

        FragmentManager fragmentManager = inVehicleDeviceActivity.getFragmentManager();

        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) {
            return;
        }

        Fragments.showModalFragment(fragmentManager, SignInFragment.newInstance(), FRAGMENT_TAG);
    }
}
