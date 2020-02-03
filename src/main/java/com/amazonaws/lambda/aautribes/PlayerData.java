package com.amazonaws.lambda.aautribes;

public class PlayerData {
	private String playerName = null;
	private double baseLatitude = -1;
	private double baseLongitude = -1;
	private int baseSize = -1;
	private int foodCount = 0;
	private int stoneCount = 0;
	private int woodCount = 0;

	public PlayerData() {
		
	}
	public PlayerData(String name, double lat, double lon, int base, int food, int stone, int wood) {
		playerName = name;
		baseLatitude = lat;
		baseLongitude = lon;
		baseSize = base;
		foodCount = food;
		stoneCount = stone;
		woodCount = wood;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public double getBaseLatitude() {
		return baseLatitude;
	}
	public void setBaseLatitude(double baseLatitude) {
		this.baseLatitude = baseLatitude;
	}
	public double getBaseLongitude() {
		return baseLongitude;
	}
	public void setBaseLongitude(double baseLongitude) {
		this.baseLongitude = baseLongitude;
	}
	public int getBaseSize() {
		return baseSize;
	}
	public void setBaseSize(int baseSize) {
		this.baseSize = baseSize;
	}
	public String toString() {
		return playerName + " " + baseLatitude + " " + baseLongitude;
	}
	public int getFoodCount() {
		return foodCount;
	}
	public void setFoodCount(int foodCount) {
		this.foodCount = foodCount;
	}
	public int getStoneCount() {
		return stoneCount;
	}
	public void setStoneCount(int stoneCount) {
		this.stoneCount = stoneCount;
	}
	public int getWoodCount() {
		return woodCount;
	}
	public void setWoodCount(int woodCount) {
		this.woodCount = woodCount;
	}

}
