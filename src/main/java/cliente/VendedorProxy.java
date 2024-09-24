package cliente;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import exceptions.CPFInvalidoException;
import exceptions.DadosInvalidosException;
import exceptions.EmailInvalidoException;
import exceptions.FalhaNaConexaoException;
import exceptions.TelefoneInvalidoException;
import exceptions.VendedorExistenteException;
import exceptions.VendedorNaoEncontradoException;
import proto.VendedorOuterClass.Message;
import proto.VendedorOuterClass.QuantidadeVendasAbsolutasArgs;
import proto.VendedorOuterClass.QuantidadeVendasAbsolutasResponse;
import proto.VendedorOuterClass.QuantidadeVendasPorAreaAtuacaoArgs;
import proto.VendedorOuterClass.QuantidadeVendasPorAreaAtuacaoResponse;
import proto.VendedorOuterClass.RemoverVendedorArgs;
import proto.VendedorOuterClass.Vendedor;
import proto.VendedorOuterClass.AdicionarVendedorArgs;
import proto.VendedorOuterClass.DesempenhoVendedorArgs;
import proto.VendedorOuterClass.DesempenhoVendedorResponse;
import proto.VendedorOuterClass.EditarVendedorArgs;
import proto.VendedorOuterClass.ListarVendedoresArgs;
import proto.VendedorOuterClass.GenericResponse;
import proto.VendedorOuterClass.ListarVendedoresResponse;;

public class VendedorProxy {

	private UDPClient udpClient;

