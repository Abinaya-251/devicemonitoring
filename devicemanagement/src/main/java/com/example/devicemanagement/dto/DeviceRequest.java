package com.example.devicemanagement.dto;

import lombok.Data;

  @Data
  
public class DeviceRequest {

    private String name;
    private String type;
    private String ipAddress;
    private String location;
    private String status;
}
