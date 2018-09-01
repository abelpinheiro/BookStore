package abelpinheiro.github.io.bookstore.Data;

import android.provider.BaseColumns;

/**
 *
 * Uma API de contrato para utilizar e referênciar variaveis dos bancos de dados
 * (i.e a ID  ou o nome da tabela) por todas as classes do aplicativo.
 */
public final class BookContract {

    //Impedir a possibilidade de instanciação da classe contrato por acidente
    private BookContract(){}

    /**
     *
     * Classe interna que contém as constantes utilizadas na tabela de Books do banco
     */
    public static final class BookEntry implements BaseColumns{

        //Nome da tabela para livros
        public static final String TABLE_NAME = "books";

        //ID unica para a tabela de livros, de tipo INTEGER
        public static final String _id = BaseColumns._ID;

        //Titulo do livro, de tipo TEXT
        public static final String COLUMNS_BOOK_NAME = "name";

        //Preço do livro, do tipo INTEGER
        public static final String COLUMNS_BOOK_PRICE = "price";

        //Quantidade disponivel do livro, de tipo INTEGER
        public static final String COLUMNS_BOOK_QUANTITY = "quantity";

        //Gênero do livro, do tipo TEXT
        public static final String COLUMNS_BOOK_GENRE = "genre";

        //Nome do fornecedor, de tipo TEXT
        public static final String COLUMNS_SUPPLIER_NAME = "supplierName";

        //Telefone do Fornecedor, do tipo TEXT
        public static final String COLUMNS_SUPPLIER_PHONE = "supplierPhone";

    }
}