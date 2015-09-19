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

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/**
 * Generic implementation for cupboard to persist database fields of type <code>T</code> as Json.
 *
 * @param <T> Field type that will be persisted by cupboard using Gson.
 * @see <a href="https://gist.github.com/hvisser/7c10d433bbf01306f158">Custom Gson converter for fields</a>
 */
public class GsonFieldConverter<T> implements FieldConverter<T> {

    private Gson mGson;
    private Type mType;

    /**
     * Constructs a new <code>GsonFieldConverter</code> using for specified type and with given Gson setup.
     *
     * @param gson The json (de)serializer.
     * @param type The field type to convert when persisting with cupboard.
     */
    public GsonFieldConverter(Gson gson, Type type) {
        mGson = gson;
        mType = type;
    }

    /**
     * Uses Gson to convert cursor contents to a field of type <code>T</code>.
     *
     * @param cursor      Contains the json for the field of type <code>T</code>.
     * @param columnIndex Specifies where in the cursor to find the field contents.
     * @return Instance of <code>T</code> deserialized by Gson.
     */
    @Override
    public T fromCursorValue(Cursor cursor, int columnIndex) {
        return mGson.fromJson(cursor.getString(columnIndex), mType);
    }

    /**
     * Stores the Gson representation of <code>value</code> in a content values object.
     *
     * @param value  The instance to convert.
     * @param key    the
     * @param values The content values object to store the
     */
    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        values.put(key, mGson.toJson(value));
    }

    /**
     * The JSON is stored as the <code>TEXT</code> column type.
     *
     * @return {@link EntityConverter.ColumnType#TEXT EntityConverter.ColumnType.TEXT}
     */
    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }
}
