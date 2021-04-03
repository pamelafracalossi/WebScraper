package com.trustly.apigithub.controller;

import com.trustly.apigithub.dto.GitHubDto;
import com.trustly.apigithub.dto.GitHubRepositorieDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/greeting")
public class WebScraperController {


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GitHubDto>> findAll() {

        GitHubRepositorieDto repositorieInformation = new GitHubRepositorieDto();
        getFiles("https://github.com/Prempeh-Gyan/WebScraper", repositorieInformation);

        return ResponseEntity.ok().body(repositorieInformation.getRepositorieInformation());
    }

    private StringBuilder getFiles(String urlGitHub, GitHubRepositorieDto repositorieInformation) {
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
                                getInformationFile(getUrl(currentLine.toString()), repositorieInformation);
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
                getFiles(urlFolder.toString(), repositorieInformation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(urlFilesList);
        return content;
    }

    private void getInformationFile(String partialUrlFile, GitHubRepositorieDto repositorieInformation) {
        StringBuilder content = new StringBuilder();
        StringBuilder urlFile = new StringBuilder();
        urlFile.append("https://github.com");
        urlFile.append(partialUrlFile);
        GitHubDto gitHubDto = new GitHubDto();

        try {

            URL url = new URL(urlFile.toString());
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            boolean c = true;
            while ((line = bufferedReader.readLine()) != null && c) {
                StringBuilder currentLine = new StringBuilder();
                currentLine.append(line + "\n");
                content.append(line + "\n");

                if (content.toString().contains("id=\"blob-path\"") && !content.toString().contains("id=\"raw-url\"")) {
                    if (currentLine.toString().contains("class=\"final-path\"")) {
                        gitHubDto.setExtension(getExtention(currentLine.toString()));
                    }
                    if (currentLine.toString().contains("lines")) {
                        gitHubDto.setLines(getLines(currentLine.toString()));
                    }
                    if (currentLine.toString().contains("Bytes") || currentLine.toString().contains("KB")) {
                        gitHubDto.setBytes(getBytes(currentLine.toString()));
                    }
                } else {
                    if (content.toString().contains("id=\"blob-path\"")) {
                        c = false;
                    }
                }
            }
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Long getBytes(String toString) {
        Pattern pattern = Pattern.compile("    (\\S+) Bytes");
        Pattern pattern2 = Pattern.compile("    (\\S+) KB");
        Matcher matcher = pattern.matcher(toString);
        Matcher matcher2 = pattern2.matcher(toString);
        if (matcher.find()) {
            String result = matcher.group(1);
            System.out.println(result);
            return Long.valueOf(result);
        } else {
            if (matcher2.find()) {
                String result = matcher2.group(1);
                System.out.println(result);
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                Number number = 0;
                try {
                    number = format.parse(result);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Double d = number.doubleValue() * 1000;
                return (d.longValue());
            }
        }
        return 0L;
    }

    private Long getLines(String toString) {
        Pattern pattern = Pattern.compile("      (\\S+) lines");
        Matcher matcher = pattern.matcher(toString);
        if (matcher.find()) {
            String result = matcher.group(1);
            System.out.println(result);
            return Long.valueOf(result);
        }
        return 0L;
    }

    private String getExtention(String toString) {
        Pattern pattern = Pattern.compile("class=\"final-path\">(\\S+)</strong>");
        Matcher matcher = pattern.matcher(toString);
        if (matcher.find()) {
            String result = matcher.group(1);
            int index = result.lastIndexOf('.');
            if (index > 0) {
                String extension = result.substring(index);
                System.out.println(extension);
                return extension;
            }
        }
        return "";
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
