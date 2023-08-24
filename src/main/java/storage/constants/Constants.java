package storage.constants;

import java.io.File;

public enum Constants {
    ASSETS_PATH {
        public String toString() {
            File currentDirectory = new File(new File(".").getAbsolutePath());
            return currentDirectory + "/storage/assets/";
        }
    },
    CONTRACTS_PATH {
        public String toString() {
            File currentDirectory = new File(new File(".").getAbsolutePath());
            return currentDirectory + "/storage/contracts/";
        }
    },
    CONTRACT_INSTANCES_PATH {
        public String toString() {
            File currentDirectory = new File(new File(".").getAbsolutePath());
            return currentDirectory + "/storage/contract-instances/";
        }
    },
    OWNERSHIPS_PATH {
        public String toString() {
            File currentDirectory = new File(new File(".").getAbsolutePath());
            return currentDirectory + "/storage/ownerships/";
        }
    }
}
