package com.quip.Quip_AEM_extract.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoTemplateConfig {

    @Autowired
    private MongoClientConfig mongoClientConfig;

    public MongoTemplate mongoTemplate(String databaseName){
        return new MongoTemplate(mongoClientConfig.mongoClient(), "admin");
    }
}
