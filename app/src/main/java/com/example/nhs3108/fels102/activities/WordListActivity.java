package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.WordAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.listeners.CommonEventHandlerUtils;
import com.example.nhs3108.fels102.listeners.EndlessRecyclerOnScrollListener;
import com.example.nhs3108.fels102.utils.Answer;
import com.example.nhs3108.fels102.utils.Category;
import com.example.nhs3108.fels102.utils.MyAsyncTask;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.PdfUtils;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
import com.example.nhs3108.fels102.utils.Word;
import com.itextpdf.text.DocumentException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nhs3108 on 1/10/16.
 */
public class WordListActivity extends Activity {
    private String mAuthToken;
    private SharedPreferences mSharedPreferences;
    private ArrayList<Word> mWordsList = new ArrayList<Word>();
    private ArrayList<Category> mCategoriesList = new ArrayList<Category>();
    private ArrayList<NameValuePair> mStatusList = new ArrayList<NameValuePair>();
    private WordAdapter mWordAdapter;
    private ArrayAdapter<Category> mCategoryAdapter;
    private RecyclerView mRecycleViewWords;
    private LinearLayoutManager mLayoutManager;
    private Spinner mSpinnerCategory;
    private Spinner mSpinnerStatus;
    private ImageButton mBtnBack;
    private ImageButton mBtnExportPdf;
    private int mCurrentPage = 1;
    private EndlessRecyclerOnScrollListener mOnScrollListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_word_list);
        mCategoriesList = (ArrayList<Category>) getIntent().getSerializableExtra(CommonConsts.KEY_CATEGORY_LIST);
        if (mCategoriesList.size() > 0) {
            initialize();
            setUpCategorySpinner();
            setUpStatusSpinner();
            setupWordRecycleView();
            setUpButtonsClickedEventHanlder();
        } else {
            Toast.makeText(WordListActivity.this, getString(R.string.no_category_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpButtonsClickedEventHanlder() {
        mBtnExportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveWordListPdfFileTask(WordListActivity.this).execute();
            }
        });
        CommonEventHandlerUtils.clickBack(WordListActivity.this, mBtnBack);
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        mRecycleViewWords = (RecyclerView) findViewById(R.id.list_words);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnExportPdf = (ImageButton) findViewById(R.id.btn_pdf);
        mSpinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        mSpinnerStatus = (Spinner) findViewById(R.id.spinner_status);
        mStatusList.add(new NameValuePair(getString(R.string.key_all_word), CommonConsts.KEY_ALL_WORD));
        mStatusList.add(new NameValuePair(getString(R.string.key_learned), CommonConsts.KEY_LEARNED));
        mStatusList.add(new NameValuePair(getString(R.string.key_not_learned), CommonConsts.KEY_NOT_LEARNED));
    }

    private void setupWordRecycleView() {
        mWordAdapter = new WordAdapter(this, mWordsList);
        mRecycleViewWords.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycleViewWords.setLayoutManager(mLayoutManager);
        mRecycleViewWords.setAdapter(mWordAdapter);
        mOnScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                loadWordList();
            }
        };
        mRecycleViewWords.addOnScrollListener(mOnScrollListener);
    }

    private void loadWordList() {
        String categoryIdSelected = String.valueOf(((Category) mSpinnerCategory.getSelectedItem()).getId());
        String statusSelected = ((NameValuePair) mSpinnerStatus.getSelectedItem()).getValue();
        new ObtainWordList(WordListActivity.this)
                .execute(mAuthToken, categoryIdSelected, statusSelected);
    }

    private void setUpCategorySpinner() {
        mCategoryAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, mCategoriesList);
        mSpinnerCategory.setAdapter(mCategoryAdapter);
        mSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeFilterHandler();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpStatusSpinner() {
        ArrayAdapter<NameValuePair> statusAdapter = new ArrayAdapter<NameValuePair>(this, android.R.layout.simple_spinner_item, mStatusList);
        mSpinnerStatus.setAdapter(statusAdapter);
        mSpinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeFilterHandler();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void changeFilterHandler() {
        mWordsList.clear();
        mCurrentPage = 1;
        mOnScrollListener.reset();
        mWordAdapter.notifyDataSetChanged();
        loadWordList();
    }

    private class ObtainWordList extends MyAsyncTask<String, Void, String> {
        String authTokenParamName = "auth_token";
        String categoryIdParamName = "category_id";
        String statusParamName = "option";
        String pageParamName = "page";
        private ProgressDialog mProgressDialog;
        private int mStatusCode;
        private String mResponseBody;

        public ObtainWordList(Context context) {
            super(context);
        }

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
            String categoryId = args[1];
            String status = args[2];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new NameValuePair(authTokenParamName, authToken));
            params.add(new NameValuePair(categoryIdParamName, categoryId));
            params.add(new NameValuePair(statusParamName, status));
            params.add(new NameValuePair(pageParamName, String.valueOf(mCurrentPage++)));
            ResponseHelper responseHelper;
            try {
                responseHelper = RequestHelper.executeRequest(UrlConsts.WORDS_URL, RequestHelper.Method.GET, params);
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

    private class SaveWordListPdfFileTask extends MyAsyncTask<Void, Void, Void> {
        boolean isCompleted = false;
        String errorMessage;
        String filePath;
        String fileName;

        public SaveWordListPdfFileTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            fileName = CommonConsts.DEFAULT_WORDLIST_FILE_NAME + new Date().toString();
            filePath = CommonConsts.DEFAULT_FILE_SAVED_PATH;
            PdfUtils fop = new PdfUtils();
            try {
                fop.writeWordList(fileName, mWordsList, filePath);
                isCompleted = true;
            } catch (IOException e) {
                errorMessage = e.getMessage();
            } catch (DocumentException e) {
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isCompleted) {
                Toast.makeText(WordListActivity.this,
                        String.format(getString(R.string.format_file_saving_successfully), filePath),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WordListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
