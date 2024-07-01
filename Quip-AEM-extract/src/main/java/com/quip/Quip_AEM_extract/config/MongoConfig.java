package com.quip.Quip_AEM_extract.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.quip.Quip_AEM_extract.utilities.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig{

    @Bean
    public MongoTemplate mongoTemplate(){
        MongoClient mongoClient= MongoClients.create();
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, Constants.DBNAME));
    }
}