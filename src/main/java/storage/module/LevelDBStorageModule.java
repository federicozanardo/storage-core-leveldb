package storage.module;

import storage.core.lib.module.StorageModule;
import storage.module.services.asset.AssetsStorageService;
import storage.module.services.contract.ContractsStorageService;
import storage.module.services.contractinstance.ContractInstancesStorageService;
import storage.module.services.ownership.OwnershipsStorageService;

public class LevelDBStorageModule extends StorageModule {

    public LevelDBStorageModule() {
        super(
                new AssetsStorageService(),
                new ContractInstancesStorageService(),
                new ContractsStorageService(),
                new OwnershipsStorageService()
        );
    }
}
