package heshwa.nevermind_thetime;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class startup extends Fragment
{
    private int image_id;
    private String text_startup;


    public startup(int image_id,String text_startup)
    {
        this.image_id=image_id;
        this.text_startup=text_startup;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_startup, container, false);
        ImageView img=view.findViewById(R.id.image_startup);
        TextView textView=view.findViewById(R.id.text_startup);
        textView.setText(text_startup);
        img.setImageResource(image_id);


        return view;
    }

}
