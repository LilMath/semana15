package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val uid = currentUser.uid
        database = Firebase.database.reference.child("usuarios").child(uid).child("tarefas")

        setupRecyclerView()
        listenForTasks()

        binding.btnAdd.setOnClickListener {
            val taskName = binding.etTaskName.text.toString()
            if (taskName.isNotEmpty()) {
                addTask(taskName)
            } else {
                Toast.makeText(this, "Digite o nome da tarefa", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(taskList, 
            onTaskChecked = { task, isChecked ->
                updateTaskStatus(task, isChecked)
            },
            onTaskDelete = { task ->
                deleteTask(task)
            }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = taskAdapter
    }

    private fun listenForTasks() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null) {
                        taskList.add(task)
                    }
                }
                taskAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTask(name: String) {
        val taskId = database.push().key ?: return
        val task = Task(id = taskId, nome = name, concluida = false)
        
        database.child(taskId).setValue(task).addOnSuccessListener {
            binding.etTaskName.text.clear()
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao salvar tarefa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTaskStatus(task: Task, isChecked: Boolean) {
        task.id?.let {
            database.child(it).child("concluida").setValue(isChecked)
        }
    }

    private fun deleteTask(task: Task) {
        task.id?.let {
            database.child(it).removeValue()
        }
    }
}
