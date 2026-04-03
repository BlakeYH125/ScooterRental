package org.scooterrental.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointResponseDto;

@Mapper(componentModel = "spring")
public interface RentalPointMapper {

    @Mapping(source = "parentPoint.parentPointId", target = "parentPointId")
    RentalPointResponseDto toRentalPointDto(RentalPoint rentalPoint);

    @Mapping(source = "parentPoint", target = "parent")
    RentalPoint toRentalPointEntity(RentalPointCreateDto rentalPointCreateDto);
}
