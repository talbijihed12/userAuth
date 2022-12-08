package com.project.authentification.payload.request;

import com.project.authentification.model.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String username;
    private List<RoleEnum> roles;
}
