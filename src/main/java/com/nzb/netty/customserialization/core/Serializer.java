package com.nzb.netty.customserialization.core;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class Serializer {
	private static final Charset CHARSET = Charset.forName("UTF-8");

	protected ChannelBuffer writeBuffer;

	protected ChannelBuffer readBuffer;

	protected abstract void read();

	protected abstract void write();

	public Serializer readFromBytes(byte[] bytes) {
		readBuffer = BufferFactory.getBuffer();
		read();
		readBuffer.clear();
		return this;
	}

	public void readFromBuffer(ChannelBuffer readBuffer) {
		this.readBuffer = readBuffer;
		read();
	}

	public ChannelBuffer writeToLocalBuffer() {
		writeBuffer = BufferFactory.getBuffer();
		write();
		return writeBuffer;
	}

	public ChannelBuffer WriteToTargetBuff(ChannelBuffer buffer) {
		writeBuffer = buffer;
		write();
		return writeBuffer;
	}

	public byte[] getBytes() {
		writeToLocalBuffer();
		byte[] bytes = null;
		if (writeBuffer.writerIndex() == 0) {
			bytes = new byte[0];
		} else {
			bytes = new byte[writeBuffer.writerIndex()];
			writeBuffer.readBytes(bytes);
		}
		readBuffer.clear();
		return bytes;
	}

	public byte readByte() {
		return readBuffer.readByte();
	}

	public short readShort() {
		return readBuffer.readShort();
	}

	public int readInt() {
		return readBuffer.readInt();
	}

	public long readLong() {
		return readBuffer.readLong();
	}

	public float readFloat() {
		return readBuffer.readFloat();
	}

	public double readDouble() {
		return readBuffer.readDouble();
	}

	public String readString() {
		int size = readBuffer.readShort();
		if (size <= 0) {
			return "";
		}
		byte[] bytes = new byte[size];
		readBuffer.readBytes(bytes);
		return new String(bytes, CHARSET);
	}

	public <T> List<T> readList(Class<T> clz) {
		List<T> list = new ArrayList<T>();
		int size = readBuffer.readShort();
		for (int i = 0; i < size; i++) {
			list.add(read(clz));
		}
		return list;
	}

	public <K, V> Map<K, V> readMap(Class<K> keyClz, Class<V> valueClz) {
		Map<K, V> map = new HashMap<K, V>();
		int size = readBuffer.readShort();
		for (int i = 0; i < size; i++) {
			K key = read(keyClz);
			V value = read(valueClz);
			map.put(key, value);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public <I> I read(Class<I> clz) {
		Object t = null;
		if (clz == int.class || clz == Integer.class) {
			t = this.readInt();
		} else if (clz == byte.class || clz == Byte.class) {
			t = this.readByte();
		} else if (clz == short.class || clz == Short.class) {
			t = this.readShort();
		} else if (clz == long.class || clz == Long.class) {
			t = this.readLong();
		} else if (clz == float.class || clz == Float.class) {
			t = this.readFloat();
		} else if (clz == double.class || clz == Double.class) {
			t = this.readDouble();
		} else if (clz == String.class) {
			t = this.readString();
		} else if (Serializer.class.isAssignableFrom(clz)) {
			try {
				byte hasOobject = this.readBuffer.readByte();
				if (hasOobject == 1) {
					Serializer temp;
					temp = (Serializer) clz.newInstance();
					temp.readFromBuffer(this.readBuffer);
					t = temp;
				} else {
					t = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException(String.format("not support type:[%s]", clz));
		}
		return (I) t;
	}

	public Serializer writeByte(Byte value) {
		writeBuffer.writeByte(value);
		return this;
	}

	public Serializer writeShort(Short value) {
		writeBuffer.writeShort(value);
		return this;
	}

	public Serializer writeInt(Integer value) {
		writeBuffer.writeInt(value);
		return this;
	}

	public Serializer writeLong(Long value) {
		writeBuffer.writeLong(value);
		return this;
	}

	public Serializer writeFloat(Float value) {
		writeBuffer.writeFloat(value);
		return this;
	}

	public Serializer writeDouble(Double value) {
		writeBuffer.writeDouble(value);
		return this;
	}

	public <T> Serializer writeList(List<T> list) {
		if (isEmpty(list)) {
			writeBuffer.writeShort((short) 0);
			return this;
		}
		writeBuffer.writeShort((short) list.size());
		for (T item : list) {
			writeObject(item);
		}
		return this;
	}

	public <K, V> Serializer writeMap(Map<K, V> map) {
		if (isEmpty(map)) {
			writeBuffer.writeShort((short) 0);
			return this;
		}
		writeBuffer.writeShort((short) map.size());
		for (Entry<K, V> entry : map.entrySet()) {
			writeObject(entry.getKey());
			writeObject(entry.getValue());
		}
		return this;
	}

	public Serializer writeString(String value) {
		if (value == null || value.isEmpty()) {
			writeShort((short) 0);
			return this;
		}
		byte[] data = value.getBytes();
		short len = (short) data.length;
		writeBuffer.writeShort(len);
		writeBuffer.writeBytes(data);
		return this;
	}

	public Serializer writeObject(Object object) {
		if (object == null) {
			writeByte((byte) 0);
		} else {
			if (object instanceof Integer) {
				writeInt((Integer) object);
				return this;
			}
			if (object instanceof Long) {
				writeLong((Long) object);
				return this;
			}
			if (object instanceof Short) {
				writeShort((Short) object);
				return this;
			}
			if (object instanceof Byte) {
				writeByte((Byte) object);
				return this;
			}
			if (object instanceof String) {
				writeString((String) object);
				return this;
			}
			if (object instanceof Serializer) {
				writeByte((byte) 1);
				Serializer value = (Serializer) object;
				value.WriteToTargetBuff(writeBuffer);
				return this;
			}

			throw new RuntimeException("can not serializer type: " + object.getClass());
		}
		return this;
	}

	private <T> boolean isEmpty(Collection<T> c) {
		return c == null || c.size() == 0;
	}

	private <K, V> boolean isEmpty(Map<K, V> c) {
		return c == null || c.size() == 0;
	}
}
