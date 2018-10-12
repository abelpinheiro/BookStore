package abelpinheiro.github.io.bookstore;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import abelpinheiro.github.io.bookstore.Data.BookContract;

public class EditorActivity extends AppCompatActivity {

    private static final String SAVE_ACTIVITY_TITLE = "SAVE_ACTIVITY_TITLE";

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
        String title = getIntent().getStringExtra(SAVE_ACTIVITY_TITLE);
        setTitle(title);

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

        contentValues.put(BookContract.BookEntry.COLUMNS_BOOK_NAME, title);
        contentValues.put(BookContract.BookEntry.COLUMNS_BOOK_PRICE, price);
        contentValues.put(BookContract.BookEntry.COLUMNS_BOOK_QUANTITY, quantity);
        contentValues.put(BookContract.BookEntry.COLUMNS_BOOK_GENRE, genre);
        contentValues.put(BookContract.BookEntry.COLUMNS_SUPPLIER_NAME, supplierName);
        contentValues.put(BookContract.BookEntry.COLUMNS_SUPPLIER_PHONE, supplierPhone);

        Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, contentValues);
        if (newUri == null){
            Toast.makeText(this, "ERRO AO ADICIONAR ", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

}