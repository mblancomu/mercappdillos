package com.mcomobile.mercadillos;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

public class Mapa extends android.support.v4.app.FragmentActivity implements OnSeekBarChangeListener,OnInfoWindowClickListener,RadioGroup.OnCheckedChangeListener,OnMapClickListener, OnMarkerDragListener,OnMarkerClickListener,LocationListener {                                                                                                                                                                                                                                                                                                     

private GoogleMap mapa = null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
final LatLng Foto = null;  
Integer icono,iconMarcador,iconMarker;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
Double lat,log,latgeo,longgeo; 
public BitmapDescriptor pos;
Polyline polyline1;
final Context context = this;
EditText userInput;
public int Alerta = 0;
private LocationManager locationManager;
PendingIntent pendingIntent;
private static final long POINT_RADIUS = 50; // in Meters
private static final long PROX_ALERT_EXPIRATION = -1; // It will never expire
private static final String PROX_ALERT_INTENT = "com.mco.mercappdillos";
LinearLayout container;
public CheckBox checkanti,checkcolecc,checkgene,checkarte,checknavi,checkeco,checkotros,checksegunda;
private ArrayList<Marker> markerAntiguedades = new ArrayList<Marker>();
private ArrayList<Marker> markerColeccionismo = new ArrayList<Marker>();
private ArrayList<Marker> markerGeneralista = new ArrayList<Marker>();
private ArrayList<Marker> markerArtesania = new ArrayList<Marker>();
private ArrayList<Marker> markerNavidad = new ArrayList<Marker>();
private ArrayList<Marker> markerEco = new ArrayList<Marker>();
private ArrayList<Marker> markerOtros = new ArrayList<Marker>();
private ArrayList<Marker> markerSegunda = new ArrayList<Marker>();
private ArrayList<Marker> markerTodos = new ArrayList<Marker>();
SharedPreferences sharedPreferences;
private Marker alerta;
private SharedPreferences preferenciasSettings;
private SharedPreferences.Editor preferenciasEditor;
boolean guardarantig,guardarcolecc,guardargene,guardararte,guardarnavi,guardareco,guardarotros,guardarsegunda,guardarmapa,guardarterreno,guardarsatelite,guardaralerta;
RadioButton mapanormal;
public String nombreMarcador="";
public String slat,slon,mercado,realidad;
Double radioDistancia, distancia;
Location myLocation;
LinearLayout lradiovision;
ToggleButton tgradio;
SeekBar mSeekBarRadio,mSeekBarRadioRadar;
TextView infoDistancia;
int mts,mtsLimite;
Location locationFoto;

@Override                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
protected void onCreate(Bundle savedInstanceState) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                
	super.onCreate(savedInstanceState);  
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.mapa);
	
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
    
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    lradiovision =  (LinearLayout) findViewById(R.id.layout_radiovision);
	lradiovision.setVisibility(View.GONE);
	mSeekBarRadio = (SeekBar) findViewById(R.id.seekBarRadio);
	mSeekBarRadio.setOnSeekBarChangeListener(this);
	infoDistancia = (TextView)findViewById(R.id.tvDistancia);
    preferenciasSettings = getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);	  
    radioDistancia = 1000000.00;
    if(mapa==null){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
		//get the map                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
		mapa = ((SupportMapFragment) getSupportFragmentManager()                                                                                                                                                                                                                                                                                                                                                                                                                                                        
				   .findFragmentById(R.id.map)).getMap();   
		mapa.setMyLocationEnabled(false);
		//check in case map/ Google Play services not available                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		if(mapa!=null){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
			//ok - proceed                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
			mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			Limites.addLimites(mapa);
		} 
    }   
  				
    Bundle extras = Mapa.this.getIntent().getExtras();
 	if(extras!=null){
    
    mercado = extras.getString("Mercado");
    realidad = extras.getString("RealidadPosition");
    

if(mercado!=null){
	SQLiteDatabase db = MDB.getWritableDatabase();	
	Cursor c = db.rawQuery(
            "select nombre,tipo,latitud,longitud from mercadillos where nombre='" + mercado +"'", null);

        if(c != null && c.moveToFirst()) {
            
                    String nombre = c.getString(c.getColumnIndex("nombre"));
                    String tipo = c.getString(c.getColumnIndex("tipo"));
                    String latitud = c.getString(c.getColumnIndex("latitud")); 
                    String longitud = c.getString(c.getColumnIndex("longitud"));
                   
                    if(tipo.equals("Mercadillo de Antig�edades")){
        	 			iconMarcador = R.drawable.markerant;
        	 		}else if (tipo.equals("Mercado Ecol�gico")){
        	 			iconMarcador = R.drawable.markereco;
        	 		}else if (tipo.equals("Mercadillo Generalista")){
        	 			iconMarcador = R.drawable.markergen;
        	 		}else if(tipo.equals("Mercado de Artesan�a")){
        	 			iconMarcador =R.drawable.markerart;
        	 		}else if(tipo.equals("Otro Mercadillo o Feria")){
        	 			iconMarcador = R.drawable.markerotros;
        	 		}else if (tipo.equals("Rastro de Segunda Mano")){
        	 			iconMarcador = R.drawable.markersecond;
        	 		}else if (tipo.equals("Mercado de Navidad")){
        	 			iconMarcador = R.drawable.markernav;
        	 		}else if (tipo.equals("Mercadillo de Coleccionismo")){
        	 			iconMarcador = R.drawable.markercol;
        	 		} else{
        	 			icono = R.drawable.marcador;
        	 		}         
                			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
                	        = BitmapDescriptorFactory.fromResource(iconMarcador);

                    lat = Double.parseDouble(latitud);
    				log = Double.parseDouble(longitud); 
    			    
    			    LatLng Foto = new LatLng(lat,log);
    			    
    			    mapa.addMarker(new MarkerOptions()
    			    .position(Foto)
    			    .title(nombre)
    			    .icon(pos)
    			    .snippet(tipo)
    			    .anchor(0.5f, 0.5f));
    			    
    			    CameraPosition camPos = new CameraPosition.Builder()
				    .target(Foto)   //Centramos el mapa en Madrid
				    .zoom(17)         //Establecemos el zoom en 19
				    .bearing(0)      //Establecemos la orientaci�n con el noreste arriba
				    .tilt(70)         //Bajamos el punto de vista de la c�mara 70 grados
				    .build();
			
			CameraUpdate camUpd3 = 
					CameraUpdateFactory.newCameraPosition(camPos);
			
			mapa.animateCamera(camUpd3);
                    
        }	
        c.close();
 	    db.close();
}else{
		  				SQLiteDatabase db = MDB.getWritableDatabase();
		  		    	Cursor c = db.rawQuery(
		  		                "select nombre,tipo,latitud,longitud from mercadillos", null);
		  		    
		  		    	if (c.moveToFirst())
		  		    		do {			
		  		    			final  String nombre = c.getString(c.getColumnIndex("nombre"));
		  		                 String tipo = c.getString(c.getColumnIndex("tipo"));
		  		                 String latitud = c.getString(c.getColumnIndex("latitud")); 
		  		                 String longitud = c.getString(c.getColumnIndex("longitud"));

		  		                   if(tipo.equals("Mercadillo de Antig�edades")){
        	 	                		iconMarcador = R.drawable.markerant;
        	 	                	}else if (tipo.equals("Mercado Ecol�gico")){
        	 	                		iconMarcador = R.drawable.markereco;
        	 	                	}else if (tipo.equals("Mercadillo Generalista")){
        	 	                		iconMarcador = R.drawable.markergen;
        	 	                	}else if(tipo.equals("Mercado de Artesan�a")){
        	 	                		iconMarcador =R.drawable.markerart;
        	 	                	}else if(tipo.equals("Otro Mercadillo o Feria")){
        	 	                		iconMarcador = R.drawable.markerotros;
        	 	                	}else if (tipo.equals("Rastro de Segunda Mano")){
        	 	                		iconMarcador = R.drawable.markersecond;
        	 	                	}else if (tipo.equals("Mercado de Navidad")){
        	 	                		iconMarcador = R.drawable.markernav;
        	 	                	}else if (tipo.equals("Mercadillo de Coleccionismo")){
        	 	                		iconMarcador = R.drawable.markercol;
        	 	                	} else{
        	 	                		icono = R.drawable.marcador;
        	 	                	}         
		  		                             			
										BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		  		             	        = BitmapDescriptorFactory.fromResource(iconMarcador);

		  		                lat = Double.parseDouble(latitud);
		  		 				log = Double.parseDouble(longitud); 
		  		 			    
		  		 			    LatLng Foto = new LatLng(lat,log);

		  		 		
		  		              Marker marker = mapa.addMarker(new MarkerOptions()
		  		 			    .position(Foto)
		  		 			    .title(nombre)
		  		 			    .icon(pos)
		  		 			    .snippet(tipo)
		  		 			    .anchor(0.5f, 0.5f));
		  		            markerColeccionismo.add(marker); 
		  		 			  	
		  		    		} while (c.moveToNext());
		  		           
		  		        c.close();	
						db.close();						
	}	
}
 	tgradio = (ToggleButton) findViewById(R.id.btn_radiovision);
    tgradio.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
             if (tgradio.isChecked()) {
            	 lradiovision.setVisibility(View.VISIBLE);                   
                } else {
                 lradiovision.setVisibility(View.INVISIBLE);  
                }
        }
    });
 	mapa.setOnInfoWindowClickListener(this);
    mapa.getUiSettings().setCompassEnabled(true);
    mapa.setInfoWindowAdapter(new MyInfoWindowAdapter());
    limiteComarca();
    Criteria criteria = new Criteria();
    String provider = locationManager.getBestProvider(criteria, false);
    myLocation = locationManager.getLastKnownLocation(provider);
    locationFoto = new Location("");
    
		    	    markersColeccionista();
		    	    markersOtros();
		    	    markersSegunda();
		    	    markersArtesania();
		    	    markersGeneralista();
		    	    markersAntiguedades();
		    	    markersNavidad();
		    	    markersEcologico();
}
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {		    
    	if (seekBar == mSeekBarRadio) {
    		mts = progress;
    		mtsLimite = progress*1000;
    		radioDistancia = (double)mtsLimite;
			infoDistancia.setText(progress+" kms");
			mostrarElementos();
		}		        	
    }

	public void onStartTrackingTouch(SeekBar arg0) {			
	}

	public void onStopTrackingTouch(SeekBar seekBar) {				
			// TODO Auto-generated method stub
		SharedPreferences prefs = getBaseContext().getSharedPreferences("mySharedPrefsFilename", Context.MODE_PRIVATE);
	    // Don't forget to call commit() when changing preferences.
	    prefs.edit().putInt("seekBarValue", seekBar.getProgress()).commit();
	}		

