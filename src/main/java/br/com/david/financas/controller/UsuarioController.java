package br.com.david.financas.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.david.financas.controller.dto.TokenDto;
import br.com.david.financas.controller.dto.UsuarioDto;
import br.com.david.financas.models.Usuario;
import br.com.david.financas.service.JwtService;
import br.com.david.financas.service.LancamentoService;
import br.com.david.financas.service.UsuarioService;
import br.com.david.financas.service.impl.exceptions.ErrorAutenticacao;
import br.com.david.financas.service.impl.exceptions.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService service;
	private final LancamentoService lancamentoService;
	private final JwtService jwtService;

	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDto dto) {

		try {

			Usuario usuarioAutenticar = service.autenticar(dto.getEmail(), dto.getSenha());
			String token = jwtService.gerarToken(usuarioAutenticar);
			TokenDto tokenDto = new TokenDto(usuarioAutenticar.getNome(), token);
			return ResponseEntity.ok(tokenDto);

		} catch (ErrorAutenticacao e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody UsuarioDto dto) {
		Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();

		try {
			Usuario usuarioSalvo = service.cadastrar(usuario);
			return new ResponseEntity<Object>(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("{id}/saldo")
	public ResponseEntity<BigDecimal> obterSaldo(@PathVariable("id") Long id) {
		Optional<Usuario> usuario = service.obterPorId(id);

		if (!usuario.isPresent()) {
			new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}

		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}

}
