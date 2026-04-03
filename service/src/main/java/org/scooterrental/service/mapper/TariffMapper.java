package org.scooterrental.service.mapper;

import org.mapstruct.Mapper;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.service.dto.TariffCreateDto;
import org.scooterrental.service.dto.TariffResponseDto;

@Mapper(componentModel = "spring")
public interface TariffMapper {
    TariffResponseDto toTariffDto(Tariff tariff);
    Tariff toTariffEntity(TariffCreateDto tariffCreateDto);
}
