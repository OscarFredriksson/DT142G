package miun.dt142g.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadWeatherData();

        final Button button = findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                loadWeatherData();
            }
        });



    }

    public void loadWeatherData()
    {
        String urlString = "https://api.met.no/weatherapi/locationforecast/1.9/?lat=60.10;lon=9.58";

        System.out.println("Sending Request");

        //Instantiate new instance of our class
        new HttpGetRequest(new HttpGetRequest.Response()
        {
            @Override
            public void processFinish(String output) {

                extractWeatherData(output);
            }
        }).execute(urlString);
    }


    public void extractWeatherData(String data)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
            Document doc = builder.parse(is);
            is.close();


            extractTemperatureData(doc);
            extractWindData(doc);
            extractCloudinessData(doc);
            extractPrecipitationData(doc);



            //System.out.println("TEMP: " + temp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void extractTemperatureData(Document doc)
    {
        /*Element element = doc.getElementById("TTT");
        String temp = element.getAttribute("value");*/

        String temp = doc.getElementsByTagName("temperature").item(0).getAttributes().getNamedItem("value").getTextContent();

        TextView label = findViewById(R.id.temperatureValueLabel);
        label.setText(temp + "°C");
    }

    public void extractWindData(Document doc)
    {
        /*String speed = doc.getElementById("ff").getAttribute("mps");
        String direction = doc.getElementById("dd").getAttribute("name");*/

        String speed = doc.getElementsByTagName("windSpeed").item(0).getAttributes().getNamedItem("mps").getTextContent();
        String direction = doc.getElementsByTagName("windDirection").item(0).getAttributes().getNamedItem("name").getTextContent();

        TextView label = findViewById(R.id.windSpeedValueLabel);
        label.setText(speed + "mps towards " + direction);
    }

    public void extractCloudinessData(Document doc)
    {
        //String percent = doc.getElementById("NN").getAttribute("percent");

        String percent = doc.getElementsByTagName("cloudiness").item(0).getAttributes().getNamedItem("percent").getTextContent();

        TextView label = findViewById(R.id.cloudinessValueLabel);
        label.setText(percent + "%");
    }

    public void extractPrecipitationData(Document doc)
    {
        NamedNodeMap nodeMap = doc.getElementsByTagName("precipitation").item(0).getAttributes();
        String minValue = nodeMap.getNamedItem("minvalue").getTextContent();
        String maxValue = nodeMap.getNamedItem("maxvalue").getTextContent();

        TextView label = findViewById(R.id.precipitationValueLabel);
        label.setText(minValue + " mm and " + maxValue + " mm");
    }
}