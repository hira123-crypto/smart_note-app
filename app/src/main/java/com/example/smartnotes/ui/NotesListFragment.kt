package com.example.smartnotes.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartnotes.MainActivity
import com.example.smartnotes.R
import com.example.smartnotes.adapters.NotesAdapter
import com.example.smartnotes.databinding.FragmentNotesListBinding
import com.example.smartnotes.models.Note
import com.example.smartnotes.utils.AuthManager

class NotesListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        observeNotes()
        updateStats()
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(
            notes = emptyList(),
            onNoteClick = { note ->
                openNoteDetail(note)
            },
            onMenuClick = { note ->
                showNoteMenu(note)
            }
        )

        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeNotes() {
        viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            notesAdapter.updateNotes(notes)
            updateStats(notes)
            updateEmptyState(notes)
        }
    }

    private fun updateEmptyState(notes: List<Note>) {
        if (notes.isEmpty()) {
            binding.recyclerViewNotes.visibility = View.GONE
            // You can show empty state here if you have it
        } else {
            binding.recyclerViewNotes.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        // FAB - Add Note
        binding.fabAddNote.setOnClickListener {
            openNoteDetail(null)
        }

        // Category Filters
        binding.chipAll.setOnClickListener {
            observeNotes() // Show all notes
        }
        binding.chipWork.setOnClickListener {
            filterNotes("Work")
        }
        binding.chipPersonal.setOnClickListener {
            filterNotes("Personal")
        }
        binding.chipStudy.setOnClickListener {
            filterNotes("Study")
        }
        binding.chipIdeas.setOnClickListener {
            filterNotes("Ideas")
        }

        // Logout Button (if you added it to XML)
        // If you haven't added btnLogout to XML yet, this will cause error
        // Comment out these lines if you haven't updated the XML
        try {
            binding.btnLogout.setOnClickListener {
                showLogoutDialog()
            }
        } catch (e: Exception) {
            // Logout button not in layout yet
            android.util.Log.d("NotesListFragment", "Logout button not found in layout")
        }
    }

    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        // Clear login state
        AuthManager.logout(requireContext())

        // Show confirmation
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Navigate to Login screen and clear back stack
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finish the current activity
        requireActivity().finish()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    observeNotes()
                } else {
                    viewModel.searchNotes(query).observe(viewLifecycleOwner) { notes ->
                        notesAdapter.updateNotes(notes)
                        if (notes.isEmpty()) {
                            Toast.makeText(context, "No notes found for \"$query\"", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun filterNotes(category: String) {
        viewModel.allNotes.observe(viewLifecycleOwner) { allNotes ->
            val filtered = allNotes.filter { it.category == category }
            notesAdapter.updateNotes(filtered)
        }
    }

    private fun showNoteMenu(note: Note) {
        val popup = PopupMenu(requireContext(), binding.root)
        popup.menuInflater.inflate(R.menu.note_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    openNoteDetail(note)
                    true
                }
                R.id.action_delete -> {
                    deleteNote(note)
                    true
                }
                R.id.action_share -> {
                    Toast.makeText(context, "Share: ${note.title}", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun openNoteDetail(note: Note?) {
        val fragment = NoteDetailFragment.newInstance(note)
        (activity as? MainActivity)?.loadFragment(fragment, true)
    }

    private fun deleteNote(note: Note) {
        viewModel.deleteNote(note)
        Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
    }

    private fun updateStats(notes: List<Note> = emptyList()) {
        binding.tvTotalNotes.text = notes.size.toString()
        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        val thisWeek = notes.count { it.timestamp >= weekAgo }
        binding.tvThisWeek.text = thisWeek.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}