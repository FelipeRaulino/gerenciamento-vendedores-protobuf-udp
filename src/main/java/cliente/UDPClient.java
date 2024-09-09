package cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UDPClient {

	private DatagramSocket socket;
	private InetAddress serverAddress;
	private int port;
	
	public UDPClient(String serverIP, int port) {
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(serverIP);
			this.port = port;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendRequest(byte[] requisicao) throws Exception {
		DatagramPacket request = new DatagramPacket(requisicao, requisicao.length, serverAddress, port);
		socket.send(request);
	}
	
	public byte[] getReply() throws Exception {
	    byte[] buffer = new byte[4096];
	    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
	    socket.receive(reply);
	    
	    // Verifique o tamanho da mensagem recebida
	    System.out.println("Bytes recebidos: " + reply.getLength());
	    return Arrays.copyOf(reply.getData(), reply.getLength()); // Retorne apenas os bytes relevantes
	}
	
	public void finaliza() {
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