@Override
public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
	
}

@Override
public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	
}

@Override
public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
}

@Override
public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
}

@Override
public boolean onMarkerClick(Marker marker) {
	// TODO Auto-generated method stub
	
	return false;
}

@Override
public void onMarkerDrag(Marker marker) {
	// TODO Auto-generated method stub
	
}

@Override
public void onMarkerDragEnd(Marker marker) {
	// TODO Auto-generated method stub
	
}

@Override
public void onMarkerDragStart(Marker marker) {
	// TODO Auto-generated method stub
	
}

@Override
public void onMapClick(final LatLng point) {
	
	if(Alerta==0){
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int id) {
								// get user input and set it to result
								// edit text
								if(alerta!=null) alerta.remove();
								if(pendingIntent!=null)locationManager.removeProximityAlert(pendingIntent);
								 String marcador = userInput.getText().toString();
								
								 BitmapDescriptor alert                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
							        = BitmapDescriptorFactory.fromResource(R.drawable.markeralerta);
								    alerta = mapa.addMarker(new MarkerOptions()
								    .position(point)
								    .title(marcador)           
								    .icon(alert));				

						           double latitude = point.latitude;
						           double longitude = point.longitude;
						           Intent intent = new Intent(PROX_ALERT_INTENT);
						           PendingIntent proximityIntent = PendingIntent.getBroadcast(Mapa.this, 0, intent, 0);
						           locationManager.addProximityAlert(
						                  latitude, // the latitude of the central point of the alert region
						                  longitude, // the longitude of the central point of the alert region
						                  POINT_RADIUS, // the radius of the central point of the alert region, in meters
						                  PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no                           expiration
						                  proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
						           );

						           IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
						           registerReceiver(new ProximityIntentReceiver(), filter);
						           Toast.makeText(getApplicationContext(),"Alerta A�adida",Toast.LENGTH_SHORT).show();
						       
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
@Override
public void onInfoWindowClick(Marker arg0) {
	nombreMarcador = arg0.getTitle();
	double dlat =arg0.getPosition().latitude;
    double dlon =arg0.getPosition().longitude;
    slat = String.valueOf(dlat);
    slon = String.valueOf(dlon);
    alertaMarkers();
}

public void radiovision(View view){
	
}

public void settings(View view) {
	Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
	final Dialog ventana=new Dialog(this);
	ventana.setContentView(R.layout.popup_settings);
	ventana.setTitle("Opciones");
	
	  guardarmapa = preferenciasSettings.getBoolean("guardarmapa", guardarmapa);
	  guardarsatelite = preferenciasSettings.getBoolean("guardarsatelite", guardarsatelite);
	  guardarterreno = preferenciasSettings.getBoolean("guardarterreno", guardarterreno);
	  guardaralerta= preferenciasSettings.getBoolean("guardaralerta", guardaralerta);
	  
	Button btnCancelar = (Button) ventana.findViewById(R.id.btnCancelar);
	btnCancelar.setTypeface(font);
	Button btnAceptar = (Button) ventana.findViewById(R.id.btnAceptar);
	btnAceptar.setTypeface(font);
	Button btnBorraAlert = (Button) ventana.findViewById(R.id.btnborraralert);
	btnBorraAlert.setTypeface(font);
	final RadioButton mapanormal=(RadioButton) ventana.findViewById(R.id.rmapa);
	final RadioButton satelite=(RadioButton) ventana.findViewById(R.id.rsatelite);
	final RadioButton terreno=(RadioButton) ventana.findViewById(R.id.rterreno);	
	final CheckBox mialerta=(CheckBox) ventana.findViewById(R.id.checkalerta);
	RadioGroup rg = (RadioGroup) ventana.findViewById(R.id.rg);	
    
	mapanormal.setChecked(guardarmapa);
	satelite.setChecked(guardarsatelite);
	terreno.setChecked(guardarterreno);	
	mialerta.setChecked(guardaralerta);	
	rg.setOnCheckedChangeListener(this);
		
	btnCancelar.setOnClickListener(new OnClickListener(){
	    @Override
		public void onClick(View v) {
	           ventana.cancel();//Cerrar ventana
	}});
	
	btnBorraAlert.setOnClickListener(new OnClickListener(){
	    @Override
		public void onClick(View v) {
	    	if(alerta!=null){
	    		alerta.setVisible(false);
	    		alerta.remove();
	    	if(pendingIntent!=null)locationManager.removeProximityAlert(pendingIntent);
	    	}
	}});
	
	btnAceptar.setOnClickListener(new OnClickListener(){
		   @Override
		public void onClick(View v) {
		          ventana.cancel();//Cerrar ventana
		          //Capturar el estado de uno de los checkbox de Dialog
		          Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
		          
		          mapanormal.setTypeface(font);
		          if (mapanormal.isChecked()==true){
		        	  mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		        	  guardarmapa = true;
		        	  guardarsatelite = false;
		        	  guardarterreno = false;
		        	  
		            }else if (satelite.isChecked()==true){
		        	  mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		        	  guardarmapa = false;
		        	  guardarsatelite = true;
		        	  guardarterreno = false;
		        	  
		            }else if (terreno.isChecked()==true){
		        	  mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		        	  guardarmapa = false;
		        	  guardarsatelite = false;
		        	  guardarterreno = true;
		        	  
		            }
                    mialerta.setTypeface(font);
				          if (mialerta.isChecked()==true){
				        	  guardaralerta = true;
				        	  if(alerta!=null){
				        		  alerta.setVisible(true);	        	  
				        	  }else{
				        		  Toast.makeText(Mapa.this, "No existen alertas. Por favor, genere alguna.", Toast.LENGTH_LONG).show();
				        	  }
				            }else{
				            	if(alerta!=null)alerta.setVisible(false);	
				            	guardaralerta  = false; 	   
				            }

		         preferenciasSettings = getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);	  
		         preferenciasEditor = preferenciasSettings.edit();
		      	
		     	 preferenciasEditor.putBoolean("guardarterreno", guardarterreno);
		     	 preferenciasEditor.putBoolean("guardarsatelite", guardarsatelite);
		     	 preferenciasEditor.putBoolean("guardarmapa", guardarmapa);		     	
		     	 preferenciasEditor.putBoolean("guardaralerta", guardaralerta);
		     	 preferenciasEditor.commit();
	}});
		 
	ventana.show();

}
public void servicios(View view) {
	Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
	final Dialog ventana=new Dialog(this);
	ventana.setContentView(R.layout.popup_servicios1);
	ventana.setTitle("Marque los servicios deseados");
	
	  guardarantig = preferenciasSettings.getBoolean("guardarantig", guardarantig);
	  guardarcolecc = preferenciasSettings.getBoolean("guardarcolecc", guardarcolecc);
	  guardargene = preferenciasSettings.getBoolean("guardargene", guardargene);
	  guardararte = preferenciasSettings.getBoolean("guardararte", guardararte);
	  guardarnavi = preferenciasSettings.getBoolean("guardarnavi", guardarnavi);
	  guardareco = preferenciasSettings.getBoolean("guardareco", guardareco);
	  guardarotros = preferenciasSettings.getBoolean("guardarotros", guardarotros);
	  guardarsegunda = preferenciasSettings.getBoolean("guardarsegunda", guardarsegunda);
	  	  
	
	Button btnCancelar = (Button) ventana.findViewById(R.id.btnCancelar);
	btnCancelar.setTypeface(font);
	Button btnAceptar = (Button) ventana.findViewById(R.id.btnAceptar);
	btnAceptar.setTypeface(font);
	
	final CheckBox coleccionista=(CheckBox) ventana.findViewById(R.id.checkcolecc);
	final CheckBox antiguedades=(CheckBox) ventana.findViewById(R.id.checkanti);
	final CheckBox ecologico=(CheckBox) ventana.findViewById(R.id.checkeco);
	final CheckBox artesania=(CheckBox) ventana.findViewById(R.id.checkarte);
	final CheckBox navidad=(CheckBox) ventana.findViewById(R.id.checknavi);
	final  CheckBox generalista=(CheckBox) ventana.findViewById(R.id.checkgene);
	final CheckBox otros=(CheckBox) ventana.findViewById(R.id.checkotros);
	final  CheckBox segunda=(CheckBox) ventana.findViewById(R.id.checksegunda);
	
	coleccionista.setChecked(guardarcolecc);
	antiguedades.setChecked(guardarantig);
	ecologico.setChecked(guardareco);
	artesania.setChecked(guardararte);
	navidad.setChecked(guardarnavi);
	generalista.setChecked(guardargene);
	otros.setChecked(guardarotros);
	segunda.setChecked(guardarsegunda);
	
	btnCancelar.setOnClickListener(new OnClickListener(){
	    @Override
		public void onClick(View v) {
	           ventana.cancel();//Cerrar ventana
	}});
	btnAceptar.setOnClickListener(new OnClickListener(){
		   @Override
		public void onClick(View v) {
		          //Capturar el estado de uno de los checkbox de Dialog
		          Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");		          
		          coleccionista.setTypeface(font);
		          if (coleccionista.isChecked()==true){
		        	  guardarcolecc = true;		        	  
		        	  for (Marker marker : markerColeccionismo) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	guardarcolecc = false;
		            	for (Marker marker : markerColeccionismo) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }		          
		          antiguedades.setTypeface(font);
		          if (antiguedades.isChecked()==true){
		        	  guardarantig = true;
		        	  for (Marker marker : markerAntiguedades) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	guardarantig = false;
		            	for (Marker marker : markerAntiguedades) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }		          
		          ecologico.setTypeface(font);
		          if (ecologico.isChecked()==true){
		        	  guardareco = true;		        	  
		        	  for (Marker marker : markerEco) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	guardareco = false;
		            	for (Marker marker : markerEco) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }		         
		          artesania.setTypeface(font);
		          if (artesania.isChecked()==true){
		        	  guardararte = true;
		        	  for (Marker marker : markerArtesania) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	guardararte = false;
		            	for (Marker marker : markerArtesania) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }		          
		          navidad.setTypeface(font);
		          if (navidad.isChecked()==true){
		        	  guardarnavi = true;
		        	  for (Marker marker : markerNavidad) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	guardarnavi = false;
		            	for (Marker marker : markerNavidad) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }		         
		          generalista.setTypeface(font);
		          if (generalista.isChecked()==true){
		        	  guardargene = true;
		        	  for (Marker marker : markerGeneralista) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	 guardargene = false;
		            	for (Marker marker : markerGeneralista) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }
				  otros.setTypeface(font);
		          if (otros.isChecked()==true){
		        	  guardarotros = true;
		        	  for (Marker marker : markerOtros) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	 guardarotros = false;
		            	for (Marker marker : markerOtros) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }
				  segunda.setTypeface(font);
		          if (segunda.isChecked()==true){
		        	  guardarsegunda = true;
		        	  for (Marker marker : markerSegunda) {
		        		  double distlat = marker.getPosition().latitude;
		        		  double distlng = marker.getPosition().longitude;
		        		  locationFoto.setLatitude(distlat);
		        		  locationFoto.setLongitude(distlng);
		        	      distancia = (double) locationFoto.distanceTo(myLocation);
		        	   if(distancia<=radioDistancia){
		        		   marker.setVisible(true);
		        	   }else{
		        		   marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		        	   }    
		            	}
		            }else{
		            	 guardarsegunda = false;
		            	for (Marker marker : markerSegunda) {
		            	    marker.setVisible(false);
		            	    //marker.remove(); <-- works too!
		            	}
		          }
		             preferenciasSettings = getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE);	  
			         preferenciasEditor = preferenciasSettings.edit();
					 
			     	 preferenciasEditor.putBoolean("guardarantig", guardarantig);
			     	 preferenciasEditor.putBoolean("guardarcolecc", guardarcolecc);
			     	 preferenciasEditor.putBoolean("guardargene", guardargene);
			     	 preferenciasEditor.putBoolean("guardararte", guardararte);
			     	 preferenciasEditor.putBoolean("guardarnavi", guardarnavi);
			     	 preferenciasEditor.putBoolean("guardareco", guardareco);
					 preferenciasEditor.putBoolean("guardarotros", guardarotros);
			     	 preferenciasEditor.putBoolean("guardarsegunda", guardarsegunda);
			     	 preferenciasEditor.commit();
			     	 ventana.cancel();
		}});
	ventana.show();
}

