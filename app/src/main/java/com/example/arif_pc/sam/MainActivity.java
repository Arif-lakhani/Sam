package com.example.arif_pc.sam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.GPS_PROVIDER;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,TextToSpeech.OnInitListener {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private ImageButton buttonSend;
    private ImageButton button;
    private boolean sideright = true;
    private boolean sideleft=false;
    private LocationRequest mLocationRequest;

    private TextToSpeech mTts;
    public static double currentLatitude;
    public String qares;
    public static double currentLongitude;
    protected static final int REQUEST_OK = 5;

    private GoogleApiClient mGoogleApiClient;





        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                        //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();
        setContentView(R.layout.activity_main);
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1

        buttonSend = (ImageButton) findViewById(R.id.send);
        button = (ImageButton)findViewById(R.id.button_voice);
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {




                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(intent, REQUEST_OK);

                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
                }
            }

        });



        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.e(chatText.getText().toString(), "firstchatmessage ater this");

                    return sendChatMessage(sideright,chatText.getText().toString());
                }
                return false;
            }
        });
        mTts = new TextToSpeech(this, this);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String s=null;
                String qares=null;
                String send=chatText.getText().toString();
                Log.d(send, "second chatmessage after thisonClick ");
                sendChatMessage(sideright,chatText.getText().toString());
               // Log.e(TAG, "firstchatmesage ");
             //   Log.e(s, "insideonClick ");

                s = check(send);
                Log.e(s, "onClickaftercheck");
               if (s == "gmail") {
                    s = "gmail is an email client, i can open it for you";
                   sendChatMessage(sideleft,s);

                   speak(s);
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setType("plain/text");
                    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail");
                    startActivity(sendIntent);
                } else if (s == "arif") {
                    s = "arif is my bot master, i will not reveal any further inforamation.";
                    sendChatMessage(sideleft,s);

                    speak(s);
                } else if (s == "alarm") {
                    s = "alarm is a good way of reminding yourself of something, i can open the alarm application";
                    sendChatMessage(sideleft,s);
                    speak(s);
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setType("plain/text");
                    sendIntent.setClassName("com.android.deskclock", "com.android.deskclock.AlarmClock");
                    startActivity(sendIntent);
                } else if (s == "fb") {
                    s = "facebook is a social networking site, i can open facebook app for you";
                    sendChatMessage(sideleft,s);
                    speak(s);
                    Intent launchfb = getPackageManager().getLaunchIntentForPackage("com.facebook.kataba");
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    //          sendIntent.setType("plain/text");
                    //   sendIntent.setClassName("com.facebook.katana", "com.facebook.browser.lite.BrowserLiteActivity");
                    startActivity(launchfb);
                } else if (s == "navi") {
                    s = "maps help with navigation, openeing google maps";
                    sendChatMessage(sideleft,s);
                    speak(s);
                    Intent launchfb = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                    startActivity(launchfb);
                } else if (s == "navi") {
                    s = "maps help with navigation, opening google maps";
                    sendChatMessage(sideleft,s);
                    speak(s);
                    Intent launchfb = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                    startActivity(launchfb);
                } else if (s == "loc") {
                   Log.e(s, "insideloconClick ");
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        s = "here is the current location address:" + address + ",city:" + city + ",state:" + state + ",country:" + country + ".";
                        sendChatMessage(sideleft,s);
                        speak(s);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

               } else {
try{
    Log.e(send, "going to qa ");
    new QAThread(send).start();    Log.e(qares, "onClickafter qa");

    Log.e(TAG, "lastchatmesage ");}
catch (Exception ex){
    ex.printStackTrace();
}

            }}

        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }      @Override
           public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OK) {
            if (data == null) return;
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            chatText.setText(thingsYouSaid.get(0));
        }
    }
    private String check(String s) {
        Log.e(s, "insidecheck ");
        int index = s.indexOf("Arif");int d=s.indexOf("arif");
        if (index != -1||d!=-1) {

            return "arif";
        }
        int index2 = s.indexOf("Gmail");int in=s.indexOf("gmail");
        if (index2 != -1||in!=-1) {
            return "gmail";
        }int index3 = s.indexOf("alarm");int n=s.indexOf("alarm");int n0=s.indexOf("reminders");int n3=s.indexOf("Reminders");
        int n4=s.indexOf("reminder") ;
        if (index3 != -1||n!=-1||n0!=-1||n3!=-1||n4!=-1) {
            return "alarm";
        }
        int index21 = s.indexOf("Facebook");int n2=s.indexOf("facebook");
        if (index21 != -1||n2!=-1) {
            return "fb";}
        int index211 = s.indexOf("Navigation");int n12=s.indexOf("navigation");int q=s.indexOf("maps");
        if (index211 != -1||n12!=-1||q!=-1) {
            return "navi";}
        int index2111 = s.indexOf("Current location");int nq12=s.indexOf("current location");
        if (index2111 != -1||nq12!=-1) {
            return "loc";}
        return  null;


    }

    private boolean sendChatMessage(Boolean side, String s) {
        chatArrayAdapter.add(new ChatMessage(side, s));
        chatText.setText("");
   //     side = !side;
        return true;
    }
