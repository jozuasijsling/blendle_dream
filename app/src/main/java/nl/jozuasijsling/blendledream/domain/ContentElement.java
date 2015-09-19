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

package nl.jozuasijsling.blendledream.domain;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.util.Arrays;

/**
 * A snippet of content, its type indicates how it should be displayed.
 */
public class ContentElement {

    public static final String TYPE_HEADER_1 = "hl1";
    public static final String TYPE_HEADER_2 = "hl2";
    public static final String TYPE_INTRO = "intro";
    public static final String TYPE_BYLINE = "byline";
    public static final String TYPE_PARAGRAPH = "p";
    public static final String TYPE_KICKER = "kicker";
    public static final String TYPE_LEAD = "lead";
    public static final String TYPE_PH = "ph";

    // Should be sorted in ascending alphabetical order to allow binary search.
    private static final String[] ALL_TYPES = {
            TYPE_BYLINE, TYPE_HEADER_1, TYPE_HEADER_2, TYPE_INTRO,
            TYPE_KICKER, TYPE_LEAD, TYPE_PARAGRAPH, TYPE_PH};

    @NonNull @Type private final String mType;
    @NonNull private final String mContent;

    public ContentElement(@NonNull @Type String type, @NonNull String content) {
        mType = type;
        mContent = content;
    }

    @NonNull
    public String getContent() {
        return mContent;
    }

    @NonNull
    public String getType() {
        return mType;
    }

    @StringDef({TYPE_HEADER_1, TYPE_HEADER_2, TYPE_INTRO, TYPE_BYLINE, TYPE_PARAGRAPH})
    public @interface Type {
    }

    @NonNull
    @ContentElement.Type
    public static String verifyType(@NonNull String type) {

        int matchIndex = Arrays.binarySearch(ALL_TYPES, type);
        boolean wasFound = matchIndex >= 0;
        if (!wasFound) {
            throw new IllegalArgumentException("Unknown content type: " + type);
        }

        return type;
    }
}
