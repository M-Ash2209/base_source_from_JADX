package okhttp3.internal.tls;

import javax.security.auth.x500.X500Principal;

final class DistinguishedNameParser {
    private int beg;
    private char[] chars;
    private int cur;

    /* renamed from: dn */
    private final String f55dn;
    private int end;
    private final int length = this.f55dn.length();
    private int pos;

    DistinguishedNameParser(X500Principal x500Principal) {
        this.f55dn = x500Principal.getName("RFC2253");
    }

    private String nextAT() {
        while (true) {
            int i = this.pos;
            if (i >= this.length || this.chars[i] != ' ') {
                int i2 = this.pos;
            } else {
                this.pos = i + 1;
            }
        }
        int i22 = this.pos;
        if (i22 == this.length) {
            return null;
        }
        this.beg = i22;
        this.pos = i22 + 1;
        while (true) {
            int i3 = this.pos;
            if (i3 >= this.length) {
                break;
            }
            char[] cArr = this.chars;
            if (cArr[i3] == '=' || cArr[i3] == ' ') {
                break;
            }
            this.pos = i3 + 1;
        }
        int i4 = this.pos;
        if (i4 < this.length) {
            this.end = i4;
            if (this.chars[i4] == ' ') {
                while (true) {
                    int i5 = this.pos;
                    if (i5 >= this.length) {
                        break;
                    }
                    char[] cArr2 = this.chars;
                    if (cArr2[i5] == '=' || cArr2[i5] != ' ') {
                        break;
                    }
                    this.pos = i5 + 1;
                }
                char[] cArr3 = this.chars;
                int i6 = this.pos;
                if (cArr3[i6] != '=' || i6 == this.length) {
                    throw new IllegalStateException("Unexpected end of DN: " + this.f55dn);
                }
            }
            this.pos++;
            while (true) {
                int i7 = this.pos;
                if (i7 >= this.length || this.chars[i7] != ' ') {
                    int i8 = this.end;
                    int i9 = this.beg;
                } else {
                    this.pos = i7 + 1;
                }
            }
            int i82 = this.end;
            int i92 = this.beg;
            if (i82 - i92 > 4) {
                char[] cArr4 = this.chars;
                if (cArr4[i92 + 3] == '.' && (cArr4[i92] == 'O' || cArr4[i92] == 'o')) {
                    char[] cArr5 = this.chars;
                    int i10 = this.beg;
                    if (cArr5[i10 + 1] == 'I' || cArr5[i10 + 1] == 'i') {
                        char[] cArr6 = this.chars;
                        int i11 = this.beg;
                        if (cArr6[i11 + 2] == 'D' || cArr6[i11 + 2] == 'd') {
                            this.beg += 4;
                        }
                    }
                }
            }
            char[] cArr7 = this.chars;
            int i12 = this.beg;
            return new String(cArr7, i12, this.end - i12);
        }
        throw new IllegalStateException("Unexpected end of DN: " + this.f55dn);
    }

    private String quotedAV() {
        this.pos++;
        this.beg = this.pos;
        this.end = this.beg;
        while (true) {
            int i = this.pos;
            if (i != this.length) {
                char[] cArr = this.chars;
                if (cArr[i] == '\"') {
                    this.pos = i + 1;
                    while (true) {
                        int i2 = this.pos;
                        if (i2 >= this.length || this.chars[i2] != ' ') {
                            char[] cArr2 = this.chars;
                            int i3 = this.beg;
                        } else {
                            this.pos = i2 + 1;
                        }
                    }
                    char[] cArr22 = this.chars;
                    int i32 = this.beg;
                    return new String(cArr22, i32, this.end - i32);
                }
                if (cArr[i] == '\\') {
                    cArr[this.end] = getEscaped();
                } else {
                    cArr[this.end] = cArr[i];
                }
                this.pos++;
                this.end++;
            } else {
                throw new IllegalStateException("Unexpected end of DN: " + this.f55dn);
            }
        }
    }

