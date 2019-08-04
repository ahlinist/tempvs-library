package club.tempvs.library.controller;

import static club.tempvs.library.domain.Source.*;

import club.tempvs.library.dao.SourceRepository;
import club.tempvs.library.domain.Source;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_CONTRIBUTOR);

        mvc.perform(post("/api/source")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createSourceJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is("src name")))
                    .andExpect(jsonPath("description", is("src description")))
                    .andExpect(jsonPath("classification", is("ARMOR")))
                    .andExpect(jsonPath("type", is("WRITTEN")))
                    .andExpect(jsonPath("period", is("ANCIENT")));
    }

    @Test
    public void testCreateSourceForInvalidInput() throws Exception {
        File createSourceFile = ResourceUtils.getFile("classpath:source/create-invalid.json");
        String createSourceJson = new String(Files.readAllBytes(createSourceFile.toPath()));
        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_CONTRIBUTOR);

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
        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_CONTRIBUTOR);
        String name = "name";
        String description = "desc";
        Classification classification = Classification.ARMOR;
        Type type = Type.GRAPHIC;
        Period period = Period.CONTEMPORARY;
        Source source = createSource(name, description, classification, type, period);
        Long sourceId = source.getId();

        mvc.perform(get("/api/source/" + sourceId)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name", is(name)))
                    .andExpect(jsonPath("description", is(description)))
                    .andExpect(jsonPath("classification", is("ARMOR")))
                    .andExpect(jsonPath("type", is("GRAPHIC")))
                    .andExpect(jsonPath("period", is("CONTEMPORARY")));
    }

    @Test
    public void testGetSources() throws Exception {
        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_USER);
        Source source1 = createSource(
                "name11", "1desc", Classification.ARMOR, Type.GRAPHIC, Period.EARLY_MIDDLE_AGES);
        Source source2 = createSource(
                "name15", "5desc", Classification.HOUSEHOLD, Type.WRITTEN, Period.EARLY_MIDDLE_AGES);

        List<String> ids = Arrays.asList(source1.getId().toString(), source2.getId().toString());
        String stringifiedIds = String.join(", ", ids);
        String query = String.format("{\"ids\": [%s]}", stringifiedIds);
        String encodedQuery = Base64.getEncoder().encodeToString(query.getBytes());

        mvc.perform(get("/api/source?q=" + encodedQuery)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("name11")))
                .andExpect(jsonPath("$[0].description", is("1desc")))
                .andExpect(jsonPath("$[0].classification", is("ARMOR")))
                .andExpect(jsonPath("$[0].type", is("GRAPHIC")))
                .andExpect(jsonPath("$[0].period", is("EARLY_MIDDLE_AGES")))
                .andExpect(jsonPath("$[1].name", is("name15")))
                .andExpect(jsonPath("$[1].description", is("5desc")))
                .andExpect(jsonPath("$[1].classification", is("HOUSEHOLD")))
                .andExpect(jsonPath("$[1].type", is("WRITTEN")))
                .andExpect(jsonPath("$[1].period", is("EARLY_MIDDLE_AGES")));
    }

    @Test
    public void testFindSources() throws Exception {
        File findSourceFile = ResourceUtils.getFile("classpath:source/find.json");
        String findSourceJson = new String(Files.readAllBytes(findSourceFile.toPath()));
        String encodedQuery = Base64.getEncoder().encodeToString(findSourceJson.getBytes());

        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_USER);
        createSource("name41", "desc", Classification.WEAPON, Type.ARCHAEOLOGICAL, Period.EARLY_MIDDLE_AGES);
        createSource("name11", "1desc", Classification.ARMOR, Type.GRAPHIC, Period.EARLY_MIDDLE_AGES);
        createSource("name12", "desc2", Classification.CLOTHING, Type.WRITTEN, Period.CONTEMPORARY);
        createSource("name13", "3desc", Classification.FOOTWEAR, Type.GRAPHIC, Period.EARLY_MIDDLE_AGES);
        createSource("name32", "desc4", Classification.HOUSEHOLD, Type.GRAPHIC, Period.WWII);
        createSource("name15", "5desc", Classification.HOUSEHOLD, Type.WRITTEN, Period.EARLY_MIDDLE_AGES);

        mvc.perform(get("/api/source/find?&page=0&size=40&q=" + encodedQuery)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].name", is("name11")))
                    .andExpect(jsonPath("$[0].description", is("1desc")))
                    .andExpect(jsonPath("$[0].classification", is("ARMOR")))
                    .andExpect(jsonPath("$[0].type", is("GRAPHIC")))
                    .andExpect(jsonPath("$[0].period", is("EARLY_MIDDLE_AGES")))
                    .andExpect(jsonPath("$[1].name", is("name13")))
                    .andExpect(jsonPath("$[1].description", is("3desc")))
                    .andExpect(jsonPath("$[1].classification", is("FOOTWEAR")))
                    .andExpect(jsonPath("$[1].type", is("GRAPHIC")))
                    .andExpect(jsonPath("$[1].period", is("EARLY_MIDDLE_AGES")))
                    .andExpect(jsonPath("$[2].name", is("name15")))
                    .andExpect(jsonPath("$[2].description", is("5desc")))
                    .andExpect(jsonPath("$[2].classification", is("HOUSEHOLD")))
                    .andExpect(jsonPath("$[2].type", is("WRITTEN")))
                    .andExpect(jsonPath("$[2].period", is("EARLY_MIDDLE_AGES")));
    }

    @Test
    public void testUpdateName() throws Exception {
        File updateSourceNameFile = ResourceUtils.getFile("classpath:source/update-name.json");
        String updateSourceNameJson = new String(Files.readAllBytes(updateSourceNameFile.toPath()));

        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_SCRIBE);
        Source source = createSource("old name", "desc", Classification.WEAPON, Type.ARCHAEOLOGICAL, Period.EARLY_MIDDLE_AGES);

        mvc.perform(patch("/api/source/" + source.getId() + "/name")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(updateSourceNameJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());
    }

    @Test
    public void testUpdateDescription() throws Exception {
        File updateSourceDescFile = ResourceUtils.getFile("classpath:source/update-description.json");
        String updateSourceDescJson = new String(Files.readAllBytes(updateSourceDescFile.toPath()));

        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_SCRIBE);
        Source source = createSource("name", "old desc", Classification.WEAPON, Type.GRAPHIC, Period.WWI);

        mvc.perform(patch("/api/source/" + source.getId() + "/description")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(updateSourceDescJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_ARCHIVARIUS);
        Source source = createSource("name", "desc", Classification.OTHER, Type.OTHER, Period.OTHER);

        mvc.perform(delete("/api/source/" + source.getId())
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());
    }

    @Test
    public void testDeleteForInsufficientRights() throws Exception {
        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_SCRIBE);
        Source source = createSource("name", "desc", Classification.OTHER, Type.OTHER, Period.OTHER);

        mvc.perform(delete("/api/source/" + source.getId())
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isForbidden());
    }

    @Test
    public void testAddImage() throws Exception {
        File uploadImageFile = ResourceUtils.getFile("classpath:source/upload-image.json");
        String uploadImageFileJson = new String(Files.readAllBytes(uploadImageFile.toPath()));

        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_CONTRIBUTOR);
        Source source = createSource("src name", "desc", Classification.OTHER, Type.OTHER, Period.OTHER);

        mvc.perform(post("/api/source/" + source.getId() + "/images")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(uploadImageFileJson)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());
    }

    @Test
    public void testDeleteImage() throws Exception {
        String objectId1 = "objectId1";
        String objectId2 = "objectId2";
        String fileName = "image.jpg";
        String userInfoValue = buildUserInfoValue(1L, Role.ROLE_SCRIBE);
        Source source = createSource("src name", "desc", Classification.OTHER, Type.OTHER, Period.OTHER);

        mvc.perform(delete("/api/source/" + source.getId() + "/images/" + objectId1)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());
    }

    private String buildUserInfoValue(Long id, Role role) throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(id);
        userInfoDto.setLang("en");
        userInfoDto.setRoles(Arrays.asList(role.toString()));
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

    @TestConfiguration
    public static class LocalRibbonClientConfiguration {

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new StaticServerList<>(new Server("localhost", 8910));
        }
    }
}
