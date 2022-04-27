import redis.clients.jedis.Jedis;

public class RedisConnection {
    public static void main(String[] args) throws Exception
    {
        try{
            Jedis jedis = new Jedis("redis://localhost:6379");
            System.out.println("Connection Successful");
            System.out.println("Server Ping: "+jedis.ping());
            System.out.println("Server Info: "+jedis.info());;
            System.out.println(jedis.lindex("A",12));
            if(jedis.lindex("A",12)==null)
            {
                System.out.println("&&&YUIHAJN");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
