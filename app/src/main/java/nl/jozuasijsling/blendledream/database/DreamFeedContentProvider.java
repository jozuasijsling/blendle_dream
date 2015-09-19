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

package nl.jozuasijsling.blendledream.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import nl.jozuasijsling.blendledream.domain.BlendleIssue;
import nl.qbusict.cupboard.DatabaseCompartment;
import timber.log.Timber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Regulates access to the dream feed database.
 */
public class DreamFeedContentProvider extends ContentProvider {

    @Nullable private DreamFeedSQLiteOpenHelper mDatabaseHelper;

    /**
     * Called by Android when the provider is created. Normally we setup the database here, but the
     * user may notice lag so instead we defer database setup until the moment the database is used.
     *
     * @return Value {@code true} since we handled this event.
     */
    @Override
    public boolean onCreate() {
        // do nothing: let the database be created lazily to speed up start up time
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (!uri.equals(DreamFeedContentUris.ISSUES_URI)) {
            throw new IllegalArgumentException("Queries are only allowed with the ISSUES uri.");
        }

        SQLiteDatabase database = getDatabaseHelper().getReadableDatabase();

        Cursor cursor = cupboard().withDatabase(database)
                .query(BlendleIssue.class)
                .withProjection(projection)
                .withSelection(selection, selectionArgs)
                .orderBy(sortOrder)
                .getCursor();
        cursor.setNotificationUri(getContext().getContentResolver(), DreamFeedContentUris.ISSUES_URI);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // We don't need to support this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        throw new UnsupportedOperationException("Single inserts are not supported, use bulkInsert() instead.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Deletes are not supported.");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Updates are not supported.");
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {

        if (!uri.equals(DreamFeedContentUris.ISSUES_REPLACE_URI)) {
            // We interpret bulk inserts as full table replace operations.
            throw new IllegalArgumentException("Bulk insert is only allowed with the REPLACE uri.");
        }

        SQLiteDatabase database = getDatabaseHelper().getWritableDatabase();
        DatabaseCompartment databaseCupboard = cupboard().withDatabase(database);
        database.beginTransaction();

        // First: delete all previous rows.
        int rowsDeleted = databaseCupboard.delete(BlendleIssue.class, null);

        try {

            // Then: bulk insert the new rows.
            for (int i = 0; i < values.length; i++) {
                ContentValues row = values[i];
                databaseCupboard.put(BlendleIssue.class, row);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        Timber.d("Replaced all " + rowsDeleted + " issues with " + values.length + " new issues.");
        getContext().getContentResolver().notifyChange(DreamFeedContentUris.ISSUES_URI, null);

        return values.length;
    }

    @NonNull
    private DreamFeedSQLiteOpenHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DreamFeedSQLiteOpenHelper(getContext());
        }
        return mDatabaseHelper;
    }
}
