package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;

public final class Code39Reader extends OneDReader {
    private static final char[] ALPHABET = ALPHABET_STRING.toCharArray();
    static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%";
    private static final int ASTERISK_ENCODING = CHARACTER_ENCODINGS[39];
    static final int[] CHARACTER_ENCODINGS = {52, 289, 97, 352, 49, 304, 112, 37, 292, 100, 265, 73, 328, 25, 280, 88, 13, 268, 76, 28, 259, 67, 322, 19, 274, 82, 7, 262, 70, 22, 385, 193, 448, 145, 400, 208, 133, 388, 196, 148, 168, 162, 138, 42};
    private final int[] counters;
    private final StringBuilder decodeRowResult;
    private final boolean extendedMode;
    private final boolean usingCheckDigit;

    public Code39Reader() {
        this(false);
    }

    public Code39Reader(boolean z) {
        this(z, false);
    }

    public Code39Reader(boolean z, boolean z2) {
        this.usingCheckDigit = z;
        this.extendedMode = z2;
        this.decodeRowResult = new StringBuilder(20);
        this.counters = new int[9];
    }

    public Result decodeRow(int i, BitArray bitArray, Map<DecodeHintType, ?> map) throws NotFoundException, ChecksumException, FormatException {
        String str;
        int[] iArr = this.counters;
        Arrays.fill(iArr, 0);
        StringBuilder sb = this.decodeRowResult;
        sb.setLength(0);
        int[] findAsteriskPattern = findAsteriskPattern(bitArray, iArr);
        int nextSet = bitArray.getNextSet(findAsteriskPattern[1]);
        int size = bitArray.getSize();
        while (true) {
            recordPattern(bitArray, nextSet, iArr);
            int narrowWidePattern = toNarrowWidePattern(iArr);
            if (narrowWidePattern >= 0) {
                char patternToChar = patternToChar(narrowWidePattern);
                sb.append(patternToChar);
                int i2 = nextSet;
                for (int i3 : iArr) {
                    i2 += i3;
                }
                int nextSet2 = bitArray.getNextSet(i2);
                if (patternToChar == '*') {
                    sb.setLength(sb.length() - 1);
                    int i4 = 0;
                    for (int i5 : iArr) {
                        i4 += i5;
                    }
                    int i6 = (nextSet2 - nextSet) - i4;
                    if (nextSet2 == size || i6 * 2 >= i4) {
                        if (this.usingCheckDigit) {
                            int length = sb.length() - 1;
                            int i7 = 0;
                            for (int i8 = 0; i8 < length; i8++) {
                                i7 += ALPHABET_STRING.indexOf(this.decodeRowResult.charAt(i8));
                            }
                            if (sb.charAt(length) == ALPHABET[i7 % 43]) {
                                sb.setLength(length);
                            } else {
                                throw ChecksumException.getChecksumInstance();
                            }
                        }
                        if (sb.length() != 0) {
                            if (this.extendedMode) {
                                str = decodeExtended(sb);
                            } else {
                                str = sb.toString();
                            }
                            float f = (float) i;
                            return new Result(str, (byte[]) null, new ResultPoint[]{new ResultPoint(((float) (findAsteriskPattern[1] + findAsteriskPattern[0])) / 2.0f, f), new ResultPoint(((float) nextSet) + (((float) i4) / 2.0f), f)}, BarcodeFormat.CODE_39);
                        }
                        throw NotFoundException.getNotFoundInstance();
                    }
                    throw NotFoundException.getNotFoundInstance();
                }
                nextSet = nextSet2;
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        }
    }

    private static int[] findAsteriskPattern(BitArray bitArray, int[] iArr) throws NotFoundException {
        int size = bitArray.getSize();
        int nextSet = bitArray.getNextSet(0);
        int length = iArr.length;
        int i = nextSet;
        boolean z = false;
        int i2 = 0;
        while (nextSet < size) {
            if (bitArray.get(nextSet) ^ z) {
                iArr[i2] = iArr[i2] + 1;
            } else {
                int i3 = length - 1;
                if (i2 != i3) {
                    i2++;
                } else if (toNarrowWidePattern(iArr) != ASTERISK_ENCODING || !bitArray.isRange(Math.max(0, i - ((nextSet - i) / 2)), i, false)) {
                    i += iArr[0] + iArr[1];
                    int i4 = length - 2;
                    System.arraycopy(iArr, 2, iArr, 0, i4);
                    iArr[i4] = 0;
                    iArr[i3] = 0;
                    i2--;
                } else {
                    return new int[]{i, nextSet};
                }
                iArr[i2] = 1;
                z = !z;
            }
            nextSet++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int toNarrowWidePattern(int[] iArr) {
        int length = iArr.length;
        int i = 0;
        while (true) {
            int i2 = Integer.MAX_VALUE;
            for (int i3 : iArr) {
                if (i3 < i2 && i3 > i) {
                    i2 = i3;
                }
            }
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            for (int i7 = 0; i7 < length; i7++) {
                int i8 = iArr[i7];
                if (i8 > i2) {
                    i5 |= 1 << ((length - 1) - i7);
                    i4++;
                    i6 += i8;
                }
            }
            if (i4 == 3) {
                for (int i9 = 0; i9 < length && i4 > 0; i9++) {
                    int i10 = iArr[i9];
                    if (i10 > i2) {
                        i4--;
                        if (i10 * 2 >= i6) {
                            return -1;
                        }
                    }
                }
                return i5;
            } else if (i4 <= 3) {
                return -1;
            } else {
                i = i2;
            }
        }
    }

    private static char patternToChar(int i) throws NotFoundException {
        int i2 = 0;
        while (true) {
            int[] iArr = CHARACTER_ENCODINGS;
            if (i2 >= iArr.length) {
                throw NotFoundException.getNotFoundInstance();
            } else if (iArr[i2] == i) {
                return ALPHABET[i2];
            } else {
                i2++;
            }
        }
    }

    private static String decodeExtended(CharSequence charSequence) throws FormatException {
        char c;
        int length = charSequence.length();
        StringBuilder sb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt == '+' || charAt == '$' || charAt == '%' || charAt == '/') {
                i++;
                char charAt2 = charSequence.charAt(i);
                if (charAt != '+') {
                    if (charAt != '/') {
                        switch (charAt) {
                            case '$':
                                if (charAt2 >= 'A' && charAt2 <= 'Z') {
                                    c = (char) (charAt2 - '@');
                                    break;
                                } else {
                                    throw FormatException.getFormatInstance();
                                }
                                break;
                            case '%':
                                if (charAt2 < 'A' || charAt2 > 'E') {
                                    if (charAt2 >= 'F' && charAt2 <= 'W') {
                                        c = (char) (charAt2 - 11);
                                        break;
                                    } else {
                                        throw FormatException.getFormatInstance();
                                    }
                                } else {
                                    c = (char) (charAt2 - '&');
                                    break;
                                }
                                break;
                            default:
                                c = 0;
                                break;
                        }
                    } else if (charAt2 >= 'A' && charAt2 <= 'O') {
                        c = (char) (charAt2 - ' ');
                    } else if (charAt2 == 'Z') {
                        c = ':';
                    } else {
                        throw FormatException.getFormatInstance();
                    }
                } else if (charAt2 < 'A' || charAt2 > 'Z') {
                    throw FormatException.getFormatInstance();
                } else {
                    c = (char) (charAt2 + ' ');
                }
                sb.append(c);
            } else {
                sb.append(charAt);
            }
            i++;
        }
        return sb.toString();
    }
}
