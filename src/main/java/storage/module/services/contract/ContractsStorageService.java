package storage.module.services.contract;

import lcp.lib.models.contract.Contract;
import lombok.NoArgsConstructor;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import storage.constants.Constants;
import storage.core.lib.exceptions.database.DatabaseException;
import storage.core.lib.exceptions.services.contract.ContractNotFoundException;
import storage.core.lib.module.services.IContractsStorageService;
import storage.exceptions.LevelDBDatabaseException;
import storage.module.services.StorageSerializer;
import storage.utils.LevelDBUtils;

import java.io.IOException;
import java.util.UUID;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

@NoArgsConstructor
public class ContractsStorageService extends StorageSerializer<Contract> implements IContractsStorageService {
    private DB db;

    public Contract getContract(String contractId) throws ContractNotFoundException, DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.CONTRACTS_PATH));

        Contract contract;
        try {
            contract = this.deserialize(db.get(bytes(contractId)));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while reading from database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }

        if (contract == null) {
            throw new ContractNotFoundException(contractId);
        }

        return contract;
    }

    public String saveContract(Contract contract) throws DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.CONTRACTS_PATH));

        // TODO: check that the id is unique
        String contractId = UUID.randomUUID().toString();
        try {
            db.put(bytes(contractId), this.serialize(contract));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while writing to database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }

        return contractId;
    }
}
