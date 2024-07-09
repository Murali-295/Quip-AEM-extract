package com.quip.Quip_AEM_extract.utilities;

import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MongoUtilities {

    public MongoTemplate mongoTemplate(MongoClient mongoClient,String databaseName){
        return new MongoTemplate(mongoClient,databaseName+"_QUIP_SEO");
    }

    public List<Document> getAllDataFromAEM(Query query, String collectionName,MongoTemplate clientMongoTemplate){
        return clientMongoTemplate.find(query, Document.class, collectionName);
    }

    public Boolean insertDataFromAEM(String collectionName, Document data,MongoTemplate clientMongoTemplate){
        InsertOneResult result =clientMongoTemplate.getCollection(collectionName).insertOne(data);
        return result.wasAcknowledged();
    }

    public Boolean updateDocumentAEM(Document document,MongoTemplate clientMongoTemplate) {
        Document query=new Document("pagePath|PagePath",document.get("pagePath|PagePath"));
        Document oldDocument=clientMongoTemplate.getDb().getCollection(Constants.AEM_COLLECTION_NAME).find(query).first();
        if(oldDocument!=null){
            UpdateResult result = clientMongoTemplate.getDb().getCollection(Constants.AEM_COLLECTION_NAME).updateOne(query,new Document("$set",document));
            return result.wasAcknowledged();
        }
        return false;
    }

    public Boolean deleteDataAEM(String clientName,MongoTemplate clientMongoTemplate){
       Query query=new Query();
       query.addCriteria(Criteria.where("clientName").is(clientName));
        DeleteResult result=clientMongoTemplate.remove(query,Constants.AEM_COLLECTION_NAME);
        return result.getDeletedCount()!=0;
    }
}