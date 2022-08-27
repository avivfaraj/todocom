// net packages
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

// Lang Package
import java.lang.StringBuffer;

// IO Packages
import java.io.BufferedReader;
import java.io.InputStreamReader;

// Exceptions packages
import java.io.IOException;
import java.lang.StringIndexOutOfBoundsException;
/***************************************
 * FoodSearch.java
 * Represents a Food item with nutrients
 * @author Aviv Farag
 * @version 6.0 - 06.10.21
 ****************************************/

public class FoodSearch {

	// Variables Declaration
	private String api_key, query, response, api_url, brand_owner;
	private String[] nutrient_id = {"1008","1003","1004","1005","2000"};
	private String[] nutrient_name =  {"Grams","Calories","Protein","Fat","Carbohydrates","Sugars"}; 
	private double[] nutrient_value;

	public FoodSearch(){
		// Initialize variables
		setKey();
		query = "";
		response = "";
		api_url = "";
		brand_owner = "";

		// Initialize nutrient value to: {100,0,0,0,0,0}
		nutrient_value = new double[6];
		for (int index = 0 ; index < nutrient_value.length ; index++)
		{
			if (index == 0) nutrient_value[index] = 100;
			if (index > 0) nutrient_value[index] = 0;
		}
	}

	
	// Setters
	public void setKey()
	{
		api_key = "g6jZti35lOsp8A6Jg0gSfeAWvbtdfyEh9Cfp0Cht";
	}
	public void setQuery(String query, String brand_owner){

		// Set nutrient values to default {100, 0,0,0,0,0}
		for (int index = 0 ; index < nutrient_value.length; index++)
		{
			if (index == 0) nutrient_value[index] = 100;
			if (index > 0) nutrient_value[index] = 0;
		}
		this.query = query;
		this.brand_owner = brand_owner;
		api_url = "https://api.nal.usda.gov/fdc/v1/foods/search?query="+this.query+"&brandOwner="+this.brand_owner+"&api_key="+api_key;
	}

	// Getters
	public String getQuery(){
		
		return query+","+brand_owner;
	}

	public String getRequest() throws IOException
	{
		
		try{

		// Citing Code
		/************************************************************
		*    Title: Do a Simple HTTP Request in Java
		*    Author: baeldung
		*    Date: January 30, 2021
		*    Code version: Not available
		*    Availability: https://www.baeldung.com/java-http-request
		*************************************************************/

			// URL for API request. Contains key and base (USD)
			URL url = new URL(api_url);

			// Open connection
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// Set method to GET
			con.setRequestMethod("GET");

			// Response Code 
			int status = con.getResponseCode();
			// System.out.print(status+"\n");

			// Ensure got a repsonse.
			// To learn more about error codes go to API docs:
			if (status == 200)
			{
				// Reading response using an input stream
				BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));

				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}

				// Closing buffer
				in.close();

				// Disconnecting from api
				con.disconnect();

				// Update private variables.
				// System.out.println(content.toString());
				response = content.toString();

			}
			else
			{
				response = "Error: Code is not 200\n";
			}
			

		}catch(UnknownHostException uhe)
		{
			// No internet Error
			response = "Error: ** No internet connection **\n";

		}
		finally
		{
			return response;
		}
		
	}

	public Food searchFood(String query, String brand_owner) 
	{
		setQuery(query, brand_owner);
		try{
			String response = getRequest();
		}catch(IOException exe)
		{
			System.out.println("Something went wrong. Please try again later!");
			return null;
		}
		

		// System.out.println(response);
		if (!response.isEmpty() && !response.contains("Error"))
		{
			// Get description
			// Get indexes 
			int des_index = response.indexOf("description");
			if (des_index != -1)
			{
				int last_index = response.indexOf(",",des_index);

				// Actual Description
				String description = response.substring(des_index+14, last_index-1);

				// Got response, but it is not the item I was looking for
				if (!description.toLowerCase().contains(query.toLowerCase())) return null;

				// Remove comma if string contains it.
				if (description.contains(",")) description = description.replace(",","");

				// Get Brand Owner
				// Get indexes
				des_index = response.indexOf("publishedDate");
				des_index = response.indexOf("brandOwner",des_index);
				last_index = response.indexOf(",\"",des_index);

				// Brand Owner
				String brandOwner = response.substring(des_index+13, last_index-1);

				// Remove comma if string contains it.
				if (brandOwner.contains(",")) brandOwner = brandOwner.replace(",","");

				// Get Brand Owner
				// Get indexes
				des_index = response.indexOf("foodCategory");
				last_index = response.indexOf(",",des_index);

				// Brand Owner
				String foodCategory = response.substring(des_index+15, last_index-1);

				// Remove comma if string contains it.
				if (foodCategory.contains(",")) foodCategory = foodCategory.replace(",","");

				// Food nutrients
				// Get indexes
				des_index = response.indexOf("foodNutrients");
				last_index = response.indexOf("]",des_index);

				// Food nutrients in a String
				String nutrients_str = response.substring(des_index, last_index);

				double value = 0.0;
				// Iterate over nutrient_id in order to get the nutrients from string
				for (int counter = 0; counter < nutrient_id.length; counter++)
		      	{

		      		try{
		      			int id_index = nutrients_str.indexOf("nutrientId\":"+nutrient_id[counter]);
			         	int last_id_index = nutrients_str.indexOf("}",id_index);
			         	String[] nutrient = (nutrients_str.substring(id_index,last_id_index)).split(",");
			         	value = Double.parseDouble(nutrient[nutrient.length-1].split(":")[1]);
		      		}
		      		catch(StringIndexOutOfBoundsException exe)
		      		{
		      			value = 0.0;
		      		}

		         nutrient_value[counter+1] = value;
		       	}


				return new Food(description,
								foodCategory,
								brandOwner,
								nutrient_value[0],
								nutrient_value[1],
								nutrient_value[2],
								nutrient_value[3],
								nutrient_value[4],
								nutrient_value[5]);
			}
		}

		return null;
		
	}

}
