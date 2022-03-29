package com.capstone.pasigsafety.User;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.capstone.pasigsafety.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );


        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .addItem( aboutTitle() )
                .setDescription(" You don't have to be anxious about your safety; this application can help you overcome your wariness about going somewhere!\n" +
                        "\n" +
                        "Pasig Safety Application has a lot of features! When you log in, the application will request GPS permission. To ensure that the application knows your current location, you must click Allow. Then simply click the search button to quickly find your desired location!\n" +
                        "\n" +
                        "You will be taken directly to the main dashboard. A blue circle with a radius of 500m represents your current location on the map. Any crime within that range will be displayed. When you click the crime marker, it will show you the crime rates and how safe your location is.\n" +
                        "\n" +
                        "This application also displays marked crime spots and shows the user's nearest police contact location.\n" +
                        "\n" +
                        "Crime spots can be viewed by clicking the \"Crime Spot\" button.\n" +
                        "\n" +
                        "New crime spots with types of crime and user experience can be added by long pressing and holding on a spot on the map.\n" +
                        "\n" +
                        "The \"Police Contact\" button must be clicked to find the nearest police contact location. This app also shows the path of a police contact location from a given location.")
                .addGroup( "Developer" )
                .addItem( new Element().setTitle( "Carl Jeremiah Dela Cruz" ) )
                .addItem( new Element().setTitle( "Richie Grace Factor" ) )
                .addItem( new Element().setTitle( "Thomas Allyson Jumawan" ) )
                .addItem( new Element().setTitle( "Alex Pornela" ) )
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("CONNECT WITH US!")
                .addEmail("pasigsafety@gmail.com")
                .addGitHub( "99163985" )
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }

    private Element aboutTitle() {

        Element about = new Element();
        about.setTitle( "About Us" );
        about.setGravity( Gravity.CENTER );

        return about;
    }

    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by Pasig Safety", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity( Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(About.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}