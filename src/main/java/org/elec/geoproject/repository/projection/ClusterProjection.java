package org.elec.geoproject.repository.projection;

public interface ClusterProjection {

  String getType();

  Long getPlaceCount();

  Double getCentroidLat();

  Double getCentroidLon();
}
