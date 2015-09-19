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

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import nl.jozuasijsling.blendledream.domain.ArticleManifest;
import nl.jozuasijsling.blendledream.domain.BlendleIssue;
import nl.jozuasijsling.blendledream.logging.Forester;
import nl.jozuasijsling.blendledream.mapping.GsonFieldConverter;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;

/**
 *
 */
public class BlendleDreamApplication extends Application {

    @Inject Gson mGson;

    @Override
    public void onCreate() {
        super.onCreate();


        setupCrashlytics();
        setupTimber();
        setupCupboard();
    }

    private void setupCrashlytics() {
        Fabric.with(this, new Crashlytics());
    }

    private void setupTimber() {
        Forester.plantForest();
    }

    private void setupCupboard() {
        // configure cupboard field converters
        GsonFieldConverter<ArticleManifest> issueChildConverter = new GsonFieldConverter<>(mGson, ArticleManifest.class);
        Cupboard cupboard = new CupboardBuilder()
                .registerFieldConverter(ArticleManifest.class, issueChildConverter)
                .build();

        // register model classes
        cupboard.register(BlendleIssue.class);

        // set custom cupboard as global instance
        CupboardFactory.setCupboard(cupboard);
    }
}
