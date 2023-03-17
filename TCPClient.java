import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.io.DataOutputStream;
//import java.util.concurrent.TimeUnit;

public class TCPClient {
	public static void main(String[] args){

			try{
			    InetAddress aHost = InetAddress.getByName(args[0]);
			    int aPort = Integer.parseInt(args[1]);
			    Socket s = new Socket(aHost,aPort);
				//Socket s = new Socket("127.0.0.1",50000);
				//DataInputStream din = new DataInputStream(s.getInputStream());
			    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			    BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String username = System.getProperty("user.name");
			    
			    System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
			    System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());	
			    
			    dout.write(("HELO\n").getBytes());
				dout.flush();
			    System.out.println("SENT: HELO");
			    
			    String str = (String)din.readLine();
			    System.out.println("RCVD: " +str);

				dout.write(("AUTH "+username+"\n").getBytes());
				dout.flush();
				System.out.println("SENT:AUTH "+username);

				str = (String)din.readLine();
			    System.out.println("RCVD: "+str);

				dout.write(("REDY\n").getBytes());
				dout.flush();
			    System.out.println("SENT: REDY");

				str = (String)din.readLine();
			    System.out.println("RCVD: "+str);

				dout.write(("QUIT\n").getBytes());
				dout.flush();
			    System.out.println("SENT: QUIT");

				str = (String)din.readLine();
			    System.out.println("RCVD: "+str);			    

			    
			    din.close();
			    dout.close();
			    s.close();
			}
			catch(Exception e){System.out.println(e);}
		
	}
}

