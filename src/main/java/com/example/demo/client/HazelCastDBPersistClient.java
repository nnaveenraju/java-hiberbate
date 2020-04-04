package com.example.demo.client;

import com.example.demo.hazelcast.persistence.DB2;
import com.example.demo.hazelcast.persistence.DB3;
import com.example.demo.hazelcast.persistence.DBPersistence2;
import com.example.demo.hazelcast.persistence.IHazelCastPersistence;
import com.example.demo.hazelcast.persistence.util.PersistenceTypes;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.HashMap;


public class HazelCastDBPersistClient<K, V> {

	private Config config;
	private HazelcastInstance hazelcastInstance;
	private PersistenceTypes persistenceType;
	private String persistenceLocation;

	/**
	 * @param config
	 */
	public HazelCastDBPersistClient(Config config) {

		this.config = config;
		this.hazelcastInstance = Hazelcast.newHazelcastInstance(this.config);
	}

	/**
	 * 
	 */
	public HazelCastDBPersistClient(String persistenceLocation) {

		this.config = new Config();
		this.persistenceLocation = persistenceLocation;

		MapStoreConfig mapStoreConfig = new MapStoreConfig();

		IHazelCastPersistence hzp = null;

		String str="1";
		
		switch (str) {
			case "1" :
				hzp = new DB2();
				break;
			case "2":
				hzp = new DB3();
				break;
			default:
				break;
		}
			
		hzp.setPersistenceLocation("test");



		DBPersistence2<String, String> persistence = new DBPersistence2<String, String>();
		persistence.setPersistenceLocation(this.persistenceLocation);
		mapStoreConfig.setImplementation(persistence);

		MapConfig customMapConfig = new MapConfig();
		customMapConfig.setMapStoreConfig(mapStoreConfig);

		HashMap<String, MapConfig> mapConfigDictionary = new HashMap<>();

		mapConfigDictionary.put("controlNumbers", customMapConfig);

		this.config.setMapConfigs(mapConfigDictionary);
		this.hazelcastInstance = Hazelcast.newHazelcastInstance(this.config);

	}

	public IMap<K, V> getMap(String mapName) {
		return this.hazelcastInstance.getMap(mapName);
	}

	public V getValue(K key, String mapName) {
		return this.getMap(mapName).getOrDefault(key, null);
	}

	public V getValue(K key, String mapName, V defaultValue) {
		return this.getMap(mapName).getOrDefault(key, defaultValue);
	}

	public void put(K key, V value, String mapName) {
		this.getMap(mapName).put(key, value);
	}

	public void remove(K key, String mapName) {
		this.getMap(mapName).remove(key);
	}

	public boolean contains(K key, String mapName) {
		return this.getMap(mapName).containsKey(key);
	}

	public PersistenceTypes getPersistenceType() {
		return persistenceType;
	}

	public void setPersistenceType(PersistenceTypes persistenceType) {
		this.persistenceType = persistenceType;
	}

	public String getPersistenceLocation() {
		return persistenceLocation;
	}

	public void setPersistenceLocation(String persistenceLocation) {
		this.persistenceLocation = persistenceLocation;
	}

}
