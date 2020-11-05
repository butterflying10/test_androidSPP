/*
 * Copyright 2018 TFI Systems

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.example.test_spp;


import com.example.test_spp.coord.Coordinates;
import com.example.test_spp.coord.SatellitePosition;
import com.example.test_spp.corrections.TopocentricCoordinates;

/**
 * Container class for storing satellite related parameters
 */
public class SatelliteParameters {



    public double getPseudorange() {
        return pseudorange.getPseudorange();
    }

    public void setPseudorange(Pseudorange pseudorange) {
        this.pseudorange = pseudorange;
    }

    /**
     * sum of all calculated corrections   全部的改正
     */
    private double accumulatedCorrection;

    /**
     * Pseudorange to the satellite         伪距
     */
    protected Pseudorange pseudorange;

    private double carrierFrequency;


    /**
     * Position of the satellite     卫星位置
     */
    protected Coordinates coordinates;

    public SatellitePosition getSatellitePosition() {
        return satellitePosition;
    }

    public void setSatellitePosition(SatellitePosition satellitePosition) {
        this.satellitePosition = satellitePosition;
        this.clockBias = satellitePosition.getSatelliteClockError() * GNSSConstants.SPEED_OF_LIGHT; //todo: add this as a separate correction.
    }

    /**
     * Satellite position in the ECEF frame
     */
    private SatellitePosition satellitePosition;

    public SatellitePosition getSatelliteVelocity() {
        return satelliteVelocity;
    }

    public void setSatelliteVelocity(SatellitePosition satelliteVelocity) {
        this.satelliteVelocity = satelliteVelocity;
    }

    /**
     * Satellite velocity in the ECEF frame
     */
    private SatellitePosition satelliteVelocity;

    /**
     * Satellite id in the constellation
     */
    private int satId;

    /**
     * Strength of the signal received from the satellite
     */
    private double signalStrength;

    /**
     * Type of the constellation as defined by the GnssStatus, extended by the definitions
     * in the Constellation class
     */
    private int satType;

    /**
     * refTime    历元时刻以unixtime为基准的时间  单位为ms
      */
    private Time refTime;

    public Time getrefTime() {
        return refTime;
    }

    public void setrefTime(Time time) {
        this.refTime = time;
    }


    /**
     * @param satelliteId Id of the newly created satellite
     * @param pseudorange pseudorange to this satellite
     */
    public SatelliteParameters(Time refTime,int satelliteId, Pseudorange pseudorange){
        this.refTime=refTime;
        this.satId = satelliteId;
        this.pseudorange = pseudorange;
        setSignalStrength(0.0);
    }

    /**
     * @param newPose new coordinates of the satellite
     */
    protected void setCoordinates(Coordinates newPose){
        coordinates = newPose;
    }

    public int getSatId() {
        return satId;
    }

    public void setSatId(int satId) {
        this.satId = satId;
    }

    /**
     * Stores the clock bias of the satellite
     */
    private double clockBias;

    public double getClockBias() {
        return clockBias;
    }

    public void setClockBias(double clockBias) {
        this.clockBias = clockBias;
    }

    /**
     * asimuth and elevation of the satellite with respect to the user
     */
    private TopocentricCoordinates rxTopo;

    /**
     * @return asimuth and elevation of the satellite with respect to the user
     */
    public TopocentricCoordinates getRxTopo() {
        return rxTopo;
    }

    /**
     * sets the asimuth and elevation of the satellite with respect to the user.
     * This also sets the pseudorange measurement variance
     * @param rxTopo new coordinates
     */
    public void setRxTopo(TopocentricCoordinates rxTopo) { //todo rename to better indicate function
        this.rxTopo = rxTopo;
        pseudorange.setMeasurementVariance(
                1.0 / Math.pow(Math.tan(rxTopo.getElevation()-0.1),2)/100.0);
    }

    /**
     * @return correction which is to be applied to the pseudorange
     */
    public double getAccumulatedCorrection() {
        return accumulatedCorrection;
    }

    /**
     *
     * @param accumulatedCorrection correction which is to be applied to the pseudorange
     */
    public void setAccumulatedCorrection(double accumulatedCorrection) {
        this.accumulatedCorrection = accumulatedCorrection;
    }

    /**
     *
     * @return used pseudorange object
     */
    public Pseudorange getPseudorangeObject() {
        return pseudorange;
    }

    /**
     * @return Signal strength for the satellite [dB]
     */
    public double getSignalStrength() {
        return signalStrength;
    }

    /**
     *
     * @param signalStrength assigned signal strength of the satellite
     */
    public void setSignalStrength(double signalStrength) {
        this.signalStrength = signalStrength;
    }

    /**
     * Unique ID of the satellite
     */
    private String uniqueSatId;

    /**
     * @return unique id of the satellite
     */
    public String getUniqueSatId(){
        return uniqueSatId;
    }

    /**
     * sets the unique id of the satellite
     * @param uniqueSatId
     */
    public void setUniqueSatId(String uniqueSatId){
        this.uniqueSatId = uniqueSatId;
    }

    /**
     *
     * @return constellation type as defined by the {@code GnssSatus} or {@code Constellation}
     */
    public int getSatType() {
        return satType;
    }

    /**
     * Sets constellation type
     * @param constellationType
     */
    public void setSatType(int constellationType) {
        this.satType = constellationType;
    }

    public double getCarrierFrequency() {
        return carrierFrequency;
    }

    public void setCarrierFrequency(double carrierFrequency) {
        this.carrierFrequency = carrierFrequency;
    }


    /**
     * 载波相位观测值  phase
     */
    private double phase;

    public double getPhase() {
        return phase;
    }

    public void setPhase(double phase) {
        this.phase = phase;
    }
    /**
     * SNR,信噪比
     */
    private double snr;

    public double getSnr() {
        return snr;
    }

    public void setSnr(double snr) {
        this.snr = snr;
    }
    /**
     * 多普勒观测值
     */
    private double doppler;

    public double getDoppler() {
        return doppler;
    }

    public void setDoppler(double doppler) {
        this.doppler = doppler;
    }
}
