package com.quip.Quip_AEM_extract.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quip.Quip_AEM_extract.service.AemConnection;
import com.quip.Quip_AEM_extract.service.AemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

@RestController
public class AemController {

    @Autowired
    private AemConnection aemConnection;

    @Autowired
    private AemService aemService;

    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/quip/v2/seo")
    public JsonNode getAEMData(@RequestParam String sitePath) throws IOException, URISyntaxException {
        if (sitePath.trim().isEmpty()){
            HashMap<String, String> response=new HashMap<>();
            response.put("status","Failed");
            response.put("Message","Invalid URL- URl is empty.");

            return objectMapper.convertValue(response,JsonNode.class);
        }

        URL url = new URL(sitePath);
        if (aemConnection.getConnection(url)==null || Objects.equals(aemConnection.getConnection(url), "")){
            HashMap<String, String> response=new HashMap<>();
            response.put("status","Failed");
            response.put("Message","Invalid URL.");

            return objectMapper.convertValue(response,JsonNode.class);
        }
        return objectMapper.readTree(aemConnection.getConnection(url));
    }

    @PostMapping("/quip/v2/seo")
    public ObjectNode postData(@RequestParam String sitePath) throws IOException {

        if (sitePath.trim().isEmpty()){
            HashMap<String, String> response=new HashMap<>();
            response.put("status","Failed");
            response.put("Message","Invalid URL");

            return objectMapper.convertValue(response,ObjectNode.class);
        }

        URL url = new URL(sitePath);
        if (aemConnection.getConnection(url)==null || aemConnection.getConnection(url).equals(" ")){
            HashMap<String, String> response=new HashMap<>();
            response.put("status","Failed");
            response.put("Message","Invalid URL");

            return objectMapper.convertValue(response,ObjectNode.class);
        }
        return objectMapper.convertValue(aemService.storeData(aemConnection.getConnection(url)),ObjectNode.class);
    }

    @PutMapping("/quip/v2/seoPage")
    public ObjectNode updateData(@RequestBody ObjectNode siteData) {
        return objectMapper.convertValue(aemService.updateData(siteData), ObjectNode.class);
    }

    @GetMapping("/quip/v2/seoPage/getAllData")
    public ObjectNode getAllData(){
        return objectMapper.convertValue(aemService.getAllData(),ObjectNode.class);
    }

   /*
    @GetMapping("/quip/v2/seoPage")
    public ObjectNode getData(@RequestBody ArrayNode sitePath){
        return objectMapper.convertValue(aemDataService.getData(sitePath),ObjectNode.class);
    }*/

}