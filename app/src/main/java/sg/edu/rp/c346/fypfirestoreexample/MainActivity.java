package sg.edu.rp.c346.fypfirestoreexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    public static final String TODOKEY = "ToDo";
    public static final String NAMEKEY = "Name";

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("ToDo").document("Sample");

    EditText todo, name;
    ListView lv;
    Button save, delete, update;
    ArrayList<String> listViewData;
    ArrayAdapter aa;

    @Override
    protected void onStart() {
        super.onStart();
        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    listViewData.clear();
                    String lvToDo = documentSnapshot.getString(TODOKEY);
                    listViewData.add(lvToDo);
                    aa.notifyDataSetChanged();
                }else{
                    listViewData.clear();
                    aa.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todo = findViewById(R.id.editTextToDo);
        name = findViewById(R.id.editTextName);
        save = findViewById(R.id.buttonSave);
        delete = findViewById(R.id.buttonDelete);
        update = findViewById(R.id.buttonUpdate);
        lv = findViewById(R.id.lv);

        listViewData = new ArrayList<String>();

        aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listViewData);

        lv.setAdapter(aa);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String fillToDo = documentSnapshot.getString(TODOKEY);
                                String fillName = documentSnapshot.getString(NAMEKEY);
                                todo.setText(fillToDo);
                                name.setText(fillName);
                            }
                        }
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoStr = todo.getText().toString();
                String nameStr = name.getText().toString();

                if(todoStr.isEmpty() == false || nameStr.isEmpty() == false){
                    Map<String, Object> saveData = new HashMap<String, Object>();
                    saveData.put(TODOKEY, todoStr);
                    saveData.put(NAMEKEY, nameStr);
                    mDocRef.set(saveData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            todo.setText("");
                            name.setText("");
                            Log.d("SAVE", "Document Saved");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("SAVE", "Document Not Saved", e);
                        }
                    });
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDocRef.delete();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedToDo = todo.getText().toString();;
                Map<String, Object> saveData = new HashMap<String, Object>();
                saveData.put(TODOKEY, updatedToDo);
                mDocRef.set(saveData, SetOptions.merge());
            }
        });
    }

}
