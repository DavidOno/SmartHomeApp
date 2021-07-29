package de.smarthome.server;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.List;

import de.smarthome.app.model.responses.CallbackValueInput;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(MockitoJUnitRunner.class)
public class CallbackObserverTests {

    @Test
    public void notify_single_sub(){
        CallbackSubscriber mockedSub = mock(CallbackSubscriber.class);
        CallbackValueInput mocketInput = mock(CallbackValueInput.class);

        CallbackObserver callbackObserver = new CallbackObserver();
        callbackObserver.subscribe(mockedSub);

        callbackObserver.notify(mocketInput);

        ArgumentCaptor<CallbackValueInput> argument = ArgumentCaptor.forClass(CallbackValueInput.class);
        verify(mockedSub).update(argument.capture());

        assertThat(argument.getValue()).isEqualTo(mocketInput);
    }

    @Test
    public void multiple_notify_single_sub(){
        CallbackSubscriber mockedSub = mock(CallbackSubscriber.class);
        CallbackValueInput mocketInput = mock(CallbackValueInput.class);
        CallbackValueInput mocketInput2 = mock(CallbackValueInput.class);

        CallbackObserver callbackObserver = new CallbackObserver();
        callbackObserver.subscribe(mockedSub);

        callbackObserver.notify(mocketInput);
        callbackObserver.notify(mocketInput2);

        ArgumentCaptor<CallbackValueInput> argument = ArgumentCaptor.forClass(CallbackValueInput.class);
        verify(mockedSub, times(2)).update(argument.capture());

        List<CallbackValueInput> allValues = argument.getAllValues();
        assertThat(allValues.get(0)).isEqualTo(mocketInput);
        assertThat(allValues.get(1)).isEqualTo(mocketInput2);
    }

    @Test
    public void notify_multi_sub(){
        CallbackSubscriber mockedSub = mock(CallbackSubscriber.class);
        CallbackSubscriber mockedSub2 = mock(CallbackSubscriber.class);
        CallbackValueInput mocketInput = mock(CallbackValueInput.class);

        CallbackObserver callbackObserver = new CallbackObserver();
        callbackObserver.subscribe(mockedSub);
        callbackObserver.subscribe(mockedSub2);

        callbackObserver.notify(mocketInput);

        ArgumentCaptor<CallbackValueInput> argument = ArgumentCaptor.forClass(CallbackValueInput.class);
        verify(mockedSub).update(argument.capture());
        verify(mockedSub2).update(argument.capture());

        assertThat(argument.getValue()).isEqualTo(mocketInput);
    }
}
