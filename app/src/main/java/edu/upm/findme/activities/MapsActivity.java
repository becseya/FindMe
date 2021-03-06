package edu.upm.findme.activities;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.databinding.ActivityMapsBinding;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.MenuManager;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, App.MortalObserver {

    App app;
    MenuManager menuManager;
    Map<Integer, Marker> markers = new HashMap<>();
    boolean firstMarker = true;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        app = ((App) getApplicationContext()).initWithObserver(this);
        menuManager = new MenuManager(this, app);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.mqtt.setLocationUpdates(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuManager.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        menuManager.onGlobalEvent(e);
        if (e == AppEvent.Type.LOCATION_DATABASE_CHANGED)
            updateAllMarker();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        app.mqtt.setLocationUpdates(true);
    }

    void updateAllMarker() {
        Map<Integer, Location> locations = app.mqtt.getLocations();

        // update markers
        for (Map.Entry<Integer, Location> entry : locations.entrySet()) {
            int userId = entry.getKey();
            LatLng latLng = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());

            if (markers.containsKey(userId))
                markers.get(userId).setPosition(latLng);
            else {
                // skip other users in singe user mode
                if ((app.singleUserOnMap != 0) && (app.singleUserOnMap != userId))
                    continue;

                String displayName = "";
                User u = User.getById(app.users, userId);

                if (u != null)
                    displayName = u.getName();

                Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(displayName));

                if (userId == app.userInfo.getUserId())
                    newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                markers.put(userId, newMarker);

                if (firstMarker) {
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(13.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    firstMarker = false;
                }
            }
        }

        // remove markers which no longer online
        Iterator<Map.Entry<Integer, Marker>> iterator = markers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Marker> entry = iterator.next();
            int userId = entry.getKey();

            if (!locations.containsKey(userId)) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }
}