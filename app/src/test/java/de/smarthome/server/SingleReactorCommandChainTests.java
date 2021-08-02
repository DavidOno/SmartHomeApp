package de.smarthome.server;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.responses.AvailabilityResponse;
import de.smarthome.app.repository.responsereactor.ResponseReactorUIConfig;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.SingleReactorCommandChain;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.SingleReactorCommandChainImpl;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.server.gira.GiraServerHandler;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(MockitoJUnitRunner.class)
public class SingleReactorCommandChainTests {

    private String uriPrefix = "https://192.168.132.101";

    @Test
    public void singleCommandChain_single_command(){
        ResponseReactorUIConfig responseReactor = mock(ResponseReactorUIConfig.class);
        SingleReactorCommandChain cc = new SingleReactorCommandChainImpl(responseReactor)
                .add(new UIConfigCommand());

        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);

        ResponseEntity<UIConfig> myEntity = new ResponseEntity<>(new UIConfig(null, null, null), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith(uriPrefix + "/api/v2/uiconfig?expand=locations&token="),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<UIConfig>>any())
        ).thenReturn(myEntity);

        GiraServerHandler sh = new GiraServerHandler(ci);
        sh.sendRequest(cc);
        ArgumentCaptor<ResponseEntity> argument = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(responseReactor).react(argument.capture());

        assertThat(argument.getValue().getStatusCode().value()).isEqualTo(200);
        assertThat(argument.getValue().getBody()).isInstanceOf(UIConfig.class);

    }

    @Test
    public void singleCommandChain_multiple_commands(){
        ResponseReactorUIConfig responseReactor = mock(ResponseReactorUIConfig.class);
        SingleReactorCommandChain cc = new SingleReactorCommandChainImpl(responseReactor);
        cc.add(new CheckAvailabilityCommand());
        cc.add(new UIConfigCommand());

        RestTemplate mockedRestTemplate = mock(RestTemplate.class);
        RestTemplateCreator mockedRestTemplateCreator = mock(RestTemplateCreator.class);
        when(mockedRestTemplateCreator.create()).thenReturn(mockedRestTemplate);
        CommandInterpreter ci = new HomeServerCommandInterpreter(mockedRestTemplateCreator);

        CheckAvailabilityCommand checkAvailabilityCommand = new CheckAvailabilityCommand();
        ResponseEntity<AvailabilityResponse> myEntity = new ResponseEntity<>(new AvailabilityResponse("", "", "", "", ""), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.eq(uriPrefix + "/api"),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<AvailabilityResponse>>any())
        ).thenReturn(myEntity);

        ResponseEntity<UIConfig> myEntity2 = new ResponseEntity<>(new UIConfig(null, null, null), HttpStatus.OK);
        Mockito.when(mockedRestTemplate.exchange(
                ArgumentMatchers.startsWith(uriPrefix + "/api/v2/uiconfig?expand=locations&token="),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>> any(),
                ArgumentMatchers.<Class<UIConfig>>any())
        ).thenReturn(myEntity2);

        GiraServerHandler sh = new GiraServerHandler(ci);
        sh.sendRequest(cc);

        ArgumentCaptor<ResponseEntity> argument = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(responseReactor, times(2)).react(argument.capture());

        List<ResponseEntity> allValues = argument.getAllValues();
        assertThat(allValues.get(0).getStatusCode().value()).isEqualTo(200);
        assertThat(allValues.get(0).getBody()).isInstanceOf(AvailabilityResponse.class);
        assertThat(allValues.get(1).getStatusCode().value()).isEqualTo(200);
        assertThat(allValues.get(1).getBody()).isInstanceOf(UIConfig.class);
    }
}
