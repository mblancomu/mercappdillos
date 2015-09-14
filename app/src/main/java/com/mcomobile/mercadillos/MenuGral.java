package com.mcomobile.mercadillos;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mcomobile.mercadillos.db.AdminSQLiteOpenHelper;

public class MenuGral extends Activity{

	Button btnmapa,btnlist,btnradar,btninfo;
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	final Context context = this;
	boolean fallo = false;
	
	private static String url_all_recursos = "http://mcomobile.com/php_merca/mercadillos.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MERCADILLOS = "mercadillos";
    private static final String	TAG_NOMBREID = "nombre";
   private static final String	TAG_FECHA = "fecha";
   private static final String	TAG_TIPO = "tipo";
   private static final String	TAG_MUNI = "municipio";
   private static final String	TAG_DIRECCION = "direccion";
   private static final String	TAG_LATITUD = "latitud";
   private static final String	TAG_LONGITUD = "longitud";
   private static final String	TAG_HORARIO = "horario";
   private static final String	TAG_PUESTOS = "puestos";
   private static final String	TAG_TRANSPORTE = "transporte";
   private static final String	TAG_OBSERV = "observaciones";

   private static String url_actualizar = "http://mcomobile.com/php_merca/get_version.php";
	private static final String TAG_ACTUALIZAR = "actualizar";
	private static final String TAG_VERSION = "version";
	
	JSONArray products = null;
	Boolean hayInt = false;
	Detector objDetector;
	public String versionAppandroid = "",versionAppweb = "";
	int versionApp = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inicio);
		
		btnmapa = (Button)findViewById(R.id.btnmapa);
		btnlist = (Button)findViewById(R.id.btnlist);
		btnradar = (Button)findViewById(R.id.btnradar);
		btninfo = (Button)findViewById(R.id.btninfo);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
		btnmapa.setTypeface(font);
		btnlist.setTypeface(font);
		btnradar.setTypeface(font);
		btninfo.setTypeface(font);
		
		obtenerVersion();     
        comprobarConexion(); 
	}
	
public void comprobarConexion(){
    	
    	objDetector = new Detector(getApplicationContext());       
        hayInt = objDetector.estasConectado();
        	if(hayInt){       		
        		new LoadActualizacion().execute();
    }
    } 
     
    class LoadMercados extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MenuGral.this);
			pDialog.setMessage("Cargando información.Por favor, espere...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_recursos, "GET", params);
			// Check your log cat for JSON reponse
			Log.d("Todos los mercados: ", json.toString());
			

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		        SQLiteDatabase db = MDB.getWritableDatabase();
	            db.delete("mercadillos", null, null);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_MERCADILLOS);

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable         
						String nombre = c.getString(TAG_NOMBREID);
                        String fecha = c.getString(TAG_FECHA);						
						String tipo = c.getString(TAG_TIPO);       
						String municipio = c.getString(TAG_MUNI);           
						String direccion = c.getString(TAG_DIRECCION);       
						String latitud = c.getString(TAG_LATITUD);
						String longitud = c.getString(TAG_LONGITUD);
						String horario = c.getString(TAG_HORARIO);
						String puestos = c.getString(TAG_PUESTOS);
						String transporte = c.getString(TAG_TRANSPORTE);
						String observaciones = c.getString(TAG_OBSERV);						 
   
						ContentValues registro = new ContentValues();
                           registro.put("nombre", nombre);
                           registro.put("fecha", fecha);
                           registro.put("tipo", tipo);
                           registro.put("municipio", municipio);
                           registro.put("direccion", direccion);
                           registro.put("latitud", latitud);
                           registro.put("longitud", longitud);
                           registro.put("horario", horario);
                           registro.put("puestos", puestos);
                           registro.put("transporte", transporte);
                           registro.put("observaciones", observaciones);                       
                           db.insert("mercadillos", null, registro);
					   							   
					}
				} else {
					Toast.makeText(MenuGral.this, "No existen datos??", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}			

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(MenuGral.this, "Información actualizada.", Toast.LENGTH_SHORT);
	            	toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
	                toast.show();
					actualizarVersion();
				}
			});

		}

	}
    
    public void activardatos(){
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this);

        // Setting Dialog Title
        alertDialog.setTitle("Actualización disponible");

        // Setting Dialog Message
        alertDialog.setMessage("¿Desea actualizar la aplicación?");

        alertDialog.setPositiveButton("SI",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(DialogInterface dialog, int which) {
                    	 
                    	if(versionAppandroid!=versionAppweb){
                    		new LoadMercados().execute();
                    		
                		}   		                   	                                     
                    }
                });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event                       
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }
    
