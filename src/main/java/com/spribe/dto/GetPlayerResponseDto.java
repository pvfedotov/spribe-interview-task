package com.spribe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spribe.models.Player;

@JsonIgnoreProperties(ignoreUnknown = false)
public class GetPlayerResponseDto extends Player {
    public GetPlayerResponseDto(){
        // Default constructor for serialization/deserialization
    }
}
