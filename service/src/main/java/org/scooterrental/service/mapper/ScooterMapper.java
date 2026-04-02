package org.scooterrental.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;

@Mapper(componentModel = "spring")
public interface ScooterMapper {
    @Mapping(source = "rentalPoint.rentalPointId", target = "rentalPointId")
    ScooterResponseDto toScooterDto(Scooter scooter);

    Scooter toScooterEntity(ScooterCreateDto scooterCreateDto);
}

