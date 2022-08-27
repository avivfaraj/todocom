// Util packages
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;

// File package
import java.io.File;

// Exceptions packages
import java.io.FileNotFoundException;
import java.io.IOException;

// File writer package
import java.io.FileWriter;



/***************************************
 * HealthTracker.java
 * Tracks the food you eat everyday
 * Tracks workouts done everyday 
 * Connects to USDA API in order to
 * get food nutrients
 * @author Aviv Farag
 * @version 6.0 - 06.10.21
 ****************************************/

public class HealthTracker {

  private static String str_pattern = "[a-zA-Z\\'\\- ]+";
  private static String date_pattern = "\\d{1,2}\\-\\d{1,2}\\-\\d{4}";
  private static Calendar today = Calendar.getInstance();;

  // ******************************* Useful Methods **********************************
  // Write to file method 
  public static void writeFile(File file, String str, boolean append_line)
  {

    try{
      /* FileWriter object to write to a file.
         If append_line is true the line will be added in the end
         Otherwise a new file will be written and override an existing one
      */
      FileWriter writer = new FileWriter(file,append_line);
      
      // Write string to the file
      writer.write(str);

      // Close writer
      writer.close();

    }catch(IOException exe){
      System.out.println("Something went wrong writing to file.\nPlease try again later.");
    }
    
  }

  // Reads a file and return lines as ArrayList<String>
  public static ArrayList<String> readFile(File file) throws FileNotFoundException
  {
    // Decalre ArrayList
    ArrayList<String> lines = new ArrayList<String>();
    String line = "";

    // Scanner object to read a file
      Scanner reader = new Scanner(file);

    /* Iterate over lines
       Scanner is iterator which means
       It has a method hasNextLine() that will
       return the sentinal value for that loop
    */
    while(reader.hasNextLine())
    {
       // Reading line
      line = reader.nextLine();

      // Add this line to ArrayList
      lines.add(line);
    }

    // Close Scanner
    reader.close();

    // Return ArrayList
    return lines;

  }

  public static boolean createNewFile(File file, String headers) {
    // Check wether file exists
    if (!file.exists())
    {
      // Write new file
      writeFile(file, headers, false);
      return true;
    }

    // File already exists
    return false;
  }

  // To-do soon: bla bla
  public static void insertToFile(Calendar current_date, File file, String line_to_insert, ArrayList<String> arr){
    
    // Initialize variables
    int counter = 0;
    Calendar another_date = Calendar.getInstance();
    int next = -1;
    String[] line_attr, date_attr;
    String toFile = "";

    if (arr.size() == 1)
      next = -1;
    else
    {
      // Next index
      next = -1;
      
      // Maximum and minimum range
      int max = arr.size(), min = 1; // Ignore headers
      
      // Element found or not
      boolean found = false;

      // Iterate if not found
      // Binary Search
      while (!found)
      {
          // Next index 
          next = Math.round((max + min )/2);

           // Line attributes splitting by comma
          line_attr = arr.get(next).split(",");

          // If item does not exist
          if (next == max || next == min) 
          {
            Calendar temp =  stringToDate(line_attr[1]);
            // line_attr = arr.get(next).split(",");
            if(!line_attr[1].equals("date") && temp != null && temp.before(current_date)) next += 1;
              // Stop loop
              break;
          }
          // Item exists
          else
          {
            another_date = stringToDate(line_attr[1]);
            
            // Ensure element is greater than look_for
            if (another_date.after(current_date))
                // define new maximum index
                max = next;

            // Ensure element is smaller than look_for 
            else if (another_date.before(current_date))
                // Define new minimum index
                min = next;

            // Ensure element is equal to look_for
            else
                // Stop iteration
                found = true;
            
            
          }
      }
    }

    // Append line
    if (next == -1 || next >= arr.size())
    {
      // Append to flie
      writeFile(file, line_to_insert+"\n", true);

      // Append line to array list
      arr.add(line_to_insert+"\n");

    }

    // Insert in between
    else
    { 
      // Insert line to array list in index 
      arr.add(next,line_to_insert+"\n");

      // Initialize variables
      toFile = "";

      // Iterate over array list
      for (String w : arr)
      {
        toFile += w.replace("\n","");
        toFile += "\n";
        
        //increment counter
        counter++;
        
      }

      // Write String to file
      writeFile(file, toFile, false);

    }
  }

