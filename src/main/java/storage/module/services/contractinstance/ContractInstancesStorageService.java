package storage.module.services.contractinstance;

import lcp.lib.models.contract.ContractInstance;
import lombok.NoArgsConstructor;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import storage.constants.Constants;
import storage.core.lib.exceptions.database.DatabaseException;
import storage.core.lib.exceptions.services.contractinstance.ContractInstanceNotFoundException;
import storage.core.lib.module.services.IContractInstancesStorageService;
import storage.exceptions.LevelDBDatabaseException;
import storage.module.services.StorageSerializer;
import storage.utils.LevelDBUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

@NoArgsConstructor
public class ContractInstancesStorageService extends StorageSerializer<ContractInstance> implements IContractInstancesStorageService {
    public DB db;

    public ContractInstance getContractInstance(String contractInstanceId) throws ContractInstanceNotFoundException, DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.CONTRACT_INSTANCES_PATH));

        ContractInstance contractInstance;
        try {
            contractInstance = this.deserialize(db.get(bytes(contractInstanceId)));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while reading from database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }

        if (contractInstance == null) {
            throw new ContractInstanceNotFoundException(contractInstanceId);
        }

        return contractInstance;
    }

    public String saveContractInstance(ContractInstance contractInstance) throws DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.CONTRACT_INSTANCES_PATH));

        // TODO: check that the id is unique
        String contractInstanceId = UUID.randomUUID().toString();
        try {
            db.put(bytes(contractInstanceId), this.serialize(contractInstance));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while writing to database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }

        return contractInstanceId;
    }

    // FIXME
    /*public void storeGlobalSpace(String contractInstanceId, HashMap<String, TraceChange> updates)
            throws IOException,
            ContractInstanceNotFoundException {
        mutex.lock();
        levelDb = factory.open(new File(String.valueOf(Constants.CONTRACT_INSTANCES_PATH)), new Options());

        ContractInstance contractInstance = this.deserialize(levelDb.get(bytes(contractInstanceId)));
        if (contractInstance == null) {
            levelDb.close();
            mutex.unlock();
            throw new ContractInstanceNotFoundException(contractInstanceId);
        }

        for (HashMap.Entry<String, TraceChange> entry : updates.entrySet()) {
            String variableName = entry.getKey();
            TraceChange value = entry.getValue();

            if (value.isChanged()) {
                contractInstance.getGlobalSpace().put(variableName, value.getValue());
            }
        }

        levelDb.put(bytes(contractInstanceId), this.serialize(contractInstance));

        levelDb.close();
        mutex.unlock();
    }*/

    // FIXME: return a boolean (true --> success, false --> otherwise)

    public void saveStateMachine(String contractInstanceId, String partyName, String functionName, ArrayList<String> argumentsTypes)
            throws ContractInstanceNotFoundException,
            DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.CONTRACT_INSTANCES_PATH));

        ContractInstance contractInstance;
        try {
            contractInstance = this.deserialize(db.get(bytes(contractInstanceId)));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while reading from database", e);
        }

        if (contractInstance == null) {
            try {
                db.close();
            } catch (IOException e) {
                throw new LevelDBDatabaseException("Error while closing the database", e);
            }
            throw new ContractInstanceNotFoundException(contractInstanceId);
        }

        // FIXME
        contractInstance.getStateMachine().nextState(partyName, functionName, argumentsTypes);
        try {
            db.put(bytes(contractInstanceId), this.serialize(contractInstance));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while writing to database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }
    }

    // FIXME: return a boolean (true --> success, false --> otherwise)

    public void saveStateMachine(String contractInstanceId, String obligationFunctionName)
            throws ContractInstanceNotFoundException,
            DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.CONTRACT_INSTANCES_PATH));

        ContractInstance contractInstance;
        try {
            contractInstance = this.deserialize(db.get(bytes(contractInstanceId)));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while reading from database", e);
        }

        if (contractInstance == null) {
            try {
                db.close();
            } catch (IOException e) {
                throw new LevelDBDatabaseException("Error while closing the database", e);
            }
            throw new ContractInstanceNotFoundException(contractInstanceId);
        }

        // FIXME
        contractInstance.getStateMachine().nextState(obligationFunctionName);
        try {
            db.put(bytes(contractInstanceId), this.serialize(contractInstance));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while writing to database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }
    }
}
