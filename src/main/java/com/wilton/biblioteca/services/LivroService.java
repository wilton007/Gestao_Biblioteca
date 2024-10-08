package com.wilton.biblioteca.services;

import com.wilton.biblioteca.dtos.LivroRequestDto;
import com.wilton.biblioteca.dtos.LivroResponseDto;
import com.wilton.biblioteca.exception.ExceptionPersonalizada;
import com.wilton.biblioteca.mappers.LivroMapper;
import com.wilton.biblioteca.model.Livro;
import com.wilton.biblioteca.repositorys.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivroService {

    @Autowired
    private LivroRepository repository;
    @Autowired
    private LivroMapper mapper;

    public LivroResponseDto cadastrarLivro(LivroRequestDto requestDto) {
        validarIsbnUnico(requestDto.getIsbn());
        return mapper.toLivroResponseDto(repository.save(mapper.toLivro(requestDto)));
    }

    public List<Object> mostrarTodosLivros() {
        return mapper.toListLivros(verificarListaSeEstaVazia());
    }

    private void validarIsbnUnico(long isbn) {
        if (repository.existsById(isbn)) {
            throw new ExceptionPersonalizada("ISBN já cadastrado", 409);
        }
    }

    private List<Livro> verificarListaSeEstaVazia() {
        List<Livro> livros = repository.findAll();

        if (livros.isEmpty()) {
            throw new ExceptionPersonalizada("lista vazia", 404);
        }
        return livros.stream()
                .filter(l -> l.getQuantidade() > 0)
                .collect(Collectors.toList());
    }

}