  /* Convert date from String to Calendar
     Returns null in case the date does not exist:
     e.g. 05-32-2021 ---> will be converted to 06-1-2021
     Automatically by Calendar instance. There is a difference
     in the month. In that case returns null.
  */
  public static Calendar stringToDate(String date)
  {
    // Split date to Month, day, year
    String[] date_attr = date.split("-");  // Month-Day-Year

    // Define another_date according to date_attr
    Calendar another_date = Calendar.getInstance();
    another_date.clear();
    another_date.set(Integer.parseInt(date_attr[2]),
                       Integer.parseInt(date_attr[0])-1,
                       Integer.parseInt(date_attr[1]));

    // Ensure date exists
    if (Integer.parseInt(date_attr[0])-1 == another_date.get(Calendar.MONTH) && !another_date.after(today))
      // Returns a copy of another_date.
      return (Calendar) another_date.clone();
    else
    {
      if(another_date.after(today))
        System.out.println("\n*** Warning *** The date you entered is someday in the future!" );
      return null;
    }
  }

  public static void closeProgram(Scanner scn)
  {
     // Close Scanner object
      scn.close();
      System.out.print("Closing program!");
      System.out.print("\n");

      // Inform user 
      System.out.print("\n>------------------- Closed ---------------------<\n");

  }

  // Get input from user and check it is in the correct pattern.
  public static String getInput(String msg,Scanner scn, String pattern, String errorMSG,boolean first_field)
  {
    /*
    String Pattern ----> str_pattern = "[a-zA-Z\\'\\- ]+";
    Date Pattern   ----> date_pattern = "\\d{1,2}\\-\\d{1,2}\\-\\d{4}";
    */
    // Initialize 
    String input = "";

    // Check whether pattern string contains date format.
    boolean date = pattern.contains(date_pattern);

    // Check whether pattern string contains string format.
    boolean string = pattern.contains(str_pattern);

    // Iterate either 3 times or untill correct format entered
    for (int count = 0; count < 3; count++)
    {
      // Print input message to user
      System.out.print(msg);

       
        // Get input from user
        input = scn.nextLine();

        /* String has an extra feature with scn.nextLine()
           To avoid this feature I wrote this if condition
        */
        if (first_field) input += scn.nextLine();

      // Ensure input is in the right format
      if (!input.isEmpty() && input.matches(pattern))
      {
        // Ensure date input
        if (date && !input.equals("-1"))
        {
          // Split date to month, day,year
          String[] date_attr = input.split("-");

          /* Format input to MM-DD-YYYY
             If user entered 6-2-2021 -> it will be converted to 06-02-2021
          */
          input = String.format("%02d-%02d-%4d", 
                             Integer.parseInt(date_attr[0]),
                             Integer.parseInt(date_attr[1]),
                             Integer.parseInt(date_attr[2]));
        
        }

        // returns valid input
        return input;
      }

      // Print an error message if input is incorrect
      else if(count < 2)
      {
        System.out.println();
        System.out.println(errorMSG);
        System.out.println();
        first_field = false;
      }
    }

    // returns an error if 3 times failed.
    return "\n*** ERROR *** Input is not in the right format\n";
    

  }

  // *********************************************************************************

  

