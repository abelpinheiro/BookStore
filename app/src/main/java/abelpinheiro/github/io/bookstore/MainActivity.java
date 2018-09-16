package abelpinheiro.github.io.bookstore;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import abelpinheiro.github.io.bookstore.Data.BookContract.BookEntry;
import abelpinheiro.github.io.bookstore.Data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    //Atributo para auxiliar a interação com o banco pelos métodos
    private BookDbHelper mBookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBookDbHelper = new BookDbHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                String activityTitle = getString(R.string.title_activity_save);
                intent.putExtra("SAVE_ACTIVITY_TITLE", activityTitle);
                startActivity(intent);
            }
        });

        ListView bookListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);
    }

    /**
     *
     * Método para inserir no banco de dados um elemento ficticio para testar a
     * funcionalidade do banco.
     *
     * Como o unico modo de inserção é de um dado default, não há necessidade de tratar no momento
     * possíveis erros de inserção, como tirar os espaços vazios e etc
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

    /**
     *
     * Override no ciclo de vida onStart da activity para fazer uma query dos dados atualmente inseridos no banco
     */
    @Override
    protected void onStart() {
        super.onStart();
        queryData();
    }

    /**
     *
     * Realiza uma busca no banco de dados e retorna todos os dados inseridos no banco
     */
    public void queryData(){
        //Recebe o banco de dados em modo de leitura
        SQLiteDatabase database = mBookDbHelper.getReadableDatabase();

        //Array de strings que contém as colunas que serão buscadas no banco
        String[] projection = {
                BookEntry._id,
                BookEntry.COLUMNS_BOOK_NAME,
                BookEntry.COLUMNS_BOOK_PRICE,
                BookEntry.COLUMNS_BOOK_QUANTITY,
                BookEntry.COLUMNS_BOOK_GENRE,
                BookEntry.COLUMNS_SUPPLIER_NAME,
                BookEntry.COLUMNS_SUPPLIER_PHONE
        };

        //Realiza uma busca no banco de dados pela tabela BookEntry.TABLE_NAME
        Cursor cursor = database.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        //Mensagem do sistema indicando a quantidade de livros existentes no banco
        Log.i("MainActivity.java", "A TABELA TEM " + cursor.getCount() + " livros");

        //Mensagem do sistema indicando as colunas do banco de dados de livros
        Log.i("MainActivity.java",
                BookEntry._id + " - " +
                BookEntry.COLUMNS_BOOK_NAME + " - " +
                BookEntry.COLUMNS_BOOK_PRICE + " - " +
                BookEntry.COLUMNS_BOOK_QUANTITY + " - " +
                BookEntry.COLUMNS_BOOK_GENRE + " - " +
                BookEntry.COLUMNS_SUPPLIER_NAME + " - " +
                BookEntry.COLUMNS_SUPPLIER_PHONE);

        try {

            //Pegando o indice de cada coluna do banco
            int idColumnIndex = cursor.getColumnIndex(BookEntry._id);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_QUANTITY);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_GENRE);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_SUPPLIER_PHONE);

            //Iteração por todas as linhas do Cursor
            while (cursor.moveToNext()) {

                //Pega os elementos da linha atual em seus respectivos tipos do cursor
                long id = cursor.getLong(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                int price = cursor.getInt(priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                String genre = cursor.getString(genreColumnIndex);
                String supplierName = cursor.getString(supplierNameColumnIndex);
                String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

                //Mensagem do sistema descrevendo os elementos de cada livro inserido no banco
                Log.i("MainActivity.java",
                        Long.toString(id) + " - " +
                                name + " - " +
                                price + " - " +
                                quantity + " - " +
                                genre + " - " +
                                supplierName + " - " +
                                supplierPhone
                );
            }
        }finally {
            //Encerramento do cursor
            cursor.close();
        }
    }

    /**
     *
     * Inflando o layout do menu na activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     *
     * Caso pressionado um item do menu, irá executar sua ação específica
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertDummyData();
                queryData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
