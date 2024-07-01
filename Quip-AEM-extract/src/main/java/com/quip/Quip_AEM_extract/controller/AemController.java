package com.quip.Quip_AEM_extract.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quip.Quip_AEM_extract.service.AemConnection;
import com.quip.Quip_AEM_extract.service.AemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
public class AemController {

    @Autowired
    private AemConnection aemConnection;

    @Autowired
    private AemService aemService;

    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/quip/v2/seo")
    public JsonNode getAEMData(@RequestParam String sitePath) throws IOException {
        URL url = new URL(sitePath);
        return objectMapper.readTree(aemConnection.getConnection(url));
    }

    @PostMapping("/quip/v2/seo")
    public List<Map<String, String>> postData(@RequestParam String sitePath) throws IOException {
        URL url = new URL(sitePath);
        return aemService.storeData(aemConnection.getConnection(url));
    }

    @PutMapping("/quip/v2/seoPage")
    public ObjectNode updateData(@RequestBody ObjectNode siteData) {
        return objectMapper.convertValue(aemService.updateData(siteData), ObjectNode.class);
    }

    @PostMapping("/quip/v2/seo_user")
    public List<Map<String, String>> postData1(@RequestParam String sitePath, @RequestParam String user_name, @RequestParam String user_domain) throws IOException {
        URL url = new URL(sitePath);
        String data = aemConnection.getConnection(url);
        return aemService.storeData1(user_name, user_domain, data);
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