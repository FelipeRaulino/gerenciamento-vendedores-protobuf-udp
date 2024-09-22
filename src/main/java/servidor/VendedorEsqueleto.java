package servidor;

import java.util.List;

import com.google.protobuf.ByteString;
import proto.VendedorOuterClass.Vendedor;
import proto.VendedorOuterClass.AdicionarVendedorArgs;
import proto.VendedorOuterClass.DesempenhoVendedorArgs;
import proto.VendedorOuterClass.DesempenhoVendedorResponse;
import proto.VendedorOuterClass.EditarVendedorArgs;
import proto.VendedorOuterClass.GenericResponse;
import proto.VendedorOuterClass.ListarVendedoresResponse;
import proto.VendedorOuterClass.QuantidadeVendasAbsolutasResponse;
import proto.VendedorOuterClass.QuantidadeVendasPorAreaAtuacaoArgs;
import proto.VendedorOuterClass.QuantidadeVendasPorAreaAtuacaoResponse;
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
	
	
	public byte[] editarVendedor(ByteString args) {
		try {
			EditarVendedorArgs requisicao = EditarVendedorArgs.parseFrom(args);
			GenericResponse resposta = servente.editarVendedor(requisicao.getId(), requisicao.getVendedor());
			return resposta.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return GenericResponse.newBuilder().setCodigo(500).setMensagem("Erro ao editar vendedor").build().toByteArray();
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
	
	public byte[] quantidadeVendasPorAreaAtuacao(ByteString args) {
		try {
			QuantidadeVendasPorAreaAtuacaoArgs reqArgs = QuantidadeVendasPorAreaAtuacaoArgs.parseFrom(args);
			int totalVendas = servente.quantidadeVendasPorAreaAtuacao(reqArgs.getAreaAtuacao());
			
			return QuantidadeVendasPorAreaAtuacaoResponse.newBuilder()
					.setQuantidade(totalVendas)
					.build()
					.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] desempenhoVendedor(ByteString args) {
		try {
			DesempenhoVendedorArgs reqArgs = DesempenhoVendedorArgs.parseFrom(args);
			DesempenhoVendedorResponse desempenho = servente.desempenhoVendedor(reqArgs.getId());
			
			return desempenho.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
