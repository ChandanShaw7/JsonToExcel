package com.example.demofiledownload.controller;

//import jdk.nashorn.internal.parser.JSONParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.json.JSONArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class FIleController {
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    @RequestMapping(value = "/download")
    public void downloadFile(HttpServletResponse response) throws IOException {
        Path fileNameAndPath = Paths.get(uploadDirectory, "flowableinstall.sh");

//        String filePath = fileNameAndPath.getFileName().toString();
//        System.out.println(fileNameAndPath.getFileName());
//        System.out.println(fileNameAndPath.getParent());
//        System.out.println("===============");

//        Resource resource = new UrlResource(fileNameAndPath.toUri());

//        return ResponseEntity.ok()
////                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);




//        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
//        System.out.println("fileName: " + fileName);
//        System.out.println("mediaType: " + mediaType);

        File file = new File(String.valueOf(fileNameAndPath.getFileName()));

        // Content-Type
        // application/pdf
//        response.setContentType(mediaType.getType());

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +file.getName());

        // Content-Length
        response.setContentLength((int) file.length());

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();

    }

    @RequestMapping(value = "/json")
    public void getJson() throws IOException, ParseException {

        ObjectMapper objm = new ObjectMapper();

        Path fileNameAndPath = Paths.get(uploadDirectory, "jsonfile.json");
//        System.out.println(fileNameAndPath.getFileName());
//        System.out.println(fileNameAndPath.getParent());
        File file = new File(String.valueOf(fileNameAndPath.getParent())+"/"+String.valueOf(fileNameAndPath.getFileName()));

        JSONParser parser = new JSONParser();
//        System.out.println(file.getPath());
//        System.out.println(file.getName());
//        System.out.println(file.getParent());
        Set<String> allkeysValue = new HashSet<>() ;

        FileReader jsonFile = new FileReader(file);
        Object obj = parser.parse(jsonFile);
        JSONArray jsonDetails = (JSONArray)obj;
        String json = jsonDetails.toString();
//        Map<String, String> map = objm.readValue(json, Map.class);
        List<TreeMap> map = objm.readValue(json, List.class);
        System.out.println(map);
        JSONArray ja = new JSONArray();
        ja.add(map);
        System.out.println(ja.toJSONString());
        for(Map em: map){
            allkeysValue.addAll(em.keySet());
            System.out.println("each row");
            System.out.println(em.keySet());
            System.out.println("row");
        }
        System.out.println(allkeysValue);
        ArrayList<String> list = new ArrayList<>(allkeysValue);

        System.out.println(list);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("student Details");

        int rownum = 0;
        Row row = sheet.createRow(rownum++);
        int cellnum = 0;
//        Cell cell = row.createCell(cellnum++);
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
                Cell cell = row.createCell(cellnum++);
                if (!em.containsKey(list.get(i))){
                    cell.setCellValue("");
                } else{
                    cell.setCellValue((String)em.get(list.get(i)));
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






//        for (Object entry: jsonDetails) {
//            String key = entry.toString();
////            System.out.println("Inside json");
//            JSONObject objl = (JSONObject)parser.parse(key);
////            Set allkeys= objl.keySet();
//            allkeysValue.addAll(objl.keySet());
////            for (Object ent: objl.keySet()) {
////                System.out.println(ent);
////                System.out.println(objl.get(ent));
////            }
////            System.out.println(allkeys);
//            System.out.println("==============");
//                System.out.println(key);
////            System.out.println(value);
//        }
//        ArrayList<String> list = new ArrayList<>(allkeysValue);
//
//        System.out.println(allkeysValue);
//        System.out.println(list);

//        System.out.println("Inside json");
//        System.out.println(jsonDetails);
//        jsonDetails.forEach(details -> System.out.println());
//        jsonDetails.forEach(details -> ArrayList<String> keys = (JSONObject)details.);



//        Map<String, >



    }

}
