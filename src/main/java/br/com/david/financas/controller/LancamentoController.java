package br.com.david.financas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.david.financas.controller.dto.AtualizaStatusDto;
import br.com.david.financas.controller.dto.LancamentoDto;
import br.com.david.financas.models.Lancamento;
import br.com.david.financas.models.Usuario;
import br.com.david.financas.models.enums.StatusLancamento;
import br.com.david.financas.models.enums.TipoLancamento;
import br.com.david.financas.service.LancamentoService;
import br.com.david.financas.service.UsuarioService;
import br.com.david.financas.service.impl.exceptions.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService service;
	private final UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<?> buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano, @RequestParam("usuario") Long id) {

		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao(descricao);
		lancamento.setMes(mes);
		lancamento.setAno(ano);

		Optional<Usuario> usuario = usuarioService.obterPorId(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi póssivel realizar a consulta. Usuário não encontrado.");
		} else {
			lancamento.setUsuario(usuario.get());
		}

		List<Lancamento> lancamentos = service.buscar(lancamento);
		return ResponseEntity.ok(lancamentos);

	}

	@PostMapping
	public ResponseEntity<Object> salvar(@RequestBody LancamentoDto dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity<Object>(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PutMapping("{id}")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDto dto) {
		return service.obterPorId(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity<?> atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDto dto) {
		return service.obterPorId(id).map(entity -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

			if (statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possivel atualizar o status do lançamento.");
			}

			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entity -> {
			service.deletar(entity);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}

	private Lancamento converter(LancamentoDto dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

		lancamento.setUsuario(usuario);
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));

		}

		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));

		}

		return lancamento;

	}

}
