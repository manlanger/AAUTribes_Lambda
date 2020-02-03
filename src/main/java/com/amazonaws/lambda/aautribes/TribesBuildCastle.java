package com.amazonaws.lambda.aautribes;

import java.util.HashMap;
import com.google.gson.*;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class TribesBuildCastle implements RequestHandler<PlayerData, String> {
	
	private AmazonDynamoDB dynamoDb;
	private final String DATA_TABLE_NAME = "AAUTribesData";
	private final String LAST_UPDATES_TABLE_NAME = "AAUTribesLastUpdates";
	private final String CASTLE_LOCATION_TABLE_NAME = "AAUTribesCastleLocations";
	private final String PLAYER_NAME = "playerName";
	private final String TIME_STAMP = "eventTime";
	
    @Override
    public String handleRequest(PlayerData input, Context context) {
    	context.getLogger().log("Input: " + input);
    	
    	if (input == null)
    		return "ERROR: Invalid input data!";
    	
    	String playerName = input.getPlayerName();
        
        dynamoDb = AmazonDynamoDBClientBuilder.standard()
                .withRegion("eu-central-1")
                .build();
        
        boolean playerExists = checkPlayerAlreadyInTable(playerName);
        
        if (playerExists)
        	return "ERROR: Player " + playerName + " has already a castle!"; 
        
        long timeStamp = System.currentTimeMillis();
        
        Map<String, AttributeValue> item;
        
        item = insertNewPlayerIntoDataTable(playerName, timeStamp);
        insertLastUpdateEntry(playerName, timeStamp);
        insertPlayerCastleLocation(input);
        
        PlayerData newPlayer = new PlayerData(playerName, input.getBaseLatitude(), input.getBaseLongitude(), 1, 0, 0, 0);
        Gson gson = new Gson();
        String playerString = gson.toJson(newPlayer);
        
        return playerString;
    }
    
    private Map<String, AttributeValue> insertNewPlayerIntoDataTable(String playerName, long timeStamp) {
    	Map<String, AttributeValue> item = createNewPlayerDataItem(playerName, 1, 0, 0, 0, timeStamp);
        PutItemRequest putItemRequest = new PutItemRequest(DATA_TABLE_NAME, item);
        dynamoDb.putItem(putItemRequest);
        
        return item;
    }
    
    private void insertLastUpdateEntry(String playerName, long timeStamp) {
    	Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
    	item.put(PLAYER_NAME, new AttributeValue(playerName));
    	
    	String timeStampString = String.valueOf(timeStamp);
    	
    	item.put(TIME_STAMP, (new AttributeValue()).withN(timeStampString));
    	
		PutItemRequest putItemRequest = new PutItemRequest(LAST_UPDATES_TABLE_NAME, item);
		dynamoDb.putItem(putItemRequest);
	}
    
    private void insertPlayerCastleLocation(PlayerData playerData) {
    	Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        
    	item.put(PLAYER_NAME, new AttributeValue(playerData.getPlayerName()));
        item.put("baseLatitude", new AttributeValue().withN(Double.toString(playerData.getBaseLatitude())));
        item.put("baseLongitude", new AttributeValue().withN(Double.toString(playerData.getBaseLongitude())));
        
        PutItemRequest putItemRequest = new PutItemRequest(CASTLE_LOCATION_TABLE_NAME, item);
		dynamoDb.putItem(putItemRequest);
    }

	private boolean checkPlayerAlreadyInTable(String playerName) {
    	Map<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
        keyMap.put(PLAYER_NAME, new AttributeValue(playerName));
        
        GetItemRequest getItemRequest = new GetItemRequest()
        		.withTableName(LAST_UPDATES_TABLE_NAME)
        		.withKey(keyMap)
        		.withProjectionExpression(PLAYER_NAME);
        
        GetItemResult result = dynamoDb.getItem(getItemRequest);
        
        Map<String, AttributeValue> item = result.getItem();
        
        if (item == null)
        	return false;
        
        AttributeValue value = item.get(PLAYER_NAME);
        
        return value.getS().equals(playerName);
	}

	private Map<String, AttributeValue> createNewPlayerDataItem(String playerName, int baseSize, int woodCount, int stoneCount, int foodCount, long timeStamp) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(PLAYER_NAME, new AttributeValue(playerName));
        item.put(TIME_STAMP, new AttributeValue().withN(Long.toString(timeStamp)));
        item.put("baseSize", new AttributeValue().withN(Integer.toString(baseSize)));
        item.put("woodCount", new AttributeValue().withN(Integer.toString(woodCount)));
        item.put("stoneCount", new AttributeValue().withN(Integer.toString(stoneCount)));
        item.put("foodCount", new AttributeValue().withN(Integer.toString(foodCount)));
        return item;
    }

}
