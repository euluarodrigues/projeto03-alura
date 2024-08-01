package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner s = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();
    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=" + System.getenv("OMDB_APIKEY");
    private SerieRepository repository;
    List<Serie> listaSeries = new ArrayList<>();
    Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para buscar: ");
        var serie = s.nextLine();
        var json = consumoApi.obterDados(ENDERECO + serie.replace(" ", "+") + API_KEY);
        return converte.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome para buscar os episódios: ");
        var nomeSerie = s.nextLine();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
                var json = consumoApi.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + API_KEY + "&season=" + i);
                DadosTemporada dadosTemporada = converte.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(serie);
    }

    private void listarSeriesBuscadas() {
        listaSeries = repository.findAll();
        listaSeries.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = s.nextLine();

        serieBuscada = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSeriePeloNomeDoAtor() {
        System.out.println("Escreva o nome do ator: ");
        var nomeAtor = s.nextLine();
        System.out.println("Buscar avaliações a partir de qual valor: ");
        var valorAvaliacao = s.nextDouble();
        s.nextLine();

        List<Serie> listaSeries = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, valorAvaliacao);

        listaSeries.forEach(s -> System.out.println(
                s.getTitulo() + " Avaliação: " + s.getAvaliacao()
        ));
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repository.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(s -> System.out.println(
                s.getTitulo() + " Avaliação: " + s.getAvaliacao()
        ));
    }

    private void buscarCategoria() {
        System.out.println("Por qual categoria você deseja buscar: " );
        var nomeGenero = s.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> categoriaPortugues = repository.findByGenero(categoria);
        categoriaPortugues.forEach(System.out::println);
    }

    private void filtrarSeries() {
        System.out.println("Deseja ver séries de até quantas temporadas: ");
        Integer temporada = s.nextInt();
        s.nextLine();
        System.out.println("E qual é a avaliação mínima: ");
        Double avaliacao = s.nextDouble();
        s.nextLine();
        List<Serie> filtroSeries = repository.seriesPorTemporadaEAvaliacao(temporada, avaliacao);
        filtroSeries.forEach(System.out::println);
    }

    private void buscarEpisodiosPorTrecho() {
        System.out.println("Digite o trecho que deseja buscar: ");
        var trecho = s.nextLine();
        List<Episodio> episodios = repository.episodioPorTrecho(trecho);
        episodios.forEach(e -> System.out.println("Série: " + e.getSerie().getTitulo() + ", " + e));
    }

    private void top5Episodios() {
        System.out.println("Digite o nome da série: ");
        var nomeSerie = s.nextLine();
        List<Episodio> top5Episodios = repository.top5Episodios(nomeSerie);
        System.out.println("Top 5 episódios de " + top5Episodios.getFirst().getSerie().getTitulo() + ":");
        top5Episodios.forEach(System.out::println);
    }

    private void episodioPorSeriePorAno () {
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()) {
            System.out.println("A partir de qual ano deseja buscar: ");
            Integer ano = s.nextInt();
            s.nextLine();
            List<Episodio> episodiosAno = repository.episodiosPorSeriePorAno(serieBuscada.get(), ano);
            episodiosAno.forEach(System.out::println);
        }
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Séries buscadas
                    4 - Buscar série por título
                    5 - Buscar série pelo nome do ator
                    6 - Top 5 séries
                    7 - Buscar por categoria
                    8 - Filtrar séries
                    9 - Buscar episódios por trecho
                    10 - Top 5 episódios
                    11 - Buscar episódios de uma série a partir de um ano
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = s.nextInt();
            s.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5: buscarSeriePeloNomeDoAtor();
                    break;
                case 6: buscarTop5Series();
                    break;
                case 7: buscarCategoria();
                    break;
                case 8: filtrarSeries();
                    break;
                case 9: buscarEpisodiosPorTrecho();
                    break;
                case 10: top5Episodios();
                    break;
                case 11: episodioPorSeriePorAno();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }
}
