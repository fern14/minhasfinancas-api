package br.com.david.financas.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.david.financas.models.Usuario;
import br.com.david.financas.models.repository.UsuarioRepository;
import br.com.david.financas.service.UsuarioService;
import br.com.david.financas.service.impl.exceptions.ErrorAutenticacao;
import br.com.david.financas.service.impl.exceptions.RegraNegocioException;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository usuarioRepository;
	private PasswordEncoder encoder;

	public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
		this.usuarioRepository = usuarioRepository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

		if (!usuario.isPresent()) {
			throw new ErrorAutenticacao("Usuário não encontrado.");
		}
		
		boolean senhaCorreta = encoder.matches(senha, usuario.get().getSenha());

		if (!senhaCorreta) {
			throw new ErrorAutenticacao("Senha inválida.");
		}

		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario cadastrar(Usuario usuario) {
		validarEmail(usuario.getEmail());
		criptografarSenha(usuario);
		return usuarioRepository.save(usuario);
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaCriptografada = encoder.encode(senha);
		usuario.setSenha(senhaCriptografada);
	}

	@Override
	public void validarEmail(String email) {
		boolean exists = usuarioRepository.existsByEmail(email);
		if (exists) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
		}

	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return usuarioRepository.findById(id);
	}

}
