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

    public Map<String,List<Map<String,String>>> storeData(String jsonData) throws JsonProcessingException {
        int failedCount=0;
        int successCount=0;

        Map<String,List<Map<String,String>>> status=new LinkedHashMap<>();

        JsonNode jsonNodes = objectMapper.readTree(jsonData);
        JsonNode databaseContent = jsonNodes.get(Constants.DBNAME);

        List<Map<String,String>> pageDetails=new ArrayList<>();
        List<Map<String,String>> errorResponse=new ArrayList<>();
        List<Map<String,String>>valuesStored =new ArrayList<>();

        for (Iterator<Map.Entry<String, JsonNode>> databases = databaseContent.fields(); databases.hasNext(); ) {
            Map.Entry<String, JsonNode> databaseEntry = databases.next();
            String collectionName = databaseEntry.getKey();
            ArrayNode pageData = (ArrayNode) databaseEntry.getValue();

            Map<String,String> response =new LinkedHashMap<>();

            for (JsonNode innerData : pageData) {
                Map<String, Object> result = objectMapper.convertValue(innerData, new TypeReference<Map<String, Object>>() {});
                result.put("page-name", collectionName);
                result.put("user-id", "");
                result.put("domain-name", "");
                Document document=new Document(result);
                if(mongoUtilities.insertData(Constants.COLLECTION_NAME, document)){
                    successCount++;
                    Map<String,String> page=new LinkedHashMap<>();
                    page.put(document.get("_id").toString(),document.getString("pagePath|PagePath"));
                    pageDetails.add(page);
                    continue;
                }
                response.put("response","Failed");
                response.put("page-name",collectionName);
                failedCount++;
                response.put("pagePath|PagePath",result.get("pagePath|PagePath").toString());
                errorResponse.add(response);

            }
            response.put("page-name",collectionName);
            response.put("Success Count",""+successCount);
            response.put("Failed Count",""+failedCount);
            valuesStored.add(response);
        }

        status.put("values-stored",valuesStored);
        status.put("error-responses",errorResponse);
        status.put("page -details",pageDetails);
        return status;
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

    public Map<String, Map<String, List<Document>>> getAllData() {

        Query query = new Query();
        query.fields().exclude("_id", "user-id", "domain-name");

        List<Document> documentList = mongoUtilities.getAllData(query, Constants.COLLECTION_NAME);

        Map<String, Map<String, List<Document>>> totalData = new HashMap<>();
        Map<String, List<Document>> collectionData = new HashMap<>();

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

        totalData.put(Constants.DBNAME, collectionData);
        return totalData;
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

}

