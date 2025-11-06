package com.helmes.task.controller;

import com.helmes.task.entity.Form;
import com.helmes.task.entity.Sector;
import com.helmes.task.repository.FormRepository;
import com.helmes.task.repository.SectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private FormRepository formRepository;

    @BeforeEach
    void setUp() {
        formRepository.deleteAll();
        sectorRepository.deleteAll();

        if (sectorRepository.count() == 0) {
            Sector s = new Sector();
            s.setId(999L);
            s.setName("Test sector");
            s.setParentId(null);
            sectorRepository.save(s);
        }
    }

    @Test
    void getRootShowsFormAndSectors() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("newIndex"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("sectors"));
    }

    @Test
    void postWithInvalidDataShowsValidationErrorsOnSamePage() throws Exception {
        mockMvc.perform(post("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("newIndex"))
                .andExpect(model().attributeHasFieldErrors(
                        "form", "name", "sectors", "agree"));
    }

    @Test
    void postValidDataCreatesFormAndRedirects() throws Exception {
        Sector anySector = sectorRepository.findAll().stream()
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/")
                        .param("name", "John Doe")
                        .param("sectors", anySector.getId().toString())
                        .param("agree", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        List<Form> forms = formRepository.findAll();
        assertThat(forms).hasSize(1);

        Form form = forms.get(0);
        assertThat(form.getName()).isEqualTo("John Doe");
        assertThat(form.isAgree()).isTrue();
        assertThat(form.getSectors())
                .extracting(Sector::getId)
                .containsExactly(anySector.getId());
    }

    @Test
    void postTwiceInSameSessionUpdatesExistingFormNotCreateNewOne() throws Exception {
        Sector anySector = sectorRepository.findAll().stream()
                .findFirst()
                .orElseThrow();

        MvcResult result1 = mockMvc.perform(post("/")
                        .param("name", "Initial Name")
                        .param("sectors", anySector.getId().toString())
                        .param("agree", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result1.getRequest().getSession(false);
        assertThat(session).isNotNull();

        List<Form> afterFirst = formRepository.findAll();
        assertThat(afterFirst).hasSize(1);
        Long formId = afterFirst.get(0).getId();

        mockMvc.perform(post("/")
                        .session(session)
                        .param("name", "Updated Name")
                        .param("sectors", anySector.getId().toString())
                        .param("agree", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        List<Form> afterSecond = formRepository.findAll();
        assertThat(afterSecond).hasSize(1);
        assertThat(afterSecond.get(0).getId()).isEqualTo(formId);
        assertThat(afterSecond.get(0).getName()).isEqualTo("Updated Name");
    }
}
