package storage.module.services.asset;

import lcp.lib.models.assets.Asset;
import lombok.NoArgsConstructor;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import storage.constants.Constants;
import storage.core.lib.exceptions.database.DatabaseException;
import storage.core.lib.exceptions.services.asset.AssetNotFoundException;
import storage.core.lib.module.services.IAssetsStorageService;
import storage.exceptions.LevelDBDatabaseException;
import storage.module.services.StorageSerializer;
import storage.utils.LevelDBUtils;

import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

@NoArgsConstructor
public class AssetsStorageService extends StorageSerializer<Asset> implements IAssetsStorageService {
    private DB db;

    public Asset getAssetInfo(String assetId) throws AssetNotFoundException, DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.ASSETS_PATH));

        Asset asset;
        try {
            asset = this.deserialize(db.get(bytes(assetId)));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while reading from database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }

        if (asset == null) {
            throw new AssetNotFoundException(assetId);
        }

        return asset;
    }
}
