package de.thbingen.epro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thbingen.epro.exception.RestExceptionHandler;
import de.thbingen.epro.model.assembler.BusinessUnitObjectiveAssembler;
import de.thbingen.epro.model.dto.BusinessUnitObjectiveDto;
import de.thbingen.epro.model.entity.BusinessUnit;
import de.thbingen.epro.model.entity.BusinessUnitObjective;
import de.thbingen.epro.model.mapper.BusinessUnitObjectiveMapper;
import de.thbingen.epro.service.BusinessUnitKeyResultService;
import de.thbingen.epro.service.BusinessUnitObjectiveService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BusinessUnitObjectiveController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                BusinessUnitObjectiveController.class,
                                BusinessUnitObjectiveMapper.class,
                                BusinessUnitObjectiveAssembler.class,
                        }
                )}
)
@Import(RestExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class BusinessUnitObjectiveControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BusinessUnitObjectiveService businessUnitObjectiveService;
    @MockBean
    private BusinessUnitKeyResultService businessUnitKeyResultService;
    @MockBean
    private CompanyKeyResultService companyKeyResultService;

    @Autowired
    private BusinessUnitObjectiveAssembler assembler;

    // region GET ALL

    @Test
    @DisplayName("Get All should return all Business Unit Objectives with 200 - OK")
    public void getAllShouldReturnAllBusinessUnitObjectives() throws Exception {
        BusinessUnit businessUnit = new BusinessUnit(1L, "Personal");

        List<BusinessUnitObjectiveDto> businessUnitObjectiveDtos = Stream.of(
                new BusinessUnitObjective(1L, 0f, "Test1", LocalDate.now(), LocalDate.now()),
                new BusinessUnitObjective(2L, 0f, "Test2", LocalDate.now(), LocalDate.now())
        ).map(businessUnitObjective -> {
            businessUnitObjective.setBusinessUnit(businessUnit);
            return assembler.toModel(businessUnitObjective);
        }).distinct().collect(Collectors.toList());

        when(businessUnitObjectiveService.getAllBusinessUnitObjectives(
                Pageable.ofSize(10),
                LocalDate.now().with(firstDayOfYear()),
                LocalDate.now().with(lastDayOfYear())
        )).thenReturn(new PageImpl<>(businessUnitObjectiveDtos)
        );

        mockMvc.perform(get("/businessUnitObjectives").accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.businessUnitObjectives").exists())
                .andExpect(jsonPath("$._embedded.businessUnitObjectives", hasSize(2)))
                .andExpect(jsonPath("$._embedded.businessUnitObjectives[*]._links").exists())
                .andExpect(jsonPath("$._embedded.businessUnitObjectives[0]._links.self.href", endsWith("/businessUnitObjectives/1")))
                .andExpect(jsonPath("$._embedded.businessUnitObjectives[0].name", is("Test1")))
                .andExpect(jsonPath("$._embedded.businessUnitObjectives[0].achievement", is(0.0)))
                .andExpect(jsonPath("$._embedded.businessUnitObjectives[1]._links.self.href", endsWith("/businessUnitObjectives/2")))
                .andExpect(jsonPath("$._embedded.businessUnitObjectives[1].name", is("Test2")))
                .andExpect(jsonPath("$._embedded.businessUnitObjectives[1].achievement", is(0.0)))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith("/businessUnitObjectives")))
                .andReturn();
    }

    // endregion

    // region GET with id

    @Test
    @DisplayName("Get With ID should Return a single Company Objective with 200 - OK")
    public void getWithIdShouldReturnSingleCompanyObjective() throws Exception {
        BusinessUnit businessUnit = new BusinessUnit(1L, "Personal");
        BusinessUnitObjective businessUnitObjective = new BusinessUnitObjective(1L, 0f, "Test1", LocalDate.now(), LocalDate.now());
        businessUnitObjective.setBusinessUnit(businessUnit);
        when(businessUnitObjectiveService.findById(1L)).thenReturn(Optional.of(assembler.toModel(businessUnitObjective)));

        mockMvc.perform(get("/businessUnitObjectives/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test1"))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith("/businessUnitObjectives/1")));
    }

    // endregion

    // region PUT

    @Test
    @DisplayName("Valid put should return 200 - OK when Object is being updated")
    public void validPutShouldReturnOkWhenObjectIsBeingUpdated() throws Exception {
        BusinessUnitObjective businessUnitObjective = new BusinessUnitObjective(1L, 0f, "changedName", LocalDate.now(), LocalDate.now().plusDays(1));
        BusinessUnit businessUnit = new BusinessUnit(1L, "Personal");
        businessUnitObjective.setBusinessUnit(businessUnit);
        BusinessUnitObjectiveDto businessUnitObjectiveDto = assembler.toModel(businessUnitObjective);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonToPut = objectMapper.writeValueAsString(businessUnitObjectiveDto);

        when(businessUnitObjectiveService.existsById(1L)).thenReturn(true);
        when(businessUnitObjectiveService.updateBusinessUnitObjective(anyLong(), any(BusinessUnitObjectiveDto.class))).thenReturn(businessUnitObjectiveDto);

        mockMvc.perform(
                        put("/businessUnitObjectives/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonToPut)
                                .characterEncoding(Charset.defaultCharset())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("changedName"))
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith("/businessUnitObjectives/1")));
    }

    // endregion

    // region DELETE

    @Test
    @DisplayName("Delete with valid ID should return 204 - No Content")
    public void deleteWithValidIdShouldReturnNoContent() throws Exception {
        when(businessUnitObjectiveService.existsById(1L)).thenReturn(true);
        doNothing().when(businessUnitObjectiveService).deleteById(1L);

        mockMvc.perform(delete("/businessUnitObjectives/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete with Invalid ID should return 404 - Not found")
    public void deleteWithInvalidIdShouldReturnNotFound() throws Exception {
        when(businessUnitObjectiveService.existsById(100L)).thenReturn(false);
        doNothing().when(businessUnitObjectiveService).deleteById(100L);

        mockMvc.perform(delete("/businessUnitObjectives/100"))
                .andExpect(status().isNotFound());
    }

    // endregion
}
