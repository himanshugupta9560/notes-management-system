package com.nms.dtos;

import com.nms.entities.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequestDto {
    @NonNull
    private String username;
    private String password;
    private Role role;
}
