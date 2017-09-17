package nikam.com.json_application;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView cityList;
   // AutoCompleteTextView cityid;
    Button load;

    ProgressDialog progressDialog;
    String response,myJSON;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityList = (AutoCompleteTextView)findViewById(R.id.citylist);
        load = (Button)findViewById(R.id.btn_load);

      load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

    }


    public void getData()
    {
        class GetDataJSON extends AsyncTask<String, String, String>
        {
            @Override
            protected void onPreExecute()
            {
                progressDialog = ProgressDialog.show(MainActivity.this, "", "Please Wait...", true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params)
            {
                try
                {

                    String url="http://alfaptop.safegird.com/owner/customerapi/getallcity";
                    Log.i("url",""+url);

                    URL url_bookinglist=new URL(url);
                    HttpURLConnection urlConnection =(HttpURLConnection)url_bookinglist.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    int responce_code=urlConnection.getResponseCode();
                    if (responce_code==HttpURLConnection.HTTP_OK)
                    {
                        String line="";
                        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                        while ((line = bufferedReader.readLine())!=null)
                        {
                            response="";
                            response +=line;
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJSON = result;
                Log.i("myJSON",myJSON);
                showbooking();
                progressDialog.dismiss();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }




    public void showbooking()
    {
        try
        {
            ArrayList<String> citylist = new ArrayList<String>();
            JSONArray jsonArray = new JSONArray(myJSON);
            Log.i("json", "" + jsonArray);

            JSONObject jsonObject=jsonArray.getJSONObject(0);
            String responce_code=jsonObject.getString("responseCode");
            String responceMassage=jsonObject.getString("responseMessage");

            Log.i("responce_code",responce_code);
            Log.i("responceMassage",responceMassage);

            JSONArray res_Data = jsonObject.getJSONArray("responseData");
            Log.i("res_Data",res_Data+"");
            for(int i=0;i< res_Data.length();i++){
                JSONObject cityobj = res_Data.getJSONObject(i);

                String cityname = cityobj.getString("ctname");
                Log.i("cityname",cityname+"");
                citylist.add(cityname);


              /*  String cityid = cityobj.getString("cityId");
                Log.i("cityid",cityid+"");
                citylist.add(cityid);*/
            }

            //Creating the instance of ArrayAdapter containing list of fruit names
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.select_dialog_item, citylist);
            //Getting the instance of AutoCompleteTextView
            cityList.setThreshold(1);//will start working from first character
            cityList.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView


        }
        catch (JSONException e)
        {
            Log.i("JSONException",e+"");
            e.printStackTrace();
        }
    }
}

