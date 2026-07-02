package com.example.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量删除请求体
 */
@Data
public class DeleteBatchRequest {
    private List<Integer> ids;
}

