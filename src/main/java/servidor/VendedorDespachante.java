package servidor;

import proto.VendedorOuterClass.Message;

public class VendedorDespachante {
	
	public byte[] selecionaEsqueleto(byte[] requestBytes) {
		try {
			Message request = Message.parseFrom(requestBytes);
			String method = request.getMethodId();
			
			VendedorEsqueleto esqueleto = new VendedorEsqueleto();
			
			switch (method) {
				case "adicionarVendedor":
					return esqueleto.adicionarVendedor(request.getArguments());
					
				case "listarVendedores":
					return esqueleto.listarVendedores();
				
				case "editarVendedor":
					return esqueleto.editarVendedor(request.getArguments());
				
				case "removerVendedor":
					return esqueleto.removerVendedor(request.getArguments());
					
				case "quantidadeVendasAbsolutas":
					return esqueleto.quantidadeVendasAbsolutas();
					
				case "quantidadeVendasPorAreaAtuacao":
					return esqueleto.quantidadeVendasPorAreaAtuacao(request.getArguments());
					
				case "desempenhoVendedor":
					return esqueleto.desempenhoVendedor(request.getArguments());
				
	
				default:
					return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
