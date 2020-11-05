package com.example.test_spp;

import com.example.test_spp.coord.Coordinates;
import com.example.test_spp.coord.SatellitePosition;
import com.example.test_spp.corrections.Correction;
import com.example.test_spp.corrections.IonoCorrection;
import com.example.test_spp.corrections.ShapiroCorrection;
import com.example.test_spp.corrections.TopocentricCoordinates;
import com.example.test_spp.corrections.TropoCorrection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Test_ReadOBS {
    private static final String TAG = Test_ReadOBS.class.getSimpleName();
    private static final double L1_FREQUENCY = 1.57542e9;
    private static final double L5_FREQUENCY = 1.17645e9;
    private static final double MASK_ELEVATION = 20; // degrees
    private static final double SIGNAL_STRENGTH = 20;//信号强度


    public Coordinates RxPos;


    /**
     * Corrections which are to be applied to received pseudoranges
     */
    private ArrayList<Correction> corrections = new ArrayList<>();

    public Test_ReadNAV test_readNAV;

    public Test_ReadOBS() {


        test_readNAV = new Test_ReadNAV();
        test_readNAV.ReadNAV("C:\\Users\\ButterFlying\\AndroidStudioProjects\\Test_SPP\\app\\src\\test\\java\\com\\example\\test_spp\\test_522_ABPO00MDG_R_20201430100_01H_GN.rnx");

        /*增加误差项的计算*/
        corrections.add(new TropoCorrection());
        corrections.add(new ShapiroCorrection());
        corrections.add(new IonoCorrection());

    }


    public void ReadOBS(String filepath) {

        /*先读取广播星历文件，得到广播星历参数的列表集合。
         * 另外，下文中的代码是根据每读取一个观测历元的数据就开始计算卫星位置，卫星高度角，误差，以及平差！！！*/


        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        //StringBuilder stringBuilder = new StringBuilder();
        File file = new File(filepath);
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {


                    //读取测站近似位置
                    if (line.contains("APPROX POSITION XYZ")) {

                        double RxPosX = Double.parseDouble(line.substring(0, 14));
                        double RxPosY = Double.parseDouble(line.substring(15, 29));
                        double RxPosZ = Double.parseDouble(line.substring(30, 45));

                        RxPos = Coordinates.globalXYZInstance(RxPosX, RxPosY, RxPosZ);
                    }
                    /* 代表一个历元的开头  */
                    else if (line.startsWith(">")) {

                        String[] rs = line.split("\\s+");

                        int year = Integer.parseInt(rs[1]);
                        int month = Integer.parseInt(rs[2]);
                        int day = Integer.parseInt(rs[3]);
                        int hour = Integer.parseInt(rs[4]);
                        int minute = Integer.parseInt(rs[5]);
                        double second = Double.parseDouble(rs[6]);
                        int satnum = Integer.parseInt(rs[8]);

                        /*历元时刻*/
                        Time epochTime = new Time(year, month, day, hour, minute, second);

                        List<SatelliteParameters> GPSsatellites = new ArrayList<>();
                        for (int i = 0; i < satnum; i++) {
                            line = bufferedReader.readLine();
                            /*读取GPS卫星的数据*/
                            if (line.startsWith("G")) {

                                //String[] re = line.split("\\s+");

                                //int satId = Integer.parseInt(re[0].substring(re[0].length() - 2));
                                int satId = Integer.parseInt(line.substring(1, 3));

                                //处理个别卫星只有L5频率的数据，没有L1频率的数据
                                //注意字符串空值的处理----其实这里面也是空格不是完全的null或者("")

                                if (!line.substring(3, 17).equals("              ")) {
                                    SatelliteParameters satelliteParameters = new SatelliteParameters(epochTime,
                                            satId,
                                            new Pseudorange(Double.parseDouble(line.substring(3, 17)), 0.0));
                                    satelliteParameters.setUniqueSatId("G" + satId + "_L1");

                                    //System.out.println("uniquesatid:" + satelliteParameters.getUniqueSatId() + "--time:" + satelliteParameters.getrefTime().toString() + "----pseudorange:" + satelliteParameters.getPseudorange());

                                    satelliteParameters.setCarrierFrequency(L1_FREQUENCY);

                                    satelliteParameters.setPhase(Double.parseDouble(line.substring(19, 33)));

                                    satelliteParameters.setDoppler(Double.parseDouble(line.substring(35, 49)));

                                    satelliteParameters.setSignalStrength(Double.parseDouble(line.substring(51, 65)));
                                    satelliteParameters.setSatType(GNSSConstants.SYSTEM_GPS);
                                    /*这里还没有计算卫星的位置和三方面的误差*/
                                    GPSsatellites.add(satelliteParameters);

                                }

                                /*代表有L5频率*/
                                if (line.length() > 65) {
                                    SatelliteParameters satelliteParametersL5 = new SatelliteParameters(epochTime,
                                            satId,
                                            new Pseudorange(Double.parseDouble(line.substring(67, 81)), 0.0));

                                    satelliteParametersL5.setUniqueSatId("G" + satId + "_L5");

                                    satelliteParametersL5.setCarrierFrequency(L5_FREQUENCY);

                                    satelliteParametersL5.setPhase(Double.parseDouble(line.substring(83, 97)));

                                    satelliteParametersL5.setDoppler(Double.parseDouble(line.substring(99, 113)));

                                    satelliteParametersL5.setSignalStrength(Double.parseDouble(line.substring(115, 129)));

                                    satelliteParametersL5.setSatType(GNSSConstants.SYSTEM_GPS);

                                    GPSsatellites.add(satelliteParametersL5);

                                    //System.out.println("uniquesatid:" + satelliteParametersL5.getUniqueSatId() + "--time:" + satelliteParametersL5.getrefTime().getMsec() + "----pseudorange:" + satelliteParametersL5.getPseudorange());

                                }

                            }

                        }

                        //高度角、卫星位置、信号强度筛选之后的卫星列表
                        GPSsatellites = calculateSatPosition(GPSsatellites, RxPos);

                    }

                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 计算卫星的位置和各方面的误差，以及通过高度角和卫星信号的强度删去弱卫星，从而筛选出可以进行平差的卫星列表
     * 对一个历元的卫星数据来说
     *
     * @param GPSsatellites
     * @param position      接收机的近似位置
     */
    public List<SatelliteParameters> calculateSatPosition(List<SatelliteParameters> GPSsatellites, Coordinates position) {

        // Make a list to hold the satellites that are to be excluded based on elevation/CN0 masking criteria
        List<SatelliteParameters> excludedSatellites = new ArrayList<>();

        //接收机的位置，这里用接收机的位置主要是为了计算对流层延迟
        //RxPos = Coordinates.globalXYZInstance(position.getX(), position.getY(), position.getZ());


        for (SatelliteParameters sat : GPSsatellites) {

            SatellitePosition sp = test_readNAV.getSatPositionAndVelocities(sat.getrefTime().getMsec(),
                    sat.getPseudorange(), sat.getSatId(), sat.getSatType(),
                    test_readNAV.getEphGpsList(), 0.0);

            if (sp == null) {
                excludedSatellites.add(sat);
                continue;
            }
            sat.setSatellitePosition(sp);

            /*计算卫星的高度角*/
            sat.setRxTopo(new TopocentricCoordinates(RxPos, sat.getSatellitePosition()));

            if (sat.getRxTopo().getElevation() < MASK_ELEVATION) {
                excludedSatellites.add(sat);
                continue;
            }
            /*卫星的信号强度*/
            if (sat.getSignalStrength() < SIGNAL_STRENGTH) {
                excludedSatellites.add(sat);
                continue;
            }
            /*计算各方面的误差*/
            double accumulatedCorrection = 0.0;

            for (Correction correction : corrections) {

                correction.calculateCorrection(
                        sat.getrefTime(),
                        RxPos,
                        sat.getSatellitePosition(),
                        test_readNAV.getIonoParameters()
                );
                accumulatedCorrection += correction.getCorrection();
            }
            sat.setAccumulatedCorrection(accumulatedCorrection);
            System.out.println("sat:" + sat.getUniqueSatId() + "time---" + sat.getrefTime().toString() + "----position----X:" + sat.getSatellitePosition().getX() + "----ele----" + sat.getRxTopo().getElevation() + "---correction---" + sat.getAccumulatedCorrection());

        }

        GPSsatellites.removeAll(excludedSatellites);
        return GPSsatellites;
    }


}
