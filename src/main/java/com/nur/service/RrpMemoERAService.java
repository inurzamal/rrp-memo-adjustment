package com.nur.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RrpMemoERAService {
    void uploadRrpMemo(MultipartFile file) throws IOException;
}
