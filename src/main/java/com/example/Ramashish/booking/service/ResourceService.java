package com.example.Ramashish.booking.service;

import com.example.Ramashish.booking.dto.resource.ResourceRequest;
import com.example.Ramashish.booking.dto.resource.ResourceResponse;
import com.example.Ramashish.booking.exception.ResourceNotFoundException;
import com.example.Ramashish.booking.model.Resource;
import com.example.Ramashish.booking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<ResourceResponse> findAll() {
        return resourceRepository.findAll().stream().map(this::toResponse).toList();
    }
    public ResourceResponse findById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }
    public ResourceResponse create(ResourceRequest request) {
        Resource resource = Resource.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .hourlyRate(request.hourlyRate())
                .build();
        return toResponse(resourceRepository.save(resource));
    }
    public ResourceResponse update(Long id, ResourceRequest request) {
        Resource resource = getEntityOrThrow(id);
        resource.setName(request.name());
        resource.setDescription(request.description());
        resource.setCategory(request.category());
        resource.setHourlyRate(request.hourlyRate());
        return toResponse(resourceRepository.save(resource));
    }
    public void delete(Long id) {
        Resource resource = getEntityOrThrow(id);
        resourceRepository.delete(resource);
    }
    private Resource getEntityOrThrow(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }
    private ResourceResponse toResponse(Resource r) {
        return new ResourceResponse(r.getId(), r.getName(), r.getDescription(),
                r.getCategory(), r.getHourlyRate(), r.getCreatedAt(), r.getUpdatedAt());
    }
    // package-visible accessor used by ReservationService for price calculation
    public Resource getEntity(Long id) {
        return getEntityOrThrow(id);
    }
}
