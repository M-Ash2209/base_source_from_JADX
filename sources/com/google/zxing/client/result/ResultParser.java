package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.silkimen.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class ResultParser {
    private static final Pattern AMPERSAND = Pattern.compile("&");
    private static final String BYTE_ORDER_MARK = "﻿";
    private static final Pattern DIGITS = Pattern.compile("\\d+");
    private static final Pattern EQUALS = Pattern.compile("=");
    private static final ResultParser[] PARSERS = {new BookmarkDoCoMoResultParser(), new AddressBookDoCoMoResultParser(), new EmailDoCoMoResultParser(), new AddressBookAUResultParser(), new VCardResultParser(), new BizcardResultParser(), new VEventResultParser(), new EmailAddressResultParser(), new SMTPResultParser(), new TelResultParser(), new SMSMMSResultParser(), new SMSTOMMSTOResultParser(), new GeoResultParser(), new WifiResultParser(), new URLTOResultParser(), new URIResultParser(), new ISBNResultParser(), new ProductResultParser(), new ExpandedProductResultParser(), new VINResultParser()};

    protected static int parseHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        }
        if (c < 'A' || c > 'F') {
            return -1;
        }
        return (c - 'A') + 10;
    }

    public abstract ParsedResult parse(Result result);

    protected static String getMassagedText(Result result) {
        String text = result.getText();
        return text.startsWith(BYTE_ORDER_MARK) ? text.substring(1) : text;
    }

    public static ParsedResult parseResult(Result result) {
        for (ResultParser parse : PARSERS) {
            ParsedResult parse2 = parse.parse(result);
            if (parse2 != null) {
                return parse2;
            }
        }
        return new TextParsedResult(result.getText(), (String) null);
    }

    protected static void maybeAppend(String str, StringBuilder sb) {
        if (str != null) {
            sb.append(10);
            sb.append(str);
        }
    }

    protected static void maybeAppend(String[] strArr, StringBuilder sb) {
        if (strArr != null) {
            for (String append : strArr) {
                sb.append(10);
                sb.append(append);
            }
        }
    }

    protected static String[] maybeWrap(String str) {
        if (str == null) {
            return null;
        }
        return new String[]{str};
    }

    protected static String unescapeBackslash(String str) {
        int indexOf = str.indexOf(92);
        if (indexOf < 0) {
            return str;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length - 1);
        sb.append(str.toCharArray(), 0, indexOf);
        boolean z = false;
        while (indexOf < length) {
            char charAt = str.charAt(indexOf);
            if (z || charAt != '\\') {
                sb.append(charAt);
                z = false;
            } else {
                z = true;
            }
            indexOf++;
        }
        return sb.toString();
    }

    protected static boolean isStringOfDigits(CharSequence charSequence, int i) {
        return charSequence != null && i > 0 && i == charSequence.length() && DIGITS.matcher(charSequence).matches();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r4 = r4 + r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static boolean isSubstringOfDigits(java.lang.CharSequence r2, int r3, int r4) {
        /*
            r0 = 0
            if (r2 == 0) goto L_0x001f
            if (r4 > 0) goto L_0x0006
            goto L_0x001f
        L_0x0006:
            int r4 = r4 + r3
            int r1 = r2.length()
            if (r1 < r4) goto L_0x001e
            java.util.regex.Pattern r1 = DIGITS
            java.lang.CharSequence r2 = r2.subSequence(r3, r4)
            java.util.regex.Matcher r2 = r1.matcher(r2)
            boolean r2 = r2.matches()
            if (r2 == 0) goto L_0x001e
            r0 = 1
        L_0x001e:
            return r0
        L_0x001f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.result.ResultParser.isSubstringOfDigits(java.lang.CharSequence, int, int):boolean");
    }

    static Map<String, String> parseNameValuePairs(String str) {
        int indexOf = str.indexOf(63);
        if (indexOf < 0) {
            return null;
        }
        HashMap hashMap = new HashMap(3);
        for (String appendKeyValue : AMPERSAND.split(str.substring(indexOf + 1))) {
            appendKeyValue(appendKeyValue, hashMap);
        }
        return hashMap;
    }

    private static void appendKeyValue(CharSequence charSequence, Map<String, String> map) {
        String[] split = EQUALS.split(charSequence, 2);
        if (split.length == 2) {
            try {
                map.put(split[0], urlDecode(split[1]));
            } catch (IllegalArgumentException unused) {
            }
        }
    }

    static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, HttpRequest.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    static String[] matchPrefixedField(String str, String str2, char c, boolean z) {
        int i;
        int length = str2.length();
        ArrayList arrayList = null;
        for (int i2 = 0; i2 < length; i2 = i) {
            int indexOf = str2.indexOf(str, i2);
            if (indexOf < 0) {
                break;
            }
            int length2 = indexOf + str.length();
            boolean z2 = true;
            i = length2;
            while (z2) {
                int indexOf2 = str2.indexOf(c, i);
                if (indexOf2 < 0) {
                    i = str2.length();
                    z2 = false;
                } else if (countPrecedingBackslashes(str2, indexOf2) % 2 != 0) {
                    i = indexOf2 + 1;
                } else {
                    if (arrayList == null) {
                        arrayList = new ArrayList(3);
                    }
                    String unescapeBackslash = unescapeBackslash(str2.substring(length2, indexOf2));
                    if (z) {
                        unescapeBackslash = unescapeBackslash.trim();
                    }
                    if (!unescapeBackslash.isEmpty()) {
                        arrayList.add(unescapeBackslash);
                    }
                    i = indexOf2 + 1;
                    z2 = false;
                }
            }
        }
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private static int countPrecedingBackslashes(CharSequence charSequence, int i) {
        int i2 = i - 1;
        int i3 = 0;
        while (i2 >= 0 && charSequence.charAt(i2) == '\\') {
            i3++;
            i2--;
        }
        return i3;
    }

    static String matchSinglePrefixedField(String str, String str2, char c, boolean z) {
        String[] matchPrefixedField = matchPrefixedField(str, str2, c, z);
        if (matchPrefixedField == null) {
            return null;
        }
        return matchPrefixedField[0];
    }
}
