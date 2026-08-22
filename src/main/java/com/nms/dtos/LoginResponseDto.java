package com.nms.dtos;

import com.nms.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String jwt;
    private ObjectId id;
    // private Role role;

}
