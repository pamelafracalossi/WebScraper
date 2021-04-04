package com.trustly.apigithub.service;

import com.trustly.apigithub.dto.GitHubDto;
import org.springframework.stereotype.Component;

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

@Component("GitHubApiServiceImpl")
public class GitHubApiServiceImpl implements GitHubApiService {

    private static final String FILE_LIST = "id=\"files\"";
    private static final String README = "id=\"readme\"";
    private static final String HREF = "href=";
    private static final String LINK_PRIMARY = "Link--primary";
    private static final String TREE = "/tree/";
    private static final String BLOB = "/blob/";
    private static final String URL_GIT_HUB = "https://github.com";
    private static final String BREADCRUMB = "id=\"blob-path\"";
    private static final String RAW = "id=\"c\"";

    @Override
    public List<GitHubDto> getInformationRepositorieFiles(String urlGitHub, List<GitHubDto> repositorieInformation) {
        StringBuilder content = new StringBuilder();
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

                if (content.toString().contains(FILE_LIST) && !content.toString().contains(README)) {
                    if (currentLine.toString().contains(HREF) && currentLine.toString().contains(LINK_PRIMARY)) {
                        if (currentLine.toString().contains(TREE)) {
                            urlFolderList.add(getUrl(currentLine.toString()));
                        } else {
                            if (currentLine.toString().contains(BLOB)) {
                                getInformationFile(getUrl(currentLine.toString()), repositorieInformation);
                            }
                        }
                    }
                } else {
                    if (content.toString().contains(FILE_LIST)) {
                        c = false;
                    }
                }
            }
            bufferedReader.close();

            for (String folder : urlFolderList) {
                StringBuilder urlFolder = new StringBuilder();
                urlFolder.append(URL_GIT_HUB);
                urlFolder.append(folder);
                getInformationRepositorieFiles(urlFolder.toString(), repositorieInformation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repositorieInformation;
    }

    private void getInformationFile(String partialUrlFile, List<GitHubDto> repositorieInformation) {
        StringBuilder content = new StringBuilder();
        StringBuilder urlFile = new StringBuilder();
        urlFile.append(URL_GIT_HUB);
        urlFile.append(partialUrlFile);
        GitHubDto gitHubDto = new GitHubDto();

        try {
            URL url = new URL(urlFile.toString());
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            boolean c = true;
            while ((line = bufferedReader.readLine()) != null && c) {
                content.append(line + "\n");
                if (content.toString().contains(BREADCRUMB) && content.toString().contains(RAW)) {
                    c = false;
                }
            }
            bufferedReader.close();
            gitHubDto.setExtension(getExtention(content.toString()));
            gitHubDto.setLines(getLines(content.toString()));
            gitHubDto.setBytes(getBytes(content.toString()));
            putInformationFileInRepositorieInformation(gitHubDto, repositorieInformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putInformationFileInRepositorieInformation(GitHubDto gitHubDto, List<GitHubDto> repositorieInformation) {
        GitHubDto gitHub = repositorieInformation.stream()
                .filter(r -> r.getExtension().equals(gitHubDto.getExtension()))
                .findAny()
                .orElse(null);

        if (gitHub != null) {
            gitHub.setLines(gitHub.getLines() + gitHubDto.getLines());
            gitHub.setBytes(gitHub.getLines() + gitHubDto.getLines());
            gitHub.setCount(gitHub.getCount() + 1);
        } else {
            repositorieInformation.add(gitHubDto);
        }
    }


    private Long getBytes(String line) {
        Pattern bytes = Pattern.compile(" {4}(\\S+) Bytes");
        Pattern kb = Pattern.compile(" {4}(\\S+) KB");
        Pattern mb = Pattern.compile(" {4}(\\S+) MB");
        Matcher matcherBytes = bytes.matcher(line);
        Matcher matcherKb = kb.matcher(line);
        Matcher matcherMb = mb.matcher(line);
        if (matcherBytes.find()) {
            String result = matcherBytes.group(1);
            return Long.valueOf(result);
        } else {
            if (matcherKb.find()) {
                String result = matcherKb.group(1);
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                Number number = 0;
                try {
                    number = format.parse(result);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Double d = number.doubleValue() * 1000;
                return (d.longValue());
            } else {
                if (matcherMb.find()) {
                    String result = matcherMb.group(1);
                    NumberFormat format = NumberFormat.getInstance(Locale.US);
                    Number number = 0;
                    try {
                        number = format.parse(result);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Double d = number.doubleValue() * 1000000;
                    return (d.longValue());
                }
            }
            return 0L;
        }
    }

    private Long getLines(String toString) {
        Pattern pattern = Pattern.compile(" {6}(\\S+) lines");
        Matcher matcher = pattern.matcher(toString);
        if (matcher.find()) {
            String result = matcher.group(1);
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
            if (index >= 0) {
                String extension = result.substring(index);
                return extension;
            }
        }
        return "without extension";
    }

    private String getUrl(String tagHtml) {
        Pattern p = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        Matcher pageMatcher = p.matcher(tagHtml);

        if (pageMatcher.find()) {
            String linktTag = pageMatcher.group().replace(" href=\"", "");
            return linktTag.substring(0, linktTag.length() - 1);
        }
        return null;
    }
}
