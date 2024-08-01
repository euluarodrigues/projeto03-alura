package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class PrincipalOld {
    private Scanner s = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();
    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=" + System.getenv("OMDB_APIKEY");
    private List<DadosTemporada> temporadas = new ArrayList<>();

    public void menuPrincipal() {
        System.out.println("Digite o nome da série para buscar: ");
        var serie = s.nextLine();
        var json = consumoApi.obterDados(ENDERECO + serie.replace(" ", "+") + APIKEY);
        DadosSerie dadosSerie = converte.obterDados(json, DadosSerie.class);



        for (int i = 1; i <= dadosSerie.totalDeTemporadas(); i++) {
            json = consumoApi.obterDados(ENDERECO + serie.replace(" ", "+") + APIKEY + "&season=" + i);
            DadosTemporada dadosTemporada = converte.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
            System.out.println(json);
        }
//        temporadas.forEach(System.out::println);

//        for (int i = 0; i < dadosSerie.totalDeTemporadas(); i++) {
//            System.out.println("\nTemporada: " + temporadas.get(i).numeroTemporada());
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).numeroEpisodio() + ". " + episodiosTemporada.get(j).titulo());
//            }
//        }
//
//        temporadas.forEach(t -> {
//            System.out.println("\nTemporada " + t.numeroTemporada());
//            t.episodios().forEach(e -> {
//                System.out.println(e.numeroEpisodio() + ". " + e.titulo());
//            });
//        });
//
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("\n Top 5 episódios: ");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);

        //Populando a classe Episódio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemporada(), d)))
                .collect(Collectors.toList());

//        episodios.forEach(System.out::println);

//        System.out.println("Digite um trecho do espisódio a ser buscado:");
//        var trechoTitulo = s.nextLine();
//
//        //BUSCANDO A PRIMEIRA REFERÊNCIA DO TRECHO
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()) {
//            System.out.println(
//                        "Temporada: " + episodioBuscado.get().getTemporada()
//                                + ", Episódio: " + episodioBuscado.get().getNumeroEpisodio()
//                                + ", Título: " + episodioBuscado.get().getTitulo()
//                                + ", Data de lançamento: " + episodioBuscado.get().getDataDeLancamento());
//        } else {
//            System.out.println("Episódio não encontrado.");
//        }
//
//        //BUSCANDO MAIS DE UM EPISÓDIO COM O TRECHO
//        episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada()
//                                + ", Episódio: " + e.getNumeroEpisodio()
//                                + ", Título: " + e.getTitulo()
//                                + ", Data de lançamento: " + e.getDataDeLancamento()));
//
//        //FILTRANDO POR ANO
//        System.out.println("A partir de que ano você deseja buscar os episódios?");
//        var ano = s.nextInt();
//        s.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataDeLancamento() != null && e.getDataDeLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada()
//                                + ", Episódio: " + e.getNumeroEpisodio()
//                                + ", Título: " + e.getTitulo()
//                                + ", Data de lançamento: " + e.getDataDeLancamento().format(dtf)));


        //Streams com episódios
        System.out.println("\n Map média avaliações: ");
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        System.out.println("\n Map estatísticas por temporada: ");
        Map<Integer, DoubleSummaryStatistics> estatisticasPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.summarizingDouble(Episodio::getAvaliacao)));
        System.out.println(estatisticasPorTemporada);

        System.out.println();
        System.out.println("Iteração sobre map de estatísticas por temporada");
        estatisticasPorTemporada.forEach((temporada, est) -> {
            System.out.println("Temporada: " + temporada);
            System.out.println("Média: " + String.format("%.2f", est.getAverage()));
            System.out.println("Mín: " + String.format("%.2f", est.getMin()));
            System.out.println("Max: " + String.format("%.2f", est.getMax()));
            System.out.println();
        });

        System.out.println("\nEstatíticas gerais dos episódios: ");
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + String.format("%.2f", est.getAverage()));
        System.out.println("Mín: " + String.format("%.2f", est.getMin()));
        System.out.println("Max: " + String.format("%.2f", est.getMax()));
    }
}
