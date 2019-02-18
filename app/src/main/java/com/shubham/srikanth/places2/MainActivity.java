package com.shubham.srikanth.places2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 545;
    private PlacesClient placesClient;
    private static final int REQUEST_CODE=254;
    private String apiKey;
    private String placeId = "ChIJdy6n09pY7TkRKMq-E4Ej8Xc";
//    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        image=findViewById(R.id.image);
        apiKey = getString(R.string.api_key);
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);
        autocompleteWidgetByActivity();

    }

    private void autocompleteWidgetByActivity(){

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "YOUR_API_KEY");
        }

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }
//    private void autoCompleteWidget(){
//
//
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), apiKey);
//        }
//
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.i("TAG", "An error occurred: " + status);
//            }
//        });
//    }
    private void autoComplete(){

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-90, -180),
                new LatLng(90, 180));
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery("nit pat")
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i("TAG", prediction.getPlaceId());
                    Log.i("TAG", prediction.getPrimaryText(null).toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    private void findCurrentPlace(){

        List<Place.Field> placeFields=Arrays.asList(Place.Field.NAME);
        final FindCurrentPlaceRequest findCurrentPlaceRequest=FindCurrentPlaceRequest.builder(placeFields).build();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            placesClient.findCurrentPlace(findCurrentPlaceRequest).addOnSuccessListener(
                    new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                    String placeName="";
                    double  likeliHoodValue=0;
                    for(PlaceLikelihood placeLikelihood :findCurrentPlaceResponse.getPlaceLikelihoods()){
                        if(placeLikelihood.getLikelihood()>likeliHoodValue){
                            placeName=placeLikelihood.getPlace().getName();
                            likeliHoodValue=placeLikelihood.getLikelihood();
                        }
                        Log.i("TAG", String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                    Log.i("TAG","highest placelikelihood value : "+placeName+" , value : "+likeliHoodValue );

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if(e instanceof ApiException){
                        ApiException apiException= (ApiException) e;
                        Log.i("TAG","place not found : "+apiException.getStatusCode() );
                    }
                }
            });
        }else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION} ,REQUEST_CODE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                findCurrentPlace();
            }
            else {
                Toast.makeText(this, "permission granted",Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void fetchPlaceById(){
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,Place.Field.NAME);
        final FetchPlaceRequest fetchPlaceRequest=FetchPlaceRequest.builder(placeId,fields ).build();
        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(
                new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {

                Place place=fetchPlaceResponse.getPlace();
                Log.i("TAG","place name :- "+place.getName() );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof ApiException){
                    ApiException apiException= (ApiException) e;
                    int statusCode=apiException.getStatusCode();
                    Log.i("TAG","places not found .status code :- "+statusCode );
                }
            }
        });
    }
    private void fetchPhotoById(){
        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest fetchPlaceRequest=FetchPlaceRequest.builder(placeId,fields ).build();
        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(
                new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {

                        Place place=fetchPlaceResponse.getPlace();
                        PhotoMetadata photoMetadata=place.getPhotoMetadatas().get(0);
                        Log.i("TAG","size of array retrieved : "+place.getPhotoMetadatas().size() );
                        String attributions=photoMetadata.getAttributions();
                        final FetchPhotoRequest fetchPhotoRequest=FetchPhotoRequest.builder(photoMetadata).setMaxHeight(500)
                                .setMaxHeight(500).build();
                        placesClient.fetchPhoto(fetchPhotoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                            @Override
                            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {

                                Bitmap bitmap=fetchPhotoResponse.getBitmap();
//                                image.setImageBitmap(bitmap);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                if(e instanceof ApiException){
                                    ApiException apiException= (ApiException) e;
                                    int statusCode=apiException.getStatusCode();
                                    Log.i("TAG","error in fetching photo ,response code : "+statusCode );
                                }
                            }
                        });
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ApiException apiException= (ApiException) e;
                int statusCode=apiException.getStatusCode();
                Log.i("TAG","error in fetching photo ,response code : "+statusCode );
            }
        });
    }
}
