package com.nur.service;

import com.nur.dto.RrpMemoERADTO;

import java.util.List;

public interface RrpMemoERAService {

    List<RrpMemoERADTO> getAllRrpMemoData();
    void uploadRrpMemo(List<RrpMemoERADTO> dtos);

    void updateRrpMemo(RrpMemoERADTO dto);


}
