package de.smarthome.server;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.responses.AvailabilityResponse;
import de.smarthome.app.model.responses.GetValueResponse;
import de.smarthome.app.model.responses.RegisterResponse;
import de.smarthome.app.model.responses.UID_Value;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.command.AdditionalConfigs;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.AdditionalConfigCommand;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.RegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.RegisterClientCommand;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.command.impl.UnRegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.UnregisterClientCommand;
import de.smarthome.server.gira.GiraServerHandler;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(MockitoJUnitRunner.class)
public class ServerHandlerTest {

    private String uriPrefix = "https://192.168.132.101";

    @Test
    public void testAvailabilityCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
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
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
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

    @Test
    public void testRegisterClientCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);

        RegisterClientCommand uiConfigCommand = new RegisterClientCommand("username", "password");
        String expectedToken = "abc";
        ResponseEntity<RegisterResponse> myEntity = new ResponseEntity<>(new RegisterResponse(expectedToken), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith(uriPrefix + "/api/v2/clients"),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<RegisterResponse>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(uiConfigCommand);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();
        Object actualBody = actualResponse.getBody();

        assertThat(actualStatusCode.value()).isEqualTo(200);
        assertThat(actualBody).isInstanceOf(RegisterResponse.class);
        String actualToken = ((RegisterResponse) actualBody).getToken();
        assertThat(actualToken).isEqualTo(expectedToken);
    }

    @Test
    public void testGetValueCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);

        GetValueCommand getValueCommand = new GetValueCommand("someUID");
        UID_Value firstExpectedValue = new UID_Value("firstUID", "21");
        UID_Value secondExpectedValue = new UID_Value("secondUID", "42");
        List<UID_Value> expectedValues = Arrays.asList(firstExpectedValue, secondExpectedValue);
        ResponseEntity<GetValueResponse> myEntity = new ResponseEntity<>(new GetValueResponse(expectedValues), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.matches(uriPrefix + "/api/v2/values/" +".*"+"?token="+".*"),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<GetValueResponse>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(getValueCommand);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();
        Object actualBody = actualResponse.getBody();

        assertThat(actualStatusCode.value()).isEqualTo(200);
        assertThat(actualBody).isInstanceOf(GetValueResponse.class);
        List<UID_Value> actualValues = ((GetValueResponse) actualBody).getValues();
        UID_Value firstActualValue = actualValues.get(0);
        assertThat(firstActualValue).isEqualToComparingFieldByField(firstExpectedValue);
        UID_Value secondActualValue = actualValues.get(1);
        assertThat(secondActualValue).isEqualToComparingFieldByField(secondExpectedValue);
    }

    @Test
    public void testChangeValueCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);

        String someUID = "someUID";
        ChangeValueCommand changeValueCommand = new ChangeValueCommand(someUID, 42);
        ResponseEntity<JsonNode> myEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith(uriPrefix + "/api/v2/values/" +someUID+"?"),
                ArgumentMatchers.eq(HttpMethod.PUT),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<JsonNode>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(changeValueCommand);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();

        assertThat(actualStatusCode.value()).isEqualTo(200);
    }

    @Test
    public void testAdditionalConfigCommand_CHANNEL(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);

        String IP = "someIP";
        AdditionalConfigs channel = AdditionalConfigs.CHANNEL;
        AdditionalConfigCommand additionalConfigCommand = new AdditionalConfigCommand(IP, channel);
        ResponseEntity<ChannelConfig> myEntity = new ResponseEntity<>(new ChannelConfig(new ArrayList<>()), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith("https://" +IP + "/"+channel.getResource()),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<ChannelConfig>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(additionalConfigCommand);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();
        Object actualBody = actualResponse.getBody();

        assertThat(actualStatusCode.value()).isEqualTo(200);
        assertThat(actualBody).isInstanceOf(ChannelConfig.class);
    }

    @Test
    public void testAdditionalConfigCommand_LOCATION(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);

        String IP = "someIP";
        AdditionalConfigs location = AdditionalConfigs.LOCATION;
        AdditionalConfigCommand additionalConfigCommand = new AdditionalConfigCommand(IP, location);
        ResponseEntity<BeaconLocations> myEntity = new ResponseEntity<>(new BeaconLocations(new ArrayList<>()), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith("https://" +IP + "/"+location.getResource()),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<BeaconLocations>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(additionalConfigCommand);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();
        Object actualBody = actualResponse.getBody();

        assertThat(actualStatusCode.value()).isEqualTo(200);
        assertThat(actualBody).isInstanceOf(BeaconLocations.class);
    }

    @Ignore
    @Test
    public void testAdditionalConfigCommand_BOUNDARIES(){
//        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
//        RestTemplateCreater mockedRestTemplateCreator = mock(RestTemplateCreater.class);
//        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
//        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
//        GiraServerHandler sh = new GiraServerHandler(ci);
//
//        String IP = "someIP";
//        AdditionalConfigs channel = AdditionalConfigs.BOUNDARIES;
//        AdditionalConfigCommand additionalConfigCommand = new AdditionalConfigCommand(IP, channel);
//        ResponseEntity<ChannelConfig> myEntity = new ResponseEntity<>(new BoundariesConfig(new ArrayList<>()), HttpStatus.OK);
//        Mockito.when(mockedRestTemplate.exchange(
//                ArgumentMatchers.startsWith("https://" +IP + "/"+channel.getResource()),
//                ArgumentMatchers.eq(HttpMethod.GET),
//                ArgumentMatchers.<HttpEntity<?>> any(),
//                ArgumentMatchers.<Class<ChannelConfig>>any())
//        ).thenReturn(myEntity);
//        ResponseEntity actualResponse = sh.sendRequest(additionalConfigCommand);
//        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
//        HttpStatus actualStatusCode = actualResponse.getStatusCode();
//        Object actualBody = actualResponse.getBody();
//
//        assertThat(actualStatusCode.value()).isEqualTo(200);
//        assertThat(actualBody).isInstanceOf(ChannelConfig.class);
    }

    @Test
    public void testUnregisterClientCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);


        UnregisterClientCommand unregisterClientCommand = new UnregisterClientCommand();
        ResponseEntity<JsonNode> myEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith(uriPrefix+"/api/v2/clients/"),
                ArgumentMatchers.eq(HttpMethod.DELETE),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<JsonNode>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(unregisterClientCommand);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();

        assertThat(actualStatusCode.value()).isEqualTo(200);
    }

    @Test
    public void testRegisterCallbackServerAtGiraCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);

        String IP = "someIP";
        RegisterCallbackServerAtGiraServer registerCallbackServerAtGiraServer = new RegisterCallbackServerAtGiraServer(IP);
        ResponseEntity<JsonNode> myEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.matches(uriPrefix+"/api/v2/clients/"+".*"+"callbacks"),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<JsonNode>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(registerCallbackServerAtGiraServer);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();

        assertThat(actualStatusCode.value()).isEqualTo(200);
    }

    @Test
    public void testUnRegisterCallbackServerAtGiraCommand(){
        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);
        GiraServerHandler sh = new GiraServerHandler(ci);

        String IP = "someIP";
        UnRegisterCallbackServerAtGiraServer unRegisterCallbackServerAtGiraServer = new UnRegisterCallbackServerAtGiraServer();
        ResponseEntity<JsonNode> myEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.matches(uriPrefix+"/api/v2/clients/"+".*"+"callbacks"),
                ArgumentMatchers.eq(HttpMethod.DELETE),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<JsonNode>>any())
        ).thenReturn(myEntity);
        ResponseEntity actualResponse = sh.sendRequest(unRegisterCallbackServerAtGiraServer);
        verify(mockedRestTemplate, times(1)).exchange(anyString(), any(), any(), any());
        HttpStatus actualStatusCode = actualResponse.getStatusCode();

        assertThat(actualStatusCode.value()).isEqualTo(200);
    }
}
