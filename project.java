import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

//Add in a ; checker for sql injection

class Project {
    public static Statement s;

    public static void main(String[] args) {

        Connection con = null;
        Scanner scan = new Scanner(System.in);
        boolean breaker = true;
        s = null;

        //Connection check
        do{

            try {
                
                System.out.print("Username:");
                String user = scan.nextLine();
                //System.out.println(user);
                System.out.print("Password:");
                String pass = scan.nextLine();
                //System.out.println(pass);

                con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user, pass);
                s=con.createStatement();
                System.out.println("connection successfully made.");
                breaker = false;
            }
            catch(Exception e) {
                System.out.println("You have the wrong login. Try again");
				//e.printStackTrace();
            }
        }while(breaker);

        //should make this a method
        Menu();
        try{
            con.close();
        }
        catch(Exception e){
            System.out.println("Failing connection close");
        }
        
        return;


    }


    public static void Menu(){
        int ans;
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to Regork, Who are you?");
        System.out.println("[1] Regork Manager\n[2] Supplier\n[3] Regork Analyst\n[0] Exit");
        ans = IntScan(scan);
        if(ans == 1){
            Manager.main();
        }
        else if(ans == 2) {//---------------------------------------------------STILL NEED TO DO!!!!!!!!!!!!!
            Supplier.main();
        }
        else if(ans == 0){
            return;
        }
        else{
            System.out.println("Please enter a correct value...");
            Menu();
        }
    }
    //possible methods for project
    //check functions(ints and strings)
    public static String StringScan(Scanner scan) {
        String result = scan.nextLine();
        for(int i = 0; i < result.length(); ++i){
            if(!Character.isLetter(result.charAt(i))){
                return "Not a letter";
            }
            else if(result.charAt(i) == ';' || result.charAt(i) == '-' || result.charAt(i) == '*' || result.charAt(i) == '/'){
                return "Do not try to do SQL injection...";
            }
            else{
                continue;
            }
        }
        return result;
    }

    public static int IntScan(Scanner scan){//checks to see if the user's input of an integer is valid
        String result = scan.nextLine();
        if(result.isEmpty()){
            return -10;
        }
        for(int i = 0; i < result.length(); ++i){
            if(!Character.isDigit(result.charAt(i))){
                return -12;
            }
            if(result.charAt(i) == ';' || result.charAt(i) == '-' || result.charAt(i) == '*' || result.charAt(i) == '/'){
                return -14;
            }
            else{
                continue;
            }
        }
        int i = Integer.valueOf(result);
        return i;
    }



}


