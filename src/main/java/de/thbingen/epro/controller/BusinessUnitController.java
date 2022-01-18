package de.thbingen.epro.controller;

import de.thbingen.epro.model.dto.BusinessUnitDto;
import de.thbingen.epro.service.BusinessUnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/businessUnits")
public class BusinessUnitController {

    final BusinessUnitService businessUnitService;

    public BusinessUnitController(BusinessUnitService businessUnitService) {
        this.businessUnitService = businessUnitService;
    }

    @GetMapping
    public List<BusinessUnitDto> findAll() {
        return businessUnitService.findAll();
    }

    @GetMapping("/{id}")
    public BusinessUnitDto findById(@PathVariable Long id) {
        Optional<BusinessUnitDto> result = businessUnitService.findById(id);
        if(result.isPresent())
            return result.get();
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No BusinessUnit with this id exists"
        );
    }

    @PostMapping
    public ResponseEntity<BusinessUnitDto> addNew(@RequestBody @Valid BusinessUnitDto newBusinessUnit) {
        BusinessUnitDto businessUnitDto = businessUnitService.saveBusinessUnit(newBusinessUnit);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/v1/businessUnits/{id}")
                .buildAndExpand(businessUnitDto.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(businessUnitDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessUnitDto> updateById(@PathVariable Long id, @RequestBody BusinessUnitDto businessUnitDto) {
        if (businessUnitDto.getId() == null) {
            businessUnitDto.setId(id);
        }
        if (!Objects.equals(businessUnitDto.getId(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id in path and id of businessUnit do not match"
            );
        }

        if(!businessUnitService.existsById(id)) {
            return this.addNew(businessUnitDto);
        }

        return ResponseEntity.ok(businessUnitService.saveBusinessUnit(businessUnitDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if(!businessUnitService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        businessUnitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
