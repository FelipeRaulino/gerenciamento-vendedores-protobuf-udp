package servidor;

import java.util.ArrayList;
import java.util.List;

import exceptions.DadosInvalidosException;
import proto.VendedorOuterClass.Vendedor;
import proto.VendedorOuterClass.GenericResponse;

public class VendedorServente {

	private static List<Vendedor> vendedores = new ArrayList<Vendedor>();

	public GenericResponse adicionarVendedor(Vendedor vendedor) {
		try {
			if (campoVazio(vendedor)) {
				throw new DadosInvalidosException("Todos os campos precisam ser preenchidos!");
			}

			vendedores.add(vendedor);

			return GenericResponse.newBuilder().setMensagem("Vendedor adicionado com sucesso!").build();
		} catch (DadosInvalidosException e) {
			return empacotaErro(e);
		}
	}

	public List<Vendedor> listarVendedores() {
		return new ArrayList<>(vendedores);
	}

	public int quantidadeVendasAbsolutas() {
		int totalVendas = 0;

		for (Vendedor vendedor : vendedores) {
			totalVendas += vendedor.getVendas();
		}

		return totalVendas;
	}

	private GenericResponse empacotaErro(Exception e) {
		if (e instanceof DadosInvalidosException) {
			return GenericResponse.newBuilder()
					.setCodigo(400)
					.setMensagem(e.getMessage())
					.build();
		} else {
			return GenericResponse.newBuilder()
					.setCodigo(500)
					.setMensagem(e.getMessage())
					.build();
		}
	}

	private boolean campoVazio(Vendedor vendedor) {
		if (vendedor.getNome() == null || vendedor.getNome().isEmpty() || vendedor.getCpf() == null
				|| vendedor.getCpf().isEmpty() || vendedor.getEmail() == null || vendedor.getEmail().isEmpty()
				|| vendedor.getTelefone() == null || vendedor.getTelefone().isEmpty()
				|| vendedor.getAreaAtuacao() == null || vendedor.getAreaAtuacao().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}
