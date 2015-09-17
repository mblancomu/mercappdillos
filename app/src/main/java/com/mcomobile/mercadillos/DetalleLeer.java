package com.mcomobile.mercadillos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DetalleLeer extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> commentsList;
	Boolean hayInt = false;
	// url to get all products list
	private static String url_all_comentarios = "http://mcomobile.com/php_merca/comentarios_php.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "comentarios";
	private static final String TAG_COMENT = "comentario";
	private static final String TAG_NOTA = "nota";
	private static final String TAG_TIPO = "tipo";
	private static final String TAG_MUNI = "municipio";
	private static final String TAG_MERCADO = "mercado";
	private static final String TAG_ID= "id";


	private String myJSON;

	 public String mercado,mercadoSelect;
	// products JSONArray
	JSONArray comments = null;
	Detector objDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tableer);

		Bundle extras = DetalleLeer.this.getIntent().getExtras();
		if(extras!=null){
	 		mercadoSelect = extras.getString("Mercado");
		}
		// Hashmap for ListView
		commentsList = new ArrayList<HashMap<String, String>>();
		comprobarConexion();
		// Loading products in Background Thread
		// Get listview
		ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				TextView textoTitulo = (TextView) view.findViewById(R.id.nombreComentario);

				CharSequence mercado = textoTitulo.getText();
				
				Toast.makeText(DetalleLeer.this, "Comentario: "+mercado, Toast.LENGTH_LONG).show();
			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	/*class LoadAllProducts extends AsyncTask<String, String, String> {

		*//**
		 * Before starting background thread Show Progress Dialog
		 * *//*
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DetalleLeer.this);
			pDialog.setMessage("Descargando comentarios. Por favor, espere...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		*//**
		 * getting All products from url
		 * *//*
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_comentarios, "GET", params);

			Log.e("","Recibo esto: " + json.toString());

			if (json ==  null){

				Toast.makeText(DetalleLeer.this,"Sin comentarios¡¡",Toast.LENGTH_SHORT).show();

			} else {

				// Check your log cat for JSON reponse
				Log.d("All Products: ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// products found
						// Getting Array of Products
						products = json.getJSONArray(TAG_PRODUCTS);

						// looping through All Products
						for (int i = 0; i < products.length(); i++) {
							JSONObject c = products.getJSONObject(i);

							// Storing each json item in variable
							String comentario = c.getString(TAG_COMENT);
							String nota = c.getString(TAG_NOTA);
							String tipo = c.getString(TAG_TIPO);
							String municipio = c.getString(TAG_MUNI);
							mercado = c.getString(TAG_MERCADO);
							String id = c.getString(TAG_ID);

							if (mercado != null && mercado.equals(mercadoSelect)) {
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key => value
								map.put(TAG_COMENT, comentario);
								map.put(TAG_NOTA, nota);
								map.put(TAG_TIPO, tipo);
								map.put(TAG_MUNI, municipio);
								map.put(TAG_MERCADO, mercado);
								map.put(TAG_ID, id);

								// adding HashList to ArrayList
								productsList.add(map);
							} else {
							}
						}
					} else {
						// no products found
						// Launch Add New product Activity

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			return null;
		}

		*//**
		 * After completing background task Dismiss the progress dialog
		 * **//*
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					*//**
					 * Updating parsed JSON data into ListView
					 * *//*
					if(mercado!=null){
						ListAdapter adapter = new SimpleAdapter(
								DetalleLeer.this, productsList,
								R.layout.list_item_comentario, new String[] { TAG_COMENT,
										TAG_NOTA},
								new int[] { R.id.nombreComentario, R.id.tipoPuntuacion });
						// updating listview
						setListAdapter(adapter);
				}else{
					Toast.makeText(DetalleLeer.this, "No existen comentarios para este mercado.", Toast.LENGTH_LONG).show();
				}
				}
			});

		}

	}*/


public void comprobarConexion(){
    	
    	objDetector = new Detector(getApplicationContext());       
        hayInt = objDetector.estasConectado();
        	if(!hayInt){       		
        		Toast toast = Toast.makeText(DetalleLeer.this, "Necesita conexión de datos para utilizar esta sección.", Toast.LENGTH_LONG);
            	toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                toast.show();
    }else{

				getData();
    }
    }

@Override
public void onRestart() {

    // TODO Auto-generated method stub
    super.onRestart();
    comprobarConexion();
}

	protected void showList() {
		try {
			JSONObject jsonObj = new JSONObject(myJSON);
			comments = jsonObj.getJSONArray(TAG_PRODUCTS);

			for (int i = 0; i < comments.length(); i++) {
				JSONObject c = comments.getJSONObject(i);

				String comentario = c.getString(TAG_COMENT);
				String nota = c.getString(TAG_NOTA);
				String tipo = c.getString(TAG_TIPO);
				String municipio = c.getString(TAG_MUNI);
				mercado = c.getString(TAG_MERCADO);
				String id = c.getString(TAG_ID);

				if (mercado != null && mercado.equals(mercadoSelect)) {
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(TAG_COMENT, comentario);
					map.put(TAG_NOTA, nota);
					map.put(TAG_TIPO, tipo);
					map.put(TAG_MUNI, municipio);
					map.put(TAG_MERCADO, mercado);
					map.put(TAG_ID, id);

					// adding HashList to ArrayList
					commentsList.add(map);

				}
			}

				if (mercado != null) {
					ListAdapter adapter = new SimpleAdapter(
							DetalleLeer.this, commentsList,
							R.layout.list_item_comentario, new String[]{TAG_COMENT,
							TAG_NOTA},
							new int[]{R.id.nombreComentario, R.id.tipoPuntuacion});
					// updating listview
					setListAdapter(adapter);
				} else {
					Toast.makeText(DetalleLeer.this, "No existen comentarios para este mercado.", Toast.LENGTH_LONG).show();
				}

			}catch(JSONException e){
				e.printStackTrace();
			}

		}


	public void getData(){
		class GetDataJSON extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... params) {
				DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
				HttpPost httppost = new HttpPost(url_all_comentarios);

				// Depends on your web service
				httppost.setHeader("Content-type", "application/json");

				InputStream inputStream = null;
				String result = null;
				try {
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();

					inputStream = entity.getContent();
					// json is UTF-8 by default
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					StringBuilder sb = new StringBuilder();

					String line = null;
					while ((line = reader.readLine()) != null)
					{
						sb.append(line + "\n");
					}
					result = sb.toString();
				} catch (Exception e) {
					// Oops
				}
				finally {
					try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result){
				myJSON=result;

				Log.e("","Los resultados son: " + myJSON.toString());
				showList();
			}
		}

		GetDataJSON g = new GetDataJSON();
		g.execute();

	}
}
