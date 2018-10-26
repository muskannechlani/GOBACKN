import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
	static public int windowsize=4;
	static 		String message[] = {"Accept","Best", "Wishes"," Server","For","Future","Luck","And","Stay","Healthy,","Calm,","Sound"};

	public static void main(String[] args) {

		
		OutputStream ostream = null;
		boolean loop=true;
		Socket sock=null;
		int ack=-1;
		String ackk="";
		DataOutputStream dos=null;
		Scanner s=new Scanner(System.in);
		int sent=0;
		try {
			 sock = new Socket("127.0.0.1", 3000);// localhost="127.0.0.1" 
			 System.out.println("************************Connection established***************************");
			 ostream = sock.getOutputStream();
			 dos = new DataOutputStream(ostream);
			while(loop)
			{
				
				for(int i=0;i<windowsize;i++)			//sending frames equal to window size
				{
					if(sent<message.length){
						
						dos.writeUTF(message[sent]); 
						System.out.println("Frame "+i+" sent:\t"+message[sent]);
						sent++;
					}
					else
						break;
				}
				 InputStream istream = sock.getInputStream();  
	    	     DataInputStream dstream = new DataInputStream(istream);
	    	
	    	     while(!sock.isClosed() && (ackk=dstream.readUTF())==null) 			//to wait for acknowledgement as receiver window is 1.it will take time to receive
	    	     {
	    	    	 
	    	     }
	    	     ack=Integer.parseInt(ackk);
	    	     System.out.println("Last acknowledgement received: "+ack);
	    	     if(ack==message.length)		//if it is the last acknowledgement completed data transfer
	    	     {
	    	    	 loop=false;
	    	     }
	    	     else if (ack!=sent)		//to reinitialize sent to last ack so that data is sent from that point again 
	    	     {
	    	    	 sent=ack;
	    	     }
	    	     //you will not enter in this if else if part if all the frames client sent are acceptable to server that means you can keep going!	 
	    	   
			}
			System.out.println("Completed Transfer!");
			dos.close();
			 ostream.close();  
		     sock.close();
		      System.out.println("************************Connection terminated***************************");

		} catch (IOException e) {
			
			e.printStackTrace();
			
		     
		}                
		  
		
	    
	}
}
/*Output:
************************Connection established***************************
Frame 0 sent:	Accept
Frame 1 sent:	Best
Frame 2 sent:	Wishes
Frame 3 sent:	 Server
Last acknowledgement received: 2
Frame 0 sent:	Wishes
Frame 1 sent:	 Server
Frame 2 sent:	For
Frame 3 sent:	Future
Last acknowledgement received: 2
Frame 0 sent:	Wishes
Frame 1 sent:	 Server
Frame 2 sent:	For
Frame 3 sent:	Future
Last acknowledgement received: 6
Frame 0 sent:	Luck
Frame 1 sent:	And
Frame 2 sent:	Stay
Frame 3 sent:	Healthy,
Last acknowledgement received: 10
Frame 0 sent:	Calm,
Frame 1 sent:	Sound
Last acknowledgement received: 12
Completed Transfer!
************************Connection terminated***************************

*/