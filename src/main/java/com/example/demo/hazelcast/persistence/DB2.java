package com.example.demo.hazelcast.persistence;

import com.example.demo.hazelcast.persistence.util.HibernateUtil;
import com.example.demo.model.HazelcastPersist;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DB2 implements IHazelCastPersistence {

    String persistenceLocation;
    public String getPersistenceLocation() {
        return persistenceLocation;
    }

    public void setPersistenceLocation(String persistenceLocation2){
        persistenceLocation = persistenceLocation2;
    }

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
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void storeAll(Map map) {

    }

    @Override
    public void delete(Object o) {

    }

    @Override
    public void deleteAll(Collection collection) {

    }

    @Override
    public Object load(Object o) {
        return null;
    }

    @Override
    public Map loadAll(Collection collection) {
        return null;
    }

    @Override
    public Iterable loadAllKeys() {
        return null;
    }


    public Boolean KeyExists(Object key, Session session)
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

    public void updateExistingKey(Object key, Object value, Session session){

        Query query = session.createQuery("update HazelcastPersist set MapValue = :mapValue where MapKey = :key and MapName = :mapName ");
        query.setParameter("key",key);
        query.setParameter("mapName", this.getPersistenceLocation());
        query.setParameter("mapValue", value);

        // save the student objects
        int results = query.executeUpdate();

    }

}
