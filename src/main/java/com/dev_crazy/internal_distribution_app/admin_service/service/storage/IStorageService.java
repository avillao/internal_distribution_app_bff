package com.dev_crazy.internal_distribution_app.admin_service.service.storage;

import com.dev_crazy.internal_distribution_app.admin_service.model.BinaryDetail;

import java.io.InputStream;

public interface IStorageService {
    void saveObject(BinaryDetail binaryDetail, InputStream inputStream);
    String getObject(String filepath);
}
