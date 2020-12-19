package com.google.zxing.oned;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class Code128Reader extends OneDReader {
    private static final int CODE_CODE_A = 101;
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_FNC_1 = 102;
    private static final int CODE_FNC_2 = 97;
    private static final int CODE_FNC_3 = 96;
    private static final int CODE_FNC_4_A = 101;
    private static final int CODE_FNC_4_B = 100;
    static final int[][] CODE_PATTERNS = {new int[]{2, 1, 2, 2, 2, 2}, new int[]{2, 2, 2, 1, 2, 2}, new int[]{2, 2, 2, 2, 2, 1}, new int[]{1, 2, 1, 2, 2, 3}, new int[]{1, 2, 1, 3, 2, 2}, new int[]{1, 3, 1, 2, 2, 2}, new int[]{1, 2, 2, 2, 1, 3}, new int[]{1, 2, 2, 3, 1, 2}, new int[]{1, 3, 2, 2, 1, 2}, new int[]{2, 2, 1, 2, 1, 3}, new int[]{2, 2, 1, 3, 1, 2}, new int[]{2, 3, 1, 2, 1, 2}, new int[]{1, 1, 2, 2, 3, 2}, new int[]{1, 2, 2, 1, 3, 2}, new int[]{1, 2, 2, 2, 3, 1}, new int[]{1, 1, 3, 2, 2, 2}, new int[]{1, 2, 3, 1, 2, 2}, new int[]{1, 2, 3, 2, 2, 1}, new int[]{2, 2, 3, 2, 1, 1}, new int[]{2, 2, 1, 1, 3, 2}, new int[]{2, 2, 1, 2, 3, 1}, new int[]{2, 1, 3, 2, 1, 2}, new int[]{2, 2, 3, 1, 1, 2}, new int[]{3, 1, 2, 1, 3, 1}, new int[]{3, 1, 1, 2, 2, 2}, new int[]{3, 2, 1, 1, 2, 2}, new int[]{3, 2, 1, 2, 2, 1}, new int[]{3, 1, 2, 2, 1, 2}, new int[]{3, 2, 2, 1, 1, 2}, new int[]{3, 2, 2, 2, 1, 1}, new int[]{2, 1, 2, 1, 2, 3}, new int[]{2, 1, 2, 3, 2, 1}, new int[]{2, 3, 2, 1, 2, 1}, new int[]{1, 1, 1, 3, 2, 3}, new int[]{1, 3, 1, 1, 2, 3}, new int[]{1, 3, 1, 3, 2, 1}, new int[]{1, 1, 2, 3, 1, 3}, new int[]{1, 3, 2, 1, 1, 3}, new int[]{1, 3, 2, 3, 1, 1}, new int[]{2, 1, 1, 3, 1, 3}, new int[]{2, 3, 1, 1, 1, 3}, new int[]{2, 3, 1, 3, 1, 1}, new int[]{1, 1, 2, 1, 3, 3}, new int[]{1, 1, 2, 3, 3, 1}, new int[]{1, 3, 2, 1, 3, 1}, new int[]{1, 1, 3, 1, 2, 3}, new int[]{1, 1, 3, 3, 2, 1}, new int[]{1, 3, 3, 1, 2, 1}, new int[]{3, 1, 3, 1, 2, 1}, new int[]{2, 1, 1, 3, 3, 1}, new int[]{2, 3, 1, 1, 3, 1}, new int[]{2, 1, 3, 1, 1, 3}, new int[]{2, 1, 3, 3, 1, 1}, new int[]{2, 1, 3, 1, 3, 1}, new int[]{3, 1, 1, 1, 2, 3}, new int[]{3, 1, 1, 3, 2, 1}, new int[]{3, 3, 1, 1, 2, 1}, new int[]{3, 1, 2, 1, 1, 3}, new int[]{3, 1, 2, 3, 1, 1}, new int[]{3, 3, 2, 1, 1, 1}, new int[]{3, 1, 4, 1, 1, 1}, new int[]{2, 2, 1, 4, 1, 1}, new int[]{4, 3, 1, 1, 1, 1}, new int[]{1, 1, 1, 2, 2, 4}, new int[]{1, 1, 1, 4, 2, 2}, new int[]{1, 2, 1, 1, 2, 4}, new int[]{1, 2, 1, 4, 2, 1}, new int[]{1, 4, 1, 1, 2, 2}, new int[]{1, 4, 1, 2, 2, 1}, new int[]{1, 1, 2, 2, 1, 4}, new int[]{1, 1, 2, 4, 1, 2}, new int[]{1, 2, 2, 1, 1, 4}, new int[]{1, 2, 2, 4, 1, 1}, new int[]{1, 4, 2, 1, 1, 2}, new int[]{1, 4, 2, 2, 1, 1}, new int[]{2, 4, 1, 2, 1, 1}, new int[]{2, 2, 1, 1, 1, 4}, new int[]{4, 1, 3, 1, 1, 1}, new int[]{2, 4, 1, 1, 1, 2}, new int[]{1, 3, 4, 1, 1, 1}, new int[]{1, 1, 1, 2, 4, 2}, new int[]{1, 2, 1, 1, 4, 2}, new int[]{1, 2, 1, 2, 4, 1}, new int[]{1, 1, 4, 2, 1, 2}, new int[]{1, 2, 4, 1, 1, 2}, new int[]{1, 2, 4, 2, 1, 1}, new int[]{4, 1, 1, 2, 1, 2}, new int[]{4, 2, 1, 1, 1, 2}, new int[]{4, 2, 1, 2, 1, 1}, new int[]{2, 1, 2, 1, 4, 1}, new int[]{2, 1, 4, 1, 2, 1}, new int[]{4, 1, 2, 1, 2, 1}, new int[]{1, 1, 1, 1, 4, 3}, new int[]{1, 1, 1, 3, 4, 1}, new int[]{1, 3, 1, 1, 4, 1}, new int[]{1, 1, 4, 1, 1, 3}, new int[]{1, 1, 4, 3, 1, 1}, new int[]{4, 1, 1, 1, 1, 3}, new int[]{4, 1, 1, 3, 1, 1}, new int[]{1, 1, 3, 1, 4, 1}, new int[]{1, 1, 4, 1, 3, 1}, new int[]{3, 1, 1, 1, 4, 1}, new int[]{4, 1, 1, 1, 3, 1}, new int[]{2, 1, 1, 4, 1, 2}, new int[]{2, 1, 1, 2, 1, 4}, new int[]{2, 1, 1, 2, 3, 2}, new int[]{2, 3, 3, 1, 1, 1, 2}};
    private static final int CODE_SHIFT = 98;
    private static final int CODE_START_A = 103;
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_STOP = 106;
    private static final float MAX_AVG_VARIANCE = 0.25f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.7f;

    private static int[] findStartPattern(BitArray bitArray) throws NotFoundException {
        int size = bitArray.getSize();
        int nextSet = bitArray.getNextSet(0);
        int[] iArr = new int[6];
        int length = iArr.length;
        int i = nextSet;
        boolean z = false;
        int i2 = 0;
        while (nextSet < size) {
            if (bitArray.get(nextSet) ^ z) {
                iArr[i2] = iArr[i2] + 1;
            } else {
                int i3 = length - 1;
                if (i2 == i3) {
                    float f = MAX_AVG_VARIANCE;
                    int i4 = -1;
                    for (int i5 = 103; i5 <= 105; i5++) {
                        float patternMatchVariance = patternMatchVariance(iArr, CODE_PATTERNS[i5], MAX_INDIVIDUAL_VARIANCE);
                        if (patternMatchVariance < f) {
                            i4 = i5;
                            f = patternMatchVariance;
                        }
                    }
                    if (i4 < 0 || !bitArray.isRange(Math.max(0, i - ((nextSet - i) / 2)), i, false)) {
                        i += iArr[0] + iArr[1];
                        int i6 = length - 2;
                        System.arraycopy(iArr, 2, iArr, 0, i6);
                        iArr[i6] = 0;
                        iArr[i3] = 0;
                        i2--;
                    } else {
                        return new int[]{i, nextSet, i4};
                    }
                } else {
                    i2++;
                }
                iArr[i2] = 1;
                z = !z;
            }
            nextSet++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeCode(BitArray bitArray, int[] iArr, int i) throws NotFoundException {
        recordPattern(bitArray, i, iArr);
        float f = MAX_AVG_VARIANCE;
        int i2 = -1;
        int i3 = 0;
        while (true) {
            int[][] iArr2 = CODE_PATTERNS;
            if (i3 >= iArr2.length) {
                break;
            }
            float patternMatchVariance = patternMatchVariance(iArr, iArr2[i3], MAX_INDIVIDUAL_VARIANCE);
            if (patternMatchVariance < f) {
                i2 = i3;
                f = patternMatchVariance;
            }
            i3++;
        }
        if (i2 >= 0) {
            return i2;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.zxing.Result decodeRow(int r25, com.google.zxing.common.BitArray r26, java.util.Map<com.google.zxing.DecodeHintType, ?> r27) throws com.google.zxing.NotFoundException, com.google.zxing.FormatException, com.google.zxing.ChecksumException {
        /*
            r24 = this;
            r0 = r26
            r1 = r27
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0012
            com.google.zxing.DecodeHintType r4 = com.google.zxing.DecodeHintType.ASSUME_GS1
            boolean r1 = r1.containsKey(r4)
            if (r1 == 0) goto L_0x0012
            r1 = 1
            goto L_0x0013
        L_0x0012:
            r1 = 0
        L_0x0013:
            int[] r4 = findStartPattern(r26)
            r5 = 2
            r6 = r4[r5]
            java.util.ArrayList r7 = new java.util.ArrayList
            r8 = 20
            r7.<init>(r8)
            byte r9 = (byte) r6
            java.lang.Byte r9 = java.lang.Byte.valueOf(r9)
            r7.add(r9)
            switch(r6) {
                case 103: goto L_0x0037;
                case 104: goto L_0x0034;
                case 105: goto L_0x0031;
                default: goto L_0x002c;
            }
        L_0x002c:
            com.google.zxing.FormatException r0 = com.google.zxing.FormatException.getFormatInstance()
            throw r0
        L_0x0031:
            r12 = 99
            goto L_0x0039
        L_0x0034:
            r12 = 100
            goto L_0x0039
        L_0x0037:
            r12 = 101(0x65, float:1.42E-43)
        L_0x0039:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>(r8)
            r8 = r4[r3]
            r14 = r4[r2]
            r15 = 6
            int[] r15 = new int[r15]
            r17 = r6
            r9 = r12
            r2 = 0
            r5 = 0
            r6 = 0
            r16 = 0
            r18 = 0
            r19 = 1
            r12 = r8
            r8 = r14
            r14 = 0
        L_0x0054:
            if (r6 != 0) goto L_0x01d4
            int r2 = decodeCode(r0, r15, r8)
            byte r12 = (byte) r2
            java.lang.Byte r12 = java.lang.Byte.valueOf(r12)
            r7.add(r12)
            r12 = 106(0x6a, float:1.49E-43)
            if (r2 == r12) goto L_0x0068
            r19 = 1
        L_0x0068:
            if (r2 == r12) goto L_0x0070
            int r18 = r18 + 1
            int r20 = r18 * r2
            int r17 = r17 + r20
        L_0x0070:
            int r11 = r15.length
            r21 = r8
            r10 = 0
        L_0x0074:
            if (r10 >= r11) goto L_0x007d
            r22 = r15[r10]
            int r21 = r21 + r22
            int r10 = r10 + 1
            goto L_0x0074
        L_0x007d:
            switch(r2) {
                case 103: goto L_0x0089;
                case 104: goto L_0x0089;
                case 105: goto L_0x0089;
                default: goto L_0x0080;
            }
        L_0x0080:
            r10 = 96
            switch(r9) {
                case 99: goto L_0x017a;
                case 100: goto L_0x0111;
                case 101: goto L_0x008e;
                default: goto L_0x0085;
            }
        L_0x0085:
            r10 = 100
            goto L_0x01b7
        L_0x0089:
            com.google.zxing.FormatException r0 = com.google.zxing.FormatException.getFormatInstance()
            throw r0
        L_0x008e:
            r11 = 64
            if (r2 >= r11) goto L_0x00a9
            if (r3 != r5) goto L_0x009b
            int r3 = r2 + 32
            char r3 = (char) r3
            r13.append(r3)
            goto L_0x00a3
        L_0x009b:
            int r3 = r2 + 32
            int r3 = r3 + 128
            char r3 = (char) r3
            r13.append(r3)
        L_0x00a3:
            r3 = 0
            r10 = 100
            r11 = 0
            goto L_0x01b9
        L_0x00a9:
            if (r2 >= r10) goto L_0x00c0
            if (r3 != r5) goto L_0x00b4
            int r3 = r2 + -64
            char r3 = (char) r3
            r13.append(r3)
            goto L_0x00ba
        L_0x00b4:
            int r3 = r2 + 64
            char r3 = (char) r3
            r13.append(r3)
        L_0x00ba:
            r3 = 0
            r10 = 100
            r11 = 0
            goto L_0x01b9
        L_0x00c0:
            if (r2 == r12) goto L_0x00c4
            r19 = 0
        L_0x00c4:
            if (r2 == r12) goto L_0x0107
            switch(r2) {
                case 96: goto L_0x0103;
                case 97: goto L_0x0103;
                case 98: goto L_0x00fe;
                case 99: goto L_0x00f9;
                case 100: goto L_0x00f4;
                case 101: goto L_0x00de;
                case 102: goto L_0x00ca;
                default: goto L_0x00c9;
            }
        L_0x00c9:
            goto L_0x0103
        L_0x00ca:
            if (r1 == 0) goto L_0x0103
            int r10 = r13.length()
            if (r10 != 0) goto L_0x00d8
            java.lang.String r10 = "]C1"
            r13.append(r10)
            goto L_0x0103
        L_0x00d8:
            r10 = 29
            r13.append(r10)
            goto L_0x0103
        L_0x00de:
            if (r5 != 0) goto L_0x00e7
            if (r3 == 0) goto L_0x00e7
            r10 = r9
            r3 = 0
            r5 = 1
            r9 = 0
            goto L_0x010b
        L_0x00e7:
            if (r5 == 0) goto L_0x00f0
            if (r3 == 0) goto L_0x00f0
            r10 = r9
            r3 = 0
            r5 = 0
            r9 = 0
            goto L_0x010b
        L_0x00f0:
            r10 = r9
            r3 = 0
            r9 = 1
            goto L_0x010b
        L_0x00f4:
            r9 = r3
            r3 = 0
            r10 = 100
            goto L_0x010b
        L_0x00f9:
            r9 = r3
            r3 = 0
            r10 = 99
            goto L_0x010b
        L_0x00fe:
            r9 = r3
            r3 = 1
            r10 = 100
            goto L_0x010b
        L_0x0103:
            r10 = r9
            r9 = r3
            r3 = 0
            goto L_0x010b
        L_0x0107:
            r10 = r9
            r6 = 1
            r9 = r3
            r3 = 0
        L_0x010b:
            r11 = r9
            r9 = r10
            r10 = 100
            goto L_0x01b9
        L_0x0111:
            if (r2 >= r10) goto L_0x012a
            if (r3 != r5) goto L_0x011c
            int r3 = r2 + 32
            char r3 = (char) r3
            r13.append(r3)
            goto L_0x0124
        L_0x011c:
            int r3 = r2 + 32
            int r3 = r3 + 128
            char r3 = (char) r3
            r13.append(r3)
        L_0x0124:
            r3 = 0
            r10 = 100
            r11 = 0
            goto L_0x01b9
        L_0x012a:
            if (r2 == r12) goto L_0x012e
            r19 = 0
        L_0x012e:
            if (r2 == r12) goto L_0x0171
            switch(r2) {
                case 96: goto L_0x016d;
                case 97: goto L_0x016d;
                case 98: goto L_0x0168;
                case 99: goto L_0x0163;
                case 100: goto L_0x014d;
                case 101: goto L_0x0148;
                case 102: goto L_0x0134;
                default: goto L_0x0133;
            }
        L_0x0133:
            goto L_0x016d
        L_0x0134:
            if (r1 == 0) goto L_0x016d
            int r10 = r13.length()
            if (r10 != 0) goto L_0x0142
            java.lang.String r10 = "]C1"
            r13.append(r10)
            goto L_0x016d
        L_0x0142:
            r10 = 29
            r13.append(r10)
            goto L_0x016d
        L_0x0148:
            r9 = r3
            r3 = 0
            r10 = 101(0x65, float:1.42E-43)
            goto L_0x0175
        L_0x014d:
            if (r5 != 0) goto L_0x0156
            if (r3 == 0) goto L_0x0156
            r10 = r9
            r3 = 0
            r5 = 1
            r9 = 0
            goto L_0x0175
        L_0x0156:
            if (r5 == 0) goto L_0x015f
            if (r3 == 0) goto L_0x015f
            r10 = r9
            r3 = 0
            r5 = 0
            r9 = 0
            goto L_0x0175
        L_0x015f:
            r10 = r9
            r3 = 0
            r9 = 1
            goto L_0x0175
        L_0x0163:
            r9 = r3
            r3 = 0
            r10 = 99
            goto L_0x0175
        L_0x0168:
            r9 = r3
            r3 = 1
            r10 = 101(0x65, float:1.42E-43)
            goto L_0x0175
        L_0x016d:
            r10 = r9
            r9 = r3
            r3 = 0
            goto L_0x0175
        L_0x0171:
            r10 = r9
            r6 = 1
            r9 = r3
            r3 = 0
        L_0x0175:
            r11 = r9
            r9 = r10
            r10 = 100
            goto L_0x01b9
        L_0x017a:
            r10 = 100
            if (r2 >= r10) goto L_0x018b
            r11 = 10
            if (r2 >= r11) goto L_0x0187
            r11 = 48
            r13.append(r11)
        L_0x0187:
            r13.append(r2)
            goto L_0x01b7
        L_0x018b:
            if (r2 == r12) goto L_0x018f
            r19 = 0
        L_0x018f:
            if (r2 == r12) goto L_0x01b3
            switch(r2) {
                case 100: goto L_0x01ae;
                case 101: goto L_0x01a9;
                case 102: goto L_0x0195;
                default: goto L_0x0194;
            }
        L_0x0194:
            goto L_0x01b7
        L_0x0195:
            if (r1 == 0) goto L_0x01b7
            int r11 = r13.length()
            if (r11 != 0) goto L_0x01a3
            java.lang.String r11 = "]C1"
            r13.append(r11)
            goto L_0x01b7
        L_0x01a3:
            r11 = 29
            r13.append(r11)
            goto L_0x01b7
        L_0x01a9:
            r11 = r3
            r3 = 0
            r9 = 101(0x65, float:1.42E-43)
            goto L_0x01b9
        L_0x01ae:
            r11 = r3
            r3 = 0
            r9 = 100
            goto L_0x01b9
        L_0x01b3:
            r11 = r3
            r3 = 0
            r6 = 1
            goto L_0x01b9
        L_0x01b7:
            r11 = r3
            r3 = 0
        L_0x01b9:
            if (r14 == 0) goto L_0x01c5
            r14 = 101(0x65, float:1.42E-43)
            if (r9 != r14) goto L_0x01c2
            r9 = 100
            goto L_0x01c7
        L_0x01c2:
            r9 = 101(0x65, float:1.42E-43)
            goto L_0x01c7
        L_0x01c5:
            r14 = 101(0x65, float:1.42E-43)
        L_0x01c7:
            r14 = r3
            r12 = r8
            r3 = r11
            r8 = r21
            r23 = r16
            r16 = r2
            r2 = r23
            goto L_0x0054
        L_0x01d4:
            int r1 = r8 - r12
            int r3 = r0.getNextUnset(r8)
            int r5 = r26.getSize()
            int r6 = r3 - r12
            r8 = 2
            int r6 = r6 / r8
            int r6 = r6 + r3
            int r5 = java.lang.Math.min(r5, r6)
            r6 = 0
            boolean r0 = r0.isRange(r3, r5, r6)
            if (r0 == 0) goto L_0x0265
            int r18 = r18 * r2
            int r17 = r17 - r18
            int r0 = r17 % 103
            if (r0 != r2) goto L_0x0260
            int r0 = r13.length()
            if (r0 == 0) goto L_0x025b
            if (r0 <= 0) goto L_0x0212
            if (r19 == 0) goto L_0x0212
            r2 = 99
            if (r9 != r2) goto L_0x020b
            int r2 = r0 + -2
            r13.delete(r2, r0)
            r0 = 1
            goto L_0x0213
        L_0x020b:
            int r2 = r0 + -1
            r13.delete(r2, r0)
            r0 = 1
            goto L_0x0213
        L_0x0212:
            r0 = 1
        L_0x0213:
            r2 = r4[r0]
            r0 = 0
            r3 = r4[r0]
            int r2 = r2 + r3
            float r0 = (float) r2
            r2 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r2
            float r3 = (float) r12
            float r1 = (float) r1
            float r1 = r1 / r2
            float r3 = r3 + r1
            int r1 = r7.size()
            byte[] r2 = new byte[r1]
            r4 = 0
        L_0x0228:
            if (r4 >= r1) goto L_0x0239
            java.lang.Object r5 = r7.get(r4)
            java.lang.Byte r5 = (java.lang.Byte) r5
            byte r5 = r5.byteValue()
            r2[r4] = r5
            int r4 = r4 + 1
            goto L_0x0228
        L_0x0239:
            com.google.zxing.Result r1 = new com.google.zxing.Result
            java.lang.String r4 = r13.toString()
            r5 = 2
            com.google.zxing.ResultPoint[] r5 = new com.google.zxing.ResultPoint[r5]
            com.google.zxing.ResultPoint r6 = new com.google.zxing.ResultPoint
            r7 = r25
            float r7 = (float) r7
            r6.<init>(r0, r7)
            r0 = 0
            r5[r0] = r6
            com.google.zxing.ResultPoint r0 = new com.google.zxing.ResultPoint
            r0.<init>(r3, r7)
            r3 = 1
            r5[r3] = r0
            com.google.zxing.BarcodeFormat r0 = com.google.zxing.BarcodeFormat.CODE_128
            r1.<init>(r4, r2, r5, r0)
            return r1
        L_0x025b:
            com.google.zxing.NotFoundException r0 = com.google.zxing.NotFoundException.getNotFoundInstance()
            throw r0
        L_0x0260:
            com.google.zxing.ChecksumException r0 = com.google.zxing.ChecksumException.getChecksumInstance()
            throw r0
        L_0x0265:
            com.google.zxing.NotFoundException r0 = com.google.zxing.NotFoundException.getNotFoundInstance()
            throw r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.Code128Reader.decodeRow(int, com.google.zxing.common.BitArray, java.util.Map):com.google.zxing.Result");
    }
}
