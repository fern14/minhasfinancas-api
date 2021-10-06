package br.com.david.financas.service;

import java.util.Optional;

import br.com.david.financas.models.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);

	Usuario cadastrar(Usuario usuario);

	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);

}
