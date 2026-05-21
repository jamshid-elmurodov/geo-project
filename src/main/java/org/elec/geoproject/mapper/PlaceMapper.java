package org.elec.geoproject.mapper;

import org.elec.geoproject.dto.PlaceCreateRequest;
import org.elec.geoproject.dto.PlaceResponse;
import org.elec.geoproject.entity.Place;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {

  private static final GeometryFactory GEOMETRY_FACTORY =
      new GeometryFactory(new PrecisionModel(), 4326);

  public PlaceResponse toResponse(Place place) {
    return PlaceResponse.builder()
        .id(place.getId())
        .name(place.getName())
        .type(place.getType())
        .description(place.getDescription())
        .lat(place.getLocation() != null ? place.getLocation().getY() : null)
        .lon(place.getLocation() != null ? place.getLocation().getX() : null)
        .osmId(place.getOsmId())
        .address(place.getAddress())
        .addressDetails(place.getAddressDetails())
        .createdAt(place.getCreatedAt())
        .updatedAt(place.getUpdatedAt())
        .build();
  }

  public Place toEntity(PlaceCreateRequest req) {
    return Place.builder()
        .name(req.getName())
        .type(req.getType())
        .description(req.getDescription())
        .location(GEOMETRY_FACTORY.createPoint(new Coordinate(req.getLon(), req.getLat())))
        .osmId(req.getOsmId())
        .address(req.getAddress())
        .build();
  }

  public static org.locationtech.jts.geom.Point buildPoint(double lon, double lat) {
    return GEOMETRY_FACTORY.createPoint(new Coordinate(lon, lat));
  }
}
