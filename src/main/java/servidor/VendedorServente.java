package servidor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.CPFInvalidoException;
import exceptions.DadosInvalidosException;
import exceptions.EmailInvalidoException;
import exceptions.VendedorExistenteException;
import exceptions.VendedorNaoEncontradoException;
import proto.VendedorOuterClass.Vendedor;
import proto.VendedorOuterClass.DesempenhoVendedorResponse;
import proto.VendedorOuterClass.GenericResponse;

public class VendedorServente {

	private static List<Vendedor> vendedores = new ArrayList<Vendedor>();
	
	private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
	
	//Métodos da aplicação

	public GenericResponse adicionarVendedor(Vendedor vendedor) {
		try {
			if (campoVazio(vendedor)) {
				throw new DadosInvalidosException("Todos os campos precisam ser preenchidos!");
			}
			
			if (!emailValido(vendedor.getEmail())) {
				throw new EmailInvalidoException("Por favor, forneça um email válido");
			}
			
			if (!validarCPF(vendedor.getCpf())) {
				throw new CPFInvalidoException("Por favor, forneça um cpf válido");
			}
			
			if (vendedorExistente(vendedor.getEmail(), vendedor.getCpf())) {
				throw new VendedorExistenteException("Já existe um vendedor com o email ou cpf informado!");
			}

			vendedores.add(vendedor);

			return GenericResponse.newBuilder()
					.setCodigo(200)
					.setMensagem("Vendedor adicionado com sucesso!")
					.build();
		} catch (DadosInvalidosException | EmailInvalidoException | CPFInvalidoException | VendedorExistenteException e) {
			return empacotaErro(e);
		}
	}
	
	public List<Vendedor> listarVendedores() {
		return new ArrayList<>(vendedores);
	}
	
	public GenericResponse editarVendedor(int id, Vendedor vendedor) {
		try {
			Vendedor vendedorExistente = getVendedorPorId(id);
			
			if (vendedorExistente == null) {
				throw new VendedorNaoEncontradoException("Vendedor de id: " + id + " não encontrado!");
			}
			
			if (campoVazio(vendedor)) {
				throw new DadosInvalidosException("Todos os campos precisam ser preenchidos!");
			}
			
			if (!emailValido(vendedor.getEmail())) {
				throw new EmailInvalidoException("Por favor, forneça um email válido");
			}
			
			if (!validarCPF(vendedor.getCpf())) {
				throw new CPFInvalidoException("Por favor, forneça um cpf válido");
			}
			
			for (int i = 0; i < vendedores.size(); i++) {
	            if (vendedores.get(i).getId() == id) {
	                vendedores.set(i, vendedor.toBuilder()
	                    .setId(id)
	                    .setNome(vendedor.getNome())
	                    .setEmail(vendedor.getEmail())
	                    .setTelefone(vendedor.getTelefone())
	                    .setStatus(vendedor.getStatus())
	                    .setComissao(vendedor.getComissao())
	                    .setMetaVendas(vendedor.getMetaVendas())
	                    .setVendas(vendedor.getVendas())
	                    .setAreaAtuacao(vendedor.getAreaAtuacao())
	                    .build());
	                break;
	            }
	        }
			
			return GenericResponse.newBuilder()
					.setCodigo(200)
					.setMensagem("Vendedor editado com sucesso!")
					.build();
			
		} catch (DadosInvalidosException | VendedorNaoEncontradoException | EmailInvalidoException | CPFInvalidoException e) {
			return empacotaErro(e);
		}
	}
	
	public GenericResponse removerVendedor(int id){
		try {
			Boolean hasVendedor = false;
			
			for (Vendedor vendedor : vendedores) {
				if (vendedor.getId() == id){
					hasVendedor = true;
				}
			}
			
			if (hasVendedor == false) {
				throw new VendedorNaoEncontradoException("Vendedor de id: " + id + " não encontrado!");
			}
			
		
			Iterator<Vendedor> iterator = vendedores.iterator();
			
			while (iterator.hasNext()) {
				Vendedor vendedor = iterator.next();
				if (vendedor.getId() == id){
					iterator.remove();
				}
			}

			return GenericResponse.newBuilder()
					.setCodigo(200)
					.setMensagem("Vendedor removido com sucesso!")
					.build();

		} catch (VendedorNaoEncontradoException e) {
			return empacotaErro(e);
		}
	}

