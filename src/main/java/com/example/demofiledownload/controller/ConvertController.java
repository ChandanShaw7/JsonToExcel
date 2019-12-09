package com.example.demofiledownload.controller;


import com.example.demofiledownload.services.ConverterService;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


@RestController
public class ConvertController {

    @Autowired
    private ConverterService converterService;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";


    @RequestMapping(value = "/jsonfile")
    public ResponseEntity<Resource> jsonToExcel(@RequestParam(value = "files") MultipartFile file, HttpServletResponse response) throws IOException, ParseException {

        return converterService.ConvertJsonToExcel(file, response, uploadDirectory);

        }
    }