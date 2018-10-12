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
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageView sellImageView = (ImageView) view.findViewById(R.id.buy_button);

        // Find the columns of pet attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMNS_BOOK_QUANTITY);
        final int idColumnIndex = cursor.getColumnIndex(BookEntry._id);

        // Read the pet attributes from the Cursor for the current pet
        String titleBook = cursor.getString(titleColumnIndex);
        String priceBook = cursor.getString(priceColumnIndex);
        String quantityBook = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current pet
        titleTextView.setText(titleBook);
        priceTextView.setText(priceBook);
        quantityTextView.setText(quantityBook);

        String currentQuantity = cursor.getString(quantityColumnIndex);
        final int quantityInteger = Integer.valueOf(currentQuantity);
        final int bookId = cursor.getInt(idColumnIndex);

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
                    Toast.makeText(context, "O livro não está mais disponível para venda.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}