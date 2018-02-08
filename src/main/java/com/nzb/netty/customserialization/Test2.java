package com.nzb.netty.customserialization;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Test2 {
	public static void main(String[] args) {
		int id = 101;
		int age = 21;
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putInt(101);
		buffer.putInt(21);
		byte[] array = buffer.array();
		System.out.println(Arrays.toString(buffer.array()));

		ByteBuffer buffer2 = ByteBuffer.wrap(array);
		System.out.println("id: " + buffer2.getInt());
		System.out.println("age: " + buffer2.getInt());
	}

}
