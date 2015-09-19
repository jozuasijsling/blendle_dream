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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nl.jozuasijsling.blendledream.database.DreamFeedContentUris;
import nl.jozuasijsling.blendledream.domain.BlendleIssue;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 *
 */
public class BlendleDreamService extends DreamService {

    @Bind(R.id.pager) ViewPager mViewPager;

    @NonNull private final ContentObserver mContentObserver = new ContentObserver(
            new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {

        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFullscreen(true);
        setInteractive(true);

        @SuppressLint("InflateParams") View contentView =
                LayoutInflater.from(this).inflate(R.layout.blendle_dream, null, false);
        setContentView(contentView);
        ButterKnife.bind(this, contentView);

        // TODO offload to worker thread
        List<BlendleIssue> issues = cupboard()
                .withContext(this)
                .query(DreamFeedContentUris.ISSUES_URI, BlendleIssue.class)
                .list();

        if (issues.isEmpty()) {
            Toast.makeText(this, "No issues to show.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Found " + issues.size() + " issues.", Toast.LENGTH_LONG).show();
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
    }

    private void registerDataObserver() {
        getContentResolver().registerContentObserver(DreamFeedContentUris.ISSUES_URI, false, mContentObserver);
    }

    private void unregisterDataObserver() {
        getContentResolver().unregisterContentObserver(mContentObserver);
    }
}
