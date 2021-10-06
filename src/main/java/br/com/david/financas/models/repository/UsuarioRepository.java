package br.com.david.financas.models.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.david.financas.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email);
}
