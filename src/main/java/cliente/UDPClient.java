package cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import exceptions.FalhaNaConexaoException;

public class UDPClient {

	private DatagramSocket socket;
	private InetAddress serverAddress;
	private int port;
	
	public UDPClient(String serverIP, int port) {
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(serverIP);
			this.port = port;
			socket.setSoTimeout(20000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendRequest(byte[] requisicao) throws Exception {
		DatagramPacket request = new DatagramPacket(requisicao, requisicao.length, serverAddress, port);
		socket.send(request);
	}
	
	public byte[] getReply() throws Exception {
	    int attempts = 3; // Número de tentativas
	    while (attempts > 0) {
	        try {
	            byte[] buffer = new byte[1024];
	            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
	            socket.receive(reply);
	            return Arrays.copyOf(reply.getData(), reply.getLength());
	        } catch (SocketTimeoutException e) {
	            attempts--;
	            if (attempts == 0) {
	                throw new FalhaNaConexaoException("Servidor não respondeu após várias tentativas.");
	            }
	        }
	    }
	    return null;
	}
	
	public void finaliza() {
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
