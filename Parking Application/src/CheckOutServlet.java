import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import redis.clients.jedis.Jedis;
import com.google.gson.Gson;

public class CheckOutServlet extends HttpServlet {
    String str;
    JSONObject jsonObject = new JSONObject();
    static Jedis jedis = new Jedis("redis://localhost:6379");
    static Gson gson = new Gson();
    static String json;
    static {
        System.out.println("\n\n\n\n\n********check out servlet is Loaded*******\n\n\n\n\n");
    }

    static long
    findDifference(String start_date,
                   String end_date)
    {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        // Try Block
        try {

            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;


            System.out.print(
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");
            long val = (((difference_In_Years*365)+(difference_In_Days))*24);
            val = val+difference_In_Hours;
            val = val*60;
            return (val+difference_In_Minutes);
        }
        // Catch the Exception
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
        public void init() throws ServletException
        {
            System.out.println("\n\n\n\n\n********check out Servlet is initiated*******\n\n\n\n\n");
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
            if(req.getParameter("choice").equals("2")) {
                tokens[0] = req.getParameter("choice");
                tokens[1] = req.getParameter("vehicleNumber");
                tokens[2] = req.getParameter("checkOutTime");

                int i,j;
                int isPresentCheckout=0;
                for(i=0;i<jedis.llen("LotDetails");i++) {
                    json = jedis.lindex("LotDetails", i);
                    //System.out.println(json);
                    CarDetails LotDetails1 = gson.fromJson(json, CarDetails.class);
                    if(LotDetails1.getNumber()==Integer.parseInt(tokens[1]))
                    {
                        String[] dateTime=tokens[2].split("\\s+");
                        String[] date=dateTime[0].split("-");
                        String[] time=dateTime[1].split(":");
                        LotDetails1.setExitDay(Integer.parseInt(date[0]));
                        LotDetails1.setExitMonth(Integer.parseInt(date[1]));
                        LotDetails1.setExitYear(Integer.parseInt(date[2]));
                        if(dateTime[2].equals("PM"))
                        {
                            LotDetails1.setExitHour(Integer.parseInt(time[0])+12);
                        }
                        else
                        {

                            LotDetails1.setExitHour(Integer.parseInt(time[0]));
                        }
                        LotDetails1.setExitMinute(Integer.parseInt(time[1]));
                        LotDetails1.setExitZone(dateTime[2]);

                        String start_date
                                =LotDetails1.getEntryDay()+"-"+LotDetails1.getEntryMonth()+"-"+LotDetails1.getEntryYear()+" "+LotDetails1.getEntryHour()+":"+LotDetails1.getEntryMinute()+":00"; //"31-10-2021 05:00:00";

                        // Given end Date
                        String end_date
                                =LotDetails1.getExitDay()+"-"+LotDetails1.getExitMonth()+"-"+LotDetails1.getExitYear()+" "+LotDetails1.getExitHour()+":"+LotDetails1.getExitMinute()+":00"; // "31-10-2021 07:30:00";

                        // Function Call
                        System.out.print("Success:Vehicle("+LotDetails1.getType()+","+LotDetails1.getNumber()+")"+" is available in "+LotDetails1.getLot()+" Total Duration:");

                        long val = findDifference(start_date, end_date);
                        int value=(int)val;
                        int cost=10;
                        if(LotDetails1.getType().equals("Car"))
                            cost=50;
                        else if(LotDetails1.getType().equals("Cycle"))
                            cost=6;
                        double c = (double)cost/60;
                        c=Math.round(value*c*100.0)/100.0;
                        System.out.println(" Amount to be paid:Rs: "+c);
                        //System.out.println("\n\n*********************************************************checkOutServlet\n\n");
                        jsonObject.put("status","1");
                        jsonObject.put("message","The Vehicle "+LotDetails1.getNumber()+" is checked out successfully ");
                        String res1="The parking duration of ";
                        if((value/60)>0){
                            res1=res1+(value/60)+" hours ";}
                        if((value%60)>0){
                            res1=res1+(value%60)+" minutes ";}
                        jsonObject.put("cost","The parking duration of "+ res1 + "costs Rs."+c);


                        System.out.println(jedis.llen(Character.toString(LotDetails1.getLot().charAt(0))));
                        System.out.println(jedis.lset(Character.toString(LotDetails1.getLot().charAt(0)),(LotDetails1.getLot().charAt(1)-'0')-1,"0"));


                        jedis.srem("VehicleNumber",Integer.toString(LotDetails1.getNumber()));

                        ParkingHistory history = new ParkingHistory(LotDetails1.getType(),LotDetails1.getNumber(),LotDetails1.getEntryDay(),LotDetails1.getEntryMonth(),LotDetails1.getEntryYear(),LotDetails1.getEntryHour(),LotDetails1.getEntryMinute(),LotDetails1.getEntryZone(),LotDetails1.getExitDay(),LotDetails1.getExitMonth(),LotDetails1.getExitYear(),LotDetails1.getExitHour(),LotDetails1.getExitMinute(),LotDetails1.getExitZone(),LotDetails1.getLot(),value,c);
                        json = gson.toJson(history);
                        System.out.println("Pushing to Jedis "+jedis.rpush("ParkHistory", json));
                        jedis.lset("LotDetails",i,"Invalid");
                        jedis.lrem("LotDetails",1,"Invalid");
                        isPresentCheckout=1;
                    }
                }

                //System.out.println("\n\n*********************************************************checkOutServlet4\n\n");
                if(isPresentCheckout==0){
                    System.out.println("Please Apologize us! There is no vehicle in the given number "+tokens[1]);
                    jsonObject.put("status","0");
                    jsonObject.put("message","Please Apologize us! There is no vehicle in the given number "+tokens[1]);
                }
                //System.out.println("\n\n*********************************************************checkOutServlett\n\n");
                System.out.println(jsonObject);
                out.println(jsonObject);
            }
            //System.out.println("choice is =  "+req.getParameter("choice"));
            //System.out.println("Vehicle Number = "+req.getParameter("vehicleNumber")+"\nVehicle Type = "+req.getParameter("vehicleType")+"\nLot = "+req.getParameter("lot")+"\nCheck In Time"+req.getParameter("checkInTime"));
            res.setContentType("text/html");
        }

        @Override
        public void destroy() {
            System.out.println("\n\n\n\n\n********Destroyed*******\n\n\n\n\n");

        }
}
