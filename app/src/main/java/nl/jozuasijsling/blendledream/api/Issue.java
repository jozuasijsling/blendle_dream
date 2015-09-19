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
import nl.jozuasijsling.blendledream.api.references.PageRef;

/**
 *
 */
public class Issue {

    private int formatVersion;
    private String[] representations;
    private Links links;
    private String id;
    private IdRef provider;
    private Date initialPublicationTime;
    private Date date;
    private String[] items;
    private Manifest _embedded;

    public int getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String[] getRepresentations() {
        return representations;
    }

    public void setRepresentations(String[] representations) {
        this.representations = representations;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IdRef getProvider() {
        return provider;
    }

    public void setProvider(IdRef provider) {
        this.provider = provider;
    }

    public Date getInitialPublicationTime() {
        return initialPublicationTime;
    }

    public void setInitialPublicationTime(Date initialPublicationTime) {
        this.initialPublicationTime = initialPublicationTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public Manifest getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(Manifest embedded) {
        this._embedded = embedded;
    }

    @Override
    public String toString() {
        return "{" +
                "format_version=" + formatVersion +
                ", representations=" + Arrays.toString(representations) +
                ", links=" + links +
                ", id='" + id + '\'' +
                ", provider=" + provider +
                ", initial_publication_time=" + initialPublicationTime +
                ", date=" + date +
                ", items=" + Arrays.toString(items) +
                ", _embedded=" + _embedded +
                '}';
    }

    public class Links {
        private PageRef self;
        private PageRef cover;
        private PageRef month;
        private PageRef year;
        private PageRef years;
        private PageRef pages;
        private PageRef page_preview;

        public PageRef getSelf() {
            return self;
        }

        public void setSelf(PageRef self) {
            this.self = self;
        }

        public PageRef getCover() {
            return cover;
        }

        public void setCover(PageRef cover) {
            this.cover = cover;
        }

        public PageRef getMonth() {
            return month;
        }

        public void setMonth(PageRef month) {
            this.month = month;
        }

        public PageRef getYear() {
            return year;
        }

        public void setYear(PageRef year) {
            this.year = year;
        }

        public PageRef getYears() {
            return years;
        }

        public void setYears(PageRef years) {
            this.years = years;
        }

        public PageRef getPages() {
            return pages;
        }

        public void setPages(PageRef pages) {
            this.pages = pages;
        }

        public PageRef getPagePreview() {
            return page_preview;
        }

        public void setPagePreview(PageRef pagePreview) {
            this.page_preview = pagePreview;
        }

        @Override
        public String toString() {
            return "{" +
                    "self=" + self +
                    ", cover=" + cover +
                    ", month=" + month +
                    ", year=" + year +
                    ", years=" + years +
                    ", pages=" + pages +
                    ", page_preview=" + page_preview +
                    '}';
        }
    }
}
