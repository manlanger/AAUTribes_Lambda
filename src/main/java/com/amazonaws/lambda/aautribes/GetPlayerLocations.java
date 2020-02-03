package com.amazonaws.lambda.aautribes;

import java.util.ArrayList;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

public class GetPlayerLocations implements RequestHandler<Object, String> {

	private AmazonDynamoDB dynamoDb;
	private final String CASTLE_LOCATION_TABLE_NAME = "AAUTribesCastleLocations";
	
    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

        dynamoDb = AmazonDynamoDBClientBuilder.standard()
                .withRegion("eu-central-1")
                .build();
        
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(CASTLE_LOCATION_TABLE_NAME);
            
        ScanResult result = dynamoDb.scan(scanRequest);
        
        ArrayList<PlayerCastleLocation> locs = new ArrayList<PlayerCastleLocation>();
            
        for (Map<String, AttributeValue> item : result.getItems()){
        	PlayerCastleLocation loc = new PlayerCastleLocation();
        	
            loc.setPlayerName(item.get("playerName").getS());
            loc.setBaseLongitude(Double.parseDouble(item.get("baseLongitude").getN()));
            loc.setBaseLatitude(Double.parseDouble(item.get("baseLatitude").getN()));
            
            locs.add(loc);
        }
        
        Gson gson = new Gson();
        
        return gson.toJson(locs);
    }

}
