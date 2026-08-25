package com.dev_crazy.internal_distribution_app.admin_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinaryDetail {
    private String filename;
    private String type;
    private Integer filesize;
    private String checksum;
    private String checksumtype;
    private String keypath;
    private String url;
}
