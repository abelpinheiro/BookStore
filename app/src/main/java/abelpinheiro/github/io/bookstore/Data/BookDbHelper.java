package abelpinheiro.github.io.bookstore.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import abelpinheiro.github.io.bookstore.Data.BookContract.BookEntry;

/**
 *
 * Classe que genrencia a criação e manutenção do banco de dados para Livros do app
 */
public class BookDbHelper extends SQLiteOpenHelper{

    //Nome do banco de dados
    private static final String DATABASE_NAME = "books.db";

    //Versão do banco de dados
    private static final int DATABASE_VERSION = 1;

    //Construtor da classe gerenciadora do banco, instancia um novo BookDbHelper
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Cria a tabela de Books quando chamado pela primeira vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry._id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMNS_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMNS_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMNS_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMNS_SUPPLIER_NAME + " TEXT, "
                + BookEntry.COLUMNS_SUPPLIER_PHONE + " TEXT);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    //chamado quando o banco precisa realizar uma atualização de versão
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
