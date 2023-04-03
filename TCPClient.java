import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

public class TCPClient {

	static String instruct(String input, DataOutputStream dout, BufferedReader din){ 
		try {
		dout.write((input+"\n").getBytes()); //Send message to server
		dout.flush(); //flush forces any buffered output bytes to be written out to the stream
		//System.out.println("SENT: "+input);
		String received = (String)din.readLine(); //recieve message from server
		//System.out.println("RCVD: "+received);
		return received; //return for use by string variable rec
		}	catch(Exception e){System.out.println(e);}
		return ""; 
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
			String rec= ""; //received String for use outside of instruct function
			String serverType = "";
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
				
			serverInfo = new String [Integer.parseInt(dataInfo[1])][]; //Create a 2D array of size dataInfo[1] - which is amount of servers
			rec = instruct("OK", dout, din); //Send OK, Receive Server Info
			serverInfo[0] = rec.split(" "); //Takes received from function sets it to first index
			for (int i = 1; i < serverInfo.length;i++){ // Takes everything else and sets to following indexes;
				rec = (String)din.readLine();
				serverInfo[i] = rec.split(" ");
			}

			for (int i = 0; i < serverInfo.length;i++){ //Algorithm for finding the largest server
				if (Integer.parseInt(serverInfo[i][4])>serverCore){ //check for new largest server size using core amount
					typeNumber = 1; //reset amount of largest servers if new largest server size
					serverType = serverInfo[i][0]; //Grabs server Type Name
					serverCore = Integer.parseInt(serverInfo[i][4]); //set new core amount to compare against
				}
				else { //Add to amount of largest servers
					if (serverType.equals(serverInfo[i][0])){
						typeNumber++;
					}
				}
			}

			instruct("OK", dout, din); //Send OK, Receive .

			while (jobInfo[0].equals("NONE")==false){ //Only do loop if there are jobs
				if (jobInfo[0].equals("JOBN")){ //Only schedule if JOBN
					if (serverID == typeNumber) serverID=0; //LRR - reset to 0 if SCHD has gone through all servers of same type.
					instruct("SCHD "+ jobInfo[2]+" " +serverType+" "+serverID, dout, din); //Sends SCHD + Job ID + Largest Server Type + ServerID, Recieve OK
					serverID++; //Increment so next job is on next server of same type
					}
				rec = instruct("REDY", dout, din); //Send REDY, Receive next job
				jobInfo = rec.split(" ");					
				}
				
				instruct("QUIT", dout, din); //Send QUIT, Receive QUIT
			    
			    din.close(); //close input stream
			    dout.close(); //close output stream
			    s.close(); //close socket

		}catch(Exception e){System.out.println(e);}
	}
}
