package abelpinheiro.github.io.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import abelpinheiro.github.io.bookstore.Data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Cria um novo {@link BookCursorAdapter}.
     *
     * @param context o contexto
     * @param c       o cursor que vai pegar os dados
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Cria um novo list view vazio sem dados inseridos.
     *
     * @param context contexto do app
     * @param cursor  cursor que vai pegar os dados
     * @param parent  o parent ao qual a view está inserida
     * @return a list view criada
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * Este método liga o dado do livro o qual o cursor está apontando para o item da list view.
     *
     * @param view    View existente a qual o método newView retorna
     * @param context contexto do app
     * @param cursor  cursor que vai pegar os dados
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Pega as referências dos campos que modificaremos no item da list view
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageView sellImageView = (ImageView) view.findViewById(R.id.buy_button);

        // Pega as colunas dos campos dos livros que serão modificados
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_QUANTITY);
        final int idColumnIndex = cursor.getColumnIndex(BookEntry._id);

        // Pega os dados do livro o qual o cursor está apontando
        String titleBook = cursor.getString(titleColumnIndex);
        String priceBook = cursor.getString(priceColumnIndex);
        String quantityBook = cursor.getString(quantityColumnIndex);

        // Atualiza as views com os dados do livro atual
        titleTextView.setText(titleBook);
        priceTextView.setText(priceBook);
        quantityTextView.setText(quantityBook);

        String currentQuantity = cursor.getString(quantityColumnIndex);
        final int quantityInteger = Integer.valueOf(currentQuantity);
        final int bookId = cursor.getInt(idColumnIndex);

        // Listener para o botão existente em cada item, que executará a lógica de decrementar o
        // valor de um campo da view e impedir que esse valor se torne negativo
        sellImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityInteger > 0){
                    ContentValues values = new ContentValues();

                    int currentQuantity = quantityInteger - 1;

                    Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);

                    values.put(BookEntry.COLUMNS_BOOK_QUANTITY, currentQuantity);
                    context.getContentResolver().update(uri,values, null, null);

                }else {
                    Toast.makeText(context, context.getString(R.string.decrease_quantity_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}