	public VendedorProxy() {
		try {
			udpClient = new UDPClient("localhost", 7896);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String adicionarVendedor(Vendedor vendedor) {
		try {
			AdicionarVendedorArgs reqArgs = AdicionarVendedorArgs.newBuilder().setVendedor(vendedor).build();

			byte[] responseBytes = doOperation("VendedorService", "adicionarVendedor", reqArgs.toByteArray());

			// Interpreta a resposta como uma resposta genérica
			GenericResponse response = GenericResponse.parseFrom(responseBytes);

			// Verifica o código de status e lança a exceção apropriada
			switch (response.getCodigo()) {
			case 200:
				return response.getMensagem();

			case 400:
				if (response.getMensagem().contains("telefone")) {
					throw new TelefoneInvalidoException(response.getMensagem());
				} else {
					throw new DadosInvalidosException(response.getMensagem());
				}

			case 409:
				throw new VendedorExistenteException(response.getMensagem());

			case 422:
				if (response.getMensagem().contains("email")) {
					throw new EmailInvalidoException(response.getMensagem());
				} else if (response.getMensagem().contains("cpf")) {
					throw new CPFInvalidoException(response.getMensagem());
				}

			default:
				throw new FalhaNaConexaoException("Erro desconhecido: " + response.getMensagem());
			}
		} catch (VendedorExistenteException | DadosInvalidosException | CPFInvalidoException
				| EmailInvalidoException e) {
			if (e instanceof VendedorExistenteException) {
				System.err.println("Código: 409");
				System.err.println("Erro ao adicionar vendedor: " + e.getMessage());
			} else if (e instanceof DadosInvalidosException) {
				System.err.println("Código: 400");
				System.err.println("Erro ao adicionar vendedor: " + e.getMessage());
			} else if (e instanceof CPFInvalidoException) {
				System.err.println("Código: 422");
				System.err.println("Erro ao adicionar vendedor: " + e.getMessage());
			} else if (e instanceof EmailInvalidoException) {
				System.err.println("Código: 422");
				System.err.println("Erro ao adicionar vendedor: " + e.getMessage());
			}

			return e.getMessage();
		} catch (FalhaNaConexaoException e) {
			System.err.println("Erro de conexão: " + e.getMessage());
			return "Erro de conexão: " + e.getMessage();
		} catch (Exception e) {
			System.err.println("Erro inesperado: " + e.getMessage());
			return "Erro inesperado: " + e.getMessage();
		}
	}

	public String editarVendedor(int id, Vendedor vendedor) throws Exception {
		try {

			EditarVendedorArgs args = EditarVendedorArgs.newBuilder().setId(id).setVendedor(vendedor).build();

			byte[] responseBytes = doOperation("VendedorService", "editarVendedor", args.toByteArray());

			GenericResponse response = GenericResponse.parseFrom(responseBytes);

			switch (response.getCodigo()) {
			case 200:
				return response.getMensagem();

			case 400:
				throw new DadosInvalidosException(response.getMensagem());

			case 404:
				throw new VendedorNaoEncontradoException(response.getMensagem());

			case 422:
				if (response.getMensagem().contains("email")) {
					throw new EmailInvalidoException(response.getMensagem());
				} else if (response.getMensagem().contains("cpf")) {
					throw new CPFInvalidoException(response.getMensagem());
				}

			default:
				throw new FalhaNaConexaoException("Erro desconhecido: " + response.getMensagem());
			}

		} catch (VendedorNaoEncontradoException | DadosInvalidosException | CPFInvalidoException
				| EmailInvalidoException e) {
			if (e instanceof VendedorNaoEncontradoException) {
				System.err.println("Código: 404");
				System.err.println("Erro ao editar vendedor: " + e.getMessage());
			} else if (e instanceof DadosInvalidosException) {
				System.err.println("Código: 400");
				System.err.println("Erro ao editar vendedor: " + e.getMessage());
			} else if (e instanceof CPFInvalidoException) {
				System.err.println("Código: 422");
				System.err.println("Erro ao editar vendedor: " + e.getMessage());
			} else if (e instanceof EmailInvalidoException) {
				System.err.println("Código: 422");
				System.err.println("Erro ao editar vendedor: " + e.getMessage());
			}

			return e.getMessage();
		} catch (FalhaNaConexaoException e) {
			System.err.println("Erro de conexão: " + e.getMessage());
			return "Erro de conexão: " + e.getMessage();
		} catch (Exception e) {
			System.err.println("Erro inesperado: " + e.getMessage());
			return "Erro inesperado: " + e.getMessage();
		}
	}

	public String removerVendedor(int id) throws Exception {
		try {
			RemoverVendedorArgs args = RemoverVendedorArgs.newBuilder().setId(id).build();

			byte[] responseBytes = doOperation("VendedorService", "removerVendedor", args.toByteArray());

			GenericResponse response = GenericResponse.parseFrom(responseBytes);

			switch (response.getCodigo()) {
			case 200:
				return response.getMensagem();

			case 404:
				throw new VendedorNaoEncontradoException(response.getMensagem());

			default:
				throw new FalhaNaConexaoException("Erro desconhecido: " + response.getMensagem());
			}

		} catch (VendedorNaoEncontradoException e) {
			System.err.println("Código: 404");
			System.err.println("Erro ao remover vendedor: " + e.getMessage());

			return e.getMessage();
		} catch (FalhaNaConexaoException e) {
			System.err.println("Erro de conexão: " + e.getMessage());
			return "Erro de conexão: " + e.getMessage();
		} catch (Exception e) {
			System.err.println("Erro inesperado: " + e.getMessage());
			return "Erro inesperado: " + e.getMessage();
		}
	}

	public List<Vendedor> listarVendedores() {
		try {
			ListarVendedoresArgs reqArgs = ListarVendedoresArgs.newBuilder().build();

			byte[] responseBytes = doOperation("VendedorService", "listarVendedores", reqArgs.toByteArray());

			ListarVendedoresResponse response = ListarVendedoresResponse.parseFrom(responseBytes);

			return response.getVendedoresList();
		} catch (InvalidProtocolBufferException e) {
			System.err.println("Erro ao interpretar a resposta do servidor: " + e.getMessage());
		} catch (FalhaNaConexaoException e) {
			System.err.println("Falha na conexão com o servidor: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.emptyList(); // Retorna uma lista vazia em vez de null
	}

	public int quantidadeVendasAbsolutas() {
	    try {
	        QuantidadeVendasAbsolutasArgs reqArgs = QuantidadeVendasAbsolutasArgs.newBuilder().build();

	        byte[] responseBytes = doOperation("VendedorService", "quantidadeVendasAbsolutas", reqArgs.toByteArray());

	        QuantidadeVendasAbsolutasResponse response = QuantidadeVendasAbsolutasResponse.parseFrom(responseBytes);

	        return response.getQuantidade();
	    } catch (InvalidProtocolBufferException e) {
	        System.err.println("Erro ao interpretar a resposta do servidor: " + e.getMessage());
	    } catch (FalhaNaConexaoException e) {
	        System.err.println("Falha na conexão com o servidor: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0; // Retorna 0 em caso de erro, para indicar que não foi possível obter a quantidade
	}


	public int quantidadeVendasPorAreaAtuacao(String areaAtuacao) {
	    // Validação de entrada
	    if (areaAtuacao == null || areaAtuacao.trim().isEmpty()) {
	        System.err.println("Área de atuação não pode ser nula ou vazia.");
	        return -1; // Retorno de erro
	    }

	    try {
	        QuantidadeVendasPorAreaAtuacaoArgs reqArgs = QuantidadeVendasPorAreaAtuacaoArgs.newBuilder()
	            .setAreaAtuacao(areaAtuacao)
	            .build();

	        byte[] responseBytes = doOperation("VendedorService", "quantidadeVendasPorAreaAtuacao", reqArgs.toByteArray());

	        QuantidadeVendasPorAreaAtuacaoResponse response = QuantidadeVendasPorAreaAtuacaoResponse.parseFrom(responseBytes);

	        return response.getQuantidade();
	    } catch (InvalidProtocolBufferException e) {
	        System.err.println("Erro ao interpretar a resposta do servidor: " + e.getMessage());
	    } catch (FalhaNaConexaoException e) {
	        System.err.println("Falha na conexão com o servidor: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0; // Retorna 0 em caso de erro, para indicar que não foi possível obter a quantidade
	}


	public int desempenhoVendedor(int id) {
	    // Validação de entrada
	    if (id <= 0) {
	        throw new IllegalArgumentException("ID do vendedor deve ser positivo.");
	    }

	    DesempenhoVendedorArgs reqArgs = DesempenhoVendedorArgs.newBuilder().setId(id).build();

	    byte[] responseBytes;
	    try {
	        responseBytes = doOperation("VendedorService", "desempenhoVendedor", reqArgs.toByteArray());
	    } catch (Exception e) {
	        System.err.println("Erro ao comunicar com o servidor: " + e.getMessage());
	        return -1; // Retorna um valor indicativo de erro
	    }

	    try {
	        DesempenhoVendedorResponse response = DesempenhoVendedorResponse.parseFrom(responseBytes);

	        switch (response.getCodigo()) {
	            case 200:
	                return response.getDesempenho();

	            case 404:
	                throw new VendedorNaoEncontradoException(response.getMensagem());

	            default:
	                throw new FalhaNaConexaoException("Erro desconhecido: " + response.getMensagem());
	        }

	    } catch (InvalidProtocolBufferException e) {
	        System.err.println("Falha na conexão com o servidor: " + e.getMessage());
	        return -1; // Retorna um valor indicativo de erro
	    } catch (VendedorNaoEncontradoException e) {
	        System.err.println("Erro: " + e.getMessage());
	        return -1; // Retorna um valor indicativo de erro
	    } catch (Exception e) {
	        System.err.println("Erro ao obter desempenho do vendedor: " + e.getMessage());
	        return -1; // Retorna um valor indicativo de erro
	    }
	}



	public byte[] doOperation(String objectRef, String method, byte[] args) throws Exception {
		Message request = empacotaMensagem(objectRef, method, args);
		byte[] requestBytes = request.toByteArray();

		int attempts = 3;
		int timeout = 500; // Tempo limite em milissegundos
		udpClient.setTimeout(timeout); // Define o timeout no cliente UDP

		while (attempts > 0) {
			try {
				// Envia a requisição
				udpClient.sendRequest(requestBytes);

				// Aguarda a resposta
				byte[] responseBytes = udpClient.getReply();

				// Processa a resposta
				Message resposta = desempacotaMensagem(responseBytes);
				return resposta.getArguments().toByteArray();

			} catch (Exception e) {
				attempts--;
				if (attempts == 0) {
					throw new FalhaNaConexaoException("Servidor não respondeu após várias tentativas.");
				}
			}
		}
		throw new FalhaNaConexaoException("Falha inesperada na operação.");
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
		int uniqueRequestId = generateRequestId();

		Message message = Message.newBuilder().setType(1).setId(uniqueRequestId).setObfReference(objectRef)
				.setMethodId(method).setArguments(ByteString.copyFrom(args)).build();

		return message;
	}

	public void close() {
		udpClient.finaliza();
	}

	private static int generateRequestId() {
		// return ++requestId; // Alternativa 1: Usar contador
		return new Random().nextInt(10000); // Alternativa 2: Usar número aleatório
	}
}
