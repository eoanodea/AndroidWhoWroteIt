package ie.wspace.whowroteit;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    //Base URL for Books API
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    // Parameter for the search string
    private static final String QUERY_PARAM = "q";
    //Parameter that limits search results
    private static final String MAX_RESULTS = "maxResults";
    //Parameter to filter by print type
    private static final String PRINT_TYPE = "printType";
    /**
     * Builds the URL and
     * fetches a book from the Google API
     *
     * @param queryString
     * @return
     */
    static String getBookInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            //Build the URI
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
            .appendQueryParameter(QUERY_PARAM, queryString)
            .appendQueryParameter(MAX_RESULTS, "10")
            .appendQueryParameter(PRINT_TYPE, "books")
            .build();

            //Convert to request URL
            URL requestURL = new URL(builtURI.toString());

            //Open connection URL and make the request
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Set up the response from the connection
            //Get the input stream
            InputStream inputStream = urlConnection.getInputStream();

            //Create a buffered reader from the input stream
            reader = new BufferedReader(new InputStreamReader(inputStream));

            //Use a StringBuilder to hold the incoming response
            StringBuilder builder = new StringBuilder();

            //Read the input line by line into the string while there is still input
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);

                //Since it's JSON, adding a newline isn't necessary
                // and wont affect parsing

                //Print out the completed buffer for debugging
                builder.append("\n");

                if(builder.length() == 0) {
                    //Stream was empty, return null
                    return null;
                }

                //Convert the StringBuilder to a string and store in bookJSONString
                bookJSONString = builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //If the urlConnection is null disconnect
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }
}
