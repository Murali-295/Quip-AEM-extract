package com.quip.Quip_AEM_extract.utilities;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoUtilities {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String getDatabase(){
        return mongoTemplate.getDb().getName();
    }

    public MongoIterable<String> getCollections(){
        return mongoTemplate.getDb().listCollectionNames();
    }

    public List<Document> getAllData(Query query, String collectionName){
        return mongoTemplate.find(query, Document.class, collectionName);
    }

    public Boolean insertData(String collectionName, Document data){
        InsertOneResult result =mongoTemplate.getCollection(collectionName).insertOne(data);
        return result.wasAcknowledged();
    }

    public Boolean updateDocument(Document document) {
        Document query=new Document("pagePath|PagePath",document.get("pagePath|PagePath"));
        Document oldDocument=mongoTemplate.getDb().getCollection(Constants.COLLECTION_NAME).find(query).first();
        if(oldDocument!=null){
            mongoTemplate.getDb().getCollection(Constants.COLLECTION_NAME).updateOne(oldDocument,new Document("$set",document));
            return true;
        }
        return false;
    }
}