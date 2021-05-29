package de.smarthome.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.responses.AvailabilityResponse;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.server.gira.GiraServerHandler;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(MockitoJUnitRunner.class)
public class ServerHandlerTest {

    private String uriPrefix = "https://192.168.132.101";

//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private Request request;

    @Test
    public void testAvailabilityCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreater mockedRestTemplateCreater = mock(RestTemplateCreater.class);
        when(mockedRestTemplateCreater.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreater);
        GiraServerHandler sh = new GiraServerHandler(ci);


        CheckAvailabilityCommand checkAvailabilityCommand = new CheckAvailabilityCommand();
        ResponseEntity<AvailabilityResponse> myEntity = new ResponseEntity<>(new AvailabilityResponse("", "", "", "", ""), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.eq(uriPrefix + "/api"),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<AvailabilityResponse>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(checkAvailabilityCommand);
        verify(mockedRestTemplate, times(1)).exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();
        Object actualBody = actualResponse.getBody();

        assertThat(actualStatusCode.value()).isEqualTo(200);
        assertThat(actualBody).isInstanceOf(AvailabilityResponse.class);
    }



    @Test
    public void testUIConfigCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreater mockedRestTemplateCreater = mock(RestTemplateCreater.class);
        when(mockedRestTemplateCreater.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreater);
        GiraServerHandler sh = new GiraServerHandler(ci);

        UIConfigCommand uiConfigCommand = new UIConfigCommand();
        ResponseEntity<UIConfig> myEntity = new ResponseEntity<>(new UIConfig(null, null, null), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith(uriPrefix + "/api/v2/uiconfig?expand=locations&token="),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<UIConfig>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(uiConfigCommand);
        verify(mockedRestTemplate, times(1)).exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();
        Object actualBody = actualResponse.getBody();

        assertThat(actualStatusCode.value()).isEqualTo(200);
        assertThat(actualBody).isInstanceOf(UIConfig.class);
    }
}
