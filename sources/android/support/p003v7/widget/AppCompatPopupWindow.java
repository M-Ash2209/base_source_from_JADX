package android.support.p003v7.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.p000v4.widget.PopupWindowCompat;
import android.support.p003v7.appcompat.C0245R;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/* renamed from: android.support.v7.widget.AppCompatPopupWindow */
class AppCompatPopupWindow extends PopupWindow {
    private static final boolean COMPAT_OVERLAP_ANCHOR = (Build.VERSION.SDK_INT < 21);
    private boolean mOverlapAnchor;

    public AppCompatPopupWindow(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i, 0);
    }

    public AppCompatPopupWindow(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int i, @StyleRes int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet, i, i2);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, C0245R.styleable.PopupWindow, i, i2);
        if (obtainStyledAttributes.hasValue(C0245R.styleable.PopupWindow_overlapAnchor)) {
            setSupportOverlapAnchor(obtainStyledAttributes.getBoolean(C0245R.styleable.PopupWindow_overlapAnchor, false));
        }
        setBackgroundDrawable(obtainStyledAttributes.getDrawable(C0245R.styleable.PopupWindow_android_popupBackground));
        obtainStyledAttributes.recycle();
    }

    public void showAsDropDown(View view, int i, int i2) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.showAsDropDown(view, i, i2);
    }

    public void showAsDropDown(View view, int i, int i2, int i3) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.showAsDropDown(view, i, i2, i3);
    }

    public void update(View view, int i, int i2, int i3, int i4) {
        super.update(view, i, (!COMPAT_OVERLAP_ANCHOR || !this.mOverlapAnchor) ? i2 : i2 - view.getHeight(), i3, i4);
    }

    private void setSupportOverlapAnchor(boolean z) {
        if (COMPAT_OVERLAP_ANCHOR) {
            this.mOverlapAnchor = z;
        } else {
            PopupWindowCompat.setOverlapAnchor(this, z);
        }
    }
}
