package p005de.appplant.cordova.plugin.notification.action;

import android.content.Context;
import android.support.p000v4.app.RemoteInput;
import com.adobe.phonegap.push.PushConstants;
import org.apache.cordova.globalization.Globalization;
import org.json.JSONArray;
import org.json.JSONObject;
import p005de.appplant.cordova.plugin.notification.util.AssetUtil;

/* renamed from: de.appplant.cordova.plugin.notification.action.Action */
public final class Action {
    public static final String CLICK_ACTION_ID = "click";
    public static final String EXTRA_ID = "NOTIFICATION_ACTION_ID";
    private final Context context;
    private final JSONObject options;

    Action(Context context2, JSONObject jSONObject) {
        this.context = context2;
        this.options = jSONObject;
    }

    public String getId() {
        return this.options.optString(PushConstants.CHANNEL_ID, getTitle());
    }

    public String getTitle() {
        return this.options.optString(PushConstants.TITLE, "unknown");
    }

    public int getIcon() {
        int resId = AssetUtil.getInstance(this.context).getResId(this.options.optString(PushConstants.ICON));
        if (resId == 0) {
            return 17301656;
        }
        return resId;
    }

    public boolean isLaunchingApp() {
        return this.options.optBoolean("launch", false);
    }

    public boolean isWithInput() {
        return this.options.optString(Globalization.TYPE).equals("input");
    }

    public RemoteInput getInput() {
        return new RemoteInput.Builder(getId()).setLabel(this.options.optString("emptyText")).setAllowFreeFormInput(this.options.optBoolean("editable", true)).setChoices(getChoices()).build();
    }

    private String[] getChoices() {
        JSONArray optJSONArray = this.options.optJSONArray("choices");
        if (optJSONArray == null) {
            return null;
        }
        String[] strArr = new String[optJSONArray.length()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = optJSONArray.optString(i);
        }
        return strArr;
    }
}
