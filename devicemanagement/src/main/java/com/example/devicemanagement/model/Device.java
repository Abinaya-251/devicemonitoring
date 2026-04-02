package com.example.devicemanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "devices") // This annotation indicates that this class will be stored in a MongoDB collection named "devices"
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
public class Device {

    @Id // MongoDB will automatically generate a unique ID for each device
    private String id;

    private String name;
    private String type;
    private String ipAddress;
    private String location;
    private String status;

    public String getId() {
        return id;
    }
    

}