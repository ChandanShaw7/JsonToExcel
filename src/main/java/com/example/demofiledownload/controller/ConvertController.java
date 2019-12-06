package com.example.demofiledownload.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.awt.image.ImageWatched;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class ConvertController {

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    @RequestMapping(value = "/jsonfile")
    public void jsonToExcel(@RequestParam(value = "files") MultipartFile file, HttpServletResponse response) throws IOException, ParseException {

        ObjectMapper objm = new ObjectMapper();

        //write file to particular directory
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
            try {
                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }


        File fetchfile = new File(String.valueOf(fileNameAndPath.getParent())+"/"+String.valueOf(fileNameAndPath.getFileName()));

        JSONParser parser = new JSONParser();
        Set<String> allkeysValue = new HashSet<>() ;

        FileReader jsonFile = new FileReader(fetchfile);

        Object obj = parser.parse(jsonFile);
        JSONArray jsonDetails = (JSONArray)obj;
        String json = jsonDetails.toString();
        List<TreeMap> map = objm.readValue(json, List.class);
//        System.out.println(map);
        Set<String> inside = new HashSet<>();

        for(Map em: map){
            inside.addAll(em.keySet());
            for(Object key: em.keySet()){
                String keyVal = (String) key;
                if (em.get(key) instanceof LinkedHashMap){
                    inside.remove(key);
                    inside.addAll(parseMap((LinkedHashMap) em.get(key),(String)key, keyVal));
                }
            }
            allkeysValue.addAll(inside);
        }
        System.out.println(allkeysValue);


        ArrayList<String> list = new ArrayList<>(allkeysValue);

        System.out.println(list);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("student Details");

//        int rownum = 0;
//        Row row = sheet.createRow(rownum++);
//        int cellnum = 0;
////        Cell cell = row.createCell(cellnum++);
//        for (int i = 0; i < list.size(); i++){
//            Cell cell = row.createCell(cellnum++);
//            cell.setCellValue(list.get(i));
//        }
//        cellnum = 0;
//        int len = list.size();
//        for(Map em: map){
//            row = sheet.createRow(rownum++);
//            cellnum = 0;
//            for(int i = 0; i < list.size(); i++){
//                Cell cell = row.createCell(cellnum++);
//                if (!em.containsKey(list.get(i))){
//                    cell.setCellValue("");
//                } else{
//                    cell.setCellValue(String.valueOf(em.get(list.get(i))));
//                }
//            }
//
//        }




        int rownum = 0;
        Row row = sheet.createRow(rownum++);
        int cellnum = 0;
        for (int i = 0; i < list.size(); i++){
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(list.get(i));
        }
        cellnum = 0;
        int len = list.size();
        for(Map em: map){
            row = sheet.createRow(rownum++);
            cellnum = 0;
            for(int i = 0; i < list.size(); i++){
                String[] mainKey = list.get(i).split("/");
                Cell cell = row.createCell(cellnum++);
                if(mainKey.length == 1){
                    if (!em.containsKey(list.get(i))){
                        cell.setCellValue("");
                    } else{
                        cell.setCellValue(String.valueOf(em.get(list.get(i))));
                    }
                } else {
                    cell.setCellValue(jsonKeyVal(mainKey, (LinkedHashMap) em));
                }
            }

        }



        try {
            // this Writes the workbook gfgcontribute
            FileOutputStream out = new FileOutputStream(new File(String.valueOf(fileNameAndPath.getParent())+"/"+"gfgcontribute.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("gfgcontribute.xlsx written successfully on disk.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Path excelFileNameAndPath = Paths.get(uploadDirectory, "gfgcontribute.xlsx");
        System.out.println("Excel file");
        System.out.println(excelFileNameAndPath.getParent());
        System.out.println(excelFileNameAndPath.getFileName());

        File excelFile = new File(String.valueOf(excelFileNameAndPath.getParent())+"/"+String.valueOf(excelFileNameAndPath.getFileName()));
        System.out.println(excelFile.getName());
        System.out.println(excelFile.getParent());

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +excelFile.getName());

        // Content-Length
        response.setContentLength((int) excelFile.length());

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(excelFile));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();

    }

    private String jsonKeyVal(String[] allKeys, LinkedHashMap em){
        String val = null;
        LinkedHashMap ema = em;
        for(String key: allKeys){
            if (ema.get(key) instanceof LinkedHashMap){
                ema = (LinkedHashMap) ema.get(key);
            }
            else{
                if(!ema.containsKey((Object)key)){
                    val = "";
                }else{
                    val = (String) ema.get(key);
                }
            }
        }
        return val;
    }

    private Set<String> parseMap(LinkedHashMap em, String key, String Val){
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
                allKeys.remove((Object)keyVal);
                keyVal = keyVal + "/"+(String) keys;
                System.out.println(keyVal);
                flag = false;
                parse((LinkedHashMap) em.get(keys), allKeys,flag, keyVal);
                keyVal = keyVal.replaceAll("/"+(String)keys,"");
            } else {
                allKeys.remove((Object)keyVal);
                allKeys.add(keyVal + "/"+(String) keys);
            }
        }
    }

}
//[address/geo/lng, website, address/geo/gp/lat, company/employeeId, address, address/geo/gp/loc, address/geo/suite, address/zipcode,
//        company/name, address/geo/houseNo, address/geo/lat, address/geo/gp/yoc, address/geo/city, phone, address/geo/pos, name,
//        company/catchPhrase, company, id, company/bs, address/geo/street, email, username]