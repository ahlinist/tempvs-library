package club.tempvs.library.controller;

import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LibraryControllerIntegrationTest {

    private static final String USER_INFO_HEADER = "User-Info";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN = "df41895b9f26094d0b1d39b7bdd9849e"; //security_token as MD5

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @BeforeClass
    public static void setupSpec() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testGetPong() throws Exception {
        mvc.perform(get("/api/ping").accept(TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("pong!")));
    }

    @Test
    public void testGetWelcomePage() throws Exception {
        Long userId = 1L;
        String userInfoValue = buildUserInfoValue(userId);

        mvc.perform(get("/api/library")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(jsonPath("greeting", is(containsString("Greetings in the Library!"))))
                    .andExpect(jsonPath("buttonText", is("Become a Contributor")))
                    .andExpect(jsonPath("adminPanelAvailable", is(false)))
                    .andExpect(jsonPath("role", is(Role.ROLE_CONTRIBUTOR.toString())))
                    .andExpect(jsonPath("roleRequestAvailable", is(true)));
    }

    @Test
    public void testRequestRole() throws Exception {
        Long userId = 1L;
        String userInfoValue = buildUserInfoValue(userId);
        String role = Role.ROLE_CONTRIBUTOR.toString();

        mvc.perform(post("/api/library/role/" + role)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(jsonPath("greeting", is(containsString("Greetings in the Library!"))))
                    .andExpect(jsonPath("buttonText", is("Cancel Contributor request")))
                    .andExpect(jsonPath("adminPanelAvailable", is(false)))
                    .andExpect(jsonPath("role", is(Role.ROLE_CONTRIBUTOR.toString())))
                    .andExpect(jsonPath("roleRequestAvailable", is(false)));
    }

    @Test
    public void testCancelRoleRequest() throws Exception {
        Long userId = 1L;
        String userInfoValue = buildUserInfoValue(userId, Role.ROLE_CONTRIBUTOR);
        String role = Role.ROLE_SCRIBE.toString();

        mvc.perform(delete("/api/library/role/" + role)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(jsonPath("greeting", is(containsString("Greetings in the Library, Contributor!"))))
                .andExpect(jsonPath("buttonText", is("Become a Scribe")))
                .andExpect(jsonPath("adminPanelAvailable", is(false)))
                .andExpect(jsonPath("role", is(Role.ROLE_SCRIBE.toString())))
                .andExpect(jsonPath("roleRequestAvailable", is(true)));
    }

    private String buildUserInfoValue(Long profileId) throws Exception {
        return buildUserInfoValue(profileId, null);
    }

    private String buildUserInfoValue(Long profileId, Role role) throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setProfileId(profileId);
        userInfoDto.setLang("en");
        userInfoDto.setTimezone("UTC");

        if (role != null) {
            userInfoDto.setRoles(Arrays.asList(role.toString()));
        }

        return mapper.writeValueAsString(userInfoDto);
    }
}
