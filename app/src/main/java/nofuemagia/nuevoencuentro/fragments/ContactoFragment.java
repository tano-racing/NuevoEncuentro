package nofuemagia.nuevoencuentro.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.share.widget.LikeView;
import com.joanzapata.iconify.widget.IconButton;

import nofuemagia.nuevoencuentro.R;

/**
 * Created by jlionti on 10/06/2016. No Fue Magia
 */
public class ContactoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacto, container, false);

        LikeView likeView = (LikeView) v.findViewById(R.id.fb_megusta_encuentro10);
        likeView.setObjectIdAndType("https://www.facebook.com/Nuevo-Encuentro-Comuna-10-177849438905900", LikeView.ObjectType.PAGE);
        likeView.setFragment(this);

        IconButton twFollow = (IconButton) v.findViewById(R.id.tw_button);
        twFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTw();
            }
        });

        IconButton mailButton = (IconButton) v.findViewById(R.id.mail_button);
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mandarMail();
            }
        });


        return v;
    }

    private void mandarMail() {
        String asunto = "";
        String cuerpo = "";

        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:?subject=" + asunto + "&body=" + cuerpo + "&to=" + "\"comuna10@encuentrocapital.com.ar");
        testIntent.setData(data);
        startActivity(testIntent);
    }

    private void openTw() {
        Intent intent = null;
        try {
            // get the Twitter app if possible
            getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=NEComuna10"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/NEComuna10"));
        }
        this.startActivity(intent);
    }
}