public void alerta(View view) {
	 Alerta = 0;
	 alertacampana();
     mapa.setOnMapClickListener(this);
}
public void radar(View view) {
	String mapgen = "Mapa";
	Intent i = new Intent(this,RealidadAumentada.class );
	i.putExtra("Mapa", mapgen);
    startActivity(i);
    finish();
}
public void compartir(View view) {
	Intent intent = new Intent(Intent.ACTION_SEND);
	intent.putExtra(Intent.EXTRA_SUBJECT,
			 "Descargue MercAPPdillos");  
	intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.mco.mercappdillos");
	intent.setType("text/plain");
	startActivity(Intent.createChooser(intent, "Compartir link"));
}
public void home(View view) {
	if(mercado!=null){
		Intent intent = new Intent(Mapa.this, Listado.class);  
	    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);	
	    intent.putExtra("Mercado", mercado);
	    startActivity(intent);
	}  else if(realidad!=null){
		Intent intent = new Intent(Mapa.this, RealidadAumentada.class);  
	    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);	
	    startActivity(intent);
	} else {
	    	Intent intent = new Intent(Mapa.this, MenuGral.class);  
		    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
	    }
	finish();
}
public void alertacampana(){
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			context);

		// set title
		alertDialogBuilder.setTitle("Insertar Alerta Sonora");

		// set dialog message
		alertDialogBuilder
			.setMessage("�Desea insertar una alerta en el mapa? Por favor, haga click en el punto del mapa que desee.")
			.setCancelable(false)
			.setPositiveButton("SI",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity

				}
			  })
			.setNegativeButton("NO",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}

