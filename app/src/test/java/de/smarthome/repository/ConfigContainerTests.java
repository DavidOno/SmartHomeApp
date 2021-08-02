package de.smarthome.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;

import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.Boundary;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.utility.ToastUtility;

import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigContainerTests {

    @Rule
    //Allows LiveData.postValue() to be executed in the mainThread of the test
    // => LiveData can be used to verify test results
    //testImplementation "android.arch.core:core-testing"
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    public ConfigContainer setUpConfigContainerWithEmptyUIConfig(){
        ConfigContainer cc = ConfigContainer.getInstance();
        LinkedList<Function> emptyFunctionList = new LinkedList<>();
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(emptyFunctionList, emptyLocationList, "1");
        cc.setUIConfig(uiConfig);
        return cc;
    }

    @After
    public void cleanUp(){
        ConfigContainer cc = ConfigContainer.getInstance();
        cc.setUIConfig(null);
        cc.setChannelConfig(null);
        cc.setBeaconLocations(null);
        cc.setBoundaryConfig(null);

        cc.setSelectedLocation(null);
        cc.setSelectedFunction(null);

        cc.setLocationList(null);
        cc.setFunctionMap(null);
        cc.setDataPointMap(null);
        cc.setBoundaryMap(null);
    }

    @Test
    public void testInitNewUIConfigWithNothingSelected(){
        ConfigContainer cc = ConfigContainer.getInstance();
        cc.setSelectedLocation(null);
        cc.setSelectedFunction(null);

        UIConfig newUiConfig = mock(UIConfig.class);
        cc.initNewUIConfig(newUiConfig);

        verify(newUiConfig, times(1)).initParentLocations();
        assertThat(cc.getUIConfig()).isEqualTo(newUiConfig);
    }

    @Test
    public void testInitNewUIConfigWithOnlyLocationSelected() throws InterruptedException {
        ConfigContainer cc = ConfigContainer.getInstance();

        LinkedList<String> fl = new LinkedList<>();
        fl.add("1");
        fl.add("2");
        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");

        cc.setSelectedLocation(location);
        cc.setSelectedFunction(null);

        LinkedList<Function> fl2 = new LinkedList<>();
        Function f1 = new Function("f_function", "1", "dummy", "dummy", null);
        Function f2 = new Function("f_Status", "2", "dummy", "dummy", null);
        fl2.add(f1);
        fl2.add(f2);
        LinkedList<Location> ll2 = new LinkedList<>();
        ll2.add(location);
        UIConfig uiConfig = new UIConfig(fl2, ll2, "2");

        cc.initNewUIConfig(uiConfig);

        assertThat(cc.getFunctionMap().getValue()).containsEntry(f1, f2);
        assertThat(cc.getUIConfig()).isEqualTo(uiConfig);
    }

    @Test
    public void testInitNewUIConfigWithOnlyFunctionSelected(){
        ConfigContainer cc = ConfigContainer.getInstance();

        Function f1 = new Function("f_function", "1", "dummy", "dummy", null);
        cc.setSelectedLocation(null);
        cc.setSelectedFunction(f1);

        LinkedList<Function> fl2 = new LinkedList<>();
        fl2.add(f1);
        LinkedList<Location> ll2 = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(fl2, ll2, "2");

        cc.initNewUIConfig(uiConfig);

        assertThat(cc.getBoundaryMap().getValue()).isNull();
        assertThat(cc.getDataPointMap().getValue()).isNull();
        assertThat(cc.getUIConfig()).isEqualTo(uiConfig);
    }

    @Test
    public void testInitNewUIConfigWithLocationAndFunctionSelected(){
        ConfigContainer cc = ConfigContainer.getInstance();
        LinkedList<String> fl = new LinkedList<>();
        fl.add("1");
        fl.add("2");
        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");

        LinkedList<Function> fl2 = new LinkedList<>();
        LinkedList<Datapoint> dpl = new LinkedList<>();
        Datapoint dp = new Datapoint("1", "datapoint1");
        dpl.add(dp);
        Function f1 = new Function("f_function", "1", "dummy", "dummy", dpl);
        Function f2 = new Function("f_Status", "2", "dummy", "dummy", dpl);
        cc.setSelectedLocation(location);
        cc.setSelectedFunction(f1);


        fl2.add(f1);
        fl2.add(f2);
        LinkedList<Location> ll2 = new LinkedList<>();
        ll2.add(location);
        UIConfig uiConfig = new UIConfig(fl2, ll2, "2");

        cc.initNewUIConfig(uiConfig);

        assertThat(Repository.getInstance(null).getBeaconCheck().getValue()).isFalse();
        assertThat(cc.getBoundaryMap().getValue()).isEmpty();
        assertThat(cc.getDataPointMap().getValue()).containsEntry(dp, dp);
        assertThat(cc.getUIConfig()).isEqualTo(uiConfig);
    }

    @Test
    public void testInitSelectedLocationWithOutUiConfigInitialised(){
        ConfigContainer cc = ConfigContainer.getInstance();

        Function f1 = new Function("f_function", "1", "dummy", "dummy", null);
        Function f2 = new Function("f_Status", "2", "dummy", "dummy", null);
        LinkedList<String> fl = new LinkedList<>();
        fl.add(f1.getID());
        fl.add(f2.getID());

        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");

        cc.initSelectedLocation(location);

        assertThat(cc.getSelectedLocation()).isEqualTo(location);
        assertThat(cc.getFunctionMap().getValue()).isEmpty();
    }

    @Test
    public void testInitSelectedLocationWithUiConfigInitialised(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        Function f1 = new Function("f_function", "1", "dummy", "dummy", null);
        Function f2 = new Function("f_Status", "2", "dummy", "dummy", null);
        LinkedList<String> fl = new LinkedList<>();
        fl.add(f1.getID());
        fl.add(f2.getID());

        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");

        cc.getUIConfig().getLocations().add(location);
        cc.getUIConfig().getFunctions().add(f1);
        cc.getUIConfig().getFunctions().add(f2);
        cc.initNewUIConfig(cc.getUIConfig());

        cc.initSelectedLocation(location);

        assertThat(cc.getSelectedLocation()).isEqualTo(location);
        assertThat(cc.getFunctionMap().getValue()).containsEntry(f1, f2);
    }

    @Test
    public void testInitSelectedFunctionWithUiConfigInitialisedStatusFunctionHasFewerDataPoints(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<Datapoint> dpl = new LinkedList<>();
        Datapoint dp = new Datapoint("1", "datapoint1");
        dpl.add(dp);
        dpl.add(dp);
        LinkedList<Datapoint> dpl2 = new LinkedList<>();
        dpl2.add(dp);
        Function f2 = new Function("f_Status", "2", "dummy", "dummy", dpl2);
        Function function = new Function("f_function", "1","dummy", "22", dpl);

        LinkedList<Function> fl2 = new LinkedList<>();
        fl2.add(f2);
        fl2.add(function);

        LinkedList<String> fl = new LinkedList<>();
        fl.add("1");
        fl.add("2");
        LinkedList<Location> ll2 = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll2,"dummy");
        UIConfig uiConfig = new UIConfig(fl2, ll2, "2");
        cc.initNewUIConfig(uiConfig);
        cc.initSelectedLocation(location);

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap()).isNotNull();
        assertThat(cc.getDataPointMap().getValue()).containsKey(dp);
    }

    @Test
    public void testInitSelectedFunctionWithUiConfigInitialised(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<Datapoint> dpl = new LinkedList<>();
        Datapoint dp = new Datapoint("1", "datapoint1");
        dpl.add(dp);
        Function function = new Function("l1", "1","dummy", "22", dpl);

        LinkedList<Function> fl2 = new LinkedList<>();
        LinkedList<Location> ll2 = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(fl2, ll2, "2");
        cc.initNewUIConfig(uiConfig);

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap()).isNotNull();
        assertThat(cc.getDataPointMap().getValue()).containsKey(dp);
    }

    @Test
    public void testInitSelectedFunctionWithOutUiConfigInitialised(){
        ConfigContainer cc = ConfigContainer.getInstance();
        LinkedList<Datapoint> dpl = new LinkedList<>();
        Datapoint dp = new Datapoint("1", "datapoint1");
        dpl.add(dp);
        Function function = new Function("l1", "1","dummy", "22", dpl);

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap()).isNotNull();
        assertThat(cc.getDataPointMap().getValue()).containsKey(dp);
    }

    @Test
    public void testInitSelectedFunctionBoundaryMapInitialised(){
        ConfigContainer cc = ConfigContainer.getInstance();
        LinkedList<Datapoint> dpl = new LinkedList<>();
        Datapoint dp = new Datapoint("1", "datapoint1");
        dpl.add(dp);
        Function function = new Function("f1", "1","dummy", "22", dpl);

        LinkedList<String> fl = new LinkedList<>();
        fl.add("1");
        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");

        cc.setSelectedLocation(location);
        LinkedList<Boundary> x = new LinkedList<>();
        LinkedList<BoundaryDataPoint> y = new LinkedList<>();
        y.add(new BoundaryDataPoint("datapoint1", "10", "20"));
        x.add(new Boundary("f1_boundary", "l1", y));
        cc.setBoundaryConfig(new BoundariesConfig(x));

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap().getValue()).containsKey(dp);
        assertThat(cc.getDataPointMap().getValue()).containsKey(dp);
    }

    @Test
    public void testInitUiConfigSelectedLocationNotContained(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<String> fl = new LinkedList<>();

        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");
        cc.initSelectedLocation(location);

        cc.initNewUIConfig(cc.getUIConfig());

        assertThat(ToastUtility.getInstance().getNewToast().getValue()).isTrue();
        assertThat(cc.getSelectedLocation()).isEqualTo(location);
    }

    @Test
    public void testInitUiConfigSelectedFunctionNotContained(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<String> fl = new LinkedList<>();
        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");

        cc.initSelectedLocation(location);
        Function test = new Function("f_notContained", "3", "dummy", "dummy", null);
        cc.setSelectedFunction(test);

        cc.initNewUIConfig(cc.getUIConfig());

        assertThat(cc.getSelectedFunction()).isEqualTo(test);
        assertThat(ToastUtility.getInstance().getNewToast().getValue()).isTrue();
    }

    @Test
    public void testInitFunctionMapWithParentLocation(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<String> fl = new LinkedList<>();
        Function f2 = new Function("f_parent", "2", "dummy", "dummy", null);
        fl.add(f2.getID());
        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l2", "2","dummy", fl, ll,"dummy");

        LinkedList<String> fl2 = new LinkedList<>();
        Function f1 = new Function("f_child", "1","dummy", "22", null);
        fl2.add(f1.getID());
        LinkedList<Location> ll2 = new LinkedList<>();
        ll2.add(location);
        Location parentLocation = new Location("l1", "1","dummy", fl2, ll2,"dummy");

        cc.getUIConfig().getFunctions().add(f1);
        cc.getUIConfig().getFunctions().add(f2);
        cc.getUIConfig().getLocations().add(parentLocation);
        cc.initNewUIConfig(cc.getUIConfig());

        cc.initSelectedLocation(location);

        assertThat(cc.getSelectedLocation()).isEqualTo(location);
        assertThat(cc.getLocationList().getValue()).hasSize(2);
        assertThat(cc.getFunctionMap().getValue()).hasSize(2);
    }
}