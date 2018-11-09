package abelpinheiro.github.io.bookstore;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import abelpinheiro.github.io.bookstore.Data.BookContract;

import static abelpinheiro.github.io.bookstore.Data.BookContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SAVE_ACTIVITY_TITLE = "SAVE_ACTIVITY_TITLE";

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;

    private EditText mBookTitleEditText;
    private EditText mBookGenreEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        Uri currentUri = intent.getData();
        if (currentUri == null){
            String title = getIntent().getStringExtra(SAVE_ACTIVITY_TITLE);
            setTitle(title);
        }else {
            setTitle(getString(R.string.title_activity_edit));
        }

        mBookTitleEditText = findViewById(R.id.title_book);
        mBookGenreEditText = findViewById(R.id.genre_book);
        mBookPriceEditText = findViewById(R.id.price_book);
        mBookQuantityEditText = findViewById(R.id.quantity_book);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        final LinearLayout addBookButton = (LinearLayout) findViewById(R.id.save_button_view);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBook();
                finish();
            }
        });

        //getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
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
        String title = mBookTitleEditText.getText().toString().trim();
        String genre = mBookGenreEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        String quantityString = mBookQuantityEditText.getText().toString().trim();

        Integer price = 0;
        if (!priceString.isEmpty()){
            price = Integer.parseInt(priceString);
        }

        Integer quantity = 0;
        if (!quantityString.isEmpty()){
            quantity = Integer.parseInt(mBookQuantityEditText.getText().toString());
        }

        String supplierName = mSupplierNameEditText.getText().toString().trim();

        String supplierPhone = PhoneNumberUtils.formatNumber(mSupplierPhoneEditText.getText().toString(), "BR");


        Log.i("EditorActivity.java", "VALOR DE TITULO É " + title + "E O SEU TIPO DE DADO É " + title.getClass().getName());
        Log.i("EditorActivity.java", "VALOR DE GENERO É " + genre);
        Log.i("EditorActivity.java", "VALOR DE PREÇO É " + price);
        Log.i("EditorActivity.java", "VALOR DE QUANTIDADE É " + quantity);
        Log.i("EditorActivity.java", "VALOR DE NOME FORNECEDOR É " + supplierName);
        Log.i("EditorActivity.java", "VALOR DE TELEFONE FORNECEDOR É " + supplierPhone);

        //Instancia um contentValue para armazenar as chaves-valores do elemento
        ContentValues contentValues = new ContentValues();

        contentValues.put(BookEntry.COLUMNS_BOOK_NAME, title);
        contentValues.put(BookEntry.COLUMNS_BOOK_PRICE, price);
        contentValues.put(BookEntry.COLUMNS_BOOK_QUANTITY, quantity);
        contentValues.put(BookEntry.COLUMNS_BOOK_GENRE, genre);
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_NAME, supplierName);
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_PHONE, supplierPhone);

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
        if (newUri == null){
            Toast.makeText(this, "ERRO AO ADICIONAR ", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                BookEntry._id,
                BookEntry.COLUMNS_BOOK_NAME,
                BookEntry.COLUMNS_BOOK_GENRE,
                BookEntry.COLUMNS_BOOK_PRICE,
                BookEntry.COLUMNS_BOOK_QUANTITY,
                BookEntry.COLUMNS_SUPPLIER_NAME,
                BookEntry.COLUMNS_SUPPLIER_PHONE
        };

        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}