public void alertagps(){
	AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  
    dialogo1.setTitle("GPS Desactivado");  
    dialogo1.setMessage("�Desea activar el GPS para obtener su posicion?\n Recuerde esperar unos instantes una vez activado para recibir se�al");            
    dialogo1.setCancelable(false);  
    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {  
        @Override
		public void onClick(DialogInterface dialogo1, int id) {  
            aceptar();  
        }  
    });  
    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {  
        @Override
		public void onClick(DialogInterface dialogo1, int id) {  
            dialogo1.cancel();
        }  
    });            
    dialogo1.show();        
}

public void aceptar() {	
	Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    startActivity(settingsIntent);
}

@Override                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
protected void onPause() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
	super.onPause();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
	if(locationManager!=null){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		locationManager.removeUpdates(this);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
	}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
}

@Override
public void onCheckedChanged(RadioGroup group, int checkedId) {
	// TODO Auto-generated method stub
	
}

public void limiteComarca(){
	LatLng Poslimite = new LatLng(40.5512861,-3.7282889);
	  
    CameraPosition camPos = new CameraPosition.Builder()
    .target(Poslimite)   //Centramos el mapa en Madrid
    .zoom(8)         //Establecemos el zoom en 19
    .bearing(0)      //Establecemos la orientaci�n con el noreste arriba
    .tilt(70)         //Bajamos el punto de vista de la c�mara 70 grados
    .build();

CameraUpdate centradocomarca = 
	CameraUpdateFactory.newCameraPosition(camPos);
mapa.animateCamera(centradocomarca);
}

