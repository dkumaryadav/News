package com.deepakyadav.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FragmentsManager extends Fragment {

    private static final String TAG = "FragmentManager";

    TextView articleHeadLine;
    TextView articleDate;
    TextView articleAuthor;
    TextView articleText;
    ImageView articlePhoto;
    TextView articleCount;
    Article article;

    View view;

    // Define constants which we will in bundle
    public static final String ARTICLE = "ARTICLE";
    public static final String INDEX = "INDEX";
    public static final String TOTAL = "TOTAL";
    public static final String NOT_FOUND = "";
    public static final String DATE_PATTERN = "MMM dd, yyyy HH:mm";

    public static final FragmentsManager newFragment(Article article, int index, int total) {
        Log.d(TAG, "FragmentsManager: STARTED");
        FragmentsManager fragment = new FragmentsManager();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ARTICLE, article);
        bundle.putInt(INDEX, index);
        bundle.putInt(TOTAL, total);
        fragment.setArguments(bundle);
        Log.d(TAG, "FragmentsManager: COMPLETED");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: STARTED");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "onCreate: COMPLETED");
    }

    // Create the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: STARTED");

        article = (Article) getArguments().getSerializable(ARTICLE);
        view = inflater.inflate(R.layout.fragment, container, false);
        articleHeadLine = view.findViewById(R.id.articleHeadline);
        articleDate = view.findViewById(R.id.articleDate);
        articleAuthor = view.findViewById(R.id.articleAuthor);
        articleText = view.findViewById(R.id.articleText);
        articlePhoto = view.findViewById(R.id.articleImage);
        articleCount = view.findViewById(R.id.articleCount);
        articleCount.setText( (getArguments().getInt(INDEX)+1) + " of " + getArguments().getInt(TOTAL) );

        // Update article title only if it is not null and "null"
        if(article.getArticleTitle() != null && !article.getArticleTitle().trim().equals("null"))
            articleHeadLine.setText(article.getArticleTitle());
        else
            articleHeadLine.setText(NOT_FOUND);

        // Update article published date only if it is not null and "null"
        if(article.getArticlePublishDate() !=null && ! article.getArticlePublishDate().isEmpty() && !article.getArticlePublishDate().trim().equals("null")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
                articleDate.setText( sdf.format( new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse( article.getArticlePublishDate() ) ) );
            } catch (ParseException e) {

            }
        }

        // Update article author only if it is not null and "null"
        if(article.getArticleAuthor()!=null  && !article.getArticleAuthor().trim().equals("null") )
            articleAuthor.setText( article.getArticleAuthor());
        else
            articleAuthor.setText(NOT_FOUND);

        // Update article text only if it is not null and "null"
        if( article.getArticleText() != null && !article.getArticleText().trim().equals("null") )
            articleText.setText( article.getArticleText() );
        else
            articleText.setText( NOT_FOUND );

        // Update article image URL only if it is not null and "null"
        if( article.getArticleImageURL()!=null ){
            if ( !article.getArticleImageURL().trim().equals("null") ) // Image URL is present and has to be loaded
                updateImage(article.getArticleImageURL(), true);
            else // Image URL is "null" and load missing image
                updateImage( article.getArticleImageURL(), false );
        }

        // Make article head line clickable and nav to article link
        articleHeadLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData( Uri.parse( article.getArticleURL()) );
                startActivity(intent);
            }
        });

        // Make article photo clickable and nav to article link
        articlePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData( Uri.parse( article.getArticleURL()) );
                startActivity(intent);
            }
        });

        // Make article text clickable and nav to article link
        articleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData( Uri.parse( article.getArticleURL()) );
                startActivity(intent);
            }
        });

        return view;
    }

    // Update the image in the image view
    private void updateImage(final String imageURL, boolean displayImage){
        Log.d(TAG, "updateImage: STARTED");
        if( displayImage ){
            Log.d(TAG, "image URL : " + imageURL);
            Picasso picasso = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        exception.printStackTrace();
                }
            }).build();
            // Enable logging to check for errors
            picasso.setLoggingEnabled(true);
            // Load the image, if any error then broken image is loaded.
            picasso.load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(articlePhoto);
        } else{
            Log.d(TAG, "updateImage imageURL is  String(null) actual is -> : "+imageURL);
            articlePhoto.setImageResource(R.drawable.missing);
        }
        Log.d(TAG, "updateImage: COMPLETED");
    }

}
