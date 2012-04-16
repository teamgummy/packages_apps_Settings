
package com.android.settings.util.devcard;

import com.android.settings.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DevCard extends Preference {
    private static final String TAG = "DevCard";
    
    private ImageView twitterButton;
    private ImageView donateButton;
    private ImageView photoView;
    
    private TextView devName;
    
    private Drawable devPic;
    private String nameDev;
    private String twitterName;
    private String donateLink;
    
    public DevCard (Context context, AttributeSet attrs) {
    	super (context, attrs);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DevCard);
        nameDev = a.getString(R.styleable.DevCard_nameDev);
        twitterName = a.getString(R.styleable.DevCard_twitterName);
        donateLink = a.getString(R.styleable.DevCard_donateLink);
        devPic = a.getDrawable(R.styleable.DevCard_devPic);
        a.recycle();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        
        View layout = View.inflate(getContext(), R.layout.dev_card, null);

        twitterButton = (ImageView) layout.findViewById (R.id.twitter_button);
        donateButton = (ImageView) layout.findViewById (R.id.donate_button);
        devName = (TextView) layout.findViewById (R.id.name);
        photoView = (ImageView) layout.findViewById(R.id.photo);
        
        return layout;
    }
    
    @Override
    protected void onBindView(View view) {
    	super.onBindView(view);

    	if (donateLink != null) {
    		final OnClickListener openDonate = new OnClickListener() {
                @Override
                public void onClick(View v) {
                	Uri donateURL = Uri.parse(donateLink);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, donateURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                }
            };	
            
            donateButton.setOnClickListener(openDonate);
    	} else {
    		donateButton.setVisibility(View.GONE);
    	}
        
        if (twitterName != null) {
        	final OnPreferenceClickListener openTwitter = new OnPreferenceClickListener() {
                @Override
                public void onClick(View v) {
                	Uri twitterURL = Uri.parse("http://twitter.com/#!/" + twitterName);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, twitterURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                }
            };
            
            //changed to clicking the preference to open twitter
            //it was a hit or miss to click the twitter bird
            //twitterButton.setOnClickListener(openTwitter);
            this.setOnPreferenceClickListener(openTwitter);
        } else {
        	twitterButton.setVisibility(View.GONE);
        }
        
        if (devPic != null) {
        	photoView.setImageDrawable(devPic);
        } else {
        	int noPic = R.drawable.nodevpic;
        	photoView.setImageResource(noPic);
        }
        
        devName.setText(nameDev);
    	
    }
}