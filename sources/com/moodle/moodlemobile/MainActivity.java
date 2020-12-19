package com.moodle.moodlemobile;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.adobe.phonegap.push.PushConstants;
import org.apache.cordova.CordovaActivity;

public class MainActivity extends CordovaActivity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean(PushConstants.START_IN_BACKGROUND, false)) {
            moveTaskToBack(true);
        }
        loadUrl(this.launchUrl);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.appView == null || keyEvent.getKeyCode() != 4) {
            return super.dispatchKeyEvent(keyEvent);
        }
        View view = this.appView.getView();
        if (view == null) {
            return true;
        }
        view.dispatchKeyEvent(keyEvent);
        return true;
    }
}
