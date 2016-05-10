package com.feng.switchbutton;

import com.feng.switchbutton.SwitchButton.OnChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SwitchButton sb=(SwitchButton)findViewById(R.id.swith_btn);
		sb.setOnChangeListener(new OnChangeListener() {
			
			@Override
			public void onChange(SwitchButton sb, boolean state) {
	            Toast.makeText(MainActivity.this, state ? "¿ª":"¹Ø", Toast.LENGTH_SHORT).show(); 
			}
		});
	}

}
