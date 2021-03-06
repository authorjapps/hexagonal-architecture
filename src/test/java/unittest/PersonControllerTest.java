package unittest;

import com.baeldung.springboot.controller.PersonController;
import com.baeldung.springboot.entity.Person;
import com.baeldung.springboot.exception.GlobalExceptionHandler;
import com.baeldung.springboot.exception.PersonException;
import com.baeldung.springboot.model.PersonResponse;
import com.baeldung.springboot.model.dto.PersonDto;
import com.baeldung.springboot.service.PersonServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(MockitoJUnitRunner.Silent.class)
public class PersonControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    PersonController personControllerController;

    @Mock
    PersonServiceImpl personServiceImpl;

    @Mock
    ModelMapper modelMapper;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(personControllerController).setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetpersonById_WhenRecordExist_thenRespondWith_200() throws Exception {

        // GIVEN
        Person mockServiceResponse = prepareMockServiceResponse_getpersonById();
        given(personServiceImpl.getPersonById(anyLong())).willReturn(mockServiceResponse);

        // WHEN
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/v1/persons/1")).andReturn().getResponse();

        //THEN
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Person person = objectMapper.readValue(response.getContentAsString(), new TypeReference<Person>() {
        });

        assertThat(person).hasFieldOrProperty("id").isNotNull();

    }

    @Test
    public void testGetpersonById_WithRecordNotFound_thenRespondWith_404() throws Exception {

        // GIVEN
        given(personServiceImpl.getPersonById(anyLong())).willThrow(new PersonException("2", "404", "Person not found"));

        // WHEN
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/v1/persons/2")).andReturn().getResponse();

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testGetpersonById_WithInCorrectIdPattern_thenRespondWith_400() throws Exception {

        // WHEN
        MockHttpServletResponse response = this.mockMvc.perform(get("/api/v1/persons/ZZ")).andReturn().getResponse();

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testRegisterperson_WithValidRequest_ThenRespondWith_201() throws Exception {

        //GIVEN
        PersonDto personDto = new PersonDto();

        personDto.setFirstName("FirstJava");
        personDto.setLastName("LastJava");
        personDto.setGender("M");
        personDto.setPostCode("CW2ZZZ");
        personDto.setEmail("email@email.com");

        String requestPayLoad = objectMapper.writeValueAsString(personDto);

        PersonResponse mockResponse = new PersonResponse("Registration Success.", "2");

        given(personServiceImpl.createPerson(any(PersonDto.class))).willReturn(mockResponse);

        //WHEN
        MockHttpServletResponse response = this.mockMvc
                .perform(post("/api/v1/persons").
                        contentType(MediaType.APPLICATION_JSON_UTF8).
                        content(requestPayLoad)).
                        andReturn().getResponse();

        //THEN
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    private Person prepareMockServiceResponse_getpersonById() throws Exception {

        File personByIdResponse = ResourceUtils
                .getFile("classpath:data_files/response/get_person_byId_response_body.json");
        return objectMapper.readValue(personByIdResponse, new TypeReference<Person>() { });

    }

}
