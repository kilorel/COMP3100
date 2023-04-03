import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

public class TCPClient {

	static String instruct(String input, DataOutputStream dout, BufferedReader din){ 
		try {
		dout.write((input+"\n").getBytes());
		dout.flush();
		System.out.println("SENT: "+input);
		String received = (String)din.readLine();
		System.out.println("RCVD: "+received);
		return received;
		}	catch(Exception e){System.out.println(e);}
		return " ";
	}

	public static void main(String[] args){
		try {
			//InetAddress aHost = InetAddress.getByName(args[0]); //Get IP Address
			//int aPort = Integer.parseInt(args[1]); //Get Port
			//Socket s = new Socket(aHost,aPort); //Create Socket Using Inputs
			Socket s = new Socket("127.0.0.1",50000); //Socket with set IP Address and Port Number				
			DataOutputStream dout = new DataOutputStream(s.getOutputStream()); //Stream to send messages to server
			BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream())); //Stream to receive messages from server
			String username = System.getProperty("user.name"); //Username grabbed from system
			String [] jobInfo;
			String [] dataInfo;
			String [][] serverInfo;
			String rec= " "; //received String
			String serverType = " ";
			int serverID = 0;
			int serverCore = 0;
			int typeNumber = 0; 
				
			//System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort()); //Print Target IP and Port
		    //System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort()); //Print Local IP and Port
				
			instruct("HELO", dout, din); //Send HELO, Receive OK

			instruct("AUTH "+username, dout, din); //Send AUTH, Receive OK

			rec = instruct("REDY", dout, din); //Send REDY, Receive Job Info
			jobInfo = rec.split(" "); //Create array of Job Info using received string after REDY

			rec = instruct("GETS All", dout, din); //Send GETS All, Receive Data Info
			dataInfo = rec.split(" "); //Create array of Data Info using received string after GETS All
				
			rec = instruct("OK", dout, din); //Send OK, Receive Server Info
			serverInfo = new String [Integer.parseInt(dataInfo[1])][]; //Create a 2D array of size dataInfo[1]()
			//Populate Server Info Array
			serverInfo[0] = rec.split(" "); //Takes first received from function
			for (int i = 1; i < Integer.parseInt(dataInfo[1]);i++){ // Takes everything else;
				rec = (String)din.readLine();
				serverInfo[i] = rec.split(" ");
			}

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

			System.out.println("Server Type: "+ serverType);
			System.out.println("Server ID: "+serverID);
			System.out.println("No. of Servers: "+typeNumber);

			instruct("OK", dout, din); //Send OK

			//SCHD
			while (jobInfo[0].equals("NONE")==false){
				if (jobInfo[0].equals("JOBN")){
					if (serverID == typeNumber) serverID=0;
					dout.write(("SCHD "+ jobInfo[2]+" " +serverType+" "+serverID+"\n").getBytes());
					dout.flush();
					//System.out.println("SENT: SCHD");
					//System.out.println("Running Task "+jobInfo[2]+" on "+serverType+" "+serverID);
					serverID++;

					rec = (String)din.readLine();
					//System.out.println("RCVD: "+str);
				}

				rec = instruct("REDY", dout, din); //Send REDY, Receive Job Info
				jobInfo = rec.split(" ");					
				}
				
				//QUIT
				instruct("QUIT", dout, din); //Send QUIT, Receive QUIT
			    
			    din.close();
			    dout.close();
			    s.close();
			
		}catch(Exception e){System.out.println(e);}
		
	}

}
