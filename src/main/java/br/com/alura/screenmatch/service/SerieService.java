package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodiosDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repository;

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(serie -> new SerieDTO(serie.getId(), serie.getTitulo(), serie.getTotalDeTemporadas(),
                        serie.getAvaliacao(), serie.getGenero(), serie.getAtores(),
                        serie.getSinopse(), serie.getPoster()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterTodasAsSeries() {
        return converteDados(repository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterSeriesLancamentos() {
        return converteDados(repository.seriesLancamentos());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalDeTemporadas(),
                    s.getAvaliacao(), s.getGenero(), s.getAtores(),
                    s.getSinopse(), s.getPoster());
        } else {
            return null;
        }
    }

    public List<EpisodiosDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodiosDTO(e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodiosDTO> obterTemporada(Long id, Integer temporada) {
        return repository.episodiosPorTemporada(id, temporada).stream()
                .map(episodio -> new EpisodiosDTO(episodio.getTitulo(), episodio.getTemporada(), episodio.getNumeroEpisodio()))
                .collect(Collectors.toList());
        }

    public List<SerieDTO> obterSeriesPorCategoria(String genero) {
        return converteDados(repository.findByGenero(Categoria.fromPortugues(genero)));
    }

    public List<EpisodiosDTO> obterTop5Episodios(Long id) {
        return repository.top5EpisodiosPorSerie(id)
                .stream()
                .map(e -> new EpisodiosDTO(e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }
}
