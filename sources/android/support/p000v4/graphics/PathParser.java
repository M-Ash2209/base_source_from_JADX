package android.support.p000v4.graphics;

import android.graphics.Path;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* renamed from: android.support.v4.graphics.PathParser */
public class PathParser {
    private static final String LOGTAG = "PathParser";

    static float[] copyOfRange(float[] fArr, int i, int i2) {
        if (i <= i2) {
            int length = fArr.length;
            if (i < 0 || i > length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int i3 = i2 - i;
            int min = Math.min(i3, length - i);
            float[] fArr2 = new float[i3];
            System.arraycopy(fArr, i, fArr2, 0, min);
            return fArr2;
        }
        throw new IllegalArgumentException();
    }

    public static Path createPathFromPathData(String str) {
        Path path = new Path();
        PathDataNode[] createNodesFromPathData = createNodesFromPathData(str);
        if (createNodesFromPathData == null) {
            return null;
        }
        try {
            PathDataNode.nodesToPath(createNodesFromPathData, path);
            return path;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error in parsing " + str, e);
        }
    }

    public static PathDataNode[] createNodesFromPathData(String str) {
        if (str == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int i = 1;
        int i2 = 0;
        while (i < str.length()) {
            int nextStart = nextStart(str, i);
            String trim = str.substring(i2, nextStart).trim();
            if (trim.length() > 0) {
                addNode(arrayList, trim.charAt(0), getFloats(trim));
            }
            i2 = nextStart;
            i = nextStart + 1;
        }
        if (i - i2 == 1 && i2 < str.length()) {
            addNode(arrayList, str.charAt(i2), new float[0]);
        }
        return (PathDataNode[]) arrayList.toArray(new PathDataNode[arrayList.size()]);
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] pathDataNodeArr) {
        if (pathDataNodeArr == null) {
            return null;
        }
        PathDataNode[] pathDataNodeArr2 = new PathDataNode[pathDataNodeArr.length];
        for (int i = 0; i < pathDataNodeArr.length; i++) {
            pathDataNodeArr2[i] = new PathDataNode(pathDataNodeArr[i]);
        }
        return pathDataNodeArr2;
    }

    public static boolean canMorph(PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
        if (pathDataNodeArr == null || pathDataNodeArr2 == null || pathDataNodeArr.length != pathDataNodeArr2.length) {
            return false;
        }
        for (int i = 0; i < pathDataNodeArr.length; i++) {
            if (pathDataNodeArr[i].mType != pathDataNodeArr2[i].mType || pathDataNodeArr[i].mParams.length != pathDataNodeArr2[i].mParams.length) {
                return false;
            }
        }
        return true;
    }

    public static void updateNodes(PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
        for (int i = 0; i < pathDataNodeArr2.length; i++) {
            pathDataNodeArr[i].mType = pathDataNodeArr2[i].mType;
            for (int i2 = 0; i2 < pathDataNodeArr2[i].mParams.length; i2++) {
                pathDataNodeArr[i].mParams[i2] = pathDataNodeArr2[i].mParams[i2];
            }
        }
    }

    private static int nextStart(String str, int i) {
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (((charAt - 'A') * (charAt - 'Z') <= 0 || (charAt - 'a') * (charAt - 'z') <= 0) && charAt != 'e' && charAt != 'E') {
                return i;
            }
            i++;
        }
        return i;
    }

    private static void addNode(ArrayList<PathDataNode> arrayList, char c, float[] fArr) {
        arrayList.add(new PathDataNode(c, fArr));
    }

