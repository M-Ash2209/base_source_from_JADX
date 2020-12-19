package org.apache.cordova.filetransfer;

import org.json.JSONException;
import org.json.JSONObject;

public class FileProgressResult {
    private boolean lengthComputable = false;
    private long loaded = 0;
    private long total = 0;

    public boolean getLengthComputable() {
        return this.lengthComputable;
    }

    public void setLengthComputable(boolean z) {
        this.lengthComputable = z;
    }

    public long getLoaded() {
        return this.loaded;
    }

    public void setLoaded(long j) {
        this.loaded = j;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long j) {
        this.total = j;
    }

    public JSONObject toJSONObject() throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append("{loaded:");
        sb.append(this.loaded);
        sb.append(",total:");
        sb.append(this.total);
        sb.append(",lengthComputable:");
        sb.append(this.lengthComputable ? "true" : "false");
        sb.append("}");
        return new JSONObject(sb.toString());
    }
}
