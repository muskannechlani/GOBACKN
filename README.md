# GOBACKN
This is sliding window protocol-GO BACK N.
RECEIVER Window size is always one in GO BACK N.
Cumulative Ack – One acknowledgement is used for many packets. Main advantage is traffic is less. 
GBN uses Cumulative Acknowledgement.
In My Code client has data to send.(Sender)
Server will be accepting data.(Receiver)
Receiver i.e Server has window size 1 ,so it accepts one frame at a time.
On the other hand client sends frames equal to its window size.
Receiver uses random function to discard a frame,everytime and sends ack to client which waits after it has sent frames=its windowsize.
If one frame is decided to be discarded,further frames are also discarded until it is received again.
In GO BACK N ,if receiver wants a desired frame,it will discard all until it gets it.
