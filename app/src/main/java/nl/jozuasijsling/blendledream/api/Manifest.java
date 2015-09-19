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

package nl.jozuasijsling.blendledream.api;

import java.util.Arrays;
import java.util.Date;

import nl.jozuasijsling.blendledream.api.references.IdRef;
import nl.jozuasijsling.blendledream.api.references.ImageRef;
import nl.jozuasijsling.blendledream.api.references.PageRef;

/**
 */
public class Manifest {

    private int formatVersion;
    private String id;
    private Date date;
    private IdRef provider;
    private ContentDescription[] body;
    private ImageInfo[] images;
    private Length length;
    private int itemIndex;
    private IdRef issue;
    private Links _links;

    public int getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public IdRef getProvider() {
        return provider;
    }

    public void setProvider(IdRef provider) {
        this.provider = provider;
    }

    public ContentDescription[] getBody() {
        return body;
    }

    public void setBody(ContentDescription[] body) {
        this.body = body;
    }

    public ImageInfo[] getImages() {
        return images;
    }

    public void setImages(ImageInfo[] images) {
        this.images = images;
    }

    public Length getLength() {
        return length;
    }

    public void setLength(Length length) {
        this.length = length;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public IdRef getIssue() {
        return issue;
    }

    public void setIssue(IdRef issue) {
        this.issue = issue;
    }

    public Links getLinks() {
        return _links;
    }

    public void setLinks(Links links) {
        this._links = links;
    }

    @Override
    public String toString() {
        return "{" +
                "format_version=" + formatVersion +
                ", id='" + id + '\'' +
                ", date=" + date +
                ", provider=" + provider +
                ", body=" + Arrays.toString(body) +
                ", images=" + Arrays.toString(images) +
                ", length=" + length +
                ", item_index=" + itemIndex +
                ", issue=" + issue +
                ", _links=" + _links +
                '}';
    }

    public static class ContentDescription {
        private String type;
        private String content;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "{" +
                    "type='" + type + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public static class ImageInfo {
        private ImageLinks _links;
        private String caption;
        private String credit;
        private boolean featured;

        public ImageLinks getLinks() {
            return _links;
        }

        public void setLinks(ImageLinks links) {
            this._links = links;
        }

        public boolean isFeatured() {
            return featured;
        }

        public void setFeatured(boolean featured) {
            this.featured = featured;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getCredit() {
            return credit;
        }

        public void setCredit(String credit) {
            this.credit = credit;
        }

        @Override
        public String toString() {
            return "{" +
                    "_links=" + _links +
                    ", caption='" + caption + '\'' +
                    ", credit='" + credit + '\'' +
                    ", featured=" + featured +
                    '}';
        }
    }

    public static class Length {
        private int words;

        public int getWords() {
            return words;
        }

        public void setWords(int words) {
            this.words = words;
        }

        @Override
        public String toString() {
            return "{words=" + words + '}';
        }
    }

    public static class Links {
        private PageRef self;

        public PageRef getSelf() {
            return self;
        }

        public void setSelf(PageRef self) {
            this.self = self;
        }

        @Override
        public String toString() {
            return "{self=" + self + '}';
        }
    }

    public static class ImageLinks {
        private ImageRef small;
        private ImageRef medium;
        private ImageRef large;
        private ImageRef original;

        public ImageRef getSmall() {
            return small;
        }

        public void setSmall(ImageRef small) {
            this.small = small;
        }

        public ImageRef getMedium() {
            return medium;
        }

        public void setMedium(ImageRef medium) {
            this.medium = medium;
        }

        public ImageRef getLarge() {
            return large;
        }

        public void setLarge(ImageRef large) {
            this.large = large;
        }

        public ImageRef getOriginal() {
            return original;
        }

        public void setOriginal(ImageRef original) {
            this.original = original;
        }

        @Override
        public String toString() {
            return "{" +
                    "small=" + small +
                    ", medium=" + medium +
                    ", large=" + large +
                    ", original=" + original +
                    '}';
        }
    }
}
