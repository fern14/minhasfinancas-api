package br.com.david.financas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.david.financas.models.Lancamento;
import br.com.david.financas.models.enums.StatusLancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);

	Lancamento atualizar(Lancamento lancamento);

	void deletar(Lancamento lancamento);

	List<Lancamento> buscar(Lancamento lancamentoFiltro);

	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Optional<Lancamento> obterPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);

}
