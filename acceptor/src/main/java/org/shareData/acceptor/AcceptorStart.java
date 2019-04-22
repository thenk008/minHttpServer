package org.shareData.acceptor;

/**
 * Hello world!
 *
 */
public class AcceptorStart 
{
    public static void main( String[] args )
    {
    	//Thread th = new Thread(new MyHeat());
    	Server server = new Server();
    	//th.start();
    	server.acceptorStart(Long.parseLong(args[0]));
    	//server.acceptorStart(8063L);
    }
}
