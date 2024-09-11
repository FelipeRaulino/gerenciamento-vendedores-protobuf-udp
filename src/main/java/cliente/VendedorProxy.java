package cliente;

import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import exceptions.CPFInvalidoException;
import exceptions.DadosInvalidosException;
import exceptions.EmailInvalidoException;
import exceptions.FalhaNaConexaoException;
import exceptions.VendedorNaoEncontradoException;
import proto.VendedorOuterClass.Message;
import proto.VendedorOuterClass.QuantidadeVendasAbsolutasArgs;
import proto.VendedorOuterClass.QuantidadeVendasAbsolutasResponse;
import proto.VendedorOuterClass.RemoverVendedorArgs;
import proto.VendedorOuterClass.Vendedor;
import proto.VendedorOuterClass.AdicionarVendedorArgs;
import proto.VendedorOuterClass.ListarVendedoresArgs;
import proto.VendedorOuterClass.GenericResponse;
import proto.VendedorOuterClass.ListarVendedoresResponse;;


public class VendedorProxy {
	
	private int requestId = 0;
	private UDPClient udpClient;
	
	public VendedorProxy() {
		try {
			udpClient = new UDPClient("localhost", 7896);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String adicionarVendedor(Vendedor vendedor) throws Exception {
	    AdicionarVendedorArgs reqArgs = AdicionarVendedorArgs.newBuilder().setVendedor(vendedor).build();

	    byte[] responseBytes = doOperation("VendedorService", "adicionarVendedor", reqArgs.toByteArray());

	    try {
	        // Interpreta a resposta como uma resposta genérica
	        GenericResponse response = GenericResponse.parseFrom(responseBytes);

	        // Verifica o código de status e lança a exceção apropriada
	        switch (response.getCodigo()) {
	            case 200:
	                return response.getMensagem(); // Sucesso

	            case 400:
	                throw new DadosInvalidosException(response.getMensagem());
	            
	            case 422:
	            	if (response.getMensagem().contains("email")) {
	            		throw new EmailInvalidoException(response.getMensagem());
	            	} else if (response.getMensagem().contains("cpf")) {
	            		throw new CPFInvalidoException(response.getMensagem());
	            	}

	            default:
	                throw new FalhaNaConexaoException("Erro desconhecido: " + response.getMensagem());
	        }
	    } catch (InvalidProtocolBufferException e) {
	        // Caso não consiga interpretar a resposta, lança uma exceção de falha na conexão
	        throw new FalhaNaConexaoException("Erro ao interpretar a resposta do servidor.");
	    }
	}

	public String removerVendedor(int id) throws Exception {
		RemoverVendedorArgs args = RemoverVendedorArgs.newBuilder().setId(id).build();

		byte[] responseBytes = doOperation("VendedorService", "removerVendedor", args.toByteArray());

		try {
			GenericResponse response = GenericResponse.parseFrom(responseBytes);

			switch(response.getCodigo()){
				case 404: 
					throw new VendedorNaoEncontradoException(response.getMensagem());
			}

			return response.getMensagem();

		} catch (Exception e) {
			throw new FalhaNaConexaoException("Erro ao interpretar a resposta do servidor.");
		}
	}
	
	public List<Vendedor> listarVendedores(){
		try {
			ListarVendedoresArgs reqArgs = ListarVendedoresArgs.newBuilder().build();
			
			byte[] responseBytes = doOperation("VendedorService", "listarVendedores", reqArgs.toByteArray());
			
			ListarVendedoresResponse response = ListarVendedoresResponse.parseFrom(responseBytes);
			
			return response.getVendedoresList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int quantidadeVendasAbsolutas() {
		try {
			QuantidadeVendasAbsolutasArgs reqArgs = QuantidadeVendasAbsolutasArgs.newBuilder().build();
			
			byte[] responseBytes = doOperation("VendedorService", "quantidadeVendasAbsolutas", reqArgs.toByteArray());
			
			QuantidadeVendasAbsolutasResponse response = QuantidadeVendasAbsolutasResponse.parseFrom(responseBytes);
			
			return response.getQuantidade();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public byte[] doOperation(String objectRef, String method, byte[] args) {
		try {
			Message request = empacotaMensagem(objectRef, method, args);
			
			udpClient.sendRequest(request.toByteArray());
			
			byte[] responseBytes = udpClient.getReply();
			
			Message resposta = desempacotaMensagem(responseBytes);
			
			return resposta.getArguments().toByteArray();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Message desempacotaMensagem(byte[] responseBytes) {
		try {
			return Message.parseFrom(responseBytes);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Message empacotaMensagem(String objectRef, String method, byte[] args) {
		Message message = Message.newBuilder()
				.setType(1)
				.setId(requestId++)
				.setObfReference(objectRef)
				.setMethodId(method)
				.setArguments(ByteString.copyFrom(args))
				.build();
		
		return message;
	}
	
	public void close() {
		udpClient.finaliza();
	}
}
