package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;

public final class Encoder {
    public static final int DEFAULT_AZTEC_LAYERS = 0;
    public static final int DEFAULT_EC_PERCENT = 33;
    private static final int MAX_NB_BITS = 32;
    private static final int MAX_NB_BITS_COMPACT = 4;
    private static final int[] WORD_SIZE = {4, 6, 6, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};

    private static int totalBitsInLayer(int i, boolean z) {
        return ((z ? 88 : 112) + (i * 16)) * i;
    }

    private Encoder() {
    }

    public static AztecCode encode(byte[] bArr) {
        return encode(bArr, 33, 0);
    }

    public static AztecCode encode(byte[] bArr, int i, int i2) {
        int i3;
        int i4;
        int i5;
        boolean z;
        BitArray bitArray;
        int i6;
        BitArray encode = new HighLevelEncoder(bArr).encode();
        int size = ((encode.getSize() * i) / 100) + 11;
        int size2 = encode.getSize() + size;
        int i7 = 32;
        int i8 = 0;
        int i9 = 1;
        if (i2 != 0) {
            z = i2 < 0;
            i4 = Math.abs(i2);
            if (z) {
                i7 = 4;
            }
            if (i4 <= i7) {
                i5 = totalBitsInLayer(i4, z);
                i3 = WORD_SIZE[i4];
                int i10 = i5 - (i5 % i3);
                bitArray = stuffBits(encode, i3);
                if (bitArray.getSize() + size > i10) {
                    throw new IllegalArgumentException("Data to large for user specified layer");
                } else if (z && bitArray.getSize() > i3 * 64) {
                    throw new IllegalArgumentException("Data to large for user specified layer");
                }
            } else {
                throw new IllegalArgumentException(String.format("Illegal value %s for layers", new Object[]{Integer.valueOf(i2)}));
            }
        } else {
            BitArray bitArray2 = null;
            int i11 = 0;
            i3 = 0;
            while (i11 <= 32) {
                boolean z2 = i11 <= 3;
                int i12 = z2 ? i11 + 1 : i11;
                int i13 = totalBitsInLayer(i12, z2);
                if (size2 <= i13) {
                    int[] iArr = WORD_SIZE;
                    if (i3 != iArr[i12]) {
                        i3 = iArr[i12];
                        bitArray2 = stuffBits(encode, i3);
                    }
                    int i14 = i13 - (i13 % i3);
                    if ((!z2 || bitArray2.getSize() <= i3 * 64) && bitArray2.getSize() + size <= i14) {
                        bitArray = bitArray2;
                        z = z2;
                        i4 = i12;
                        i5 = i13;
                    }
                }
                i11++;
                i8 = 0;
                i9 = 1;
            }
            throw new IllegalArgumentException("Data too large for an Aztec code");
        }
        BitArray generateCheckWords = generateCheckWords(bitArray, i5, i3);
        int size3 = bitArray.getSize() / i3;
        BitArray generateModeMessage = generateModeMessage(z, i4, size3);
        int i15 = z ? (i4 * 4) + 11 : (i4 * 4) + 14;
        int[] iArr2 = new int[i15];
        int i16 = 2;
        if (z) {
            for (int i17 = 0; i17 < iArr2.length; i17++) {
                iArr2[i17] = i17;
            }
            i6 = i15;
        } else {
            int i18 = i15 / 2;
            i6 = i15 + 1 + (((i18 - 1) / 15) * 2);
            int i19 = i6 / 2;
            for (int i20 = 0; i20 < i18; i20++) {
                int i21 = (i20 / 15) + i20;
                iArr2[(i18 - i20) - 1] = (i19 - i21) - 1;
                iArr2[i18 + i20] = i21 + i19 + i9;
            }
        }
        BitMatrix bitMatrix = new BitMatrix(i6);
        int i22 = 0;
        int i23 = 0;
        while (i22 < i4) {
            int i24 = z ? ((i4 - i22) * 4) + 9 : ((i4 - i22) * 4) + 12;
            int i25 = 0;
            while (i25 < i24) {
                int i26 = i25 * 2;
                while (i8 < i16) {
                    if (generateCheckWords.get(i23 + i26 + i8)) {
                        int i27 = i22 * 2;
                        bitMatrix.set(iArr2[i27 + i8], iArr2[i27 + i25]);
                    }
                    if (generateCheckWords.get((i24 * 2) + i23 + i26 + i8)) {
                        int i28 = i22 * 2;
                        bitMatrix.set(iArr2[i28 + i25], iArr2[((i15 - 1) - i28) - i8]);
                    }
                    if (generateCheckWords.get((i24 * 4) + i23 + i26 + i8)) {
                        int i29 = (i15 - 1) - (i22 * 2);
                        bitMatrix.set(iArr2[i29 - i8], iArr2[i29 - i25]);
                    }
                    if (generateCheckWords.get((i24 * 6) + i23 + i26 + i8)) {
                        int i30 = i22 * 2;
                        bitMatrix.set(iArr2[((i15 - 1) - i30) - i25], iArr2[i30 + i8]);
                    }
                    i8++;
                    i16 = 2;
                }
                i25++;
                i8 = 0;
                i16 = 2;
            }
            i23 += i24 * 8;
            i22++;
            i8 = 0;
            i16 = 2;
        }
        drawModeMessage(bitMatrix, z, i6, generateModeMessage);
        if (z) {
            drawBullsEye(bitMatrix, i6 / 2, 5);
        } else {
            int i31 = i6 / 2;
            drawBullsEye(bitMatrix, i31, 7);
            int i32 = 0;
            int i33 = 0;
            while (i32 < (i15 / 2) - 1) {
                for (int i34 = i31 & 1; i34 < i6; i34 += 2) {
                    int i35 = i31 - i33;
                    bitMatrix.set(i35, i34);
                    int i36 = i31 + i33;
                    bitMatrix.set(i36, i34);
                    bitMatrix.set(i34, i35);
                    bitMatrix.set(i34, i36);
                }
                i32 += 15;
                i33 += 16;
            }
        }
        AztecCode aztecCode = new AztecCode();
        aztecCode.setCompact(z);
        aztecCode.setSize(i6);
        aztecCode.setLayers(i4);
        aztecCode.setCodeWords(size3);
        aztecCode.setMatrix(bitMatrix);
        return aztecCode;
    }

