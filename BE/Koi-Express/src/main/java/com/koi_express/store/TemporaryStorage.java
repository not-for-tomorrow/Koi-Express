package com.koi_express.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemporaryStorage {
    private static final TemporaryStorage instance = new TemporaryStorage();

    private final Map<Long, Map<String, Object>> temporaryData = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(TemporaryStorage.class);

    private TemporaryStorage() {}

    public static TemporaryStorage getInstance() {
        return instance;
    }

    public void storeData(Long key, Map<String, Object> data) {
        temporaryData.put(key, data);
        logger.info("Data stored in TemporaryStorage for key {}: {}", key, data);
    }

    public Map<String, Object> retrieveData(Long key) {
        Map<String, Object> data = temporaryData.get(key);
        if (data != null) {
            logger.info("Data retrieved from TemporaryStorage for key {}: {}", key, data);
        } else {
            logger.info("No data found in TemporaryStorage for key {}", key);
        }
        return data;
    }

}