/*Things I want the manager to do
1) check deliveries that have been shipped between x and y days
2) place a new order
3) get rid of something the store has
*/
class Manager extends Project{
    public static void main(){
        //Variables
        Scanner in = new Scanner(System.in);
        

        //what would the manager like to do
        System.out.println("Welcome manager.");
        System.out.println("Please enter your Store ID:");
        int storeID = Project.IntScan(in);
        String check = "select * from store where store_id = " + storeID;

        try {
            ResultSet result = s.executeQuery(check);
            if (!result.next()){
                System.out.println("That store does not exist");
                Manager.main();
            }
        }
        catch(Exception e){
            System.out.println("Something went wrong when store_id input");
            //e.printStackTrace();
        }
        ManagerMenu(in, storeID);
    }

//-------------------------------------------------------------------------------MANAGER MENU MEHTOD-----------------------------------------------------------
    public static void ManagerMenu(Scanner in, int storeID){
        
        int ans;
        System.out.println("[1] check deliveries\n[2] place a new order\n[3] remove an item\n[4] Exit");
        ans = Project.IntScan(in);
        answerCheck(ans, storeID);
    }

//-------------------------------------------------------------------------------MENU ANSWER CHECK MEHTOD-----------------------------------------------------------
    public static void answerCheck(int i, int storeID){
        Scanner in = new Scanner(System.in);
        if(i == 1){
            deliveries(storeID);
        }
        else if(i == 2){
            newOrder(0, storeID);
        }
        else if (i == 3){
            removeItem(storeID);
        }
        else if(i == 4){
            Project.Menu();
        }
        else{
            System.out.println("You did not give a valid response");
            ManagerMenu(in, storeID);
        }
    }

//-------------------------------------------------------------------------------CHECKING DELIEVERIES MEHTOD-----------------------------------------------------------
    /*
    This will show all of the orders that have been shipped between X and Y dates. It will split up the shipments between ones that have products
    and those that do not. It will also order them by ship date

    Notes:
        - Asks the user for the dates they would like to input
        -

    possible query:
    select * from orders natural join send natural join contains
    where send.time_shipped >= 'variable'
    order by send.time_shipped


    */
    public static void deliveries(int storeID){
        Scanner in = new Scanner(System.in);
        ResultSet result;
        String find;
        int answer;

        try{
            System.out.println("You wish to find some shipments");
            System.out.println("Enter the dates you wish to search between (Ex: 01-JAN-19)");

            System.out.print("Month(JAN) 1:");
            String monthOne = Project.StringScan(in);
            if(!checkMonth(monthOne)){
                deliveries(storeID);
            }
            System.out.print("Day(01) 1:");
            int dayOne = Project.IntScan(in);
            if(!checkDay(dayOne, monthOne)){
                deliveries(storeID);
            }
            System.out.print("Year(19) 1:");
            int yearOne = Project.IntScan(in);


            System.out.println();
            
            
            System.out.print("Month(JAN) 2:");
            String monthTwo = Project.StringScan(in);
            if(!checkMonth(monthOne)){
                deliveries(storeID);
            }
            System.out.print("Day(01) 2:");
            int dayTwo = Project.IntScan(in);
            if(!checkDay(dayTwo, monthTwo)){
                deliveries(storeID);
            }
            System.out.print("Year(19) 2:");
            int yearTwo = Project.IntScan(in);

            find = "select tracking_num as Tracking#, time_shipped as Shipment from orders natural join send where send.time_shipped >='"+dayOne + "-" + monthOne + "-" + yearOne + 
            "'and send.time_shipped <='"+ dayTwo + "-" + monthTwo + "-" + yearTwo + "' order by send.time_shipped";


            result = s.executeQuery(find);
            if (!result.next()) {
                System.out.println("There are no deliveries in the time you specified");
            }
            else {
                System.out.println("--------------------------------------------------");
                do{
                    System.out.println("Tracking Number: " + result.getString("Tracking#"));
                    System.out.println("Time Sent: " + result.getString("Shipment"));
                    System.out.println("--------------------------------------------------");
                }while(result.next());

            }
        }
        catch(Exception e){
            System.out.println("You did not put in a valid date");
        }
        //------------------------------------------------------------------Done with initial asking of deliveries----------------------------------------------
        try{
            System.out.println("Would you like to see the full information of any shipment?\n[1] Yes\n[2] No");
            answer = Project.IntScan(in);

            
            if(answer == 1) {
                System.out.println("Please enter a Tracking Number(#):");
                int trackingNum = Project.IntScan(in);
                find = "select tracking_num, time_shipped, supplier_id, product_id, quantity, price_per_unit " +
                "from orders natural join send natural join contains where tracking_num = " + trackingNum;
                result = s.executeQuery(find);
                if (!result.next()) {
                    System.out.println("You must have entered in a wrong tracking number or there is nothing in the shipment");
                }
                else {
                    System.out.println("--------------------------------------------------");
                    do {
                        System.out.println("Tracking Number: " + result.getString("tracking_num"));
                        System.out.println("Time Shipped: " + result.getString("time_shipped"));
                        System.out.println("Supplier ID: " + result.getString("supplier_id"));
                        System.out.println("Product ID: " + result.getString("product_id"));
                        System.out.println("Quantity: " + result.getString("quantity"));
                        System.out.println("Price Per Unit: " + result.getString("price_per_unit"));
                        System.out.println("--------------------------------------------------");
                    } while (result.next());
                }

            }
            ManagerMenu(in, storeID);
        }
        catch(Exception e){

            System.out.println("You entered in a falty tracking number");
            //e.printStackTrace();
        }
    }



//-------------------------------------------------------------------------------SUBMITTING A NEW ORDER MEHTOD-----------------------------------------------------------
    /*
    Asks for a product description and then finds the supplier(s) that make that product. Then asks for the certain supplier you want to
    order from and them creates a new order.


    Notes:
    - Ask for an item you want to have
        -> select Product_id, name from product
        - then ask for the supplier ID
        -> select product_id, ipo, exchange_price, name from product natural join make where supplier_id = X
            - Then put in the new order
            -> insert into orders values
            -> insert into send values
            - For the receive tuple ask for 2 types of shipment fast or slow
                - Fast -> 1 week from today
                ->insert into receiving value (current_time + 7)
                - Slow -> 2 weeks from today
                ->insert into receiving value (current_time + 14)
            - Then print out the receive date
    possible query:
    insert into orders values(tracking num, quantity, price per unit);


     */
    public static void newOrder(int SUP_ID, int STORE_ID){
        Scanner in = new Scanner(System.in);
        String find;
        ResultSet result;
        //System.out.println(SUP_ID);
        try{
            System.out.println("Purchase order underway....");
            System.out.println("Enter in a product ID that you want to order:");
            find = "select Product_id, name from product";
            result = s.executeQuery(find);

            if (!result.next()) {
                System.out.println("There is no product with that ID");
                newOrder(SUP_ID, STORE_ID);
                //May want to call the method again for restart
            }
            else {
                System.out.println("--------------------------------------------------");
                do {
                    System.out.println("Product ID: " + result.getString("Product_ID"));
                    System.out.println("Product Name: " + result.getString("name"));
                    System.out.println("--------------------------------------------------");
                } while (result.next());
            }
        }
        catch(Exception e){
            System.out.println("You did not enter in a valid ID");
            newOrder(SUP_ID, STORE_ID);
        }


        String tracking = "";
        try{
            tracking = "select Max(CAST(tracking_num as int)) + 1 from orders";
            result = s.executeQuery(tracking);
            while(result.next()){
                tracking = result.getNString(1); 
            }
            int value = Integer.valueOf(tracking);
        }
        catch(Exception e){
            System.out.println("Something went wrong with seeting the max value");
        }

            //-------------------------------------Product ID selection---------------------------------------------
           
        int Product = 0;
        try{
            System.out.print("Select a Product ID to order:");
            Product = Project.IntScan(in);
            find = "select supplier_id, name from supplier natural join make where product_id = " + Product;

            result = s.executeQuery(find);
            if(!result.next()){
                System.out.println("There are no companies that supply that product");
                newOrder(SUP_ID,STORE_ID);

            }
            else{
                System.out.println("\n");
                System.out.println("<><><>  SUPPLIERS  <><><>");
                do{
                    System.out.println("Supplier ID: " + result.getString("supplier_id"));
                    System.out.println("Supplier Name: " + result.getString("name"));
                    System.out.println("--------------------------------------------------");
                }while(result.next());
            }
        }
            catch(Exception e){
            System.out.println("You did not enter in the supplier ID correctly");
            newOrder(SUP_ID, STORE_ID);
        }



    //-------------------------------------Creating the order---------------------------------------------
            
        String supply = "";
        int quantity = 0;
        String price = "";
        try{
            System.out.println("Select a supplier ID to place an order with");
            supply = in.nextLine();
            System.out.print("Select the amount to purchase:");
            quantity = Project.IntScan(in);
            price = "select IPO from make natural join product where product_id = " + Product + " and supplier_id = " + supply;
            result = s.executeQuery(price);
            while(result.next()){
                price = result.getNString(1);
            }
            System.out.println("Price: " + price);


                
        }
        catch(Exception e){
            System.out.println("When finishing up the order the query went ka-put");
            e.printStackTrace();
            newOrder(SUP_ID, STORE_ID);
        }


        if(SUP_ID == 0){
            try{
                //System.out.println("STORE REVEIVE!!!!!!");
                find = "insert into orders(tracking_num) values ("+tracking+")";
                s.executeQuery(find);
                find = "insert into send(time_shipped, supplier_id, tracking_num, quantity, price_per_unit) values (CURRENT_DATE," + supply + ", " + tracking
                + ", " + quantity + ", " + price + ")";
                s.executeQuery(find);


                find = "insert into contains(tracking_num, product_id) values (" + tracking + ", " + Product + ")";
                s.executeQuery(find);

                System.out.println("Fast or Slow Shipping:");
                System.out.println("[1] Slow (2 weeks)\n[2] Fast(1 Week)");
                int ans = Project.IntScan(in);
                if(ans == 1){
                    find = "insert into receive (time_recieved, supplier_id, tracking_num, store_id) values ((current_date + 14), "+ SUP_ID + ", " + tracking +
                            ", " + STORE_ID + ")";
                }
                else if(ans == 2){
                    find = "insert into receive (time_recieved, supplier_id, tracking_num, store_id) values ((current_date + 7)," + SUP_ID + "," + tracking +
                            ", " + STORE_ID + ")";
                }
                    
            }
            catch(Exception e){
                System.out.println("Something went wrong with your order");
                e.printStackTrace();
            }
        }
        else{
            try{
                //System.out.println("SUPPLIER REVEIVE!!!!!!");
                find = "insert into orders(tracking_num) values ("+tracking+")";
                s.executeQuery(find);

                find = "insert into send(time_shipped, supplier_id, tracking_num, quantity, price_per_unit) values (CURRENT_DATE," + supply + ", " + tracking
                + ", " + quantity + ", " + price + ")";
                s.executeQuery(find);


                find = "insert into contains(tracking_num, product_id) values (" + tracking + ", " + Product + ")";
                s.executeQuery(find);

                System.out.println("Fast or Slow Shipping:");
                System.out.println("[1] Slow (2 weeks)\n[2] Fast(1 Week)");
                int ans = Project.IntScan(in);
                
                if(ans == 1){
                    find = "insert into receive (time_recieved, supplier_id, tracking_num, store_id) values ((current_date + 14), "+ SUP_ID + ", " + tracking +
                            ", -1)";
                    s.executeQuery(find);
                }
                else if(ans == 2){
                    find = "insert into receive (time_recieved, supplier_id, tracking_num, store_id) values ((current_date + 7)," + SUP_ID + "," + tracking +
                            ", -1)";
                    s.executeQuery(find);
                }

            }
            catch(Exception e){
                System.out.println("Something went wrong with your order");
                e.printStackTrace();
            }
        }

        System.out.println("Would you like to buy another product with this order?\n[1] Yes\n[2] No");
        int ans = Project.IntScan(in);
        if(ans == 1){
            newOrder(SUP_ID, STORE_ID);   
        }


//Completes the order and brings you back to the menus
        System.out.println("The order has been complete");
        if(SUP_ID == 0){
            ManagerMenu(in, STORE_ID);
        }
        else{
            Supplier.supplierMenu(in, SUP_ID);
        }
        
    }
    


//-------------------------------------------------------------------------------REMOVING AN ITEM MEHTOD-----------------------------------------------------------
    /*
    You list the product name for the item you want to remove from the store then it will remove all instances of it.
    (possibly) ask you if you want to remove the suppliers that ship it

    Notes:
        - Show the inventory of the store
        ->select product_id, name from stock natural join product where store_id = X
        - Prompt to ask the user what item they would like to remove by name

        - It will then show a list of the items and ask the user for the product ID of the item
            - It will then remove that item from the Store table
            - Also it will remove the item from all tables
        - It will then print out the current Store Item list
        - Then it will prompt the user and ask if they wish to not due business with the person who made the item(Tentative)
     */
    public static void removeItem(int storeID){
        Scanner in = new Scanner(System.in);
        String find = "";
        ResultSet result;
       
       
        try{
            System.out.println("Here is the inventory of your store");
            find = "select product_id, name from stock natural join product where store_id =" + storeID;
            result = s.executeQuery(find);
            if(!result.next()){
                System.out.println("Your store has no inventory...");
                ManagerMenu(in, storeID);
            }
            else{
                System.out.println("--------------------------------------------------");
                do{
                    System.out.println("Product ID: " + result.getString("product_id"));
                    System.out.println("Product Name: " + result.getString("name"));
                    System.out.println("--------------------------------------------------");
                }while(result.next());
            }
        }
        catch(Exception e){
            System.out.println("Something is wrong with your inventory query.... Probably");
            e.printStackTrace();
        }
        //-------------------------------------printing the inventory of the store---------------------------------------------

        try{
            System.out.print("\nWhat item ID would you like to remove: ");
            String remove = in.nextLine();
            System.out.print("\nAre you sure you want to remove it [1] Yes\n[2] No\n: ");
            int ans = Project.IntScan(in);
            if(ans == 1){
                find = "delete from stock where store_id = " + storeID + " and product_id = " + remove + ";";
                result = s.executeQuery(find);
                ManagerMenu(in, storeID);
            }
            else{
                System.out.println("Okay that is fine.");
                ManagerMenu(in, storeID);
            }
        }
        catch(Exception e){
            System.out.println("Deleting did not happen properly");
            ManagerMenu(in, storeID);   
        }
    }



//-------------------------------------------------------------------------------CHECKMONTH MEHTOD-----------------------------------------------------------
    //Checks whether the month that the user gave is valid
    public static boolean checkMonth(String check){
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL" ,"AUG", "SEP", "OCT", "NOV", "DEC"};
        for(int i = 0; i < 12; ++i){
            if(check.equals(months[i])){
                return true;
            }
        }
        System.out.println("You did not enter in a valid month");
        return false;
    }
//-------------------------------------------------------------------------------CHECKDAY MEHTOD-----------------------------------------------------------
    public static boolean checkDay(int check, String month){
        String[] daysOne = {"JAN", "MAR", "MAY", "JUL", "AUG", "OCT", "DEC"};//31 days
        String[] daysTwo = {"APR", "JUN", "SEP", "NOV"};//30 days


        for(int i = 0; i < daysOne.length; ++i){//31 days
            if(month.equals(daysOne[i])){
                if(check > 31 || check < 1){
                    System.out.println("You provided days that are not real");
                    return false;
                }
                else{
                    return true;
                }
            }
        }
        for(int i = 0; i < daysTwo.length; ++i){//30 days
            if(month.equals(daysOne[i])){
                if(check > 30 || check < 1){
                    System.out.println("You provided days that are not real");
                    return false;
                }
                else{
                    return true;
                }
            }
        }

        //Feburary
        if(check > 28 || check < 1){
            System.out.println("You provided days that are not real");
            return false;
        }
        else{
            return true;
        }
    }
}






