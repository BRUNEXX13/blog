package br.com.ss.blog.domain.mapper;

import br.com.ss.blog.domain.dto.UserDTO;
import br.com.ss.blog.domain.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Converts a UserEntity to a UserDTO.
     */

    UserDTO toDto(UserEntity entity);

    /**
     * Converts a UserDTO to a UserEntity.
     * Note: id and createdAt are managed by the entity's constructor/persistence logic,
     * so they are not mapped from the DTO.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true, defaultValue = "USER")
    UserEntity toEntity(UserDTO dto);
}
