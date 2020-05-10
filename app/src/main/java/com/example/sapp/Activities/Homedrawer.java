package com.example.sapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.sapp.Fragments.HomeFragment;
import com.example.sapp.Fragments.ProfileFragment;
import com.example.sapp.Fragments.SettingsFragment;
import com.example.sapp.Models.Post;
import com.example.sapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Homedrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PRegCode=2;
    private static final int REQUESCODE=2;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;
    ImageView popupPostimage, popupUserImage, popupAddbtn;
    TextView popuptitle, popupdes;
    ProgressBar pb;
    private Uri pickedImgUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homedrawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


        iniPopup();
        setupPopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

    }

    private void setupPopupImageClick() {
        popupPostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForpermission();
            }
        });
    }

    private void checkAndRequestForpermission() {

        if (ContextCompat.checkSelfPermission(Homedrawer.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(Homedrawer.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Homedrawer.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(Homedrawer.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PRegCode);
            }
        else
        {
            openGallery();
        }

    }

    private void openGallery() {
        Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostimage.setImageURI(pickedImgUri);



        }


    }

    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity= Gravity.TOP;

        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostimage = popAddPost.findViewById(R.id.popup_image);
        popuptitle = popAddPost.findViewById(R.id.popup_title);
        popupdes = popAddPost.findViewById(R.id.popup_des);
        popupAddbtn = popAddPost.findViewById(R.id.popup_add);
        pb = popAddPost.findViewById(R.id.popup_progressBar);

        Glide.with(Homedrawer.this).load(currentUser.getPhotoUrl()).into(popupUserImage);


        popupAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddbtn.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if(!popuptitle.getText().toString().isEmpty() && !popupdes.getText().toString().isEmpty() && pickedImgUri!=null){

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                        final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageDownlaodLink = uri.toString();
                                        Post post = new Post(popuptitle.getText().toString(),
                                                popupdes.getText().toString(),
                                                imageDownlaodLink,
                                                currentUser.getUid(),
                                                currentUser.getPhotoUrl().toString());

                                        addPost(post);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage(e.getMessage());
                                        pb.setVisibility(View.INVISIBLE);
                                        popupAddbtn.setVisibility(View.VISIBLE);


                                    }
                                });
                            }
                        });
                    }
                    else{
                        showMessage("Please verify all input fields and choose image");
                        popupAddbtn.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        String key = myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added successfully");
                pb.setVisibility(View.INVISIBLE);
                popupAddbtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }
    private void showMessage(String message) {

        Toast.makeText(Homedrawer.this,message,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homedrawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();

        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

        }
        else if(id==R.id.nav_signout){
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void updateNavHeader(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);
        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);
    }
}
