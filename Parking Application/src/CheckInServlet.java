import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
public class CheckInServlet extends HttpServlet
{
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
        if(req.getParameter("choice").equals("1")) {
            tokens[0] = req.getParameter("choice");
            tokens[1] = req.getParameter("vehicleNumber");
            tokens[2] = req.getParameter("vehicleType");
            tokens[3] = req.getParameter("checkInTime");
            tokens[4] = req.getParameter("lot");

            String[] dateTime=tokens[3].split("\\s+");
            String[] date=dateTime[0].split("-");
            String[] time=dateTime[1].split(":");

            if(jedis.sismember("VehicleNumber",tokens[1]))
            {

                jsonObject.put("status","0");
                jsonObject.put("message","The "+(Parking.findVehicle(tokens).get("INPARKINGVal")).toString().replace("is","is already"));
            }
            else if(jedis.exists(tokens[4]))
            {
                int l=0,cVal=0;
                for(l=0;l<10;l++)
                {
                    if(jedis.lindex(tokens[4],l)==null)
                    {
                        cVal=1;
                        break;
                    }
                    if(jedis.lindex(tokens[4],l).equals(Integer.toString(0)))
                    {
                        cVal++;
                    }

                }
                if(cVal==0) {
                    System.out.println("Please Apologize us! There is no space in the selected lot for " + tokens[1]);
                    jsonObject.put("status","0");
                    jsonObject.put("message","The lot "+tokens[4]+" is full ");
                }
                else
                {
                    l=0;
                    for(l=0;l<10;l++)
                    {
                        if(jedis.lindex(tokens[4],l)==null)
                        {
                            jedis.lpush(tokens[4],Integer.toString(l));
                            break;
                        }
                        else if(jedis.lindex(tokens[4],l).equals("0"))
                        {
                            jedis.lset(tokens[4],l-0,Integer.toString(l)+1);
                            break;
                        }
                    }
                    CarDetails ld = new CarDetails(tokens[2],Integer.parseInt(tokens[1]),Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]),Integer.parseInt(time[0]),Integer.parseInt(time[1]),dateTime[2],tokens[4]+(l+1));//(k+1));
                    json = gson.toJson(ld);
                    System.out.println("Pushing to Jedis "+jedis.rpush("LotDetails", json));
                    System.out.println("Success:Vehicle "+tokens[1]+" is parked at "+tokens[4]+(l+1));//(k+1));
                    jsonObject.put("status","1");
                    jsonObject.put("message","The Vehicle is parked at "+tokens[4]+(l+1));//(k+1));
                    jedis.sadd("VehicleNumber",tokens[1]);
                }
            }
            else
            {
                int hour=Integer.parseInt(time[0]);
                if(dateTime[2].equals("PM"))
                {
                    hour+=12;
                }
                CarDetails ld = new CarDetails(tokens[2],Integer.parseInt(tokens[1]),Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]),hour,Integer.parseInt(time[1]),dateTime[2],tokens[4]+"1");
                json = gson.toJson(ld);
                System.out.println("Pushing to Jedis "+jedis.rpush("LotDetails", json));
                System.out.println("Success:Vehicle "+tokens[1]+" parked at "+tokens[4]+"1");
                jsonObject.put("status","1");
                jsonObject.put("message","The Vehicle is parked at "+tokens[4]+"1");
                jedis.sadd("VehicleNumber",tokens[1]);
                int[] mapVal = new int[10];
                System.out.println(gson.toJson(mapVal));
                jedis.lpush(tokens[4],"1");
            }
            out.println(jsonObject);
        }
        else if(req.getParameter("choice").equals("5"))
        {
            System.out.println("print all vehicle parking history is called\n");
            Parking.printAllVehicleParkingHistory();
        }
        else if(req.getParameter("choice").equals("6"))
        {
            System.out.println("print all vehicle currently parking                                    \n");
            Parking.printAllVehicle();
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