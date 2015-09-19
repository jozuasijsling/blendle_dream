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

import android.text.format.DateUtils;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

import nl.jozuasijsling.blendledream.api.MostRecentResource;
import nl.jozuasijsling.blendledream.database.DreamFeedContentUris;
import nl.jozuasijsling.blendledream.domain.BlendleIssue;
import nl.jozuasijsling.blendledream.mapping.ApiObjectMapper;
import timber.log.Timber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Handles downloading issues from Blendle's Most Recent API endpoint.
 */
public class DownloadDreamFeedService extends GcmTaskService {

    public static final String TAG = "DownloadFeedService";

    @Inject OkHttpClient mOkHttpClient;
    @Inject Gson mGson;
    @Inject ApiObjectMapper mApiMapper;

    @Override
    public void onCreate() {
        super.onCreate();
        BlendleDreamApplication.getMainComponent().inject(this);
    }

    @Override
    public int onRunTask(TaskParams taskParams) {

        Request request = new Request.Builder()
                .tag(TAG)
                .url("https://static.blendle.nl/meta/newsstand/language/code/nl/most_recent.json")
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {

                Timber.tag(TAG).w("Could not download dream feed. " +
                        "Endpoint responded with error code " + response.code() + ". " +
                        "No reattempt scheduled.");
                return GcmNetworkManager.RESULT_FAILURE;
            } else {

                InputStream inputStream = response.body().byteStream();
                Timber.tag(TAG).d("Opened input stream with endpoint. Will start parsing the stream.");

                // Parse the json directly from the input stream.
                JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
                MostRecentResource mostRecent = mGson.fromJson(jsonReader, MostRecentResource.class);
                Timber.tag(TAG).d("Successfully parsed data from the most_recent resource. " +
                        "Will start mapping to the domain model.");

                // Map objects to domain variants.
                List<BlendleIssue> issues = mApiMapper.mapToBlendleIssues(mostRecent.getEmbedded().getIssues());
                Timber.tag(TAG).d("Successfully mapped " + issues.size() + " issues. Will update the local database.");

                // Replace the database entries with the new feed.
                cupboard().withContext(this).put(
                        DreamFeedContentUris.ISSUES_REPLACE_URI, BlendleIssue.class, issues);
                Timber.tag(TAG).i("Successfully updated the dream feed. " + issues.size() + " issues were downloaded.");

                return GcmNetworkManager.RESULT_SUCCESS;
            }
        } catch (IOException e) {

            Timber.tag(TAG).d(e, "Could not download dream feed. Reattempt is scheduled.");
            return GcmNetworkManager.RESULT_RESCHEDULE;
        }
    }

    @Override
    public void onDestroy() {
        mOkHttpClient.cancel(TAG);
        super.onDestroy();
    }

    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();

        // If there's a connection download every hour with a flex window of 15 minutes.
        PeriodicTask periodicDownloadTask = new PeriodicTask.Builder()
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setPeriod(DateUtils.HOUR_IN_MILLIS)
                .setFlex(15 * DateUtils.MINUTE_IN_MILLIS)
                .setPersisted(true)
                .setService(DownloadDreamFeedService.class)
                .setTag(TAG)
                .build();

        GcmNetworkManager.getInstance(this).schedule(periodicDownloadTask);
        Timber.tag(TAG).d("Reinitialized periodic downloads. Next download will happen in one hour.");
    }
}
