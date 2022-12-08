package com.communication.plateforme.services;



import com.project.authentification.model.Role;
import com.project.authentification.model.enums.RoleEnum;

import java.util.List;

public interface IRoleService {
    Role addRole(RoleEnum roleEnum);

    boolean roleExist(RoleEnum roleEnum);
    List<Role> listRole();
    List<Role> listRoleByRoleEnumsList(List<RoleEnum> roleEnums);
    Role findRoleByRoleEnum(RoleEnum roleEnum);

}
