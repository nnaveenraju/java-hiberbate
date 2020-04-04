package com.example.demo.hazelcast.persistence;

import com.example.demo.hazelcast.persistence.util.HibernateUtil;
import com.example.demo.model.HazelcastPersist;
import com.hazelcast.core.MapStore;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DBPersistence2<K, V> implements MapStore<K, V> {

    private String persistenceLocation;

    @Override
    public void store(K key, V value) {
        HazelcastPersist hazelcastPersist = new HazelcastPersist();
        hazelcastPersist.setPersistenceLocation( this.getPersistenceLocation() );
        hazelcastPersist.setMapKey((String) key);
        hazelcastPersist.setMapValue((String) value);


        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student objects
            if(KeyExists(key, session))
                session.saveOrUpdate(hazelcastPersist);
            else
                updateExistingKey(key, value, session);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

    }

    @Override
    public void storeAll(Map<K, V> map) {
        HazelcastPersist hazelcastPersist ;

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            for (K key : map.keySet()) {
                hazelcastPersist = new HazelcastPersist();
                hazelcastPersist.setPersistenceLocation((String) this.getPersistenceLocation() );
                hazelcastPersist.setMapKey((String) key);
                hazelcastPersist.setMapValue((String) map.get(key));

                // save the student objects
                if(! KeyExists(key, session))
                    session.saveOrUpdate(hazelcastPersist);
                else
                    updateExistingKey(key, (V) map.get(key), session);
            }
            // commit transaction
            transaction.commit();

            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }


    }

    @Override
    public void delete(K key) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query query = session.createQuery("delete HazelcastPersist where MapKey = :key and MapName = :mapName ");
            query.setParameter("key",key);
            query.setParameter("mapName", this.getPersistenceLocation());


            // start a transaction
            transaction = session.beginTransaction();

            session.flush();
//            session.clear();
            // save the student objects
            int results = query.executeUpdate();
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll(Collection<K> collection) {

        Transaction transaction = null;
        Query query ;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            session.flush();
            session.clear();

            for( K key: collection){
                query = session.createQuery("delete HazelcastPersist where MapKey = :key and MapName = :mapName ");
                query.setParameter("key",key);
                query.setParameter("mapName", this.getPersistenceLocation());

                int results = query.executeUpdate();
            }

            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

    }

    @Override
    public V load(K key) {
        Transaction transaction = null;

        try ( Session session = HibernateUtil.getSessionFactory().openSession() ){

            Query query = session.createQuery("from HazelcastPersist where MapKey = :key and MapName = :mapName");
            query.setParameter("key", key);
            query.setParameter("mapName", this.getPersistenceLocation());

            List<HazelcastPersist> results = query.getResultList();
            if (results.size()>0)
                    return (V) results.get(0);
            else
                return null;

        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map<K, V> loadAll(Collection<K> collection) {
        Transaction transaction = null;
        List<String> keys = new ArrayList<>();

        for( K key : collection)
        {
            keys.add((String) key);
        }

        try ( Session session = HibernateUtil.getSessionFactory().openSession() ){

            Query query = session.createQuery("from HazelcastPersist where MapKey  in (:key) and MapName = :mapName");
            query.setParameter("key", keys);
            query.setParameter("mapName", this.getPersistenceLocation());

            List<HazelcastPersist> results = query.getResultList();
            Map<K, V> returnValues = ( Map<K, V> ) results.stream().collect(
                                                Collectors.toMap(HazelcastPersist::getMapKey, HazelcastPersist::getMapValue)
                                        );

            return returnValues;

        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<K> loadAllKeys() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession() ){

            Query query = session.createQuery("from HazelcastPersist where MapName = :mapName");
            query.setParameter("mapName", this.getPersistenceLocation());

            List<HazelcastPersist> results = query.getResultList();
            List<K> returnValues = new ArrayList<>();

            for(HazelcastPersist hazelcastPersist: results)
            {
                returnValues.add((K)hazelcastPersist.getMapKey());
            }

            return returnValues;

        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getPersistenceLocation() {
        return persistenceLocation;
    }

    public void setPersistenceLocation(String persistenceLocation) {
        this.persistenceLocation = persistenceLocation;
    }

    public Boolean KeyExists(K key, Session session)
    {
        try {

            Query query = session.createQuery("from HazelcastPersist where MapKey = :key and MapName = :mapName");
            query.setParameter("key", key);
            query.setParameter("mapName", this.getPersistenceLocation());

            List<HazelcastPersist> results = query.getResultList();
            if (results.size()>0)
                return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  false;
    }

    public void updateExistingKey(K key, V value, Session session){

        Query query = session.createQuery("update HazelcastPersist set MapValue = :mapValue where MapKey = :key and MapName = :mapName ");
        query.setParameter("key",key);
        query.setParameter("mapName", this.getPersistenceLocation());
        query.setParameter("mapValue", value);

        // save the student objects
        int results = query.executeUpdate();

    }

}