@Override                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
protected void onDestroy() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
	super.onDestroy();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
	finish();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
	}  

public void alertaMarkers(){
	Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
	Typeface fontbold = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
	final Dialog dialog = new Dialog(context);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.setContentView(R.layout.infowindow);

	// set the custom dialog components - text, image and button
	TextView text = (TextView) dialog.findViewById(R.id.tvInfo);
	text.setText(nombreMarcador);
    text.setTypeface(fontbold);

	Button masinfo = (Button) dialog.findViewById(R.id.btnMasInfo);
	masinfo.setTypeface(font);
	Button comollegar = (Button) dialog.findViewById(R.id.btnComoLlegar);
	comollegar.setTypeface(font);
	// if button is clicked, close the custom dialog
	 masinfo.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
	        SQLiteDatabase db = MDB.getWritableDatabase();	        
	        Cursor c = db.rawQuery(
	                "select nombre from mercadillos where nombre='" + nombreMarcador +"'", null);
	            if(c != null && c.moveToFirst()) {
	            	String nombremercado = c.getString(c.getColumnIndex("nombre"));
	            		            	
	            		String mercado = nombremercado;
	            		if(nombremercado.equals(nombremercado)){
	            			Intent intent = new Intent(getApplicationContext(), DetalleMercado.class);
	            			// disable default animation for new intent
	            			 intent.putExtra("Mercado", mercado);
	            			 startActivity(intent);
	            			 finish();
	            				
	            		}else{
	            			Toast toast = Toast.makeText(Mapa.this, "No existe m�s informaci�n para este mercado.", Toast.LENGTH_SHORT);
	            	        toast.show();
	            		}
	            }
	            c.close();
	            db.close();
		}
	});
	 comollegar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+slat+","+slon));
	        	startActivity(i);
			}
		});

	dialog.show();
	}
