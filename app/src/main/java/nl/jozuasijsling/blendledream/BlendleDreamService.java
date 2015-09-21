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

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.service.dreams.DreamService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import nl.jozuasijsling.blendledream.database.DreamFeedContentUris;
import nl.jozuasijsling.blendledream.domain.ArticleManifest;
import nl.jozuasijsling.blendledream.domain.BlendleIssue;
import nl.jozuasijsling.blendledream.domain.ContentElement;
import nl.jozuasijsling.blendledream.preferences.AutoDownloadOptions;
import timber.log.Timber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 *
 */
public class BlendleDreamService extends DreamService {

    private static final String TAG = "DayDreamService";

    private static final long ISSUE_DISPLAY_DURATION = 30_000L;
    private static final ArrayMap<String, Integer> sTextStylePerContentType = new ArrayMap<>(9);
    static {
        sTextStylePerContentType.put(ContentElement.TYPE_BYLINE, R.style.BodyTextByline);
        sTextStylePerContentType.put(ContentElement.TYPE_DATELINE, R.style.BodyTextParagraph);
        sTextStylePerContentType.put(ContentElement.TYPE_HEADER_1, R.style.BodyTextHeader1);
        sTextStylePerContentType.put(ContentElement.TYPE_HEADER_2, R.style.BodyTextHeader2);
        sTextStylePerContentType.put(ContentElement.TYPE_INTRO, R.style.BodyTextIntro);
        sTextStylePerContentType.put(ContentElement.TYPE_KICKER, R.style.BodyTextKicker);
        sTextStylePerContentType.put(ContentElement.TYPE_LEAD, R.style.BodyTextLead);
        sTextStylePerContentType.put(ContentElement.TYPE_PARAGRAPH, R.style.BodyTextParagraph);
        sTextStylePerContentType.put(ContentElement.TYPE_PH, R.style.BodyTextPh);
    }

    @Bind(R.id.view_switcher) ViewSwitcher mViewSwitcher;

    @Bind(R.id.issue_holder) FrameLayout mIssueHolderLayout;
    @Bind(R.id.image) KenBurnsView mBackgroundImageView;
    @Bind(R.id.provider) TextView mProviderTextView;
    @Bind(R.id.publication_date) TextView mPublicationDateTextView;
    @Bind(R.id.content) TextView mContentTextView;

    @Nullable private List<BlendleIssue> mBlendleIssues;
    private int mCurrentIssueIndex;


