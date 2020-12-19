package p006io.liteglue;

import java.sql.SQLException;

/* renamed from: io.liteglue.SQLiteConnectionFactory */
public interface SQLiteConnectionFactory {
    SQLiteConnection newSQLiteConnection(String str, int i) throws SQLException;
}
