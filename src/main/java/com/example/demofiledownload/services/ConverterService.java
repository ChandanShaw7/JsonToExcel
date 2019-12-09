package com.example.demofiledownload.services;

import com.example.demofiledownload.sources.CreateExcel;
import com.example.demofiledownload.sources.ParseJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ConverterService {

    public ResponseEntity<Resource> ConvertJsonToExcel(MultipartFile file, HttpServletResponse response, String uploadDirectory) throws IOException, ParseException {
//        ParseJson parseJson = new ParseJson();
        CreateExcel createExcel = new CreateExcel();
        //write file to particular directory
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createExcel.downloadExcelFile(fileNameAndPath, uploadDirectory, response);
    }
}

