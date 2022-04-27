import com.google.gson.Gson;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetSlotsCapacityServlet extends HttpServlet{
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

    public static int listLength(String key)
    {
        int len = 0;

        //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n\n\n\n\n\n");
        for(int i=0;i<10;i++)
        {
            //System.out.println("\n"+jedis.lindex(key,i));
            if(jedis.lindex(key,i)!=null && (jedis.lindex(key,i).equals(Integer.toString(0))!=true))
            {
                len++;
            }
        }

        return len;
    }
    @Override
    public void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        String[] tokens = new String[6];
        PrintWriter out = res.getWriter();
        JSONObject jsonObject = new JSONObject();
        int aCount=listLength("A");   //0;
        int bCount=listLength("B");    //0;
        int cCount=listLength("C");    //0;
        int dCount=listLength("D");    //0;
        System.out.println("\n\n\n\n*************************");
        if(aCount>0) {
            jsonObject.put("A",aCount);
            jsonObject.put("ACapcitiy",10-aCount);
        }
        else {
            jsonObject.put("A","-");
            jsonObject.put("ACapcitiy","-");
        }
        if(bCount>0) {
            jsonObject.put("B",bCount);
            jsonObject.put("BCapcitiy",10-bCount);
        }
        else {
            jsonObject.put("B","-");
            jsonObject.put("BCapcitiy","-");
        }
        if(cCount>0) {
            jsonObject.put("C",cCount);
            jsonObject.put("CCapcitiy",10-cCount);
        }
        else {
            jsonObject.put("C","-");
            jsonObject.put("CCapcitiy","-");
        }
        if(dCount>0) {
            jsonObject.put("D",dCount);
            jsonObject.put("DCapcitiy",10-dCount);
        }
        else {
            jsonObject.put("D","-");
            jsonObject.put("DCapcitiy","-");
        }
        //System.out.println(" ");
        //System.out.println("\n\n\n\n*************************");


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
