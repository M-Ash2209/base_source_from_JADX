package com.google.zxing.pdf417.detector;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Detector {
    private static final int BARCODE_MIN_HEIGHT = 10;
    private static final int[] INDEXES_START_PATTERN = {0, 4, 1, 5};
    private static final int[] INDEXES_STOP_PATTERN = {6, 2, 7, 3};
    private static final float MAX_AVG_VARIANCE = 0.42f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.8f;
    private static final int MAX_PATTERN_DRIFT = 5;
    private static final int MAX_PIXEL_DRIFT = 3;
    private static final int ROW_STEP = 5;
    private static final int SKIPPED_ROW_COUNT_MAX = 25;
    private static final int[] START_PATTERN = {8, 1, 1, 1, 1, 1, 1, 3};
    private static final int[] STOP_PATTERN = {7, 1, 1, 3, 1, 1, 1, 2, 1};

    private Detector() {
    }

    public static PDF417DetectorResult detect(BinaryBitmap binaryBitmap, Map<DecodeHintType, ?> map, boolean z) throws NotFoundException {
        BitMatrix blackMatrix = binaryBitmap.getBlackMatrix();
        List<ResultPoint[]> detect = detect(z, blackMatrix);
        if (detect.isEmpty()) {
            blackMatrix = blackMatrix.clone();
            blackMatrix.rotate180();
            detect = detect(z, blackMatrix);
        }
        return new PDF417DetectorResult(blackMatrix, detect);
    }

    private static List<ResultPoint[]> detect(boolean z, BitMatrix bitMatrix) {
        int i;
        float f;
        ArrayList<ResultPoint[]> arrayList = new ArrayList<>();
        int i2 = 0;
        int i3 = 0;
        boolean z2 = false;
        while (i2 < bitMatrix.getHeight()) {
            ResultPoint[] findVertices = findVertices(bitMatrix, i2, i3);
            if (findVertices[0] != null || findVertices[3] != null) {
                arrayList.add(findVertices);
                if (!z) {
                    break;
                }
                if (findVertices[2] != null) {
                    i = (int) findVertices[2].getX();
                    f = findVertices[2].getY();
                } else {
                    i = (int) findVertices[4].getX();
                    f = findVertices[4].getY();
                }
                i2 = (int) f;
                i3 = i;
                z2 = true;
            } else if (!z2) {
                break;
            } else {
                for (ResultPoint[] resultPointArr : arrayList) {
                    if (resultPointArr[1] != null) {
                        i2 = (int) Math.max((float) i2, resultPointArr[1].getY());
                    }
                    if (resultPointArr[3] != null) {
                        i2 = Math.max(i2, (int) resultPointArr[3].getY());
                    }
                }
                i2 += 5;
                i3 = 0;
                z2 = false;
            }
        }
        return arrayList;
    }

    private static ResultPoint[] findVertices(BitMatrix bitMatrix, int i, int i2) {
        int i3;
        int i4;
        int height = bitMatrix.getHeight();
        int width = bitMatrix.getWidth();
        ResultPoint[] resultPointArr = new ResultPoint[8];
        copyToResult(resultPointArr, findRowsWithPattern(bitMatrix, height, width, i, i2, START_PATTERN), INDEXES_START_PATTERN);
        if (resultPointArr[4] != null) {
            int x = (int) resultPointArr[4].getX();
            i4 = (int) resultPointArr[4].getY();
            i3 = x;
        } else {
            i4 = i;
            i3 = i2;
        }
        copyToResult(resultPointArr, findRowsWithPattern(bitMatrix, height, width, i4, i3, STOP_PATTERN), INDEXES_STOP_PATTERN);
        return resultPointArr;
    }

    private static void copyToResult(ResultPoint[] resultPointArr, ResultPoint[] resultPointArr2, int[] iArr) {
        for (int i = 0; i < iArr.length; i++) {
            resultPointArr[iArr[i]] = resultPointArr2[i];
        }
    }

    private static ResultPoint[] findRowsWithPattern(BitMatrix bitMatrix, int i, int i2, int i3, int i4, int[] iArr) {
        int i5;
        boolean z;
        int i6;
        int i7 = i;
        ResultPoint[] resultPointArr = new ResultPoint[4];
        int[] iArr2 = new int[iArr.length];
        int i8 = i3;
        while (true) {
            if (i8 >= i7) {
                z = false;
                break;
            }
            int[] findGuardPattern = findGuardPattern(bitMatrix, i4, i8, i2, false, iArr, iArr2);
            if (findGuardPattern != null) {
                int[] iArr3 = findGuardPattern;
                while (true) {
                    if (i8 <= 0) {
                        break;
                    }
                    i8--;
                    int[] findGuardPattern2 = findGuardPattern(bitMatrix, i4, i8, i2, false, iArr, iArr2);
                    if (findGuardPattern2 == null) {
                        i8++;
                        break;
                    }
                    iArr3 = findGuardPattern2;
                }
                float f = (float) i8;
                resultPointArr[0] = new ResultPoint((float) iArr3[0], f);
                resultPointArr[1] = new ResultPoint((float) iArr3[1], f);
                z = true;
            } else {
                i8 += 5;
            }
        }
        int i9 = i8 + 1;
        if (z) {
            int[] iArr4 = {(int) resultPointArr[0].getX(), (int) resultPointArr[1].getX()};
            int i10 = i9;
            int i11 = 0;
            while (true) {
                if (i10 >= i7) {
                    i6 = i11;
                    break;
                }
                i6 = i11;
                int[] findGuardPattern3 = findGuardPattern(bitMatrix, iArr4[0], i10, i2, false, iArr, iArr2);
                if (findGuardPattern3 != null && Math.abs(iArr4[0] - findGuardPattern3[0]) < 5 && Math.abs(iArr4[1] - findGuardPattern3[1]) < 5) {
                    iArr4 = findGuardPattern3;
                    i11 = 0;
                } else if (i6 > 25) {
                    break;
                } else {
                    i11 = i6 + 1;
                }
                i10++;
            }
            i9 = i10 - (i6 + 1);
            float f2 = (float) i9;
            resultPointArr[2] = new ResultPoint((float) iArr4[0], f2);
            resultPointArr[3] = new ResultPoint((float) iArr4[1], f2);
        }
        if (i9 - i8 < 10) {
            for (i5 = 0; i5 < resultPointArr.length; i5++) {
                resultPointArr[i5] = null;
            }
        }
        return resultPointArr;
    }

    private static int[] findGuardPattern(BitMatrix bitMatrix, int i, int i2, int i3, boolean z, int[] iArr, int[] iArr2) {
        Arrays.fill(iArr2, 0, iArr2.length, 0);
        int length = iArr.length;
        int i4 = 0;
        while (bitMatrix.get(i, i2) && i > 0) {
            int i5 = i4 + 1;
            if (i4 >= 3) {
                break;
            }
            i--;
            i4 = i5;
        }
        int i6 = i;
        boolean z2 = z;
        int i7 = 0;
        while (true) {
            boolean z3 = true;
            if (i < i3) {
                if (bitMatrix.get(i, i2) ^ z2) {
                    iArr2[i7] = iArr2[i7] + 1;
                } else {
                    int i8 = length - 1;
                    if (i7 != i8) {
                        i7++;
                    } else if (patternMatchVariance(iArr2, iArr, MAX_INDIVIDUAL_VARIANCE) < MAX_AVG_VARIANCE) {
                        return new int[]{i6, i};
                    } else {
                        i6 += iArr2[0] + iArr2[1];
                        int i9 = length - 2;
                        System.arraycopy(iArr2, 2, iArr2, 0, i9);
                        iArr2[i9] = 0;
                        iArr2[i8] = 0;
                        i7--;
                    }
                    iArr2[i7] = 1;
                    if (z2) {
                        z3 = false;
                    }
                    z2 = z3;
                }
                i++;
            } else if (i7 != length - 1 || patternMatchVariance(iArr2, iArr, MAX_INDIVIDUAL_VARIANCE) >= MAX_AVG_VARIANCE) {
                return null;
            } else {
                return new int[]{i6, i - 1};
            }
        }
    }

    private static float patternMatchVariance(int[] iArr, int[] iArr2, float f) {
        int length = iArr.length;
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            i += iArr[i3];
            i2 += iArr2[i3];
        }
        if (i < i2) {
            return Float.POSITIVE_INFINITY;
        }
        float f2 = (float) i;
        float f3 = f2 / ((float) i2);
        float f4 = f * f3;
        float f5 = 0.0f;
        for (int i4 = 0; i4 < length; i4++) {
            int i5 = iArr[i4];
            float f6 = ((float) iArr2[i4]) * f3;
            float f7 = (float) i5;
            float f8 = f7 > f6 ? f7 - f6 : f6 - f7;
            if (f8 > f4) {
                return Float.POSITIVE_INFINITY;
            }
            f5 += f8;
        }
        return f5 / f2;
    }
}
