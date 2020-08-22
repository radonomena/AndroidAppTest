package com.example.myapplication1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

//import android.support.annotation.Nullable; might cause troubles

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO differentiation de siortie et entrée
    EditText txtDesignation, txtMontant;
    RadioButton radioSortie, radioEntree;
    Button btnAjouter, btnEquation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        txtDesignation = (EditText)findViewById(R.id.txt_designation);
        txtMontant = (EditText)findViewById(R.id.txt_montant);

        radioSortie = (RadioButton)findViewById(R.id.radio_sortie);
        radioEntree = (RadioButton)findViewById(R.id.radio_entree);

        btnAjouter = (Button)findViewById(R.id.btn_ajouter);
        btnAjouter.setOnClickListener(this);

        btnEquation = (Button) findViewById(R.id.btn_equation);
    }

    //This is the part where data is transfered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addNewMovementToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String designation = txtDesignation.getText().toString().trim();
        //recuperation des valeurs selon les boutons
        String montantSortie = "", montantEntree = "";
        if(radioSortie.isChecked()){
            montantSortie = txtMontant.getText().toString().trim();
        }else if(radioEntree.isChecked()){
            montantEntree = txtMontant.getText().toString().trim();
        }


        final String finalMontantSortie = montantSortie;
        final String finalMontantEntree = montantEntree;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxPjoP-uJzdzrN-See1zfiqB1zrBihh1lhHAWwaxw/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","addItem");//can't be changed to some other Value, it makes the script do error execution
                params.put("Designation",designation);
                params.put("MontantSortie" , finalMontantSortie);
                params.put("MontantEntree", finalMontantEntree);

                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    private void openEquationActivity(){
        Intent intent = new Intent(getApplicationContext(), EquationActivity.class);
        startActivity(intent);
    }



    @Override
    public void onClick(View v) {

        if(v==btnAjouter){
            //methode a appeler conditionner
            addNewMovementToSheet();
        }else if(v==btnEquation){
            openEquationActivity();
        }
    }
}