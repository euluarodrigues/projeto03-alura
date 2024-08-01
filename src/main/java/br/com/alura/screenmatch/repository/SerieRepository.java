package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double valorAvaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalDeTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer temporadas, Double avaliacao);

    @Query("SELECT s FROM Serie s WHERE s.totalDeTemporadas <= :temporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer temporadas, Double avaliacao);

    @Query("SELECT e FROM Episodio e WHERE e.titulo ILIKE %:trecho%")
    List<Episodio> episodioPorTrecho(String trecho);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE LOWER(s.titulo) = LOWER(:titulo) ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> top5Episodios(String titulo);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataDeLancamento) >= :ano")
    List<Episodio> episodiosPorSeriePorAno(Serie serie, Integer ano);

    @Query("SELECT s FROM Serie s " +
            "JOIN s.episodios e WHERE e.dataDeLancamento IS NOT NULL " +
            "GROUP BY s ORDER BY MAX(e.dataDeLancamento) DESC LIMIT 5")
    List<Serie> seriesLancamentos();

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :temporada")
    List<Episodio> episodiosPorTemporada(Long id, Integer temporada);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> top5EpisodiosPorSerie(Long id);
}