//<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
//<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
//<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>



class Supplier extends Project{
    public static void main(){
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Please enter your supplier ID. If you do not know your ID then press -1");
        int notKnown = scan.nextInt();
        //System.out.println(notKnown);
        if(notKnown == -1){
            try{
                String check = "select supplier_id, name from supplier";
                ResultSet result = s.executeQuery(check);
                System.out.println("--------------------------------------------------");
                if(!result.next()){
                    System.out.println("Stuff");
                }
                else{
                    do{
                        System.out.println("Supplier ID: " + result.getString("supplier_id"));
                        System.out.println("Supplier Name: " + result.getString("name"));
                        System.out.println("--------------------------------------------------");
                    }while(result.next());
                }
                

                System.out.println("Are you a new Supplier?\n[1] Yes\n[2] No");
                notKnown = scan.nextInt();

                //System.out.println("HIIIIII");
                if(notKnown == 1){
                    addSupplier(scan);
                }
                else{
                    main();
                }
            }
            catch(Exception e){

                System.out.println("Something went wrong with printing the suppliers");
                e.printStackTrace();
            }
        }
        int supplier = checkSupplier(notKnown);
        supplierMenu(scan, supplier);


    }
//-------------------------------------------------------------------------------CHECK SUPPLIER MEHTOD-----------------------------------------------------------    
    public static int checkSupplier(int supplierID){
        //int supplierID = Project.IntScan(scan);
        try{
            String check = "select supplier_id, name from supplier where supplier_id = " + supplierID;
            ResultSet result = s.executeQuery(check);
            if (!result.next()){
                System.out.println("That supplier does not exist");
                Supplier.main();//possibly checkSupplier call
            }
        }
        catch(Exception e){
            System.out.println("The supplier ID you have entered has gone wrong.");
            e.printStackTrace();
            Supplier.main();
        }
        return supplierID;
    }
//-------------------------------------------------------------------------------SUPPLIER MENU MEHTOD-----------------------------------------------------------
    public static void supplierMenu(Scanner scan, int supplier){
        System.out.println("Welcome supplier...");
        System.out.println("Press...\n[1] Add a Product\n[2] Placing an order\n[3]check your inventory\n[4]exit to main menu");
        int ans = scan.nextInt();
        if(ans == 1){
            createProduct(scan, supplier);
        }
        else if(ans == 2){
            Manager.newOrder(supplier, -1);
        }
        else if(ans == 3){
            checkInventory(scan, supplier);
        }
        else if(ans == 4){
            Project.Menu();
        }
        else{
            System.out.println("Your did not pick a proper option...");
            supplierMenu(scan, supplier);
        }
    }
//-------------------------------------------------------------------------------CREATION OF A PRODUCT MEHTOD-----------------------------------------------------------
    public static void createProduct(Scanner in, int supplier){
        String find = "";
        System.out.println("What product would you like to create?");

        System.out.print("Enter in the Product ID: ");//product ID
        int productID = Project.IntScan(in);
        
        System.out.print("Enter in the initial public offering: ");
        int IPO = Project.IntScan(in);

        System.out.print("Enter in the exchange price: ");
        int exchange = Project.IntScan(in);
        
        System.out.print("Enter in the industry: ");
        String industry = Project.StringScan(in);
        
        System.out.print("Enter in the name: ");
        String name = Project.StringScan(in);

        if(ans == 1){
            createProduct(in, supplier);
        }
        else{
            supplierMenu(in, supplier);
        }
        catch(Exception e){
            System.out.println("You did not enter the product in the proper way");
            createProduct(in, supplier);
        }




    }
//-------------------------------------------------------------------------------ADDING A SUPPLIER MEHTOD-----------------------------------------------------------
    public static void addSupplier(Scanner in){
        int supplierID = 0;
        String find = "";
        int value = 0;
        ResultSet result;


        System.out.println("You would like to become a supplier for Regork\n[1] Yes\n[2] No");
        int ans = Project.IntScan(in);
        if(ans == 1){        
            try{
                find = "select MAX(CAST(supplier_id as int)) + 1 from supplier";
                result = s.executeQuery(find);
                if(!result.next()){
                    System.out.println("There is not a supplier ID avaiable for you");
                }
                else{
                    while(result.next()){
                        find = result.getNString(1); 
                    }
                    value = Integer.valueOf(find);
                }
            }
            catch(Exception e){
                System.out.println("That Supplier ID already exists...");
            }

            try{
                System.out.print("\nEnter what industry your company is in: ");//industry
                String industry = Project.StringScan(in);

                System.out.print("\nEnter in the street number of your company: ");//street name
                int str_num = Project.IntScan(in);
                
                System.out.print("\nEnter in the street name of your company: ");//street num
                String str_name = Project.StringScan(in);
                
                System.out.print("\nEnter in the apartment number of your company (if none then put 0): ");//apt num
                int apt_num = Project.IntScan(in);
                
                System.out.print("\nEnter in the city your company resides in: ");//city
                String city = Project.StringScan(in);
                
                System.out.print("\nEnter in the state which your company resides in (Ex: PA for pennsylvania): ");//state
                String state = Project.StringScan(in);
                
                System.out.print("\nEnter in your Zip code: ");//zip
                int zip = Project.IntScan(in);
                
                System.out.print("\nEnter in your company's name: ");//name
                String name = Project.StringScan(in);
                
                
                find = "insert into supplier(supplier_id, industry, street_number, street_name, apt_number, city, state, zip, name) values " +
                 "(" + value + ", '" + industry + "', " + str_num + ", '" + str_name + "', "+ apt_num + ", '" + city + "', '" + state + "', " + zip + ", '" + name + "')";
                result = s.executeQuery(find);

            }
            catch(Exception e){
                System.out.println("You did not enter in the proper format for your company");
            }

            System.out.println("would you like to add the products you sell now or later\n[1] Now\n[2] Later");
            ans = Project.IntScan(in);
            if(ans == 1){
                createProduct(in, value);
            }
            else{
                supplierMenu(in, value);
            }
        }
        else{
            System.out.println("Have a nice day then");
            supplierMenu(in, 0);
        }
    }

//-------------------------------------------------------------------------------CHECKING INVENTORY MEHTOD-----------------------------------------------------------
    //select product_id, name ,IPO, exchange_price  from make natural join product where supplier_id = X
    public static void checkInventory(Scanner in, int supplier){
        String find = "";
        ResultSet result;
        int ans = 0;
        try{
            find = "select product_id, name ,IPO, exchange_price from make natural join product where supplier_id = " + supplier;
            result = s.executeQuery(find);
            if(!result.next()){
                System.out.println("You do not have any inventory. Would you like to add a product to sell?\n[1] Yes\n[2] No");
                ans = Project.IntScan(in);
                if(ans == 1){
                    createProduct(in, supplier);
                }
                else{
                    supplierMenu(in, supplier);
                }
            }
            else{
                System.out.println("--------------------------------------------------");
                do{
                    System.out.println("Product ID: "+result.getString("product_id"));
                    System.out.println("Product Name: "+result.getString("name"));
                    System.out.println("Inital Public Offering: "+result.getString("IPO"));
                    System.out.println("Product Price: "+result.getString("exchange_price"));
                    System.out.println("--------------------------------------------------");
                }while(result.next());
            }
            supplierMenu(in, supplier);
        }
        catch(Exception e){
            System.out.println("While getting the inventory something went wrong");
            supplierMenu(in, supplier);
        }
    }
}