	public int quantidadeVendasAbsolutas() {
		int totalVendas = 0;

		for (Vendedor vendedor : vendedores) {
			totalVendas += vendedor.getVendas();
		}

		return totalVendas;
	}

	public int quantidadeVendasPorAreaAtuacao(String areaAtuacao) {
		int total = 0;
		
		for (Vendedor vendedor : vendedores) {
			if (vendedor.getAreaAtuacao().equalsIgnoreCase(areaAtuacao)) {
				total += vendedor.getVendas();
			}
		}
		
		return total;
	}
	
	public DesempenhoVendedorResponse desempenhoVendedor(int id) {
		try {
			Vendedor vendedorExistente = getVendedorPorId(id);
			
			if (vendedorExistente == null) {
				throw new VendedorNaoEncontradoException("Vendedor de id: " + id + " não encontrado!");
			}
			
			int vendas = vendedorExistente.getVendas();
	        int meta = vendedorExistente.getMetaVendas();
	        int desempenho = (int) ((vendas / (double) meta) * 100);

	        return DesempenhoVendedorResponse.newBuilder()
	                .setCodigo(200)
	                .setMensagem("Desempenho calculado com sucesso!")
	                .setDesempenho(desempenho)
	                .build();

		    } catch (VendedorNaoEncontradoException e) {
		        return DesempenhoVendedorResponse.newBuilder()
		                .setCodigo(404)
		                .setMensagem(e.getMessage())
		                .setDesempenho(-1)
		                .build();
		    }
	}
	
	// Método para tratamento de erros
 	private GenericResponse empacotaErro(Exception e) {
		if (e instanceof DadosInvalidosException) {
			return GenericResponse.newBuilder()
					.setCodigo(400)
					.setMensagem(e.getMessage())
					.build();
		} else if (e instanceof VendedorNaoEncontradoException) {
			return GenericResponse.newBuilder()
					.setCodigo(404)
					.setMensagem(e.getMessage())
					.build();
		} else if (e instanceof VendedorExistenteException) {
			return GenericResponse.newBuilder()
					.setCodigo(409)
					.setMensagem(e.getMessage())
					.build();
		} else if (e instanceof EmailInvalidoException) {
			return GenericResponse.newBuilder()
					.setCodigo(422)
					.setMensagem(e.getMessage())
					.build();
		} else if (e instanceof CPFInvalidoException) {
			return GenericResponse.newBuilder()
					.setCodigo(422)
					.setMensagem(e.getMessage())
					.build();
		} else {
			return GenericResponse.newBuilder()
					.setCodigo(500)
					.setMensagem(e.getMessage())
					.build();
		}
	}

	
	// Métodos auxiliadores
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
	
	private boolean emailValido(String email) {
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		
		Matcher matcher = pattern.matcher(email);
		
		return matcher.matches();
	}
	
	private boolean validarCPF(String cpf) {
        // Remover caracteres especiais
        cpf = cpf.replaceAll("\\D", "");  // Remove qualquer caractere que não seja dígito

        // Verifica se o CPF tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verificar se todos os dígitos são iguais (ex.: 111.111.111-11)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int primeiroDigito = calcularDigito(cpf, 9);
        
        // Calcula o segundo dígito verificador
        int segundoDigito = calcularDigito(cpf, 10);

        // Verifica se os dígitos verificadores estão corretos
        return cpf.charAt(9) == Character.forDigit(primeiroDigito, 10) 
            && cpf.charAt(10) == Character.forDigit(segundoDigito, 10);
    }

    private static int calcularDigito(String cpf, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < pesoInicial; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial + 1 - i);
        }

        int resto = 11 - (soma % 11);
        return (resto == 10 || resto == 11) ? 0 : resto;
    }
	
    private boolean vendedorExistente(String email, String cpf) {
    	for (Vendedor vendedor : vendedores) {
			if (vendedor.getEmail().equalsIgnoreCase(email) || vendedor.getCpf().equalsIgnoreCase(cpf)) {
				return true;
			}
		}
    	
    	return false;
    }
    
    private Vendedor getVendedorPorId(int id) {
    	for (Vendedor vendedor : vendedores) {
			if (vendedor.getId() == id) {
				return vendedor;
			}
		}
    	
    	return null;
    }
    
}
