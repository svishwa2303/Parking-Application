import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import redis.clients.jedis.Jedis;
public class Parking {

    static Jedis jedis = new Jedis("redis://localhost:6379");
    static Gson gson = new Gson();
    static String json;

    // FUNCTION TO START REDIS CONNECTION
    public static void redisConnection() throws Exception{
        try{
            System.out.println("Connection Successful");
            System.out.println("Server Ping: "+jedis.ping());
            System.out.println("Server Info: "+jedis.info());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // FIND VEHICLE FUNCTION

    public static JSONObject findVehicle(String[] tokens)
    {
        JSONObject jsonObject = new JSONObject();
        String parkingStatus="0";
        String parkingHistoryStatus="0";
        int i,j;
        int isPresent=0;
        System.out.println("******************************************************************Calling findVehicle\n\n\n\n");

        /*   CODE WITH REDIS*/

        for(i=0;i<jedis.llen("LotDetails");i++) {
            json = jedis.lindex("LotDetails", i);
            //System.out.println(json);
            CarDetails LotDetails1 = gson.fromJson(json, CarDetails.class);
            if(LotDetails1.getNumber()==Integer.parseInt(tokens[1]))
            {
                System.out.println("Success:Vehicle("+LotDetails1.getType()+","+LotDetails1.getNumber()+")"+" is available in "+LotDetails1.getLot());
                isPresent=1;
                parkingStatus="1";
                jsonObject.put("INPARKING","IN PARKING");
                String entryTime = LotDetails1.getEntryDay()+"-"+LotDetails1.getEntryMonth()+"-"+LotDetails1.getEntryYear()+" "+LotDetails1.getEntryHour()+":"+LotDetails1.getEntryMinute()+" "+LotDetails1.getEntryZone();
                jsonObject.put("INPARKINGVal","Vehicle "+LotDetails1.getNumber()+" is parked at the "+LotDetails1.getLot()+" lot at "+entryTime);

            }
        }

        /*   CODE WITH REDIS UNTIL*/

        if(isPresent==0) {
            jsonObject.put("parkingStatus","0");
            jsonObject.put("INPARKING","");
            jsonObject.put("INPARKINGVal","");
        }
        else{

            jsonObject.put("parkingStatus","1");
        }
        int hasParkingHistory=0;
        //System.out.println("Parking History of "+Integer.parseInt(tokens[1]));
        int parkingHisCountForFive=0;
        JSONArray jsonObjectParkingHistory = new JSONArray();
        /* Starting search on Redis */
        for(i=(int)jedis.llen("ParkHistory")-1;i>=0;i--)
        {
            JSONArray ja = new JSONArray();
            json = jedis.lindex("ParkHistory",i);
            //System.out.println(json);
            ParkingHistory ParkHistory1 = gson.fromJson(json, ParkingHistory.class);
            if(ParkHistory1.getNumber()==Integer.parseInt(tokens[1]))
            {
                hasParkingHistory=1;
                if(parkingHisCountForFive<5)
                {
                    String entryDate=ParkHistory1.getEntryDay()+"-"+ParkHistory1.getEntryMonth()+"-"+ParkHistory1.getEntryYear();
                    String exitDate=ParkHistory1.getExitDay()+"-"+ParkHistory1.getExitMonth()+"-"+ParkHistory1.getExitYear()+" "+ParkHistory1.getExitHour()+":"+ParkHistory1.getExitMinute()+" "+ParkHistory1.getExitZone();
                    ja.put(ParkHistory1.getLot());
                    ja.put(entryDate);
                    entryDate=entryDate+" "+ParkHistory1.getEntryHour()+":"+ParkHistory1.getEntryMinute()+" "+ParkHistory1.getEntryZone();
                    ja.put(entryDate);
                    ja.put(exitDate);
                    parkingHisCountForFive++;
                    jsonObjectParkingHistory.put(ja);
                    System.out.println(ja);
                }

            }
        }

        /* Redis upto this */

        if(isPresent==0 && hasParkingHistory==1)
        {

            jsonObject.put("INPARKING","IN PARKING");
            jsonObject.put("INPARKINGVal","*Currently, Vehicle "+tokens[1]+" is not checked in");
        }
        if(hasParkingHistory==1)
        {
            System.out.println("Vehicle has previous parking history");
            parkingHistoryStatus="1";
            jsonObject.put("parkingHisCountForFive",parkingHisCountForFive);
            jsonObject.put("parkingHistory",jsonObjectParkingHistory);
            jsonObject.put("parkingHistoryStatus","1");
        }
        if(hasParkingHistory==0)
        {
            System.out.println("Vehicle has no parking history");
            jsonObject.put("parkingHistoryStatus","0");
            jsonObject.put("parkingHisCountForFive","0");
        }


        return jsonObject;
    }

    // PRINT ALL VEHICLE

    public static void printAllVehicle()
    {
        int i;

        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        for(i=0;i<jedis.llen("LotDetails");i++) {
            json = jedis.lindex("LotDetails", i);
            //System.out.println(json);
            CarDetails LotDetails1 = gson.fromJson(json, CarDetails.class);
            System.out.println("Number : "+LotDetails1.getNumber());
            System.out.println("Type : "+LotDetails1.getType());
            System.out.print("Entry Time : "+LotDetails1.getEntryDay()+"-");
            System.out.print(LotDetails1.getEntryMonth()+"-");
            System.out.print(LotDetails1.getEntryYear()+" ");
            System.out.print(LotDetails1.getEntryHour()+":");
            System.out.print(LotDetails1.getEntryMinute()+" ");
            System.out.println(LotDetails1.getEntryZone());
            System.out.print("Exit Time : "+LotDetails1.getExitDay()+"-");
            System.out.print(LotDetails1.getExitMonth()+"-");
            System.out.print(LotDetails1.getExitYear()+" ");
            System.out.print(LotDetails1.getExitHour()+":");
            System.out.print(LotDetails1.getExitMinute()+" ");
            System.out.println(LotDetails1.getExitZone());
            System.out.println("Lot : "+LotDetails1.getLot());
        }

        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

    }


    // PRINT PARKING HISTORY FUNCTION
    public static void printAllVehicleParkingHistory()
    {
        int i;
        System.out.println("/////////////////////");

        System.out.println("/////////////////////\n\n");

        System.out.println("/////////////////////                  values from redis");
        for(i=0;i<jedis.llen("ParkHistory");i++)
        {
            json = jedis.lindex("ParkHistory",i);
            ParkingHistory ParkHistory1 = gson.fromJson(json, ParkingHistory.class);
            System.out.println("\n");
            System.out.println("Number : "+ParkHistory1.getNumber());
            System.out.println("Type : "+ParkHistory1.getType());
            System.out.print("Entry Time : "+ParkHistory1.getEntryDay()+"-");
            System.out.print(ParkHistory1.getEntryMonth()+"-");
            System.out.print(ParkHistory1.getEntryYear()+" ");
            System.out.print(ParkHistory1.getEntryHour()+":");
            System.out.print(ParkHistory1.getEntryMinute()+" ");
            System.out.println(ParkHistory1.getEntryZone());
            System.out.print("Exit Time : "+ParkHistory1.getExitDay()+"-");
            System.out.print(ParkHistory1.getExitMonth()+"-");
            System.out.print(ParkHistory1.getExitYear()+" ");
            System.out.print(ParkHistory1.getExitHour()+":");
            System.out.print(ParkHistory1.getExitMinute()+" ");
            System.out.println(ParkHistory1.getExitZone());
            System.out.println("Lot : "+ParkHistory1.getLot());
            System.out.println("Cost : "+ParkHistory1.getTotalCost());
            System.out.println("\n");
        }

        System.out.println("/////////////////////\n\n");

    }
    public static void  main(String[] args)  {
        File f = new File("D:\\input.txt");

    }
}
