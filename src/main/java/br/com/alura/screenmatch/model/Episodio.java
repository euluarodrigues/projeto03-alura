package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "episodios")
public class Episodio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private Integer temporada;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataDeLancamento;
    @ManyToOne
    private Serie serie;

    public Episodio(){}

    public Episodio(Integer numeroTemporada, DadosEpisodio d) {
        this.titulo = d.titulo();
        this.temporada = numeroTemporada;
        this.numeroEpisodio = d.numeroEpisodio();
        try {
            this.avaliacao = Double.valueOf(d.avaliacao());
        } catch (NumberFormatException ex) {
            this.avaliacao = 0.0;
        }
        try {
            this.dataDeLancamento = LocalDate.parse(d.dataDeLancamento());
        } catch (DateTimeParseException ex) {
            this.dataDeLancamento = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataDeLancamento() {
        return dataDeLancamento;
    }

    public void setDataDeLancamento(LocalDate dataDeLancamento) {
        this.dataDeLancamento = dataDeLancamento;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    @Override
    public String toString() {
        return  "Temporada: " + temporada +
                ", Episodio: " + numeroEpisodio +
                ", Título: " + titulo  +
                ", Avaliacão: " + avaliacao +
                ", Data de lançamento: " + dataDeLancamento;
    }
}
