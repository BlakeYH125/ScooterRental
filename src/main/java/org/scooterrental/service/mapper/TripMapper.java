package org.scooterrental.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.entity.Trip;
import org.scooterrental.model.entity.User;
import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "scooter.scooterId", target = "scooterId")
    @Mapping(source = "startPoint.rentalPointId", target = "startPointId")
    @Mapping(source = "endPoint.rentalPointId", target = "endPointId")
    @Mapping(source = "tariff.tariffId", target = "tariffId")
    TripResponseDto toTripDto(Trip trip);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "scooter", target = "scooter")
    @Mapping(source = "tariff", target = "tariff")
    @Mapping(source = "scooter.rentalPoint", target = "startPoint")
    Trip toTripEntity(TripCreateDto tripCreateDto, User user, Scooter scooter, Tariff tariff);
}