public void centerMapOnMyLocation() {                                                                                                                                                                                                                                                                                                                                                                                                                                           
	Criteria criteria = new Criteria();
    String provider = locationManager.getBestProvider(criteria, false);
    Location lastLoc = locationManager.getLastKnownLocation(provider);
    
	if(lastLoc != null)
	{
   	double lat = lastLoc.getLatitude();
   	double lng = lastLoc.getLongitude();
   	//create LatLng
   	LatLng lastLatLng = new LatLng(lat, lng);
	//create LatLng    
	    
	    CameraPosition camPos = new CameraPosition.Builder()
	    .target(lastLatLng)   //Centramos el mapa en Madrid
	    .zoom(17)         //Establecemos el zoom en 19
	    .bearing(0)      //Establecemos la orientaci�n con el noreste arriba
	    .tilt(70)         //Bajamos el punto de vista de la c�mara 70 grados
	    .build();
	    CameraUpdate miPosicion = 
	    		CameraUpdateFactory.newCameraPosition(camPos);
	    	mapa.animateCamera(miPosicion);
	    	
	    locationManager.requestLocationUpdates(
	       			provider, 30000, 50, this);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
	}else{
		if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {	    	
			alertagps();
	    }else{
	    	Toast toast = Toast.makeText(Mapa.this, "Por favor, espere a recibir se�al GPS.", Toast.LENGTH_SHORT);
	        toast.show();
	    }
	}			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
}
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
    	home(null);
        return true;
    }
    return super.onKeyDown(keyCode, event);
}

class MyInfoWindowAdapter implements InfoWindowAdapter {

    private final View v;

    MyInfoWindowAdapter() {
        v = getLayoutInflater().inflate(R.layout.popupmarkers,
                null);
    }

