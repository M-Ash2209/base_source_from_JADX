package p005de.appplant.cordova.plugin.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.p000v4.app.NotificationCompat;
import android.support.p000v4.internal.view.SupportMenu;
import android.support.p000v4.media.app.NotificationCompat;
import android.support.p000v4.media.session.MediaSessionCompat;
import com.adobe.phonegap.push.PushConstants;
import java.util.List;
import java.util.Random;
import p005de.appplant.cordova.plugin.notification.action.Action;

/* renamed from: de.appplant.cordova.plugin.notification.Builder */
public final class Builder {
    private Class<?> clearReceiver;
    private Class<?> clickActivity;
    private final Context context;
    private Bundle extras;
    private final Options options;
    private final Random random = new Random();

    public Builder(Options options2) {
        this.context = options2.getContext();
        this.options = options2;
    }

    public Builder setClearReceiver(Class<?> cls) {
        this.clearReceiver = cls;
        return this;
    }

    public Builder setClickActivity(Class<?> cls) {
        this.clickActivity = cls;
        return this;
    }

    public Builder setExtras(Bundle bundle) {
        this.extras = bundle;
        return this;
    }

    public Notification build() {
        if (this.options.isSilent()) {
            return new Notification(this.context, this.options);
        }
        Uri sound = this.options.getSound();
        Bundle bundle = new Bundle();
        bundle.putInt(Notification.EXTRA_ID, this.options.getId().intValue());
        bundle.putString("NOTIFICATION_SOUND", sound.toString());
        NotificationCompat.Builder lights = findOrCreateBuilder().setDefaults(this.options.getDefaults()).setExtras(bundle).setOnlyAlertOnce(false).setChannelId(this.options.getChannel()).setContentTitle(this.options.getTitle()).setContentText(this.options.getText()).setTicker(this.options.getText()).setNumber(this.options.getNumber()).setAutoCancel(this.options.isAutoClear().booleanValue()).setOngoing(this.options.isSticky().booleanValue()).setColor(this.options.getColor()).setVisibility(this.options.getVisibility()).setPriority(this.options.getPrio()).setShowWhen(this.options.showClock()).setUsesChronometer(this.options.showChronometer()).setGroup(this.options.getGroup()).setGroupSummary(this.options.getGroupSummary()).setTimeoutAfter(this.options.getTimeout()).setLights(this.options.getLedColor(), this.options.getLedOn(), this.options.getLedOff());
        if (sound != Uri.EMPTY && !isUpdate()) {
            lights.setSound(sound);
        }
        if (this.options.isWithProgressBar()) {
            lights.setProgress(this.options.getProgressMaxValue(), this.options.getProgressValue(), this.options.isIndeterminateProgress());
        }
        if (this.options.hasLargeIcon()) {
            lights.setSmallIcon(this.options.getSmallIcon());
            Bitmap largeIcon = this.options.getLargeIcon();
            if (this.options.getLargeIconType().equals(PushConstants.IMAGE_TYPE_CIRCLE)) {
                largeIcon = getCircleBitmap(largeIcon);
            }
            lights.setLargeIcon(largeIcon);
        } else {
            lights.setSmallIcon(this.options.getSmallIcon());
        }
        applyStyle(lights);
        applyActions(lights);
        applyDeleteReceiver(lights);
        applyContentReceiver(lights);
        return new Notification(this.context, this.options, lights);
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(SupportMenu.CATEGORY_MASK);
        float width = (float) (bitmap.getWidth() / 2);
        float height = (float) (bitmap.getHeight() / 2);
        canvas.drawCircle(width, height, width < height ? width : height, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return createBitmap;
    }

    private void applyStyle(NotificationCompat.Builder builder) {
        NotificationCompat.MessagingStyle.Message[] messages = this.options.getMessages();
        String summary = this.options.getSummary();
        if (messages != null) {
            applyMessagingStyle(builder, messages);
            return;
        }
        MediaSessionCompat.Token mediaSessionToken = this.options.getMediaSessionToken();
        if (mediaSessionToken != null) {
            applyMediaStyle(builder, mediaSessionToken);
            return;
        }
        List<Bitmap> attachments = this.options.getAttachments();
        if (attachments.size() > 0) {
            applyBigPictureStyle(builder, attachments);
            return;
        }
        String text = this.options.getText();
        if (text != null && text.contains("\n")) {
            applyInboxStyle(builder);
        } else if (text == null) {
        } else {
            if (summary != null || text.length() >= 45) {
                applyBigTextStyle(builder);
            }
        }
    }

    private void applyMessagingStyle(NotificationCompat.Builder builder, NotificationCompat.MessagingStyle.Message[] messageArr) {
        NotificationCompat.MessagingStyle messagingStyle;
        String title = this.options.getTitle();
        Notification findActiveNotification = findActiveNotification(this.options.getId());
        if (findActiveNotification != null) {
            messagingStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(findActiveNotification);
        } else {
            messagingStyle = new NotificationCompat.MessagingStyle("");
        }
        for (NotificationCompat.MessagingStyle.Message addMessage : messageArr) {
            messagingStyle.addMessage(addMessage);
        }
        Integer valueOf = Integer.valueOf(messagingStyle.getMessages().size());
        if (valueOf.intValue() > 1) {
            String str = "(" + valueOf + ")";
            if (this.options.getTitleCount() != null) {
                str = this.options.getTitleCount().replace("%n%", "" + valueOf);
            }
            if (!str.trim().equals("")) {
                title = title + " " + str;
            }
        }
        messagingStyle.setConversationTitle(title);
        builder.setStyle(messagingStyle);
    }

    private void applyBigPictureStyle(NotificationCompat.Builder builder, List<Bitmap> list) {
        String summary = this.options.getSummary();
        String text = this.options.getText();
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle(builder);
        if (summary == null) {
            summary = text;
        }
        builder.setStyle(bigPictureStyle.setSummaryText(summary).bigPicture(list.get(0)));
    }

    private void applyInboxStyle(NotificationCompat.Builder builder) {
        String text = this.options.getText();
        NotificationCompat.InboxStyle summaryText = new NotificationCompat.InboxStyle(builder).setSummaryText(this.options.getSummary());
        for (String addLine : text.split("\n")) {
            summaryText.addLine(addLine);
        }
        builder.setStyle(summaryText);
    }

    private void applyBigTextStyle(NotificationCompat.Builder builder) {
        builder.setStyle(new NotificationCompat.BigTextStyle(builder).setSummaryText(this.options.getSummary()).bigText(this.options.getText()));
    }

    private void applyMediaStyle(NotificationCompat.Builder builder, MediaSessionCompat.Token token) {
        builder.setStyle(new NotificationCompat.MediaStyle(builder).setMediaSession(token).setShowActionsInCompactView(1));
    }

    private void applyDeleteReceiver(NotificationCompat.Builder builder) {
        Class<?> cls = this.clearReceiver;
        if (cls != null) {
            Intent putExtra = new Intent(this.context, cls).setAction(this.options.getIdentifier()).putExtra(Notification.EXTRA_ID, this.options.getId());
            Bundle bundle = this.extras;
            if (bundle != null) {
                putExtra.putExtras(bundle);
            }
            builder.setDeleteIntent(PendingIntent.getBroadcast(this.context, this.random.nextInt(), putExtra, 134217728));
        }
    }

    private void applyContentReceiver(NotificationCompat.Builder builder) {
        Class<?> cls = this.clickActivity;
        if (cls != null) {
            Intent flags = new Intent(this.context, cls).putExtra(Notification.EXTRA_ID, this.options.getId()).putExtra(Action.EXTRA_ID, Action.CLICK_ACTION_ID).putExtra(Options.EXTRA_LAUNCH, this.options.isLaunchingApp()).setFlags(1073741824);
            Bundle bundle = this.extras;
            if (bundle != null) {
                flags.putExtras(bundle);
            }
            builder.setContentIntent(PendingIntent.getService(this.context, this.random.nextInt(), flags, 134217728));
        }
    }

    private void applyActions(NotificationCompat.Builder builder) {
        Action[] actions = this.options.getActions();
        if (actions != null && actions.length != 0) {
            for (Action action : actions) {
                NotificationCompat.Action.Builder builder2 = new NotificationCompat.Action.Builder(action.getIcon(), action.getTitle(), getPendingIntentForAction(action));
                if (action.isWithInput()) {
                    builder2.addRemoteInput(action.getInput());
                }
                builder.addAction(builder2.build());
            }
        }
    }

    private PendingIntent getPendingIntentForAction(Action action) {
        Intent flags = new Intent(this.context, this.clickActivity).putExtra(Notification.EXTRA_ID, this.options.getId()).putExtra(Action.EXTRA_ID, action.getId()).putExtra(Options.EXTRA_LAUNCH, action.isLaunchingApp()).setFlags(1073741824);
        Bundle bundle = this.extras;
        if (bundle != null) {
            flags.putExtras(bundle);
        }
        return PendingIntent.getService(this.context, this.random.nextInt(), flags, 134217728);
    }

    private boolean isUpdate() {
        Bundle bundle = this.extras;
        return bundle != null && bundle.getBoolean(Notification.EXTRA_UPDATE, false);
    }

    private NotificationCompat.Builder findOrCreateBuilder() {
        NotificationCompat.Builder cachedBuilder = Notification.getCachedBuilder(this.options.getId().intValue());
        return cachedBuilder == null ? new NotificationCompat.Builder(this.context, this.options.getChannel()) : cachedBuilder;
    }

    private Notification findActiveNotification(Integer num) {
        if (Build.VERSION.SDK_INT < 23) {
            return null;
        }
        StatusBarNotification[] activeNotifications = ((NotificationManager) this.context.getSystemService("notification")).getActiveNotifications();
        for (int i = 0; i < activeNotifications.length; i++) {
            if (activeNotifications[i].getId() == num.intValue()) {
                return activeNotifications[i].getNotification();
            }
        }
        return null;
    }
}