    private String hexAV() {
        int i = this.pos;
        if (i + 4 < this.length) {
            this.beg = i;
            this.pos = i + 1;
            while (true) {
                int i2 = this.pos;
                if (i2 == this.length) {
                    break;
                }
                char[] cArr = this.chars;
                if (cArr[i2] == '+' || cArr[i2] == ',' || cArr[i2] == ';') {
                    break;
                } else if (cArr[i2] == ' ') {
                    this.end = i2;
                    this.pos = i2 + 1;
                    while (true) {
                        int i3 = this.pos;
                        if (i3 >= this.length || this.chars[i3] != ' ') {
                            break;
                        }
                        this.pos = i3 + 1;
                    }
                } else {
                    if (cArr[i2] >= 'A' && cArr[i2] <= 'F') {
                        cArr[i2] = (char) (cArr[i2] + ' ');
                    }
                    this.pos++;
                }
            }
            this.end = this.pos;
            int i4 = this.end;
            int i5 = this.beg;
            int i6 = i4 - i5;
            if (i6 < 5 || (i6 & 1) == 0) {
                throw new IllegalStateException("Unexpected end of DN: " + this.f55dn);
            }
            byte[] bArr = new byte[(i6 / 2)];
            int i7 = i5 + 1;
            for (int i8 = 0; i8 < bArr.length; i8++) {
                bArr[i8] = (byte) getByte(i7);
                i7 += 2;
            }
            return new String(this.chars, this.beg, i6);
        }
        throw new IllegalStateException("Unexpected end of DN: " + this.f55dn);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x009a, code lost:
        r1 = r6.chars;
        r2 = r6.beg;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a6, code lost:
        return new java.lang.String(r1, r2, r6.cur - r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String escapedAV() {
        /*
            r6 = this;
            int r0 = r6.pos
            r6.beg = r0
            r6.end = r0
        L_0x0006:
            int r0 = r6.pos
            int r1 = r6.length
            if (r0 < r1) goto L_0x0019
            java.lang.String r0 = new java.lang.String
            char[] r1 = r6.chars
            int r2 = r6.beg
            int r3 = r6.end
            int r3 = r3 - r2
            r0.<init>(r1, r2, r3)
            return r0
        L_0x0019:
            char[] r1 = r6.chars
            char r2 = r1[r0]
            r3 = 59
            r4 = 32
            if (r2 == r4) goto L_0x005b
            if (r2 == r3) goto L_0x004e
            r3 = 92
            if (r2 == r3) goto L_0x003b
            switch(r2) {
                case 43: goto L_0x004e;
                case 44: goto L_0x004e;
                default: goto L_0x002c;
            }
        L_0x002c:
            int r2 = r6.end
            int r3 = r2 + 1
            r6.end = r3
            char r3 = r1[r0]
            r1[r2] = r3
            int r0 = r0 + 1
            r6.pos = r0
            goto L_0x0006
        L_0x003b:
            int r0 = r6.end
            int r2 = r0 + 1
            r6.end = r2
            char r2 = r6.getEscaped()
            r1[r0] = r2
            int r0 = r6.pos
            int r0 = r0 + 1
            r6.pos = r0
            goto L_0x0006
        L_0x004e:
            java.lang.String r0 = new java.lang.String
            char[] r1 = r6.chars
            int r2 = r6.beg
            int r3 = r6.end
            int r3 = r3 - r2
            r0.<init>(r1, r2, r3)
            return r0
        L_0x005b:
            int r2 = r6.end
            r6.cur = r2
            int r0 = r0 + 1
            r6.pos = r0
            int r0 = r2 + 1
            r6.end = r0
            r1[r2] = r4
        L_0x0069:
            int r0 = r6.pos
            int r1 = r6.length
            if (r0 >= r1) goto L_0x0082
            char[] r1 = r6.chars
            char r2 = r1[r0]
            if (r2 != r4) goto L_0x0082
            int r2 = r6.end
            int r5 = r2 + 1
            r6.end = r5
            r1[r2] = r4
            int r0 = r0 + 1
            r6.pos = r0
            goto L_0x0069
        L_0x0082:
            int r0 = r6.pos
            int r1 = r6.length
            if (r0 == r1) goto L_0x009a
            char[] r1 = r6.chars
            char r2 = r1[r0]
            r4 = 44
            if (r2 == r4) goto L_0x009a
            char r2 = r1[r0]
            r4 = 43
            if (r2 == r4) goto L_0x009a
            char r0 = r1[r0]
            if (r0 != r3) goto L_0x0006
        L_0x009a:
            java.lang.String r0 = new java.lang.String
            char[] r1 = r6.chars
            int r2 = r6.beg
            int r3 = r6.cur
            int r3 = r3 - r2
            r0.<init>(r1, r2, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.tls.DistinguishedNameParser.escapedAV():java.lang.String");
    }

    private char getEscaped() {
        this.pos++;
        int i = this.pos;
        if (i != this.length) {
            char c = this.chars[i];
            if (!(c == ' ' || c == '%' || c == '\\' || c == '_')) {
                switch (c) {
                    case '\"':
                    case '#':
                        break;
                    default:
                        switch (c) {
                            case '*':
                            case '+':
                            case ',':
                                break;
                            default:
                                switch (c) {
                                    case ';':
                                    case '<':
                                    case '=':
                                    case '>':
                                        break;
                                    default:
                                        return getUTF8();
                                }
                        }
                }
            }
            return this.chars[this.pos];
        }
        throw new IllegalStateException("Unexpected end of DN: " + this.f55dn);
    }

    private char getUTF8() {
        int i;
        int i2;
        int i3 = getByte(this.pos);
        this.pos++;
        if (i3 < 128) {
            return (char) i3;
        }
        if (i3 < 192 || i3 > 247) {
            return '?';
        }
        if (i3 <= 223) {
            i2 = i3 & 31;
            i = 1;
        } else if (i3 <= 239) {
            i = 2;
            i2 = i3 & 15;
        } else {
            i = 3;
            i2 = i3 & 7;
        }
        for (int i4 = 0; i4 < i; i4++) {
            this.pos++;
            int i5 = this.pos;
            if (i5 == this.length || this.chars[i5] != '\\') {
                return '?';
            }
            this.pos = i5 + 1;
            int i6 = getByte(this.pos);
            this.pos++;
            if ((i6 & 192) != 128) {
                return '?';
            }
            i2 = (i2 << 6) + (i6 & 63);
        }
        return (char) i2;
    }

    private int getByte(int i) {
        int i2;
        int i3;
        int i4 = i + 1;
        if (i4 < this.length) {
            char c = this.chars[i];
            if (c >= '0' && c <= '9') {
                i2 = c - '0';
            } else if (c >= 'a' && c <= 'f') {
                i2 = c - 'W';
            } else if (c < 'A' || c > 'F') {
                throw new IllegalStateException("Malformed DN: " + this.f55dn);
            } else {
                i2 = c - '7';
            }
            char c2 = this.chars[i4];
            if (c2 >= '0' && c2 <= '9') {
                i3 = c2 - '0';
            } else if (c2 >= 'a' && c2 <= 'f') {
                i3 = c2 - 'W';
            } else if (c2 < 'A' || c2 > 'F') {
                throw new IllegalStateException("Malformed DN: " + this.f55dn);
            } else {
                i3 = c2 - '7';
            }
            return (i2 << 4) + i3;
        }
        throw new IllegalStateException("Malformed DN: " + this.f55dn);
    }

    public String findMostSpecific(String str) {
        this.pos = 0;
        this.beg = 0;
        this.end = 0;
        this.cur = 0;
        this.chars = this.f55dn.toCharArray();
        String nextAT = nextAT();
        if (nextAT == null) {
            return null;
        }
        do {
            String str2 = "";
            int i = this.pos;
            if (i == this.length) {
                return null;
            }
            switch (this.chars[i]) {
                case '\"':
                    str2 = quotedAV();
                    break;
                case '#':
                    str2 = hexAV();
                    break;
                case '+':
                case ',':
                case ';':
                    break;
                default:
                    str2 = escapedAV();
                    break;
            }
            if (str.equalsIgnoreCase(nextAT)) {
                return str2;
            }
            int i2 = this.pos;
            if (i2 >= this.length) {
                return null;
            }
            char[] cArr = this.chars;
            if (cArr[i2] == ',' || cArr[i2] == ';' || cArr[i2] == '+') {
                this.pos++;
                nextAT = nextAT();
            } else {
                throw new IllegalStateException("Malformed DN: " + this.f55dn);
            }
        } while (nextAT != null);
        throw new IllegalStateException("Malformed DN: " + this.f55dn);
    }
}