    @Override
    public View getInfoContents(Marker marker) {
    	Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
    	Typeface fontbold = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
    	String nombrefoto = marker.getTitle(); 
    	String snipe = marker.getSnippet();
    	if(snipe.equals("Mercadillo de Antig�edades")){
 			iconMarker = R.drawable.iconomarkerant;
 		}else if (snipe.equals("Mercado Ecol�gico")){
 			iconMarker = R.drawable.iconomarkereco;
 		}else if (snipe.equals("Mercadillo Generalista")){
 			iconMarker = R.drawable.iconomarkergen;
 		}else if(snipe.equals("Mercado de Artesan�a")){
 			iconMarker =R.drawable.iconomarkerart;
 		}else if(snipe.equals("Otro Mercadillo o Feria")){
 			iconMarker = R.drawable.iconomarkerotros;
 		}else if (snipe.equals("Rastro de Segunda Mano")){
 			iconMarker = R.drawable.iconomarkersecond;
 		}else if (snipe.equals("Mercado de Navidad")){
 			iconMarker = R.drawable.iconomarkernav;
 		}else if (snipe.equals("Mercadillo de Coleccionismo")){
 			iconMarker = R.drawable.iconomarkercol;
 		} else{
 			iconMarker = R.drawable.iconomarcador;
 		}         
        ImageView icon = (ImageView) v.findViewById(R.id.icon); 
    	icon.setImageResource(iconMarker);
        TextView info = (TextView) v.findViewById(R.id.tvInfoMarker);
        info.setTypeface(fontbold);
        info.setText(nombrefoto);
        TextView infosnipe = (TextView) v.findViewById(R.id.tvSnipet);
        infosnipe.setText(snipe);
        infosnipe.setTypeface(font);
        // set some bitmap to the imageview         

        return v;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}

public void markersColeccionista(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Mercadillo de Coleccionismo'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markercol);

                lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);
			    
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerColeccionismo.add(marker);           
  		} while (c.moveToNext());
         
      c.close();
}

public void markersTodos(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               if(tipo.equals("Mercadillo de Antig�edades")){
   	 			iconMarcador = R.drawable.markerant;
   	 		}else if (tipo.equals("Mercado Ecol�gico")){
   	 			iconMarcador = R.drawable.markereco;
   	 		}else if (tipo.equals("Mercadillo Generalista")){
   	 			iconMarcador = R.drawable.markergen;
   	 		}else if(tipo.equals("Mercado de Artesan�a")){
   	 			iconMarcador =R.drawable.markerart;
   	 		}else if(tipo.equals("Otro Mercadillo o Feria")){
   	 			iconMarcador = R.drawable.markerotros;
   	 		}else if (tipo.equals("Rastro de Segunda Mano")){
   	 			iconMarcador = R.drawable.markersecond;
   	 		}else if (tipo.equals("Mercado de Navidad")){
   	 			iconMarcador = R.drawable.markernav;
   	 		}else if (tipo.equals("Mercadillo de Coleccionismo")){
   	 			iconMarcador = R.drawable.markercol;
   	 		} else{
   	 			icono = R.drawable.marcador;
   	 		}                       
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(iconMarcador);

                lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);
			    		     
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerTodos.add(marker);          
  		} while (c.moveToNext());
         
      c.close();
}
public void markersAntiguedades(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Mercadillo de Antig�edades'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markerant);

              lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);

		
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerAntiguedades.add(marker); 
			  	
  		} while (c.moveToNext());
         
      c.close();
}
public void markersEcologico(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Mercado Ecol�gico'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markereco);

              lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);

		
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerEco.add(marker); 
			  	
  		} while (c.moveToNext());
         
      c.close();
}
public void markersArtesania(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Mercado de Artesan�a'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markerart);

              lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);

		
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));								
          markerArtesania.add(marker); 
			  	
  		} while (c.moveToNext());
         
      c.close();
}
public void markersNavidad(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Mercado de Navidad'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markernav);

              lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);

		
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerNavidad.add(marker); 
			  	
  		} while (c.moveToNext());
         
      c.close();
}
public void markersGeneralista(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Mercadillo Generalista'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markergen);

              lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);

		
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerGeneralista.add(marker); 
			  	
  		} while (c.moveToNext());
         
      c.close();
}
public void markersOtros(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Otro Mercadillo o Feria'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markerotros);

              lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);

		
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerOtros.add(marker); 
			  	
  		} while (c.moveToNext());
         
      c.close();
}
public void markersSegunda(){
	AdminSQLiteOpenHelper MDB = new AdminSQLiteOpenHelper(getApplicationContext());
		SQLiteDatabase db = MDB.getWritableDatabase();
  	Cursor c = db.rawQuery(
              "select nombre,tipo,latitud,longitud from mercadillos where tipo='Rastro de Segunda Mano'", null);
  
  	if (c.moveToFirst())
  		do {			
  			final  String nombre = c.getString(c.getColumnIndex("nombre"));
               String tipo = c.getString(c.getColumnIndex("tipo"));
               String latitud = c.getString(c.getColumnIndex("latitud")); 
               String longitud = c.getString(c.getColumnIndex("longitud"));

               
           			BitmapDescriptor pos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
           	        = BitmapDescriptorFactory.fromResource(R.drawable.markersecond);

              lat = Double.parseDouble(latitud);
				log = Double.parseDouble(longitud); 
			    
			    LatLng Foto = new LatLng(lat,log);

		
            Marker marker = mapa.addMarker(new MarkerOptions()
			    .position(Foto)
			    .title(nombre)
			    .icon(pos)
			    .snippet(tipo)
			    .visible(false)
			    .anchor(0.5f, 0.5f));
          markerSegunda.add(marker); 
			  	
  		} while (c.moveToNext());
         
      c.close();
}

