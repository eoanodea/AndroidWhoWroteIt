package ie.wspace.whowroteit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = (EditText)findViewById(R.id.bookInput);
        mTitleText = (TextView)findViewById(R.id.titleText);
        mAuthorText = (TextView)findViewById(R.id.authorText);
    }

    public void searchBooks(View view) {
        String queryString = mBookInput.getText().toString();

        //Get the keyboard from context
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Remove the keyboard from the screen
        if(inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //Call the fetch book task
        new FetchBook(mTitleText, mAuthorText).execute(queryString);

        //Update UI with loading indicator
        mAuthorText.setText("");
        mTitleText.setText(R.string.loading);

    }
}
