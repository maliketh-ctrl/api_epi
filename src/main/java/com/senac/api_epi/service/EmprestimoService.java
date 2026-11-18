package com.senac.api_epi.service;

import com.senac.api_epi.entities.Emprestimo;
import com.senac.api_epi.entities.Epi;
import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.repositories.EmprestimoRepository;
import com.senac.api_epi.repositories.EpiRepository;
import com.senac.api_epi.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EpiRepository epiRepository;

    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            FuncionarioRepository funcionarioRepository,
            EpiRepository epiRepository) {

        this.emprestimoRepository = emprestimoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.epiRepository = epiRepository;
    }

    public Emprestimo solicitar(Emprestimo pedido) {

        if (pedido.getFuncionario() == null
                || pedido.getFuncionario().getId() == null
                || pedido.getEpi() == null
                || pedido.getEpi().getId() == null) {

            throw new IllegalArgumentException(
                    "Informe o funcionário e o EPI");
        }

        Funcionario funcionario =
                funcionarioRepository.findById(
                                pedido.getFuncionario().getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Funcionário não encontrado"));

        Epi epi =
                epiRepository.findById(
                                pedido.getEpi().getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "EPI não encontrado"));

        if (epi.isCaVencido()) {
            throw new IllegalArgumentException(
                    "EPI com CA vencido não pode ser emprestado");
        }

        if (epi.getQuantidadeDisponivel() <= 0) {
            throw new IllegalArgumentException(
                    "EPI sem estoque disponível");
        }

        boolean jaSolicitou =
                emprestimoRepository
                        .existsByFuncionarioIdAndEpiIdAndStatus(
                                funcionario.getId(),
                                epi.getId(),
                                "SOLICITADO");

        if (jaSolicitou) {
            throw new IllegalArgumentException(
                    "Você já tem uma solicitação aberta para este EPI");
        }

        pedido.setId(null);
        pedido.setFuncionario(funcionario);
        pedido.setEpi(epi);
        pedido.setDataEmprestimo(LocalDate.now());
        pedido.setDataDevolucao(null);
        pedido.setStatus("SOLICITADO");

        return emprestimoRepository.save(pedido);
    }

    public List<Emprestimo> listarTodos() {
        return emprestimoRepository.findAllByOrderByIdDesc();
    }

    public List<Emprestimo> listarPorFuncionario(
            Long funcionarioId) {

        return emprestimoRepository
                .findByFuncionarioIdOrderByIdDesc(
                        funcionarioId);
    }
}