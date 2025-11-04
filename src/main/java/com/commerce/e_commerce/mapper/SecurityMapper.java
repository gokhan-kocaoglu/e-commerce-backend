package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.customer.UserDetail;
import com.commerce.e_commerce.domain.security.Role;
import com.commerce.e_commerce.domain.security.User;
import com.commerce.e_commerce.dto.security.UserRegisterRequest;
import com.commerce.e_commerce.dto.security.UserResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SecurityMapper {

    // Register -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // service: passwordEncoder set eder
    @Mapping(target = "roles", ignore = true)        // service: ROLE_USER set eder
    User toUser(UserRegisterRequest req);

    // User -> UserResponse (UserDetail YOK)
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName",  ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "roles", expression = "java(toRoleNames(user.getRoles()))")
    UserResponse toUserResponse(User user);

    // User + UserDetail -> UserResponse (UserDetail VAR)
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "detail.firstName")
    @Mapping(target = "lastName",  source = "detail.lastName")
    @Mapping(target = "avatarUrl", source = "detail.avatarUrl")
    @Mapping(target = "roles", expression = "java(toRoleNames(user.getRoles()))")
    UserResponse toUserResponse(User user, UserDetail detail);

    // helper
    default List<String> toRoleNames(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) return List.of();
        return roles.stream().map(Role::getName).toList();
    }
}
