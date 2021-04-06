package com.hector.locationmapsite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;

public class SiteActivity extends AppCompatActivity {
    private static final String TAG = "SiteActivity";
    // Declare a SearchService object.
    private SearchService searchService;

    EditText editTextQuery;
    EditText editTextLat;
    EditText editTextLong;
    Button buttonQuery;
    Button buttonCorrdinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_site);

        editTextQuery = findViewById(R.id.editTextQuery);
        editTextLat = findViewById(R.id.editTextLat);
        editTextLong = findViewById(R.id.editTextLong);
        buttonQuery = findViewById(R.id.buttonQuery);
        buttonCorrdinate = findViewById(R.id.buttonCoordinate);

// Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, AGConnectServicesConfig.fromContext(this).getString("client/api_key"));
        buttonQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextQuery.getText().toString();
                if(!query.isEmpty())
                    search(query, null);

            }
        });

        buttonCorrdinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    double latitude = Double.parseDouble(editTextLat.getText().toString());
                    double longitude = Double.parseDouble(editTextLong.getText().toString());
                    Coordinate coordinate = new Coordinate(latitude, longitude);
                    search("", coordinate);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    void showToast(String text){
        Toast.makeText(getApplicationContext(), text , Toast.LENGTH_LONG).show();
    }

    //Site kit
    public void search(String query, Coordinate coordinate) {
        TextSearchRequest textSearchRequest = new TextSearchRequest();
        //textSearchRequest.setQuery();
        if(!query.isEmpty())
            textSearchRequest.setQuery(query);
        if(coordinate != null)
            textSearchRequest.setLocation(coordinate);

        searchService.textSearch(textSearchRequest, new SearchResultListener<TextSearchResponse>() {
            @Override
            public void onSearchResult(TextSearchResponse textSearchResponse) {
                StringBuilder response = new StringBuilder("\n");
                response.append("success\n");
                int count = 1;
                AddressDetail addressDetail;
                if (null != textSearchResponse) {
                    if (null != textSearchResponse.getSites()) {
                        for (Site site : textSearchResponse.getSites()) {
                            addressDetail = site.getAddress();
                            response
                                    .append(String.format("[%s]  name: %s, formatAddress: %s, country: %s, countryCode: %s \r\n",
                                            "" + (count++), site.getName(), site.getFormatAddress(),
                                            (addressDetail == null ? "" : addressDetail.getCountry()),
                                            (addressDetail == null ? "" : addressDetail.getCountryCode())));
                        }
                    } else {
                        response.append("textSearchResponse.getSites() is null!");
                    }
                } else {
                    response.append("textSearchResponse is null!");
                }
                Log.d(TAG, "search result is : " + response);
                showToast(response.toString());
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
                Log.e(TAG, "onSearchError is: " + searchStatus.getErrorCode());
                showToast(searchStatus.getErrorMessage());
            }
        });
    }
}