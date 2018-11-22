package abelpinheiro.github.io.bookstore.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static abelpinheiro.github.io.bookstore.Data.BookContract.*;

/**
 * {@link BookProvider} para o aplicativo BookStore
 */
public class BookProvider extends ContentProvider {

    // Tag para mensagens de log
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    // Código URI matcher para o content URI da tabela de livros
    private static final int BOOK = 100;

    // Código URI matcher para o content URI de um único livro da tabela de livros
    private static final int BOOK_ID = 101;

    // Objeto URI para corresponder com um content URI de um dado ID
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Inicializador estático
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOK);

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    // Objeto do banco de dados
    private BookDbHelper mBookDbHelper;

    @Override
    public boolean onCreate() {

        // Instanciação do banco
        mBookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Pega o banco no modo de leitura
        SQLiteDatabase database = mBookDbHelper.getReadableDatabase();

        // Cursor para retornar o resultado da consulta
        Cursor cursor;

        // Switch para ver se a URI corresponde a um código específico ou não
        switch (sUriMatcher.match(uri)) {
            case BOOK:

                // Faz uma consulta para retornar as linhas da tabela de Books
                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BOOK_ID:

                // Pega o ID específico do content URI e faz uma consulta, retornando
                // o Book que possui esse ID do banco
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Não é possível buscar " + uri);
        }

        // Saber para que o content URI foi criado e verificar se houve alterações nele
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("URI desconhecido: " + uri + " de vaçlor: " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (sUriMatcher.match(uri)) {
            case BOOK:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Não é possível inserir " + uri);
        }
    }

    /**
     * Realiza uma inserção no banco de dados com os content values indicados.
     * @param uri
     * @param values
     * @return um novo content URI para essa linha do banco
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        //TODO FAZER VALIDAÇÃO DOS DADOS AO INSERIR
        // Chega recebe os dados da tabela e verifica se são nulos
        String title = values.getAsString(BookEntry.COLUMNS_BOOK_NAME);
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Título do livro não pode ser nulo.");
        }

        String genre = values.getAsString(BookEntry.COLUMNS_BOOK_GENRE);
        if (genre.isEmpty()) {
            throw new IllegalArgumentException("Gênero do livro não pode ser nulo.");
        }

        String supplierName = values.getAsString(BookEntry.COLUMNS_SUPPLIER_NAME);
        if (supplierName.isEmpty()) {
            throw new IllegalArgumentException("Nome do fornecedor não pode ser nulo.");
        }

        /*String title = values.getAsString(BookEntry.COLUMNS_BOOK_NAME);
        if (title == null) {
            throw new IllegalArgumentException("O livro precisa de um título");
        }
        // Check that the gender is valid
        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }
        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }*/

        // Pega o banco no modo de escrita
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        // Insere o novo livro no banco com os valores dados
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        // Se a inserção for -1, significa que falhou e retorna uma mensagem de erro
        if (id == -1) {
            Log.e(LOG_TAG, "Não foi possível inserir no banco: " + uri);
            return null;
        }

        // Notifica mudança dos dados para este URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Retorna o novo URI com o ID no final
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Obtenção do banco no modo escrita
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        // Switch para ver se a URI corresponde a um código específico ou não
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK:
                // É deletado todos os registros que coincidem com os selection e selectionArgs
                int deleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);

                // Notifica que content URI possuem dados alterados caso tenha mais de uma linha deletada
                if (deleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // Retorna quantidade de linhas deletadas
                return deleted;
            case BOOK_ID:
                // Deleta um único registro dado pelo ID na URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);

                // Notifica que content URI possuem dados alterados caso tenha mais de uma linha deletada
                if (rowDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // Retorna quantidade de linhas deletadas
                return rowDeleted;
            default:
                throw new IllegalArgumentException("Não foi possível deletar para " + uri);
        }
    }

    // Update
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Não é possível fazer update em " + uri);
        }
    }

    /**
     * Atualiza os livros com os ContentValue fornecidos. As mudanças serão feitas nos registros
     * de acordo com o selection e os selectionArgs dados.
     * Retorna o número de registros atualizados.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Verifica se a chave está presente. Se estiver, pega a string dela e verifica se é nula
        if (values.containsKey(BookEntry.COLUMNS_BOOK_NAME)){
            String title = values.getAsString(BookEntry.COLUMNS_BOOK_NAME);
            if (title == null){
                throw new IllegalArgumentException("Titulo do livro não pode ser nulo.");
            }
        }

        if (values.containsKey(BookEntry.COLUMNS_BOOK_PRICE)){
            Integer price = values.getAsInteger(BookEntry.COLUMNS_BOOK_PRICE);
            if (price == null){
                throw new IllegalArgumentException("Preço de um livro não pode ser nulo.");
            }
        }

        if (values.containsKey(BookEntry.COLUMNS_BOOK_QUANTITY)){
            Integer quantity = values.getAsInteger(BookEntry.COLUMNS_BOOK_QUANTITY);
            if (quantity == null){
                throw new IllegalArgumentException("Quantidade de livros não podem ser nulo.");
            }
        }

        if (values.containsKey(BookEntry.COLUMNS_BOOK_GENRE)){
            String genre = values.getAsString(BookEntry.COLUMNS_BOOK_GENRE);
            if (genre == null){
                throw new IllegalArgumentException("O gênero do livro não pode ser nulo.");
            }
        }

        if (values.containsKey(BookEntry.COLUMNS_SUPPLIER_NAME)){
            String supplierName = values.getAsString(BookEntry.COLUMNS_SUPPLIER_NAME);
            if (supplierName == null){
                throw new IllegalArgumentException("Nome do fornecedor não pode ser nulo.");
            }
        }

        if (values.containsKey(BookEntry.COLUMNS_SUPPLIER_PHONE)){
            String supplierPhone = values.getAsString(BookEntry.COLUMNS_SUPPLIER_PHONE);
            if (supplierPhone == null){
                throw new IllegalArgumentException("Telefone do fornecedor não pode ser nulo.");
            }
        }

        // Retorna caso não haja values para atualizar no banco de dados, não precisando fazer um update vazio
        if (values.size() == 0){
            return 0;
        }

        // Pega o banco no modo escrita e faz a atualização no banco. Se a atualização for
        // bem sucedida, ele notifica que há mudanças nos dados da URI dada
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();
        int updated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (updated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Retorna número de linhas atualizadas
        return updated;
    }
}