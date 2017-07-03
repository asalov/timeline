package com.alexsalov.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import com.alexsalov.models.Entry;
import com.alexsalov.models.EntryStat;
import com.alexsalov.models.Fact;
import com.alexsalov.models.Picture;
import com.alexsalov.models.Stat;
import com.alexsalov.models.User;

@Configuration
public class HibernateUtils {
    private static final SessionFactory sessionFactory;
     
    static {
    	org.hibernate.cfg.Configuration conf = new org.hibernate.cfg.Configuration();
        
        conf.addAnnotatedClass(Stat.class);
        conf.addAnnotatedClass(EntryStat.class);
        conf.addAnnotatedClass(User.class);
        conf.addAnnotatedClass(Entry.class);
        conf.addAnnotatedClass(Fact.class);
        conf.addAnnotatedClass(Picture.class);
        
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();
        
        try {
            sessionFactory = conf.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }       
    }
    
    @Bean
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}