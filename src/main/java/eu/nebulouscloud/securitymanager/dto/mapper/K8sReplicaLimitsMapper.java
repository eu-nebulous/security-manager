package eu.nebulouscloud.securitymanager.dto.mapper;

import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.allowed.K8sReplicaLimits;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface K8sReplicaLimitsMapper {

    K8sReplicaLimitsMapper INSTANCE = Mappers.getMapper(K8sReplicaLimitsMapper.class);

    @Mapping(target = "metadata.name", source = "name")
    @Mapping(target = "spec.match.kinds", source = "kinds")
    @Mapping(target = "spec.parameters.ranges", source = "ranges")
    K8sReplicaLimits dtoToModel(ConstraintDTO dto);

    @Mapping(target = "name", source = "metadata.name")
    @Mapping(target = "kinds", source = "spec.match.kinds")
    @Mapping(target = "ranges", source = "spec.parameters.ranges")
    ConstraintDTO modelToDto(K8sReplicaLimits model);
}