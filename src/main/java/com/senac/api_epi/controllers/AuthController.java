
package com.senac.api_epi.controllers;

import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.repositories.FuncionarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> dados) {

        String matricula = dados.get("matricula");
        String senha = dados.get("senha");

        // Verifica se os campos foram enviados
        if (matricula == null || senha == null ||
                matricula.isBlank() || senha.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "erro", "Informe matrícula e senha"
                    ));
        }

        // Procura o funcionário no banco de dados
        Optional<Funcionario> funcionario =
                funcionarioRepository.findByMatriculaAndSenha(
                        matricula.trim(),
                        senha
                );

        // Verifica se a matrícula e a senha existem
        if (funcionario.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "erro", "Matrícula ou senha inválidas"
                    ));
        }

        Funcionario usuario = funcionario.get();

        // Retorna os dados do funcionário autenticado
        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "matricula", usuario.getMatricula(),
                "nome", usuario.getNome(),
                "setor", usuario.getSetor(),
                "perfil", usuario.getPerfil()
        ));
    }
}
