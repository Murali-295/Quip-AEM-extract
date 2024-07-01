package com.quip.Quip_AEM_extract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quip.Quip_AEM_extract.utilities.Constants;
import com.quip.Quip_AEM_extract.utilities.MongoUtilities;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AemService {
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoUtilities mongoUtilities;

    public List<Map<String, String>> storeData1(String user_name, String user_domain, String jsonData) throws JsonProcessingException {
        int failedCount = 0;
        int successCount = 0;
        List<Map<String, String>> status = new ArrayList<>();

        JsonNode jsonNodes = objectMapper.readTree(jsonData);
        JsonNode databaseContent = jsonNodes.get(Constants.DBNAME);

        for (Iterator<Map.Entry<String, JsonNode>> databases = databaseContent.fields(); databases.hasNext(); ) {
            Map.Entry<String, JsonNode> databaseEntry = databases.next();
            String collectionName = databaseEntry.getKey();
            ArrayNode pageData = (ArrayNode) databaseEntry.getValue();

            Map<String, String> response = new LinkedHashMap<>();

            for (JsonNode innerData : pageData) {
                Map<String, Object> result = objectMapper.convertValue(innerData, new TypeReference<Map<String, Object>>() {
                });
                result.put("page-name", collectionName);
                result.put("user_name", user_name);
                result.put("user_domain", user_domain);
                if (mongoUtilities.insertData(Constants.COLLECTION_NAME, new Document(result))) {
                    successCount++;
                    continue;
                }
                response.put("response", "Failed");
                response.put("page-name", collectionName);
                failedCount++;
                response.put("pagePath|PagePath", result.get("pagePath|PagePath").toString());
                status.add(response);
            }
            response.put("Success Count", "" + successCount);
            response.put("Failed Count", "" + failedCount);
            status.add(response);
        }
        return status;

    }

    public List<Map<String, String>> storeData(String jsonData) throws JsonProcessingException {
        int failedCount = 0;
        int successCount = 0;
        List<Map<String, String>> status = new ArrayList<>();

        JsonNode jsonNodes = objectMapper.readTree(jsonData);
        JsonNode databaseContent = jsonNodes.get(Constants.DBNAME);

        for (Iterator<Map.Entry<String, JsonNode>> databases = databaseContent.fields(); databases.hasNext(); ) {
            Map.Entry<String, JsonNode> databaseEntry = databases.next();
            String collectionName = databaseEntry.getKey();
            ArrayNode pageData = (ArrayNode) databaseEntry.getValue();

            Map<String, String> response = new LinkedHashMap<>();

            for (JsonNode innerData : pageData) {
                Map<String, Object> result = objectMapper.convertValue(innerData, new TypeReference<Map<String, Object>>() {
                });
                result.put("page-name", collectionName);
                result.put("user-id", "");
                result.put("domain-name", "");
                if (mongoUtilities.insertData(Constants.COLLECTION_NAME, new Document(result))) {
                    successCount++;
                    continue;
                }
                response.put("response", "Failed");
                response.put("page-name", collectionName);
                failedCount++;
                response.put("pagePath|PagePath", result.get("pagePath|PagePath").toString());
                status.add(response);
            }
            response.put("Success Count", "" + successCount);
            response.put("Failed Count", "" + failedCount);
            status.add(response);
        }
        return status;

    }

    public Map<String, Map<String, List<Document>>> getData(ArrayNode sitePaths) {

        Map<String, Map<String, List<Document>>> dbData = new HashMap<>();
        Map<String, List<Document>> totalData = new HashMap<>();

        for (int i = 0; i < sitePaths.size(); i++) {
            String sitePath = sitePaths.get(i).get("sitePath").asText();
            Query query = new Query();
            query.fields().exclude("_id", "page-name", "user-id", "domain-name");
            query.addCriteria(Criteria.where("page-name").is(sitePath));

            List<Document> coll = mongoUtilities.getAllData(query, Constants.COLLECTION_NAME);
            totalData.put(sitePath, coll);
        }
        dbData.put(Constants.DBNAME, totalData);

        return dbData;
    }

    public Map<String, String> updateData(ObjectNode siteData) {
        Document document = objectMapper.convertValue(siteData, Document.class);
        Map<String, String> status = new HashMap<>();
        if (mongoUtilities.updateDocument(document)) {
            status.put("update", "success");
        } else {
            status.put("update", "failed");
            status.put("response", "No page found with the specified pagePath");
        }
        return status;
    }

    public Map<String,Map<String,List<Document>>> getAllData() {

        Query query = new Query();
        query.fields().exclude("_id","user-id","domain-name");

        List<Document> documentList=mongoUtilities.getAllData(query,Constants.COLLECTION_NAME);

        Map<String,Map<String,List<Document>>> totalData=new HashMap<>();
        Map<String,List<Document>> collectionData=new HashMap<>();

        for (Document document : documentList) {
            //System.out.println(document);
            String pageName = document.getString("page-name");
            document.remove("page-name");

            if (collectionData.containsKey(pageName)) {
                List<Document> innerData = collectionData.get(pageName);
                innerData.add(document);
                continue;
            }
            List<Document> innerData = new ArrayList<>();
            innerData.add(document);
            collectionData.put(pageName, innerData);
        }

        totalData.put(Constants.DBNAME,collectionData);
        return totalData;
    }
}

