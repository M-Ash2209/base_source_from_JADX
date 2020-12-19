package p007me.leolin.shortcutbadger.impl;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import java.util.Arrays;
import java.util.List;
import p007me.leolin.shortcutbadger.Badger;
import p007me.leolin.shortcutbadger.ShortcutBadgeException;

@Deprecated
/* renamed from: me.leolin.shortcutbadger.impl.XiaomiHomeBadger */
public class XiaomiHomeBadger implements Badger {
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
    private ResolveInfo resolveInfo;

    /* JADX WARNING: Can't wrap try/catch for region: R(2:8|9) */
    /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
        r1.set(r0, java.lang.Integer.valueOf(r7));
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x0029 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void executeBadge(android.content.Context r5, android.content.ComponentName r6, int r7) throws p007me.leolin.shortcutbadger.ShortcutBadgeException {
        /*
            r4 = this;
            java.lang.String r0 = "android.app.MiuiNotification"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ Exception -> 0x0031 }
            java.lang.Object r0 = r0.newInstance()     // Catch:{ Exception -> 0x0031 }
            java.lang.Class r1 = r0.getClass()     // Catch:{ Exception -> 0x0031 }
            java.lang.String r2 = "messageCount"
            java.lang.reflect.Field r1 = r1.getDeclaredField(r2)     // Catch:{ Exception -> 0x0031 }
            r2 = 1
            r1.setAccessible(r2)     // Catch:{ Exception -> 0x0031 }
            if (r7 != 0) goto L_0x001d
            java.lang.String r2 = ""
            goto L_0x0021
        L_0x001d:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0029 }
        L_0x0021:
            java.lang.String r2 = java.lang.String.valueOf(r2)     // Catch:{ Exception -> 0x0029 }
            r1.set(r0, r2)     // Catch:{ Exception -> 0x0029 }
            goto L_0x0071
        L_0x0029:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0031 }
            r1.set(r0, r2)     // Catch:{ Exception -> 0x0031 }
            goto L_0x0071
        L_0x0031:
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.intent.action.APPLICATION_MESSAGE_UPDATE"
            r0.<init>(r1)
            java.lang.String r1 = "android.intent.extra.update_application_component_name"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r6.getPackageName()
            r2.append(r3)
            java.lang.String r3 = "/"
            r2.append(r3)
            java.lang.String r6 = r6.getClassName()
            r2.append(r6)
            java.lang.String r6 = r2.toString()
            r0.putExtra(r1, r6)
            java.lang.String r6 = "android.intent.extra.update_application_message_text"
            if (r7 != 0) goto L_0x0061
            java.lang.String r1 = ""
            goto L_0x0065
        L_0x0061:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r7)
        L_0x0065:
            java.lang.String r1 = java.lang.String.valueOf(r1)
            r0.putExtra(r6, r1)
            p007me.leolin.shortcutbadger.util.BroadcastHelper.sendIntentExplicitly(r5, r0)     // Catch:{ ShortcutBadgeException -> 0x0070 }
            goto L_0x0071
        L_0x0070:
        L_0x0071:
            java.lang.String r6 = android.os.Build.MANUFACTURER
            java.lang.String r0 = "Xiaomi"
            boolean r6 = r6.equalsIgnoreCase(r0)
            if (r6 == 0) goto L_0x007e
            r4.tryNewMiuiBadge(r5, r7)
        L_0x007e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p007me.leolin.shortcutbadger.impl.XiaomiHomeBadger.executeBadge(android.content.Context, android.content.ComponentName, int):void");
    }

    @TargetApi(16)
    private void tryNewMiuiBadge(Context context, int i) throws ShortcutBadgeException {
        if (this.resolveInfo == null) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            this.resolveInfo = context.getPackageManager().resolveActivity(intent, 65536);
        }
        if (this.resolveInfo != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            Notification build = new Notification.Builder(context).setContentTitle("").setContentText("").setSmallIcon(this.resolveInfo.getIconResource()).build();
            try {
                Object obj = build.getClass().getDeclaredField("extraNotification").get(build);
                obj.getClass().getDeclaredMethod("setMessageCount", new Class[]{Integer.TYPE}).invoke(obj, new Object[]{Integer.valueOf(i)});
                notificationManager.notify(0, build);
            } catch (Exception e) {
                throw new ShortcutBadgeException("not able to set badge", e);
            }
        }
    }

    public List<String> getSupportLaunchers() {
        return Arrays.asList(new String[]{"com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2", "com.i.miui.launcher"});
    }
}
