package com.app.englishapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    InputStream inputStream = null;
    LinearLayout resultButtons;
    Button btn_words, btn_firstWord, btn_secondWord, btn_thirdWord, btn_fourthWord, btn_search, btn_report;
    TextView resultPhrases;
    List<String> words;
    List<String> dailyWords;
    String urlTranslate, selectedWord;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_words = findViewById(R.id.btn_words);
        resultButtons = findViewById(R.id.resultButtons);
        resultPhrases = findViewById(R.id.resultPhrases);
        btn_firstWord = findViewById(R.id.firsWord);
        btn_secondWord = findViewById(R.id.secondWord);
        btn_thirdWord = findViewById(R.id.thirdWord);
        btn_fourthWord= findViewById(R.id.fourthWord);
        btn_search = findViewById(R.id.search);
        btn_report = findViewById(R.id.report);

        dailyWords = new ArrayList<>();
        words = new ArrayList<>();

        resultButtons.setVisibility(View.GONE);
        btn_report.setVisibility(View.GONE);
        btn_search.setVisibility(View.GONE);
        //resultPhrases.setVisibility(View.GONE);

        btn_words.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Wait a moment please");
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        words.clear();
                        dailyWords.clear();

                        randomLine();

                        btn_firstWord.setText(words.get(0));
                        btn_secondWord.setText(words.get(1));
                        btn_thirdWord.setText(words.get(2));
                        btn_fourthWord.setText(words.get(3));

                        pd = new ProgressDialog(MainActivity.this);

                        getBodyText(btn_firstWord.getText().toString());
                        getBodyText(btn_secondWord.getText().toString());
                        getBodyText(btn_thirdWord.getText().toString());
                        getBodyText(btn_fourthWord.getText().toString());

                        resultButtons.setVisibility(View.VISIBLE);
                        btn_words.setText("Give me another round");

                        progressDialog.dismiss();
                    }
                }, 300);
            }
        });

        btn_firstWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultPhrases.setText("");
                resultPhrases.setText(Html.fromHtml(displayWords(btn_firstWord.getText().toString())));

                if(resultPhrases.getText().toString().equals("")){
                    resultPhrases.setText("Wow! It seems that this word is not yet found");
                    btn_report.setVisibility(View.VISIBLE);
                    selectedWord = "";
                }
                else {
                    btn_report.setVisibility(View.GONE);
                    btn_search.setVisibility(View.VISIBLE);
                    selectedWord = btn_firstWord.getText().toString();
                }
            }
        });

        btn_secondWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultPhrases.setText("");
                resultPhrases.setText(Html.fromHtml(displayWords(btn_secondWord.getText().toString())));

                if(resultPhrases.getText().toString().equals("")){
                    resultPhrases.setText("Wow! It seems that this word is not yet found");
                    btn_report.setVisibility(View.VISIBLE);
                    selectedWord = "";
                }
                else {
                    btn_report.setVisibility(View.GONE);
                    btn_search.setVisibility(View.VISIBLE);
                    selectedWord = btn_secondWord.getText().toString();
                }
            }
        });

        btn_thirdWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultPhrases.setText("");
                resultPhrases.setText(Html.fromHtml(displayWords(btn_thirdWord.getText().toString())));

                if(resultPhrases.getText().toString().equals("")){
                    resultPhrases.setText("Wow! It seems that this word is not yet found");
                    btn_report.setVisibility(View.VISIBLE);
                    selectedWord = "";
                }
                else {
                    btn_report.setVisibility(View.GONE);
                    btn_search.setVisibility(View.VISIBLE);
                    selectedWord = btn_thirdWord.getText().toString();
                }
            }
        });

        btn_fourthWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultPhrases.setText("");
                resultPhrases.setText(Html.fromHtml(displayWords(btn_fourthWord.getText().toString())));

                if(resultPhrases.getText().toString().equals("")){
                    resultPhrases.setText("Wow! It seems that this word is not yet found");
                    btn_report.setVisibility(View.VISIBLE);
                    selectedWord = "";
                }
                else {
                    btn_report.setVisibility(View.GONE);
                    btn_search.setVisibility(View.VISIBLE);
                    selectedWord = btn_fourthWord.getText().toString();
                }
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    if(selectedWord != null){

                        if(!selectedWord.equals("")) {

                            urlTranslate = "https://translate.google.com/?hl=es&sl=en&tl=es&text=";
                            String parametters = "";

                            for (String s : dailyWords) {
                                if (s.toLowerCase().contains(selectedWord)) {
                                    for (String w : s.split(" ")) {
                                        parametters = parametters + w + "%20";
                                    }
                                    parametters = parametters + "%0A%0A";
                                }
                            }

                            urlTranslate = urlTranslate + parametters + "&op=translate";

                            openWebURL(urlTranslate);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "This word was not found. Sorry D:", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Sorry, we have a problem. Please try again", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Log.e("Error", e.getMessage());
                    Toast.makeText(MainActivity.this, "Sorry, we have a problem. Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getBodyText(String searchedWord) {

        new Thread(new Runnable() {

            boolean isRunnung = false;

            public void exit(boolean exit){
                this.isRunnung = exit;
            }

            @Override
            public void run() {

                while(!isRunnung) {

                    isRunnung = true;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultPhrases.setText("");
                            pd.setMessage("We are working on this, please wait");
                            if(!pd.isShowing()){
                                pd.show();
                            }
                        }
                    });

                    String phrase = "";

                    try {
                        Document doc = Jsoup.parse(readURL("https://www.wordhippo.com/what-is/sentences-with-the-word/" + searchedWord + ".html"));

                        if(doc.getElementById("gexv2row1").text() != null){

                            String sentence1 = doc.getElementById("gexv2row1").text();
                            dailyWords.add(sentence1);

                            if(doc.getElementById("gexv2row2").text() != null){

                                String sentence2 = doc.getElementById("gexv2row2").text();
                                dailyWords.add(sentence2);

                                if(doc.getElementById("gexv2row3").text() != null){

                                    String sentence3 = doc.getElementById("gexv2row3").text();
                                    dailyWords.add(sentence3);

                                }

                            }

                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                pd.dismiss();
                                isRunnung = false;

                            }

                        });

                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        pd.dismiss();
                    }
                }
            }
        }).start();
    }

    public void randomLine(){

        for(int i = 0; i < 4; i++){

            try{
                inputStream = getAssets().open("engmix.txt");
                String result = null;
                Random rand = new Random();
                int n = 0;
                Scanner sc = new Scanner(inputStream);

                while (sc.hasNext()) {
                    ++n;
                    String line = sc.nextLine();
                    if (rand.nextInt(n) == 0)
                        result = line;
                }

                sc.close();

                words.add(result.toString());

            }
            catch (Exception ex){
                Log.e("Error",ex.getMessage());
                Toast.makeText(MainActivity.this,"Lo sentimos, por favor intenta de nuevo", Toast.LENGTH_LONG).show();
            }

        }

    }

    public String readURL(String url) {

        String fileContents = "";
        String currentLine = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            fileContents = reader.readLine();
            while (currentLine != null) {
                currentLine = reader.readLine();
                fileContents += "\n" + currentLine;
            }
            reader.close();
            reader = null;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return fileContents;
    }

    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

        startActivity( browse );
    }

    public String displayWords(String word){

        String finalExample = "";

        for(String s : dailyWords){
            if(s.toLowerCase().contains(word)){
                for(String w : s.split(" ")){
                    if(w.toLowerCase().contains(word)){
                        w="<font color='#EE0000'>"+word+"</font>";
                    }
                    finalExample = finalExample + w + " ";
                }
                finalExample = finalExample + "<br/><br/>";
            }
        }

        return finalExample;
    }

}