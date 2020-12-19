package p005de.appplant.cordova.plugin.notification.action;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.cordova.globalization.Globalization;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: de.appplant.cordova.plugin.notification.action.ActionGroup */
public final class ActionGroup {
    private static final Map<String, ActionGroup> groups = new HashMap();
    private final Action[] actions;

    /* renamed from: id */
    private final String f47id;

    public static ActionGroup lookup(String str) {
        return groups.get(str);
    }

    public static void register(ActionGroup actionGroup) {
        groups.put(actionGroup.getId(), actionGroup);
    }

    public static void unregister(String str) {
        groups.remove(str);
    }

    public static boolean isRegistered(String str) {
        return groups.containsKey(str);
    }

    public static ActionGroup parse(Context context, JSONArray jSONArray) {
        return parse(context, (String) null, jSONArray);
    }

    public static ActionGroup parse(Context context, String str, JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList(jSONArray.length());
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            String optString = optJSONObject.optString(Globalization.TYPE, "button");
            if (optString.equals("input") && Build.VERSION.SDK_INT < 24) {
                Log.w("Action", "Type input is not supported");
            } else if (optString.equals("button") || optString.equals("input")) {
                arrayList.add(new Action(context, optJSONObject));
            } else {
                Log.w("Action", "Unknown type: " + optString);
            }
        }
        return new ActionGroup(str, (Action[]) arrayList.toArray(new Action[arrayList.size()]));
    }

    private ActionGroup(String str, Action[] actionArr) {
        this.f47id = str;
        this.actions = actionArr;
    }

    public String getId() {
        return this.f47id;
    }

    public Action[] getActions() {
        return this.actions;
    }
}
