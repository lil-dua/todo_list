package tech.demoproject.todo_list.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import tech.demoproject.todo_list.R;
import tech.demoproject.todo_list.adapter.TaskAdapter;
import tech.demoproject.todo_list.database.Database;
import tech.demoproject.todo_list.object.Tasks;

public class MainActivity extends AppCompatActivity {

    Database database;
    RecyclerView recycleViewTasks;
    ArrayList<Tasks> tasksArrayList;
    TaskAdapter taskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** mapping*/

        taskAdapter = new TaskAdapter(this,R.layout.task_line,tasksArrayList);
        recycleViewTasks = findViewById(R.id.recycleViewTasks);
        tasksArrayList = new ArrayList<>();

        /** Create database Note*/
        database = new Database(this,"note.sqlite",null,1);
        /** create table tasks*/
        database.QueryData("CREATE TABLE IF NOT EXISTS Tasks(Id INTEGER PRIMARY KEY AUTOINCREMENT, TaskName VARCHAR(200) )");
        /** insert data*/
        database.QueryData("INSERT INTO Tasks VALUES(null, 'To do job 1')");
        /** select data*/
        Cursor dataTask = database.GetData("SELECT * FROM Tasks");
        while (dataTask.moveToNext()){
            String taskName = dataTask.getString(1);
            int id = dataTask.getInt(0);
            tasksArrayList.add(new Tasks(id,taskName));

        }
        taskAdapter.notifyDataSetChanged();
    }
}