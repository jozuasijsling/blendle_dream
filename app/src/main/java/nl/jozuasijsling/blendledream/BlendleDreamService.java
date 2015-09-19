/*
 * Copyright (c) 2015 Jozua Sijsling <jozua.sijsling@gmail.com>
 *
 * All rights reserved. No warranty, explicit or implicit, provided.
 *
 * NOTICE:  All information contained herein is, and remains the property of Jozua Sijsling. The
 * intellectual and technical concepts contained herein are proprietary to Jozua Sijsling and are
 * protected by trade secret or copyright law. Dissemination of this information or reproduction
 * of this material is strictly forbidden unless prior written permission is obtained from Jozua
 * Sijsling. Access to the source code contained herein is hereby forbidden to anyone unless
 * prior written permission is obtained from Jozua Sijsling. This includes those who have
 * executed Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure
 * of this source code, which includes information that is confidential and/or proprietary, and
 * is a trade secret, of Jozua Sijsling. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC
 * PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT
 * THE EXPRESS WRITTEN CONSENT OF  Jozua Sijsling IS STRICTLY PROHIBITED, AND IN
 * VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION
 * OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR  IMPLY ANY
 * RIGHTS  TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE,
 * OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

package nl.jozuasijsling.blendledream;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.service.dreams.DreamService;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nl.jozuasijsling.blendledream.database.DreamFeedContentUris;
import nl.jozuasijsling.blendledream.domain.BlendleIssue;
import nl.jozuasijsling.blendledream.preferences.AutoDownloadOptions;
import timber.log.Timber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 *
 */
public class BlendleDreamService extends DreamService {

    private static final String TAG = "DayDreamService";

    @Bind(R.id.view_switcher) ViewSwitcher mViewSwitcher;

    @Bind(R.id.image) KenBurnsView mBackgroundImageView;
    @Bind(R.id.provider) TextView mProviderTextView;
    @Bind(R.id.content) TextView mContentView;


    @NonNull private final ContentObserver mContentObserver = new ContentObserver(
            new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            startLoadingLocalData();
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFullscreen(true);
        setInteractive(false);

        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(this).inflate(R.layout.blendle_dream, null, false);
        setContentView(contentView);
        ButterKnife.bind(this, contentView);

        initAutoDownload();
        startLoadingLocalData();
    }

    private void initAutoDownload() {
        AutoDownloadOptions autoDownloadOptions = AutoDownloadOptions.fromPreferences(this);
        if (!autoDownloadOptions.isEnabled()) {
            // For now, auto download must always be enabled. If it is not, then we know
            // the dream was launched for the first time and we should enable it.

            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int connectionResult = googleApiAvailability.isGooglePlayServicesAvailable(this);
            if (connectionResult != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(connectionResult)) {
                    googleApiAvailability.showErrorNotification(this, connectionResult);
                    Timber.tag(TAG).w("Cannot start auto download. Waiting for user to enable Google Play Services.");
                } else {
                    throw new UnsupportedOperationException("Cannot schedule downloads " +
                            "without Google Play Services.");
                }
            } else {

                // If there's a connection download every hour with a flex window of 15 minutes.
                PeriodicTask periodicDownloadTask = new PeriodicTask.Builder()
                        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                        .setPeriod(DateUtils.HOUR_IN_MILLIS)
                        .setFlex(15 * DateUtils.MINUTE_IN_MILLIS)
                        .setPersisted(true)
                        .setService(DownloadDreamFeedService.class)
                        .setTag(DownloadDreamFeedService.TAG)
                        .build();
                GcmNetworkManager.getInstance(this).schedule(periodicDownloadTask);

                Timber.tag(TAG).d("Successfully started periodic download task. Next periodic download starts in one hour.");
                new AutoDownloadOptions(true).writeToPreferences(this);
            }
        } else {

            Timber.tag(TAG).d("Periodic download is already initialized. No action required.");
        }
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        registerDataObserver();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        unregisterDataObserver();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // Cancel any pending one off tasks, but let the periodic task continue.
        GcmNetworkManager.getInstance(this).cancelTask(TAG, DownloadDreamFeedService.class);
    }

    private void startLoadingLocalData() {
        // TODO offload to worker thread
        List<BlendleIssue> issues = cupboard()
                .withContext(this)
                .query(DreamFeedContentUris.ISSUES_URI, BlendleIssue.class)
                .list();
        onDataLoaded(issues);
    }

    private void onDataLoaded(@NonNull List<BlendleIssue> issues) {
        if (issues.isEmpty()) {

            mViewSwitcher.setDisplayedChild(0);
            Toast.makeText(this, "No issues found. Starting new download.", Toast.LENGTH_LONG).show();

            // Since there's no data, let's start a new download task.
            OneoffTask initialDownloadTask = new OneoffTask.Builder()
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setExecutionWindow(0, 30000)
                    .setService(DownloadDreamFeedService.class)
                    .setTag(TAG)
                    .build();
            GcmNetworkManager.getInstance(this).schedule(initialDownloadTask);
            Timber.tag(TAG).d("Scheduled download task to execute within thirty seconds.");

        } else {
            mViewSwitcher.setDisplayedChild(1);

            // TODO Select an issue to show, populate the views with the data...
            // TODO Begin timer that animates between issues.

            Toast.makeText(this, "Found " + issues.size() + " issues. Now to display them...", Toast.LENGTH_LONG).show();
        }
    }

    private void registerDataObserver() {
        getContentResolver().registerContentObserver(DreamFeedContentUris.ISSUES_URI, false, mContentObserver);
    }

    private void unregisterDataObserver() {
        getContentResolver().unregisterContentObserver(mContentObserver);
    }
}
