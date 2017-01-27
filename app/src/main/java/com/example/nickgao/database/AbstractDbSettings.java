package com.example.nickgao.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import org.apache.commons.lang.StringUtils;
import static com.example.nickgao.database.RCMDataStore.GeneralSettingsTable.KEY;
import static com.example.nickgao.database.RCMDataStore.GeneralSettingsTable.VALUE;
/**
 * Created by Antonenko Vyacheslav.
 */
public abstract class AbstractDbSettings {

    private static final boolean DEBUG = false;

    protected final String TAG = "[RC]" + getClass().getSimpleName();

    protected abstract Context getContext();

    protected abstract Uri getUri();

    protected byte[] getBytes(final String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }

        final String value = getString(key, "");
        if (StringUtils.isBlank(value)) {
            return new byte[0];
        }

        return Base64.decode(value, Base64.DEFAULT);
    }

    protected void setBytes(final String key, final byte[] value) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }

        if (value == null) {
            setString(key, "");
        } else {
            setString(key, Base64.encodeToString(value, Base64.DEFAULT));
        }
    }

    protected boolean getBoolean(final String key, final boolean def) {
        final String result = getString(key, null);
        return TextUtils.isEmpty(result) ? def : Boolean.parseBoolean(result);
    }

    protected void setBoolean(final String key, final boolean value) {
        setString(key, Boolean.toString(value));
    }

    private int updateValue(final String key, final Uri uri, final ContentValues values) {
        return getContext().getContentResolver().update(uri, values,
                KEY + "=?",
                new String[] { key });
    }

    protected void setLong(final String key, final long value) {
        setString(key, Long.toString(value));
    }

    protected long getLong(final String key, final long def) {
        final String result = getString(key, null);
        return TextUtils.isEmpty(result) ? def : Long.valueOf(result);
    }

    protected void setInt(final String key, final int value) {
        setString(key, Integer.toString(value));
    }

    protected int getInt(final String key, final int def) {
        final String result = getString(key, null);
        return TextUtils.isEmpty(result) ? def : Integer.valueOf(result);
    }

    public String getString(final String key, final String def) {
        final String result = simpleQuery(getContext(), getUri(), VALUE, KEY + " = '" + key + "'");
        return TextUtils.isEmpty(result) ? def : result;
    }

    public void setString(final String key, final String value) {
        final ContentValues values = new ContentValues();
        values.put(KEY, key);
        values.put(VALUE, value);
        final Uri uri = getUri();
        final String mailboxId = uri.getQueryParameter(RCMDataStore.RCMColumns.MAILBOX_ID);
        if (!TextUtils.isEmpty(mailboxId)) {
            values.put(RCMDataStore.RCMColumns.MAILBOX_ID, mailboxId);
        }
        if (updateValue(key, uri, values) <= 0) {
            getContext().getContentResolver().insert(uri, values);
        }
    }

    public boolean removeRow(final String key) {
        return getContext().getContentResolver().delete(
                getUri(),
                RCMDataStore.GeneralSettingsTable.KEY + " = ?",
                new String[] { key }) > 0;
    }

    private String simpleQuery(final Context context, final Uri uri, final String column, final String selection) {
        if (context == null) {
            return "";
        }

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, new String[] { column }, selection, null, null);
            if (cursor == null) {
                return "";
            }

            if (!cursor.moveToFirst()) {
                return "";
            }

            String result = cursor.getString(0);
            if (result == null) {
                result = "";
            }
            return result;
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
