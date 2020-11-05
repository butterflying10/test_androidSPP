package com.example.test_spp;

import java.util.ArrayList;
import java.util.List;

/**
 * 存放观测文件一个历元的数据
 */
public class EpochOBS {
    private static final String TAG = Test_ReadOBS.class.getSimpleName();

    public List<SatelliteParameters> GPSsatelliteParametersList = new ArrayList<>();

    public List<SatelliteParameters> BDSsatelliteParametersList = new ArrayList<>();

    public List<SatelliteParameters> getBDSsatelliteParametersList() {
        return BDSsatelliteParametersList;
    }

    public List<SatelliteParameters> getGPSsatelliteParametersList() {
        return GPSsatelliteParametersList;
    }

    public void setBDSsatelliteParametersList(List<SatelliteParameters> BDSsatelliteParametersList) {
        this.BDSsatelliteParametersList = BDSsatelliteParametersList;
    }

    public void setGPSsatelliteParametersList(List<SatelliteParameters> GPSsatelliteParametersList) {
        this.GPSsatelliteParametersList = GPSsatelliteParametersList;
    }


}
