package heshwa.nevermind_thetime.AdapterForViews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import heshwa.nevermind_thetime.Model.Chat;
import heshwa.nevermind_thetime.Model.User;
import heshwa.nevermind_thetime.R;
import heshwa.nevermind_thetime.message;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private Context mcontext;
    private List<User> mUsers;
    private boolean ischat;
    String theLastmesaage;


    public UserAdapter(Context mcontext,List<User> mUsers,boolean ischat)
    {
        this.mUsers=mUsers;
        this.mcontext= mcontext;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view=LayoutInflater.from(mcontext).inflate(R.layout.item_list,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final User user= mUsers.get(position);
        holder.username.setText(user.getusername());
        if(user.getImageURL().equals("default"))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mcontext).load(user.getImageURL()).into(holder.profile_image);
        }
        if(ischat)
        {
            lastMessage(user.getid(),holder.last_msg);
        }
        else
        {
            holder.last_msg.setVisibility(View.GONE);
        }

        if(ischat)
        {
//            if(user.getStatus().equals("online"))
//            {
//                holder.img_on.setVisibility(View.VISIBLE);
//                holder.img_off.setVisibility(View.GONE);
//            }
//            else
//            {
//                holder.img_on.setVisibility(View.GONE);
//                holder.img_off.setVisibility(View.VISIBLE);
//            }
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        else
        {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mcontext, message.class);
                intent.putExtra("userid",user.getid() );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               // Log.i("UserAdapter :","fine upto this");

                mcontext.startActivity(intent);
                ((Activity)mcontext).finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView)
        {
            super(itemView);

            username=itemView.findViewById(R.id.Username);
            profile_image=itemView.findViewById(R.id.profile_image);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_msg);

        }

    }
    private void lastMessage(final String userid, final TextView last_msg)
    {
        theLastmesaage="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))
                    {
                      //  Log.i("UserAdapter :","value "+chat.getMessage());
                        theLastmesaage=chat.getMessage();
                    }
                }
                if(theLastmesaage.length()>20)
                {
                    theLastmesaage=theLastmesaage.substring(0,19)+".....";
                }
                switch (theLastmesaage)
                {
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastmesaage);
                        break;
                }
                theLastmesaage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
