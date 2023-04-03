package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diary.model.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vdurmont.emoji.EmojiParser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseFirestore firestore;

    private RecyclerView rvNotes;
    private FloatingActionButton btnAdd;

    private String emojiSmile;
    private String emojiHeart;
    private String emojiPensive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("message");

        firestore = FirebaseFirestore.getInstance();

        rvNotes = findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        btnAdd = findViewById(R.id.btn_add);

        emojiSmile = EmojiParser.parseToUnicode("\uD83D\uDE00");
        emojiHeart = EmojiParser.parseToUnicode("\u2764 \uFE0F");
        emojiPensive = EmojiParser.parseToUnicode("\uD83D\uDE14");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

    }

    public void addNote() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.add_note, null);
        mDialog.setView(mView);

        AlertDialog dialog = mDialog.create();
        dialog.setCancelable(true);

        Button save = mView.findViewById(R.id.btn_save);
        EditText edtTitle = mView.findViewById(R.id.edt_title);
        EditText edtContent = mView.findViewById(R.id.edt_content);
        TextView tvEmoji = mView.findViewById(R.id.tv_emoji);
        tvEmoji.setText(emojiSmile);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateFormat.format(calendar.getTime());

        tvEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setGravity(Gravity.TOP | Gravity.END);

                popupMenu.getMenu().add(emojiSmile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        tvEmoji.setText(emojiSmile);
                        return true;
                    }
                });
                popupMenu.getMenu().add(emojiHeart).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        tvEmoji.setText(emojiHeart);
                        return true;
                    }
                });
                popupMenu.getMenu().add(emojiPensive).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        tvEmoji.setText(emojiPensive);
                        return true;
                    }
                });

                popupMenu.show();
            }

        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = myRef.push().getKey();
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();
                String emoji = tvEmoji.getText().toString();

                myRef.child(id).setValue(new Post(id, title, content, getRandomColor(),  datetime, emoji)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Add note successful!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Add note fail!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void editNote(Post model)
    {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View mView = inflater.inflate(R.layout.add_note, null);
        mDialog.setView(mView);

        AlertDialog dialog = mDialog.create();
        dialog.setCancelable(true);

        TextView updateTitle = mView.findViewById(R.id.tv_title);
        Button save = mView.findViewById(R.id.btn_save);
        EditText edtTitle = mView.findViewById(R.id.edt_title);
        EditText edtContent = mView.findViewById(R.id.edt_content);
        TextView tvEmoji = mView.findViewById(R.id.tv_emoji);
        tvEmoji.setText(emojiSmile);

        updateTitle.setText("Edit note");
        edtTitle.setText(model.getTitle());
        edtContent.setText(model.getContent());
        dialog.show();

        tvEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "emoji", Toast.LENGTH_LONG);
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setGravity(Gravity.TOP | Gravity.END);

                popupMenu.getMenu().add(emojiSmile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        tvEmoji.setText(emojiSmile);
                        return true;
                    }
                });
                popupMenu.getMenu().add(emojiHeart).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        tvEmoji.setText(emojiHeart);
                        return true;
                    }
                });
                popupMenu.getMenu().add(emojiPensive).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        tvEmoji.setText(emojiPensive);
                        return true;
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = model.getId();
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dtedit = dateFormat.format(calendar.getTime());

                Map<String, Object> updates = new HashMap<>();
                updates.put("title", title);
                updates.put("content", content);
                updates.put("datetime", dtedit);

                myRef.child(id).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Update successful!", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Update fail!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(myRef, Post.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Post, PostHolder>(options) {
            @Override
            public PostHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_items, parent, false);

                return new PostHolder(view);
            }

            @Override
            protected void onBindViewHolder(PostHolder holder, int position, Post model)
            {
                holder.tvTitle.setText(model.getTitle());
                holder.tvContent.setText(model.getContent());
                holder.linearLayout.setBackgroundColor(Color.parseColor(model.getColor()));
                holder.tvDateTime.setText(model.getDateTime());
                holder.tvEmojil.setText((model.getEmoji()));

                ImageView ivAction = holder.itemView.findViewById(R.id.iv_action);

                ivAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);

                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                editNote(model);
                                return true;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                String id = model.getId();
                                myRef.child(id).removeValue();
                                return true;
                            }
                        });

                        popupMenu.show();
                    }
                });
            }
        };

        rvNotes.setAdapter(adapter);
        adapter.startListening();
    }


    public static class PostHolder extends RecyclerView.ViewHolder
    {
        public TextView tvTitle;
        public TextView tvContent;
        public LinearLayout linearLayout;
        public TextView tvEmojil;
        public TextView tvDateTime;


        public PostHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_title);
            tvContent = view.findViewById(R.id.tv_content);
            linearLayout = view.findViewById(R.id.layout_notes);
            tvEmojil = view.findViewById(R.id.tv_emojil);
            tvDateTime = view.findViewById(R.id.tv_datetime);
        }
    }

    private String getRandomColor()
    {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#ffc181");
        colors.add("#fff2f4");
        colors.add("#bbffcc");
        colors.add("#ffe4e1");
        colors.add("#99ffe6");
        colors.add("#bbccff");
        colors.add("#eebbff");
        colors.add("#eeffbb");
        colors.add("#f08080");
        colors.add("#40e0d0");

        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.mi_logout:
                mAuth.signOut();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}