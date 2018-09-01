package abelpinheiro.github.io.bookstore;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import abelpinheiro.github.io.bookstore.Data.BookContract.BookEntry;
import abelpinheiro.github.io.bookstore.Data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    private BookDbHelper mBookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBookDbHelper = new BookDbHelper(this);
    }

    /**
     *
     * Método para inserir no banco de dados um elemento ficticio para testar a
     * funcionalidade do banco
     */
    public void insertDummyData(){
        //Recebe o banco em modo de escrita
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        //Instancia um contentValue para armazenar as chaves-valores do elemento
        ContentValues contentValues = new ContentValues();

        contentValues.put(BookEntry.COLUMNS_BOOK_NAME, "O senhor dos anéis");
        contentValues.put(BookEntry.COLUMNS_BOOK_PRICE, 160);
        contentValues.put(BookEntry.COLUMNS_BOOK_QUANTITY, 1);
        contentValues.put(BookEntry.COLUMNS_BOOK_GENRE, "Fantasia");
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_NAME, "Cultura");
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_PHONE, "988276752");

        //Inserção dos atributos do elemento em uma linha da tabela do banco de dados, retornando
        //o ID da nova linha
        Long rowId = database.insert(BookEntry.TABLE_NAME, null, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertDummyData();
                //TODO display novo dado
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
