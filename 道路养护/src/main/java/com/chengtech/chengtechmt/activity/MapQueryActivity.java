package com.chengtech.chengtechmt.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengtech.chengtechmt.R;
import com.chengtech.chengtechmt.entity.MapEntity;
import com.chengtech.chengtechmt.fragment.GISMenuDialogFragment;
import com.chengtech.chengtechmt.util.MyConstants;

public class MapQueryActivity extends Activity implements GISMenuDialogFragment.ExchangeDataListener{
	private RelativeLayout btnBack;
	private TextView txtTitle;
	private WebView webView;
	private RelativeLayout right_tv;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		btnBack = (RelativeLayout) findViewById(R.id.left_area);
		right_tv = (RelativeLayout) findViewById(R.id.right_area);
		txtTitle = (TextView) findViewById(R.id.titleTxt);
		txtTitle.setText("地图查询定位");
		webView = (WebView)findViewById(R.id.web);

		//获取参数
		Intent intent = getIntent();
//		//基础数据查询功能中的定位，传入layer、code变量
//		String layer = intent.getStringExtra("layer");
//		String code = intent.getStringExtra("code");
//
//		//日常巡查采集功能中绘制图像，draw变量传入字符串"1"
//		String draw = intent.getStringExtra("draw");
//
//		//病害数据查询功能中进行定位，传入poi变量
//		String poi = intent.getStringExtra("poi");
		//基础数据查询功能中的定位，传入layer、code变量
		MapEntity mapEntity = (MapEntity) intent.getSerializableExtra("map");
		String layer = mapEntity==null?null:mapEntity.layer;
		String code = mapEntity==null?null:mapEntity.code;

		//日常巡查采集功能中绘制图像，draw变量传入字符串"1"
		String draw = mapEntity==null?null:mapEntity.draw;

		//病害数据查询功能中进行定位，传入poi变量
		String poi = mapEntity==null?null:mapEntity.poi;

		//路段查询参数
		String lineData = mapEntity==null?null:mapEntity.lineData;

		url= MyConstants.PRE_URL+"map/appgis/query.html";

		if(layer != null && layer.length()>0)
		{
			url +="?layer=" + layer +"&code="+code;
		}
		else if(draw !=null && draw.length()>0)
		{
			url +="?draw=1";
		}
		else if(poi !=null && poi.length()>0)
		{
			url +="?poi=" + poi;
		}

		/*
		* 路段查询，直接在URL中传入data参数，
		* 或者页面加载完成后调用js方法： function queryPath(data)
		*/
		if(lineData!=null && lineData.length()>0)
		{
			if(url.indexOf("?")<0)
			{
				url +="?data="+lineData;
			}else {
				url += "&data=" + lineData;
			}
		}

		WebSettings ws = webView.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setAppCacheEnabled(true);
		//让js能调用这里的方法
		webView.addJavascriptInterface(this, "javato");

		webView.loadUrl(url);


		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		right_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GISMenuDialogFragment dialogFragment = new GISMenuDialogFragment();
				dialogFragment.show(getFragmentManager(),"GIS");
				String exp = "2233";
//		webView.loadUrl("javascript:queryPath("+exp+")");
//		webView.loadUrl("javascript:alert(123)");
//		String json = "[{\"name\":\"zxx\", \"amount\":\"9999999\", \"phone\":\"18600012345\"}]";
//		// 调用JS中的方法
//		webView.loadUrl("javascript:show('" + json + "')");
			}
		});
	}

	/*
	 * 地图页面绘制完成后，js调用这个方法，在这里得到绘制图形的json字符串，保存起来。
	 * 后面定位的时候poi参数就是传入这个json
	 */
	@JavascriptInterface
	public void OnDrawFinish(String json)
	{
		System.out.println("JS回调:");
		Log.i("tag",json);
	}

	@JavascriptInterface
	public void OnOpenURL(String url) {

	}



	@Override
	public void onExchangData(Object object) {
		if (object!=null) {
			String mapdata = (String) object;
			webView.loadUrl("javascript:queryPath('"+mapdata+"')");
		}
	}


}
