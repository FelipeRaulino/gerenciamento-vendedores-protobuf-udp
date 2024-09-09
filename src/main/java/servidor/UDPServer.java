package servidor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import proto.VendedorOuterClass.Message;
import proto.VendedorOuterClass.GenericResponse;

public class UDPServer {
	
	public static void main (String args[]) {
		try {
			int serverPort = 7896;
			try (DatagramSocket socket = new DatagramSocket(serverPort)) {
				System.out.println("Servidor UDP rodando na porta " + serverPort);
				
				while (true) {
					byte[] buffer = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);
					Connection c = new Connection(socket, packet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class Connection extends Thread {
	DatagramSocket socket;
	DatagramPacket packet;
	VendedorDespachante despachante;
	
	public Connection(DatagramSocket socket, DatagramPacket packet) {
		this.socket = socket;
		this.packet = packet;
		despachante = new VendedorDespachante();
		this.start();
	}
	
	public byte[] getRequest() {
		return packet.getData();
	}
	
	public Message desempacotaRequisicao(byte[] array) {
		try {
			// Somente use os bytes relevantes
	        int length = packet.getLength();
	        byte[] relevantBytes = new byte[length];
	        System.arraycopy(array, 0, relevantBytes, 0, length);

	        // Desempacota a mensagem usando apenas os bytes relevantes
	        return Message.parseFrom(relevantBytes);
		} catch (Exception e) {
			System.out.println("Erro ao desempacotar a requisição: " + e.getMessage());
			return null;
		}
	}
	
	public byte[] empacotaResposta(byte[] resultado, int requestId) {
		try {
			// Cria uma resposta genérica que inclui o resultado e o ID da requisição original
	        Message response = Message.newBuilder()
	            .setType(2) // Indica que é uma resposta
	            .setId(requestId) // Associa com a requisição original
	            .setObfReference("VendedorServente") // Exemplo de referência ao serviço
	            .setMethodId("resposta") // Pode ser ajustado conforme necessário
	            .setArguments(com.google.protobuf.ByteString.copyFrom(resultado)) // Inclui o resultado
	            .build();

	        // Serializa a resposta em bytes
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        response.writeTo(byteArrayOutputStream);
	        return byteArrayOutputStream.toByteArray(); // Retorna os bytes da resposta
		} catch (IOException e) {
			System.out.println("Erro ao empacotar a resposta: " + e.getMessage());
			return null;
		}
	}	

	public void sendReply(byte[] resposta) {
		try {
			System.out.println("Enviando resposta ao cliente...");
			DatagramPacket replyPacket = new DatagramPacket(resposta, resposta.length, packet.getAddress(), packet.getPort());
			socket.send(replyPacket);
	        System.out.println("Resposta enviada.");
		} catch (Exception e) {
			System.out.println("Erro ao enviar resposta: " + e.getMessage());
		}
	}
	
	public void run() {
		Message requisicao = desempacotaRequisicao(getRequest());
	    if (requisicao != null) {
	        byte[] resultado = despachante.selecionaEsqueleto(requisicao.toByteArray());
	        sendReply(empacotaResposta(resultado, requisicao.getId()));
	    } else {
	        System.out.println("Requisição inválida recebida.");
	    }
	}
	
}
