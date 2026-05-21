package org.elec.geoproject.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.elec.geoproject.dto.PlaceCreateRequest;
import org.elec.geoproject.dto.PlaceResponse;
import org.elec.geoproject.dto.PlaceUpdateRequest;
import org.elec.geoproject.entity.Place;
import org.elec.geoproject.exception.ResourceNotFoundException;
import org.elec.geoproject.mapper.PlaceMapper;
import org.elec.geoproject.repository.PlaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final PlaceMapper placeMapper;

  public Page<PlaceResponse> findAll(Pageable pageable) {
    return placeRepository.findAllByDeletedAtIsNull(pageable)
        .map(placeMapper::toResponse);
  }

  public PlaceResponse findById(Long id) {
    return placeRepository.findByIdAndDeletedAtIsNull(id)
        .map(placeMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Place", id));
  }

  @Transactional
  public PlaceResponse create(PlaceCreateRequest request) {
    Place place = placeMapper.toEntity(request);
    return placeMapper.toResponse(placeRepository.save(place));
  }

  @Transactional
  public PlaceResponse update(Long id, PlaceUpdateRequest request) {
    Place place = placeRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Place", id));

    if (request.getName() != null) {
      place.setName(request.getName());
    }
    if (request.getType() != null) {
      place.setType(request.getType());
    }
    if (request.getDescription() != null) {
      place.setDescription(request.getDescription());
    }
    if (request.getAddress() != null) {
      place.setAddress(request.getAddress());
    }

    if (request.getLat() != null && request.getLon() != null) {
      place.setLocation(PlaceMapper.buildPoint(request.getLon(), request.getLat()));
    }

    return placeMapper.toResponse(placeRepository.save(place));
  }

  @Transactional
  public void delete(Long id) {
    Place place = placeRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Place", id));
    place.setDeletedAt(LocalDateTime.now());
    placeRepository.save(place);
  }
}
