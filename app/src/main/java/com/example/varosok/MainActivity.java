package com.example.varosok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button buttonback;
    private Button buttonList;
    private Button buttonmodify;
    private Button buttonFelvetel;
    private Button buttonsave;

    private EditText editTextId;
    private EditText editTextName;
    private EditText editTextOrszag;
    private EditText editTextLakossag;

    private LinearLayout LinearLayoutForm;

    private ListView listViewData;

    private List<Varosok> varosok = new ArrayList<>();

    private String url = "https://retoolapi.dev/z2wo5s/data\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        buttonFelvetel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayoutForm.setVisibility(View.VISIBLE);
                buttonsave.setVisibility(View.VISIBLE);
                buttonFelvetel.setVisibility(View.GONE);
            }
        });
        buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlapAlaphelyzetbe();
            }
        });
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ujEmberAdd();
            }
        });
        buttonmodify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emberModositas();
            }
        });



    }
    public void ujEmberAdd() {
        int id = Integer.parseInt(editTextId.getText().toString());
        String name = editTextName.getText().toString();
        String orszag = editTextOrszag.getText().toString();
        int lakossag = Integer.parseInt(editTextLakossag.getText().toString());
        Varosok varosok = new Varosok(0, name, orszag, lakossag);
        Gson jsonConverter = new Gson();
        RequestTask task = new RequestTask(url + "/" + id, "POST", jsonConverter.toJson(varosok));
        task.execute();
    }

    public void emberModositas(){
        String name = editTextName.getText().toString();
        String orszag = editTextOrszag.getText().toString();
        int lakossag = Integer.parseInt(editTextLakossag.getText().toString());
        Varosok varosok = new Varosok(0, name, orszag, lakossag);
        Gson jsonConverter = new Gson();
        RequestTask task = new RequestTask(url, "PUT", jsonConverter.toJson(varosok));
        task.execute();
    }
    public void init() {
        //változók inicializálása
        buttonback = findViewById(R.id.buttonback);
        buttonList = findViewById(R.id.buttonList);
        buttonmodify = findViewById(R.id.buttonmodify);
        buttonFelvetel = findViewById(R.id.buttonFelvetel);
        buttonsave = findViewById(R.id.buttonsave);
        editTextId = findViewById(R.id.editTextId);
        editTextName = findViewById(R.id.editTextName);
        editTextOrszag= findViewById(R.id.editTextOrszag);
        editTextLakossag = findViewById(R.id.editTextLakossag);
        LinearLayoutForm = findViewById(R.id.LinearLayoutForm);
        //listview adapter beállítás
        listViewData = findViewById(R.id.listViewData);
        listViewData.setAdapter(new VarosokAdapter());
        LinearLayoutForm.setVisibility(View.GONE);
        buttonmodify.setVisibility(View.GONE);
    }
//a VarosokAdapter a listactivitycostum file tartalmát dolgozza fel
    private class VarosokAdapter extends ArrayAdapter<Varosok>{
        public VarosokAdapter(){
            super(MainActivity.this, R.layout.listactivitycostum, varosok);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //inflatert hozzuk létre
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.listactivitycostum, null, false);
            TextView texViewDelete = view.findViewById(R.id.texViewDelete);
            TextView texViewModify = view.findViewById(R.id.texViewModify);
            TextView texViewName = view.findViewById(R.id.texViewName);
            TextView texViewCountry = view.findViewById(R.id.texViewCountry);
            TextView texViewPopulations = view.findViewById(R.id.texViewPopulations);
            //aktuális elem lekérdezése
            Varosok actualVaros = varosok.get(position);


            texViewName.setText(actualVaros.getNev());
            texViewCountry.setText(actualVaros.getOrszag());
            texViewPopulations.setText(String.valueOf(actualVaros.getLakossag()));

            texViewModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //űrlapot kitöltjük a kiválasztott elem adataival
                    editTextId.setText(String.valueOf(actualVaros.getId()));
                    editTextName.setText(actualVaros.getNev());
                    editTextOrszag.setText(actualVaros.getOrszag());
                    editTextLakossag.setText(String.valueOf(actualVaros.getLakossag()));
                    buttonmodify.setVisibility(View.VISIBLE);
                    buttonsave.setVisibility(View.GONE);
                    buttonFelvetel.setVisibility(View.GONE);
                }
            });

            texViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RequestTask task = new RequestTask(url, "DELETE", String.valueOf(actualVaros.getId()));
                    task.execute();
                }
            });

            return view;
        }
    }

    //űrlap alaphelyzetbe állítása
    public void urlapAlaphelyzetbe(){
        editTextId.setText("");
        editTextName.setText("");
        editTextOrszag.setText("");
        editTextLakossag.setText("");
        LinearLayoutForm.setVisibility(View.GONE);
        buttonmodify.setVisibility(View.GONE);
        buttonsave.setVisibility(View.VISIBLE);
        buttonmodify.setVisibility(View.VISIBLE);

    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams);
                        break;
                    case "PUT":
                        response = RequestHandler.put(requestUrl, requestParams);
                        break;
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl + "/" + requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(MainActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError:", response.getContent());
            }
            switch (requestType) {
                case "GET":
                    Varosok[] varosokArray = converter.fromJson(response.getContent(), Varosok[].class);
                    varosok.clear();
                    varosok.addAll(Arrays.asList(varosokArray));
                    break;
                case "POST":
                    Varosok varos = converter.fromJson(response.getContent(), Varosok.class);
                    varosok.add(0, varos);
                    urlapAlaphelyzetbe();
                    break;
                case "PUT":
                    Varosok updateVaros = converter.fromJson(response.getContent(), Varosok.class);
                    varosok.replaceAll(varos1 -> varos1.getId() == updateVaros.getId() ? updateVaros : varos1);
                    urlapAlaphelyzetbe();
                    break;
                case "DELETE":
                    int id = Integer.parseInt(requestParams);
                    varosok.removeIf(varos1 -> varos1.getId() == id);
                    break;
            }
        }
    }


}