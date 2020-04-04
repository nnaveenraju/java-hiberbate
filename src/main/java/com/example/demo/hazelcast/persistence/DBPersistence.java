package com.example.demo.hazelcast.persistence;

import com.example.demo.exceptions.FailedToDeleteExecption;
import com.example.demo.exceptions.FailedToLoadExecption;
import com.example.demo.exceptions.FailedToStoreException;
import com.example.demo.hazelcast.persistence.util.Constants;
import com.example.demo.hazelcast.persistence.util.HibernateUtil;
import com.example.demo.model.HazelcastPersist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DBPersistence implements IHazelCastPersistence {

    private String persistenceLocation;
    Logger LOGGER = LogManager.getLogger(DBPersistence.class);

    @Override
    public void store(Object key, Object value) {
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
            LOGGER.error("Failed to Persist key.", e);
            String message = String.format("Failed to store Key %s & Value %s. \n Root Cause - %s ", (String) key, (String) value, e.getMessage());

            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new FailedToStoreException(message, Constants.FAILED_TO_STORE_KEY_EXCEPTION_CODE);
        }

    }

    @Override
    public void storeAll(Map map) {
        HazelcastPersist hazelcastPersist ;

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();

            for (Object key : map.keySet()) {
                hazelcastPersist = new HazelcastPersist();
                hazelcastPersist.setPersistenceLocation(this.getPersistenceLocation() );
                hazelcastPersist.setMapKey((String) key);
                hazelcastPersist.setMapValue((String) map.get(key));

                // save the student objects
                if(! KeyExists(key, session))
                    session.saveOrUpdate(hazelcastPersist);
                else
                    updateExistingKey(key, map.get(key), session);
            }
            // commit transaction
            transaction.commit();

            } catch (Exception e) {
                LOGGER.error("Failed to store map. ", e);
                String message = String.format("Failed to store map. \n Root Cause - %s " , e.getMessage());
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
                throw new FailedToStoreException(message, Constants.FAILED_TO_STORE_MAP_EXCEPTION_CODE);
            }


    }

    @Override
    public void delete(Object key) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query query = session.createQuery("delete HazelcastPersist where MapKey = :key and PersistenceLocation = :persistenceLocation ");
            query.setParameter("key",key);
            query.setParameter("persistenceLocation", this.getPersistenceLocation());


            // start a transaction
            transaction = session.beginTransaction();

            session.flush();
//            session.clear();
            // save the student objects
            int results = query.executeUpdate();
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Failed to delete key. ", e);
            String message = String.format("Failed to delete key %s from map. \n Root Cause - %s ", (String) key, e.getMessage() );

            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new FailedToDeleteExecption(message, Constants.FAILED_TO_DELETE_KEY_EXCEPTION_CODE);
        }
    }

    @Override
    public void deleteAll(Collection collection) {

        Transaction transaction = null;
        Query query ;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // start a transaction
            transaction = session.beginTransaction();

            session.flush();
            session.clear();

            for( Object key: collection){
                query = session.createQuery("delete HazelcastPersist where MapKey = :key and PersistenceLocation = :persistenceLocation ");
                query.setParameter("key",key);
                query.setParameter("persistenceLocation", this.getPersistenceLocation());

                int results = query.executeUpdate();
            }

            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            LOGGER.error("Failed to delete Collection. ", e);
            String message = String.format("Failed to delete Collection. \n Root Cause - %s ", e.getMessage());
            throw new FailedToDeleteExecption(message, Constants.FAILED_TO_DELETE_KEY_EXCEPTION_CODE);
        }

    }

    @Override
    public Object load(Object key) {
        Transaction transaction = null;

        try ( Session session = HibernateUtil.getSessionFactory().openSession() ){

            Query query = session.createQuery("from HazelcastPersist where MapKey = :key and PersistenceLocation = :persistenceLocation");
            query.setParameter("key", key);
            query.setParameter("persistenceLocation", this.getPersistenceLocation());

            List results = query.getResultList();
            if (results.size()>0)
                    return results.get(0);
            else
                return null;

        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            LOGGER.error("Failed to load key. ", e);
            String message = String.format("Failed to load key %s from map. \n Root Cause - %s ", (String) key, e.getMessage());
            throw new FailedToLoadExecption(message, Constants.FAILED_TO_LOAD_KEY_EXCEPTION_CODE);
        }
    }

    @Override
    public Map loadAll(Collection collection) {
        Transaction transaction = null;
        List<String> keys = new ArrayList<>();

        for( Object key : collection)
        {
            keys.add((String) key);
        }

        try ( Session session = HibernateUtil.getSessionFactory().openSession() ){

            Query query = session.createQuery("from HazelcastPersist where MapKey  in (:key) and PersistenceLocation = :persistenceLocation");
            query.setParameter("key", keys);
            query.setParameter("persistenceLocation", this.getPersistenceLocation());

            List<HazelcastPersist> results = query.getResultList();
            return results.stream().collect(
                                                Collectors.toMap(HazelcastPersist::getMapKey, HazelcastPersist::getMapValue)
                                        );

            //return returnValues;

        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            LOGGER.error("Failed to load Collection. ", e);
            String message = String.format("Failed to load collection. \n Root Cause - %s" , e.getMessage());
            throw new FailedToLoadExecption(message, Constants.FAILED_TO_LOAD_MAP_EXCEPTION_CODE);
        }
    }

    @Override
    public Iterable<Object> loadAllKeys() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession() ){

            Query query = session.createQuery("from HazelcastPersist where PersistenceLocation = :persistenceLocation");
            query.setParameter("persistenceLocation", this.getPersistenceLocation());

            List<HazelcastPersist> results = query.getResultList();
            List<Object> returnValues = new ArrayList<>();

            for(HazelcastPersist hazelcastPersist: results)
            {
                returnValues.add((Object)hazelcastPersist.getMapKey());
            }

            return returnValues;

        }catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to load All keys. ", e);
            String message = String.format("Failed to load all keys. \n Root Cause - %s", e.getMessage());
            throw new FailedToLoadExecption(message, Constants.FAILED_TO_LOAD_KEYS_EXCEPTION_CODE);
        }
    }

    public String getPersistenceLocation() {
        return persistenceLocation;
    }

    public void setPersistenceLocation(String persistenceLocation) {
        this.persistenceLocation = persistenceLocation;
    }

    public Boolean KeyExists(Object key, Session session)
    {
        try {

            Query query = session.createQuery("from HazelcastPersist where MapKey = :key and PersistenceLocation = :persistenceLocation");
            query.setParameter("key", key);
            query.setParameter("persistenceLocation", this.getPersistenceLocation());

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

    public void updateExistingKey(Object key, Object value, Session session){

        Query query = session.createQuery("update HazelcastPersist set MapValue = :mapValue where MapKey = :key and PersistenceLocation = :persistenceLocation ");
        query.setParameter("key",key);
        query.setParameter("persistenceLocation", this.getPersistenceLocation());
        query.setParameter("mapValue", value);

        // save the student objects
        int results = query.executeUpdate();

    }

}
