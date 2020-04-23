package fr.clementcaillaud.memo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    List<MemoDTO> listeMemoDTO;
    MemoAdapter memoAdapter;
    TextInputEditText inputMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation de la BDD
        AppDatabaseHelper.getDatabase(this);

        //Init recycler
        RecyclerView recyclerView = findViewById(R.id.RecyclerMemo);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Récupération des données depuis la BDD
        listeMemoDTO = AppDatabaseHelper.getDatabase(this).memoDAO().getListeMemos();
        //Init de l'adapter
        memoAdapter = new MemoAdapter(listeMemoDTO, this);
        recyclerView.setAdapter(memoAdapter);

        //Init input nouveau mémo
        inputMemo = findViewById(R.id.InputMemo);

        //Init bouton nouveau mémo
        Button btnOk = findViewById(R.id.ButtonOK);
        btnOk.setOnClickListener(this);

        //Init interactions avec les mémos (swipe et move)
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(memoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /*
        //Récupération des données stockées en local
        // initialisation :
        FileInputStream stream = null;
        try {
            stream = openFileInput("hello_file.txt");
            StringBuilder stringBuilder = new StringBuilder();
            byte[] buffer = new byte[1024];
            // lecture :
            while (stream.read(buffer) != -1)
            {
                stringBuilder.append(new String(buffer));
            }
            stream.close();
            // affichage :
            Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onClick(View view)
    {
        //Clic sur le bouton de création d'un mémo
        if(view.getId() == R.id.ButtonOK)
        {
            //Création du mémo
            MemoDTO newMemo = new MemoDTO(inputMemo.getText().toString());
            //Ajout dans la BDD
            AppDatabaseHelper.getDatabase(view.getContext()).memoDAO().insert(newMemo);
            //Actualisation de la liste
            listeMemoDTO.add(newMemo);
            memoAdapter.notifyItemInserted(listeMemoDTO.size());
        }

    }
}
