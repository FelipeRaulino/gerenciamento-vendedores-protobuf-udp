package servidor;

import java.util.List;

import com.google.protobuf.ByteString;
import proto.VendedorOuterClass.Vendedor;
import proto.VendedorOuterClass.AdicionarVendedorArgs;
import proto.VendedorOuterClass.GenericResponse;
import proto.VendedorOuterClass.ListarVendedoresResponse;
import proto.VendedorOuterClass.QuantidadeVendasAbsolutasResponse;
import proto.VendedorOuterClass.RemoverVendedorArgs;

public class VendedorEsqueleto {
	
	private VendedorServente servente;
	
	public VendedorEsqueleto() {
		servente = new VendedorServente();
	}
	
	public byte[] adicionarVendedor(ByteString args) {
		try {
			AdicionarVendedorArgs requisicao = AdicionarVendedorArgs.parseFrom(args);
			GenericResponse resposta = servente.adicionarVendedor(requisicao.getVendedor());
			return resposta.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return GenericResponse.newBuilder().setMensagem("Erro ao adicionar vendedor").build().toByteArray();
		}
	}

	public byte[] removerVendedor(ByteString args){
		try {
			RemoverVendedorArgs requisicao = RemoverVendedorArgs.parseFrom(args);
			GenericResponse resposta = servente.removerVendedor(requisicao.getId());
			return resposta.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return GenericResponse.newBuilder().setMensagem("Erro ao remover vendedor").build().toByteArray();
		}
	}
	
	public byte[] listarVendedores() {
		try {
			List<Vendedor> vendedores = servente.listarVendedores();
			
			return ListarVendedoresResponse.newBuilder()
					.addAllVendedores(vendedores)
					.build()
					.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] quantidadeVendasAbsolutas() {
		try {
			int totalVendas = servente.quantidadeVendasAbsolutas();
			
			return QuantidadeVendasAbsolutasResponse.newBuilder()
					.setQuantidade(totalVendas)
					.build()
					.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
