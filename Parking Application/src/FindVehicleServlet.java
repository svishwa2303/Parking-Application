import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
public class FindVehicleServlet extends HttpServlet{
    String str;
    JSONObject jsonObject = new JSONObject();
    static Jedis jedis = new Jedis("redis://localhost:6379");
    static Gson gson = new Gson();
    static String json;
    static{
        System.out.println("\n\n\n\n\n********Loaded*******\n\n\n\n\n");
    }
    public void init() throws ServletException
    {
        System.out.println("\n\n\n\n\n********Servlet is initiated*******\n\n\n\n\n");
        str="My first servlet displayed in Tomcat Server";
    }
    @Override
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException
    {
        res.setContentType("text/html");
        PrintWriter out=res.getWriter();
        out.println("<html><body bgcolor=white text=green>"+"wrong method"+"</body></html>" );
    }
    @Override
    public void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException
    {
        String[] tokens = new String[6];
        PrintWriter out=res.getWriter();



        tokens[0] = req.getParameter("choice");
        tokens[1] = req.getParameter("vehicleNumber");

        String parkingStatus="0";
        String parkingHistoryStatus="0";
        int i,j;
        int isPresent=0;
        System.out.println("******************************************************************Calling findVehicle\n\n\n\n");


        for(i=0;i<jedis.llen("LotDetails");i++) {
            json = jedis.lindex("LotDetails", i);
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
                    //jsonObject.put(numbersMap.get(parkingHisCountForFive),ja);
                    System.out.println(ja);
                }

            }
        }

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


        out.println(jsonObject);






        //System.out.println("choice is =  "+req.getParameter("choice"));
        //System.out.println("Vehicle Number = "+req.getParameter("vehicleNumber")+"\nVehicle Type = "+req.getParameter("vehicleType")+"\nLot = "+req.getParameter("lot")+"\nCheck In Time"+req.getParameter("checkInTime"));
        res.setContentType("text/html");
    }
    @Override
    public void destroy() {
        System.out.println("\n\n\n\n\n********Destroyed*******\n\n\n\n\n");

    }
}
