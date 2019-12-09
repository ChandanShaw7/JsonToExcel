package com.example.demofiledownload.sources;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.core.io.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CreateExcel {

    //Creating excel file from json map data
    private boolean createExcelFile(Path fileNameAndPath, String uploadDirectory,String fileName, HttpServletResponse response) throws IOException, ParseException {
        ParseJson parseJson = new ParseJson();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(fileName.replace(".xlsx",""));

        List<TreeMap> JsonMap = parseJson.parseJson(fileNameAndPath);
        List<String> jsonKeys = parseJson.allKeys(JsonMap);

        int rownum = 0;
        Row row = sheet.createRow(rownum++);
        int cellnum = 0;
        for (int i = 0; i < jsonKeys.size(); i++) {
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(jsonKeys.get(i));
        }
        for (Map em : JsonMap) {
            row = sheet.createRow(rownum++);
            cellnum = 0;
            for (int i = 0; i < jsonKeys.size(); i++) {
                String[] mainKey = jsonKeys.get(i).split("/");
                Cell cell = row.createCell(cellnum++);
                if (mainKey.length == 1) {
                    if (!em.containsKey(jsonKeys.get(i))) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(String.valueOf(em.get(jsonKeys.get(i))));
                    }
                } else {
                    cell.setCellValue(jsonKeyVal(mainKey, (LinkedHashMap) em));
                }
            }
        }

        try {
            // this Writes the workbook
            FileOutputStream out = new FileOutputStream(new File(String.valueOf(fileNameAndPath.getParent()) + "/" + fileName));
            workbook.write(out);
            out.close();
            System.out.println(fileName+" written successfully on disk.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    public ResponseEntity<Resource> downloadExcelFile(Path fileNameAndPath, String uploadDirectory, HttpServletResponse response) throws IOException, ParseException {
        Random random = new Random();
        String second = new Date(System.currentTimeMillis()).getSeconds() +String.valueOf(random.nextInt());
        String fileName = fileNameAndPath.getFileName().toString();
        fileName = fileName.replace(".json",second+".xlsx");
        if(createExcelFile(fileNameAndPath,uploadDirectory,fileName, response))
        {
            Path excelFileNameAndPath = Paths.get(uploadDirectory, fileName);

//        File excelFile = new File(String.valueOf(excelFileNameAndPath.getParent()) + "/" + String.valueOf(excelFileNameAndPath.getFileName()));
//
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + excelFile.getName());
//
//        // Content-Length
//        response.setContentLength((int) excelFile.length());
//
//        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(excelFile));
//        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
//
//        byte[] buffer = new byte[1024];
//        int bytesRead = 0;
//        while ((bytesRead = inStream.read(buffer)) != -1) {
//            outStream.write(buffer, 0, bytesRead);
//        }
//        outStream.flush();
//        inStream.close();

            Resource resource = new UrlResource(excelFileNameAndPath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        return (ResponseEntity<Resource>) ResponseEntity.notFound();
    }
}
