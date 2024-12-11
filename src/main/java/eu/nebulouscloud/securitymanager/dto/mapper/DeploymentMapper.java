package eu.nebulouscloud.securitymanager.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import eu.nebulouscloud.securitymanager.dto.DeploymentDTO;
import eu.nebulouscloud.securitymanager.model.DeploymentModel;

@Mapper
public interface DeploymentMapper {

    DeploymentMapper INSTANCE = Mappers.getMapper(DeploymentMapper.class);

    DeploymentDTO modelToDTO(DeploymentModel deploymentModel);
    DeploymentModel dtoToModel(DeploymentDTO deploymentDTO);
}
