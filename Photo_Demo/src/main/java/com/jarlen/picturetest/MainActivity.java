package com.jarlen.picturetest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jarlen.temptest.FrameFilterActivity;
import com.jarlen.utils.Constants;
import com.js.photosdk.operate.OperateUtils;
import com.js.photosdk.utils.FileUtils;

public class MainActivity extends Activity implements OnClickListener
{
	private LinearLayout content_layout;
	private Button addPictureFromPhotoBtn;
	private Button addPictureFromCameraBtn;
	private ImageView pictureShow;
	private Button testBtn;
	private Class<?> intentClass;
	private int intentType = 0;

	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;

	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;

	/* 边框 */
	private static final int PHOTO_FRAME_WITH_DATA = 3024;
	
	/* 马赛克 */
	private static final int PHOTO_MOSAIC_WITH_DATA = 3025;
	
	/* 涂鸦 */
	private static final int PHOTO_DRAW_WITH_DATA = 3026;
	
	/* 剪切 */
	private static final int PHOTO_CROP_WITH_DATA = 3027;
	
	/* 滤镜 */
	private static final int PHOTO_FILTER_WITH_DATA = 3028;
	
	/* 增强 */
	private static final int PHOTO_ENHANCE_WITH_DATA = 3029;
	
	/* 旋转 */
	private static final int PHOTO_REVOLVE_WITH_DATA = 3030;
	
	/* 图像变形 */
	private static final int PHOTO_WARP_WITH_DATA = 3031;
	
	/* 添加水印图片 */
	private static final int PHOTO_ADD_WATERMARK_DATA = 3032;
	/* 添加文字 */
	private static final int PHOTO_ADD_TEXT_DATA = 3033;
	
	/*  测试接口 */
	private static final int PHOTO_TEST_TEXT_DATA = 3034;
	
	/* 照相机拍照得到的图片 */
	private File mCurrentPhotoFile;
	private String photoPath = null, tempPhotoPath, camera_path;

	private int scale = 2;
	int width = 0;

	OperateUtils operateUtils;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		pictureShow = (ImageView) findViewById(R.id.pictureShow);
		testBtn = (Button) findViewById(R.id.testBtn);
		testBtn.setOnClickListener(this);
		content_layout = (LinearLayout) findViewById(R.id.mainLayout);
		addPictureFromPhotoBtn = (Button) findViewById(R.id.addPictureFromPhoto);
		addPictureFromPhotoBtn.setOnClickListener(this);
		addPictureFromCameraBtn = (Button) findViewById(R.id.addPictureFromCamera);
		addPictureFromCameraBtn.setOnClickListener(this);
		operateUtils = new OperateUtils(this);

