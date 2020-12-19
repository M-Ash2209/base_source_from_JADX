package p006io.liteglue;

import java.sql.SQLException;

/* renamed from: io.liteglue.SQLiteConnection */
public interface SQLiteConnection {
    void dispose() throws SQLException;

    long getLastInsertRowid() throws SQLException;

    int getTotalChanges() throws SQLException;

    void keyNativeString(String str) throws SQLException;

    SQLiteStatement prepareStatement(String str) throws SQLException;
}
