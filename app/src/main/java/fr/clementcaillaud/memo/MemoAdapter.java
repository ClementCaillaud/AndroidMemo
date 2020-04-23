package fr.clementcaillaud.memo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.parceler.Parcels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder>
{
    private List<MemoDTO> listeMemos;
    private AppCompatActivity mainActivity;

    public MemoAdapter(List<MemoDTO> listeMemos, AppCompatActivity mainActivity)
    {
        this.listeMemos = listeMemos;
        this.mainActivity = mainActivity;
    }

    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View viewMemo = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item_liste, parent, false);
        return new MemoViewHolder(viewMemo);
    }

    @Override
    public void onBindViewHolder(MemoViewHolder holder, int position)
    {
        holder.textViewContenuMemo.setText(listeMemos.get(position).contenu);
    }

    @Override
    public int getItemCount()
    {
        return listeMemos.size();
    }

    // Appelé à chaque changement de position, pendant un déplacement.
    public boolean onItemMove(int positionDebut, int positionFin)
    {
        Collections.swap(listeMemos, positionDebut, positionFin);
        notifyItemMoved(positionDebut, positionFin);
        return true;
    }
    // Appelé une fois à la suppression.
    public void onItemDismiss(int position)
    {
        if (position > -1)
        {
            //Suppression de la BDD
            AppDatabaseHelper.getDatabase(mainActivity).memoDAO().delete(listeMemos.get(position));
            //Actualisation de la liste
            listeMemos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public class MemoViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewContenuMemo;

        public MemoViewHolder(View itemView)
        {
            super(itemView);
            textViewContenuMemo = itemView.findViewById(R.id.contenu_memo);

            //Clic sur un mémo de la liste
            textViewContenuMemo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    MemoDTO memo = listeMemos.get(getAdapterPosition());

                    //Mode paysage
                    if(mainActivity.findViewById(R.id.FragmentLayout) != null)
                    {
                        DetailFragment fragment = new DetailFragment();
                        //Passage de paramètres
                        Bundle bundle = new Bundle();
                        bundle.putString("contenu", memo.contenu);
                        fragment.setArguments(bundle);
                        //Ajout du fragment au layout de la main activity
                        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.FragmentLayout, fragment, "detail_fragment");
                        fragmentTransaction.commit();
                    }
                    //Mode portrait
                    else
                    {
                        //Lancement d'une nouvelle activité et passage de l'objet mémo
                        Intent intent = new Intent(view.getContext(), DetailActivity.class);
                        intent.putExtra("memo", Parcels.wrap(memo));
                        view.getContext().startActivity(intent);
                    }

                    //Appel webservice
                    /*AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("memo", memo.contenu);
                    client.post("http://httpbin.org/post", requestParams, new AsyncHttpResponseHandler()
                    {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response)
                        {
                            String retour = new String(response);
                            Gson gson = new Gson();
                            RetourWS retourWS = gson.fromJson(retour, RetourWS.class);
                            Toast.makeText(view.getContext(), "Retour WS : " + retourWS.form.get("memo"), Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                        {
                            Log.e("test", e.toString());
                        }
                    });

                    //Enregistrement local
                    try {
                        FileOutputStream stream = view.getContext().openFileOutput("hello_file.txt", Context.MODE_PRIVATE);
                        stream.write(Integer.toString(listeMemos.indexOf(memo)).getBytes());
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            });
        }
    }
}
