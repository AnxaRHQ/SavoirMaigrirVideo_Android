package anxa.com.smvideo.activities.account;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.BaseFragment;
import anxa.com.smvideo.contracts.RecipeContract;
import anxa.com.smvideo.util.RecipeHelper;
import anxa.com.smvideo.util.UITagHandler;

/**
 * Created by aprilanxa on 04/08/2017.
 */

public class RecipeAccountActivity extends BaseFragment implements View.OnClickListener {
    private List<RecipeContract> recipesList;
    private ImageView backButton;
    private Button shareButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.recipe, null);

        ((TextView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_title_tv)).setText(getString(R.string.menu_recette));
        ((Button) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);


        backButton = (ImageView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_menu_back);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this);

        shareButton = (Button) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        shareButton.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)shareButton.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        shareButton.setLayoutParams(params);

        updateUI(ApplicationData.getInstance().selectedRelatedRecipe);
        super.onCreateView(inflater, container, savedInstanceState);
        return mView;
    }

    private void updateUI(RecipeContract recipeContract) {
        ((TextView)mView.findViewById(R.id.recipeTitle)).setText(recipeContract.Title);
        Bitmap avatar = null;
        avatar = RecipeHelper.GetRecipeImage(recipeContract.Id);
        ImageView img = (ImageView) mView.findViewById(R.id.recipeImage);
        //img.setTag(recipeContract.Id);
        if (avatar == null) {

            Glide.with(this).load(recipeContract.ImageUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).into(img);
            try {
                if (!ApplicationData.getInstance().recipePhotoList.containsKey(String.valueOf(recipeContract.Id)) && img.getDrawable() != null) {

                    ApplicationData.getInstance().recipePhotoList.put(String.valueOf(recipeContract.Id), ((GlideBitmapDrawable) img.getDrawable()).getBitmap());
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            mView.findViewById(R.id.recipeImageProgress).setVisibility(View.GONE);
            //new RecipeDownloadImageAsync(img, (ProgressBar) mView.findViewById(R.id.recipeImageProgress), recipeContract.Id).execute(recipeContract.ImageUrl);
        } else {
            ((ImageView) mView.findViewById(R.id.recipeImage)).setImageBitmap(avatar);
            ((ProgressBar) mView.findViewById(R.id.recipeImageProgress)).setVisibility(View.GONE);
        }
        ((TextView) mView.findViewById(R.id.recipeIngredientsTitle)).setText((recipeContract.IngredientsTitle));
        ((TextView) mView.findViewById(R.id.recipeIngredients)).setText(Html.fromHtml(recipeContract.IngredientsHtml, null, new UITagHandler()));
        ((TextView) mView.findViewById(R.id.recipePreparation)).setText(Html.fromHtml(recipeContract.PreparationHtml, null, new UITagHandler()));
    }


    private Bitmap getAvatar(int recipeId) {
        Bitmap avatarBMP = null;
        if (recipeId > 0) {
            avatarBMP = ApplicationData.getInstance().recipePhotoList.get(String.valueOf(recipeId));

            return avatarBMP;
        }
        return avatarBMP;
    }

    @Override
    public void onClick(final View v) {
        if (v==backButton){
            try {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (v==shareButton){
            generatePdf((ScrollView)mView.findViewById(R.id.recipeScrollView));
        }
    }

    private void generatePdf(ViewGroup v)
    {
        if (isStoragePermissionGranted()) {
            try {
                /*v.measure(View.MeasureSpec.makeMeasureSpec(
                        View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                v.layout(0, 0, v.getMeasuredWidth(),
                        v.getMeasuredHeight());
                v.setDrawingCacheEnabled(true);
                v.buildDrawingCache();*/


                PdfDocument document = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    document = new PdfDocument();

                    int pageNumber = 1;
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(v.getChildAt(0).getWidth(),
                            v.getChildAt(0).getHeight(), pageNumber).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    //wb.draw(page.getCanvas());
                    v.draw(page.getCanvas());
                    document.finishPage(page);


                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    //bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String docName = ApplicationData.getInstance().selectedRelatedRecipe.Title + ".pdf";

                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), docName);
                    document.writeTo(new FileOutputStream(f));
                    document.close();
                    //openScreenshot(f);

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                /*    Uri uri = Uri.fromFile(f);
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());*/
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("application/pdf");

                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+f));
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));

                    FragmentManager fragmentManager = getFragmentManager();

                    fragmentManager.beginTransaction().addToBackStack(null).commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {


                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }


}