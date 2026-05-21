package org.elec.geoproject.repository.projection;

public interface NearbyProjection {

  Long getId();

  String getName();

  String getType();

  String getAddress();

  Double getLat();

  Double getLon();

  Double getDistanceMeters();
}
