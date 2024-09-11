package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import proto.VendedorOuterClass.Vendedor;

public class VendedorClient {

	private VendedorProxy proxy;

	public VendedorClient() {
		try {
			proxy = new VendedorProxy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void interagir() throws Exception {
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			Boolean option = true;
			
			while (option) {
				System.out.println();
				System.out.println("Bem-Vindo ao SGV. Escolha uma de nossos serviços: ");
				System.out.println("1 - Adicionar novo vendedor");
				System.out.println("2 - Listar vendedores");
				System.out.println("3 - Ver quantidade de vendas absolutas");
				
				int escolha = Integer.parseInt(stdin.readLine());
				
				switch (escolha) {
					case 1:
						adicionarVendedor(stdin);
						break;
						
					case 2:
						List<Vendedor> vendedores = proxy.listarVendedores();
						
						if (vendedores.size() > 0) {
							System.out.println("Lista de vendedores");
							for (Vendedor vendedor : vendedores) {
								System.out.println("ID: " + vendedor.getId());
								System.out.println("Nome: " + vendedor.getNome());
								System.out.println("---------------------------------------------------");
							}
						} else {
							System.out.println("Lista vazia!");
						}
						
						break;
					
					case 3:
						int vendasTotal = proxy.quantidadeVendasAbsolutas();
						System.out.println("Quantidade total de vendas: " + vendasTotal);
						break;
						
					default:
						option = false;
						break;
				}
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void adicionarVendedor(BufferedReader stdin) throws Exception {
		String nome, cpf, email, telefone, areaAtuacao;
		int comissao, metaVendas, vendas;
		boolean status;
		
		List<Vendedor> vendedores = proxy.listarVendedores();

		System.out.println("Adicionar novo vendedor:");
		System.out.print("Nome: ");
		nome = stdin.readLine();
		System.out.print("CPF (XXX.XXX.XXX-XX): ");
		cpf = stdin.readLine();
		System.out.print("Email: ");
		email = stdin.readLine();
		System.out.print("Telefone: ");
		telefone = stdin.readLine();
		System.out.print("Status (true/false): ");
		status = Boolean.parseBoolean(stdin.readLine());
		System.out.print("Comissão: ");
		comissao = Integer.parseInt(stdin.readLine());
		System.out.print("Meta de Vendas: ");
		metaVendas = Integer.parseInt(stdin.readLine());
		System.out.print("Vendas: ");
		vendas = Integer.parseInt(stdin.readLine());
		System.out.print("Área de Atuação: ");
		areaAtuacao = stdin.readLine();
		
		Vendedor vendedor = Vendedor.newBuilder()
				.setId(vendedores.size() + 1)
                .setNome(nome)
                .setCpf(cpf)
                .setEmail(email)
                .setTelefone(telefone)
                .setStatus(status)
                .setComissao(comissao)
                .setMetaVendas(metaVendas)
                .setVendas(vendas)
                .setAreaAtuacao(areaAtuacao)
                .build();
		
		String resposta = proxy.adicionarVendedor(vendedor);
		System.out.println("Resposta do servidor: " + resposta);
	}

	public static void main(String args[]) {
		try {
			VendedorClient client = new VendedorClient();
			client.interagir();
			client.proxy.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
