package com.daniel.onlineshop.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.daniel.onlineshop.R;
import com.daniel.onlineshop.models.StoreItem;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEditItemActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_add_edit)
    Toolbar toolbar;

    @BindView(R.id.et_add_edit_name)
    EditText etName;

    @BindView(R.id.et_add_edit_description)
    EditText etDescription;

    @BindView(R.id.et_add_edit_price)
    EditText etPrice;

    @BindView(R.id.et_add_edit_quantity)
    EditText etQuantity;

    @BindView(R.id.iv_add_edit_image)
    ImageView imageView;

    @BindView(R.id.chk_add_edit_prevent)
    CheckBox chkPrevent;

    @BindView(R.id.progress_bar_add_edit)
    ProgressBar progressBar;

    @BindView(R.id.btn_add_edit_upload_image)
    Button btnUpload;

    @OnClick(R.id.btn_add_edit_choose_image)
    public void btnChooseImage() {
        imageSelection();
    }

    @OnClick(R.id.btn_add_edit_upload_image)
    public void btnUploadImage() {
        uploadImageRequest();
    }

    @OnClick(R.id.btn_add_edit_save)
    public void btnAddEditSaveItem() { addEditSaveItem(); }

    private String originalItemName = "", originalItemDescription = "", originalItemImage = "";
    private double originalItemPrice = 0;
    private int originalItemQuantity = 0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = db.collection(MyConstants.COLLECTION_ITEM_NAME);
    private StorageReference storageRef;
    private MyMethods myMethods = new MyMethods();
    private Uri imageUri;
    private String downloadUri;
    private StorageReference imageUploadRef;
    private boolean hasOldImage = false, hasUploadedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setUpView();

        imageUploadRef = FirebaseStorage.getInstance().getReference(MyConstants.STORAGE_UPLOAD_NAME);
    }

    private void setUpView() {
        Intent data = getIntent();
        if (data.hasExtra(MyConstants.EXTRA_ID)) {
            setData();
        }
    }

    private void setData() {
        StoreItem item = (StoreItem) getIntent().getSerializableExtra(MyConstants.EXTRA_STORE_ITEM_OBJECT);

        etName.setText(item.getTitle());
        etDescription.setText(item.getDescription());
        etPrice.setText(String.valueOf(item.getPrice()));
        etQuantity.setText(String.valueOf(item.getQuantity()));
        originalItemImage = item.getImageUrl();
        hasOldImage = true;
        downloadUri = originalItemImage;
        Glide.with(this).load(item.getImageUrl()).into(imageView);
    }

    private boolean isEqualUserInput(String itemName, String itemDescription, double itemPrice, int itemQuantity, String downloadUri) {
        return originalItemName.equals(itemName) && originalItemDescription.equals(itemDescription) && originalItemPrice == itemPrice && originalItemQuantity == itemQuantity && originalItemImage.equals(downloadUri);
    }

    private void  addEditSaveItem() {
        if (isAllFilled() && (hasOldImage || hasUploadedImage) && isNetworkConnected()) {
            actionSaveItem();
            if (hasUploadedImage && originalItemName.equals(etName.getText().toString()) && !originalItemName.equals("")) {
                deleteOldImage(originalItemImage);
            }
        } else {
            Toast.makeText(this, R.string.toast_error_item, Toast.LENGTH_SHORT).show();
        }
    }

    private void actionSaveItem() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        double price = Double.parseDouble(etPrice.getText().toString());
        int quantity = Integer.parseInt(etQuantity.getText().toString());

        Intent data = new Intent();
        Bundle bundle = new Bundle();
        String id = getIntent().getStringExtra(MyConstants.EXTRA_ID);
        bundle.putString(MyConstants.EXTRA_TITLE, name);
        bundle.putString(MyConstants.EXTRA_DESCRIPTION, description);
        bundle.putDouble(MyConstants.EXTRA_PRICE, price);
        bundle.putString(MyConstants.EXTRA_IMAGE_URL, downloadUri);
        bundle.putInt(MyConstants.EXTRA_QUANTITY, quantity);
        bundle.putString(MyConstants.EXTRA_ID, id);

        data.putExtras(bundle);

        createStoreItem(name, description, price, quantity, data);
    }

    private void createStoreItem(String name, String description, double price, int quantity, Intent data) {
        myMethods.showProgressDialog(getString(R.string.progress_pushing_changes), getString(R.string.progress_please_wait), this);
        itemRef.document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    myMethods.dismissProgressDialog();
                    if (chkPrevent.isChecked()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_item_already_exists), Toast.LENGTH_SHORT).show();
                    } else {
                        setResult(RESULT_OK, data);
                        finish();
                    }
                } else if (!task.getResult().exists()) {
                    if (isEqualUserInput(name, description, price, quantity, downloadUri)) {
                        setResult(RESULT_CANCELED);
                    } else {
                        myMethods.dismissProgressDialog();
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            }
        });
    }

    private void imageSelection() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, MyConstants.REQUEST_SELECT_IMAGE);
    }

    private void uploadImageRequest() {
        String name = etName.getText().toString().trim();
        if (imageUri != null && !name.equals("") && isNetworkConnected()) {
                uploadImage();
        } else {
            Toast.makeText(this, getString(R.string.toast_invalid_image_item_name), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        StorageReference fileRef = imageUploadRef.child(etName.getText().toString() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUri = uri.toString();
                                hasUploadedImage = true;
                                Toast.makeText(AddEditItemActivity.this, getString(R.string.toast_upload_complete), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                            }
                        }, 4000);
                        btnUpload.setEnabled(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                btnUpload.setEnabled(true);

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);
                btnUpload.setEnabled(false);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean isAllFilled() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();

        return !name.equals("") && !description.equals("") && !price.equals("") && !quantity.equals("") && Double.parseDouble(price) > 0.0 && Integer.parseInt(quantity) > 0;
    }

    private void deleteOldImage(String url) {
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_item_image_deleted), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddEditItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConstants.REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_edit_close) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
