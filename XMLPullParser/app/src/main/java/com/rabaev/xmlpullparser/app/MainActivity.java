package com.rabaev.xmlpullparser.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView ecbList;
    TextView messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ecbList = (ListView) findViewById(R.id.ecbListView);
        messageText = (TextView)findViewById(R.id.messageText);

        ecbList.setAdapter(getAdapter());

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayAdapter<String> getAdapter(){
        ArrayList<String> list = new ArrayList<String>();

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(getUrlData("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")));
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                if (parser.getEventType() == XmlPullParser.START_TAG
                        && parser.getName().equals("Cube")) {
                    list.add(parser.getAttributeValue(0) + " = "
                            + parser.getAttributeValue(1) + "\n");
                }

                parser.next();
            }

        }catch (Throwable exc){
            ecbList = null;
            messageText.setText(exc.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);

        return  adapter;
    }

    public InputStream getUrlData(String urlString)throws URISyntaxException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(String.valueOf(new URL(urlString)));
        HttpResponse res = client.execute(method);
        return res.getEntity().getContent();
    }


}
