package com.example.demo;

import com.example.demo.hazelcast.persistence.DBPersistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class DemoApplication {

	public static void main(String[] args) {

		DBPersistence dbPersistence = new DBPersistence();
		dbPersistence.setPersistenceLocation("Sample1");

		HashMap<String, String> mul = new HashMap<String, String>();
		mul.put("key 2", "n2.1");
		mul.put("key 2", "n2");
		mul.put("key 3", "n3");
		mul.put("key 4", "n4");
		mul.put("key 1", "n1");
		mul.put("key 1", "n1111");

		dbPersistence.storeAll(mul);

		//dbPersistence.delete("key 2");

		Collection c = new ArrayList<String>();
		c.add("key 3");
		c.add("key 4");
		c.add("key1");

		//dbPersistence.deleteAll(c);

		System.out.println(dbPersistence.loadAll(c));

		System.out.println("All Keys -: " + dbPersistence.loadAllKeys());

	}
}
