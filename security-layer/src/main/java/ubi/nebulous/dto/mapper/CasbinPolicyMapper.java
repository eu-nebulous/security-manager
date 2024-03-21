package ubi.nebulous.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ubi.nebulous.dto.CasbinPolicyDTO;
import ubi.nebulous.model.casbin.policy.CasbinPolicy;

@Mapper(componentModel = "cdi")
public interface CasbinPolicyMapper {

    CasbinPolicyMapper INSTANCE = Mappers.getMapper(CasbinPolicyMapper.class);

    @Mapping(target = "metadata.name", source = "name")
    CasbinPolicy dtoToModel(CasbinPolicyDTO dto);

    @Mapping(target = "name", source = "metadata.name")
    CasbinPolicyDTO modelToDto(CasbinPolicy model);
}