/*
    -check orders to make sure they have things in them 
    -check who is supplying the suppliers
    -check what goes into certain products
*/
class Analyst extends Project{
    public static void main(){
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome Regork Analyst what would you like to do?");
        System.out.println("[1] Check False Orders\n[2] Check what suppliers supply who\n[3] Check what products go into a product");
        analystMenu(in);
    }
    public static void analystMenu(Scanner in){
        int ans = Project.IntScan(in);
        if(ans == 1){
            checkingOrders(in);
        }
        else if(ans == 2){
            checkingSuppliers(in);
        }
        else if(ans == 3){
            checkingProducts();
        }
        else if(ans == 4){
            Project.Menu();
        }
        else{
            System.out.println("You did not select a proper value");
            analystMenu(in);
        }
    }

    public static void checkingOrders(Scanner in){
        try{
            String find = "select tracking_num from orders minus select tracking_num from contains";
            ResultSet result = s.executeQuery(find);
            System.out.println("These are the Orders that have no products in them");
            if(!result.next()){
                System.out.println("Something went horribly wrong with printing the order nums");
            }
            else{
                System.out.println("--------------------------------------------------");
                do{
                    System.out.println("Tracking Number: " + result.getString("tracking_num"));
                    System.out.println("--------------------------------------------------");
                }while(result.next());
            }
        }
        catch(Exception e){
            System.out.println("Something went wrong with checking orders");
            analystMenu(in);
        }
        analystMenu(in);
    }
    /*
    
    select * from supplying where supplier_id = X
    */
    public static void checkingSuppliers(Scanner in){
        System.out.println("Enter a supplier you wish to see who supplies them: ");
        String supplier = Project.StringScan(in);
        String find = "select supplier_id, name from supplier where name likea " + supplier;
    }

    /*
    select start_product from into_goods where end_product = X
    */
    public static void checkingProducts(){

    }
}

