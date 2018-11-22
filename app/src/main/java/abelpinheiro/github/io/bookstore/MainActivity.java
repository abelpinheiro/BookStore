package abelpinheiro.github.io.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import abelpinheiro.github.io.bookstore.Data.BookContract.BookEntry;
import abelpinheiro.github.io.bookstore.Data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identificador do loader
    private static final int BOOK_LOADER = 0;

    // Pega a referência do adapter
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Listener do botão flutuante de adição de elemento. Ao clicar, vai abrir a tela para
        // inserir elemento no banco de dados
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

        // Pega a referência da ListView.
        ListView bookListView = findViewById(R.id.list);

        // Seta o container de empty_view para caso não houver itens no banco de dados
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Instanciação do adapter
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        // Listener da ListView. Abre em EditorActivity o elemento clicado.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        // Prepara o loader (reconectando com um existente ou criando um novo)
        getSupportLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     *
     * Método para inserir no banco de dados um elemento ficticio para testar a
     * funcionalidade do banco.
     *
     * Como o unico modo de inserção é de um dado default, não há necessidade de tratar
     * possíveis erros de inserção
     */
    public void insertBook(){
        //Instancia um contentValue para armazenar as chaves-valores do elemento
        ContentValues contentValues = new ContentValues();

        contentValues.put(BookEntry.COLUMNS_BOOK_NAME, "O senhor dos anéis");
        contentValues.put(BookEntry.COLUMNS_BOOK_PRICE, 160);
        contentValues.put(BookEntry.COLUMNS_BOOK_QUANTITY, 1);
        contentValues.put(BookEntry.COLUMNS_BOOK_GENRE, "Fantasia");
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_NAME, "Cultura");
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_PHONE, "988276752");

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
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
                insertBook();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Método chamado quando um Loader é criado
     * @param i
     * @param bundle
     * @return um cursorloader
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                BookEntry._id,
                BookEntry.COLUMNS_BOOK_NAME,
                BookEntry.COLUMNS_BOOK_PRICE,
                BookEntry.COLUMNS_BOOK_QUANTITY
        };

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    /**
     *
     * Realiza a mudança do cursor no loader
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    /**
     *
     * Chamado quando onLoadFinished é executado, reiniciando o loader com os novos dados
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}