		if(!PermissionUtil.isGrantAllPermissions(this)){
			PermissionUtil.requestAllPermissions(this);
		}else{
			Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.addPictureFromCamera :
				getPictureFormCamera();
				break;
			case R.id.addPictureFromPhoto :
				getPictureFromPhoto();
				break;
			case R.id.testBtn :
				if (photoPath == null)
				{
					Toast.makeText(MainActivity.this, "请选择图片",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (intentClass == null)
				{
					Toast.makeText(MainActivity.this, "请图片操作类型",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 将图片路径photoPath传到所要调试的模块
				Intent photoFrameIntent = new Intent(MainActivity.this,
						intentClass);
				photoFrameIntent.putExtra("camera_path", camera_path);
				MainActivity.this.startActivityForResult(photoFrameIntent,
						intentType);
				break;
			default :
				break;
		}

	}

	/* 从相册中获取照片 */
	private void getPictureFromPhoto()
	{
		Intent intent1 = new Intent(Intent.ACTION_PICK);
		intent1.setType("image/*");

		//Intent intent1 = new Intent();
		//intent1.setType("image/*");
		//intent1.setAction(Intent.ACTION_GET_CONTENT);
		//intent1.addCategory(Intent.CATEGORY_OPENABLE);

		startActivityForResult(intent1, PHOTO_PICKED_WITH_DATA);
	}

	/* 从相机中获取照片 */
	private void getPictureFormCamera()
	{
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		tempPhotoPath = FileUtils.DCIMCamera_PATH + FileUtils.getNewFileName()
				+ ".jpg";

		mCurrentPhotoFile = new File(tempPhotoPath);

		if (!mCurrentPhotoFile.exists())
		{
			try
			{
				mCurrentPhotoFile.createNewFile();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(mCurrentPhotoFile));
		startActivityForResult(intent, CAMERA_WITH_DATA);
	}
	private void compressed()
	{
		Bitmap resizeBmp = operateUtils.compressionFiller(photoPath,
				content_layout);
		pictureShow.setImageBitmap(resizeBmp);
		camera_path = SaveBitmap(resizeBmp, "saveTemp");
	}
	final Handler myHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 1)
			{
				if (content_layout.getWidth() != 0)
				{
					Log.i("LinearLayoutW", content_layout.getWidth() + "");
					Log.i("LinearLayoutH", content_layout.getHeight() + "");
					// 取消定时器
					timer.cancel();
					compressed();
				}
			}
		}
	};
	Timer timer = new Timer();
	TimerTask task = new TimerTask()
	{
		public void run()
		{
			Message message = new Message();
			message.what = 1;
			myHandler.sendMessage(message);
		}
	};
	// 将生成的图片保存到内存中
	public String SaveBitmap(Bitmap bitmap, String name)
	{
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			File dir = new File(Constants.filePath);
			if (!dir.exists())
				dir.mkdir();
			File file = new File(Constants.filePath + name + ".jpg");
			FileOutputStream out;
			try
			{
				out = new FileOutputStream(file);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
				{
					out.flush();
					out.close();
				}
				return file.getAbsolutePath();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (resultCode != RESULT_OK)
		{
			return;
		}

		switch (requestCode)
		{
			case CAMERA_WITH_DATA :

				photoPath = tempPhotoPath;
				if (content_layout.getWidth() == 0)
				{
					timer.schedule(task, 10, 1000);
				} else
				{
					compressed();
				}

				break;

			case PHOTO_PICKED_WITH_DATA :
				try {
					Uri originalUri = data.getData();
					Log.d("jarlen", "originalUri = " + originalUri);


					String[] filePathColumn = {MediaColumns.DATA};
					Cursor cursor = MainActivity.this.getContentResolver().query(
							originalUri, filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					photoPath = cursor.getString(columnIndex);
					// 延迟每次延迟10 毫秒 隔1秒执行一次
					if (content_layout.getWidth() == 0) {
						timer.schedule(task, 10, 1000);
					} else {
						compressed();
					}
				}catch (Exception e){
					Log.e("jarlen", "PHOTO_PICKED_WITH_DATA exception "+e);
				}

				break;
			case PHOTO_FRAME_WITH_DATA :
			case PHOTO_MOSAIC_WITH_DATA:
			case PHOTO_DRAW_WITH_DATA:
			case PHOTO_CROP_WITH_DATA:
			case PHOTO_FILTER_WITH_DATA:
			case PHOTO_ENHANCE_WITH_DATA:
			case PHOTO_REVOLVE_WITH_DATA:
			case PHOTO_WARP_WITH_DATA:
			case PHOTO_ADD_WATERMARK_DATA:
			case PHOTO_ADD_TEXT_DATA:
			case PHOTO_TEST_TEXT_DATA:
				
				String resultPath = data.getStringExtra("camera_path");
				Bitmap resultBitmap = BitmapFactory.decodeFile(resultPath);
				pictureShow.setImageBitmap(resultBitmap);
				break;

			default :
				break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 1, 1, "滤镜");
		menu.add(0, 2, 2, "人体变形");
		menu.add(0, 3, 3, "边框");
		menu.add(0, 4, 4, "涂鸦");
		menu.add(0, 5, 5, "马赛克");
		menu.add(0, 6, 6, "剪切");
		menu.add(0, 7, 7, "添加水印");
		menu.add(0, 8, 9, "图像增强");
		menu.add(0, 9, 10, "旋转");
		menu.add(0, 10, 8, "添加文字");
		menu.add(0, 11, 11, "测试");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (photoPath == null)
		{
			Toast.makeText(MainActivity.this, "请选择图片", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		switch (item.getItemId())
		{
			case 1 :
				intentClass = ImageFilterActivity.class;
				intentType = PHOTO_FILTER_WITH_DATA;
				break;
			case 2 :
				intentClass = WarpActivity.class;
				intentType = PHOTO_WARP_WITH_DATA;
				break;
			case 3 :
				intentClass = PhotoFrameActivity.class;
				intentType = PHOTO_FRAME_WITH_DATA;
				break;
			case 4 :
				intentClass = DrawBaseActivity.class;
				intentType = PHOTO_DRAW_WITH_DATA;
				break;
			case 5 :
				intentClass = MosaicActivity.class;
				intentType = PHOTO_MOSAIC_WITH_DATA;
				break;
			case 6 :
				intentClass = ImageCropActivity.class;
				intentType = PHOTO_CROP_WITH_DATA;
				break;
			case 7 :
				intentClass = AddWatermarkActivity.class;
				intentType = PHOTO_ADD_WATERMARK_DATA;
				break;
			case 8 :
				intentClass = EnhanceActivity.class;
				intentType = PHOTO_ENHANCE_WITH_DATA;
				break;
			case 9 :
				intentClass = RevolveActivity.class;
				intentType = PHOTO_REVOLVE_WITH_DATA;
				break;
			case 10:
				intentClass = AddTextActivity.class;
				intentType = PHOTO_ADD_TEXT_DATA;
				break;
			default :
				intentClass = FrameFilterActivity.class;
				intentType = PHOTO_TEST_TEXT_DATA;
				break;
		}
		Toast.makeText(MainActivity.this, "请点击测试按钮", Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}

}
