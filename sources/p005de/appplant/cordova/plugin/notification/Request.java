package p005de.appplant.cordova.plugin.notification;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;
import p005de.appplant.cordova.plugin.notification.trigger.DateTrigger;
import p005de.appplant.cordova.plugin.notification.trigger.IntervalTrigger;
import p005de.appplant.cordova.plugin.notification.trigger.MatchTrigger;

/* renamed from: de.appplant.cordova.plugin.notification.Request */
public final class Request {
    public static final String EXTRA_LAST = "NOTIFICATION_LAST";
    static final String EXTRA_OCCURRENCE = "NOTIFICATION_OCCURRENCE";
    private final int count;
    private final Options options;
    private final JSONObject spec;
    private final DateTrigger trigger;
    private Date triggerDate;

    public Request(Options options2) {
        this.options = options2;
        this.spec = options2.getTrigger();
        this.count = Math.max(this.spec.optInt("count"), 1);
        this.trigger = buildTrigger();
        this.triggerDate = this.trigger.getNextTriggerDate(getBaseDate());
    }

    public Request(Options options2, Date date) {
        this.options = options2;
        this.spec = options2.getTrigger();
        this.count = Math.max(this.spec.optInt("count"), 1);
        this.trigger = buildTrigger();
        this.triggerDate = this.trigger.getNextTriggerDate(date);
    }

    public Options getOptions() {
        return this.options;
    }

    /* access modifiers changed from: package-private */
    public String getIdentifier() {
        return this.options.getId().toString() + "-" + getOccurrence();
    }

    /* access modifiers changed from: package-private */
    public int getOccurrence() {
        return this.trigger.getOccurrence();
    }

    private boolean hasNext() {
        return this.triggerDate != null && getOccurrence() <= this.count;
    }

    /* access modifiers changed from: package-private */
    public boolean moveNext() {
        if (hasNext()) {
            this.triggerDate = getNextTriggerDate();
        } else {
            this.triggerDate = null;
        }
        return this.triggerDate != null;
    }

    public Date getTriggerDate() {
        Calendar instance = Calendar.getInstance();
        Date date = this.triggerDate;
        if (date == null) {
            return null;
        }
        long time = date.getTime();
        if (instance.getTimeInMillis() - time <= 60000 && time < this.spec.optLong("before", 1 + time)) {
            return this.triggerDate;
        }
        return null;
    }

    private Date getNextTriggerDate() {
        return this.trigger.getNextTriggerDate(this.triggerDate);
    }

    private DateTrigger buildTrigger() {
        if (this.spec.opt("every") instanceof JSONObject) {
            return new MatchTrigger(getMatchingComponents(), getSpecialMatchingComponents());
        }
        return new IntervalTrigger(getTicks(), getUnit());
    }

    private DateTrigger.Unit getUnit() {
        Object opt = this.spec.opt("every");
        String str = "SECOND";
        if (this.spec.has("unit")) {
            str = this.spec.optString("unit", "second");
        } else if (opt instanceof String) {
            str = this.spec.optString("every", "second");
        }
        return DateTrigger.Unit.valueOf(str.toUpperCase());
    }

    private int getTicks() {
        Object opt = this.spec.opt("every");
        if (this.spec.has("at")) {
            return 0;
        }
        if (this.spec.has("in")) {
            return this.spec.optInt("in", 0);
        }
        if (opt instanceof String) {
            return 1;
        }
        if (!(opt instanceof JSONObject)) {
            return this.spec.optInt("every", 0);
        }
        return 0;
    }

    private List<Integer> getMatchingComponents() {
        JSONObject optJSONObject = this.spec.optJSONObject("every");
        return Arrays.asList(new Integer[]{(Integer) optJSONObject.opt("minute"), (Integer) optJSONObject.opt("hour"), (Integer) optJSONObject.opt("day"), (Integer) optJSONObject.opt("month"), (Integer) optJSONObject.opt("year")});
    }

    private List<Integer> getSpecialMatchingComponents() {
        JSONObject optJSONObject = this.spec.optJSONObject("every");
        return Arrays.asList(new Integer[]{(Integer) optJSONObject.opt("weekday"), (Integer) optJSONObject.opt("weekdayOrdinal"), (Integer) optJSONObject.opt("weekOfMonth"), (Integer) optJSONObject.opt("quarter")});
    }

    private Date getBaseDate() {
        if (this.spec.has("at")) {
            return new Date(this.spec.optLong("at", 0));
        }
        if (this.spec.has("firstAt")) {
            return new Date(this.spec.optLong("firstAt", 0));
        }
        if (this.spec.has("after")) {
            return new Date(this.spec.optLong("after", 0));
        }
        return new Date();
    }
}
