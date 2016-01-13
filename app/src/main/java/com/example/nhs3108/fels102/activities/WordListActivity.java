package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.WordAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.utils.Answer;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
import com.example.nhs3108.fels102.utils.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/10/16.
 */
public class WordListActivity extends Activity {
    private SharedPreferences mSharedPreferences;
    private ArrayList<Word> mWordsList = new ArrayList<Word>();
    private WordAdapter mWordAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_word_list);
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        String authToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        if (!TextUtils.isEmpty(authToken)) {
            new ObtainWordList().execute(authToken);
            ListView listViewWords = (ListView) findViewById(R.id.list_words);
            mWordAdapter = new WordAdapter(this, R.layout.item_word, mWordsList);
            listViewWords.setAdapter(mWordAdapter);
        }
    }

    private class ObtainWordList extends AsyncTask<String, Void, String> {
        String authTokenParamName = "auth_token";
        private ProgressDialog mProgressDialog;
        private int mStatusCode;
        private String mResponseBody;

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(WordListActivity.this);
            mProgressDialog.setMessage(getString(R.string.msg_wait));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        protected String doInBackground(String... args) {
            String authToken = args[0];
            NameValuePair nvp1 = new NameValuePair(authTokenParamName, authToken);
            ResponseHelper responseHelper = null;
            try {
                responseHelper = RequestHelper.executeRequest(UrlConsts.WORDS_URL, RequestHelper.Method.GET, nvp1);
                mStatusCode = responseHelper.getResponseCode();
                mResponseBody = responseHelper.getResponseBody();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            switch (mStatusCode) {
                case HttpStatusConsts.OK:
                    try {
                        updateWordList();
                    } catch (JSONException e) {
                        // Do nothing
                    }
                    break;
                case HttpStatusConsts.UNAUTHORIZED:
                    String errorMessage = getString(R.string.error_unauthorized);
                    break;
                case HttpStatusConsts.NOT_FOUND:
                    Toast.makeText(WordListActivity.this, getString(R.string.error_server_not_found), Toast.LENGTH_SHORT).show();
                    break;
                case HttpStatusConsts.INTERNAL_SERVER_ERROR:
                    Toast.makeText(WordListActivity.this, getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(WordListActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
            }
        }

        private void updateWordList() throws JSONException {
            JSONArray wordsJson = new JSONObject(mResponseBody).optJSONArray("words");
            int numOfWords = wordsJson.length();
            for (int i = 0; i < numOfWords; i++) {
                String wordContent = wordsJson.optJSONObject(i).optString("content");
                JSONArray answersJson = wordsJson.optJSONObject(i).optJSONArray("answers");
                ArrayList<Answer> answers = new ArrayList<Answer>();
                int numOfAnswers = answersJson.length();
                for (int j = 0; j < numOfAnswers; j++) {
                    String answerContent = answersJson.optJSONObject(j).optString("content");
                    Boolean isCorrect = answersJson.optJSONObject(j).optBoolean("is_correct");
                    answers.add(new Answer(answerContent, isCorrect));
                }
                mWordsList.add(new Word(wordContent, answers));
            }
            mWordAdapter.notifyDataSetChanged();
        }
    }
}
