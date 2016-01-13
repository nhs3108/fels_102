package com.example.nhs3108.fels102.fragments;

import android.os.Bundle;
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
import com.example.nhs3108.fels102.utils.Word;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/13/16.
 */
public abstract class QuestionFragment extends Fragment {
    public ArrayList<NameValuePair> mNameValuePairs;
    private ListView mlistViewAnswers;
    private ArrayList<Answer> mAnswersList;
    private int mFragmentIndex;
    private Word mWord;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWord = getWord();
        mAnswersList = mWord.getAnswers();
        mFragmentIndex = getFragmentIndex();
        mNameValuePairs = getRequestParamsPairs();

        View view = inflater.inflate(R.layout.fragment_question, container, false);
        TextView textViewQuestionName = (TextView) view.findViewById(R.id.text_question_name);
        textViewQuestionName.setText("Question #" + mFragmentIndex);
        TextView textViewQuestionContent = (TextView) view.findViewById(R.id.text_question_content);
        textViewQuestionContent.setText(mWord.getContent());
        mlistViewAnswers = (ListView) view.findViewById(R.id.list_answers);
        AnswerAdapter answerAdapter = new AnswerAdapter(getActivity(), R.layout.item_answer, mAnswersList);
        mlistViewAnswers.setAdapter(answerAdapter);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mlistViewAnswers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Button btnAnswerSelected = (Button) view.findViewById(R.id.text_anwser_content);
                btnAnswerSelected.setBackgroundColor(getResources().getColor(R.color.actionbar));
                setViewAndChildrenEnabled((View) view.getParent().getParent(), false);
                String resultWordIdFormat = "lesson[results_attributes][%s]['id']";
                String resultAnswerIdFormat = "lesson[results_attributes][%s]['answer_id']";
                Answer answer = mAnswersList.get(position);
                NameValuePair nameValuePair1 = new NameValuePair(
                        String.format(resultWordIdFormat, String.valueOf(mFragmentIndex)),
                        String.valueOf(mWord.getId())
                );
                NameValuePair nameValuePair2 = new NameValuePair(
                        String.format(resultAnswerIdFormat, String.valueOf(mFragmentIndex)),
                        String.valueOf(answer.getId())
                );

                mNameValuePairs.add(nameValuePair1);
                mNameValuePairs.add(nameValuePair2);
                changePage();
            }
        });
    }

    public abstract Word getWord();

    public abstract ArrayList<NameValuePair> getRequestParamsPairs();

    public abstract void changePage();

    public abstract int getFragmentIndex();
}
