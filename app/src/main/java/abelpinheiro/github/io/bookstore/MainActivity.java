package abelpinheiro.github.io.bookstore;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import abelpinheiro.github.io.bookstore.Data.BookContract.BookEntry;
import abelpinheiro.github.io.bookstore.Data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

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

        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     *
     * Método para inserir no banco de dados um elemento ficticio para testar a
     * funcionalidade do banco.
     *
     * Como o unico modo de inserção é de um dado default, não há necessidade de tratar no momento
     * possíveis erros de inserção, como tirar os espaços vazios e etc
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

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
