package studio.da.gpshelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setText();
    }

    public void setText()
    {
        TextView mText = (TextView) findViewById(R.id.mText);
        mText.setText("asdf");

        GPSHelper gh = GPSHelper.getInstance();
        gh.RegisterUserGpsLocation("XIAOMING",1.5,3.3);
        gh.RegisterUserGpsLocation("xiaoli",1.51,3.32);
        gh.RegisterUserGpsLocation("lala",1.6,4.02);

        ArrayList<String> response = new ArrayList<String>();
        response = gh.GetPeopleNearbyByUserId("xiaoli",99999);

        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<response.size();i++)
        {
            sb.append(response.get(i));
        }

        mText.setText(sb.toString());

    }

}
