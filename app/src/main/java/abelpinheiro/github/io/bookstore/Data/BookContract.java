package abelpinheiro.github.io.bookstore.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Uma API de contrato para utilizar e referênciar variaveis dos bancos de dados
 * (i.e a ID  ou o nome da tabela) por todas as classes do aplicativo.
 */
public final class BookContract {

    //Impedir a possibilidade de instanciação da classe contrato por acidente
    private BookContract(){}

    public static final String CONTENT_AUTHORITY = "abelpinheiro.github.io.bookstore";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";

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

        // Content URI para acessar o livro no provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        // MIME para um único livro
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // MIME para retornar todos os livros
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
    }
}