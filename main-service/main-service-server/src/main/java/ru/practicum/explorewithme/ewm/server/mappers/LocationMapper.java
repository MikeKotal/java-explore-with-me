package ru.practicum.explorewithme.ewm.server.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.ewm.server.models.Location;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {

    public static LocationDto mapToLocationDto(Location location) {
        log.info("Location в маппер: {}", location);
        LocationDto locationDto = LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
        log.info("LocationDto из маппера: {}", locationDto);
        return locationDto;
    }

    public static Location mapToLocation(LocationRequest locationRequest) {
        log.info("LocationRequest в маппер: {}", locationRequest);
        Location location = Location.builder()
                .lat(locationRequest.getLat())
                .lon(locationRequest.getLon())
                .build();
        log.info("Location из маппера: {}", location);
        return location;
    }
}
