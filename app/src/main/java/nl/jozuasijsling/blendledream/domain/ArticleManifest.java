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
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class ArticleManifest {

    @NonNull private String mId;
    @NonNull private Date mDate;
    @NonNull private String mProviderId;
    @NonNull private ArticleBody mBody;
    @NonNull private List<ImageInfo> mImages;

    public ArticleManifest(@NonNull String id,
                           @NonNull Date date,
                           @NonNull String providerId,
                           @NonNull ArticleBody body,
                           @NonNull List<ImageInfo> images) {
        mId = id;
        mDate = date;
        mProviderId = providerId;
        mBody = body;
        mImages = images;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public Date getDate() {
        return mDate;
    }

    @NonNull
    public String getProviderId() {
        return mProviderId;
    }

    @NonNull
    public ArticleBody getBody() {
        return mBody;
    }

    @NonNull
    public List<ImageInfo> getImages() {
        return mImages;
    }


    public static class Builder {
        @Nullable private String mId;
        @Nullable private Date mDate;
        @Nullable private String mProviderId;
        @Nullable private List<ImageInfo> mImages;
        @Nullable private List<ContentElement> mContentElements;

        @NonNull
        public Builder setId(@Nullable String id) {
            mId = id;
            return this;
        }

        @NonNull
        public Builder setDate(@Nullable Date date) {
            mDate = date;
            return this;
        }

        @NonNull
        public Builder setProviderId(@Nullable String providerId) {
            mProviderId = providerId;
            return this;
        }

        @NonNull
        public Builder setContentElements(@Nullable List<ContentElement> contentElements) {
            mContentElements = contentElements;
            return this;
        }

        @NonNull
        public Builder setImages(@Nullable List<ImageInfo> images) {
            mImages = images;
            return this;
        }

        public ArticleManifest build() {

            if (mId == null) throw new NullPointerException("id must not be null");
            if (mDate == null) throw new NullPointerException("date must not be null");
            if (mProviderId == null) throw new NullPointerException("providerId must not be null");
            if (mImages == null) throw new NullPointerException("images must not be null");
            if (mContentElements == null) throw new NullPointerException("contentElements most not be null");

            ArticleBody body = new ArticleBody(mContentElements);
            return new ArticleManifest(mId, mDate, mProviderId, body, mImages);
        }
    }

    public static class ImageInfo {

        @NonNull private final ImageResource mSmall;
        @NonNull private final ImageResource mMedium;
        @NonNull private final ImageResource mLarge;
        @NonNull private final ImageResource mOriginal;
        private final boolean mFeatured;
        @Nullable private final String mCaption;
        @Nullable private final String mCredit;

        public ImageInfo(@NonNull ImageResource small,
                         @NonNull ImageResource medium,
                         @NonNull ImageResource large,
                         @NonNull ImageResource original,
                         boolean featured,
                         @Nullable String caption,
                         @Nullable String credit) {
            mSmall = small;
            mMedium = medium;
            mLarge = large;
            mOriginal = original;
            mFeatured = featured;
            mCaption = caption;
            mCredit = credit;
        }

        @NonNull
        public ImageResource getSmall() {
            return mSmall;
        }

        @NonNull
        public ImageResource getMedium() {
            return mMedium;
        }

        @NonNull
        public ImageResource getLarge() {
            return mLarge;
        }

        @NonNull
        public ImageResource getOriginal() {
            return mOriginal;
        }

        public boolean isFeatured() {
            return mFeatured;
        }

        @Nullable
        public String getCaption() {
            return mCaption;
        }

        @Nullable
        public String getCredit() {
            return mCredit;
        }
    }

}
