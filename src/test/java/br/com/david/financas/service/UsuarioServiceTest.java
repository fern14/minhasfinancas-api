package br.com.david.financas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.david.financas.models.Usuario;
import br.com.david.financas.models.repository.UsuarioRepository;
import br.com.david.financas.service.impl.UsuarioServiceImpl;
import br.com.david.financas.service.impl.exceptions.RegraNegocioException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl usuarioService;

	@MockBean
	UsuarioRepository usuarioRepository;


	@Test
	public void autenticarUsuarioComSucesso() {
		String email = "email@email.com";
		String senha = "senha";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

		Usuario result = usuarioService.autenticar(email, senha);

		Assertions.assertThat(result).isNotNull();

	}

	@Test
	public void lancarErroQuandoNaoEncontrarUsuario() {
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		usuarioService.autenticar("email@email.com", "senha");
	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		usuarioService.autenticar("email@email.com", "123");
	}

	@Test
	public void deveSalvarUmUsuario() {
		Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("David").email("email@gmail.com").senha("senha").build();
		Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		Usuario usuarioSalvo = usuarioService.cadastrar(new Usuario());
		
		Assertions.assertThat(usuarioSalvo).isNotNull();
		
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("David");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@gmail.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);
		
		usuarioService.cadastrar(usuario);
		
		Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);
		
	}

	@Test
	public void deveValidarEmail() {

		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

		usuarioService.validarEmail("email@email.com");
	}

	@Test
	public void lancarErroQuandoExistirEmailCadastrado() {

		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

		usuarioService.validarEmail("email@email.com");
	}

}
