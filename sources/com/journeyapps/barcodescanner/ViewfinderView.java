package com.journeyapps.barcodescanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.C0369R;
import com.journeyapps.barcodescanner.CameraPreview;
import java.util.ArrayList;
import java.util.List;

public class ViewfinderView extends View {
    protected static final long ANIMATION_DELAY = 80;
    protected static final int CURRENT_POINT_OPACITY = 160;
    protected static final int MAX_RESULT_POINTS = 20;
    protected static final int POINT_SIZE = 6;
    protected static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    protected static final String TAG = "ViewfinderView";
    protected CameraPreview cameraPreview;
    protected Rect framingRect;
    protected final int laserColor;
    protected List<ResultPoint> lastPossibleResultPoints;
    protected final int maskColor;
    protected final Paint paint = new Paint(1);
    protected List<ResultPoint> possibleResultPoints;
    protected Rect previewFramingRect;
    protected Bitmap resultBitmap;
    protected final int resultColor;
    protected final int resultPointColor;
    protected int scannerAlpha;

    public ViewfinderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Resources resources = getResources();
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, C0369R.styleable.zxing_finder);
        this.maskColor = obtainStyledAttributes.getColor(C0369R.styleable.zxing_finder_zxing_viewfinder_mask, resources.getColor(C0369R.color.zxing_viewfinder_mask));
        this.resultColor = obtainStyledAttributes.getColor(C0369R.styleable.zxing_finder_zxing_result_view, resources.getColor(C0369R.color.zxing_result_view));
        this.laserColor = obtainStyledAttributes.getColor(C0369R.styleable.zxing_finder_zxing_viewfinder_laser, resources.getColor(C0369R.color.zxing_viewfinder_laser));
        this.resultPointColor = obtainStyledAttributes.getColor(C0369R.styleable.zxing_finder_zxing_possible_result_points, resources.getColor(C0369R.color.zxing_possible_result_points));
        obtainStyledAttributes.recycle();
        this.scannerAlpha = 0;
        this.possibleResultPoints = new ArrayList(5);
        this.lastPossibleResultPoints = null;
    }

    public void setCameraPreview(CameraPreview cameraPreview2) {
        this.cameraPreview = cameraPreview2;
        cameraPreview2.addStateListener(new CameraPreview.StateListener() {
            public void cameraError(Exception exc) {
            }

            public void previewStarted() {
            }

            public void previewStopped() {
            }

            public void previewSized() {
                ViewfinderView.this.refreshSizes();
                ViewfinderView.this.invalidate();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void refreshSizes() {
        CameraPreview cameraPreview2 = this.cameraPreview;
        if (cameraPreview2 != null) {
            Rect framingRect2 = cameraPreview2.getFramingRect();
            Rect previewFramingRect2 = this.cameraPreview.getPreviewFramingRect();
            if (framingRect2 != null && previewFramingRect2 != null) {
                this.framingRect = framingRect2;
                this.previewFramingRect = previewFramingRect2;
            }
        }
    }

    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        Rect rect;
        refreshSizes();
        Rect rect2 = this.framingRect;
        if (rect2 != null && (rect = this.previewFramingRect) != null) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            this.paint.setColor(this.resultBitmap != null ? this.resultColor : this.maskColor);
            float f = (float) width;
            canvas.drawRect(0.0f, 0.0f, f, (float) rect2.top, this.paint);
            canvas.drawRect(0.0f, (float) rect2.top, (float) rect2.left, (float) (rect2.bottom + 1), this.paint);
            float f2 = f;
            canvas.drawRect((float) (rect2.right + 1), (float) rect2.top, f2, (float) (rect2.bottom + 1), this.paint);
            canvas.drawRect(0.0f, (float) (rect2.bottom + 1), f2, (float) height, this.paint);
            if (this.resultBitmap != null) {
                this.paint.setAlpha(CURRENT_POINT_OPACITY);
                canvas.drawBitmap(this.resultBitmap, (Rect) null, rect2, this.paint);
                return;
            }
            this.paint.setColor(this.laserColor);
            this.paint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
            this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
            int height2 = (rect2.height() / 2) + rect2.top;
            canvas.drawRect((float) (rect2.left + 2), (float) (height2 - 1), (float) (rect2.right - 1), (float) (height2 + 2), this.paint);
            float width2 = ((float) rect2.width()) / ((float) rect.width());
            float height3 = ((float) rect2.height()) / ((float) rect.height());
            List<ResultPoint> list = this.possibleResultPoints;
            List<ResultPoint> list2 = this.lastPossibleResultPoints;
            int i = rect2.left;
            int i2 = rect2.top;
            if (list.isEmpty()) {
                this.lastPossibleResultPoints = null;
            } else {
                this.possibleResultPoints = new ArrayList(5);
                this.lastPossibleResultPoints = list;
                this.paint.setAlpha(CURRENT_POINT_OPACITY);
                this.paint.setColor(this.resultPointColor);
                for (ResultPoint next : list) {
                    canvas.drawCircle((float) (((int) (next.getX() * width2)) + i), (float) (((int) (next.getY() * height3)) + i2), 6.0f, this.paint);
                }
            }
            if (list2 != null) {
                this.paint.setAlpha(80);
                this.paint.setColor(this.resultPointColor);
                for (ResultPoint next2 : list2) {
                    canvas.drawCircle((float) (((int) (next2.getX() * width2)) + i), (float) (((int) (next2.getY() * height3)) + i2), 3.0f, this.paint);
                }
            }
            postInvalidateDelayed(ANIMATION_DELAY, rect2.left - 6, rect2.top - 6, rect2.right + 6, rect2.bottom + 6);
        }
    }

    public void drawViewfinder() {
        Bitmap bitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (bitmap != null) {
            bitmap.recycle();
        }
        invalidate();
    }

    public void drawResultBitmap(Bitmap bitmap) {
        this.resultBitmap = bitmap;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint resultPoint) {
        List<ResultPoint> list = this.possibleResultPoints;
        list.add(resultPoint);
        int size = list.size();
        if (size > 20) {
            list.subList(0, size - 10).clear();
        }
    }
}
