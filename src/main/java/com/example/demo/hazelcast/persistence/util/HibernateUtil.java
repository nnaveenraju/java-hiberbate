package com.example.demo.hazelcast.persistence.util;

import com.example.demo.hazelcast.persistence.PropertiesUtil;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {
    private static ServiceRegistry serviceRegistry;
    private static SessionFactory sessionFactory;

    private static Properties properties = new Properties();

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                properties = PropertiesUtil.propertyLoad();
                Configuration configuration = new Configuration().mergeProperties(properties);

                configuration.configure("hibernate.cfg.xml").addProperties(properties);;
                // Create registry
                serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).configure().build();
                // Create MetadataSources
                MetadataSources sources = new MetadataSources(serviceRegistry);
                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();
                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

//                sessionFactory = configuration
//                        .buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
                if (serviceRegistry != null) {
                    StandardServiceRegistryBuilder.destroy(serviceRegistry);
                }
            }
        }
        return sessionFactory;
    }
    public static void shutdown() {
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }
}