package p006io.liteglue;

import java.sql.SQLException;

/* renamed from: io.liteglue.SQLiteConnector */
public class SQLiteConnector implements SQLiteConnectionFactory {
    static boolean isLibLoaded = false;

    public SQLiteConnector() {
        if (!isLibLoaded) {
            System.loadLibrary("sqlc-native-driver");
            if (SQLiteNative.sqlc_api_version_check(1) == 0) {
                isLibLoaded = true;
                return;
            }
            throw new RuntimeException("native library version mismatch");
        }
    }

    public SQLiteConnection newSQLiteConnection(String str, int i) throws SQLException {
        return new SQLiteGlueConnection(str, i);
    }
}
