package com.quip.Quip_AEM_extract.utilities;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.quip.Quip_AEM_extract.config.MongoClientConfig;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MongoUtilities {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoClientConfig mongoClientConfig;

    public MongoTemplate mongoTemplate(String databaseName){
        return new MongoTemplate(mongoClientConfig.mongoClient(), databaseName+"_QUIP_SEO");
    }

    public String getDatabase(){
        return mongoTemplate.getDb().getName();
    }

    public List<Document> getAllData(Query query, String collectionName,MongoTemplate clientMongoTemplate){
        return clientMongoTemplate.find(query, Document.class, collectionName);
    }

    public Boolean insertData(String collectionName, Document data,MongoTemplate clientMongoTemplate){
        InsertOneResult result =clientMongoTemplate.getCollection(collectionName).insertOne(data);
        return result.wasAcknowledged();
    }

    public Boolean updateDocument(Document document,MongoTemplate clientMongoTemplate) {
        Document query=new Document("pagePath|PagePath",document.get("pagePath|PagePath"));
        Document oldDocument=clientMongoTemplate.getDb().getCollection(Constants.COLLECTION_NAME).find(query).first();
        if(oldDocument!=null){
            UpdateResult result = clientMongoTemplate.getDb().getCollection(Constants.COLLECTION_NAME).updateOne(query,new Document("$set",document));
            return result.wasAcknowledged();
        }
        return false;
    }

    public Boolean deleteData(String clientName,MongoTemplate clientMongoTemplate){
       Query query=new Query();
       query.addCriteria(Criteria.where("clientName").is(clientName));
        DeleteResult result=clientMongoTemplate.remove(query,Constants.COLLECTION_NAME);
        return result.getDeletedCount()!=0;
    }
}