    /* renamed from: android.support.v4.graphics.PathParser$ExtractFloatResult */
    private static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    private static float[] getFloats(String str) {
        if (str.charAt(0) == 'z' || str.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            float[] fArr = new float[str.length()];
            ExtractFloatResult extractFloatResult = new ExtractFloatResult();
            int length = str.length();
            int i = 1;
            int i2 = 0;
            while (i < length) {
                extract(str, i, extractFloatResult);
                int i3 = extractFloatResult.mEndPosition;
                if (i < i3) {
                    fArr[i2] = Float.parseFloat(str.substring(i, i3));
                    i2++;
                }
                i = extractFloatResult.mEndWithNegOrDot ? i3 : i3 + 1;
            }
            return copyOfRange(fArr, 0, i2);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + str + "\"", e);
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x003e A[LOOP:0: B:1:0x0007->B:20:0x003e, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0041 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void extract(java.lang.String r8, int r9, android.support.p000v4.graphics.PathParser.ExtractFloatResult r10) {
        /*
            r0 = 0
            r10.mEndWithNegOrDot = r0
            r1 = r9
            r2 = 0
            r3 = 0
            r4 = 0
        L_0x0007:
            int r5 = r8.length()
            if (r1 >= r5) goto L_0x0041
            char r5 = r8.charAt(r1)
            r6 = 32
            r7 = 1
            if (r5 == r6) goto L_0x0039
            r6 = 69
            if (r5 == r6) goto L_0x0037
            r6 = 101(0x65, float:1.42E-43)
            if (r5 == r6) goto L_0x0037
            switch(r5) {
                case 44: goto L_0x0039;
                case 45: goto L_0x002c;
                case 46: goto L_0x0022;
                default: goto L_0x0021;
            }
        L_0x0021:
            goto L_0x0035
        L_0x0022:
            if (r3 != 0) goto L_0x0027
            r2 = 0
            r3 = 1
            goto L_0x003b
        L_0x0027:
            r10.mEndWithNegOrDot = r7
            r2 = 0
            r4 = 1
            goto L_0x003b
        L_0x002c:
            if (r1 == r9) goto L_0x0035
            if (r2 != 0) goto L_0x0035
            r10.mEndWithNegOrDot = r7
            r2 = 0
            r4 = 1
            goto L_0x003b
        L_0x0035:
            r2 = 0
            goto L_0x003b
        L_0x0037:
            r2 = 1
            goto L_0x003b
        L_0x0039:
            r2 = 0
            r4 = 1
        L_0x003b:
            if (r4 == 0) goto L_0x003e
            goto L_0x0041
        L_0x003e:
            int r1 = r1 + 1
            goto L_0x0007
        L_0x0041:
            r10.mEndPosition = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.graphics.PathParser.extract(java.lang.String, int, android.support.v4.graphics.PathParser$ExtractFloatResult):void");
    }

    /* renamed from: android.support.v4.graphics.PathParser$PathDataNode */
    public static class PathDataNode {
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public float[] mParams;
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public char mType;

        PathDataNode(char c, float[] fArr) {
            this.mType = c;
            this.mParams = fArr;
        }

        PathDataNode(PathDataNode pathDataNode) {
            this.mType = pathDataNode.mType;
            float[] fArr = pathDataNode.mParams;
            this.mParams = PathParser.copyOfRange(fArr, 0, fArr.length);
        }

        public static void nodesToPath(PathDataNode[] pathDataNodeArr, Path path) {
            float[] fArr = new float[6];
            char c = 'm';
            for (int i = 0; i < pathDataNodeArr.length; i++) {
                addCommand(path, fArr, c, pathDataNodeArr[i].mType, pathDataNodeArr[i].mParams);
                c = pathDataNodeArr[i].mType;
            }
        }

        public void interpolatePathDataNode(PathDataNode pathDataNode, PathDataNode pathDataNode2, float f) {
            int i = 0;
            while (true) {
                float[] fArr = pathDataNode.mParams;
                if (i < fArr.length) {
                    this.mParams[i] = (fArr[i] * (1.0f - f)) + (pathDataNode2.mParams[i] * f);
                    i++;
                } else {
                    return;
                }
            }
        }

        private static void addCommand(Path path, float[] fArr, char c, char c2, float[] fArr2) {
            int i;
            int i2;
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            Path path2 = path;
            float[] fArr3 = fArr2;
            float f6 = fArr[0];
            float f7 = fArr[1];
            float f8 = fArr[2];
            float f9 = fArr[3];
            float f10 = fArr[4];
            float f11 = fArr[5];
            switch (c2) {
                case 'A':
                case 'a':
                    i = 7;
                    break;
                case 'C':
                case 'c':
                    i = 6;
                    break;
                case 'H':
                case 'V':
                case 'h':
                case 'v':
                    i = 1;
                    break;
                case 'L':
                case 'M':
                case 'T':
                case 'l':
                case 'm':
                case 't':
                    i = 2;
                    break;
                case 'Q':
                case 'S':
                case 'q':
                case 's':
                    i = 4;
                    break;
                case 'Z':
                case 'z':
                    path.close();
                    path2.moveTo(f10, f11);
                    f6 = f10;
                    f8 = f6;
                    f7 = f11;
                    f9 = f7;
                    i = 2;
                    break;
                default:
                    i = 2;
                    break;
            }
            float f12 = f6;
            float f13 = f7;
            float f14 = f10;
            float f15 = f11;
            int i3 = 0;
            char c3 = c;
            while (i3 < fArr3.length) {
                float f16 = 0.0f;
                switch (c2) {
                    case 'A':
                        i2 = i3;
                        int i4 = i2 + 5;
                        int i5 = i2 + 6;
                        drawArc(path, f12, f13, fArr3[i4], fArr3[i5], fArr3[i2 + 0], fArr3[i2 + 1], fArr3[i2 + 2], fArr3[i2 + 3] != 0.0f, fArr3[i2 + 4] != 0.0f);
                        f12 = fArr3[i4];
                        f13 = fArr3[i5];
                        f9 = f13;
                        f8 = f12;
                        break;
                    case 'C':
                        i2 = i3;
                        int i6 = i2 + 2;
                        int i7 = i2 + 3;
                        int i8 = i2 + 4;
                        int i9 = i2 + 5;
                        path.cubicTo(fArr3[i2 + 0], fArr3[i2 + 1], fArr3[i6], fArr3[i7], fArr3[i8], fArr3[i9]);
                        f12 = fArr3[i8];
                        float f17 = fArr3[i9];
                        float f18 = fArr3[i6];
                        float f19 = fArr3[i7];
                        f13 = f17;
                        f9 = f19;
                        f8 = f18;
                        break;
                    case 'H':
                        i2 = i3;
                        int i10 = i2 + 0;
                        path2.lineTo(fArr3[i10], f13);
                        f12 = fArr3[i10];
                        break;
                    case 'L':
                        i2 = i3;
                        int i11 = i2 + 0;
                        int i12 = i2 + 1;
                        path2.lineTo(fArr3[i11], fArr3[i12]);
                        f12 = fArr3[i11];
                        f13 = fArr3[i12];
                        break;
                    case 'M':
                        i2 = i3;
                        int i13 = i2 + 0;
                        f12 = fArr3[i13];
                        int i14 = i2 + 1;
                        f13 = fArr3[i14];
                        if (i2 <= 0) {
                            path2.moveTo(fArr3[i13], fArr3[i14]);
                            f15 = f13;
                            f14 = f12;
                            break;
                        } else {
                            path2.lineTo(fArr3[i13], fArr3[i14]);
                            break;
                        }
                    case 'Q':
                        i2 = i3;
                        int i15 = i2 + 0;
                        int i16 = i2 + 1;
                        int i17 = i2 + 2;
                        int i18 = i2 + 3;
                        path2.quadTo(fArr3[i15], fArr3[i16], fArr3[i17], fArr3[i18]);
                        float f20 = fArr3[i15];
                        float f21 = fArr3[i16];
                        f12 = fArr3[i17];
                        f13 = fArr3[i18];
                        f8 = f20;
                        f9 = f21;
                        break;
                    case 'S':
                        float f22 = f13;
                        float f23 = f12;
                        i2 = i3;
                        if (c3 == 'c' || c3 == 's' || c3 == 'C' || c3 == 'S') {
                            float f24 = (f23 * 2.0f) - f8;
                            f = (f22 * 2.0f) - f9;
                            f2 = f24;
                        } else {
                            f2 = f23;
                            f = f22;
                        }
                        int i19 = i2 + 0;
                        int i20 = i2 + 1;
                        int i21 = i2 + 2;
                        int i22 = i2 + 3;
                        path.cubicTo(f2, f, fArr3[i19], fArr3[i20], fArr3[i21], fArr3[i22]);
                        float f25 = fArr3[i19];
                        float f26 = fArr3[i20];
                        f12 = fArr3[i21];
                        f13 = fArr3[i22];
                        f8 = f25;
                        f9 = f26;
                        break;
                    case 'T':
                        float f27 = f13;
                        float f28 = f12;
                        i2 = i3;
                        if (c3 == 'q' || c3 == 't' || c3 == 'Q' || c3 == 'T') {
                            f27 = (f27 * 2.0f) - f9;
                            f28 = (f28 * 2.0f) - f8;
                        }
                        int i23 = i2 + 0;
                        int i24 = i2 + 1;
                        path2.quadTo(f28, f27, fArr3[i23], fArr3[i24]);
                        f12 = fArr3[i23];
                        f8 = f28;
                        f9 = f27;
                        f13 = fArr3[i24];
                        break;
                    case 'V':
                        i2 = i3;
                        int i25 = i2 + 0;
                        float f29 = f12;
                        path2 = path;
                        path2.lineTo(f29, fArr3[i25]);
                        f12 = f29;
                        f13 = fArr3[i25];
                        break;
                    case 'a':
                        int i26 = i3 + 5;
                        float f30 = fArr3[i26] + f12;
                        int i27 = i3 + 6;
                        float f31 = fArr3[i27] + f13;
                        float f32 = fArr3[i3 + 0];
                        float f33 = fArr3[i3 + 1];
                        float f34 = fArr3[i3 + 2];
                        float f35 = f12;
                        boolean z = fArr3[i3 + 3] != 0.0f;
                        i2 = i3;
                        drawArc(path, f12, f13, f30, f31, f32, f33, f34, z, fArr3[i3 + 4] != 0.0f);
                        f12 = f35 + fArr3[i26];
                        f13 += fArr3[i27];
                        f9 = f13;
                        f8 = f12;
                        path2 = path;
                        break;
                    case 'c':
                        int i28 = i3 + 2;
                        int i29 = i3 + 3;
                        int i30 = i3 + 4;
                        int i31 = i3 + 5;
                        path.rCubicTo(fArr3[i3 + 0], fArr3[i3 + 1], fArr3[i28], fArr3[i29], fArr3[i30], fArr3[i31]);
                        float f36 = fArr3[i28] + f12;
                        float f37 = fArr3[i29] + f13;
                        f12 += fArr3[i30];
                        f13 += fArr3[i31];
                        f8 = f36;
                        f9 = f37;
                        i2 = i3;
                        break;
                    case 'h':
                        int i32 = i3 + 0;
                        path2.rLineTo(fArr3[i32], 0.0f);
                        f12 += fArr3[i32];
                        i2 = i3;
                        break;
                    case 'l':
                        int i33 = i3 + 0;
                        int i34 = i3 + 1;
                        path2.rLineTo(fArr3[i33], fArr3[i34]);
                        f12 += fArr3[i33];
                        f13 += fArr3[i34];
                        i2 = i3;
                        break;
                    case 'm':
                        int i35 = i3 + 0;
                        f12 += fArr3[i35];
                        int i36 = i3 + 1;
                        f13 += fArr3[i36];
                        if (i3 <= 0) {
                            path2.rMoveTo(fArr3[i35], fArr3[i36]);
                            f15 = f13;
                            f14 = f12;
                            i2 = i3;
                            break;
                        } else {
                            path2.rLineTo(fArr3[i35], fArr3[i36]);
                            i2 = i3;
                            break;
                        }
                    case 'q':
                        int i37 = i3 + 0;
                        int i38 = i3 + 1;
                        int i39 = i3 + 2;
                        int i40 = i3 + 3;
                        path2.rQuadTo(fArr3[i37], fArr3[i38], fArr3[i39], fArr3[i40]);
                        float f38 = fArr3[i37] + f12;
                        float f39 = fArr3[i38] + f13;
                        f12 += fArr3[i39];
                        f13 += fArr3[i40];
                        f8 = f38;
                        f9 = f39;
                        i2 = i3;
                        break;
                    case 's':
                        if (c3 == 'c' || c3 == 's' || c3 == 'C' || c3 == 'S') {
                            float f40 = f12 - f8;
                            f3 = f13 - f9;
                            f4 = f40;
                        } else {
                            f4 = 0.0f;
                            f3 = 0.0f;
                        }
                        int i41 = i3 + 0;
                        int i42 = i3 + 1;
                        int i43 = i3 + 2;
                        int i44 = i3 + 3;
                        path.rCubicTo(f4, f3, fArr3[i41], fArr3[i42], fArr3[i43], fArr3[i44]);
                        float f41 = fArr3[i41] + f12;
                        float f42 = fArr3[i42] + f13;
                        f12 += fArr3[i43];
                        f13 += fArr3[i44];
                        f8 = f41;
                        f9 = f42;
                        i2 = i3;
                        break;
                    case 't':
                        if (c3 == 'q' || c3 == 't' || c3 == 'Q' || c3 == 'T') {
                            f16 = f12 - f8;
                            f5 = f13 - f9;
                        } else {
                            f5 = 0.0f;
                        }
                        int i45 = i3 + 0;
                        int i46 = i3 + 1;
                        path2.rQuadTo(f16, f5, fArr3[i45], fArr3[i46]);
                        float f43 = f16 + f12;
                        float f44 = f5 + f13;
                        f12 += fArr3[i45];
                        f13 += fArr3[i46];
                        f9 = f44;
                        i2 = i3;
                        f8 = f43;
                        break;
                    case 'v':
                        int i47 = i3 + 0;
                        path2.rLineTo(0.0f, fArr3[i47]);
                        f13 += fArr3[i47];
                        i2 = i3;
                        break;
                    default:
                        float f45 = f13;
                        float f46 = f12;
                        i2 = i3;
                        f13 = f45;
                        break;
                }
                i3 = i2 + i;
                c3 = c2;
            }
            fArr[0] = f12;
            fArr[1] = f13;
            fArr[2] = f8;
            fArr[3] = f9;
            fArr[4] = f14;
            fArr[5] = f15;
        }

        private static void drawArc(Path path, float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean z, boolean z2) {
            double d;
            double d2;
            float f8 = f;
            float f9 = f3;
            float f10 = f5;
            boolean z3 = z2;
            double radians = Math.toRadians((double) f7);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double d3 = (double) f8;
            Double.isNaN(d3);
            double d4 = d3 * cos;
            double d5 = d3;
            double d6 = (double) f2;
            Double.isNaN(d6);
            double d7 = (double) f10;
            Double.isNaN(d7);
            double d8 = (d4 + (d6 * sin)) / d7;
            double d9 = (double) (-f8);
            Double.isNaN(d9);
            Double.isNaN(d6);
            double d10 = (d9 * sin) + (d6 * cos);
            double d11 = d6;
            double d12 = (double) f6;
            Double.isNaN(d12);
            double d13 = (double) f9;
            Double.isNaN(d13);
            double d14 = d10 / d12;
            double d15 = (double) f4;
            Double.isNaN(d15);
            Double.isNaN(d7);
            double d16 = ((d13 * cos) + (d15 * sin)) / d7;
            double d17 = d7;
            double d18 = (double) (-f9);
            Double.isNaN(d18);
            Double.isNaN(d15);
            Double.isNaN(d12);
            double d19 = ((d18 * sin) + (d15 * cos)) / d12;
            double d20 = d8 - d16;
            double d21 = d14 - d19;
            double d22 = (d8 + d16) / 2.0d;
            double d23 = (d14 + d19) / 2.0d;
            double d24 = sin;
            double d25 = (d20 * d20) + (d21 * d21);
            if (d25 == 0.0d) {
                Log.w(PathParser.LOGTAG, " Points are coincident");
                return;
            }
            double d26 = (1.0d / d25) - 0.25d;
            if (d26 < 0.0d) {
                Log.w(PathParser.LOGTAG, "Points are too far apart " + d25);
                float sqrt = (float) (Math.sqrt(d25) / 1.99999d);
                drawArc(path, f, f2, f3, f4, f10 * sqrt, f6 * sqrt, f7, z, z2);
                return;
            }
            double sqrt2 = Math.sqrt(d26);
            double d27 = d20 * sqrt2;
            double d28 = sqrt2 * d21;
            boolean z4 = z2;
            if (z == z4) {
                d2 = d22 - d28;
                d = d23 + d27;
            } else {
                d2 = d22 + d28;
                d = d23 - d27;
            }
            double atan2 = Math.atan2(d14 - d, d8 - d2);
            double atan22 = Math.atan2(d19 - d, d16 - d2) - atan2;
            if (z4 != (atan22 >= 0.0d)) {
                atan22 = atan22 > 0.0d ? atan22 - 6.283185307179586d : atan22 + 6.283185307179586d;
            }
            Double.isNaN(d17);
            double d29 = d2 * d17;
            Double.isNaN(d12);
            double d30 = d * d12;
            arcToBezier(path, (d29 * cos) - (d30 * d24), (d29 * d24) + (d30 * cos), d17, d12, d5, d11, radians, atan2, atan22);
        }

        private static void arcToBezier(Path path, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
            double d10 = d3;
            int ceil = (int) Math.ceil(Math.abs((d9 * 4.0d) / 3.141592653589793d));
            double cos = Math.cos(d7);
            double sin = Math.sin(d7);
            double cos2 = Math.cos(d8);
            double sin2 = Math.sin(d8);
            double d11 = -d10;
            double d12 = d11 * cos;
            double d13 = d4 * sin;
            double d14 = (d12 * sin2) - (d13 * cos2);
            double d15 = d11 * sin;
            double d16 = d4 * cos;
            double d17 = (sin2 * d15) + (cos2 * d16);
            double d18 = (double) ceil;
            Double.isNaN(d18);
            double d19 = d9 / d18;
            double d20 = d5;
            double d21 = d6;
            double d22 = d17;
            double d23 = d14;
            int i = 0;
            double d24 = d8;
            while (i < ceil) {
                double d25 = d24 + d19;
                double sin3 = Math.sin(d25);
                double cos3 = Math.cos(d25);
                double d26 = (d + ((d10 * cos) * cos3)) - (d13 * sin3);
                double d27 = d2 + (d10 * sin * cos3) + (d16 * sin3);
                double d28 = (d12 * sin3) - (d13 * cos3);
                double d29 = (sin3 * d15) + (cos3 * d16);
                double d30 = d25 - d24;
                double tan = Math.tan(d30 / 2.0d);
                double sin4 = (Math.sin(d30) * (Math.sqrt(((tan * 3.0d) * tan) + 4.0d) - 1.0d)) / 3.0d;
                int i2 = ceil;
                double d31 = cos;
                path.rLineTo(0.0f, 0.0f);
                float f = (float) (d21 + (d22 * sin4));
                float f2 = (float) (d26 - (sin4 * d28));
                path.cubicTo((float) (d20 + (d23 * sin4)), f, f2, (float) (d27 - (sin4 * d29)), (float) d26, (float) d27);
                i++;
                d19 = d19;
                ceil = i2;
                sin = sin;
                d21 = d27;
                d15 = d15;
                d24 = d25;
                d22 = d29;
                d23 = d28;
                cos = d31;
                d10 = d3;
                d20 = d26;
            }
        }
    }
}