    private static void drawBullsEye(BitMatrix bitMatrix, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3 += 2) {
            int i4 = i - i3;
            int i5 = i4;
            while (true) {
                int i6 = i + i3;
                if (i5 > i6) {
                    break;
                }
                bitMatrix.set(i5, i4);
                bitMatrix.set(i5, i6);
                bitMatrix.set(i4, i5);
                bitMatrix.set(i6, i5);
                i5++;
            }
        }
        int i7 = i - i2;
        bitMatrix.set(i7, i7);
        int i8 = i7 + 1;
        bitMatrix.set(i8, i7);
        bitMatrix.set(i7, i8);
        int i9 = i + i2;
        bitMatrix.set(i9, i7);
        bitMatrix.set(i9, i8);
        bitMatrix.set(i9, i9 - 1);
    }

    static BitArray generateModeMessage(boolean z, int i, int i2) {
        BitArray bitArray = new BitArray();
        if (z) {
            bitArray.appendBits(i - 1, 2);
            bitArray.appendBits(i2 - 1, 6);
            return generateCheckWords(bitArray, 28, 4);
        }
        bitArray.appendBits(i - 1, 5);
        bitArray.appendBits(i2 - 1, 11);
        return generateCheckWords(bitArray, 40, 4);
    }

    private static void drawModeMessage(BitMatrix bitMatrix, boolean z, int i, BitArray bitArray) {
        int i2 = i / 2;
        int i3 = 0;
        if (z) {
            while (i3 < 7) {
                int i4 = (i2 - 3) + i3;
                if (bitArray.get(i3)) {
                    bitMatrix.set(i4, i2 - 5);
                }
                if (bitArray.get(i3 + 7)) {
                    bitMatrix.set(i2 + 5, i4);
                }
                if (bitArray.get(20 - i3)) {
                    bitMatrix.set(i4, i2 + 5);
                }
                if (bitArray.get(27 - i3)) {
                    bitMatrix.set(i2 - 5, i4);
                }
                i3++;
            }
            return;
        }
        while (i3 < 10) {
            int i5 = (i2 - 5) + i3 + (i3 / 5);
            if (bitArray.get(i3)) {
                bitMatrix.set(i5, i2 - 7);
            }
            if (bitArray.get(i3 + 10)) {
                bitMatrix.set(i2 + 7, i5);
            }
            if (bitArray.get(29 - i3)) {
                bitMatrix.set(i5, i2 + 7);
            }
            if (bitArray.get(39 - i3)) {
                bitMatrix.set(i2 - 7, i5);
            }
            i3++;
        }
    }

    private static BitArray generateCheckWords(BitArray bitArray, int i, int i2) {
        ReedSolomonEncoder reedSolomonEncoder = new ReedSolomonEncoder(getGF(i2));
        int i3 = i / i2;
        int[] bitsToWords = bitsToWords(bitArray, i2, i3);
        reedSolomonEncoder.encode(bitsToWords, i3 - (bitArray.getSize() / i2));
        BitArray bitArray2 = new BitArray();
        bitArray2.appendBits(0, i % i2);
        for (int appendBits : bitsToWords) {
            bitArray2.appendBits(appendBits, i2);
        }
        return bitArray2;
    }

    private static int[] bitsToWords(BitArray bitArray, int i, int i2) {
        int[] iArr = new int[i2];
        int size = bitArray.getSize() / i;
        for (int i3 = 0; i3 < size; i3++) {
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                i4 |= bitArray.get((i3 * i) + i5) ? 1 << ((i - i5) - 1) : 0;
            }
            iArr[i3] = i4;
        }
        return iArr;
    }

    private static GenericGF getGF(int i) {
        if (i == 4) {
            return GenericGF.AZTEC_PARAM;
        }
        if (i == 6) {
            return GenericGF.AZTEC_DATA_6;
        }
        if (i == 8) {
            return GenericGF.AZTEC_DATA_8;
        }
        if (i == 10) {
            return GenericGF.AZTEC_DATA_10;
        }
        if (i == 12) {
            return GenericGF.AZTEC_DATA_12;
        }
        throw new IllegalArgumentException("Unsupported word size " + i);
    }

    static BitArray stuffBits(BitArray bitArray, int i) {
        BitArray bitArray2 = new BitArray();
        int size = bitArray.getSize();
        int i2 = (1 << i) - 2;
        int i3 = 0;
        while (i3 < size) {
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = i3 + i5;
                if (i6 >= size || bitArray.get(i6)) {
                    i4 |= 1 << ((i - 1) - i5);
                }
            }
            int i7 = i4 & i2;
            if (i7 == i2) {
                bitArray2.appendBits(i7, i);
                i3--;
            } else if (i7 == 0) {
                bitArray2.appendBits(i4 | 1, i);
                i3--;
            } else {
                bitArray2.appendBits(i4, i);
            }
            i3 += i;
        }
        return bitArray2;
    }
}
