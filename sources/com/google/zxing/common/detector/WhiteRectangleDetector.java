package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

public final class WhiteRectangleDetector {
    private static final int CORR = 1;
    private static final int INIT_SIZE = 10;
    private final int downInit;
    private final int height;
    private final BitMatrix image;
    private final int leftInit;
    private final int rightInit;
    private final int upInit;
    private final int width;

    public WhiteRectangleDetector(BitMatrix bitMatrix) throws NotFoundException {
        this(bitMatrix, 10, bitMatrix.getWidth() / 2, bitMatrix.getHeight() / 2);
    }

    public WhiteRectangleDetector(BitMatrix bitMatrix, int i, int i2, int i3) throws NotFoundException {
        this.image = bitMatrix;
        this.height = bitMatrix.getHeight();
        this.width = bitMatrix.getWidth();
        int i4 = i / 2;
        this.leftInit = i2 - i4;
        this.rightInit = i2 + i4;
        this.upInit = i3 - i4;
        this.downInit = i3 + i4;
        if (this.upInit < 0 || this.leftInit < 0 || this.downInit >= this.height || this.rightInit >= this.width) {
            throw NotFoundException.getNotFoundInstance();
        }
    }

    public ResultPoint[] detect() throws NotFoundException {
        int i = this.leftInit;
        int i2 = this.rightInit;
        int i3 = this.upInit;
        int i4 = this.downInit;
        boolean z = false;
        int i5 = i;
        boolean z2 = true;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        while (true) {
            if (!z2) {
                break;
            }
            boolean z8 = true;
            boolean z9 = false;
            while (true) {
                if ((z8 || !z3) && i2 < this.width) {
                    z8 = containsBlackPoint(i3, i4, i2, false);
                    if (z8) {
                        i2++;
                        z3 = true;
                        z9 = true;
                    } else if (!z3) {
                        i2++;
                    }
                }
            }
            if (i2 >= this.width) {
                z = true;
                break;
            }
            boolean z10 = true;
            while (true) {
                if ((z10 || !z4) && i4 < this.height) {
                    z10 = containsBlackPoint(i5, i2, i4, true);
                    if (z10) {
                        i4++;
                        z4 = true;
                        z9 = true;
                    } else if (!z4) {
                        i4++;
                    }
                }
            }
            if (i4 >= this.height) {
                z = true;
                break;
            }
            boolean z11 = true;
            while (true) {
                if ((z11 || !z5) && i5 >= 0) {
                    z11 = containsBlackPoint(i3, i4, i5, false);
                    if (z11) {
                        i5--;
                        z5 = true;
                        z9 = true;
                    } else if (!z5) {
                        i5--;
                    }
                }
            }
            if (i5 < 0) {
                z = true;
                break;
            }
            boolean z12 = true;
            while (true) {
                if ((z12 || !z7) && i3 >= 0) {
                    z12 = containsBlackPoint(i5, i2, i3, true);
                    if (z12) {
                        i3--;
                        z7 = true;
                        z9 = true;
                    } else if (!z7) {
                        i3--;
                    }
                }
            }
            if (i3 < 0) {
                z = true;
                break;
            }
            if (z9) {
                z6 = true;
            }
            z2 = z9;
        }
        if (z || !z6) {
            throw NotFoundException.getNotFoundInstance();
        }
        int i6 = i2 - i5;
        ResultPoint resultPoint = null;
        ResultPoint resultPoint2 = null;
        for (int i7 = 1; i7 < i6; i7++) {
            resultPoint2 = getBlackPointOnSegment((float) i5, (float) (i4 - i7), (float) (i5 + i7), (float) i4);
            if (resultPoint2 != null) {
                break;
            }
        }
        if (resultPoint2 != null) {
            ResultPoint resultPoint3 = null;
            for (int i8 = 1; i8 < i6; i8++) {
                resultPoint3 = getBlackPointOnSegment((float) i5, (float) (i3 + i8), (float) (i5 + i8), (float) i3);
                if (resultPoint3 != null) {
                    break;
                }
            }
            if (resultPoint3 != null) {
                ResultPoint resultPoint4 = null;
                for (int i9 = 1; i9 < i6; i9++) {
                    resultPoint4 = getBlackPointOnSegment((float) i2, (float) (i3 + i9), (float) (i2 - i9), (float) i3);
                    if (resultPoint4 != null) {
                        break;
                    }
                }
                if (resultPoint4 != null) {
                    for (int i10 = 1; i10 < i6; i10++) {
                        resultPoint = getBlackPointOnSegment((float) i2, (float) (i4 - i10), (float) (i2 - i10), (float) i4);
                        if (resultPoint != null) {
                            break;
                        }
                    }
                    if (resultPoint != null) {
                        return centerEdges(resultPoint, resultPoint2, resultPoint4, resultPoint3);
                    }
                    throw NotFoundException.getNotFoundInstance();
                }
                throw NotFoundException.getNotFoundInstance();
            }
            throw NotFoundException.getNotFoundInstance();
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private ResultPoint getBlackPointOnSegment(float f, float f2, float f3, float f4) {
        int round = MathUtils.round(MathUtils.distance(f, f2, f3, f4));
        float f5 = (float) round;
        float f6 = (f3 - f) / f5;
        float f7 = (f4 - f2) / f5;
        for (int i = 0; i < round; i++) {
            float f8 = (float) i;
            int round2 = MathUtils.round((f8 * f6) + f);
            int round3 = MathUtils.round((f8 * f7) + f2);
            if (this.image.get(round2, round3)) {
                return new ResultPoint((float) round2, (float) round3);
            }
        }
        return null;
    }

    private ResultPoint[] centerEdges(ResultPoint resultPoint, ResultPoint resultPoint2, ResultPoint resultPoint3, ResultPoint resultPoint4) {
        float x = resultPoint.getX();
        float y = resultPoint.getY();
        float x2 = resultPoint2.getX();
        float y2 = resultPoint2.getY();
        float x3 = resultPoint3.getX();
        float y3 = resultPoint3.getY();
        float x4 = resultPoint4.getX();
        float y4 = resultPoint4.getY();
        if (x < ((float) this.width) / 2.0f) {
            return new ResultPoint[]{new ResultPoint(x4 - 1.0f, y4 + 1.0f), new ResultPoint(x2 + 1.0f, y2 + 1.0f), new ResultPoint(x3 - 1.0f, y3 - 1.0f), new ResultPoint(x + 1.0f, y - 1.0f)};
        }
        return new ResultPoint[]{new ResultPoint(x4 + 1.0f, y4 + 1.0f), new ResultPoint(x2 + 1.0f, y2 - 1.0f), new ResultPoint(x3 - 1.0f, y3 + 1.0f), new ResultPoint(x - 1.0f, y - 1.0f)};
    }

    private boolean containsBlackPoint(int i, int i2, int i3, boolean z) {
        if (z) {
            while (i <= i2) {
                if (this.image.get(i, i3)) {
                    return true;
                }
                i++;
            }
            return false;
        }
        while (i <= i2) {
            if (this.image.get(i3, i)) {
                return true;
            }
            i++;
        }
        return false;
    }
}
