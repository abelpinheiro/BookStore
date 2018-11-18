package abelpinheiro.github.io.bookstore;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import abelpinheiro.github.io.bookstore.Data.BookContract;

import static abelpinheiro.github.io.bookstore.Data.BookContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SAVE_ACTIVITY_TITLE = "SAVE_ACTIVITY_TITLE";

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    private EditText mBookTitleEditText;
    private EditText mBookGenreEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button deleteButton = (Button) findViewById(R.id.delete_button);
        TextView informationMessage = (TextView) findViewById(R.id.information_message);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Se for nulo, é por que a tela está no modo de inserção de livros. Se não,
        // é a tela de de detalhes e edição do livro existente
        if (mCurrentBookUri == null){
            String title = getIntent().getStringExtra(SAVE_ACTIVITY_TITLE);
            setTitle(title);
            deleteButton.setVisibility(View.GONE);
            informationMessage.setText(getString(R.string.information_message_editor_layout));
        }else {
            setTitle(getString(R.string.title_activity_edit));
            informationMessage.setText(getString(R.string.detail_message_editor_layout));
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mBookTitleEditText = findViewById(R.id.title_book);
        mBookGenreEditText = findViewById(R.id.genre_book);
        mBookPriceEditText = findViewById(R.id.price_book);
        mBookQuantityEditText = findViewById(R.id.quantity_book);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        mBookTitleEditText.setOnTouchListener(mTouchListener);
        mBookGenreEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        final Button addBookButton = (Button) findViewById(R.id.save_button_view);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
                finish();
            }
        });

        Button deleteBookButton = (Button) findViewById(R.id.delete_button);
        deleteBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
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
    public void saveBook(){
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

        if (mCurrentBookUri == null && TextUtils.isEmpty(title) && TextUtils.isEmpty(genre) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)){
            return;
        }

        //Instancia um contentValue para armazenar as chaves-valores do elemento
        ContentValues contentValues = new ContentValues();

        contentValues.put(BookEntry.COLUMNS_BOOK_NAME, title);
        contentValues.put(BookEntry.COLUMNS_BOOK_PRICE, price);
        contentValues.put(BookEntry.COLUMNS_BOOK_QUANTITY, quantity);
        contentValues.put(BookEntry.COLUMNS_BOOK_GENRE, genre);
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_NAME, supplierName);
        contentValues.put(BookEntry.COLUMNS_SUPPLIER_PHONE, supplierPhone);

        if (mCurrentBookUri == null){
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
            if (newUri == null){
                Toast.makeText(this, "ERRO AO ADICIONAR ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, contentValues, null, null);
            if (rowsAffected == 0){
                Toast.makeText(this, "ERRO AO ATUALIZAR ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUnsavedChangeDialog(DialogInterface.OnClickListener discardButtonListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_dialog_change_message);
        builder.setPositiveButton(R.string.discard_message, discardButtonListener);
        builder.setNegativeButton(R.string.keep_editing_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                if (!mBookHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangeDialog(discardListener);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed(){
        if (!mBookHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        showUnsavedChangeDialog(discardListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        if (mCurrentBookUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0){
                Toast.makeText(this, getString(R.string.editor_delete_book_failed), Toast.LENGTH_SHORT ).show();
            }else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful), Toast.LENGTH_SHORT ).show();
            }
        }

        finish();
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

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()){
            // Acha as colunas de atributos pet em que estamos interessados
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_QUANTITY);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_GENRE);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_SUPPLIER_PHONE);

            // Extrai o valor do Cursor para o índice de coluna dado
            String title = cursor.getString(titleColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String genre = cursor.getString(genreColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mBookTitleEditText.setText(title);
            mBookPriceEditText.setText(Integer.toString(price));
            mBookQuantityEditText.setText(Integer.toString(quantity));
            mBookGenreEditText.setText(genre);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}