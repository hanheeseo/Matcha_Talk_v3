package com.example.Matcha_Talk_v3.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalMessage {
    private String type;
    private String sender;
    private String receiver;
    private Object payload;
}
