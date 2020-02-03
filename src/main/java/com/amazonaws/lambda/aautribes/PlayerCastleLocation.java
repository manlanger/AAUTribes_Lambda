package com.amazonaws.lambda.aautribes;

public class PlayerCastleLocation {
	private String playerName = null;
	private double baseLatitude = -1;
	private double baseLongitude = -1;
	
	public PlayerCastleLocation() {
		
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
}
