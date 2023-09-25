package com.iag.reactive.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gender {
    private String name;
    private String gender;
    private float probability;
    private int count;
}
