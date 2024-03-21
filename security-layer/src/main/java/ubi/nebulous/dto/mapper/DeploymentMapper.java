package ubi.nebulous.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ubi.nebulous.dto.DeploymentDTO;
import ubi.nebulous.model.DeploymentModel;

@Mapper
public interface DeploymentMapper {

    DeploymentMapper INSTANCE = Mappers.getMapper(DeploymentMapper.class);

    DeploymentDTO modelToDTO(DeploymentModel deploymentModel);
    DeploymentModel dtoToModel(DeploymentDTO deploymentDTO);
}
