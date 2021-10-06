package br.com.david.financas.models.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.david.financas.models.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {

		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		boolean result = usuarioRepository.existsByEmail("usuario@gmail.com");

		Assertions.assertThat(result).isTrue();

	}

	@Test
	public void retornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {

		boolean result = usuarioRepository.existsByEmail("usuario@gmail.com");

		Assertions.assertThat(result).isFalse();

	}
	

	@Test
	public void devePersistirUmUsuarioNoBD() {

		Usuario usuario = criarUsuario();

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}

	@Test
	public void buscarUsuarioPorEmail() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		Optional<Usuario> result = usuarioRepository.findByEmail(usuario.getEmail());
		Assertions.assertThat(result.isPresent()).isTrue();

	}

	@Test
	public void deveRetornarVazioQuandoNaoExistirUsuarioNaBase() {

		Optional<Usuario> result = usuarioRepository.findByEmail("usuario@gmail.com");
		Assertions.assertThat(result.isPresent()).isFalse();

	}

	public static Usuario criarUsuario() {
		return Usuario.builder().nome("Usuario").email("usuario@gmail.com").senha("senha").build();
	}

}
