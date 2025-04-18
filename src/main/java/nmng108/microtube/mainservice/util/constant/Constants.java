package nmng108.microtube.mainservice.util.constant;

public interface Constants {
    String DATABASE_NAME = "MICROTUBE";

    interface Paging {
        int DEFAULT_PAGE_SIZE = 20;
    }

    interface MessageSources {
        String MESSAGES = "messages";
        String CUSTOM_RESPONSE_STATUS = "custom-response-status";
        String ERROR_CODES = "error-codes";
        String ERRORS = "errors";
    }

    interface BeanName {
        interface Database {
            interface MainRelationalDatabase {
                String baseName = "mainDb";
                String CONNECTION_FACTORY = baseName + "ConnectionFactory";
                String DATABASE_CLIENT = baseName + "DatabaseClient";
                String ENTITY_TEMPLATE = baseName + "EntityTemplate";
                String TRANSACTION_MANAGER = baseName + "TransactionManager";
            }
        }
    }
}
