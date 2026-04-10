package org.scooterrental.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointDetailsDto;
import org.scooterrental.service.dto.RentalPointResponseDto;

@Mapper(componentModel = "spring")
public interface RentalPointMapper {

    @Mapping(source = "parentPoint.rentalPointId", target = "parentPointId")
    RentalPointResponseDto toRentalPointDto(RentalPoint rentalPoint);

    @Mapping(target = "rentalPointId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "rentalPointCreateDto.location", target = "location")
    @Mapping(source = "parentPoint", target = "parentPoint")
    RentalPoint toRentalPointEntity(RentalPointCreateDto rentalPointCreateDto, RentalPoint parentPoint);
}
