package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.customer.Address;
import com.commerce.e_commerce.domain.customer.UserDetail;
import com.commerce.e_commerce.dto.account.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapstructConfig.class)
public interface AccountMapper {

    // ---- UserDetail -> ProfileUpdateRequest (isteğe bağlı olarak kullanabilir)
    default ProfileUpdateRequest toProfileUpdateRequest(UserDetail d) {
        if (d == null) return null;
        return new ProfileUpdateRequest(d.getFirstName(), d.getLastName(), d.getPhone(), d.getAvatarUrl());
    }

    // ---- ProfileUpdateRequest -> UserDetail (patch)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target="firstName", source="firstName")
    @Mapping(target="lastName",  source="lastName")
    @Mapping(target="phone",     source="phone")
    @Mapping(target="avatarUrl", source="avatarUrl")
    void updateUserDetail(@MappingTarget UserDetail detail, ProfileUpdateRequest req);


    // ---- Address map’leri
    @Mapping(target="id", ignore = true)
    @Mapping(target="version", ignore = true)
    @Mapping(target="deleted", ignore = true)
    @Mapping(target="user", ignore = true) // serviste set et
    @Mapping(target="defaultShipping", ignore = true) // serviste mantıkla set et
    @Mapping(target="defaultBilling",  ignore = true)
    Address toAddress(AddressRequest req);

    default AddressResponse toAddressResponse(Address a) {
        return new AddressResponse(
                a.getId(),
                a.getFullName(),
                a.getLine1(),
                a.getLine2(),
                a.getCity(),
                a.getState(),
                a.getPostalCode(),
                a.getCountryCode(),
                a.isDefaultShipping(),
                a.isDefaultBilling()
        );
    }

    @IterableMapping(elementTargetType = AddressResponse.class)
    List<AddressResponse> toAddressResponseList(List<Address> list);

    // PATCH
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target="fullName",   source="fullName")
    @Mapping(target="line1",      source="line1")
    @Mapping(target="line2",      source="line2")
    @Mapping(target="city",       source="city")
    @Mapping(target="state",      source="state")
    @Mapping(target="postalCode", source="postalCode")
    @Mapping(target="countryCode",source="countryCode")
    void updateAddress(@MappingTarget Address a, AddressRequest req);
}
