import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class SocketTest {
	public static void main(String[] args) throws UnknownHostException, IOException {
		SocketChannel sc=SocketChannel.open(new InetSocketAddress("127.0.0.1",8888));
		for(;;){
		System.out.println(sc.read(ByteBuffer.allocate(4)));
		}
	}
}
