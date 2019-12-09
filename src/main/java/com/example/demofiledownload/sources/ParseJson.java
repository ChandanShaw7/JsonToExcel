package com.example.demofiledownload.sources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ParseJson {


    //Package private method
    //parsing json data to map
    List<TreeMap> jsonParse(Path fileNameAndPath) throws IOException, ParseException {
        File fetchFile = new File(fileNameAndPath.getParent()+"/"+fileNameAndPath.getFileName());
        ObjectMapper mapper = new ObjectMapper();
        JSONParser parser = new JSONParser();
        FileReader jsonFile = new FileReader(fetchFile);

        Object obj = parser.parse(jsonFile);
        JSONArray jsonDetails = (JSONArray)obj;
        String json = jsonDetails.toString();
        List<TreeMap> map = mapper.readValue(json, List.class);
        return map;
    }

    //package private method
    //extracting all the keys
        List<String> allKeys(List<TreeMap> map){
            Set<String> allKeysValue = new HashSet<>() ;
            Set<String> inside = new HashSet<>();
            for(Map em: map){
                inside.addAll(em.keySet());
                for(Object key: em.keySet()){
                    String keyVal = (String) key;
                    if (em.get(key) instanceof LinkedHashMap){
                        inside.remove(key);
                        inside.addAll(parseMap((LinkedHashMap) em.get(key), keyVal));
                    }
                }
                allKeysValue.addAll(inside);
            }
            return new ArrayList<>(allKeysValue);
        }

    private Set<String> parseMap(LinkedHashMap em, String Val){
        Set<String> allKeys = new HashSet<>();
        Boolean flag = false;
        parse(em,allKeys,flag, Val);
        return allKeys;
    }

    private void parse(LinkedHashMap em, Set<String> allKeys, Boolean flag, String keyVal){
    if (flag)
        return;
    flag = true;
    for(Object keys: em.keySet()) {
        if (em.get(keys) instanceof LinkedHashMap) {
            allKeys.remove(keyVal);
            keyVal = keyVal + "/"+(String) keys;
            flag = false;
            parse((LinkedHashMap) em.get(keys), allKeys,flag, keyVal);
            keyVal = keyVal.replaceAll("/"+ keys,"");
        } else {
            allKeys.remove(keyVal);
            allKeys.add(keyVal + "/"+ keys);
            }
        }
    }
}

