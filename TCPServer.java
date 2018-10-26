import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

public class TCPServer {

	public static void main(String[] args) throws SocketException {
		
		ServerSocket sersock;
		InputStream istream = null;  
		DataInputStream dstream = null;
		DataOutputStream     dos =null;
		int flag=0;
		Socket sock=null;
		try {
			sersock = new ServerSocket(3000);
			System.out.println("************************Connection established***************************");
			System.out.println("Server  ready for accepting");
			sock = sersock.accept( );   
			int windowsize=1,rcvd=0,k=-1,skip=0;      
			OutputStream	 ostream =null;
			Random r=new Random();   //to discard a frame randomly
			boolean loop=true;		 //to continue data transfer till loop=false
			String message[]=new String[TCPClient.message.length];		//Received data to be stored in this!
			istream = sock.getInputStream();  
			dstream = new DataInputStream(istream);
			while(loop)															//Data Transfer Starts
			{

				if(rcvd!=0) 						//First frame cannot be discarded because it will lead to infinte loop 
					k=r.nextInt(rcvd+windowsize);				//k = value of frame to be discarded
				
				if(rcvd<message.length)								//rcvd keeps track of no of frames arrived,if it is less than total frames loop will continue
				{
					if(rcvd==k && k < message.length){ 			//indicating we have to skip this frame
						flag=1;									//setting this flag will help us to determine what number to send as ack
					}

					else
					{ 

						String message2 = dstream.readUTF();	//read message from client
						message[rcvd]=message2;					//store data
						System.out.println("Succesfully received frame "+skip+":\t"+message[rcvd]);
						skip++;									//this will help us in determine how many frames to discard if we don't receive the frame desired
						rcvd++;									//increase the count of frames arrived
						flag=0;									//to indicate data has arrived safely
					}
				}
				else
				{
					loop=false;
					//"Loop will break after this"
				}

//There are two conditions for sending acknowlegdement:1)When you don't receive a frame desired(flag==1) 2)All frames sent by client are received.To send a cumulative acknowledgement(skip=windowsize of client)


				if(flag==1 || skip==TCPClient.windowsize)
				{
					ostream = sock.getOutputStream();                
					dos = new DataOutputStream(ostream);
					System.out.println("Last Acknowledgement: "+rcvd);	
					if(!sock.isClosed())							//To check if connection still exists!
						dos.writeUTF(String.valueOf(rcvd));			//send ack

					String message2;
					//------------------------------------DISCARDING FRAMES-----------------------------------------
					if(rcvd<(message.length-TCPClient.windowsize))			//this indicates sender has send frames equal to window size
					{		
						while(skip<TCPClient.windowsize){
							message2 = dstream.readUTF();
							System.out.println("Discarding frame "+skip+ ":\t"+message2);
							skip++;
						}
						skip=0;	 //skip should be less than windowsize always so initializing it.
					}
					else		//if sender is about to complete data transfer lastly sent frames will be less than window size 
					{
						
						while(skip<(message.length-rcvd))		//skipping remaining frames
						{
							message2 = dstream.readUTF();
							System.out.println("Discarding frame "+skip+ ":\t"+message2);
							skip++;

						}
						skip=0;
					}
				}
				
			}
			//to tell client that data is complete the last acknowledgement is to be sent.
			if(!sock.isClosed())
				dos.writeUTF(String.valueOf(rcvd));
			System.out.println("Displaying Data Received:");
			System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
			for(int i=0;i<message.length;i++)
			{
				System.out.print(message[i]+" ");
			}
			System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------------------");

			dstream .close(); 
			istream.close(); 
			sock.close();
			sersock.close();
			System.out.println("************************Connection terminated***************************");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
/*
OUTPUT:
************************Connection established***************************
Server  ready for accepting
-1  0
Succesfully received frame 0:	Accept
Reaches here
0  1
Succesfully received frame 1:	Best
Reaches here
2  2
Last Acknowledgement: 2
Discarding frame 2:	Wishes
Discarding frame 3:	 Server
Loop breaks at 4
Reaches here
2  2
Last Acknowledgement: 2
Discarding frame 0:	Wishes
Discarding frame 1:	 Server
Discarding frame 2:	For
Discarding frame 3:	Future
Loop breaks at 4
Reaches here
1  2
Succesfully received frame 0:	Wishes
Reaches here
0  3
Succesfully received frame 1:	 Server
Reaches here
2  4
Succesfully received frame 2:	For
Reaches here
2  5
Succesfully received frame 3:	Future
Last Acknowledgement: 6
Loop breaks at 4
Reaches here
3  6
Succesfully received frame 0:	Luck
Reaches here
0  7
Succesfully received frame 1:	And
Reaches here
5  8
Succesfully received frame 2:	Stay
Reaches here
0  9
Succesfully received frame 3:	Healthy,
Last Acknowledgement: 10
Skip4--2
Reaches here
6  10
Succesfully received frame 0:	Calm,
Reaches here
6  11
Succesfully received frame 1:	Sound
Reaches here
11  12
Loop will break after this:
Reaches here
Displaying Data Received:
-----------------------------------------------------------------------------------------------------------------------------------------
Accept Best Wishes  Server For Future Luck And Stay Healthy, Calm, Sound 
-----------------------------------------------------------------------------------------------------------------------------------------
************************Connection terminated***************************

 */