package com.nzb.netty.customserialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Test1 {

	public static void main(String[] args) throws IOException {
		int id = 101;
		int age = 21;

		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		arrayOutputStream.write(int2byte(id));
		arrayOutputStream.write(int2byte(age));
		byte[] byteArray = arrayOutputStream.toByteArray();

		System.out.println(Arrays.toString(byteArray));
		byte[] ageBytes = new byte[4];
		arrayOutputStream.write(ageBytes);

		System.out.println("age: " + byte2int(ageBytes));
	}

	public static byte[] int2byte(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (i >> 3 * 8);
		bytes[1] = (byte) (i >> 2 * 8);
		bytes[2] = (byte) (i >> 1 * 8);
		bytes[3] = (byte) (i >> 0 * 8);
		return bytes;
	}

	public static int byte2int(byte[] bytes) {
		return (bytes[0] << 3 * 8) | (bytes[1] << 2 * 8) | (bytes[2] << 1 * 8) | (bytes[3] << 0 * 8);
	}

}
