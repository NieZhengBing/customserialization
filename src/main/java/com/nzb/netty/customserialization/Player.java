package com.nzb.netty.customserialization;

import java.util.ArrayList;
import java.util.List;

import com.nzb.netty.customserialization.core.Serializer;

public class Player extends Serializer {

	private long playerId;

	private int age;

	private List<Integer> skills = new ArrayList<Integer>();

	private Resource resource = new Resource();

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<Integer> getSkills() {
		return skills;
	}

	public void setSkills(List<Integer> skills) {
		this.skills = skills;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	protected void read() {
		this.playerId = readLong();
		this.age = readInt();
		this.skills = readList(Integer.class);
		this.resource = read(Resource.class);
	}

	@Override
	protected void write() {
		writeLong(playerId);
		writeInt(age);
		writeList(skills);
		writeObject(resource);
	}

}
