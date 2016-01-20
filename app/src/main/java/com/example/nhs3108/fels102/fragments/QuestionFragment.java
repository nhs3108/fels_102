package com.example.nhs3108.fels102.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.AnswerAdapter;
import com.example.nhs3108.fels102.utils.Answer;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.UserAnswer;
import com.example.nhs3108.fels102.utils.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by nhs3108 on 1/13/16.
 */
public abstract class QuestionFragment extends Fragment {
    private final String RESULT_WORD_ID_FORMAT = "lesson[results_attributes][%s]['id']";
    private final String RESULT_ANWSER_ID_FORMAT = "lesson[results_attributes][%s]['answer_id']";
    private ArrayList<NameValuePair> mNameValuePairs;
    private ArrayList<UserAnswer> mUserAnswers = new ArrayList<UserAnswer>();
    private ArrayList<Answer> mAnswersList;
    private int mFragmentIndex;
    private Word mWord;
    private TextView mTextViewQuestionName;
    private TextView mTextViewQuestionContent;
    private ListView mlistViewAnswers;
    private View mFragmentView;
    private Activity mActivity;
    private TextToSpeech mTextToSpeech;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_question, container, false);
        initialize();
        mTextViewQuestionName.setText(mActivity.getString(R.string.label_question) + (mFragmentIndex + 1));
        mTextViewQuestionContent.setText(mWord.getContent());
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(mWord.getId()));
        mTextViewQuestionContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextToSpeech.speak(mTextViewQuestionContent.getText().toString(), TextToSpeech.QUEUE_FLUSH, params);
            }
        });
        AnswerAdapter answerAdapter = new AnswerAdapter(mActivity, R.layout.item_answer, mAnswersList);
        mlistViewAnswers.setAdapter(answerAdapter);
        return mFragmentView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mlistViewAnswers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Button btnAnswerSelected = (Button) view.findViewById(R.id.text_anwser_content);
                btnAnswerSelected.setBackgroundColor(getResources().getColor(R.color.actionbar));
                setViewAndChildrenEnabled((View) view.getParent().getParent(), false);
                Answer answer = mAnswersList.get(position);
                mUserAnswers.add(new UserAnswer(mWord.getContent(), answer.getContent(), answer.isCorrect()));
                updateNameValuePairs(mFragmentIndex, mNameValuePairs, mWord, answer);
                updateUserAnswers(mUserAnswers, mWord, answer);
                changePage();
            }
        });
    }

    private void initialize() {
        mActivity = getActivity();
        mWord = getWord();
        mAnswersList = mWord.getAnswers();
        mFragmentIndex = getFragmentIndex();
        mNameValuePairs = getRequestParamsPairs();
        mUserAnswers = getUserAnswers();
        initViewComponent();
        mTextToSpeech = new TextToSpeech(mActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void initViewComponent() {
        mTextViewQuestionName = (TextView) mFragmentView.findViewById(R.id.text_question_name);
        mTextViewQuestionContent = (TextView) mFragmentView.findViewById(R.id.text_question_content);
        mlistViewAnswers = (ListView) mFragmentView.findViewById(R.id.list_answers);
    }

    private void updateNameValuePairs(int index, ArrayList<NameValuePair> nameValuePairs, Word word, Answer answer) {
        NameValuePair nameValuePair1 = new NameValuePair(
                String.format(RESULT_WORD_ID_FORMAT, String.valueOf(index)),
                String.valueOf(word.getId())
        );
        NameValuePair nameValuePair2 = new NameValuePair(
                String.format(RESULT_ANWSER_ID_FORMAT, String.valueOf(index)),
                String.valueOf(answer.getId())
        );

        nameValuePairs.add(nameValuePair1);
        nameValuePairs.add(nameValuePair2);
    }

    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    private void updateUserAnswers(ArrayList<UserAnswer> userAnswers, Word word, Answer answer) {
        userAnswers.add(new UserAnswer(word.getContent(), answer.getContent(), answer.isCorrect()));
    }

    public abstract Word getWord();

    public abstract ArrayList<NameValuePair> getRequestParamsPairs();

    public abstract ArrayList<UserAnswer> getUserAnswers();

    public abstract void changePage();

    public abstract int getFragmentIndex();
}
