package br.com.david.financas.service.impl.exceptions;

public class ErrorAutenticacao extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorAutenticacao(String mensagem) {
		super(mensagem);
	}

}
