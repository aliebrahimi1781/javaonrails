import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class ServerTest {
	
	
	public static void main(String[] args) throws IOException {
		ServerSocketChannel ssc=ServerSocketChannel.open();
		ssc.socket().bind(new InetSocketAddress(8888));
		SocketChannel sc=ssc.accept();
	}
}
