package com.example.blin.updatesample;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by blin on 2015/4/15.
 */
public class ReadJsonAsync {
    static private OnRetriveJsonListener mListener;
    private AsyncHttpTask  mTask;
    private Handler mHandler;
    static private String RDSTR;
    public interface OnRetriveJsonListener {
        void ReceiveScuess(String msg);
    }

    public void setOnRetriveScuessListener(OnRetriveJsonListener listener) {
        this.mListener = listener;
    }

    // ?°e
    public void ReadJson(String URLSTR) {
        //TODO »Ý§P?¦³?¦³ÊI?
        mTask = new AsyncHttpTask(URLSTR);
        mTask.execute();
    }

    // °±¤î
    public void stop() {
        if (mTask != null)
            mTask.cancel(true);
    }
    public static class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        private static final String TAGSTR = "Http Connection";

        private ArrayAdapter arrayAdapter = null;
        private Context mcontext;
        private String urlstr;
        public AsyncHttpTask(String Urlpass){
            this.urlstr=Urlpass;
        }
        /*public AsyncHttpTask(Context mcontext) {
            this.mcontext = mcontext;
        }*/
        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;

            HttpURLConnection urlConnection =  null;
            Log.i("TAGSTR", "Backgroud");
            Integer result = 0;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(this.urlstr);
                Log.i(TAGSTR,"URL:"+url.toString() );
                urlConnection = (HttpURLConnection) url.openConnection();

                 /* optional request header */
                urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
                urlConnection.setRequestProperty("Accept", "application/json");

                /* for Get request */
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();
                Log.i("TAGSTR",Integer.toString(statusCode) );
                /* 200 represents HTTP OK */
                if (statusCode ==  200) {

                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String response = convertInputStreamToString(inputStream);
                    Log.i("TAGSTR", response);
                    RDSTR=response;
                    result = 1; // Successful

                }else{
                    result = 0; //"Failed to fetch data!";
                }

            } catch (Exception e) {
                Log.d(TAGSTR, e.getLocalizedMessage());
            }

            return result; //"Failed to fetch data!";
        }


        @Override
        protected void onPostExecute(Integer result) {
            /* Download complete. Lets update UI */
            Log.i(TAGSTR,"onPostExecut");
            if(result == 1){
                mListener.ReceiveScuess(RDSTR);
//                arrayAdapter = new ArrayAdapter(mcontext, android.R.layout.simple_list_item_1, blogTitles);

//                listView.setAdapter(arrayAdapter);
//                ED1.setText(blogTitles[0]);
//                ED1.setText("OK");
            }else{
                Log.e(TAGSTR, "Failed to fetch data!");
            }
        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));

        String line = "";
        String result = "";
        Log.i("TAGSTR","convertInputStreamToString");
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

            /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }

        return result;
    }
}
