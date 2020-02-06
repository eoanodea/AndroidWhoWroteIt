package ie.wspace.whowroteit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = findViewById(R.id.bookInput);
        mTitleText = findViewById(R.id.titleText);
        mAuthorText = findViewById(R.id.authorText);

        //If the loader exists, initialize it
        if(getSupportLoaderManager().getLoader(0)!=null) {
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    public void searchBooks(View view) {
        String queryString = mBookInput.getText().toString();

        //Check the Network connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if(connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        //Get the keyboard from context
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Remove the keyboard from the screen
        if(inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        /**
         * Only make the request if the following conditions are met
         * Condition: networkInfo is not null
         * Condition: networkInfo is connected
         * Condition: query inputted on UI is not equal to 0
         */
        if(networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            //Call the fetch book task
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);

            //Update UI with loading indicator
            mAuthorText.setText("");
            mTitleText.setText(R.string.loading);
        } else {
            //If query is empty, populate UI with no search
            if(queryString.length() == 0) {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_search_term);
            //Else populate textField with no network message
            } else {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_network);
            }
        }

    }

    /**
     * Called when the loader in instantiated
     * @param id
     * @param args
     * @return
     */
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";

        if(args != null) {
            queryString = args.getString("queryString");
        }

        return new BookLoader(this, queryString);
    }

    /**
     * Called when the loader is finished
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        try {
            //Convert the response into a JSON object
            JSONObject jsonObject = new JSONObject(data);
            //Get the JSON array of book items
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            //Initialize iterator and results fields
            int i = 0;
            String title = null;
            String authors = null;

            //Loop through the items array, exiting when
            // both title and author are found,
            // or when all items have been checked
            while(i < itemsArray.length()
                    && (authors == null && title == null)) {
                //Get the current item information
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                //Try get the author and title from the current item,
                // catch if either field is empty and move on
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Move to the next item
                i++;
            }

            //If the loop found a title and author,
            // update the UI with the new result
            //Else, return no results string
            if(title != null && authors != null) {
                mTitleText.setText(title);
                mAuthorText.setText(authors);
            } else {
                mTitleText.setText(R.string.no_results);
                mAuthorText.setText("");
            }

        } catch (JSONException e) {
            //If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results
            mTitleText.setText(R.string.no_results);
            mAuthorText.setText("");
            e.printStackTrace();
        }
    }

    /**
     * Cleans up any remaining resources
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
