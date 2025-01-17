package p007me.leolin.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import java.util.Arrays;
import java.util.List;
import p007me.leolin.shortcutbadger.Badger;
import p007me.leolin.shortcutbadger.ShortcutBadgeException;
import p007me.leolin.shortcutbadger.util.CloseHelper;

/* renamed from: me.leolin.shortcutbadger.impl.SamsungHomeBadger */
public class SamsungHomeBadger implements Badger {
    private static final String[] CONTENT_PROJECTION = {"_id", "class"};
    private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
    private DefaultBadger defaultBadger;

    public SamsungHomeBadger() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.defaultBadger = new DefaultBadger();
        }
    }

    public void executeBadge(Context context, ComponentName componentName, int i) throws ShortcutBadgeException {
        DefaultBadger defaultBadger2 = this.defaultBadger;
        if (defaultBadger2 == null || !defaultBadger2.isSupported(context)) {
            Uri parse = Uri.parse(CONTENT_URI);
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(parse, CONTENT_PROJECTION, "package=?", new String[]{componentName.getPackageName()}, (String) null);
                if (cursor != null) {
                    String className = componentName.getClassName();
                    boolean z = false;
                    while (cursor.moveToNext()) {
                        int i2 = cursor.getInt(0);
                        contentResolver.update(parse, getContentValues(componentName, i, false), "_id=?", new String[]{String.valueOf(i2)});
                        if (className.equals(cursor.getString(cursor.getColumnIndex("class")))) {
                            z = true;
                        }
                    }
                    if (!z) {
                        contentResolver.insert(parse, getContentValues(componentName, i, true));
                    }
                }
            } finally {
                CloseHelper.close(cursor);
            }
        } else {
            this.defaultBadger.executeBadge(context, componentName, i);
        }
    }

    private ContentValues getContentValues(ComponentName componentName, int i, boolean z) {
        ContentValues contentValues = new ContentValues();
        if (z) {
            contentValues.put("package", componentName.getPackageName());
            contentValues.put("class", componentName.getClassName());
        }
        contentValues.put("badgecount", Integer.valueOf(i));
        return contentValues;
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.sec.android.app.launcher", "com.sec.android.app.twlauncher"});
    }
}