    @NonNull private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    @NonNull private final ContentObserver mContentObserver = new ContentObserver(
            mUiHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            startLoadingLocalData();
        }
    };
    private Animator mFadeOutAnimator;
    private Animator mFadeInAnimator;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFullscreen(true);
        setInteractive(false);

        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(this).inflate(R.layout.blendle_dream, null, false);
        setContentView(contentView);
        ButterKnife.bind(this, contentView);

        setupAnimations();
        initAutoDownload();
        startLoadingLocalData();
    }

    private void setupAnimations() {
        mFadeOutAnimator = AnimatorInflater.loadAnimator(this, R.animator.fade_out);
        mFadeInAnimator = AnimatorInflater.loadAnimator(this, R.animator.fade_in);
        mFadeOutAnimator.setTarget(mIssueHolderLayout);
        mFadeInAnimator.setTarget(mIssueHolderLayout);
        mFadeOutAnimator.addListener(new FadeOutEventHandler());
        mFadeInAnimator.addListener(new FadeInEventHandler());
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

        mFadeOutAnimator.cancel();
        mFadeInAnimator.cancel();
        mFadeOutAnimator.removeAllListeners();
        mFadeInAnimator.removeAllListeners();
        mFadeOutAnimator.setTarget(null);
        mFadeInAnimator.setTarget(null);
        mFadeOutAnimator = null;
        mFadeInAnimator = null;

        // Cancel any pending one off tasks, but let the periodic task continue.
        GcmNetworkManager.getInstance(this).cancelTask(TAG, DownloadDreamFeedService.class);
    }

    private void startLoadingLocalData() {
        // TODO offload to worker thread
        List<BlendleIssue> issues = cupboard()
                .withContext(this)
                .query(DreamFeedContentUris.ISSUES_URI, BlendleIssue.class)
                .orderBy("mDate DESC")
                .list();
        onDataLoaded(issues);
    }

    private void onDataLoaded(@NonNull List<BlendleIssue> issues) {

        // Stop any ongoing animations.
        mFadeOutAnimator.cancel();
        mFadeInAnimator.cancel();

        if (issues.isEmpty()) {
            mBlendleIssues = null;
            mViewSwitcher.setDisplayedChild(0);
            Timber.tag(TAG).d("No issues found. Starting new download.");

            // TODO Replace with AsyncTask or Loader.. OneOffTasks are too slow
            // and progress or error results cannot be tracked by this ui.

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
            mBlendleIssues = issues;
            mViewSwitcher.setDisplayedChild(1);
            Timber.tag(TAG).d("Found " + issues.size() + " issues. Starting rotating display.");

            selectRandomIssueToDisplay();
            updateIssueDisplay(false);
            mFadeOutAnimator.setStartDelay(ISSUE_DISPLAY_DURATION);
            mFadeOutAnimator.start();
        }
    }

    private void selectRandomIssueToDisplay() {
        assert mBlendleIssues != null;
        mCurrentIssueIndex = new Random().nextInt(mBlendleIssues.size());
    }

    private void updateIssueDisplay(boolean animate) {
        assert mBlendleIssues != null;

        if (animate) {
            mFadeOutAnimator.setStartDelay(0);
            mFadeOutAnimator.start();
        } else {
            updateIssueViews(mBlendleIssues.get(mCurrentIssueIndex));
        }
    }

    private void selectNextIssueToDisplay() {
        assert mBlendleIssues != null;
        mCurrentIssueIndex = (mCurrentIssueIndex + 1) % mBlendleIssues.size();
    }

    private void updateIssueViews(BlendleIssue currentIssue) {
        ArticleManifest manifest = currentIssue.getManifest();
        List<ArticleManifest.ImageInfo> images = manifest.getImages();
        if (images.isEmpty()) {
            Glide.with(this).load(R.drawable.blendle_generic_cover).into(mBackgroundImageView);
        } else {
            // Display the first image, ignore any other images.
            ArticleManifest.ImageInfo firstImage = images.get(0);
            Glide.with(this).load(firstImage.getOriginal().getHref())
                    .placeholder(R.drawable.blendle_generic_cover)
                    .error(R.drawable.blendle_generic_cover)
                    .fallback(R.drawable.blendle_generic_cover)
                    .into(mBackgroundImageView);
        }

        SimpleDateFormat publicationDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
        String formattedDate = publicationDateFormat.format(manifest.getDate());

        // Loop through elements, wrap their text in a paragraph and combine them.
        List<ContentElement> bodyElements = manifest.getBody().getElements();
        String fullBodyText = "";
        int[] elementTextLengths = new int[bodyElements.size()];
        int[] elementTextStartIndexes = new int[bodyElements.size()];
        for (int i = 0; i < bodyElements.size(); i++) {
            ContentElement element = bodyElements.get(i);
            String elementText = "<p>" + element.getContent() + "</p>";
            fullBodyText += elementText;
            elementTextLengths[i] = Html.fromHtml(elementText).length();
            int currentIndex = 0;
            for (int j = 0; j < i - 1; j++) currentIndex += elementTextLengths[j];
            elementTextStartIndexes[i] = currentIndex;
        }

        // Apply style spans based on the element types.
        Spanned spanned = Html.fromHtml(fullBodyText);
        SpannableString formattedBodyText = new SpannableString(spanned);
        for (int i = 0; i < bodyElements.size(); i++) {
            ContentElement element = bodyElements.get(i);
            String type = element.getType();
            int textStyleResId = sTextStylePerContentType.get(type);
            TextAppearanceSpan styleSpan = new TextAppearanceSpan(this, textStyleResId);
            int start = elementTextStartIndexes[i];
            int end = start + elementTextLengths[i];
            formattedBodyText.setSpan(styleSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        mProviderTextView.setText(manifest.getProviderId());
        mPublicationDateTextView.setText(formattedDate);
        mContentTextView.setText(formattedBodyText);
    }

    private void registerDataObserver() {
        getContentResolver().registerContentObserver(DreamFeedContentUris.ISSUES_URI, false, mContentObserver);
    }

    private void unregisterDataObserver() {
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    private class FadeOutEventHandler extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            assert mBlendleIssues != null;
            updateIssueViews(mBlendleIssues.get(mCurrentIssueIndex));
            mFadeInAnimator.start();
        }
    }

    private class FadeInEventHandler extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            mFadeOutAnimator.setStartDelay(ISSUE_DISPLAY_DURATION);
            mFadeOutAnimator.start();
            mFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    selectNextIssueToDisplay();
                    mFadeOutAnimator.removeListener(this);
                }
            });
        }
    }
}
