package com.ruizhou.blueterminal;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void bytes_to_hex_test1() {
        String expected = "AAAAA";
        byte[] bytes = expected.getBytes(); // Input to function
        expected = "4141414141";

        String result = bytes_to_hex(bytes);

        assertEquals(expected, result);
    }

    @Test
    public void bytes_to_hex_test2() {
        String expected = "Hello World!";
        byte[] bytes = expected.getBytes(); // Input to function
        expected = "48656c6c6f20576f726c6421";

        String result = bytes_to_hex(bytes);

        assertEquals(expected, result);
    }

    @Test
    public void decode_bytes_to_ASCII_test1() {
        String expected = "Hello World!";
        byte[] bytes = expected.getBytes(); // Input to function

        String result = decode(bytes);

        assertEquals(expected, result);
    }

    @Test
    public void decode_bytes_to_ASCII_test2() {
        String expected = "14, 256.7, 90";
        byte[] bytes = expected.getBytes(); // Input to function

        String result = decode(bytes);

        assertEquals(expected, result);
    }

    @Test
    public void encode_ASCII_to_bytes_short_test() {
        String expected = "Hello World!"; // Input
        byte[] bytes = expected.getBytes();

        byte[] result = encode(expected);

        assertEquals(bytes[0], result[0]);
    }

    @Test
    public void encode_ASCII_to_bytes_long_test() {
        String expected = "#DUMP:Sensor1_Mar-20-2021.txt:16748#"; // Input
        byte[] bytes = expected.getBytes();

        byte[] result = encode(expected);
        // Compare bytes one by one
        for (int i = 0; i < result.length; i++) {
            assertEquals(bytes[i], result[i]);
        }
    }





    // Actual functions we are testing

    public String bytes_to_hex(byte[] bytes) {
        String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {

            sb.append(HEX.charAt((b >> 4) & 0x0f));
            sb.append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }

    public String decode(byte[] bytes) {
        String hex = bytes_to_hex(bytes);

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i = i + 2) {
            String s = hex.substring(i, i + 2);
            int n = Integer.valueOf(s, 16);
            output.append((char)n);
        }

        return output.toString();
    }

    public byte[] encode(String content) {
        byte[] data = content.getBytes();
        if (data.length>20){//Data greater than 20 bytes
            int num=0;
            if (data.length%20!=0){
                num=data.length/20+1;
            }else{
                num=data.length/20;
            }
            for (int i=0;i<num;i++){
                byte[] tempArr;
                if (i==num-1){
                    tempArr=new byte[data.length-i*20];
                    System.arraycopy(data,i*20,tempArr,0,data.length-i*20);
                }else{
                    tempArr=new byte[20];
                    System.arraycopy(data,i*20,tempArr,0,20);
                }
                return tempArr;
            }
        }

        return data;

    }
}