package co.aquario.socialkit.fragment.newuser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.aquario.chatui.utils.EndpointManager;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.util.PathManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.RoundedTransformation;

public class EditProfileFragment extends BaseFragment {

	Context context;
	Activity act;

	Button signupVdomax;
	EditText txtName;
    EditText txtLastname;
	EditText txtUsername;
	EditText txtEmail;
    EditText txtAbout;
	ImageView avatar;

	String fname;
	String lname;
	String user;
	String pass;
	String email;

	AQuery aq;
	Button updateButton;

    public EditProfileFragment() {

    }

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillProfile(VMApp.mPref.userId().getOr("0"));
    }

    public void checkCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("hahaha", jo.toString(4));

        if(jo.optString("status").equals("1"))
            txtUsername.setError("Username is not available");

        //Utils.showToast("Update complete!");
    }

    LinearLayout btnUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_profile, container, false);

        context = getActivity();
		aq = new AQuery(context);

		avatar = (ImageView) rootView.findViewById(R.id.avatar);
        txtName = (EditText) rootView.findViewById(R.id.firstName);
        txtLastname = (EditText) rootView.findViewById(R.id.lastName);
		txtEmail = (EditText) rootView.findViewById(R.id.emailSignup);
        txtUsername = (EditText) rootView.findViewById(R.id.userName);
        txtAbout = (EditText) rootView.findViewById(R.id.about);

        btnUpdate = (LinearLayout) rootView.findViewById(R.id.btn_update_profile);

        txtUsername.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {   //Convert the Text to String
                String inputText = txtUsername.getText().toString();
                AQuery aq = new AQuery(getActivity());

                String url = "http://api.vdomax.com/user/"+inputText+"/available";
                aq.ajax(url, JSONObject.class, this, "checkCb");

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }
        });

		avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



		//updateButton = (Button) rootView.findViewById(R.id.updateButton);
        btnUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                updateProfile(VMApp.mPref.userId().getOr("0"));
            }
        });

        return rootView;
	}

    private void updateProfile(String userId) {
        VMApp.mPref.name().put(txtName.getText() + " " + txtLastname.getText())
                .username().put(txtUsername.getText().toString())
                .avatar().put("")
                .commit();
    }



	public void fillProfile(String uid) {
        String fullName = VMApp.mPref.name().getOr("");
        String[] parts = fullName.split(" ");
        if(parts[0] != null)
            txtName.setText(parts[0]);
        if(parts[1] != null)
            txtLastname.setText(parts[1]);

        txtUsername.setText(VMApp.mPref.username().getOr(""));
        txtEmail.setText(VMApp.mPref.email().getOr(""));
        Picasso.with(context)
                .load(EndpointManager.getPath(VMApp.mPref.avatar().getOr("")))
                        .error(R.drawable.avatar_default)
                //.centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(avatar);
	}


	public void uploadAvatar() {
		String url = "https://www.vdomax.com/ajax.php?mobile=1&t=avatar&a=new&user_id="+VMApp.mPref.userId().getOr("0")+"&token=123456&user_pass="+VMApp.mPref.password().getOr("");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("timeline_id", VMApp.mPref.userId().getOr("0"));
		params.put("image", tempFile);
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setInverseBackgroundForced(false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle("Uploading");
		aq.progress(dialog).ajax(url, params, JSONObject.class, context,
                "uploadAvatarCb");

	}

    public void uploadAvatarCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {

        if (jo != null) {
            Utils.showToast("Your avatar is uploaded");
            String avatarUrl = jo.optString("avatar_url");
            if(avatarUrl != null)
                Picasso.with(context)

                        .load(avatarUrl)
                                //.error(R.drawable.avatar_default)
                                //.centerCrop()
                        .resize(200, 200)
                        .transform(new RoundedTransformation(100, 4))
                        .into(avatar);


        }
    }

    private void setAvatar(String pathStr) {
        Picasso.with(context)

                .load(pathStr)
                        //.error(R.drawable.avatar_default)
                        //.centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(avatar);
    }

	private void setAvatar(Uri uri) {
        Picasso.with(context)

                .load(uri)
                //.error(R.drawable.avatar_default)
                        //.centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(avatar);
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
	}



	File tempFile;
	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final int REQUEST_CHOOSE_PHOTO = 2;
	private static final int REQUEST_CODE_CROP_IMAGE = 3;

	private static final int PHOTO_SIZE_WIDTH = 200;
	private static final int PHOTO_SIZE_HEIGHT = 200;

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "co.aquario.socialkit.fileprovider";

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");

					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					// intent.putExtra("crop", "true");
					startActivityForResult(intent, REQUEST_TAKE_PHOTO);
				} else if (items[item].equals("Choose from Library")) {
                    //Crop.pickImage(getActivity());
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setDataAndType(
							MediaStore.Images.Media.INTERNAL_CONTENT_URI,
							"image/*");
					intent.setType("image/*");
					// intent.putExtra("crop", "true");
					intent.putExtra("scale", true);
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", PHOTO_SIZE_WIDTH);
					intent.putExtra("outputY", PHOTO_SIZE_HEIGHT);
					startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

    Uri outputCropUri;
    Uri inputCropUri;



	private void startCropImage(Uri uri) {

        Crop.of(uri, outputCropUri).asSquare().start(getActivity());
	}

    private void startCropImage(File file) {

        inputCropUri = Uri.fromFile(file);
        Crop.of(inputCropUri, outputCropUri).asSquare().start(getActivity());
    }


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		// Toast.makeText(context, requestCode + "", Toast.LENGTH_SHORT).show();
		if (resultCode == Activity.RESULT_OK) {
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;

                    break;
                }
            }

            tempFile = f;
            setAvatar(f.getPath());

            if (requestCode == Crop.REQUEST_PICK) {

                uploadAvatar();
            } else
			if (requestCode == REQUEST_TAKE_PHOTO) {

                uploadAvatar();

//                outputCropUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
//                        .toString(), "temp.jpg"));
//
//                startCropImage(f);

//				try {
//					Bitmap bm;
//					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
//
//					tempFile = f;
//					bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
//                            btmapOptions);
//
//					String path = tempFile.getAbsolutePath();
//					// setAvatar(path);
//					startCropImage(result.getData());
//
//					f.delete();
//					OutputStream fOut = null;
//					File file = new File(path);
//					try {
//						fOut = new FileOutputStream(file);
//						bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
//						fOut.flush();
//						fOut.close();
//
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			} else if (requestCode == REQUEST_CHOOSE_PHOTO) {

				Uri selectedImageUri = result.getData();

               tempFile = new File(PathManager.getPath(getActivity(),selectedImageUri));

                uploadAvatar();



				// this one
				//String path = getRealPath(context, selectedImageUri);
//				tempFile = new File(selectedImageUri.getPath());
//
//                outputCropUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
//                        .toString(), "temp.jpg"));
//
//				// setAvatar(path);
//                startCropImage(selectedImageUri);

			} else if (requestCode == Crop.REQUEST_CROP) {
                setAvatar(outputCropUri);

                //handleCrop(resultCode, result);

			}
		}
		super.onActivityResult(requestCode, resultCode, result);

	}

    public static String getRealPath(Context context,Uri mFileURI) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            return getRealPathFromURIForKitKat(context,mFileURI);
        } else {
            return getRealPathFromURI(context, mFileURI);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURIForKitKat(Context context, Uri uri) {
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return getRealPathFromURI(context, uri);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
}
