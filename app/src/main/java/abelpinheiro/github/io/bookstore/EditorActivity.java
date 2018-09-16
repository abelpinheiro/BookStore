package abelpinheiro.github.io.bookstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        String title = getIntent().getStringExtra("SAVE_ACTIVITY_TITLE");
        setTitle(title);
    }
}
