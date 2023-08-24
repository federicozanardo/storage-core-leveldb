package storage.utils;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import storage.exceptions.LevelDBDatabaseException;

import java.io.File;
import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class LevelDBUtils {
    public static DB open(Options options, String path) throws LevelDBDatabaseException {
        DB db;

        try {
            db = factory.open(new File(path), options);
        } catch (IOException e) {
            throw new LevelDBDatabaseException("Error while opening the database", e);
        }

        return db;
    }

    public static DB open(String path) throws LevelDBDatabaseException {
        DB db;

        try {
            db = factory.open(new File(path), new Options());
        } catch (IOException e) {
            throw new LevelDBDatabaseException("Error while opening the database", e);
        }

        return db;
    }
}
