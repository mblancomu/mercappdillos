package com.mcomobile.mercadillos;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetalleMercado extends Activity {

	// Progress Dialog
    static TextView tvnombre,tvtipo,tvfecha,tvdireccion,tvmuni,tvpuestos,tvtransporte,tvobservaciones,tvhorario;
    public String nombre,tipo,fecha,direccion,muni,puestos,transporte,observaciones,horario;
	LinearLayout container;
	public String mercado;
	public Typeface font;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.info_list);
		
		font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
        Bundle extras = DetalleMercado.this.getIntent().getExtras();
	 	if(extras!=null){
	 		mercado = extras.getString("Mercado");
	 		
	 		tvnombre = (TextView) findViewById(R.id.tvnombreMercado);
	 		tvtipo = (TextView) findViewById(R.id.tvtipoMercado);
	 		tvfecha = (TextView) findViewById(R.id.tvfechaMercado);
	 		tvmuni = (TextView) findViewById(R.id.tvmuniMercado);
	 		tvhorario = (TextView) findViewById(R.id.tvhorarioMercado);
	 		tvtransporte = (TextView) findViewById(R.id.tvtransporteMercado);
	 		tvobservaciones = (TextView) findViewById(R.id.tvobservacionesMercado);
	 		tvpuestos = (TextView) findViewById(R.id.tvpuestosMercado);
	 		tvdireccion = (TextView) findViewById(R.id.tvdireccionMercado);	 			 		
	 		tvnombre.setTypeface(font);
	 		tvnombre.setText(mercado);
	 	}        
	 	infobbdd();      
    }	
		private String unescape(String description) {
	    return description.replaceAll("\\\\n", "\\\n");
	}

public void infobbdd(){

AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
SQLiteDatabase db = MDB.getWritableDatabase();

Bundle extras = DetalleMercado.this.getIntent().getExtras();

if(extras!=null){

		String mercado = extras.getString("Mercado");
		
		Cursor c = db.rawQuery(
            "select nombre,fecha,tipo,municipio,direccion,horario,puestos,transporte,observaciones from mercadillos where nombre='" + mercado +"'", null);
		if(c != null && c.moveToFirst())
		{
			nombre = unescape(c.getString(c.getColumnIndex("nombre")));
	 		tipo = unescape(c.getString(c.getColumnIndex("tipo")));
	 		fecha = unescape(c.getString(c.getColumnIndex("fecha")));
	 		muni = unescape(c.getString(c.getColumnIndex("municipio")));
	 		horario = unescape(c.getString(c.getColumnIndex("horario")));
	 		transporte = unescape(c.getString(c.getColumnIndex("transporte")));
	 		observaciones = unescape(c.getString(c.getColumnIndex("observaciones")));
	 		puestos = unescape(c.getString(c.getColumnIndex("puestos")));
	 		direccion = unescape(c.getString(c.getColumnIndex("direccion")));

	 		tvtipo.setTypeface(font);
	 		tvfecha.setTypeface(font);
	 		tvmuni.setTypeface(font);
	 		tvhorario.setTypeface(font);
	 		tvtransporte.setTypeface(font);
	 		tvobservaciones.setTypeface(font);
	 		tvpuestos.setTypeface(font);
	 		tvdireccion.setTypeface(font);
	 		
	 		tvtipo.setText(tipo);
	 		tvfecha.setText(fecha);
	 		tvmuni.setText(muni);
	 		tvhorario.setText(horario);
	 		tvtransporte.setText(transporte);
	 		tvobservaciones.setText(observaciones);
	 		tvpuestos.setText(puestos);
	 		tvdireccion.setText(direccion);
	 		
		}
		c.close();
 	 db.close();
}
}

public boolean onKeyDown(int keyCode, KeyEvent event) {
if (keyCode == KeyEvent.KEYCODE_BACK) {	
	Intent intent = new Intent(DetalleMercado.this, Listado.class);  
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("Mercado", mercado);
    startActivity(intent);
    finish();
    return true;
}
return super.onKeyDown(keyCode, event);
}
}
