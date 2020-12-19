package p005de.appplant.cordova.plugin.notification.trigger;

import java.util.Calendar;
import java.util.Date;

/* renamed from: de.appplant.cordova.plugin.notification.trigger.DateTrigger */
public abstract class DateTrigger {
    private int occurrence = 1;

    /* renamed from: de.appplant.cordova.plugin.notification.trigger.DateTrigger$Unit */
    public enum Unit {
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        QUARTER,
        YEAR
    }

    public abstract Date getNextTriggerDate(Date date);

    public int getOccurrence() {
        return this.occurrence;
    }

    /* access modifiers changed from: package-private */
    public void incOccurrence() {
        this.occurrence++;
    }

    /* access modifiers changed from: package-private */
    public Calendar getCal(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }
}
