package com.app.englishapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    InputStream inputStream = null;
    Scanner sc = null;
    String result = "";
    Button btn_words, btn_search;
    EditText edit_search;
    TextView todayWords, resultSearch;
    List<String> words;
    List<String> dailyWords;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_words = findViewById(R.id.btn_words);
        btn_search = findViewById(R.id.btn_search);
        resultSearch = findViewById(R.id.result);
        edit_search = findViewById(R.id.edit_search);
        todayWords = findViewById(R.id.words);
        dailyWords = new ArrayList<>();

        todayWords.setVisibility(View.GONE);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = edit_search.getText().toString();
                if(!words.equals("")){
                    getBodyText(words);
                }
            }
        });

        btn_words.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = ProgressDialog.show(MainActivity.this, "Creando base de datos","Espera un momento por favor", true);
                btn_words.setEnabled(false);
                readWords();
                pd.setMessage("Escogiedo palabras");
                Random random = new Random();
                dailyWords.clear();

                for(int i = 0; i < 4; i++){
                    String randomElement = words.get(random.nextInt(words.size()));
                    dailyWords.add(randomElement);
                }
                String total = "";
                for(String s : dailyWords){
                    total = total + s+"\n";
                    todayWords.setText(total);
                }
                todayWords.setVisibility(View.VISIBLE);
                btn_words.setEnabled(true);
            }
        });

    }

    private void getBodyText(String seachedWord) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    String url="https://sentence.yourdictionary.com/" + seachedWord;//your website url
                    Document doc = Jsoup.connect(url).get();

                    Element body = doc.body();
                    builder.append(body.text());

                } catch (Exception e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String search = builder.toString().substring(493,1021);
                        String phrases = "";
                        List<String> wordsInPhrases = new ArrayList<>();
                        boolean nextWord = false;

                        for(String s : search.split(" ")){
                            if(!s.contains("[1234567890]")){
                                phrases = phrases +" "+ s;
                            }
                            else {
                                wordsInPhrases.add(phrases);
                            }
                            phrases = "";
                        }

                        for(String s : wordsInPhrases){
                            Log.w("palabras", s);
                        }
                        resultSearch.setText(search);
                    }
                });
            }
        }).start();
    }

    public void searchNet(String words){
        try{
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, words);
            startActivity(intent);
        }
        catch (ActivityNotFoundException ex){
            ex.printStackTrace();
            searchNetCompat(words);
        }
    }

    private void searchNetCompat(String words){
        try{
            Uri uri = Uri.parse("https://sentence.yourdictionary.com/" + words);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        catch (ActivityNotFoundException ex){
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void readWords(){

        words = new ArrayList<>();

        try {
            inputStream = getAssets().open("engmix.txt");
            sc = new Scanner(inputStream);

            while (sc.hasNext()){
                result = sc.next();
                words.add(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pd.dismiss();

    }
}