package de.smarthome.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.Boundary;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.app.utility.ToastUtility;
import de.smarthome.beacons.BeaconLocation;
import de.smarthome.beacons.BeaconLocations;

import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigContainerTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    public ConfigContainer setUpConfigContainerWithEmptyUIConfig(){
        ConfigContainer cc = new ConfigContainer();
        LinkedList<Function> emptyFunctionList = new LinkedList<>();
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(emptyFunctionList, emptyLocationList, "1");
        cc.setUIConfig(uiConfig);
        return cc;
    }

    //ok
    @Test
    public void testInitNewUIConfigWithNothingSelected(){
        ConfigContainer cc = new ConfigContainer();
        cc.setSelectedLocation(null);
        cc.setSelectedFunction(null);

        UIConfig newUiConfig = mock(UIConfig.class);
        cc.initNewUIConfig(newUiConfig);

        verify(newUiConfig, times(1)).initParentLocations();
        assertThat(cc.getUIConfig()).isEqualTo(newUiConfig);
    }

    //ok
    @Test
    public void testInitNewUIConfigWithOnlyLocationSelected() throws InterruptedException {
        ConfigContainer cc = new ConfigContainer();

        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add("1");
        functionIdList.add("2");
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, emptyLocationList,"dummy");

        cc.setSelectedLocation(location);
        cc.setSelectedFunction(null);

        LinkedList<Function> functionList = new LinkedList<>();
        Function function = new Function("f_function", "1", "dummy", "dummy", null);
        Function functionStatus = new Function("f_Status", "2", "dummy", "dummy", null);
        functionList.add(function);
        functionList.add(functionStatus);
        LinkedList<Location> locationList = new LinkedList<>();
        locationList.add(location);
        UIConfig uiConfig = new UIConfig(functionList, locationList, "2");

        cc.initNewUIConfig(uiConfig);

        assertThat(cc.getFunctionMap().getValue()).containsEntry(function, functionStatus);
        assertThat(cc.getUIConfig()).isEqualTo(uiConfig);
    }

    //ok
    @Test
    public void testInitNewUIConfigWithOnlyFunctionSelected(){
        ConfigContainer cc = new ConfigContainer();

        Function function = new Function("f_function", "1", "dummy", "dummy", null);
        cc.setSelectedLocation(null);
        cc.setSelectedFunction(function);

        LinkedList<Function> functionList = new LinkedList<>();
        functionList.add(function);
        LinkedList<Location> locationList = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(functionList, locationList, "2");

        cc.initNewUIConfig(uiConfig);

        assertThat(cc.getBoundaryMap().getValue()).isNull();
        assertThat(cc.getDataPointMap().getValue()).isNull();
        assertThat(cc.getUIConfig()).isEqualTo(uiConfig);
    }

    //ok
    @Test
    public void testInitNewUIConfigWithLocationAndFunctionSelected(){
        ConfigContainer cc = new ConfigContainer();
        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add("1");
        functionIdList.add("2");
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, emptyLocationList,"dummy");

        LinkedList<Datapoint> datapointList = new LinkedList<>();
        Datapoint dp = new Datapoint("1", "datapoint1");
        datapointList.add(dp);
        Function function = new Function("f_function", "1", "dummy", "dummy", datapointList);
        Function functionStatus = new Function("f_Status", "2", "dummy", "dummy", datapointList);

        LinkedList<Function> functionList = new LinkedList<>();
        functionList.add(function);
        functionList.add(functionStatus);
        cc.setSelectedLocation(location);
        cc.setSelectedFunction(function);

        LinkedList<Location> ll2 = new LinkedList<>();
        ll2.add(location);
        UIConfig uiConfig = new UIConfig(functionList, ll2, "2");

        cc.initNewUIConfig(uiConfig);

        assertThat(cc.getBoundaryMap().getValue()).isEmpty();
        assertThat(cc.getDataPointMap().getValue()).containsEntry(dp, dp);
        assertThat(cc.getUIConfig()).isEqualTo(uiConfig);
    }

    //ok
    @Test
    public void testInitSelectedLocationWithOutUiConfigInitialised(){
        ConfigContainer cc = new ConfigContainer();

        Function function = new Function("f_function", "1", "dummy", "dummy", null);
        Function functionStatus = new Function("f_Status", "2", "dummy", "dummy", null);
        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add(function.getID());
        functionIdList.add(functionStatus.getID());

        LinkedList<Location> emptyLocationLost = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, emptyLocationLost,"dummy");

        cc.initSelectedLocation(location);

        assertThat(cc.getSelectedLocation()).isEqualTo(location);
        assertThat(cc.getFunctionMap().getValue()).isEmpty();
    }

    //ok
    @Test
    public void testInitSelectedLocationWithUiConfigInitialised(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        Function function = new Function("f_function", "1", "dummy", "dummy", null);
        Function functionStatus = new Function("f_Status", "2", "dummy", "dummy", null);
        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add(function.getID());
        functionIdList.add(functionStatus.getID());

        LinkedList<Location> locationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, locationList,"dummy");

        cc.getUIConfig().getLocations().add(location);
        cc.getUIConfig().getFunctions().add(function);
        cc.getUIConfig().getFunctions().add(functionStatus);
        cc.initNewUIConfig(cc.getUIConfig());

        cc.initSelectedLocation(location);

        assertThat(cc.getSelectedLocation()).isEqualTo(location);
        assertThat(cc.getFunctionMap().getValue()).containsEntry(function, functionStatus);
    }

    //ok
    @Test
    public void testInitSelectedFunctionWithUiConfigInitialisedStatusFunctionHasFewerDataPoints(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<Datapoint> datapointListLong = new LinkedList<>();
        Datapoint datapoint = new Datapoint("1", "datapoint1");
        datapointListLong.add(datapoint);
        datapointListLong.add(datapoint);
        LinkedList<Datapoint> dataPointListShort = new LinkedList<>();
        dataPointListShort.add(datapoint);
        Function functionStatus = new Function("f_Status", "2", "dummy", "dummy", dataPointListShort);
        Function function = new Function("f_function", "1","dummy", "22", datapointListLong);

        LinkedList<Function> functionList = new LinkedList<>();
        functionList.add(functionStatus);
        functionList.add(function);

        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add("1");
        functionIdList.add("2");
        LinkedList<Location> locationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, locationList,"dummy");
        UIConfig uiConfig = new UIConfig(functionList, locationList, "2");
        cc.initNewUIConfig(uiConfig);
        cc.initSelectedLocation(location);

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap()).isNotNull();
        assertThat(cc.getDataPointMap().getValue()).containsKey(datapoint);
    }

    //ok
    @Test
    public void testInitSelectedFunctionWithUiConfigInitialised(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<Datapoint> datapointList = new LinkedList<>();
        Datapoint datapoint = new Datapoint("1", "datapoint1");
        datapointList.add(datapoint);
        Function function = new Function("l1", "1","dummy", "22", datapointList);

        LinkedList<Function> functionList = new LinkedList<>();
        LinkedList<Location> locationList = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(functionList, locationList, "2");
        cc.initNewUIConfig(uiConfig);

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap()).isNotNull();
        assertThat(cc.getDataPointMap().getValue()).containsKey(datapoint);
    }

    //ok
    @Test
    public void testInitSelectedFunctionWithOutUiConfigInitialised(){
        ConfigContainer cc = new ConfigContainer();
        LinkedList<Datapoint> datapointList = new LinkedList<>();
        Datapoint datapoint = new Datapoint("1", "datapoint1");
        datapointList.add(datapoint);
        Function function = new Function("l1", "1","dummy", "22", datapointList);

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap()).isNotNull();
        assertThat(cc.getDataPointMap().getValue()).containsKey(datapoint);
    }

    //ok
    @Test
    public void testInitSelectedFunctionBoundaryMapInitialised(){
        ConfigContainer cc = new ConfigContainer();
        LinkedList<Datapoint> datapointList = new LinkedList<>();
        Datapoint datapoint = new Datapoint("1", "datapoint1");
        datapointList.add(datapoint);
        Function function = new Function("f1", "1","dummy", "22", datapointList);

        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add("1");
        LinkedList<Location> locationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, locationList,"dummy");

        cc.setSelectedLocation(location);
        LinkedList<Boundary> boundaryList = new LinkedList<>();
        LinkedList<BoundaryDataPoint> boundaryDatapointList = new LinkedList<>();
        boundaryDatapointList.add(new BoundaryDataPoint("datapoint1", "10", "20"));
        boundaryList.add(new Boundary("f1_boundary", "l1", boundaryDatapointList));
        cc.setBoundaryConfig(new BoundariesConfig(boundaryList));

        cc.initSelectedFunction(function);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(cc.getBoundaryMap().getValue()).containsKey(datapoint);
        assertThat(cc.getDataPointMap().getValue()).containsKey(datapoint);
    }

    //ok
    @Test
    public void testInitUiConfigSelectedLocationNotContained(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<String> functionIdList = new LinkedList<>();

        LinkedList<Location> locationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, locationList,"dummy");
        cc.initSelectedLocation(location);

        UIConfig mockUIConfig = mock(UIConfig.class);
        cc.initNewUIConfig(mockUIConfig);

        assertThat(ToastUtility.getInstance().getNewToast().getValue()).isTrue();
        assertThat(cc.getSelectedLocation()).isEqualTo(location);
    }

    //ok
    @Test
    public void testInitUiConfigSelectedFunctionNotContained(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<String> functionIdList = new LinkedList<>();
        LinkedList<Location> locationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, locationList,"dummy");

        cc.initSelectedLocation(location);
        Function function = new Function("f_notContained", "3", "dummy", "dummy", null);
        cc.setSelectedFunction(function);

        UIConfig mockUIConfig = mock(UIConfig.class);
        cc.initNewUIConfig(mockUIConfig);

        assertThat(cc.getSelectedFunction()).isEqualTo(function);
        assertThat(ToastUtility.getInstance().getNewToast().getValue()).isTrue();
    }

    //ok
    @Test
    public void testInitFunctionMapWithParentLocation(){
        ConfigContainer cc = setUpConfigContainerWithEmptyUIConfig();
        LinkedList<String> functionIdListChild = new LinkedList<>();
        Function functionChild = new Function("f_child", "2", "dummy", "dummy", null);
        functionIdListChild.add(functionChild.getID());
        LinkedList<Location> locationListChild = new LinkedList<>();
        Location locationChild = new Location("lChild", "2","dummy", functionIdListChild, locationListChild,"dummy");

        LinkedList<String> functionIdListParent = new LinkedList<>();
        Function functionParent = new Function("f_parent", "1","dummy", "22", null);
        functionIdListParent.add(functionParent.getID());
        LinkedList<Location> locationListParent = new LinkedList<>();
        locationListParent.add(locationChild);
        Location parentLocation = new Location("lParent", "1","dummy", functionIdListParent, locationListParent,"dummy");

        UIConfig newUIConfig = new UIConfig(cc.getUIConfig().getFunctions(), cc.getUIConfig().getLocations(), "2");
        newUIConfig.getFunctions().add(functionChild);
        newUIConfig.getFunctions().add(functionParent);
        newUIConfig.getLocations().add(parentLocation);
        cc.initNewUIConfig(newUIConfig);

        cc.initSelectedLocation(locationChild);

        assertThat(cc.getSelectedLocation()).isEqualTo(locationChild);
        assertThat(cc.getLocationList().getValue()).hasSize(2);
        assertThat(cc.getFunctionMap().getValue()).hasSize(2);
    }

    //ok
    @Test
    public void testInitBeaconLocationWithChangedBeaconLocations(){
        List<BeaconLocation> bl = new ArrayList<>();
        BeaconLocations bls = new BeaconLocations(bl);

        ConfigContainer cc = new ConfigContainer();
        cc.initBeaconLocations(bls);

        List<BeaconLocation> changedBl = new ArrayList<>();
        changedBl.add(new BeaconLocation("roomUID", "BeaconID"));
        BeaconLocations changedBls = new BeaconLocations(changedBl);

        cc.initBeaconLocations(changedBls);

        assertThat(cc.getBeaconLocations()).isEqualTo(changedBls);
    }
}