package com.ios7.vibeify;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.Window; // Added import for Window
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnAdapterChangeListener;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import androidx.core.view.WindowCompat;
import androidx.core.graphics.Insets;

import com.google.android.material.navigation.NavigationView;
import com.ios7.vibeify.MyClasses.EzTimer;
import com.ios7.vibeify.R;

public class MainActivity extends AppCompatActivity {

	private LinearLayout linear1;
	private ViewPager viewpager1;
	private LinearLayout bottombarroot;
	private LinearLayout linear2;
	private TextView textview1;
	private LinearLayout linear4;
	private TextView button1;
	private TextView button2;
	// private View tab1bg;
	private BottomNavigationView bottom_nav;
	private NavigationView navview1;
	private String navDetector;

	private PageLoaderInitFragmentAdapter pageLoaderInit;
	private SharedPreferences config;

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		// WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
		setContentView(R.layout.main);

		Window window = getWindow();
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundviolent));
		window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundviolent));

		initialize(_savedInstanceState);
		initializeLogic();
		// checkInternetOrCrash();
	}

	public void checkInternetOrCrash() {
		new Thread(() -> {
			try {
				URL url = new URL("https://github.com");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(3000); // 3 seconds timeout
				connection.setReadTimeout(3000);
				connection.setRequestMethod("HEAD");
				connection.connect();

				int responseCode = connection.getResponseCode();
				if (responseCode != 200) {
					throw new RuntimeException("GitHub unreachable - terminating by design.");
				}

			} catch (IOException e) {
				throw new RuntimeException("No internet - terminating by design.", e);
			}
		}).start();
	}



	private void initialize(Bundle _savedInstanceState) {

	View root = findViewById(android.R.id.content);

		ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			// Apply insets to our own colored views so their backgrounds fill the bar areas.
			// linear1 (top bar, backgroundviolent) absorbs the status bar height via top padding.
			// bottombarroot (bottom bar, backgroundviolent) absorbs the nav bar height via bottom padding.
			// This avoids exposing the transparent system FrameLayout behind the bars.
			View topBar = findViewById(R.id.linear1);
			View bottomBar = findViewById(R.id.bottombarroot);
			if (topBar != null) {
				topBar.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
			}
			if (bottomBar != null) {
				bottomBar.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
			}
			return WindowInsetsCompat.CONSUMED;
		});


		linear1 = findViewById(R.id.linear1);
		viewpager1 = findViewById(R.id.viewpager1);
		bottombarroot = findViewById(R.id.bottombarroot);
		linear2 = findViewById(R.id.linear2);
		textview1 = findViewById(R.id.textview1);
		linear4 = findViewById(R.id.linear4);
		 button1 = findViewById(R.id.button1);
		 button2 = findViewById(R.id.button2);
		 linear4.setVisibility(View.GONE);
		 try {
			 bottom_nav = findViewById(R.id.bottomnav1);
			 bottom_nav.getMenu().clear();
			 bottom_nav.inflateMenu(R.menu.bottomnav_java);
			 bottom_nav.setVisibility(View.GONE);
			 bottom_nav.setVisibility(View.VISIBLE);
			 navDetector = "1";
		 } catch (Exception a) {
			 try {
				 Log.d("DEBUG", "Bottom nav not found, possible a large screen device?");
				 Log.d("DEBUG", "Trying the tablet oriented navview1");
				 navview1 = findViewById(R.id.navview1);
				 navview1.getMenu().clear();
				 navview1.inflateMenu(R.menu.bottomnav_java);
				 navview1.setVisibility(View.GONE);
				 navview1.setVisibility(View.VISIBLE);
				 navDetector = "2";
			 } catch (Exception e) {
				 Log.d("DEBUG", "Bottom nav not found, possible a large screen device?");
				 Log.d("DEBUG", "Defaulting to the old bottom navigation view and disabling all the new code");
				 navDetector = "0";
			 }
		 }
		 button1.setBackgroundResource(R.drawable.activetab);
		 button2.setBackgroundResource(R.drawable.roundedbgviolent);
		pageLoaderInit = new PageLoaderInitFragmentAdapter(getApplicationContext(), getSupportFragmentManager());
		config = getSharedPreferences("config", Activity.MODE_PRIVATE);


			button1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					viewpager1.setCurrentItem((int)0);
				}
			});

		 	button2.setOnClickListener(new View.OnClickListener() {
		 		@Override
				public void onClick(View _view) {
				viewpager1.setCurrentItem((int)1);
				}
		 	});
		 }

		 @Override
		 public void onBackPressed() {
			 config.edit().putString("backSignal", "1").apply();

			 if (config.getString("currenttab", "").equals("1")) {
				 new Handler().postDelayed(new Runnable() {
					 @Override
					 public void run() {
						 viewpager1.setCurrentItem(0);  
					 }
				 }, 150); 

			 } else {
				 if (config.getString("fragmentCanExit", "").equals("0")) {
					 // Do nothing
				 } else {
					 super.onBackPressed();
					 finish();
				 }
			 }
		 }


	private void initializeLogic() {
		config.edit().putString("repo", "https://ihs.ios7.xyz/wallify-api/categories.json").apply();
		config.edit().putString("repo", "https://raw.githubusercontent.com/j1459863h/wallify-walls/refs/heads/main/").apply();
		config.edit().putString("categories", "1").apply();
		config.edit().putString("directrepo", "https://altdisk.eimaen.pw/api/download/a69b5e5031f23e06cd1af7f885de5c0c/anime.json").apply();
		if (config.getString("timeout", "").equals("")) {
			config.edit().putString("timeout", "5000").apply();
		}
		String setupFlag = config.getString("setupcomplete", "");
		Log.d("DEBUG", "setupcomplete read: " + setupFlag);

		if (setupFlag.equals("")) {
			Log.d("DEBUG", "Launching setup activity");
			startActivity(new Intent(MainActivity.this, SetupActivity1.class));
		}
		if (config.getString("colorextraction", "").equals("")) {
			config.edit().putString("colorextraction", "1").apply();
		}
		if (config.getString("disableanims", "").equals("")) {
			config.edit().putString("disableanims", "0").apply();
		}
		if (config.getString("disableblur", "").equals("")) {
			config.edit().putString("disableblur", "0").apply();
		}


		if (config.getString("forcedDebug", "").equals("1")) {
			config.edit().putString("debugMode", "1").apply();
		} else {
			config.edit().putString("debugMode", "0").apply();
		}

		if (android.os.Debug.isDebuggerConnected()) {
			config.edit().putString("debugMode", "1").apply();
			textview1.setText("WALLIFY");
			config.edit().putString("disableanims", "1").apply();
			config.edit().putString("disableblur", "1").apply();
		} else if (config.getString("forcedDebug", "").equals("1")) {
			config.edit().putString("debugMode", "1").apply();
			textview1.setText("DEBUGGER NOT ATTACHED!");
			config.edit().putString("disableanims", "1").apply();
			config.edit().putString("disableblur", "1").apply();
		} else {
			config.edit().putString("debugMode", "0").apply();
		}

		config.edit().putString("debugMode", "0").apply();
		config.edit().putString("disableanims", "0").apply();
		config.edit().putString("disableblur", "0").apply();
		config.edit().putString("colorextraction", "1").apply();
		textview1.setText(R.string.app_name);



		Log.d("MANDEBUG", "Forceddebug state:"+config.getString("forcedDebug", ""));
		Log.d("MANDEBUG", "DebugMode state:"+config.getString("debugMode", ""));

		if (config.getString("disableblur", "").equals("")) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { 
				config.edit().putString("disableblur", "0").apply();
			} else {
				config.edit().putString("disableblur", "1").apply();
			}
		} else {
			// Nothing
		}
		pageLoaderInit.setTabCount(2);
		viewpager1.setAdapter(pageLoaderInit);
		ViewPager viewPager = findViewById(R.id.viewpager1);
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					button1.setBackgroundResource(R.drawable.activetab);
					button2.setBackgroundResource(R.drawable.roundedbgviolent);
					config.edit().putString("currenttab", "0").apply();
					try {
						bottom_nav.setSelectedItemId(R.id.page_1);
					} catch (Exception e) {
						Log.d("DEBUG", "Bottom nav not found, possible a large screen device?");
						Log.d("DEBUG", "Defaulting to the old bottom navigation view and disabling all the new code");
					}
				}
				if (position == 1) {
					button2.setBackgroundResource(R.drawable.activetab);
					button1.setBackgroundResource(R.drawable.roundedbgviolent);
					config.edit().putString("currenttab", "1").apply();
					try {
						bottom_nav.setSelectedItemId(R.id.page_2);
					} catch (Exception e) {
						Log.d("DEBUG", "Bottom nav not found, possible a large screen device?");
						Log.d("DEBUG", "Defaulting to the old bottom navigation view and disabling all the new code");
					}
				}

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		if (navDetector.equals("1")) { 
			linear4.setVisibility(View.GONE);

			bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					int id = item.getItemId();
					if (id == R.id.page_1) {
						viewpager1.setCurrentItem(0);
						return true;
					} else if (id == R.id.page_2) {
						viewpager1.setCurrentItem(1);
						return true;
					} else {
						return false;
					}
				}
			});
			int activeColor = ContextCompat.getColor(this, R.color.activetab);
			ColorStateList colorStateList = ColorStateList.valueOf(activeColor);
			bottom_nav.setItemActiveIndicatorColor(colorStateList);


		} else if (navDetector.equals("2")) { 
			linear4.setVisibility(View.GONE);
			navview1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					int id = item.getItemId();
					if (id == R.id.page_1) {
						viewpager1.setCurrentItem(0);
						return true;
					} else if (id == R.id.page_2) {
						viewpager1.setCurrentItem(1);
						return true;
					}
					return false;
				}
			});
		} else {
			{
				try {
				} catch (Exception e) {
					Log.d("DEBUG", "Bottom nav not found, possible a large screen device?");
					Log.d("DEBUG", "Defaulting to the old bottom navigation view and disabling all the new code");
					linear4.setVisibility(View.VISIBLE);
				}
			}
		}

		Log.d("DEBUG", "NavView current style is" + navDetector);


		// Window window = getWindow();

		// clear FLAG_TRANSLUCENT_STATUS flag:
		// window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
		// window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

		// finally change the color
		// window.setStatusBarColor(ContextCompat.getColor(this,R.color.backgroundviolent));
		viewpager1.setClipToOutline(true);
	}

	public class PageLoaderInitFragmentAdapter extends FragmentStatePagerAdapter {
		Context context;
		int tabCount;

		public PageLoaderInitFragmentAdapter(Context context, FragmentManager manager) {
			super(manager);
			this.context = context;
		}

		public void setTabCount(int tabCount) {
			this.tabCount = tabCount;
		}

		@Override
		public int getCount() {
			return tabCount;
		}

		@Override
		public CharSequence getPageTitle(int _position) {

			return null;
		}

		@Override
		public Fragment getItem(int _position) {
			if (_position == 0) {
				return new WallpapersFragmentActivity();
			}
			if (_position == 1) {
				return new SettingsDialogFragmentActivity();
			}
			return null;
		}
	}


	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}

	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}

	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}

	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}

	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
				_result.add((double) _arr.keyAt(_iIdx));
		}
		return _result;
	}

	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}

	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}

}
