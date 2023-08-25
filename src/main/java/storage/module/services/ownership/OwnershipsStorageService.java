package storage.module.services.ownership;

import lcp.lib.models.ownership.Ownership;
import lcp.lib.models.singleuseseal.SingleUseSeal;
import lombok.NoArgsConstructor;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import storage.constants.Constants;
import storage.core.lib.exceptions.database.DatabaseException;
import storage.core.lib.exceptions.services.ownership.OwnershipNotFoundException;
import storage.core.lib.exceptions.services.ownership.OwnershipsNotFoundException;
import storage.core.lib.module.services.IOwnershipsStorageService;
import storage.exceptions.LevelDBDatabaseException;
import storage.module.services.StorageSerializer;
import storage.utils.LevelDBUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

@NoArgsConstructor
public class OwnershipsStorageService extends StorageSerializer<ArrayList<Ownership>> implements IOwnershipsStorageService {
    private DB db;

    // FIXME: return a boolean (true --> success, false --> otherwise)

    public void addOwnerships(HashMap<String, SingleUseSeal> funds) throws DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.OWNERSHIPS_PATH));

        for (HashMap.Entry<String, SingleUseSeal> entry : funds.entrySet()) {
            String address = entry.getKey();
            ArrayList<Ownership> currentFunds;

            // Try to get the funds associate to the address
            try {
                currentFunds = this.deserialize(db.get(bytes(address)));
            } catch (DBException e) {
                throw new LevelDBDatabaseException("Error while reading from database", e);
            }

            if (currentFunds == null) {
                // TODO: log
                // System.out.println("addFund: This address does not have any asset saved in the storage");
                currentFunds = new ArrayList<>();
            }

            // TODO: check that the id is unique
            String ownershipId = UUID.randomUUID().toString();
            Ownership ownership = new Ownership(ownershipId, entry.getValue());
            currentFunds.add(ownership);

            try {
                db.put(bytes(address), this.serialize(currentFunds));
            } catch (DBException e) {
                throw new LevelDBDatabaseException("Error while writing to database", e);
            } finally {
                try {
                    db.close();
                } catch (IOException ignored) {

                }
            }
        }

        try {
            db.close();
        } catch (IOException e) {
            throw new LevelDBDatabaseException("Error while closing the database", e);
        }
    }

    public ArrayList<Ownership> getOwnerships(String address) throws OwnershipsNotFoundException, DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.OWNERSHIPS_PATH));

        ArrayList<Ownership> ownerships;
        try {
            ownerships = this.deserialize(db.get(bytes(address)));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while reading from database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }

        if (ownerships == null) {
            throw new OwnershipsNotFoundException(address);
        }

        return ownerships;
    }

    public Ownership getOwnership(String address, String ownershipId)
            throws OwnershipsNotFoundException,
            OwnershipNotFoundException,
            DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.OWNERSHIPS_PATH));

        ArrayList<Ownership> funds;
        try {
            funds = this.deserialize(db.get(bytes(address)));
        } catch (DBException e) {
            throw new LevelDBDatabaseException("Error while reading from database", e);
        } finally {
            try {
                db.close();
            } catch (IOException ignored) {

            }
        }

        if (funds == null) {
            throw new OwnershipsNotFoundException(address);
        }

        int i = 0;
        boolean found = false;
        Ownership fund = null;

        while (i < funds.size() && !found) {
            Ownership currentFund = funds.get(i);

            if (currentFund.getId().equals(ownershipId)) {
                found = true;
                fund = currentFund;
            } else {
                i++;
            }
        }

        if (!found) {
            throw new OwnershipNotFoundException(address, ownershipId);
        }

        return fund;
    }

    // FIXME: return a boolean (true --> success, false --> otherwise)

    public void spendOwnership(
            String address,
            String ownershipId,
            String contractInstanceId,
            String unlockScript
    ) throws OwnershipsNotFoundException,
            OwnershipNotFoundException,
            DatabaseException {
        db = LevelDBUtils.open(String.valueOf(Constants.OWNERSHIPS_PATH));

        ArrayList<Ownership> funds;
        try {
            funds = this.deserialize(db.get(bytes(address)));
        } catch (DBException e) {
            try {
                db.close();
            } catch (IOException ex) {
                throw new LevelDBDatabaseException("Error while closing the database", e);
            }
            throw new LevelDBDatabaseException("Error while reading from database", e);
        }

        if (funds == null) {
            try {
                db.close();
            } catch (IOException e) {
                throw new LevelDBDatabaseException("Error while closing the database", e);
            }
            throw new OwnershipsNotFoundException(address);
        }

        int i = 0;
        boolean found = false;

        while (i < funds.size() && !found) {
            Ownership currentFund = funds.get(i);

            if (currentFund.getId().equals(ownershipId)) {
                found = true;
            } else {
                i++;
            }
        }

        if (!found) {
            try {
                db.close();
            } catch (IOException e) {
                throw new LevelDBDatabaseException("Error while closing the database", e);
            }
            throw new OwnershipNotFoundException(address, ownershipId);
        }

        // Update the ownership
        funds.get(i).setContractInstanceId(contractInstanceId);
        funds.get(i).setUnlockScript(unlockScript);

        // Save
        try {
            db.put(bytes(address), this.serialize(funds));
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
