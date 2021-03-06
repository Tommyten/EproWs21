package de.thbingen.epro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thbingen.epro.exception.RestExceptionHandler;
import de.thbingen.epro.model.assembler.BusinessUnitKeyResultAssembler;
import de.thbingen.epro.model.dto.BusinessUnitKeyResultDto;
import de.thbingen.epro.model.dto.BusinessUnitKeyResultUpdateDto;
import de.thbingen.epro.model.entity.BusinessUnitKeyResult;
import de.thbingen.epro.model.mapper.BusinessUnitKeyResultMapper;
import de.thbingen.epro.service.BusinessUnitKeyResultHistoryService;
import de.thbingen.epro.service.BusinessUnitKeyResultService;
import de.thbingen.epro.service.CompanyKeyResultService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BusinessUnitKeyResultController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                BusinessUnitKeyResultController.class,
                                BusinessUnitKeyResultMapper.class,
                                BusinessUnitKeyResultAssembler.class,
                        }
                )}
)
@Import(RestExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class BusinessUnitKeyResultControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BusinessUnitKeyResultService businessUnitKeyResultService;
    @MockBean
    private BusinessUnitKeyResultHistoryService businessUnitKeyResultHistoryService;
    @MockBean
    private CompanyKeyResultService companyKeyResultService;

    @Autowired
    private AnnotationLinkRelationProvider annotationLinkRelationProvider;

    @Autowired
    private BusinessUnitKeyResultAssembler businessUnitKeyResultAssembler;


    // region GET ALL

    @Test
    @DisplayName("Get All should return all Business Unit Objectives with 200 - OK")
    public void getAllShouldReturnAllBusinessUnitObjectives() throws Exception {
        Set<BusinessUnitKeyResultDto> businessUnitObjectiveDtos = Stream.of(
                new BusinessUnitKeyResult(1L, "BKR1", 10, 100, 50, "a comment", OffsetDateTime.now()),
                new BusinessUnitKeyResult(2L, "BKR2", 10, 100, 50, "no comment", OffsetDateTime.now())
        ).map(businessUnitKeyResultAssembler::toModel).collect(Collectors.toSet());


        when(businessUnitKeyResultService.findAllBusinessUnitKeyResults(Pageable.ofSize(10))).thenReturn(new PageImpl<>(new ArrayList<>(businessUnitObjectiveDtos)));

        mockMvc.perform(get("/businessUnitKeyResults").accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.businessUnitKeyResults").exists())
                .andExpect(jsonPath("$._embedded.businessUnitKeyResults", hasSize(2)))
                .andExpect(jsonPath("$._embedded.businessUnitKeyResults[*]._links").exists())
                .andExpect(jsonPath("$._embedded.businessUnitKeyResults.._links.self.href", everyItem(matchesRegex("/businessUnitKeyResults/\\d"))))
                .andExpect(jsonPath("$._embedded.businessUnitKeyResults..name", everyItem(matchesRegex("BKR\\d"))))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith("/businessUnitKeyResults")))
                .andReturn();
        //TODO: fix Problem
    }

    // endregion
    // region GET with id

    @Test
    @DisplayName("Get With ID should Return a single Business Unit Key result with 200 - OK")
    public void getWithIdShouldReturnSingleBusinessUnitkeyResult() throws Exception {

        when(businessUnitKeyResultService.findById(1L)).thenReturn(Optional.of(businessUnitKeyResultAssembler.toModel(new BusinessUnitKeyResult(1L, "BKR1", 10, 100, 50, "a comment", OffsetDateTime.now()))));

        mockMvc.perform(get("/businessUnitKeyResults/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("BKR1"))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith("/businessUnitKeyResults/1")));
    }

    // endregion

    // region PUT

    @Test
    @DisplayName("Valid put should return 200 - OK when Object is being updated")
    public void validPutShouldReturnOkWhenObjectIsBeingUpdated() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        BusinessUnitKeyResult businessUnitKeyResult = new BusinessUnitKeyResult(1L, "changedName", 10f, 100, 50, "a comment", OffsetDateTime.now());
        BusinessUnitKeyResultUpdateDto toPut = new BusinessUnitKeyResultUpdateDto(10f, null, "a comment");
        String jsonToPut = objectMapper.writeValueAsString(toPut);

        when(businessUnitKeyResultService.existsById(1L)).thenReturn(true);
        when(businessUnitKeyResultService.updateBusinessUnitKeyResult(anyLong(), any(BusinessUnitKeyResultUpdateDto.class)))
                .thenReturn(businessUnitKeyResultAssembler.toModel(businessUnitKeyResult));

        mockMvc.perform(put("/businessUnitKeyResults/1").contentType(MediaType.APPLICATION_JSON).content(jsonToPut))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("a comment"))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith("/businessUnitKeyResults/1")));
    }

    // endregion

    // region DELETE

    @Test
    @DisplayName("Delete with valid ID should return 204 - No Content")
    public void deleteWithValidIdShouldReturnNoContent() throws Exception {
        when(businessUnitKeyResultService.existsById(1L)).thenReturn(true);
        doNothing().when(businessUnitKeyResultService).deleteById(1L);

        mockMvc.perform(delete("/businessUnitKeyResults/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete with Invalid ID should return 404 - Not found")
    public void deleteWithInvalidIdShouldReturnNotFound() throws Exception {
        when(businessUnitKeyResultService.existsById(100L)).thenReturn(false);
        doNothing().when(businessUnitKeyResultService).deleteById(100L);

        mockMvc.perform(delete("/businessUnitKeyResults/100"))
                .andExpect(status().isNotFound());
    }

    // endregion


}
