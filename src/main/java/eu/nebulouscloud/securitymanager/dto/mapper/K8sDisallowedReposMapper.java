package eu.nebulouscloud.securitymanager.dto.mapper;

import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.repository.K8sDisallowedRepos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface K8sDisallowedReposMapper {

    K8sDisallowedReposMapper INSTANCE = Mappers.getMapper(K8sDisallowedReposMapper.class);

    @Mapping(target = "metadata.name", source = "name")
    @Mapping(target = "spec.match.kinds", source = "kinds")
    @Mapping(target = "spec.match.namespaces", source = "namespaces")
    @Mapping(target = "spec.parameters.repos", source = "repos")
    K8sDisallowedRepos dtoToModel(ConstraintDTO dto);

    @Mapping(target = "name", source = "metadata.name")
    @Mapping(target = "kinds", source = "spec.match.kinds")
    @Mapping(target = "namespaces", source = "spec.match.namespaces")
    @Mapping(target = "repos", source = "spec.parameters.repos")
    ConstraintDTO modelToDto(K8sDisallowedRepos model);
}

