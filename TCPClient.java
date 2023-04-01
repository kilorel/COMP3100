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
			    //InetAddress aHost = InetAddress.getByName(args[0]);
			    //int aPort = Integer.parseInt(args[1]);
			    //Socket s = new Socket(aHost,aPort);

				//Set Socket
				Socket s = new Socket("127.0.0.1",50000);

				//Output
			    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				//Input
			    BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
				//Username
				String username = System.getProperty("user.name");
			    
				//Ip Addresses and Ports
			    //System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
			    //System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());	
			    
				//Send Hello
			    dout.write(("HELO\n").getBytes());
				dout.flush();
			    //System.out.println("SENT: HELO");
			    
			    String str = (String)din.readLine();
			    //System.out.println("RCVD: " +str);

				//Send Auth
				dout.write(("AUTH "+username+"\n").getBytes());
				dout.flush();
				//System.out.println("SENT:AUTH "+username);

				str = (String)din.readLine();
			    //System.out.println("RCVD: "+str);

			

				//Send Ready
				dout.write(("REDY\n").getBytes());
				dout.flush();
				//System.out.println("SENT: REDY");
				
				str = (String)din.readLine();
				//System.out.println("RCVD: "+str);	

				String [] jobInfo = str.split(" ");
	
				
				
				//Send Gets
				dout.write(("GETS All\n").getBytes());
				dout.flush();
				//System.out.println("SENT: GETS All");

				str = (String)din.readLine();
				//System.out.println("RCVD: "+str);

				String [] dataInfo = str.split(" ");

				//Send OK
				dout.write(("OK\n").getBytes());
				dout.flush();
				//System.out.println("SENT: OK");


				//ServerInfo
				String [][] serverInfo = new String [Integer.parseInt(dataInfo[1])][];

				//ServerType
				for (int i = 0; i < Integer.parseInt(dataInfo[1]);i++){
					str = (String)din.readLine();
					//System.out.println("RCVD: "+str);
					serverInfo[i] = str.split(" ");
				}

				String serverType;
				int serverID;
				int serverCore;
				int typeNumber;

				serverType = " ";
				serverID = 0;
				serverCore = 0;
				typeNumber = 0;

				for (int i = 0; i < serverInfo.length;i++){
					if (Integer.parseInt(serverInfo[i][4])>serverCore){
						typeNumber = 1;
						serverType = serverInfo[i][0];
						serverID = Integer.parseInt(serverInfo[i][1]);
						serverCore = Integer.parseInt(serverInfo[i][4]);
					}
					else {
						if (serverType.equals(serverInfo[i][0])){
							typeNumber++;
						}
					}
				}

				//System.out.println("Server Type: "+ serverType);
				//System.out.println("Server ID: "+serverID);
				//System.out.println("No. of Servers: "+typeNumber);


				//Send OK
				dout.write(("OK\n").getBytes());
				dout.flush();
				//System.out.println("SENT: OK");

				str = (String)din.readLine();
				//System.out.println("RCVD: "+str);

				//SCHD
				while (jobInfo[0].equals("NONE")==false){
					if (jobInfo[0].equals("JOBN")){
						if (serverID == typeNumber) serverID=0;
						dout.write(("SCHD "+ jobInfo[2]+" " +serverType+" "+serverID+"\n").getBytes());
						dout.flush();
						//System.out.println("SENT: SCHD");
						//System.out.println("Running Task "+jobInfo[2]+" on "+serverType+" "+serverID);
						serverID++;

						str = (String)din.readLine();
						//System.out.println("RCVD: "+str);
					}
					//Send Ready
					dout.write(("REDY\n").getBytes());
					dout.flush();
					//System.out.println("SENT: REDY");
				
					str = (String)din.readLine();
					//System.out.println("RCVD: "+str);	
					
					jobInfo = str.split(" ");					
				}
				
				//QUIT
				dout.write(("QUIT\n").getBytes());
				dout.flush();
				//System.out.println("SENT: QUIT");
	
				str = (String)din.readLine();
				//System.out.println("RCVD: "+str);



			    
			    din.close();
			    dout.close();
			    s.close();
			}
			catch(Exception e){System.out.println(e);}
		
	}
} 

