package com.nms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponseDto {
    @NonNull
    private String username;
    private String message;
    private ObjectId id;
}
