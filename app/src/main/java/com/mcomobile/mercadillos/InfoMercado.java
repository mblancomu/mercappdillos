package com.mcomobile.mercadillos;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class InfoMercado extends TabActivity{

	private static final String INFO = "Info";
	private static final String LEER = "Leer";
	private static final String ESCRIBIR = "Escribir";
	private static final String LLEGAR = "Llegar";
	TextView municipio;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.infodetail_gral);
        
        Bundle extras = InfoMercado.this.getIntent().getExtras();
	 	if(extras!=null){
	 		String mercado = extras.getString("Mercado");
	 		
	    Resources res = getResources();
	 		 
	 	TabHost tabHost=(TabHost)findViewById(android.R.id.tabhost);
	 	tabHost.setup();	

        // Inbox Tab
        TabSpec infoSpec = tabHost.newTabSpec(INFO);
        // Tab Icon       
        infoSpec.setIndicator("", res.getDrawable(R.drawable.tab_info));
        Intent infoIntent = new Intent(this, DetalleMercado.class);
        infoIntent.putExtra("Mercado", mercado);
        // Tab Content
        infoSpec.setContent(infoIntent);
        
        // Outbox Tab
        TabSpec webSpec = tabHost.newTabSpec(LEER);
        webSpec.setIndicator("", res.getDrawable(R.drawable.tab_leer));
        Intent webIntent = new Intent(this, DetalleLeer.class);
        webIntent.putExtra("Mercado", mercado);
        webSpec.setContent(webIntent);       
        
        // Profile Tab
        TabSpec mailSpec = tabHost.newTabSpec(ESCRIBIR);
        mailSpec.setIndicator("", res.getDrawable(R.drawable.tab_escribir));
        Intent mailIntent = new Intent(this, DetalleEscribir.class);
        mailIntent.putExtra("Mercado", mercado);
        mailSpec.setContent(mailIntent);
        
        TabSpec telfSpec = tabHost.newTabSpec(LLEGAR);
        telfSpec.setIndicator("", res.getDrawable(R.drawable.tab_llegar));
        Intent telfIntent = new Intent(this, DetalleLlegar.class);
        telfIntent.putExtra("Mercado", mercado);
        telfSpec.setContent(telfIntent);
	 	
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(infoSpec); // Adding Inbox tab
        tabHost.addTab(webSpec); // Adding Outbox tab
        tabHost.addTab(mailSpec); // Adding Profile tab
        tabHost.addTab(telfSpec); // Adding Profile tab
        
	 	}
    }

    public void radar(View view) {   
    	Bundle extras = InfoMercado.this.getIntent().getExtras();
     	if(extras!=null){
     		String mercado = extras.getString("Mercado");
    			
    	Intent i = new Intent(this, RealidadAumentada.class);
    	i.putExtra("Mercado", mercado);
        startActivity(i);
        finish();}
    }
   
    public void mapa(View view) {
    	Bundle extras = InfoMercado.this.getIntent().getExtras();
     	if(extras!=null){
     		String mercado = extras.getString("Mercado");
    			
    	Intent i = new Intent(this, Mapa.class );
    	i.putExtra("Mercado", mercado);
        startActivity(i);
        finish();}
    		}

    public void compartir(View view) {    	
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.putExtra(Intent.EXTRA_SUBJECT,
    			 "Descargue la APP MercAPPdillos");  
    	intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.mcomobile.mercadillos");
    	intent.setType("text/plain");
    	startActivity(Intent.createChooser(intent, "Compartir link"));
    }

    public void home(View view) {   	
    	Intent intent = new Intent(InfoMercado.this, MenuGral.class);  
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        startActivity(intent); 
        finish();
    }

    public void atras(View view) {  	
    	Intent intent = new Intent(getApplicationContext(), Listado.class);
    	startActivity(intent);
        finish();   
     	}
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Intent intent = new Intent(InfoMercado.this, Listado.class);  
	        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);  
	        startActivity(intent);
	        finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}	
}
