package com.wilton.biblioteca.services;

import com.wilton.biblioteca.dtos.*;
import com.wilton.biblioteca.exception.ExceptionPersonalizada;
import com.wilton.biblioteca.mappers.UsuarioMapper;
import com.wilton.biblioteca.model.Emprestimo;
import com.wilton.biblioteca.model.Livro;
import com.wilton.biblioteca.model.Usuario;
import com.wilton.biblioteca.repositorys.EmprestimoRepository;
import com.wilton.biblioteca.repositorys.LivroRepository;
import com.wilton.biblioteca.repositorys.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository repository;
    @Autowired
    LivroRepository livroRepository;
    @Autowired
    EmprestimoRepository emprestimoRepository;
    @Autowired
    UsuarioMapper mapper;

    public UsuarioResponseDto salvarUsuario(UsuarioRequestDto requestDto) {
        verificarEmailExistente(requestDto.getEmail());
        return mapper.toUsuarioResponseDto(repository.save(mapper.toUsuario(requestDto)));
    }

    public List<Object> listarUsuarios() {
        return mapper.toListUsuarioResponseDto(verificarListaSeEstaVazia());
    }

    public EmprestimoResponseDto pegarLivroEmprestado(EmprestimoRequestDto requestDto) {

        Emprestimo emprestimo = new Emprestimo(verificarQuantidadeDeLivrosNaBilbioteca(requestDto.getIsbn())
                , verificarUsuarioExiste(requestDto.getId_Usuario()), LocalDate.now());
        emprestimoRepository.save(emprestimo);
        return mapper.toEmprestimoResponseDto(emprestimo);
    }

    public List<Object> listDeEmprestimosDoUsuario(EmprestimoListRequestDto requestDto) {
        Usuario usuario = verificarUsuarioExiste(requestDto.getId_usuario());
        List<Emprestimo> list = usuario.getEmprestimos();
        return mapper.toEmprestimoListResponse(list);

    }


    private Livro verificarQuantidadeDeLivrosNaBilbioteca(long isbn) {
        Livro livro = livroRepository.findById(isbn).orElse(null);

        if (livro != null) {
            if (livro.getQuantidade() < 1) {
                throw new ExceptionPersonalizada("livo em falta", 404);
            } else {
                livro.setQuantidade(livro.getQuantidade() - 1);
                livroRepository.save(livro);
            }
            return livro;
        }
        throw new ExceptionPersonalizada("isbn não existente", 404);
    }

    private Usuario verificarUsuarioExiste(long id) {
        Usuario usuario = repository.findById(id).orElse(null);

        if (usuario != null) {

            return usuario;
        }
        throw new ExceptionPersonalizada("usuario não existente", 404);
    }

    private void verificarEmailExistente(String email) {
        if (repository.existsByEmail(email)) {
            throw new ExceptionPersonalizada("Email já cadastrado", 409);
        }
    }

    private List<Usuario> verificarListaSeEstaVazia() {
        List<Usuario> list = repository.findAll();
        if (list.isEmpty()) {
            throw new ExceptionPersonalizada("Lista de Usuario vazia", 404);
        } else {
            return list;
        }
    }
}
