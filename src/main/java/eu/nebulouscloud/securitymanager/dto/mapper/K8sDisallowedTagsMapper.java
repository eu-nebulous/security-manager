package eu.nebulouscloud.securitymanager.dto.mapper;

import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.K8sDisallowedTags;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface K8sDisallowedTagsMapper {

    K8sDisallowedTagsMapper INSTANCE = Mappers.getMapper(K8sDisallowedTagsMapper.class);

    @Mapping(target = "metadata.name", source = "name")
    @Mapping(target = "spec.match.kinds", source = "kinds")
    @Mapping(target = "spec.match.namespaces", source = "namespaces")
    @Mapping(target = "spec.parameters.tags", source = "tags")
    @Mapping(target = "spec.parameters.exemptImages", source = "exemptImages")
    K8sDisallowedTags dtoToModel(ConstraintDTO dto);

    @Mapping(target = "name", source = "metadata.name")
    @Mapping(target = "kinds", source = "spec.match.kinds")
    @Mapping(target = "namespaces", source = "spec.match.namespaces")
    @Mapping(target = "tags", source = "spec.parameters.tags")
    @Mapping(target = "exemptImages", source = "spec.parameters.exemptImages")
    ConstraintDTO modelToDto(K8sDisallowedTags model);
}