public void obtenerVersion(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
    SQLiteDatabase db = MDB.getWritableDatabase();
    
 		Cursor c = db.rawQuery(
                "select version from actualizar", null);
 		if(c != null && c.moveToFirst())
 		{			
 		String version = c.getString(c.getColumnIndex("version"));
 		
 		versionAppandroid = version;
 		}
 		c.close();
	 	 db.close();
}

public void actualizarVersion(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
    SQLiteDatabase db = MDB.getWritableDatabase();
    db.delete("actualizar", null, null);
    
    ContentValues registro = new ContentValues();
    registro.put("version", versionAppweb);
    db.insert("actualizar", null, registro);	
}

public void obtenerVersionWeb(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
    SQLiteDatabase db = MDB.getWritableDatabase();
 		Cursor c = db.rawQuery(
                "select versionweb from actualizarweb", null);
 		if(c != null && c.moveToFirst())
 		{			
 		String versionweb = c.getString(c.getColumnIndex("versionweb"));
 		
 		versionAppweb = versionweb;
 		}
 		c.close();
	 	db.close();
}

public void comprobandoVersion(){
	
	if(versionAppweb.equals(versionAppandroid)){		
		Toast toast = Toast.makeText(MenuGral.this, "Información actualizada.", Toast.LENGTH_SHORT);
    	            	toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
    	                toast.show();
	}else{	
		versionApp = 1;
		activardatos();
	}		
}

class LoadActualizacion extends AsyncTask<String, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(MenuGral.this);
		pDialog.setMessage("Comprobando actualizaciones.Por favor, espere...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	/**
	 * getting All products from url
	 * */
	@Override
	protected String doInBackground(String... args) {
		// Building Parameters 
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_actualizar, "GET", params);
		if(json!=null){
		// Check your log cat for JSON reponse
		Log.d("Todas las versiones: ", json.toString());

		try {
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);
		    		    
			if (success == 1) {
				// products found
				// Getting Array of Products
				products = json.getJSONArray(TAG_ACTUALIZAR);

				// looping through All Products
				for (int i = 0; i < products.length(); i++) {
					JSONObject c = products.getJSONObject(i);

					// Storing each json item in variable         
					versionAppweb = c.getString(TAG_VERSION);                       				 																	
				}
			} else {
				Toast.makeText(MenuGral.this, "No existen datos??", Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();			
		}
}else{
	runOnUiThread(new Runnable() {
		@Override
		public void run() {	
			Toast.makeText(MenuGral.this, "Problemas de conexión con el servidor.", Toast.LENGTH_LONG).show();
			fallo=true;
		}
	});
}
		return null;
	}
	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	@Override
	protected void onPostExecute(String file_url) {
		// dismiss the dialog after getting all products
		pDialog.dismiss();
		// updating UI from Background Thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {	
				if(fallo!=true){
					comprobandoVersion();
				}
			}
		});
	}
}

public void mapa (View view){
	Intent intent = new Intent(this,Mapa.class);
	startActivity(intent);
}
public void listado (View view){
	Intent intent = new Intent(this,Filtrar.class);
	startActivity(intent);
}
public void realidad (View view){
	/*Intent intent = new Intent(this,RealidadAumentada.class);
	startActivity(intent);*/
}
public void infogral (View view){
	alertaInfo();
}

public void alertaInfo() {
	final Dialog dialog = new Dialog(context);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.popuptextos_infoapp);
    Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
    final TextView tvInfo= (TextView)dialog.findViewById(R.id.tvInfoApp);
    tvInfo.setTypeface(font);
    // Getting a reference to Close button, and close the popup when
    // clicked.
    ImageButton close = (ImageButton) dialog.findViewById(R.id.btn_cerrarApp);
    ImageButton mcolink = (ImageButton) dialog.findViewById(R.id.ivMco);
    close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
            }
    });
    mcolink.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
       	 Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://www.mcomobile.com/"));
			startActivity(intent);
        }
});
	
    dialog.show();
}
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
        exitByBackKey();

        //moveTaskToBack(false);

        return true;
    }
    return super.onKeyDown(keyCode, event);
}

protected void exitByBackKey() {

    AlertDialog alertbox = new AlertDialog.Builder(this)
    .setMessage("¿Desea salir de la Aplicación?")
    .setPositiveButton("SI", new DialogInterface.OnClickListener() {

        // do something when the button is clicked
        public void onClick(DialogInterface arg0, int arg1) {	
        	System.exit(0);
            //close();
        }
    })
    .setNegativeButton("NO", new DialogInterface.OnClickListener() {

        // do something when the button is clicked
        public void onClick(DialogInterface arg0, int arg1) {
                       }
    })
      .show();

}
}
