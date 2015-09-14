package com.mcomobile.mercadillos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Filtrar extends Activity {

	public Spinner spMuni,spDia,spTipo;
	public  String mMuni,mDia,mTipo;
	private Button enviarDatos;
	public String EXTRA_MUNI;
	public String EXTRA_TIPO;
	public String EXTRA_DIA;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        
        spMuni = (Spinner) findViewById(R.id.spmuni);
        spTipo = (Spinner) findViewById(R.id.sptipo);
        spDia = (Spinner) findViewById(R.id.spdia);
        enviarDatos = (Button) findViewById(R.id.btnenviar);
           
        loadSpinnerData();
	}
	
	private void loadSpinnerData() {
		
		ArrayAdapter<CharSequence> dataAdapterFecha = ArrayAdapter.createFromResource(
				  this, R.array.fecha, android.R.layout.simple_spinner_item );
		dataAdapterFecha.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );			
			
		ArrayAdapter<CharSequence> dataAdapterTipo = ArrayAdapter.createFromResource(
				  this, R.array.tipo, android.R.layout.simple_spinner_item );
		dataAdapterTipo.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		
		ArrayAdapter<CharSequence> dataAdapterMuni = ArrayAdapter.createFromResource(
				  this, R.array.municipios, android.R.layout.simple_spinner_item );
		dataAdapterMuni.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		
        spDia.setAdapter(dataAdapterFecha);
        spMuni.setAdapter(dataAdapterMuni);
        spTipo.setAdapter(dataAdapterTipo);
        
	    initListener();
	}
	
	private void initListener(){
		spDia.setOnItemSelectedListener(
	            new OnItemSelectedListener() {
	                public void onItemSelected(
	                        AdapterView<?> parent, View view, int position, long id) {
	                	mDia = spDia.getSelectedItem().toString();
	                }

	                public void onNothingSelected(AdapterView<?> parent) {
	                }
	            });
		spMuni.setOnItemSelectedListener(
	            new OnItemSelectedListener() {
	                public void onItemSelected(
	                        AdapterView<?> parent, View view, int position, long id) {
	                	mMuni = spMuni.getSelectedItem().toString(); 
	                }

	                public void onNothingSelected(AdapterView<?> parent) {
	                }
	            });
		spTipo.setOnItemSelectedListener(
	            new OnItemSelectedListener() {
	                public void onItemSelected(
	                        AdapterView<?> parent, View view, int position, long id) {
	                	mTipo = spTipo.getSelectedItem().toString();
	                }

	                public void onNothingSelected(AdapterView<?> parent) {
	                }
	            });	

        enviarDatos.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
            Intent i = new Intent(Filtrar.this, Listado.class);
            i.putExtra("EXTRA_DIA", mDia);
            i.putExtra("EXTRA_MUNI", mMuni);            
            i.putExtra("EXTRA_TIPO", mTipo);
            startActivity(i);				
			}
		});
	}
	
}
