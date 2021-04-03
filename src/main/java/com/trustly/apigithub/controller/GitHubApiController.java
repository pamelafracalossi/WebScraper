package com.trustly.apigithub.controller;

import com.trustly.apigithub.dto.GitHubDto;
import com.trustly.apigithub.service.GitHubApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GitHubApiController {

    @Autowired
    GitHubApiService gitHubApiService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GitHubDto>> getInformationRepositorieFiles(@RequestParam(value = "url") String url) {
        List<GitHubDto> repositorieInformation = new ArrayList<>();
        gitHubApiService.getInformationRepositorieFiles(url, repositorieInformation);
        return ResponseEntity.ok().body(repositorieInformation);
    }

}
