package de.thbingen.epro.model.mapper;

import de.thbingen.epro.model.business.Privilege;
import de.thbingen.epro.model.dto.PrivilegeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrivilegeMapper {

    public PrivilegeDto PrivilegeToDto(Privilege privilege);

    @Mapping(target = "role", ignore = true)
    public Privilege DtoToPrivilege(PrivilegeDto privilegeDto);

}