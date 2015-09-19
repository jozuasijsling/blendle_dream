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

package nl.jozuasijsling.blendledream.mapping;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.jozuasijsling.blendledream.api.Issue;
import nl.jozuasijsling.blendledream.api.Manifest;
import nl.jozuasijsling.blendledream.api.references.ImageRef;
import nl.jozuasijsling.blendledream.domain.ArticleManifest;
import nl.jozuasijsling.blendledream.domain.BlendleIssue;
import nl.jozuasijsling.blendledream.domain.ContentElement;
import nl.jozuasijsling.blendledream.domain.ImageResource;

import static nl.jozuasijsling.blendledream.domain.ArticleManifest.Builder;

/**
 * Handles mapping the API DTOs to their domain counterparts.
 */
public class ApiObjectMapper {

    @NonNull
    public List<ArticleManifest.ImageInfo> mapToImageInfoList(@NonNull Manifest.ImageInfo[] images) {
        List<ArticleManifest.ImageInfo> mappedImages = new ArrayList<>(images.length);
        for (int i = 0; i < images.length; i++) {
            Manifest.ImageInfo imageInfo = images[i];
            ArticleManifest.ImageInfo mappedImageInfo = mapToImageInfo(imageInfo);
            mappedImages.add(mappedImageInfo);
        }

        return mappedImages;
    }

    @NonNull
    public ArticleManifest.ImageInfo mapToImageInfo(@NonNull Manifest.ImageInfo imageInfo) {

        Manifest.ImageLinks links = imageInfo.getLinks();
        ImageResource small = mapToImageResource(links.getSmall());
        ImageResource medium = mapToImageResource(links.getMedium());
        ImageResource large = mapToImageResource(links.getLarge());
        ImageResource original = mapToImageResource(links.getOriginal());

        boolean featured = imageInfo.isFeatured();
        String caption = imageInfo.getCaption();
        String credit = imageInfo.getCredit();

        return new ArticleManifest.ImageInfo(small, medium, large,
                original, featured, caption, credit);
    }

    @NonNull
    public ImageResource mapToImageResource(@NonNull ImageRef imageResource) {
        String href = imageResource.getHref();
        int width = imageResource.getWidth();
        int height = imageResource.getHeight();

        if (href == null) throw new NullPointerException("href must not be null.");

        return new ImageResource(href, width, height);
    }

    @NonNull
    public ContentElement mapToContentElement(@NonNull Manifest.ContentDescription element) {
        String type = ContentElement.verifyType(element.getType());
        String content = element.getContent();
        return new ContentElement(type, content);
    }

    @NonNull
    public List<ContentElement> mapToContentElements(@NonNull Manifest.ContentDescription[] elements) {

        List<ContentElement> mappedElements = new ArrayList<>(elements.length);
        for (int i = 0; i < elements.length; i++) {
            Manifest.ContentDescription element = elements[i];
            ContentElement mappedElement = mapToContentElement(element);
            mappedElements.add(mappedElement);
        }

        return mappedElements;
    }

    @NonNull
    public ArticleManifest mapToArticleManifest(@NonNull Manifest manifest) {


        String id = manifest.getId();
        Date date = manifest.getDate();
        String providerId = manifest.getProvider().getId();

        if (id == null) throw new NullPointerException("id must not be null");
        if (date == null) throw new NullPointerException("date must not be null");
        if (providerId == null) throw new NullPointerException("provider_id must not be null");

        return new Builder()
                .setId(id)
                .setDate(date)
                .setProviderId(providerId)
                .setContentElements(mapToContentElements(manifest.getBody()))
                .setImages(mapToImageInfoList(manifest.getImages()))
                .build();
    }

    @NonNull
    public BlendleIssue mapToBlendleIssue(@NonNull Issue issue) {

        Manifest embeddedManifest = issue.getEmbedded().getManifest();
        ArticleManifest mappedManifest = mapToArticleManifest(embeddedManifest);

        String id = issue.getId();
        Date initialPublicationTime = issue.getInitialPublicationTime();
        Date date = issue.getDate();

        if (id == null) throw new NullPointerException("id must not be null");
        if (initialPublicationTime == null) throw new NullPointerException("initial_publication_time must not be null");
        if (date == null) throw new NullPointerException("date must not be null");

        return new BlendleIssue(id, initialPublicationTime, date, mappedManifest);
    }

    @NonNull
    public List<BlendleIssue> mapToBlendleIssues(@NonNull Issue... issues) {

        List<BlendleIssue> mappedIssues = new ArrayList<>(issues.length);
        for (int i = 0; i < issues.length; i++) {
            Issue issue = issues[i];
            BlendleIssue mappedIssue = mapToBlendleIssue(issue);
            mappedIssues.add(mappedIssue);
        }

        return mappedIssues;
    }

}