QAService qa=new QAService(this,1000000000);
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }




    @Override
    public void onInit(int status) {

        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
                Log.e("404","Language is not available.");
            }
        } else {
            // Initialization failed.
            Log.e("404", "Could not initialize TextToSpeech.");
            // May be its not installed so we prompt it to be installed
            Intent installIntent = new Intent();
            installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        }

    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
         /*
          * Google Play services can resolve some errors it detects.
          * If the error has a resolution, try sending an Intent to
          * start a Google Play services activity that can resolve
          * error.
          */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 9000);
                 /*
                  * Thrown if Google Play services canceled the original
                  * PendingIntent
                  */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
             /*
              * If no resolution is available, display a dialog to the
              * user with the error.
              */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

   class QAThread extends Thread {
        String inputText;

        public QAThread(String txt) {
            inputText = txt;
        }

        String s;

        public void run() {
            if (inputText == null || inputText.length() == 0)
                return;

       //     s = check(inputText);
          qares=  qa.runQA(inputText, getLocation());

          runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    sendChatMessage(sideleft,qares);
                    speak(qares);
                   /* if (s == null) {
                        sendChatMessage(s);
                        speak(s);
                        Log.e(s, "proceding for speech ");
                        Log.e("speech", "run ");
                    } else if (s == "gmail") {
                        s = "gmail is an email client, i can open it for you";
                        sendChatMessage(s);
                        speak(s);
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setType("plain/text");
                        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail");
                        startActivity(sendIntent);
                    } else if (s == "arif") {
                        s = "arif is my bot master, i will not reveal any further inforamation.";
                        sendChatMessage(s);
                        speak(s);
                    } else if (s == "alarm") {
                        s = "alarm is a good way of reminding yourself of something, i can open the alarm application";
                        sendChatMessage(s);
                        speak(s);
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setType("plain/text");
                        sendIntent.setClassName("com.android.deskclock", "com.android.deskclock.AlarmClock");
                        startActivity(sendIntent);
                    } else if (s == "fb") {
                        s = "facebook is a social networking site, i can open facebook app for you";
                        sendChatMessage(s);
                        speak(s);
                        Intent launchfb = getPackageManager().getLaunchIntentForPackage("com.facebook.kataba");
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        //          sendIntent.setType("plain/text");
                        //   sendIntent.setClassName("com.facebook.katana", "com.facebook.browser.lite.BrowserLiteActivity");
                        startActivity(launchfb);
                    } else if (s == "navi") {
                        s = "maps help with navigation, openeing google maps";
                        sendChatMessage(s);
                        speak(s);
                        Intent launchfb = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                        startActivity(launchfb);
                    } else if (s == "navi") {
                        s = "maps help with navigation, opening google maps";
                        sendChatMessage(s);
                        speak(s);
                        Intent launchfb = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                        startActivity(launchfb);
                    } else if (s == "loc") {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            s = "here is the current location address:" + address + ",city:" + city + ",state:" + state + ",country:" + country + ".";
                            sendChatMessage(s);
                            speak(s);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }*/


                }
            });
        }
    }        @Override
        public void onConnected(Bundle bundle) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

             //   Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            }

        }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void speak(String textToSpeak){
     mTts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private String getLocation() {
        String location = null;
        android.location.LocationListener e=null;
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location l = locationManager.getLastKnownLocation(GPS_PROVIDER);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,1000,1000,e);
                location = l.getLatitude() + "," + l.getLongitude();}
        catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
        return location;

    }
}