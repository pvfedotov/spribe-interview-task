package com.spribe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spribe.models.Player;

@JsonIgnoreProperties(ignoreUnknown = false)
public class CreatePlayerResponseDto extends Player {

    public CreatePlayerResponseDto(){
        // Default constructor for serialization/deserialization
    }
}
