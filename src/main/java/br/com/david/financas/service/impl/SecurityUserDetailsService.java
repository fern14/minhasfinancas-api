package br.com.david.financas.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.david.financas.models.Usuario;
import br.com.david.financas.models.repository.UsuarioRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

	private UsuarioRepository repository;

	public SecurityUserDetailsService(UsuarioRepository repository) {
		this.repository = repository;

	};

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuarioEncontrado = repository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado"));

		return User.builder().username(usuarioEncontrado.getEmail()).password(usuarioEncontrado.getSenha())
				.roles("USER").build();
		
	}

}
