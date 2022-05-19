package com.nhwb.breeze.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResult implements Serializable {
    private Long id;
    private String description;
    private Boolean grant =false;
}
