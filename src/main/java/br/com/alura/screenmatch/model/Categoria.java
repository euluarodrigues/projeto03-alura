package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Ação"),
    DRAMA("Drama", "Drama"),
    COMEDIA("Comedy", "Comédia"),
    ROMANCE("Romance", "Romance"),
    TERROR("Horror", "Terror"),
    CRIME("Crime", "Crime"),
    AVENTURA("Adventure", "Aventura"),
    FANTASIA("Fantasy", "Fantasia");

    private String categoriaOmdb;
    private String categoriaPortugues;

    Categoria(String categoriaOmdb, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a série");
    }

    public static Categoria fromPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a série");
    }

}
