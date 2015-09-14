package com.mcomobile.mercadillos;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class DetalleEscribir extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;
	private RatingBar ratingBar;
	JSONParser jsonParser = new JSONParser();
	EditText inputName;
	EditText inputPrice;
	EditText inputDesc;
	Detector objDetector;
	Boolean hayInt = false;
	String ratingValue = "Sin valoración";
	// url to create new product
	private static String url_crear_comentario = "http://mcomobile.com/php_merca/comentarios.php";

    String mercado,tipo,municipio,id,comentario;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcomentario);
		
		Bundle extras = DetalleEscribir.this.getIntent().getExtras();
		if(extras!=null){
	 		mercado = extras.getString("Mercado");
	 		
	 		AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
	        SQLiteDatabase db = MDB.getWritableDatabase();
	            Cursor c = db.rawQuery("select nombre,tipo,municipio from mercadillos where nombre='" + mercado +"'", null);
	 
	            if (c != null ) {
	                if  (c.moveToFirst()) {
	                    do {
	                        municipio = c.getString(c.getColumnIndex("municipio"));
	                        tipo = c.getString(c.getColumnIndex("tipo"));                        
	                       
	                    }while (c.moveToNext());
	                } 
	            }	
	            c.close();
	            db.close();
	 	}
		// Edit Text
		inputName = (EditText) findViewById(R.id.inputName);
		inputDesc = (EditText) findViewById(R.id.inputDesc);
		
		inputName.setText(mercado);
		comprobarConexion();
		addListenerOnRatingBar();
		// Create button
		Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);

		// button click event
		btnCreateProduct.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new product in background thread
				if(inputDesc.equals("")||inputDesc==null){
					Toast toast = Toast.makeText(DetalleEscribir.this, "Por favor, inserte un comentario antes de publicarlo.", Toast.LENGTH_LONG);
		        	toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		            toast.show();
				}else{
					new CreateNewProduct().execute();
				}	
			}
		});
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DetalleEscribir.this);
			pDialog.setMessage("Insertando comentario...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

			mercado = inputName.getText().toString();
			comentario = inputDesc.getText().toString();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {

			String nota = ratingValue;


			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mercado", mercado));
			params.add(new BasicNameValuePair("nota", nota));
			params.add(new BasicNameValuePair("comentario", comentario));
			params.add(new BasicNameValuePair("municipio", municipio));
			params.add(new BasicNameValuePair("tipo", tipo));
			params.add(new BasicNameValuePair("id", "2"));

			Log.e("","Datos: " + mercado + " -- " + nota + " -- " + comentario + " -- " + municipio + " -- " + tipo);

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_crear_comentario,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			Toast toast = Toast.makeText(DetalleEscribir.this, "Comentario creado correctamente.", Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
            toast.show();
            pDialog.dismiss();
            Intent i = new Intent(DetalleEscribir.this,InfoMercado.class);
            i.putExtra("Mercado",mercado);
            startActivity(i);
            finish();
		}

	}
public void comprobarConexion(){
    	
    	objDetector = new Detector(getApplicationContext());       
        hayInt = objDetector.estasConectado();
        	if(!hayInt){       		
        		Toast toast = Toast.makeText(DetalleEscribir.this, "Necesita conexi�n de datos para utilizar esta sección.", Toast.LENGTH_LONG);
            	toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                toast.show();
    }
    } 

public void addListenerOnRatingBar() {

	ratingBar = (RatingBar) findViewById(R.id.ratingMercado);
	
	ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

		public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {

			ratingValue = String.valueOf(rating);

		}
	});
  }
}

