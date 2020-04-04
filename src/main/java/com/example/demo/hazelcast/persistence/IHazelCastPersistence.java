package com.example.demo.hazelcast.persistence;

import com.hazelcast.core.MapStore;

public interface IHazelCastPersistence extends MapStore {
    void setPersistenceLocation(String persistenceLocation);
    String getPersistenceLocation();
}
