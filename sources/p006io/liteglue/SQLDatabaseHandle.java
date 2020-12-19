package p006io.liteglue;

/* renamed from: io.liteglue.SQLDatabaseHandle */
interface SQLDatabaseHandle {
    int close();

    String getLastErrorMessage();

    long getLastInsertRowid();

    int getTotalChanges();

    boolean isOpen();

    int keyNativeString(String str);

    SQLStatementHandle newStatementHandle(String str);

    int open();
}
