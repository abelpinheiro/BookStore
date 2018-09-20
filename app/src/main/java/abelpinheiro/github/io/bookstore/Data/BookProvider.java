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

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    private static final int BOOK = 100;

    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOK);

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mBookDbHelper;

    @Override
    public boolean onCreate() {
        mBookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mBookDbHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)){
            case BOOK:
                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
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

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
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

    private Uri insertPet(Uri uri, ContentValues values) {
        //TODO FAZER VALIDAÇÃO DOS DADOS AO INSERIR
        String title = values.getAsString(BookEntry.COLUMNS_BOOK_NAME);
        if (title.isEmpty()){
            throw new IllegalArgumentException("Título do livro não pode ser nulo.");
        }

        String genre = values.getAsString(BookEntry.COLUMNS_BOOK_GENRE);
        if (genre.isEmpty()){
            throw new IllegalArgumentException("Gênero do livro não pode ser nulo.");
        }

        String supplierName = values.getAsString(BookEntry.COLUMNS_SUPPLIER_NAME);
        if (supplierName.isEmpty()){
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

        // Get writeable database
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Não foi possível inserir no banco: " + uri);
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        return 0;
    }
}