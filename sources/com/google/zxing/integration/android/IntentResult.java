package com.google.zxing.integration.android;

public final class IntentResult {
    private final String barcodeImagePath;
    private final String contents;
    private final String errorCorrectionLevel;
    private final String formatName;
    private final Integer orientation;
    private final byte[] rawBytes;

    IntentResult() {
        this((String) null, (String) null, (byte[]) null, (Integer) null, (String) null, (String) null);
    }

    IntentResult(String str, String str2, byte[] bArr, Integer num, String str3, String str4) {
        this.contents = str;
        this.formatName = str2;
        this.rawBytes = bArr;
        this.orientation = num;
        this.errorCorrectionLevel = str3;
        this.barcodeImagePath = str4;
    }

    public String getContents() {
        return this.contents;
    }

    public String getFormatName() {
        return this.formatName;
    }

    public byte[] getRawBytes() {
        return this.rawBytes;
    }

    public Integer getOrientation() {
        return this.orientation;
    }

    public String getErrorCorrectionLevel() {
        return this.errorCorrectionLevel;
    }

    public String getBarcodeImagePath() {
        return this.barcodeImagePath;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(120);
        sb.append("Format: ");
        sb.append(this.formatName);
        sb.append(10);
        sb.append("Contents: ");
        sb.append(this.contents);
        sb.append(10);
        byte[] bArr = this.rawBytes;
        int length = bArr == null ? 0 : bArr.length;
        sb.append("Raw bytes: (");
        sb.append(length);
        sb.append(" bytes)\n");
        sb.append("Orientation: ");
        sb.append(this.orientation);
        sb.append(10);
        sb.append("EC level: ");
        sb.append(this.errorCorrectionLevel);
        sb.append(10);
        sb.append("Barcode image: ");
        sb.append(this.barcodeImagePath);
        sb.append(10);
        return sb.toString();
    }
}
