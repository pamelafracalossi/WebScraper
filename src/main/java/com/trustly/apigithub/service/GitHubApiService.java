package com.trustly.apigithub.service;

import com.trustly.apigithub.dto.GitHubDto;

import java.util.List;

public interface GitHubApiService {

    List<GitHubDto> getInformationRepositorieFiles(String url, List<GitHubDto> repositorieInformation);
}
