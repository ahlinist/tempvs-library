package club.tempvs.library.controller;

import static club.tempvs.library.domain.Source.Classification;
import static club.tempvs.library.domain.Source.Type;
import static club.tempvs.library.domain.Source.Period;

import club.tempvs.library.dao.SourceRepository;
import club.tempvs.library.domain.Source;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SourceControllerIntegrationTest {

    private static final String USER_INFO_HEADER = "User-Info";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN = "df41895b9f26094d0b1d39b7bdd9849e"; //security_token as MD5

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    public void testCreateSource() throws Exception {
        File createSourceFile = ResourceUtils.getFile("classpath:source/create.json");
        String createSourceJson = new String(Files.readAllBytes(createSourceFile.toPath()));
        Long userId = 1L;
        String userInfoValue = buildUserInfoValue(userId, Role.ROLE_CONTRIBUTOR);

        mvc.perform(post("/api/source")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createSourceJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is("src name")))
                    .andExpect(jsonPath("description", is("src description")))
                    .andExpect(jsonPath("classification", is("Armor")))
                    .andExpect(jsonPath("type", is("Written")))
                    .andExpect(jsonPath("period", is("Ancient")));
    }

    @Test
    public void testCreateSourceForInvalidInput() throws Exception {
        File createSourceFile = ResourceUtils.getFile("classpath:source/create-invalid.json");
        String createSourceJson = new String(Files.readAllBytes(createSourceFile.toPath()));
        Long userId = 1L;
        String userInfoValue = buildUserInfoValue(userId, Role.ROLE_CONTRIBUTOR);

        mvc.perform(post("/api/source")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createSourceJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.period", is("Please choose a historical period")))
                    .andExpect(jsonPath("errors.name", is("Source's name can't be empty")))
                    .andExpect(jsonPath("errors.classification", is("Please choose classification")))
                    .andExpect(jsonPath("errors.type", is("Please choose a type")));
    }

    @Test
    public void testGetSource() throws Exception {
        Long userId = 1L;
        String userInfoValue = buildUserInfoValue(userId, Role.ROLE_CONTRIBUTOR);
        String name = "name";
        String description = "desc";
        Classification classification = Classification.ARMOR;
        Type type = Type.GRAPHIC;
        Period period = Period.CONTEMPORARY;
        Source source = createSource(name, description, classification, type, period);
        Long sourceId = source.getId();

        mvc.perform(get("/api/source/" + sourceId)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is(name)))
                    .andExpect(jsonPath("description", is(description)))
                    .andExpect(jsonPath("classification", is("Armor")))
                    .andExpect(jsonPath("type", is("Graphic")))
                    .andExpect(jsonPath("period", is("Contemporary")));
    }

    private String buildUserInfoValue(Long id, Role role) throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(id);
        userInfoDto.setProfileId(id);
        userInfoDto.setLang("en");
        userInfoDto.setTimezone("UTC");

        if (role != null) {
            userInfoDto.setRoles(Arrays.asList(role.toString()));
        }

        return objectMapper.writeValueAsString(userInfoDto);
    }

    private Source createSource(String name, String desc, Classification classification, Type type, Period period) {
        Source source = new Source();
        source.setName(name);
        source.setDescription(desc);
        source.setClassification(classification);
        source.setType(type);
        source.setPeriod(period);
        return sourceRepository.save(source);
    }
}