  // ************************************** Main Method ******************************
  public static void main (String args[]) {

    // Variables Declaration
    // Primitive data types
    String msg = "",pattern = "",errorMSG = "", input = "" ;
    double food_grams = 0.0; 
    int index = -1, max = -1, counter = 0, lines_num = -1;
    boolean run = true, added = false, inaugural_run = false, new_user = true;
    String choice = "", headers = "", month = "",date = "", toFile = "", food_name = "", brand_owner = "",food_type = "",workoutsStr = "";

    // Custom data types + Calendar + Scanner
    Calendar current_date,another_date;
    FoodDataset foodData = new FoodDataset();
    Scanner keyboard = new Scanner(System.in);
    FoodSearch fs = new FoodSearch();
    Food meal = null, new_meal = null;

    // Arrays
    double[] nutrients;
    String[] date_attr, line_attr;

    // Define External Files required for the program
    File wFile = new File("Files" +File.separator+ "workouts.csv");
    File uFile = new File("Files" +File.separator+ "users.csv");
    File dcFile = new File("Files" +File.separator+ "dailyConsumption.csv");
    File fdFile = new File("Files" +File.separator+ "foodDataset.csv");
    
    // Array Lists for reading files
    ArrayList<String> workoutsArrayList = new ArrayList<String>();
    ArrayList<String> usersArrayList = new ArrayList<String>();
    ArrayList<String> fdArrayList = new ArrayList<String>();
    ArrayList<String> dcArrayList = new ArrayList<String>();

    // Welcome Message
    System.out.print("\n>--------------- Health Tracker -----------------<\n");
    
    // Define user to null to avoid an error of no initialization
    Person user = null;

    // "Files" directory
    File filesDir = new File('.' + File.separator +"Files");

    // Ensure directory exists
    if (!filesDir.exists())
    {
      // Make a new directory
      filesDir.mkdir();

      // Inaugural run - new user
      inaugural_run = true;
    }

    
    // Try reading users file
    try{
         usersArrayList = readFile(uFile);

         // Get number of lines in file
         lines_num = usersArrayList.size();

    }catch(FileNotFoundException exc)
    {
      // No users in memory - inaugural_run
      inaugural_run  = true;

      // Creating required files
      headers = "id,name,gender,dci,weight,height\n";
      createNewFile(uFile, headers);
      usersArrayList.add(headers);

      headers = "name,category,brandOwner,grams,calorie,protein,fat,carbs,sugars\n";
      createNewFile(fdFile, headers);
      fdArrayList.add(headers);

      headers = "id,date,type,duration,location,calorie\n";
      createNewFile(wFile, headers);
      workoutsArrayList.add(headers);

       headers = "id,date,name,category,brandOwner,grams,calorie,protein,fat,carbs,sugars\n";
      createNewFile(dcFile, headers);
      dcArrayList.add(headers);

      // Max id is to make unique id
      max = 1000;
    }

    if (lines_num == 1)
    {
      // Only headers
      inaugural_run = true;

      max = 1000;

    }
    else if (lines_num > 1)
    {
        // Find user
        try{
          workoutsArrayList = readFile(wFile);
        }catch(FileNotFoundException exc)
        {
          headers = "id,date,type,duration,location,calorie\n";
          createNewFile(wFile, headers);
          workoutsArrayList.add(headers);
        }

        try{
          dcArrayList = readFile(dcFile);
        }catch(FileNotFoundException exc)
        {
           headers = "id,date,name,category,brandOwner,grams,calorie,protein,fat,carbs,sugars\n";
          createNewFile(dcFile, headers);
          dcArrayList.add(headers);

        }

        int user_id = -1;

        // Ask for ID
        msg = "\nPlease enter your ID (to make a new user enter 1).\nID: ";
        pattern = "\\d+";
        errorMSG = "*** ERROR *** Input must be an integer number greater than 0";
        input = getInput(msg,keyboard,pattern,errorMSG, false);
        if (!input.contains("ERROR"))
        {
          user_id = Integer.parseInt(input);
        }
        
        // Iterate over users save in file
        for (String line : usersArrayList)
        {

          // Split line into different attributes
          line_attr = line.split(",");

          // Ensure not first line (headers)
          if (!line_attr[0].equalsIgnoreCase("id"))
          {

            // Get id as an integer number
            max = Integer.parseInt(line_attr[0]);

            // Ensure user exist.
            if (max == user_id)
            {
              // Create user's instance based on gender
              if (line_attr[2].equalsIgnoreCase("male"))
              {
                user = new Male(max, line_attr[1],
                                Double.parseDouble(line_attr[4]),
                                Double.parseDouble(line_attr[5]),
                                Double.parseDouble(line_attr[3]));
              }
              else
              {
                user = new Female(max, line_attr[1],
                                Double.parseDouble(line_attr[4]),
                                Double.parseDouble(line_attr[5]),
                                Double.parseDouble(line_attr[3]));
              }

              // Avoid creating new user
              new_user = false;

            }
          }
        }
        
    }
  
    // Ensure user is defined
    if (!inaugural_run && !new_user)
    {
      // Welcome message
      System.out.printf("\nWelcome Back %s!\n", user.getName());

      // Declaration of required variables
      ArrayList<DailyConsumption> dc_temp = new ArrayList<DailyConsumption>();
      ArrayList<Workout> w_temp = new ArrayList<Workout>();
      boolean first_line = true;
      Food food_from_file;
      int id;

      // Initialize dates
      current_date = Calendar.getInstance();
      another_date = Calendar.getInstance();
      
      // Get workouts and daily consumption data for a specific user
      if (dcArrayList.size() > 1) // First line is headers
      {

        // Iterate over lines in dcArrayList
        for (String line : dcArrayList)
        {
          // Split line by comma
          line_attr = line.split(",");

          // Ensure not headers
          if (counter >= 1)
          {

            // Convert id from String to int
            id = Integer.parseInt(line_attr[0]);

            // Ensure id match
            if (id == user.getID())
            {
              // Convert date from string (in file) to Calendar
              another_date = stringToDate(line_attr[1]);

              // Ensure first line
              if (first_line)
              {
                // Convert date from string (in file) to Calendar
                current_date = stringToDate(line_attr[1]);

                // Create a new DailyConsumption instance
                dc_temp.add(new DailyConsumption(current_date, 
                                                 new ArrayList<Food>()));

                // Change first_line to false
                first_line = false;
              }

              // Ensure current date comes before another_date
              else if (another_date.after(current_date))
              {
                // Clear all fields in current_date
                current_date.clear();

                // Update current_date with a copy of another_date
                current_date = (Calendar) another_date.clone();

                // Create a new daily_consumption 
                dc_temp.add(new DailyConsumption(current_date, 
                                                 new ArrayList<Food>()));
              }

              // Ensure there are DailyConsumptions in dcArrayList
              if (dc_temp.size() > 0)
              {
                // headers = "id,date,name,category,brandOwner,grams,calorie,protein,fat,carbs,sugars\n";
                food_from_file = new Food(line_attr[2],                           // Name
                                               line_attr[3],                      // Category
                                               line_attr[4],                      // BrandOwner
                                               Double.parseDouble(line_attr[5]),  // Grams
                                               Double.parseDouble(line_attr[6]),  // Calorie
                                               Double.parseDouble(line_attr[7]),  // Protein
                                               Double.parseDouble(line_attr[8]),  // Fat
                                               Double.parseDouble(line_attr[9]),  // Carbs
                                               Double.parseDouble(line_attr[10]));// Sugars
                // Add food to the current DailyConsumption instance
                dc_temp.get(dc_temp.size() - 1).addFood(food_from_file);
              }
            }
          }

          // Increment counter
          counter++;
        }

        // Add all daily consumptions to user array list
        for(DailyConsumption dc : dc_temp) user.addDailyConsumption(dc);

        // Free memory by clearing array list and Food variable
        dc_temp.clear();
        food_from_file = null;
      }


      // Ensure ther are workouts in ArrayList
      // Avoid headers (> 1)
      if (workoutsArrayList.size() > 1) 
      {
        // Initialize variables
        current_date = Calendar.getInstance();
        counter = 0;
        
        // Iterate over lines in ArrayList
        for (String line : workoutsArrayList)
        {
          // Skip headers
          if (counter > 0) 
          {
            // Split line by comma
            line_attr = line.split(",");

            // Convert id from String to int
            id = Integer.parseInt(line_attr[0]);

            // Ensure id in file matches user's id
            if (id == user.getID())
            {
              // Convert date from string to Calendar
              current_date = stringToDate(line_attr[1]);

              // Add new workout to w_temp
              w_temp.add(new Workout(Integer.parseInt(line_attr[3]),  // Duration in seconds
                                     line_attr[4],                    // Location String
                                     line_attr[2],                    // Type String
                                     Double.parseDouble(line_attr[5]),// Calories double
                                    current_date));                   // Date Calendar

            }
          }

          // Increment Counter
          counter++;
        }

        // Add workouts arraylist to user
        user.addWorkouts(w_temp);

        // Free memory by clearing array list
        w_temp.clear();  
      }
    }
    // New user must be created.
    else  
    {
      System.out.print("\n>------------------ New User --------------------<\n");
      int unique_id = max + 1;

      // Ask for details
      String user_name = "";

      // Get name from user
      // Initialize msg, pattern and error message
      msg = "Enter Your Name: ";
      pattern = str_pattern;
      errorMSG = "*** ERROR *** Only letters, whitespaces, Apostrophes and dashes are allowed!";

      // Get input
      user_name = getInput(msg,keyboard,pattern,errorMSG,false);

      // Ensure not error
      if (user_name.contains("ERROR"))
      {
        System.out.println(user_name);
        closeProgram(keyboard);
        return; // terminate program
      }

      // 
      String user_gender = "";

      // Initialize msg, pattern and error message
      msg = "Please enter you Gender: ";
      pattern = "[M|m]ale|[F|f]emale";
      errorMSG = "*** ERROR *** Either Male or Female!";

      // Get input
      input = getInput(msg,keyboard,pattern,errorMSG,false);

      // Ensure not error
      if (!input.contains("ERROR"))
      {
        // Save input
        user_gender = input;
      }
      else
      {
        System.out.println(input);
        // Close program
        closeProgram(keyboard);

        // Terminate program
        return;
      }

      double user_dci = 0.0;

      // Initialize msg, pattern and error message
      msg = "Please enter your desired Calorie intake (Enter -1 for default): ";
      pattern = "[1-3][0-9]{3}(\\.[0-9]+)?|-1";
      errorMSG = "*** ERROR *** Must be either a positive number in the range 1000-4000 (exclusive) or -1 for default!";

      // Get input
      input = getInput(msg,keyboard,pattern,errorMSG,false);

      // Ensure not error
      if (!input.contains("ERROR"))
      {
        // Convert input to double
        user_dci = Double.parseDouble(input);
      }
      else
      {
        System.out.println(input);
        // Close program
        closeProgram(keyboard);

        // Terminate program
        return;
      }
    
      double user_weight = 0.0;

      // Initialize msg, pattern and error message
      msg = "Please enter your weight (kg): ";
      pattern = "[1-9][0-9](\\.[0-9]+)?|[1-4][0-9]{2}(\\.[0-9]+)?";
      errorMSG = "*** ERROR *** Must be a positive number in the range 10-500 (exclusive)!";

      // Get input
      input = getInput(msg,keyboard,pattern,errorMSG,false);

      // Ensure not error
      if (!input.contains("ERROR"))
      {
        // Conver input to double
        user_weight = Double.parseDouble(input);
      }
      else
      {
        System.out.println(input);

        // Close program
        closeProgram(keyboard);

        // Terminate program
        return;
      }

      double user_height = 0.0;
      // Initialize msg, pattern and error message
      msg = "Please enter your Height (cm): ";
      pattern = "[1-2][0-9]{2}(\\.[0-9]+)?";
      errorMSG = "*** ERROR *** Must be a positive number in range 100-300 (exclusive)!";

      // Get input
      input = getInput(msg,keyboard,pattern,errorMSG,false);

      // Ensure not error
      if (!input.contains("ERROR"))
      {
        // Convert input to double
        user_height = Double.parseDouble(input);
      }
      else
      {
        System.out.println(input);

        // Close program
        closeProgram(keyboard);

        // Terminate program
        return;
      }

      // Create user's instance
      if (user_gender.equalsIgnoreCase("male"))
      {
        // Ensure dci received
        if (user_dci < 0)
          user = new Male(unique_id, user_name, user_weight, user_height);
        else
          user = new Male(unique_id, user_name, user_weight, user_height, user_dci);
      }
      else
      {
        // Ensure dci received
        if (user_dci < 0)
          user = new Female(unique_id, user_name, user_weight, user_height);
        else
          user = new Female(unique_id, user_name, user_weight, user_height, user_dci);
      }

      // Welcome message to the new user
      System.out.printf("\nHey %s, Welcome to HealthTracker program!\n",user_name);
      System.out.printf("\nYour unique id is: %d. Make sure to remember it in order to log in.\n",unique_id);

      // Append new user to file
      writeFile(uFile, user.toFile(), true);
    }

    
    try{

      // Read foodDataset.csv file
      fdArrayList = readFile(fdFile);

    }catch(FileNotFoundException exc)
    {
      // Create new file
      headers = "name,category,brandOwner,grams,calorie,protein,fat,carbs,sugars\n";
      createNewFile(fdFile, headers);

      // Append header to fdArrayList
      fdArrayList.add(headers);
    }

    // Initialize counter
    counter = 0;

    // Ensure data exists
    if (fdArrayList.size() > 1)
    {
      // Iterate over lines 
      for(String line : fdArrayList)
      {
        // Skip headers
        if (counter > 0)
        {
          // Split line by comma
          line_attr = line.split(",");

          // Add new food to foodData
          foodData.addFood(new Food(line_attr[0],                      // Name
                                    line_attr[1],                      // Category
                                    line_attr[2],                      // Brand owner
                                    Double.parseDouble(line_attr[3]),  // Grams
                                    Double.parseDouble(line_attr[4]),  // Calories
                                    Double.parseDouble(line_attr[5]),  // Protein
                                    Double.parseDouble(line_attr[6]),  // Fat
                                    Double.parseDouble(line_attr[7]),  // Carbs
                                    Double.parseDouble(line_attr[8])));// Sugars

        }

        // Increment counter
        counter++;
      }
    }

    // Run Menu
    while(run){

      // Menu
      System.out.print("\n>-------------------- Menu ----------------------<\n");
      System.out.print("\nChoose one of the following (enter a number): \n"+
                       "1. Add New Meal \n"+
                       "2. Add New Workout \n"+
                       "3. Daily Consumption \n"+ 
                       "4. Workouts Done This Week \n"+
                       "5. Search Food in Data Set \n" + 
                       "6. Print All Food in Data Set \n"+
                       "7. Add Food to Data Set \n"+
                       "8. Exit\n\n");

      // Get choice from user
      System.out.print("Your Choice: ");
      choice = keyboard.next();
    
      System.out.print("\n");

      // Switch case 
      switch(choice){

            // Add New Meal
            case "1":

              // Initialize vars
              added = false;
              meal = null;
              new_meal = null;
              
              // // Get date from user
              date = getInput("Enter date (Format MM-DD-YYYY):", keyboard,date_pattern,"*** ERROR *** Input is not in the right format",true);

              // Ensure date is in the right format
              if (date.contains("ERROR"))
              {
                System.out.println(date);
                break;
              }
              // Convert date from string to Calendar
              current_date = stringToDate(date);
              if (current_date == null) 
              {
                System.out.println("\n*** ERROR *** Date does not exist!\n");
                break;
              }
              // Get food from user
              // Initialize msg, pattern and error message
              msg = "Enter Food's Name: ";
              pattern = str_pattern;
              errorMSG = "*** ERROR *** Only letters, whitespaces, Apostrophes and dashes are allowed!";

              // Get input
              food_name = getInput(msg,keyboard,pattern,errorMSG,false);

              // Ensure not error
              if (food_name.contains("ERROR"))
              {
                System.out.println(food_name);
                break;
              }

              // Initialize msg, pattern and error message
              msg = "Enter Grams: ";
              pattern = "[1-9][0-9]([0-9])?(\\.[0-9]+)?";
              errorMSG = "*** ERROR *** Must be a positive number in range 10-1000 (exclusive)!";
              food_grams = 0.0;

              // Get input
              input = getInput(msg,keyboard,pattern,errorMSG,false);

              // Ensure not error
              if (input.contains("ERROR"))
              {
                System.out.println(input);
                break;
              }
              // Convert input to double
              food_grams = Double.parseDouble(input);

              // Ensure food item exist in data set
              if (foodData.search(food_name))
              {

                  // Find the food in data set
                  meal = foodData.getFood(food_name);

                  // Get its type
                  food_type = meal.getCategory();

                  // Get brand 
                  brand_owner = meal.getBrand();

                  // Get food's nutrients in double
                  nutrients = foodData.getNutrientsDouble(food_name);

                  // Iterate over nutrients and change them according to 
                  // the grams entered by user
                  for (int iter = 0; iter < nutrients.length; iter ++)
                  {
                    nutrients[iter] = nutrients[iter] * food_grams / 100;
                  }

                  // Create a new Food instance.
                  new_meal = new Food(food_name,
                                      food_type,
                                      brand_owner,
                                      nutrients[0], // Grams
                                      nutrients[1], // Calorie
                                      nutrients[2], // Protein
                                      nutrients[3], // Fat
                                      nutrients[4], // Carbs
                                      nutrients[5]);// Sugars

                  // Add the new meal to the user
                  added = user.addFood(date,new_meal);


                  // Date not found
                  if (!added)
                  {
                    // Create a new Daily Consumption instance for user.
                    user.addDailyConsumption(new DailyConsumption(current_date, new ArrayList<Food>()));

                    // Add new meal
                    user.addFood(date,new_meal);
                  }

                  // Write new line to file and add to Array List
                  insertToFile(current_date,dcFile,user.getID() + "," + date +","+ new_meal.toFile(), dcArrayList);
                  System.out.println("\nSuccessfully Saved!\n");
                  
              }
              else
                System.out.println(food_name+ " does not exist in data set!");
            
            break;

          case "2":

              // Get date from user 
              date = getInput("Enter date (Format MM-DD-YYYY):",keyboard,date_pattern,"*** ERROR *** Input is not in the right format",true);

              // Ensure date is in the right format
              if (date.contains("ERROR"))
              {
                System.out.println(date);
                break;
              }
              
              // Convert date from string to Calendar
              current_date = stringToDate(date);
              if (current_date == null) 
              {
                System.out.println("\n*** ERROR *** Date does not exist!\n");
                break;
              }
              // Get food from user
              // Initialize msg, pattern and error message
              msg = "Enter Workout's Type: ";
              pattern = str_pattern;
              errorMSG = "*** ERROR *** Only letters, whitespaces, Apostrophes and dashes are allowed!";

              // Get input
              String type = getInput(msg,keyboard,pattern,errorMSG,false);

              // Ensure not error
              if (type.contains("ERROR"))
              {
                System.out.println(type);
                break;
              }

              // Get duration in minutes from user
              double durationMinutes = 0.0;
              
              // Initialize msg, pattern and error message
              msg = "Enter duration in minutes: ";
              pattern = "[1-9][0-9]([0-9])?(\\.[0-9]+)?";
              errorMSG = "*** ERROR *** Must be a positive number in range 10-1000 (exclusive)!";

              // Get input
              input = getInput(msg,keyboard,pattern,errorMSG,false);

              // Ensure not error
              if (!input.contains("ERROR"))
              {
                // Convert input to double
                durationMinutes = Double.parseDouble(input);
              }
              else
              {
                System.out.println(input); //Print error!
                break;
              }

              // Get location from user
              // Initialize msg, pattern and error message
              msg = "Enter Location: ";
              pattern = str_pattern;
              errorMSG = "*** ERROR *** Only letters, whitespaces, Apostrophes and dashes are allowed!";

              // Get input
              String location = getInput(msg,keyboard,pattern,errorMSG,false);

              // Ensure not error
              if (location.contains("ERROR"))
              {
                System.out.println(location);
                break;
              }

              // // Get calories burned from user.
              // System.out.print("Enter calories burned: ");
              double calorie = 0.0;

              // Initialize msg, pattern and error message
              msg = "Enter calories burned: ";
              pattern = "[1-9][0-9]{1,2}(\\.[0-9]+)?|[1-3][0-9]{3}(\\.[0-9]+)?";
              errorMSG = "*** ERROR *** Must be a positive number in range 10-4000 (exclusive)!";

              // Get input
              input = getInput(msg,keyboard,pattern,errorMSG,false);

              // Ensure not error
              if (!input.contains("ERROR"))
              {
                // Convert input to double
                calorie = Double.parseDouble(input);
              }
              else
              {
                System.out.println(input); //Print error!
                break;
              }
              double durationSec = durationMinutes*60;

              // Create a new workout instance
              Workout new_workout = new Workout((int)durationSec, 
                                                location, type, calorie, current_date);

              // Add the new instance to user.
              user.addWorkouts(new_workout);

              // Write new line to file and add to Array List
              insertToFile(current_date,wFile,user.getID() +","+new_workout.toFile(), workoutsArrayList);
              System.out.println("\nSuccessfully Saved!\n");
            

              break;

          case "3":

              // Get date from user 
              date = getInput("Enter date (Format MM-DD-YYYY or -1 for today):", 
                                  keyboard,
                                  "\\d{1,2}-\\d{1,2}-\\d{4}|-1",
                                  "*** ERROR *** Input is not in the right format",true);

              if (date.contains("ERROR"))
              {
                // Print error
                System.out.println(date);

                // Back to menu
                break;
              }
              // Ensure -1 entered
              if (date.equals ("-1"))
              {
                // Make it easier to enter today's date
                // Calendar instance
                current_date = Calendar.getInstance();
              }
              else
              {
                current_date = stringToDate(date);
                if (current_date == null) 
                {
                  System.out.println("\n*** ERROR *** Date does not exist!\n");
                  break;
                }
              }

             // Convert month to string 
              // (0 - january, 11- december) - that's the reason for +1
              month = String.format("%02d" ,current_date.get(Calendar.MONTH) + 1);

              // date as string
              date = (month + "-" +
                          String.format("%02d",current_date.get(Calendar.DATE)) + "-" +
                          current_date.get(Calendar.YEAR));

              // Daily consumption string for that user
              String dcString = user.getDailyConsumption(date);

              // Ensure there is data
              if (!dcString.equalsIgnoreCase("Not found"))
              {
                // Print data to user
                System.out.print("\n*********** Daily Consumption ***********\n\n");
                System.out.print(dcString);

              }
              // No records 
              else System.out.print("\n*** MSG *** No records for that date!\n");

              break;
              
          case "4":

              // Current date
              current_date = Calendar.getInstance();

              // Substract 7 days
              current_date.add(Calendar.DATE, -7); 
              
              // Print last week workouts
              workoutsStr = user.getWorkouts(current_date);

              // Ensure there is data
              if (!workoutsStr.isEmpty())
              {
                // Print data to user
                System.out.print("\n*********** Workouts Done This Week ***********\n");
                System.out.print(workoutsStr);
                
              }
              // No records 
              else System.out.print("No records for that date!\n");

              break;

          case "5":

              // Get location from user
              // Initialize msg, pattern and error message
              msg = "Enter Food's Name: ";
              pattern = str_pattern;
              errorMSG = "*** ERROR *** Only letters, whitespaces, Apostrophes and dashes are allowed!";

              // Get input
              food_name = getInput(msg,keyboard,pattern,errorMSG,true);

              // Ensure not error
              if (food_name.contains("ERROR"))
              {
                System.out.println(food_name);
                break;
              }

              if (foodData.search(food_name))
              {
                System.out.print("\n*********** Food Item Found ***********\n\n");
                System.out.println(foodData.getFood(food_name).toString());
              }
              else
              {
                food_name = food_name.substring(0,1).toUpperCase() + food_name.substring(1).toLowerCase();
                // A temporary copy of the data
                ArrayList<Food> temp = new ArrayList<Food>(foodData.getData());
                boolean similar = false;
                String suggestions = "";
                for (Food food : temp)
                {
                  if (food.getName().contains(food_name))
                  {
                    similar = true;
                    suggestions += food.toString() +"\n\n";
                  }
                    
                }
                if (similar)
                {
                  System.out.print("\n*********** Food Suggestions ***********\n\n");
                  System.out.println(suggestions);
                }
                else
                  System.out.println("*** MSG *** Nothing Found!");

                temp.clear();
              }
              break;


          case "6":

              // Print Food Data Set to user
              System.out.print("\n*********** Food Data Set ***********\n\n");
              System.out.print(foodData.toString());
              System.out.print("\n");

              break;

          case "7":
            
              // Get food from user
              // Initialize msg, pattern and error message
              msg = "Enter Food's Name: ";
              pattern = str_pattern;
              errorMSG = "*** ERROR *** Only letters, whitespaces, Apostrophes and dashes are allowed!";

              // Get input
              food_name = getInput(msg,keyboard,pattern,errorMSG,true);

              // Ensure not error
              if (food_name.contains("ERROR"))
              {
                System.out.println(food_name);
                break;
              }

              // Get food's brand from user
              // Initialize msg, pattern and error message
              msg = "Enter Brand: ";
              pattern = str_pattern;
              errorMSG = "*** ERROR *** Only letters, whitespaces, Apostrophes and dashes are allowed!";

              // Get input
              brand_owner = getInput(msg,keyboard,pattern,errorMSG,false);

              // Ensure not error
              if (brand_owner.contains("ERROR"))
              {
                System.out.println(brand_owner);
                break;
              }
             
              // Initialize new_meal to null to avoid compiling error
              new_meal = null;

              // Try search for food
             new_meal = fs.searchFood(food_name,brand_owner);

              // Ensure there is data
              if (new_meal != null)
              {
                boolean to_add = false;

                // Ennsure name exists in data set
                if (foodData.search(new_meal.getName()))
                {
                  // Check wether new meal equals the item found in data
                  // It might have a similar name, but different brandOwner
                  if (!new_meal.equals(foodData.getFood(new_meal.getName())))
                    to_add = true;
                  else
                  {
                    // Inform user and print item details
                    System.out.println("\nItem already exists in dataset!\nItem Information: \n");
                    System.out.println(new_meal.toString());
                  }

                }
                else
                  to_add = true;

                // Add new food to data set
                if (to_add)
                {
                  if (new_meal.toFile().split(",").length == 9)
                  {
                  // Write data to file
                    writeFile(fdFile,new_meal.toFile()+"\n", true);

                    // Add data to ArrayList
                    fdArrayList.add(new_meal.toFile()+"\n");

                    // Add data to foodData
                    foodData.addFood(new_meal);

                    // Print Message
                    System.out.println("\nItem Was Successfully saved: ");
                    System.out.println(new_meal.toString());
                  }
                  else
                    System.out.println("\n*** Warning *** Something went wrong");
                    /* NOTE: Too many elements in the array. If were saved in file
                             it would have created an error in the future! 
                             I made sure that FoodSearch delete 
                             all commas (",") from Strings, so it
                             shouldn't be an issue. However, just to make
                             sure that there will not be index errors in 
                             the future I added this condition.
                    */
                }

              }
              else
                // Print error message
                System.out.println("\n*** Warning *** Couldn't find this item");


              break;

          case "8":

              // Exit
              run = false;
              break;

          default:
              System.out.print("Wrong choice.\nPlease choose a number between 1 and 7 (inclusive) according to the menu below.\n\n");
              break;
      }

    
      
    }
    // Close Scanner object
    closeProgram(keyboard);
  }
} 