package com.journeyapps.barcodescanner;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import java.util.ArrayList;
import java.util.List;

public class Decoder implements ResultPointCallback {
    private List<ResultPoint> possibleResultPoints = new ArrayList();
    private Reader reader;

    public Decoder(Reader reader2) {
        this.reader = reader2;
    }

    /* access modifiers changed from: protected */
    public Reader getReader() {
        return this.reader;
    }

    public Result decode(LuminanceSource luminanceSource) {
        return decode(toBitmap(luminanceSource));
    }

    /* access modifiers changed from: protected */
    public BinaryBitmap toBitmap(LuminanceSource luminanceSource) {
        return new BinaryBitmap(new HybridBinarizer(luminanceSource));
    }

    /* access modifiers changed from: protected */
    public Result decode(BinaryBitmap binaryBitmap) {
        Result result;
        this.possibleResultPoints.clear();
        try {
            if (this.reader instanceof MultiFormatReader) {
                result = ((MultiFormatReader) this.reader).decodeWithState(binaryBitmap);
            } else {
                result = this.reader.decode(binaryBitmap);
            }
        } catch (Exception unused) {
            result = null;
        } catch (Throwable th) {
            this.reader.reset();
            throw th;
        }
        this.reader.reset();
        return result;
    }

    public List<ResultPoint> getPossibleResultPoints() {
        return new ArrayList(this.possibleResultPoints);
    }

    public void foundPossibleResultPoint(ResultPoint resultPoint) {
        this.possibleResultPoints.add(resultPoint);
    }
}
