package de.thbingen.epro.controller;

import de.thbingen.epro.model.dto.OkrUserDto;
import de.thbingen.epro.service.OkrUserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class OkrUserController {

    private final OkrUserService okrUserService;
    private final PagedResourcesAssembler<OkrUserDto> pagedResourcesAssembler;

    public OkrUserController(OkrUserService okrUserService, PagedResourcesAssembler<OkrUserDto> pagedResourcesAssembler) {
        this.okrUserService = okrUserService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<OkrUserDto>> findAll(@PageableDefault Pageable pageable) {
        return pagedResourcesAssembler.toModel(okrUserService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public OkrUserDto findById(@PathVariable Long id) {
        Optional<OkrUserDto> result = okrUserService.findById(id);
        if (result.isPresent())
            return result.get();
        throw new EntityNotFoundException("No User with this id exists");
    }

    @PostMapping
    public ResponseEntity<OkrUserDto> addNew(@RequestBody @Valid OkrUserDto newUser) {
        OkrUserDto okrUserDto = okrUserService.insertOkrUser(newUser);
        return ResponseEntity.created(okrUserDto.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(okrUserDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OkrUserDto> updateById(@PathVariable Long id, @RequestBody @Valid OkrUserDto okrUserDto) {
        if (!okrUserService.existsById(id))
            throw new EntityNotFoundException("No OkrUser with this id exists");

        return ResponseEntity.ok(okrUserService.updateOkrUser(id, okrUserDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!okrUserService.existsById(id)) {
            throw new EntityNotFoundException("No OkrUser with this id exists");
        }
        okrUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
