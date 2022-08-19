package tech.demoproject.todo_list.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import tech.demoproject.todo_list.R;
import tech.demoproject.todo_list.adapter.TaskAdapter;
import tech.demoproject.todo_list.database.Database;
import tech.demoproject.todo_list.object.Tasks;

public class MainActivity extends AppCompatActivity {

    Database database;
    ListView listViewTasks;
    ArrayList<Tasks> tasksArrayList;
    TaskAdapter taskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** mapping*/
        listViewTasks = findViewById(R.id.listViewTasks);
        tasksArrayList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this,R.layout.task_line,tasksArrayList);
        listViewTasks.setAdapter(taskAdapter);
        /** Create database Note*/
        database = new Database(this,"note.sqlite",null,1);
        /** create table tasks*/
        database.QueryData("CREATE TABLE IF NOT EXISTS Tasks(Id INTEGER PRIMARY KEY AUTOINCREMENT, TaskName VARCHAR(200) )");

       GetDataTask();
    }

    private  void GetDataTask(){
        /** select data*/
        Cursor dataTask = database.GetData("SELECT * FROM Tasks");
        tasksArrayList.clear();
        while (dataTask.moveToNext()){
            String taskName = dataTask.getString(1);
            int id = dataTask.getInt(0);
            tasksArrayList.add(new Tasks(id,taskName));
        }
        taskAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.add_task,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() ==  R.id.menuAddTask){
            DialogAdd();
        }
        return super.onOptionsItemSelected(item);
    }

    /** Dialog add new task*/
    private void DialogAdd(){
        Dialog dialogAdd = new Dialog(this);
        dialogAdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAdd.setContentView(R.layout.dialog_add_task);

        EditText editTextNameTask = dialogAdd.findViewById(R.id.editTextAddTask);
        Button btnAddTask = dialogAdd.findViewById(R.id.btnAddTask);
        Button btnCancelTask = dialogAdd.findViewById(R.id.btnCancelTask);

        /**
         * set onClick button add the dialog*/
        btnAddTask.setOnClickListener(view -> {
            String taskName = editTextNameTask.getText().toString();
            if(taskName.equals("")){
                Toast.makeText(MainActivity.this, "Please enter the name of task!", Toast.LENGTH_SHORT).show();
            }else {
                /** insert data*/
                database.QueryData("INSERT INTO Tasks VALUES(null, '"+taskName+"')");
                Toast.makeText(MainActivity.this, "Task "+taskName+" was added!", Toast.LENGTH_SHORT).show();
                dialogAdd.dismiss();
                GetDataTask();
            }
        });
        /**
         * set onClick button cancel the dialog*/
        btnCancelTask.setOnClickListener(view -> dialogAdd.dismiss());

        dialogAdd.show();
    }

    /** Dialog add new task*/
    public void DialogEdit(String nameTask, int id){
        Dialog dialogEdit = new Dialog(this);
        dialogEdit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEdit.setContentView(R.layout.dialog_edit_task);

        EditText editTextRenameTask = dialogEdit.findViewById(R.id.editTextRenameTask);
        Button btnConfirmEditTask = dialogEdit.findViewById(R.id.btnConfirmTask);
        Button btnCancelEditTask = dialogEdit.findViewById(R.id.btnCancelEditTask);

        editTextRenameTask.setText(nameTask);

        /** set onClick button confirm edit task*/
        btnConfirmEditTask.setOnClickListener(view -> {
            String taskRename = editTextRenameTask.getText().toString().trim();
            database.QueryData("UPDATE Tasks SET TaskName = '"+taskRename+"' WHERE Id = '"+id+"' ");
            Toast.makeText(MainActivity.this, "Task "+ taskRename +"was updated!", Toast.LENGTH_SHORT).show();
            dialogEdit.dismiss();
            GetDataTask();
        });
        /** set onClick button cancel edit task*/
        btnCancelEditTask.setOnClickListener(view -> dialogEdit.dismiss());

        dialogEdit.show();
    }
    /** Dialog remove current task*/
    public void DialogRemoveTask(String taskName, int id){
        AlertDialog.Builder dialogRemoveTask = new AlertDialog.Builder(this);
        dialogRemoveTask.setMessage("Do you want to remove "+taskName+"?");
        /**Confirm*/
        dialogRemoveTask.setNegativeButton("Yes", (dialogInterface, i) -> {
            database.QueryData("DELETE FROM Tasks WHERE Id = '"+id+"'");
            Toast.makeText(MainActivity.this, taskName+" was removed!", Toast.LENGTH_SHORT).show();
            GetDataTask();
        });
        /**Cancel*/
        dialogRemoveTask.setPositiveButton("No", (dialogInterface, i) -> {
            /** didn't do anything and close the dialog*/
        });

        dialogRemoveTask.show();
    }
}