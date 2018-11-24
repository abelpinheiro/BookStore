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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static abelpinheiro.github.io.bookstore.Data.BookContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // uma forma de password para pegar uma string passada por intent da main
    private static final String SAVE_ACTIVITY_TITLE = "SAVE_ACTIVITY_TITLE";

    // Indentificador do loader
    private static final int EXISTING_BOOK_LOADER = 0;

    // Content_URI para o livro atual. Nulo se não houver um livro puxado da list_view
    // ou inserido corretamente
    private Uri mCurrentBookUri;

    // Flag para saber se um livro teve algum campo modificado
    private boolean mBookHasChanged = false;

    // Listener para a ocorrencia de clicagem em uma view.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    // Campos da tela
    private EditText mBookTitleEditText;
    private EditText mBookGenreEditText;
    private EditText mBookPriceEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private TextView mBookQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Pegando as referências das views da XML
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        TextView informationMessage = (TextView) findViewById(R.id.information_message);
        Button dialPhone = (Button) findViewById(R.id.dial_phone_button);
        Button increaseQuantity = (Button) findViewById(R.id.increase_quantity_button);
        Button reduceQuantity = (Button) findViewById(R.id.reduce_quantity_button);
        final Button addBookButton = (Button) findViewById(R.id.save_button_view);

        mBookTitleEditText = findViewById(R.id.title_book);
        mBookGenreEditText = findViewById(R.id.genre_book);
        mBookPriceEditText = findViewById(R.id.price_book);
        mBookQuantityTextView = findViewById(R.id.quantity_book);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        // Análise da intent para verificar se está criando um novo livro ou editando um existente.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Se for nulo, é por que a tela está no modo de inserção de livros. Se não,
        // é a tela de de detalhes e edição do livro existente
        if (mCurrentBookUri == null){
            String title = getIntent().getStringExtra(SAVE_ACTIVITY_TITLE);
            setTitle(title);
            addBookButton.setText(getString(R.string.save_button));
            deleteButton.setVisibility(View.GONE);
            dialPhone.setVisibility(View.GONE);
            informationMessage.setText(getString(R.string.information_message_editor_layout));
        }else {
            setTitle(getString(R.string.title_activity_edit));
            informationMessage.setText(getString(R.string.detail_message_editor_layout));
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Listener para verificar se o usuário modificou algum destes campos. Assim, podemos
        // saber se há mudanças não salvas, permitindo o app saber e notificar o usuario caso
        // ele tente sair sem salvar as mudanças feitas.
        mBookTitleEditText.setOnTouchListener(mTouchListener);
        mBookGenreEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityTextView.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        // Listener para o botão de discagem. Executa o método de discagem
        dialPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber();
            }
        });

        // Listener para o botão de decremento. Executa o método de decremento de quantidade
        reduceQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean quantityState = false;
                changeQuantity(quantityState);
            }
        });

        // Listener para o botão de incremento. Executa o método de incremento de quantidade
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean quantityState = true;
                changeQuantity(quantityState);
            }
        });

        // Listener para o botão de salvar livro. Executa o método de salvar novo livro ou
        // atualizar um já existente
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
                finish();
            }
        });

        // Listener para o botão de exclusão de livro.
        Button deleteBookButton = (Button) findViewById(R.id.delete_button);
        deleteBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    }

    /**
     * Método vai incrementar ou decrementar a quantidade de livros, sem permitir que fique abaixo de 0
     * @param change se verdadeiro, foi clicado o botão de incremento. Se falso, decremento
     */
    private void changeQuantity(boolean change){
        String quantityValue = mBookQuantityTextView.getText().toString();
        int newQuantity;
        if (change){
            // Verifica se a String obtida de quantidade é vazia. Se for, recebe 0.
            // Se não, retorna em int a string.
            if (quantityValue.isEmpty()){
                newQuantity = 0;
            }else {
                newQuantity = Integer.parseInt(quantityValue);
            }
            // Incremento da quantidade
            mBookQuantityTextView.setText(String.valueOf(newQuantity + 1));
        }else {
            // Decremento. Se a string obtida for 0 ou vazia, não faz nada. Se for algum
            // outro numero, decrementa.
            if (quantityValue.isEmpty() || quantityValue.equals("0")){
                return;
            }else{
                newQuantity = Integer.parseInt(quantityValue);
                mBookQuantityTextView.setText(String.valueOf(newQuantity - 1));
            }
        }
    }

    /**
     * Ao pressionar no botão de discagem, pega o telefone do fornecedor e envia para o
     * app de ligação default do android
     */
    private void dialPhoneNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String phoneNumber = mSupplierPhoneEditText.getText().toString();
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    /**
     *
     * Método para inserir no banco de dados um livro
     */
    public void saveBook(){
        // Recebe os dados de um livro dos campos da tela
        String title = mBookTitleEditText.getText().toString().trim();
        String genre = mBookGenreEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        String quantityString = mBookQuantityTextView.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = PhoneNumberUtils.formatNumber(mSupplierPhoneEditText.getText().toString(), "BR");

        // Se tentar salvar com todos os parâmetros vazios, a tela não insere no banco e retorna para a main
        if (mCurrentBookUri == null && TextUtils.isEmpty(title) && TextUtils.isEmpty(genre) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)){
            return;
        }

        // Se tentar salvar com um parâmetro vazio, é lançado um toast avisando e retorna para a main,
        // sem inserir no banco de dados um elemento com campos nulos
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(genre) || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhone)){
            Toast.makeText(this, "Você não pode salvar um livro sem preencher todos os dados!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fazendo um cast da string para int de preço
        Integer price = 0;
        if (!priceString.isEmpty()){
            price = Integer.parseInt(priceString);
        }

        // Fazendo um cast da string para int de quantidade
        Integer quantity = 0;
        if (!quantityString.isEmpty()){
            quantity = Integer.parseInt(mBookQuantityTextView.getText().toString());
        }

        // Verificando se o usuario não colocou um preço aceitável no livro (maior que 0)
        if (price.equals(0)){
            Toast.makeText(this, "0 não é um preço aceitável. Por favor modifique!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificando se o usuario não colocou uma quantidade aceitável no livro (maior que 0)
        if (quantity.equals(0)){
            Toast.makeText(this, "0 não é uma quantidade aceitável. Por favor modifique!", Toast.LENGTH_SHORT).show();
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

        // Verifica se é um livro novo (realizando assim a inserção no banco)
        // ou uma já existente (fazendo assim um update)
        if (mCurrentBookUri == null){
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
            if (newUri == null){
                Toast.makeText(this, getString(R.string.editor_save_book_failed), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, getString(R.string.editor_save_book_successful), Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, contentValues, null, null);
            if (rowsAffected == 0){
                Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, getString(R.string.editor_update_book_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * mostra um diálogo para avisar ao usuário que está saindo da tela de edição sem ter salvo
     * suas alterações no livro
     * @param discardButtonListener
     */
    private void showUnsavedChangeDialog(DialogInterface.OnClickListener discardButtonListener){
        // Cria um AlertDialog e seta a mensagem e o listener
        // para a mensagem positiva e negativa
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_dialog_change_message);
        builder.setPositiveButton(R.string.discard_message, discardButtonListener);
        builder.setNegativeButton(R.string.keep_editing_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Se clicado em continuar editando, ele vai cancelar o evento de retornar para a main
                // e continuar na tela de edição com o livro
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        // Gera a tela de dialogo
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Método chamado quando o botão de retorno em cima é chamado
     * @param menuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                // Se o livro não tiver sido alterado, permitir que o retorno seja executado
                if (!mBookHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Se tiver algo alterado, criar um diálogo pra avisar ao usuario sobre descarte
                // de alterações não salvas
                DialogInterface.OnClickListener discardListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // gerar o diálogo de descartar alterações não salvas
                showUnsavedChangeDialog(discardListener);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     *
     * Método chamado quando o botão de back é pressionado
     */
    @Override
    public void onBackPressed(){
        // Se o livro não tiver sido alterado, permitir que o onback seja executado
        if (!mBookHasChanged){
            super.onBackPressed();
            return;
        }

        // Se tiver algo alterado, criar um diálogo pra avisar ao usuario sobre descarte
        // de alterações não salvas
        DialogInterface.OnClickListener discardListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        // gerar o diálogo de descartar alterações não salvas
        showUnsavedChangeDialog(discardListener);
    }

    /**
     * Pergunta ao usuário se ele quer deletar o livro
     */
    private void showDeleteConfirmationDialog() {
        // Cria um AlertDialog e seta a mensagem e o listener
        // para a mensagem positiva e negativa
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Usuario clicou no botão de deletar, deleta o livro do banco.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Usuario clicou no botão de cancelar, retornando para a tela de edição
                // com o livro atual
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Gera a tela de dialogo
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Ao pressionar o botão deletar, vai remover o livro do banco de dados
     */
    private void deleteBook() {
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
        // Verificação da linha do cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Move a linha para a primeira do cursor
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

            // Setando nos campos os dados
            mBookTitleEditText.setText(title);
            mBookPriceEditText.setText(Integer.toString(price));
            mBookQuantityTextView.setText(Integer.toString(quantity));
            mBookGenreEditText.setText(genre);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // limpa os campos caso o loader fique invalido
        mBookTitleEditText.setText("");
        mBookPriceEditText.setText("");
        mBookQuantityTextView.setText("");
        mBookGenreEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}