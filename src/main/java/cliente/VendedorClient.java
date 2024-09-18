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
				System.out.println("1 - Adicionar novo(a) vendedor(a)");
				System.out.println("2 - Listar vendedores");
				System.out.println("3 - Editar vendedor(a)");
				System.out.println("4 - Remover vendedor(a)");
				System.out.println("5 - Ver quantidade de vendas absolutas");
				System.out.println("6 - Ver quantidade de vendas por área de atuação");
				System.out.println("7 - Desempenho do Vendedor");
				
				int escolha = Integer.parseInt(stdin.readLine());
				
				switch (escolha) {
					case 1:
						adicionarVendedor(stdin);
						break;
						
					case 2:
						List<Vendedor> vendedores = proxy.listarVendedores();
						
						if (vendedores.size() > 0) {
							System.out.println();
							System.out.println("Lista de vendedores");
							for (Vendedor vendedor : vendedores) {
								System.out.println("---------------------------------------------------");
								System.out.println("ID: " + vendedor.getId());
								System.out.println("Nome: " + vendedor.getNome());
								System.out.println("Email: " + vendedor.getEmail());
								System.out.println("Status: " + (vendedor.getStatus() ? "Ativo(a)" : "Inativo(a)"));
								System.out.println("Área de Atuação: " + vendedor.getAreaAtuacao());
								System.out.println("---------------------------------------------------");
							}
						} else {
							System.out.println();
							System.out.println("Lista vazia!");
						}
						
						break;
						
					case 3:
						System.out.println();
						System.out.println("Informe o id: ");
						int idEditar = Integer.parseInt(stdin.readLine());
						
						editarVendedor(idEditar, stdin);
						break;
					
					case 4:
						System.out.println();
						System.out.print("Informe o id: ");
						int id = Integer.parseInt(stdin.readLine());
						System.out.println(proxy.removerVendedor(id));
						break;
						
					case 5:
						System.out.println();
						int vendasTotal = proxy.quantidadeVendasAbsolutas();
						System.out.println("Quantidade total de vendas: " + vendasTotal);
						break;
						
					case 6:
						System.out.println();
						System.out.println("Escolha a área de atuação: ");
						System.out.println("1 - Área Comercial");
						System.out.println("2 - Vendas de Imóveis");
						System.out.println("3 - Vendas de Produtos de Saúde");
						System.out.println("4 - Vendas de Produtos de Consumo");
						System.out.println("5 - Vendas de Serviços Financeiros");
						
						int areaAtuacaoCodigo = Integer.parseInt(stdin.readLine());
						
						String areaAtuacao = "";
						
						switch (areaAtuacaoCodigo) {
							case 1:
								areaAtuacao = "Área Comercial";			
								break;
							
							case 2:
								areaAtuacao = "Vendas de Imóveis";			
								break;
							
							case 3:
								areaAtuacao = "Vendas de Produtos de Saúde";			
								break;
								
							case 4:
								areaAtuacao = "Vendas de Produtos de Consumo";			
								break;
					
							case 5:
								areaAtuacao = "Vendas de Serviços Financeiros";			
								break;
								
							default:
								System.out.println("Área de atuação inválida!");
								break;
						}
						
						System.out.println("Quantidade de vendas: " + proxy.quantidadeVendasPorAreaAtuacao(areaAtuacao));
						
						break;
					
					case 7:
						System.out.println();
						System.out.print("Informe o id do vendedor(a): ");
						int idDesempenho = Integer.parseInt(stdin.readLine());
						
						int desempenho = proxy.desempenhoVendedor(idDesempenho);
						
						System.out.println("Produtividade: " + desempenho + "%");
						
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
		String nome, cpf, email, telefone, areaAtuacao = "";
		int comissao, metaVendas, vendas, statusVendedor, areaAtuacaoCodigo;
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
		
		System.out.println("Selecione o status do vendedor: ");
		System.out.println("1 - Ativo");
		System.out.println("2 - Inativo");
		statusVendedor = Integer.parseInt(stdin.readLine());
		status = statusVendedor == 1 ? true : false;
		
		System.out.print("Comissão (% por venda):");
		comissao = Integer.parseInt(stdin.readLine());
		
		System.out.print("Meta de Vendas (Unidades): ");
		metaVendas = Integer.parseInt(stdin.readLine());
		
		System.out.print("Vendas (Unidades): ");
		vendas = Integer.parseInt(stdin.readLine());
		
		System.out.println("Escolha a área de Atuação: ");
		System.out.println("1 - Área Comercial");
		System.out.println("2 - Vendas de Imóveis");
		System.out.println("3 - Vendas de Produtos de Saúde");
		System.out.println("4 - Vendas de Produtos de Consumo");
		System.out.println("5 - Vendas de Serviços Financeiros");
		
		areaAtuacaoCodigo = Integer.parseInt(stdin.readLine());
		
		switch (areaAtuacaoCodigo) {
			case 1:
				areaAtuacao = "Área Comercial";			
				break;
			
			case 2:
				areaAtuacao = "Vendas de Imóveis";			
				break;
			
			case 3:
				areaAtuacao = "Vendas de Produtos de Saúde";			
				break;
				
			case 4:
				areaAtuacao = "Vendas de Produtos de Consumo";			
				break;
	
			case 5:
				areaAtuacao = "Vendas de Serviços Financeiros";			
				break;
				
			default:
				System.out.println("Área de atuação inválida!");
				break;
		}
		
		
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

	private void editarVendedor(int id, BufferedReader stdin) throws Exception {
		String nome, cpf, email, telefone, areaAtuacao = "";
		int comissao, metaVendas, vendas, statusVendedor, areaAtuacaoCodigo;
		boolean status;
		
		List<Vendedor> vendedores = proxy.listarVendedores();

		System.out.println("Editar vendedor:");
		
		System.out.print("Nome: ");
		nome = stdin.readLine();
		
		System.out.print("CPF (XXX.XXX.XXX-XX): ");
		cpf = stdin.readLine();
		
		System.out.print("Email: ");
		email = stdin.readLine();
		
		System.out.print("Telefone: ");
		telefone = stdin.readLine();
		
		System.out.println("Selecione o status do vendedor: ");
		System.out.println("1 - Ativo");
		System.out.println("2 - Inativo");
		statusVendedor = Integer.parseInt(stdin.readLine());
		status = statusVendedor == 1 ? true : false;
		
		System.out.print("Comissão (% por venda):");
		comissao = Integer.parseInt(stdin.readLine());
		
		System.out.print("Meta de Vendas (Unidades): ");
		metaVendas = Integer.parseInt(stdin.readLine());
		
		System.out.print("Vendas (Unidades): ");
		vendas = Integer.parseInt(stdin.readLine());
		
		System.out.println("Escolha a área de Atuação: ");
		System.out.println("1 - Área Comercial");
		System.out.println("2 - Vendas de Imóveis");
		System.out.println("3 - Vendas de Produtos de Saúde");
		System.out.println("4 - Vendas de Produtos de Consumo");
		System.out.println("5 - Vendas de Serviços Financeiros");
		
		areaAtuacaoCodigo = Integer.parseInt(stdin.readLine());
		
		switch (areaAtuacaoCodigo) {
			case 1:
				areaAtuacao = "Área Comercial";			
				break;
			
			case 2:
				areaAtuacao = "Vendas de Imóveis";			
				break;
			
			case 3:
				areaAtuacao = "Vendas de Produtos de Saúde";			
				break;
				
			case 4:
				areaAtuacao = "Vendas de Produtos de Consumo";			
				break;
	
			case 5:
				areaAtuacao = "Vendas de Serviços Financeiros";			
				break;
				
			default:
				System.out.println("Área de atuação inválida!");
				break;
		}
		
		
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
		
		String resposta = proxy.editarVendedor(id, vendedor);
		System.out.println(resposta);
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
