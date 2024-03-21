package ubi.nebulous.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ubi.nebulous.dto.CasbinModelDTO;
import ubi.nebulous.model.casbin.model.CasbinModel;

@Mapper(componentModel = "cdi")
public interface CasbinModelMapper {

    CasbinModelMapper INSTANCE = Mappers.getMapper(CasbinModelMapper.class);

    @Mapping(target = "metadata.name", source = "name")
    CasbinModel dtoToModel(CasbinModelDTO dto);

    @Mapping(target = "name", source = "metadata.name")
    CasbinModelDTO modelToDto(CasbinModel model);
}
