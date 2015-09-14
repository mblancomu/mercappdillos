package com.mcomobile.mercadillos;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mcomobile.mercadillos.db.AdminSQLiteOpenHelper;

public class DetalleLlegar extends Activity implements LocationListener {

private WebView webView;
public Typeface font;
String mercado,nombre,latitud,longitud,transporte;
private LocationManager locationManager;
double lat,lng;
Detector objDetector;
Boolean hayInt = false;
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tabllegar);
    
    font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    String provider = locationManager.getBestProvider(criteria, false);
    Location lastLoc = locationManager.getLastKnownLocation(provider);
    locationManager.requestLocationUpdates(
   			provider, 30000, 50, this); 
    
	if(lastLoc != null)
	{
   	lat = lastLoc.getLatitude();
   	lng = lastLoc.getLongitude();
	}
    AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
    SQLiteDatabase db = MDB.getWritableDatabase();		
    Bundle extras = DetalleLlegar.this.getIntent().getExtras();
	if(extras!=null){
 		mercado = extras.getString("Mercado");
 		
 		Cursor c = db.rawQuery(
 	            "select nombre,latitud,longitud from mercadillos where nombre='" + mercado +"'", null);
 			if(c != null && c.moveToFirst())
 			{
 				nombre = unescape(c.getString(c.getColumnIndex("nombre")));
 		 		latitud = unescape(c.getString(c.getColumnIndex("latitud")));
 		 		longitud = unescape(c.getString(c.getColumnIndex("longitud")));		
 			}
 			c.close();
 	 	 db.close();
 	}
	comprobarConexion();     
}
private String unescape(String description) {
    return description.replaceAll("\\\\n", "\\\n");
}
@Override
public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
	
}
@Override
public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
}
@Override
public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
}
@Override
public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	
}
@Override                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
protected void onPause() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
	super.onPause();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
	if(locationManager!=null){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		locationManager.removeUpdates(this);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
	}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
}
public void comprobarConexion(){
	
	objDetector = new Detector(getApplicationContext());       
    hayInt = objDetector.estasConectado();
    	if(!hayInt){       		
    		Toast toast = Toast.makeText(DetalleLlegar.this, "Necesita conexi�n de datos para utilizar esta secci�n.", Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
            toast.show();
}else{
	String url = "http://maps.google.com/maps?" + "saddr="+lat+","+lng+"&daddr="+latitud+","+longitud;
    webView = (WebView) findViewById(R.id.wvLlegar);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebViewClient(new WebViewClient());
    webView.loadUrl(url);
}
} 
}
