package org.elec.geoproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elec.geoproject.dto.PlaceCreateRequest;
import org.elec.geoproject.dto.PlaceResponse;
import org.elec.geoproject.dto.PlaceUpdateRequest;
import org.elec.geoproject.service.PlaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
@Tag(name = "Places", description = "CRUD operations for geo places")
public class PlaceController {

  private final PlaceService placeService;

  @GetMapping
  @Operation(summary = "List all places (paginated)")
  public Page<PlaceResponse> findAll(
      @RequestParam("page") int page,
      @RequestParam("size") int size
  ) {
    return placeService.findAll(Pageable.ofSize(size).withPage(page - 1));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a place by ID")
  public PlaceResponse findById(@PathVariable Long id) {
    return placeService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new place")
  public PlaceResponse create(@Valid @RequestBody PlaceCreateRequest request) {
    return placeService.create(request);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Partially update a place")
  public PlaceResponse update(@PathVariable Long id,
      @Valid @RequestBody PlaceUpdateRequest request) {
    return placeService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Soft-delete a place")
  public void delete(@PathVariable Long id) {
    placeService.delete(id);
  }
}