public void mostrarElementos(){
    
	if(guardarcolecc==true){
		for (Marker marker : markerColeccionismo) {
  		  double distlat = marker.getPosition().latitude;
  		  double distlng = marker.getPosition().longitude;
  		  locationFoto.setLatitude(distlat);
  		  locationFoto.setLongitude(distlng);
  	      distancia = (double) locationFoto.distanceTo(myLocation);
  	   if(distancia<=radioDistancia){
  		   marker.setVisible(true);
  	   }else{
  		   marker.setVisible(false);
      	    //marker.remove(); <-- works too!
  	   }    
	}}if(guardargene==true){
		for (Marker marker : markerGeneralista) {
	  		  double distlat = marker.getPosition().latitude;
	  		  double distlng = marker.getPosition().longitude;
	  		  locationFoto.setLatitude(distlat);
	  		  locationFoto.setLongitude(distlng);
	  	      distancia = (double) locationFoto.distanceTo(myLocation);
	  	   if(distancia<=radioDistancia){
	  		   marker.setVisible(true);
	  	   }else{
	  		   marker.setVisible(false);
	      	    //marker.remove(); <-- works too!
	  	   }    
	}}if(guardararte==true){
		for (Marker marker : markerArtesania) {
	  		  double distlat = marker.getPosition().latitude;
	  		  double distlng = marker.getPosition().longitude;
	  		  locationFoto.setLatitude(distlat);
	  		  locationFoto.setLongitude(distlng);
	  	      distancia = (double) locationFoto.distanceTo(myLocation);
	  	   if(distancia<=radioDistancia){
	  		   marker.setVisible(true);
	  	   }else{
	  		   marker.setVisible(false);
	      	    //marker.remove(); <-- works too!
	  	   }    
	}}if(guardarnavi==true){
		for (Marker marker : markerNavidad) {
	  		  double distlat = marker.getPosition().latitude;
	  		  double distlng = marker.getPosition().longitude;
	  		  locationFoto.setLatitude(distlat);
	  		  locationFoto.setLongitude(distlng);
	  	      distancia = (double) locationFoto.distanceTo(myLocation);
	  	   if(distancia<=radioDistancia){
	  		   marker.setVisible(true);
	  	   }else{
	  		   marker.setVisible(false);
	      	    //marker.remove(); <-- works too!
	  	   }    
	}}if(guardareco==true){
		for (Marker marker : markerEco) {
	  		  double distlat = marker.getPosition().latitude;
	  		  double distlng = marker.getPosition().longitude;
	  		  locationFoto.setLatitude(distlat);
	  		  locationFoto.setLongitude(distlng);
	  	      distancia = (double) locationFoto.distanceTo(myLocation);
	  	   if(distancia<=radioDistancia){
	  		   marker.setVisible(true);
	  	   }else{
	  		   marker.setVisible(false);
	      	    //marker.remove(); <-- works too!
	  	   }    
	}}if(guardarotros==true){
		for (Marker marker : markerOtros) {
	  		  double distlat = marker.getPosition().latitude;
	  		  double distlng = marker.getPosition().longitude;
	  		  locationFoto.setLatitude(distlat);
	  		  locationFoto.setLongitude(distlng);
	  	      distancia = (double) locationFoto.distanceTo(myLocation);
	  	   if(distancia<=radioDistancia){
	  		   marker.setVisible(true);
	  	   }else{
	  		   marker.setVisible(false);
	      	    //marker.remove(); <-- works too!
	  	   }    
	}}if(guardarsegunda==true){
		for (Marker marker : markerSegunda) {
	  		  double distlat = marker.getPosition().latitude;
	  		  double distlng = marker.getPosition().longitude;
	  		  locationFoto.setLatitude(distlat);
	  		  locationFoto.setLongitude(distlng);
	  	      distancia = (double) locationFoto.distanceTo(myLocation);
	  	   if(distancia<=radioDistancia){
	  		   marker.setVisible(true);
	  	   }else{
	  		   marker.setVisible(false);
	      	    //marker.remove(); <-- works too!
	  	   }    
	}}if(guardarantig==true){
		for (Marker marker : markerAntiguedades) {
	  		  double distlat = marker.getPosition().latitude;
	  		  double distlng = marker.getPosition().longitude;
	  		  locationFoto.setLatitude(distlat);
	  		  locationFoto.setLongitude(distlng);
	  	      distancia = (double) locationFoto.distanceTo(myLocation);
	  	   if(distancia<=radioDistancia){
	  		   marker.setVisible(true);
	  	   }else{
	  		   marker.setVisible(false);
	      	    //marker.remove(); <-- works too!
	  	   }    
	}
}
}
}


