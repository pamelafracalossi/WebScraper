package com.trustly.apigithub.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/greeting")
public class WebScraperController {


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findAll() {

        StringBuilder content = getFiles("https://github.com/angular/protractor/tree/5.4.1");

        return ResponseEntity.ok().body(content.toString());
    }

    private StringBuilder getFiles(String urlGitHub) {
        StringBuilder content = new StringBuilder();
        List<String> urlFilesList = new ArrayList<>();
        List<String> urlFolderList = new ArrayList<>();

        try {
            URL url = new URL(urlGitHub);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            boolean c = true;
            while ((line = bufferedReader.readLine()) != null && c) {
                StringBuilder currentLine = new StringBuilder();
                currentLine.append(line + "\n");
                content.append(line + "\n");

                if (content.toString().contains("class=\"sr-only\"") && !content.toString().contains("id=\"readme\"")) {
                    if (currentLine.toString().contains("href=") && currentLine.toString().contains("Link--primary")) {
                        if (currentLine.toString().contains("/tree/")) {
                            urlFolderList.add(getUrl(currentLine.toString()));
                        } else {
                            if (currentLine.toString().contains("/blob/")) {
                                urlFilesList.add(getUrl(currentLine.toString()));
                            }
                        }
                    }
                } else {
                    if (content.toString().contains("class=\"sr-only\"")) {
                        c = false;
                    }
                }
            }
            bufferedReader.close();

            for (String folder : urlFolderList) {
                StringBuilder urlFolder = new StringBuilder();
                urlFolder.append("https://github.com");
                urlFolder.append(folder);
                    getFiles(urlFolder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(urlFilesList);
        return content;
    }


    private String getUrl(String tagHtml) {
        Pattern p = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        Matcher pageMatcher = p.matcher(tagHtml);

        if (pageMatcher.find()) {
            String linktTag = pageMatcher.group().replace(" href=\"", "");
            return linktTag.substring(0, linktTag.length() - 1);
        }
        return "";
    }
}
