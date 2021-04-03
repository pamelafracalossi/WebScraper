package com.trustly.apigithub.dto;

import java.util.ArrayList;
import java.util.List;

public class GitHubRepositorieDto {

    private List<GitHubDto> repositorieInformation = new ArrayList<>();

    public List<GitHubDto> getRepositorieInformation() {
        return repositorieInformation;
    }

    public void setRepositorieInformation(List<GitHubDto> repositorieInformation) {
        this.repositorieInformation = repositorieInformation;
    }
}
