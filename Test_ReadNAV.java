package com.example.test_spp;

import android.util.Log;

import com.example.test_spp.coord.SatellitePosition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Test_ReadNAV extends EphemerisSystem{
    private static final String TAG = Test_ReadNAV.class.getSimpleName();


    /*存放广播星历参数*/
    public List<EphGps> ephGpsList = new ArrayList<>();

    /*存放电离层参数*/

    public IonoParameters ionoParameters=new IonoParameters();

    public IonoParameters getIonoParameters() {
        return ionoParameters;
    }

    public void setIonoParameters(IonoParameters ionoParameters) {
        this.ionoParameters = ionoParameters;
    }

    public List<EphGps> getEphGpsList() {
        return ephGpsList;
    }

    public void setEphGpsList(List<EphGps> ephGpsList) {
        this.ephGpsList = ephGpsList;
    }

    public void ReadNAV(String filepath) {
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        File file = new File(filepath);

        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                /*记录是否已经把头文件读取完了*/
                int flag = 0;
                while ((line = bufferedReader.readLine()) != null) {

                    if(line.startsWith("GPSA") && flag==0)
                    {
                        ionoParameters.setA0(Double.parseDouble(line.substring(5,17)));

                        ionoParameters.setA1(Double.parseDouble(line.substring(17,29)));

                        ionoParameters.setA2(Double.parseDouble(line.substring(29,41)));

                        ionoParameters.setA3(Double.parseDouble(line.substring(41,53)));
                    }
                    if(line.startsWith("GPSB")  &&  flag==0)
                    {
                        ionoParameters.setB0(Double.parseDouble(line.substring(5,17)));

                        ionoParameters.setB1(Double.parseDouble(line.substring(17,29)));

                        ionoParameters.setB2(Double.parseDouble(line.substring(29,41)));

                        ionoParameters.setB3(Double.parseDouble(line.substring(41,53)));

                    }

                    if (line.trim().equals("END OF HEADER")) {
                        flag = 1;
                        System.out.println("开始读取广播星历参数");
                    }
                    /*start to read main of NAV*/
                    if (line.startsWith("G") && flag == 1) {
                        EphGps ephGps = new EphGps();

                        /*卫星prn*/



                        int satid = Integer.parseInt(line.substring(1, 3));

                        ephGps.setSatType(GNSSConstants.SYSTEM_GPS);
                        ephGps.setSatID(satid);

                        int year = Integer.parseInt(line.substring(4, 8));

                        int month = Integer.parseInt(line.substring(9, 11));

                        int day = Integer.parseInt(line.substring(12, 14));

                        int hour = Integer.parseInt(line.substring(15, 17));

                        int minute = Integer.parseInt(line.substring(18, 20));

                        double second = Double.parseDouble(line.substring(21, 23));

                        Time reftime = new Time(year, month, day, hour, minute, second);

                        ephGps.setRefTime(reftime);

                        System.out.println(reftime.toString());

                        double af0 = Double.parseDouble(line.substring(23, 42));
                        double af1 = Double.parseDouble(line.substring(42, 61));
                        double af2 = Double.parseDouble(line.substring(61, 80));


                        ephGps.setAf0(af0);
                        ephGps.setAf1(af1);
                        ephGps.setAf2(af2);


                        line = bufferedReader.readLine();
                        //广播轨道1参数
                        int iode = (int) Double.parseDouble(line.substring(4, 23));
                        double Crs = Double.parseDouble(line.substring(23, 42));
                        double delta_n = Double.parseDouble(line.substring(42, 61));
                        double M0 = Double.parseDouble(line.substring(61, 80));

                        ephGps.setIode(iode);
                        ephGps.setCrs(Crs);
                        ephGps.setDeltaN(delta_n);
                        ephGps.setM0(M0);

                        line = bufferedReader.readLine();
                        //广播轨道2参数 Cuc, es, Cus, sqrtA
                        double Cuc = Double.parseDouble(line.substring(4, 23));
                        double es = Double.parseDouble(line.substring(23, 42));
                        double Cus = Double.parseDouble(line.substring(42, 61));
                        double sqrtA = Double.parseDouble(line.substring(61, 80));

                        ephGps.setCuc(Cuc);
                        ephGps.setE(es);
                        ephGps.setCus(Cus);
                        ephGps.setRootA(sqrtA);

                        line = bufferedReader.readLine();
                        //广播轨道3参数 Toe, Cic, Omega_0, Cis
                        double  Toe =  Double.parseDouble(line.substring(4, 23));
                        double Cic = Double.parseDouble(line.substring(23, 42));
                        double Omega_0 = Double.parseDouble(line.substring(42, 61));
                        double Cis = Double.parseDouble(line.substring(61, 80));

                        ephGps.setToe(Toe);
                        ephGps.setCic(Cic);
                        ephGps.setOmega0(Omega_0);
                        ephGps.setCis(Cis);

                        line = bufferedReader.readLine();
                        //广播轨道4参数 i0, Crc, w, Omega_dot
                        double  i0 =  Double.parseDouble(line.substring(4, 23));
                        double Crc = Double.parseDouble(line.substring(23, 42));
                        double w = Double.parseDouble(line.substring(42, 61));
                        double Omega_dot = Double.parseDouble(line.substring(61, 80));

                        ephGps.setI0(i0);
                        ephGps.setCrc(Crc);
                        ephGps.setOmg(w);
                        ephGps.setOmegaDot(Omega_dot);

                        line = bufferedReader.readLine();
                        //广播轨道5参数 i_dot, L2code, gpsweek, L2Flag
                        double  i_dot =  Double.parseDouble(line.substring(4, 23));
                        int  L2code = (int) Double.parseDouble(line.substring(23, 42));
                        int  gpsweek = (int) Double.parseDouble(line.substring(42, 61));
                        int  L2Flag = (int) Double.parseDouble(line.substring(61, 80));

                        ephGps.setiDot(i_dot);
                        ephGps.setL2Code(L2code);
                        ephGps.setWeek(gpsweek);
                        ephGps.setL2Flag(L2Flag);

                        line = bufferedReader.readLine();
                        //广播轨道6参数svAccur, svHealth, TGD, iodc
                        int   svAccur =  (int) Double.parseDouble(line.substring(4, 23));
                        int  svHealth = (int) Double.parseDouble(line.substring(23, 42));
                        double  TGD = Double.parseDouble(line.substring(42, 61));
                        int  iodc = (int) Double.parseDouble(line.substring(61, 80));

                        ephGps.setSvAccur(svAccur);
                        ephGps.setSvHealth(L2code);
                        ephGps.setTgd(TGD);
                        ephGps.setIodc(iodc);



                        line = bufferedReader.readLine();
                        //广播轨道7参数
                        int  fitint = (int) Double.parseDouble(line.substring(23, 42));

                        ephGps.setFitInt(fitint);



                        ephGpsList.add(ephGps);
                    }


                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 在读取观测文件的时候，需要利用卫星的广播星历计算卫星的位置，所以得先找到卫星的广播星历
     * @param refTime_Mesc
     * @param satID
     * @param satType
     * @param ephGpsList
     * @return
     */

    public EphGps findEphGps(long refTime_Mesc,int satID,int satType,List<EphGps> ephGpsList) {
        long dt = 0;
        long dtMin = 0;
        long dtMax = 0;
        long delta = 0;
        EphGps refEph = null;

        for (int i = 0; i < ephGpsList.size(); i++) {
            // Find ephemeris sets for given satellite
            if (ephGpsList.get(i).getSatID() == satID && ephGpsList.get(i).getSatType() == satType) {


                // Compare current time and ephemeris reference time
                dt = Math.abs(ephGpsList.get(i).getRefTime().getMsec() - refTime_Mesc /*getGpsTime() - gpsTime*/) / 1000;

                // If it's the first round, set the minimum time difference and
                // select the first ephemeris set candidate; if the current ephemeris set
                // is closer in time than the previous candidate, select new candidate
                if (refEph == null || dt < dtMin) {
                    dtMin = dt;
                    refEph = ephGpsList.get(i);
                }
            }
        }

        if (refEph == null)
            return null;


        //maximum allowed interval from ephemeris reference time
        //fit interval  是拟合区间，是这个时刻的广播星历所适用的时间段  fitint*3600/2
        //如果没有给的话，拟合区间就为0
        long fitInterval = refEph.getFitInt();

        if (fitInterval != 0) {
            dtMax = fitInterval * 3600 / 2;
        }
        else {
            switch (refEph.getSatType()) {
                case GNSSConstants.SYSTEM_GLONASS:
                    dtMax = 950;
                case GNSSConstants.SYSTEM_QZSS:
                    dtMax = 3600;
                default:
                    dtMax = 7200;
            }
        }
            if (dtMin > dtMax) {
                refEph = null;
            }


            return refEph;
        }



    public SatellitePosition getSatPositionAndVelocities(long unixTime, double range, int satID, int satType,List<EphGps> ephGpsList, double receiverClockError)
    {

        EphGps ephGps=findEphGps(unixTime,satID,satType,ephGpsList);




        //Log.d(TAG,"getSatPositionAndVelocities  "+ephGps.getRefTime().toString());


        if(ephGps==null)
        {
            //Log.d(TAG, "未找到此卫星的广播星历"+satType+satID);
            return null;
        }
        //System.out.println("sat:"+satID+"找到的广播星历为："+ephGps.getRefTime().getMsec()+"此卫星伪距观测的时刻为："+unixTime);

        SatellitePosition satellitePosition=computeSatPositionAndVelocities(unixTime,range,satID,satType, ephGps,receiverClockError);

        if(satellitePosition==null)
        {
            return null;
        }
        return satellitePosition;

    }





}