/*JsonNode jsonNodes=objectMapper.readTree(jsonData);
        JsonNode databaseContent=jsonNodes.get(Constants.DBNAME);

        Iterator<String> collectionContent=databaseContent.fieldNames();

        while (collectionContent.hasNext()){
            String collectionName=collectionContent.next();
            ArrayNode pageData=(ArrayNode) databaseContent.get(collectionName);

            for(int i=0;i<pageData.size();i++){
                JsonNode innerData=pageData.get(i);
                Map<String, Object> result = objectMapper.convertValue(innerData, new TypeReference<>() {
                });
                result.put("page-name",collectionName);
                result.put("user-id","");
                result.put("domain-name","");
                mongoUtilities.insertData(Constants.COLLECTION_NAME,new Document(result));
            }
        }*/

/* int failedCount=0;
        int successCount=0;

        JsonNode jsonNodes=objectMapper.readTree(jsonData);

        List<Map<String,String>> status=new ArrayList<>();
        JSONObject data=jsonData.getJSONObject(Constants.DBNAME);

        for(String collectionName: data.keySet()){
            Map<String,String> response =new LinkedHashMap<>();
            JSONArray keyValues=data.getJSONArray(collectionName);
            for(int i=0;i<keyValues.length();i++){
                Document document=new Document(keyValues.getJSONObject(i).toMap());
                document.put("page-name",collectionName);
                if(mongoUtilities.insertData(Constants.COLLECTION_NAME,document)){
                    successCount++;
                    continue;
                }
                response.put("response","Failed");
                response.put("page-name",collectionName);
                failedCount++;
                response.put("pagePath|PagePath",document.getString("pagePath|PagePath"));
                status.add(response);
            }
            response.put("Success Count",""+successCount);
            response.put("Failed Count",""+failedCount);
            status.add(response);
        }

        return status;*/

/*
 -------------- get data--------------------
 public Map<String,Map<String,List<Document>>> getData(URL url){
        Map<String,Map<String,List<Document>>> dbData=new HashMap<>();
        Map<String,List<Document>> totalData=new HashMap<>();
        Query query = new Query();

        for(String collectionName: mongoUtilities.getCollections()){
            query.fields().exclude("_id");
            List<Document> coll=mongoUtilities.getAllData(query,collectionName);
            totalData.put(collectionName,coll);
        }
        dbData.put(mongoUtilities.getDatabase(),totalData);
        return dbData;
    }*/



/*
 -------------- store data with constant dbname and creates each collection for each page--------------------

List<Map<String,String>> status=new ArrayList<>();
        JSONObject data=jsonData.getJSONObject(Constants.DBNAME);

        for(String collectionName: data.keySet()){
            System.out.println(collectionName);
            Map<String,String> response =new LinkedHashMap<>();

            JSONArray keyValues=data.getJSONArray(collectionName);
            List<Document> documentList=new ArrayList<>();
            if (keyValues.isEmpty() || keyValues.isNull(0) || keyValues.length()==0){
                response.put("Database Created",Constants.DBNAME);
                response.put("Collection Created",collectionName);
                response.put("response","No values to insert.");
                status.add(response);
                continue;
            }

            for(int i=0;i<keyValues.length();i++){
                documentList.add(new Document(keyValues.getJSONObject(i).toMap()));
            }
            mongoUtilities.insertData(collectionName,documentList);
            response.put("Database Created",Constants.DBNAME);
            response.put("Collection Created",collectionName);
            response.put("response","Values Inserted: "+keyValues.length());
            status.add(response);
        }
        return status;
    }*/

/*
public List<Map<String,String>> storeData(JSONObject jsonData) {
        ObjectMapper objectMapper=new ObjectMapper();

        List<Map<String,String>> status=new ArrayList<>();

        for(String databaseName: jsonData.keySet()){
            Map<String,String> response =new LinkedHashMap<>();

            JSONObject subData=jsonData.getJSONObject(databaseName);
            response.put("Database Created",databaseName);

            for(String collectionName: subData.keySet()){
                mongoTemplate.getDb().getCollection(collectionName);
                JSONArray keyValues=subData.getJSONArray(collectionName);
                response.put("Collection Created",collectionName);

                List<Document> documentList=new ArrayList<>();

                for(int i=0;i<keyValues.length();i++){
                    JSONObject inputData=keyValues.getJSONObject(i);
                    documentList.add(new Document(inputData.toMap()));
                }
                mongoTemplate.getCollection(collectionName).insertMany(documentList);
                response.put("Values Inserted",""+keyValues.length());
                status.add(response);
            }
        }
        return status;
    }*/