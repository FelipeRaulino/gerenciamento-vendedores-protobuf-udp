package servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.CPFInvalidoException;
import exceptions.DadosInvalidosException;
import exceptions.EmailInvalidoException;
import proto.VendedorOuterClass.Vendedor;
import proto.VendedorOuterClass.GenericResponse;

public class VendedorServente {

	private static List<Vendedor> vendedores = new ArrayList<Vendedor>();
	
	private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";

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
			
			if (vendedorExistente(vendedor.getEmail())) {
				
			}
			
			vendedores.add(vendedor);

			return GenericResponse.newBuilder()
					.setCodigo(200)
					.setMensagem("Vendedor adicionado com sucesso!")
					.build();
		} catch (DadosInvalidosException | EmailInvalidoException | CPFInvalidoException e) {
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
	
    private boolean vendedorExistente(String email) {
    	for (Vendedor vendedor : vendedores) {
			if (vendedor.getEmail() == email) {
				return true;
			}
		}
    	
    	return false;
    }
    
}
