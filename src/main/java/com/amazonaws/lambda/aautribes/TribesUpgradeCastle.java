package com.amazonaws.lambda.aautribes;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

public class TribesUpgradeCastle implements RequestHandler<PlayerData, String> {

	private AmazonDynamoDB dynamoDb;
	private final String DATA_TABLE_NAME = "AAUTribesData";
	private final String LAST_UPDATES_TABLE_NAME = "AAUTribesLastUpdates";
	private final String PLAYER_NAME = "playerName";
	private final String EVENT_TIME = "eventTime";
	private final String BASE_SIZE = "baseSize";
	private final String FOOD_COUNT = "foodCount";
	private final String STONE_COUNT = "stoneCount";
	private final String WOOD_COUNT = "woodCount";
	
    @Override
    public String handleRequest(PlayerData input, Context context) {
        context.getLogger().log("Input: " + input);
        
        dynamoDb = AmazonDynamoDBClientBuilder.standard()
                .withRegion("eu-central-1")
                .build();
        
	    String lastUpdateTimestamp = getPlayersLastUpdateTimestamp(input.getPlayerName());
	    
	    if (lastUpdateTimestamp == null)
	    	return "ERROR: User not found in AAUTribesLastUpdates table!";
	    
	    Map<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
	    keyMap.put(PLAYER_NAME, new AttributeValue(input.getPlayerName()));
	    keyMap.put(EVENT_TIME, (new AttributeValue()).withN(lastUpdateTimestamp));
	    
	    GetItemRequest getItemRequest = new GetItemRequest()
	    		.withTableName(DATA_TABLE_NAME)
	    		.withKey(keyMap);
	    
	    GetItemResult result = dynamoDb.getItem(getItemRequest);
	    Map<String, AttributeValue> item = result.getItem();
	    
	    int foodCount = getNumberFromItem(item.get(FOOD_COUNT));
	    int stoneCount = getNumberFromItem(item.get(STONE_COUNT));
	    int woodCount = getNumberFromItem(item.get(WOOD_COUNT));
	    int baseSize = getNumberFromItem(item.get(BASE_SIZE));
	    
	    int requiredRessources = baseSize*baseSize*10;
	    
	    String errorReply = "";
	    boolean enoughRessources = true;
	    
	    if (foodCount < requiredRessources) {
	    	errorReply += "Player has too less food! foodCount: " + foodCount + " required: " + requiredRessources + "\n";
	    	enoughRessources = false;
	    }
	    if (stoneCount < requiredRessources) {
	    	errorReply += "Player has too less stones! stoneCount: " + stoneCount + " required: " + requiredRessources + "\n";
	    	enoughRessources = false;
	    }
	    if (woodCount < requiredRessources) {
	    	errorReply += "Player has too less wood! woodCount: " + woodCount + " required: " + requiredRessources;
	    	enoughRessources = false;
	    }
	    
	    if (!enoughRessources)
	    	return "ERROR: " + errorReply;
	    
	    baseSize++;	// increase castle size
	    
	    // reduce ressources
	    foodCount -= requiredRessources;
	    stoneCount -= requiredRessources;
	    woodCount -= requiredRessources;

	    AttributeValue updatedBaseSize = (new AttributeValue()).withN(String.valueOf(baseSize));
	    item.replace(BASE_SIZE, updatedBaseSize);

	    AttributeValue updatedFoodCount = (new AttributeValue()).withN(String.valueOf(foodCount));
	    item.replace(FOOD_COUNT, updatedFoodCount);
	    AttributeValue updatedStoneCount = (new AttributeValue()).withN(String.valueOf(stoneCount));
	    item.replace(STONE_COUNT, updatedStoneCount);
	    AttributeValue updatedWoodCount = (new AttributeValue()).withN(String.valueOf(woodCount));
	    item.replace(WOOD_COUNT, updatedWoodCount);
	    
	    long timeStamp = System.currentTimeMillis();
	    AttributeValue newTimeStamp = (new AttributeValue()).withN(String.valueOf(timeStamp));
	    item.replace(EVENT_TIME, newTimeStamp);
	    
	    PutItemRequest putItemRequest = new PutItemRequest(DATA_TABLE_NAME, item);
	    dynamoDb.putItem(putItemRequest);
	    
	    updatePlayersLastUpdateTimestamp(input.getPlayerName(), timeStamp);
	    
	    PlayerData newPlayer = new PlayerData(input.getPlayerName(), 0.0, 0.0, baseSize, foodCount, stoneCount, woodCount);
        Gson gson = new Gson();
        String playerString = gson.toJson(newPlayer);
        
        return playerString;
    }
    
    private int getNumberFromItem(AttributeValue value) {
    	return Integer.parseInt(value.getN());
    }
    
    private String getPlayersLastUpdateTimestamp(String playerName) {
    	Map<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
	    keyMap.put(PLAYER_NAME, new AttributeValue(playerName));
	    
	    GetItemRequest getItemRequest = new GetItemRequest()
	    		.withTableName(LAST_UPDATES_TABLE_NAME)
	    		.withKey(keyMap)
	    		.withProjectionExpression(EVENT_TIME);
	    
	    GetItemResult result = dynamoDb.getItem(getItemRequest);
	    
	    Map<String, AttributeValue> item = result.getItem();
	    
	    if (item == null)
	    	return null;
	    
	    AttributeValue value = item.get(EVENT_TIME);
	    
	    System.out.println(value.getN());
	    
	    return value.getN();
	}
    
    private boolean updatePlayersLastUpdateTimestamp(String playerName, long timeStamp) {
    	Map<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
	    keyMap.put(PLAYER_NAME, new AttributeValue(playerName));
	    keyMap.put(EVENT_TIME, (new AttributeValue()).withN(String.valueOf(timeStamp)));
	    
	    dynamoDb.putItem(LAST_UPDATES_TABLE_NAME, keyMap);
	    
	    return true;
	}

}
