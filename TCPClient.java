import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class TCPClient {

	static String instruct(String input, DataOutputStream dout, BufferedReader din) {
		try {
			dout.write((input + "\n").getBytes()); // Send message to server
			dout.flush(); // flush forces any buffered output bytes to be written out to the stream
			System.out.println("SENT: " + input);
			String received = (String) din.readLine(); // recieve message from server
			System.out.println("RCVD: " + received);
			return received; // return for use by string variable rec
		} catch (Exception e) {
			System.out.println(e);
		}
		return "";
	}

	public static void main(String[] args) {
		try {
			// InetAddress aHost = InetAddress.getByName(args[0]); //Get IP Address
			// int aPort = Integer.parseInt(args[1]); //Get Port
			// Socket s = new Socket(aHost,aPort); //Create Socket Using Inputs
			Socket s = new Socket("127.0.0.1", 50000); // Socket with set IP Address and Port Number
			DataOutputStream dout = new DataOutputStream(s.getOutputStream()); // Stream to send messages to server
			BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream())); // Stream to receive
																								// messages from server
			String username = System.getProperty("user.name"); // Username grabbed from system
			String[] jobInfo;
			String[] dataInfo;
			String[][] serverInfo;
			String rec = ""; // received String for use outside of instruct function
			int serverCount = 0;

			instruct("HELO", dout, din); // Send HELO, Receive OK

			instruct("AUTH " + username, dout, din); // Send AUTH, Receive OK

			rec = instruct("REDY", dout, din); // Send REDY, Receive Job Info
			jobInfo = rec.split(" "); // Create array of Job Info using received string after REDY

			while (jobInfo[0].equals("NONE")==false) {
				if (jobInfo[0].equals("JOBN")) {
					rec = instruct("GETS Capable " + jobInfo[4] + " " + jobInfo[5] + " " + jobInfo[6], dout, din);// Gets data of servers that are capable of job

					dataInfo = rec.split(" "); // Create array of Data Info using received string after GETS All

					serverInfo = new String[Integer.parseInt(dataInfo[1])][]; // Create a 2D array of size dataInfo[1] which is amount of servers

					// Read all lines recieved from data
					dout.write(("OK\n").getBytes());
					dout.flush();
					System.out.println("SENT: OK");
					for (int i = 0; i < serverInfo.length; i++) {
						rec = (String) din.readLine();
						System.out.println("RCVD: " + rec);
						serverInfo[i] = rec.split(" ");
					}

					rec = instruct("OK", dout, din); // Send OK, Receive .

					// Schedules job in first available server
					boolean running = false;
					boolean checked = false;
					for (int i = 0; i < serverInfo.length; i++) {
						if (serverInfo[i][8].equals("0")) {
							instruct("SCHD " + jobInfo[2] + " " + serverInfo[i][0] + " " + serverInfo[i][1], dout, din);
							running = true;
							break;
						}
					}
					
					if (running == false){
						for (int i = 0; i < serverInfo.length; i++) {
							if (serverInfo[i][7].equals("0")) {
								instruct("SCHD " + jobInfo[2] + " " + serverInfo[i][0] + " " + serverInfo[i][1], dout, din);
								checked = true;
								break;
							}
						}
						}
					if (running == false && checked == false){
						instruct("SCHD " + jobInfo[2] + " " + serverInfo[0][0] + " " + serverInfo[0][1], dout, din);
					}

				}
				rec = instruct("REDY", dout, din); // Send REDY, Receive next job
				jobInfo = rec.split(" ");
			}
			instruct("QUIT", dout, din); // Send QUIT, Receive QUIT

			din.close(); // close input stream
			dout.close(); // close output stream
			s.close(); // close socket

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
