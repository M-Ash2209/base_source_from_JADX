package okio;

import java.util.AbstractList;
import java.util.RandomAccess;

public final class Options extends AbstractList<ByteString> implements RandomAccess {
    final ByteString[] byteStrings;

    private Options(ByteString[] byteStringArr) {
        this.byteStrings = byteStringArr;
    }

    /* renamed from: of */
    public static Options m36of(ByteString... byteStringArr) {
        return new Options((ByteString[]) byteStringArr.clone());
    }

    public ByteString get(int i) {
        return this.byteStrings[i];
    }

    public int size() {
        return this.byteStrings.length;
    }
}
