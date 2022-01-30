package de.thbingen.epro.controller;

import de.thbingen.epro.exception.NonMatchingIdsException;
import de.thbingen.epro.model.dto.CompanyObjectiveDto;
import de.thbingen.epro.service.CompanyObjectiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/companyobjectives")
public class CompanyObjectiveController {

    private final CompanyObjectiveService companyObjectiveService;

    public CompanyObjectiveController(CompanyObjectiveService companyObjectiveService) {
        this.companyObjectiveService = companyObjectiveService;
    }

    @GetMapping
    public List<CompanyObjectiveDto> findAll(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return companyObjectiveService.getAllCompanyObjectives(pageNo, pageSize, sortBy);
    }

    @PostMapping
    public ResponseEntity<CompanyObjectiveDto> addNew(@RequestBody @Valid CompanyObjectiveDto newCompanyObjective) {
        CompanyObjectiveDto companyObjectiveDto = companyObjectiveService.saveCompanyObjective(newCompanyObjective);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/v1/companyobjectives/{id}")
                .buildAndExpand(companyObjectiveDto.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(companyObjectiveDto);
    }

    @GetMapping("/{id}")
    public CompanyObjectiveDto findById(@PathVariable Long id) {
        Optional<CompanyObjectiveDto> result = companyObjectiveService.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new EntityNotFoundException("No CompanyObjective with this id exists");
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyObjectiveDto> updateById(@PathVariable Long id, @RequestBody CompanyObjectiveDto companyObjectiveDto) {
        if (companyObjectiveDto.getId() == null) {
            companyObjectiveDto.setId(id);
        }
        if (!Objects.equals(companyObjectiveDto.getId(), id)) {
            throw new NonMatchingIdsException("Ids in path and jsonObject do not match");
        }

        if (!companyObjectiveService.existsById(id)) {
            return this.addNew(companyObjectiveDto);
        }

        return ResponseEntity.ok(companyObjectiveService.saveCompanyObjective(companyObjectiveDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!companyObjectiveService.existsById(id)) {
            throw new EntityNotFoundException("No CompanyObjective with this id exists");
        }
        companyObjectiveService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}