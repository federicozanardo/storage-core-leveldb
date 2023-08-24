package storage.exceptions;

import storage.core.lib.exceptions.database.DatabaseException;

public class LevelDBDatabaseException extends DatabaseException {
    private static final String databaseName = "LevelDB";

    public LevelDBDatabaseException(String message) {
        super(databaseName, message);
    }

    public LevelDBDatabaseException(String message, Exception exception) {
        super(databaseName, message, exception);
    }
}
