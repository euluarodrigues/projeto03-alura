package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;
import jakarta.persistence.*;

public record SerieDTO(Long id,
                       String titulo,
                       Integer totalDeTemporadas,
                       Double avaliacao,
                       Categoria genero,
                       String atores,
                       String sinopse,
                       String poster) {
}
