package com.example.test_spp;

import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
//        Time time=new Time(2020,11,2,10,56,20.5);
//        long ms=time.getMsec();
//
//        int is=time.isLeapYear(2020);
//        int day=time.getDayofYear(2020,11,2);
//
//        System.out.println(ms+"年纪日"+day+"runnian"+is);


        /*测试读文件是不是对的了*/

        Test_ReadOBS read=new Test_ReadOBS();

        read.ReadOBS("C:\\Users\\ButterFlying\\AndroidStudioProjects\\Test_SPP\\app\\src\\test\\java\\com\\example\\test_spp\\test_522_OB202005220849401.20o");

        /*测试字符串的截取怎么进行   substring*/
//        String s="asfabghkbhlahgag546a948ghh4";
//
//        String ss=s.substring(0,2);
//        System.out.println(ss);

        /*测试广播星历文件是否读取正确*/

//        Test_ReadNAV readNAV=new Test_ReadNAV();
//        readNAV.ReadNAV("C:\\Users\\ButterFlying\\AndroidStudioProjects\\Test_SPP\\app\\src\\test\\java\\com\\example\\test_spp\\test_522_ABPO00MDG_R_20201430100_01H_GN.rnx");



    }

}