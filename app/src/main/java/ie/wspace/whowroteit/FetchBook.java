package ie.wspace.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {
    private WeakReference<TextView> mTitleText;
    private WeakReference<TextView> mAuthorText;

    public FetchBook(TextView mTitleText, TextView mAuthorText) {
        this.mTitleText = new WeakReference<>(mTitleText);
        this.mAuthorText = new WeakReference<>(mAuthorText);
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            //Convert the response into a JSON object
            JSONObject jsonObject = new JSONObject(s);
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
                mTitleText.get().setText(title);
                mAuthorText.get().setText(authors);
            } else {
                mTitleText.get().setText(R.string.no_results);
                mAuthorText.get().setText("");
            }

        } catch (JSONException e) {
            //If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results
            mTitleText.get().setText(R.string.no_results);
            mAuthorText.get().setText("");
            e.printStackTrace();
        }
    }
}
